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

public class Constants {

    private Constants() {
    }

    static final String REMOTE_AUDIO_DATA = "remoteAudioData";
    static final String TRACKING_URLS = "trackingUrls";
    static final String EVENTS = "events";
    static final String METADATA = "metadata";
    static final String SESSION = "session";
    static final String EVENT_TIME = "eventTime";
    static final String LAST_UPDATED = "LAST_UPDATED";

    static final String USER_AGENT_HEADER = "User-Agent";

    public static final int DEFAULT_BATCH_SIZE = 50;
    public static final long DEFAULT_SESSION_EXPIRATION_TIME_INTERVAL = 24 * 60 * 60 * 1000L; /* 24 hours */
    public static final long DEFAULT_EXPIRATION_TIME_INTERVAL = 24 * 60 * 60 * 1000L; /* 24 hours */
    public static final long DEFAULT_SUBMISSION_TIME_INTERVAL = 20 * 60 * 1000L; /* 20 minutes */
    public static final String DEFAULT_USER_AGENT = "RAD/Android";
}
