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

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.npr.rad.db.DaoMaster;
import com.npr.rad.model.PlayBackEvent;
import com.npr.rad.model.ReportingData;

import static com.npr.rad.Constants.DEFAULT_BATCH_SIZE;
import static com.npr.rad.Constants.DEFAULT_EXPIRATION_TIME_INTERVAL;
import static com.npr.rad.Constants.DEFAULT_SESSION_EXPIRATION_TIME_INTERVAL;
import static com.npr.rad.Constants.DEFAULT_SUBMISSION_TIME_INTERVAL;
import static com.npr.rad.Constants.DEFAULT_USER_AGENT;

public class Rad {

    /**
     * DebugListener for receiving back Rad data, triggered events and sent requests when running debug builds
     */
    public interface DebugListener {
        void onEventTriggered(String s);

        void onRequestSent(String s);

        void onMetadataChanged(String data);
    }

    private PlaybackEventScheduler eventScheduler;
    private SubmissionScheduler submissionScheduler;
    private boolean metadataAvailable;
    private DefaultTrackSelector selector;
    private ExoPlayer player;
    private PlayerListener playerListener;
    private ReportingData reportingData;
    private static Context applicationContext;
    private static int batchSize = DEFAULT_BATCH_SIZE;
    private static long sessionExpirationTimeInterval = DEFAULT_SESSION_EXPIRATION_TIME_INTERVAL;
    private static long submissionTimeInterval = DEFAULT_SUBMISSION_TIME_INTERVAL;
    private static long expirationTimeInterval = DEFAULT_EXPIRATION_TIME_INTERVAL;
    private static String userAgent = DEFAULT_USER_AGENT;
    private static boolean debugEnabled = false;

    private DebugListener debugListener;

    private static Rad instance;

    private Rad() {
        instance = this;
    }

    /**
     * Initialize the framework with a given application context. Must be called in {Application#onCreate()}
     *
     * @param applicationContext
     */
    public static void with(Context applicationContext) {
        if (null == applicationContext) {
            throw new IllegalArgumentException("Application context must not be null");
        }
        instance = new Rad();
        Rad.applicationContext = applicationContext;
        DaoMaster.getInstance().setContext(applicationContext);
        instance.submissionScheduler = new SubmissionScheduler();
        instance.submissionScheduler.scheduleSubmission();
        instance.eventScheduler = new PlaybackEventScheduler(instance);
    }

    /**
     * Initialize the framework with a given application context and a Rad.Configuration object. Must be called in {Application#onCreate()}
     *
     * @param applicationContext
     * @param configuration
     */
    public static void with(Context applicationContext, Configuration configuration) {
        validateConfig(configuration);
        debugEnabled = configuration.debugEnabled;
        batchSize = configuration.batchSize;
        sessionExpirationTimeInterval = configuration.sessionExpirationTimeInterval;
        submissionTimeInterval = configuration.submissionTimeInterval;
        expirationTimeInterval = configuration.expirationTimeInterval;
        userAgent = configuration.userAgent;
        with(applicationContext);
    }

    private static void validateConfig(Configuration configuration) {
        if (configuration.batchSize <= 0) {
            throw new IllegalArgumentException("Batch size must be greater than 0");
        }
        if (configuration.sessionExpirationTimeInterval <= 0) {
            throw new IllegalArgumentException("Session expiration time must be equal or greater than 0");
        }
        if (configuration.submissionTimeInterval <= 0) {
            throw new IllegalArgumentException("Submission time must be equal or greater than 0");
        }
        if (configuration.expirationTimeInterval <= 0) {
            throw new IllegalArgumentException("Event expiration time must be equal or greater than 0");
        }
    }

    /**
     * Start monitoring a player instance for playback events
     *
     * @param player
     * @param selector
     */
    public static void start(ExoPlayer player, DefaultTrackSelector selector) {
        if (null == player || null == selector) {
            throw new IllegalArgumentException("Player and Track selector must not be null.");
        }
        instance.metadataAvailable = false;
        instance.player = player;
        instance.selector = selector;
        instance.playerListener = new PlayerListener(instance);
        instance.player.addListener(instance.playerListener);
    }

    /**
     * Get the currently running rad instance.
     */
    public static Rad getInstance() {
        return instance;
    }

    /**
     * Get the current application context the framework was initialized with
     */
    public Context getApplicationContext() {
        return applicationContext;
    }

