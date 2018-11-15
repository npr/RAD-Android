/*
 * Copyright 2018 NPR
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.npr.rad;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;

import com.npr.rad.db.DaoMaster;
import com.npr.rad.model.ReportingData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

import static com.npr.rad.Constants.EVENTS;
import static com.npr.rad.Constants.LAST_UPDATED;
import static com.npr.rad.Constants.USER_AGENT_HEADER;

public class HTTPService extends JobIntentService {

    private static final String TAG = HTTPService.class.getSimpleName();

    public static final String TIMESTAMP = "timestamp";
    public static final String AUDIO_SESSIONS = "audioSessions";
    public static final String HTTPS = "https://";
    public static final String HTTP = "http://";

    public static final int DEFAULT_THREAD_POOL_SIZE = 16;

    final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    final SimpleDateFormat timeZoneFormat = new SimpleDateFormat("HH:mm");

    ExecutorService executorService = Executors.newFixedThreadPool(DEFAULT_THREAD_POOL_SIZE);

    private interface NetworkApi {
        @POST("/{path}")
        Call<Void> sendEvents(@Path("path") String path, @Body RequestBody requestBodybody);
    }

    @Override
    protected synchronized void onHandleWork(@NonNull Intent intent) {
        if (null == Rad.getInstance() || null == Rad.getInstance().getApplicationContext()) {
            return;
        }

        DaoMaster.getInstance().cleanUpDb(Rad.getInstance().getReportingData());
        if (!isInternetConnectionAvailable()) {
            return;
        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Rad.getInstance().getApplicationContext());

        long lastUpdated = prefs.getLong(LAST_UPDATED, 0);
        if (System.currentTimeMillis() - lastUpdated < Rad.getInstance().getSubmissionTimeInterval()) {
            return;
        }
        prefs.edit().putLong(LAST_UPDATED, System.currentTimeMillis()).apply();

        List<ReportingData> data = DaoMaster.getInstance().getReportingData();
        if (data == null || data.isEmpty()) {
            return;
        }
        sendRequests(batchRequests(createRequestObjects(data)));
    }

    /*
     * Method creates request objects from the reporting data retrieved from the local database.
     */
    private List<RequestObject> createRequestObjects(List<ReportingData> dataList) {
        List<RequestObject> enqueuedRequests = new ArrayList<>();
        RequestObject requestObject;
        for (ReportingData data : dataList) {
            boolean requestFound = false;
            for (RequestObject request : enqueuedRequests) {
                if (data.getTrackingUrls().get(0).getTrackingUrlString().equalsIgnoreCase(request.trackingUrl)) {
                    request.radObjects.add(data);
                    requestFound = true;
                    break;
                }
            }
            if (!requestFound) {
                requestObject = new RequestObject();
                requestObject.setTrackingUrl(data.getTrackingUrls().get(0).getTrackingUrlString());
                requestObject.radObjects.add(data);
                enqueuedRequests.add(requestObject);
            }
        }
        return enqueuedRequests;
    }

    /*
     * Method breaks down request objects that have more events than the configured batch size
     * and returns a list of smaller request objects that comply with the event batch size limit
     */
    private List<RequestObject> batchRequests(List<RequestObject> requestObjects) {
        List<RequestObject> results = new ArrayList<>();
        RequestObject batchedRequest = new RequestObject();
        batchedRequest.setTrackingUrl(requestObjects.get(0).trackingUrl);
        int availableRequestSize = Rad.getInstance().getBatchSize();
        for (RequestObject requestObject : requestObjects) {
            if (!results.isEmpty() && results.get(results.size() - 1) != null && results.get(results.size() - 1).trackingUrl.equalsIgnoreCase(batchedRequest.trackingUrl)) {
                batchedRequest = new RequestObject();
                batchedRequest.setTrackingUrl(requestObject.trackingUrl);
                availableRequestSize = Rad.getInstance().getBatchSize();
            }
            for (ReportingData data : requestObject.radObjects) {
                if (data.getEvents().size() <= availableRequestSize) {
                    batchedRequest.radObjects.add(data);
                    availableRequestSize -= data.getEvents().size();
                } else {
                    batchedRequest.radObjects.add(new ReportingData(data, data.getEvents().subList(0, availableRequestSize)));
                    if (batchedRequest.size() > 0 && !results.contains(batchedRequest)) {
                        results.add(batchedRequest);
                    }
                    batchedRequest = new RequestObject();
                    batchedRequest.setTrackingUrl(requestObject.trackingUrl);
                    data = new ReportingData(data, data.getEvents().subList(availableRequestSize, data.getEvents().size()));
                    availableRequestSize = Rad.getInstance().getBatchSize();
                    List<ReportingData> batches = data.split(Rad.getInstance().getBatchSize());
                    for (ReportingData batch : batches) {
                        batchedRequest.radObjects.add(batch);
                        if (batch.getEvents().size() == Rad.getInstance().getBatchSize()) {
                            results.add(batchedRequest);
                            batchedRequest = new RequestObject();
                            batchedRequest.setTrackingUrl(requestObject.trackingUrl);
                            availableRequestSize = Rad.getInstance().getBatchSize();
                        } else {
                            availableRequestSize -= batch.getEvents().size();
                        }
                    }
                }
                if (batchedRequest.size() > 0 && !results.contains(batchedRequest)) {
                    results.add(batchedRequest);
                }
            }
        }
        return results;
    }


    /*
     *  Method sends out requests and handles response codes
     */
    private void sendRequests(List<RequestObject> enqueuedRequests) {
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request request = chain.request().newBuilder().addHeader(USER_AGENT_HEADER, Rad.getInstance().getUserAgent()).build();
                        return chain.proceed(request);
                    }
                })
                .build();
        for (final RequestObject requestObject : enqueuedRequests) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    JSONObject requestBodyJson = new JSONObject();
                    JSONObject[] audioSessions = new JSONObject[requestObject.radObjects.size()];
                    for (Iterator<ReportingData> iterator = requestObject.radObjects.iterator(); iterator.hasNext(); ) {
                        ReportingData rad = iterator.next();
                        if (rad.getEvents().isEmpty()) {
                            iterator.remove();
                        }
                    }
                    if (requestObject.radObjects.isEmpty()) {
                        return;
                    }
                    for (int i = 0; i < requestObject.radObjects.size(); i++) {
                        ReportingData rad = requestObject.radObjects.get(i);
                        if (rad == null) {
                            continue;
                        }
                        JSONObject radJson = null;
                        try {
                            radJson = new JSONObject(rad.getMetadata().getFields());
                            radJson.accumulate("sessionId", rad.getSession().getSessionUuid());
                        } catch (JSONException | NullPointerException e) {
                            Log.e(TAG, "Error creating request json body", e);
                        }
                        JSONObject[] eventsJson = new JSONObject[rad.getEvents().size()];
                        for (int j = 0; j < rad.getEvents().size(); j++) {
                            try {
                                eventsJson[j] = new JSONObject(rad.getEvents().get(j).getFields());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                Date d = new Date(rad.getEvents().get(j).getTimestamp());
                                Date tz = new Date(rad.getEvents().get(j).getTimezoneOffset());
                                StringBuilder date = new StringBuilder();
                                date.append(dateFormat.format(d));
                                String timeZone = timeZoneFormat.format(tz);
                                if (tz.getTime() >= 0) {
                                    date.append("+");
                                } else {
                                    date.append("-");
                                }
                                date.append(timeZone);
                                eventsJson[j].accumulate(TIMESTAMP, date.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        try {
                            radJson.put(EVENTS, new JSONArray(eventsJson));
                            audioSessions[i] = radJson;
                        } catch (JSONException | NullPointerException e) {
                            Log.e(TAG, "Error creating request json", e);
                        }
                    }
                    try {
                        requestBodyJson.accumulate(AUDIO_SESSIONS, new JSONArray(audioSessions));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Retrofit retrofit;
                    try {
                        retrofit = new Retrofit.Builder()
                                .baseUrl(requestObject.radObjects.get(0).getTrackingUrls().get(0).getTrackingUrlString())
                                .client(okHttpClient)
                                .build();
                    } catch (IllegalArgumentException e) {
                        Log.w(TAG, "sendRequests: " + requestBodyJson.toString(), e);
                        return;
                    }
                    NetworkApi api = retrofit.create(NetworkApi.class);
                    RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), requestBodyJson.toString());
                    String path = getPath(requestObject.radObjects.get(0).getTrackingUrls().get(0).getTrackingUrlString());
                    Request request = api.sendEvents(path, requestBody).request();
                    StringBuilder debugStringBuilder = new StringBuilder("\n\n\nREQUEST: ").append(request.toString());
                    debugStringBuilder.append("\nHEADERS: ").append(request.headers().toString());
                    debugStringBuilder.append("BODY: ").append(requestBodyJson.toString());
                    Response response;
                    try {
                        response = api.sendEvents(path, requestBody).execute();
                    } catch (IOException e) {
                        Log.e(TAG, "Error sending request", e);
                        return;
                    }
                    debugStringBuilder.append("\nRESPONSE: ").append(response.toString());
                    Rad.getInstance().onRequestSent(debugStringBuilder.toString());

                    // 3xx Redirect - follow new URL
                    while (response.code() >= 300 && response.code() < 400) {
                        try {
                            String newUrl = response.raw().header("location");
                            path = getPath(newUrl);
                            if (TextUtils.isEmpty(newUrl)) {
                                Log.e(TAG, "Redirect url is empty!");
                                break;
                            }
                            newUrl = buildRedirectUrl(newUrl, requestObject.radObjects.get(0).getTrackingUrls().get(0).getTrackingUrlString());
                            if (TextUtils.isEmpty(newUrl) || !Patterns.WEB_URL.matcher(newUrl).matches()) {
                                for (ReportingData rad : requestObject.radObjects) {
                                    DaoMaster.getInstance().deleteReportingData(rad);
                                }
                            } else {
                                retrofit = new Retrofit.Builder()
                                        .baseUrl(newUrl)
                                        .client(okHttpClient)
                                        .build();
                                api = retrofit.create(NetworkApi.class);
                                requestBody = RequestBody.create(MediaType.parse("application/json"), requestBodyJson.toString());
                                request = api.sendEvents(path, requestBody).request();
                                debugStringBuilder.append("\nREDIRECTED: ").append(request.toString());
                                response = api.sendEvents(path, requestBody).execute();
                                debugStringBuilder.append("\nRESPONSE: ").append(response.toString());
                            }
                        } catch (IOException | IllegalArgumentException e) {
                            Log.e(TAG, "sendRequests: ", e);
                            break;
                        }

                    }

                    //* 2xx Success - delete events*//*
                    if (response.code() >= 200 && response.code() < 300) {
                        for (ReportingData rad : requestObject.radObjects) {
                            DaoMaster.getInstance().deleteReportingData(rad);
                        }
                    }

                    //* 4xx Request Error - delete events*//*
                    if (response.code() >= 400 && response.code() < 500) {
                        for (ReportingData rad : requestObject.radObjects) {
                            DaoMaster.getInstance().deleteReportingData(rad);
                        }
                    }
                    //* 5xx Server error - retry later, keep events*//*
                }
            });
        }
    }

    private String getPath(String trackingUrl) {
        try {
            return new URL(trackingUrl).getPath().replaceFirst("/", "");
        } catch (MalformedURLException e) {
            Log.e(TAG, "getPath: ", e);
        }
        return null;
    }

    private String buildRedirectUrl(String newUrl, String trackingUrl) {
        if (!(newUrl.startsWith(HTTPS) || newUrl.startsWith(HTTP))) {
            return trackingUrl + "/" + newUrl;
        } else {
            if (newUrl.startsWith(HTTPS)) {
                return newUrl;
            }
        }
        return "";
    }

    public boolean isInternetConnectionAvailable() {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) Rad.getInstance().getApplicationContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (null != connectivityManager) {
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                return networkInfo != null && networkInfo.isAvailable() &&
                        networkInfo.isConnected();
            }
        } catch (Exception e) {
            Log.w(TAG, e.toString());
        }
        return false;
    }

    class RequestObject {
        String trackingUrl;
        ArrayList<ReportingData> radObjects;

        RequestObject() {
            radObjects = new ArrayList<>();
        }

        void setTrackingUrl(String trackingUrl) {
            this.trackingUrl = trackingUrl;
        }

        int size() {
            int size = 0;
            for (ReportingData rad : radObjects) {
                size += rad.getEvents().size();
            }
            return size;
        }
    }
}
