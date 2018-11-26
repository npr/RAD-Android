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

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.URLUtil;

import com.npr.rad.db.DaoMaster;
import com.npr.rad.model.Event;
import com.npr.rad.model.ReportingData;
import com.npr.rad.model.Session;
import com.npr.rad.model.TrackingUrl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.core.app.JobIntentService;

import static com.npr.rad.Constants.EVENTS;
import static com.npr.rad.Constants.EVENT_TIME;
import static com.npr.rad.Constants.METADATA;
import static com.npr.rad.Constants.REMOTE_AUDIO_DATA;
import static com.npr.rad.Constants.SESSION;
import static com.npr.rad.Constants.TRACKING_URLS;


public class PersistenceService extends JobIntentService {

    public static final String TAG = PersistenceService.class.getSimpleName();

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        if (null == intent.getAction()) {
            return;
        }
        switch (intent.getAction()) {
            case EVENTS:
                if (null == intent.getExtras()) {
                    return;
                }
                long sessionId = intent.getExtras().getLong(SESSION);
                List<TrackingUrl> trackingUrls = intent.getParcelableArrayListExtra(TRACKING_URLS);
                List<Event> events = intent.getParcelableArrayListExtra(EVENTS);
                storeEvents(sessionId, trackingUrls, events);
                break;

            case METADATA:
                storeMetadata(intent.getStringExtra(METADATA));

        }
    }

    private synchronized void storeEvents(long sessionId, List<TrackingUrl> trackingUrls, List<Event> events) {
        DaoMaster.getInstance().storeEvents(sessionId, trackingUrls, events);
    }

    private synchronized void storeMetadata(String s) {
        ReportingData radData = parseJson(s);
        if (null != radData) {
            radData = DaoMaster.getInstance().storeReportingData(radData);
            Rad.getInstance().setReportingData(radData);
        }
    }

    /*
     * Method validates and creates a reporting data model object
     * from a rad json extracted from a media file.
     */
    @WorkerThread
    private ReportingData parseJson(String json) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        ReportingData radData = new ReportingData();
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(json);
        } catch (JSONException e) {
            Log.e(TAG, "parseJson: ", e);
            return null;
        }
        String hash = com.npr.rad.model.Metadata.hashMetadata(jsonObject.toString());
        JSONObject radJson = null;
        try {
            radJson = jsonObject.getJSONObject(REMOTE_AUDIO_DATA);
        } catch (JSONException e) {
            Log.e(TAG, "parseJson: ", e);
            return null;
        }

        JSONArray trackingUrlArray = null;
        try {
            trackingUrlArray = radJson.getJSONArray(TRACKING_URLS);
        } catch (JSONException e) {
            Log.e(TAG, "parseJson: ", e);
            return null;
        }
        if (null == trackingUrlArray || trackingUrlArray.length() <= 0) {
            Log.w(TAG, "parse RAD data: No tracking URLs!");
            return null;
        }
        JSONObject element;
        List<TrackingUrl> trackingUrls = new ArrayList<>();
        for (int i = 0; i < trackingUrlArray.length(); i++) {
            String trackingUrl = null;
            try {
                trackingUrl = trackingUrlArray.getString(i);
            } catch (JSONException e) {
                Log.e(TAG, "parseJson: ", e);
                continue;
            }
            if (!trackingUrl.endsWith("/")) {
                trackingUrl += "/";
            }
            if (URLUtil.isValidUrl(trackingUrl)) {
                trackingUrls.add(new TrackingUrl(trackingUrl));
            }
        }
        if (trackingUrls.isEmpty()) {
            Log.e(TAG, "parseJson: No valid tracking url!");
            return null;
        }
        radData.setTrackingUrls(trackingUrls);
        radJson.remove(TRACKING_URLS);
        JSONArray eventsJsonArray = null;
        try {
            eventsJsonArray = radJson.getJSONArray(EVENTS);
        } catch (JSONException e) {
            Log.e(TAG, "parseJson: ", e);
            return null;
        }

        if (null == eventsJsonArray || eventsJsonArray.length() <= 0) {
            Log.w(TAG, "parse RAD data: No events!");
            return null;
        }
        radJson.remove(EVENTS);

        Map<String, Object> fields = null;
        try {
            fields = JsonUtils.jsonToMap(radJson);
        } catch (JSONException e) {
            Log.e(TAG, "parseJson: ", e);
            return null;
        }
        fields.remove(TRACKING_URLS);
        radData.setMetadata(new com.npr.rad.model.Metadata(radJson.toString()));
        radData.getMetadata().setHash(hash);
        radData.setSession(new Session());
        Map<String, Object> eventFields;
        Event event;

        for (int i = 0; i < eventsJsonArray.length(); i++) {
            try {
                element = eventsJsonArray.getJSONObject(i);
            } catch (JSONException e) {
                Log.e(TAG, "parseJson: ", e);
                continue;
            }
            event = new Event();
            try {
                eventFields = JsonUtils.jsonToMap(element);
            } catch (JSONException e) {
                Log.e(TAG, "parseJson: ", e);
                continue;
            }

            if (null == eventFields.get(EVENT_TIME) || TextUtils.isEmpty(eventFields.get(EVENT_TIME).toString()) || 0 > Utils.getTime(eventFields.get(EVENT_TIME).toString())) {
                Log.w(TAG, "parse RAD data: Missing or corrupt eventTime for event: " + element);
                continue;
            }

            event.setEventTime(Utils.getTime(eventFields.get(EVENT_TIME).toString()));
            event.setFields(element.toString());
            radData.addEvent(event);
        }
        if (radData.getEvents().isEmpty()) {
            Log.w(TAG, "parse RAD data: No valid events!");
            return null;
        }
        radData.sortEvents();

        return radData;
    }
}