    /**
     * Gets the {@link ExoPlayer} object the framework was instantiated with.
     */
    public ExoPlayer getPlayer() {
        return player;
    }

    /**
     * Gets the {@link DefaultTrackSelector} object the framework was intantiated with.
     */
    public DefaultTrackSelector getSelector() {
        return selector;
    }


    void setReportingData(ReportingData reportingData) {
        instance.eventScheduler.cancel();
        instance.submissionScheduler.scheduleSubmission();
        if (null != reportingData) {
            instance.reportingData = reportingData;
            instance.metadataAvailable = true;
            instance.addPlayBackEvent(new PlayBackEvent(PlayBackEvent.State.STARTED, 0));
            if (debugListener != null && debugEnabled) {
                debugListener.onMetadataChanged(reportingData != null ? reportingData.toString() : "");
            }
        }
    }

    ReportingData getReportingData() {
        return reportingData;
    }

    /**
     * Sets a callback for receiving the triggered events and sent requests when running debug builds
     */
    public void setDebugListener(DebugListener callback) {
        debugListener = callback;
    }

    void onEventTriggered(String s) {
        if (null != debugListener && debugEnabled) {
            debugListener.onEventTriggered(s);
        }
    }

    void onRequestSent(String s) {
        if (null != debugListener && debugEnabled) {
            debugListener.onRequestSent(s);
        }
    }

    synchronized void addPlayBackEvent(PlayBackEvent event) {
        if (event.getState() == PlayBackEvent.State.STARTED) {
            if (metadataAvailable) {
                eventScheduler.scheduleEvent(event.getMillis());
            }
        } else {
            eventScheduler.cancel();
        }
    }

    /**
     * Sets the maximum number of events to be reported in one request.
     */
    public void setBatchSize(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Batch size must be greater than 0");
        }
        batchSize = size;
    }

    /**
     * Sets the age of a session of listening for each media file.
     */
    public void setSessionExpirationTimeInterval(long millis) {
        if (millis < 0) {
            throw new IllegalArgumentException("Session expiration time must be equal or greater than 0");
        }
        sessionExpirationTimeInterval = millis;
    }

    /**
     * Sets the interval at which events are to be reported to the server.
     */
    public void setSubmissionTimeInterval(long millis) {
        if (millis < 0) {
            throw new IllegalArgumentException("Submission time must be equal or greater than 0");
        }
        submissionTimeInterval = millis;
        submissionScheduler.cancel();
        if (metadataAvailable) {
            submissionScheduler.scheduleSubmission();
        }
    }

    /**
     * Sets the time interval after which events are considered expired and will no longer reported to the server.
     */
    public void setExpirationTimeInterval(long millis) {
        if (millis < 0) {
            throw new IllegalArgumentException("Event expiration time must be equal or greater than 0");
        }
        expirationTimeInterval = millis;
    }

    /**
     * Sets the user agent header to be used on every request made by the framework
     */
    public void setUserAgent(String userAgent) {
        Rad.userAgent = userAgent;
    }

    /**
     * Returns the currently configured maximum number of events that are reported in a request.
     */
    public int getBatchSize() {
        return batchSize;
    }

    /**
     * Returns the currently configured age of a session of listening to media files.
     */
    public long getSessionExpirationTimeInterval() {
        return sessionExpirationTimeInterval;
    }

    /**
     * Returns the currently configured interval at which events are to be reported to the server.
     */
    public long getSubmissionTimeInterval() {
        return submissionTimeInterval;
    }

    /**
     * Returns the time interval after which events are considered expired and will no longer reported to the server.
     */
    public long getExpirationTimeInterval() {
        return expirationTimeInterval;
    }


    /**
     * Returns the configured user agent header set on every request made by the framework
     */
    public String getUserAgent() {
        return userAgent;
    }

    /**
     * For debug builds returns all records in the SQL database, otherwise return null;
     */
    public static String getDatabase() {
        if (debugEnabled) {
            return DaoMaster.getInstance().printData();
        }
        return null;
    }

    public static boolean isDebugEnabled() {
        return debugEnabled;
    }

    public static void setDebugEnabled(boolean debugEnabled) {
        Rad.debugEnabled = debugEnabled;
    }

    /**
     * Class containing all framework configuration that can be set when initializing the framework
     */
    public static class Configuration {
        public int batchSize;
        public long submissionTimeInterval;
        public long sessionExpirationTimeInterval;
        public long expirationTimeInterval;
        public String userAgent;
        public boolean debugEnabled;
    }
}
