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
import android.net.Uri;
import android.os.Parcelable;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.npr.rad.db.DaoMaster;
import com.npr.rad.model.ReportingData;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.test.core.app.ApplicationProvider;

import static com.npr.rad.Constants.DEFAULT_BATCH_SIZE;
import static com.npr.rad.Constants.DEFAULT_EXPIRATION_TIME_INTERVAL;
import static com.npr.rad.Constants.DEFAULT_SESSION_EXPIRATION_TIME_INTERVAL;
import static com.npr.rad.Constants.DEFAULT_SUBMISSION_TIME_INTERVAL;
import static com.npr.rad.Constants.DEFAULT_USER_AGENT;
import static com.npr.rad.Constants.EVENTS;
import static com.npr.rad.Constants.METADATA;
import static com.npr.rad.Constants.SESSION;
import static com.npr.rad.Constants.TRACKING_URLS;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(RobolectricGradleTestRunner.class)
public class ExampleUnitTest {

    private String userAgent = UUID.randomUUID().toString();
    private long expirationTimeInterval = ThreadLocalRandom.current().nextLong(0, Long.MAX_VALUE);
    private long sessionExpirationTimeInterval = ThreadLocalRandom.current().nextLong(0, Long.MAX_VALUE);
    private long submissionTimeInterval = ThreadLocalRandom.current().nextLong(0, Long.MAX_VALUE);
    private int batchSize = ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);

    String metadata = "{\"remoteAudioData\":{\"trackingUrls\":[\"Https://remoteaudio.npr.org\",\"Https://tracking.npr.org\"],\"events\":[{\"load_test\":\"Zapuc Ciprian\",\"count\":1,\"eventTime\":\"00:00:00.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":2,\"eventTime\":\"00:00:01.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":3,\"eventTime\":\"00:00:02.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":4,\"eventTime\":\"00:00:03.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":5,\"eventTime\":\"00:00:04.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":6,\"eventTime\":\"00:00:05.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":7,\"eventTime\":\"00:00:06.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":8,\"eventTime\":\"00:00:07.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":9,\"eventTime\":\"00:00:08.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":10,\"eventTime\":\"00:00:09.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":11,\"eventTime\":\"00:00:10.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":12,\"eventTime\":\"00:00:11.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":13,\"eventTime\":\"00:00:12.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":14,\"eventTime\":\"00:00:13.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":15,\"eventTime\":\"00:00:14.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":16,\"eventTime\":\"00:00:15.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":17,\"eventTime\":\"00:00:16.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":18,\"eventTime\":\"00:00:17.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":19,\"eventTime\":\"00:00:18.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":20,\"eventTime\":\"00:00:19.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":21,\"eventTime\":\"00:00:20.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":22,\"eventTime\":\"00:00:21.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":23,\"eventTime\":\"00:00:22.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":24,\"eventTime\":\"00:00:23.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":25,\"eventTime\":\"00:00:24.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":26,\"eventTime\":\"00:00:25.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":27,\"eventTime\":\"00:00:26.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":28,\"eventTime\":\"00:00:27.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":29,\"eventTime\":\"00:00:28.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":30,\"eventTime\":\"00:00:29.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":31,\"eventTime\":\"00:00:30.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":32,\"eventTime\":\"00:00:31.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":33,\"eventTime\":\"00:00:32.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":34,\"eventTime\":\"00:00:33.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":35,\"eventTime\":\"00:00:34.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":36,\"eventTime\":\"00:00:35.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":37,\"eventTime\":\"00:00:36.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":38,\"eventTime\":\"00:00:37.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":39,\"eventTime\":\"00:00:38.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":40,\"eventTime\":\"00:00:39.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":41,\"eventTime\":\"00:00:40.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":42,\"eventTime\":\"00:00:41.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":43,\"eventTime\":\"00:00:42.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":44,\"eventTime\":\"00:00:43.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":45,\"eventTime\":\"00:00:44.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":46,\"eventTime\":\"00:00:45.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":47,\"eventTime\":\"00:00:46.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":48,\"eventTime\":\"00:00:47.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":49,\"eventTime\":\"00:00:48.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":50,\"eventTime\":\"00:00:49.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":51,\"eventTime\":\"00:01:00.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":52,\"eventTime\":\"00:01:01.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":53,\"eventTime\":\"00:01:02.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":54,\"eventTime\":\"00:01:03.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":55,\"eventTime\":\"00:01:04.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":56,\"eventTime\":\"00:01:05.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":57,\"eventTime\":\"00:01:06.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":58,\"eventTime\":\"00:01:07.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":59,\"eventTime\":\"00:01:08.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":60,\"eventTime\":\"00:01:09.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":61,\"eventTime\":\"00:01:10.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":62,\"eventTime\":\"00:01:11.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":63,\"eventTime\":\"00:01:12.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":64,\"eventTime\":\"00:01:13.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":65,\"eventTime\":\"00:01:14.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":66,\"eventTime\":\"00:01:15.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":67,\"eventTime\":\"00:01:16.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":68,\"eventTime\":\"00:01:17.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":69,\"eventTime\":\"00:01:18.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":70,\"eventTime\":\"00:01:19.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":71,\"eventTime\":\"00:01:20.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":72,\"eventTime\":\"00:01:21.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":73,\"eventTime\":\"00:01:22.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":74,\"eventTime\":\"00:01:23.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":75,\"eventTime\":\"00:01:24.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":76,\"eventTime\":\"00:01:25.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":77,\"eventTime\":\"00:01:26.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":78,\"eventTime\":\"00:01:27.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":79,\"eventTime\":\"00:01:28.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":80,\"eventTime\":\"00:01:29.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":81,\"eventTime\":\"00:01:30.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":82,\"eventTime\":\"00:01:31.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":83,\"eventTime\":\"00:01:32.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":84,\"eventTime\":\"00:01:33.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":85,\"eventTime\":\"00:01:34.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":86,\"eventTime\":\"00:01:35.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":87,\"eventTime\":\"00:01:36.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":88,\"eventTime\":\"00:01:37.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":89,\"eventTime\":\"00:01:38.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":90,\"eventTime\":\"00:01:39.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":91,\"eventTime\":\"00:01:40.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":92,\"eventTime\":\"00:01:41.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":93,\"eventTime\":\"00:01:42.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":94,\"eventTime\":\"00:01:43.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":95,\"eventTime\":\"00:01:44.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":96,\"eventTime\":\"00:01:45.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":97,\"eventTime\":\"00:01:42.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":98,\"eventTime\":\"00:01:42.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":99,\"eventTime\":\"00:01:42.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":100,\"eventTime\":\"00:01:42.000\"}]}}";
    String invalidJson = "{\"remoteAudioData\":{\"trackingUrls\":[\"Https://remoteaudio.npr.org\",\"Https://tracking.npr.org\"],\"events\":[{\"load_test\":\"Zapuc Ciprian\",\"count\":1,\"eventTime\":\"00:00:00.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":2,\"eventTime\":\"00:00:01.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":3,\"eventTime\":\"00:00:02.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":4,\"eventTime\":\"00:00:03.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":5,\"eventTime\":\"00:00:04.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":6,\"eventTime\":\"00:00:05.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":7,\"eventTime\":\"00:00:06.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":8,\"eventTime\":\"00:00:07.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":9,\"eventTime\":\"00:00:08.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":10,\"eventTime\":\"00:00:09.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":11,\"eventTime\":\"00:00:10.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":12,\"eventTime\":\"00:00:11.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":13,\"eventTime\":\"00:00:12.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":14,\"eventTime\":\"00:00:13.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":15,\"eventTime\":\"00:00:14.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":16,\"eventTime\":\"00:00:15.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":17,\"eventTime\":\"00:00:16.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":18,\"eventTime\":\"00:00:17.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":19,\"eventTime\":\"00:00:18.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":20,\"eventTime\":\"00:00:19.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":21,\"eventTime\":\"00:00:20.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":22,\"eventTime\":\"00:00:21.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":23,\"eventTime\":\"00:00:22.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":24,\"eventTime\":\"00:00:23.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":25,\"eventTime\":\"00:00:24.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":26,\"eventTime\":\"00:00:25.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":27,\"eventTime\":\"00:00:26.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":28,\"eventTime\":\"00:00:27.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":29,\"eventTime\":\"00:00:28.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":30,\"eventTime\":\"00:00:29.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":31,\"eventTime\":\"00:00:30.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":32,\"eventTime\":\"00:00:31.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":33,\"eventTime\":\"00:00:32.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":34,\"eventTime\":\"00:00:33.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":35,\"eventTime\":\"00:00:34.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":36,\"eventTime\":\"00:00:35.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":37,\"eventTime\":\"00:00:36.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":38,\"eventTime\":\"00:00:37.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":39,\"eventTime\":\"00:00:38.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":40,\"eventTime\":\"00:00:39.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":41,\"eventTime\":\"00:00:40.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":42,\"eventTime\":\"00:00:41.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":43,\"eventTime\":\"00:00:42.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":44,\"eventTime\":\"00:00:43.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":45,\"eventTime\":\"00:00:44.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":46,\"eventTime\":\"00:00:45.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":47,\"eventTime\":\"00:00:46.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":48,\"eventTime\":\"00:00:47.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":49,\"eventTime\":\"00:00:48.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":50,\"eventTime\":\"00:00:49.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":51,\"eventTime\":\"00:01:00.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":52,\"eventTime\":\"00:01:01.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":53,\"eventTime\":\"00:01:02.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":54,\"eventTime\":\"00:01:03.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":55,\"eventTime\":\"00:01:04.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":56,\"eventTime\":\"00:01:05.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":57,\"eventTime\":\"00:01:06.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":58,\"eventTime\":\"00:01:07.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":59,\"eventTime\":\"00:01:08.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":60,\"eventTime\":\"00:01:09.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":61,\"eventTime\":\"00:01:10.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":62,\"eventTime\":\"00:01:11.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":63,\"eventTime\":\"00:01:12.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":64,\"eventTime\":\"00:01:13.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":65,\"eventTime\":\"00:01:14.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":66,\"eventTime\":\"00:01:15.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":67,\"eventTime\":\"00:01:16.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":68,\"eventTime\":\"00:01:17.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":69,\"eventTime\":\"00:01:18.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":70,\"eventTime\":\"00:01:19.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":71,\"eventTime\":\"00:01:20.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":72,\"eventTime\":\"00:01:21.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":73,\"eventTime\":\"00:01:22.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":74,\"eventTime\":\"00:01:23.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":75,\"eventTime\":\"00:01:24.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":76,\"eventTime\":\"00:01:25.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":77,\"eventTime\":\"00:01:26.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":78,\"eventTime\":\"00:01:27.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":79,\"eventTime\":\"00:01:28.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":80,\"eventTime\":\"00:01:29.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":81,\"eventTime\":\"00:01:30.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":82,\"eventTime\":\"00:01:31.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":83,\"eventTime\":\"00:01:32.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":84,\"eventTime\":\"00:01:33.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":85,\"eventTime\":\"00:01:34.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":86,\"eventTime\":\"00:01:35.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":87,\"eventTime\":\"00:01:36.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":88,\"eventTime\":\"00:01:37.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":89,\"eventTime\":\"00:01:38.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":90,\"eventTime\":\"00:01:39.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":91,\"eventTime\":\"00:01:40.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":92,\"eventTime\":\"00:01:41.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":93,\"eventTime\":\"00:01:42.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":94,\"eventTime\":\"00:01:43.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":95,\"eventTime\":\"00:01:44.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":96,\"eventTime\":\"00:01:45.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":97,\"eventTime\":\"00:01:42.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":98,\"eventTime\":\"00:01:42.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":99,\"eventTime\":\"00:01:42.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":100,\"eventTime\":\"00:01:42.000\"}]}";
    String emptyMetadata = "";
    String metadataWithMissingTrackingUrls = "{\"remoteAudioData\":{\"events\":[{\"load_test\":\"Zapuc Ciprian\",\"count\":1,\"eventTime\":\"00:00:00.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":2,\"eventTime\":\"00:00:01.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":3,\"eventTime\":\"00:00:02.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":4,\"eventTime\":\"00:00:03.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":5,\"eventTime\":\"00:00:04.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":6,\"eventTime\":\"00:00:05.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":7,\"eventTime\":\"00:00:06.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":8,\"eventTime\":\"00:00:07.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":9,\"eventTime\":\"00:00:08.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":10,\"eventTime\":\"00:00:09.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":11,\"eventTime\":\"00:00:10.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":12,\"eventTime\":\"00:00:11.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":13,\"eventTime\":\"00:00:12.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":14,\"eventTime\":\"00:00:13.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":15,\"eventTime\":\"00:00:14.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":16,\"eventTime\":\"00:00:15.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":17,\"eventTime\":\"00:00:16.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":18,\"eventTime\":\"00:00:17.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":19,\"eventTime\":\"00:00:18.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":20,\"eventTime\":\"00:00:19.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":21,\"eventTime\":\"00:00:20.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":22,\"eventTime\":\"00:00:21.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":23,\"eventTime\":\"00:00:22.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":24,\"eventTime\":\"00:00:23.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":25,\"eventTime\":\"00:00:24.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":26,\"eventTime\":\"00:00:25.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":27,\"eventTime\":\"00:00:26.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":28,\"eventTime\":\"00:00:27.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":29,\"eventTime\":\"00:00:28.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":30,\"eventTime\":\"00:00:29.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":31,\"eventTime\":\"00:00:30.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":32,\"eventTime\":\"00:00:31.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":33,\"eventTime\":\"00:00:32.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":34,\"eventTime\":\"00:00:33.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":35,\"eventTime\":\"00:00:34.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":36,\"eventTime\":\"00:00:35.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":37,\"eventTime\":\"00:00:36.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":38,\"eventTime\":\"00:00:37.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":39,\"eventTime\":\"00:00:38.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":40,\"eventTime\":\"00:00:39.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":41,\"eventTime\":\"00:00:40.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":42,\"eventTime\":\"00:00:41.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":43,\"eventTime\":\"00:00:42.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":44,\"eventTime\":\"00:00:43.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":45,\"eventTime\":\"00:00:44.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":46,\"eventTime\":\"00:00:45.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":47,\"eventTime\":\"00:00:46.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":48,\"eventTime\":\"00:00:47.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":49,\"eventTime\":\"00:00:48.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":50,\"eventTime\":\"00:00:49.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":51,\"eventTime\":\"00:01:00.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":52,\"eventTime\":\"00:01:01.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":53,\"eventTime\":\"00:01:02.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":54,\"eventTime\":\"00:01:03.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":55,\"eventTime\":\"00:01:04.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":56,\"eventTime\":\"00:01:05.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":57,\"eventTime\":\"00:01:06.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":58,\"eventTime\":\"00:01:07.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":59,\"eventTime\":\"00:01:08.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":60,\"eventTime\":\"00:01:09.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":61,\"eventTime\":\"00:01:10.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":62,\"eventTime\":\"00:01:11.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":63,\"eventTime\":\"00:01:12.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":64,\"eventTime\":\"00:01:13.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":65,\"eventTime\":\"00:01:14.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":66,\"eventTime\":\"00:01:15.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":67,\"eventTime\":\"00:01:16.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":68,\"eventTime\":\"00:01:17.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":69,\"eventTime\":\"00:01:18.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":70,\"eventTime\":\"00:01:19.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":71,\"eventTime\":\"00:01:20.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":72,\"eventTime\":\"00:01:21.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":73,\"eventTime\":\"00:01:22.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":74,\"eventTime\":\"00:01:23.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":75,\"eventTime\":\"00:01:24.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":76,\"eventTime\":\"00:01:25.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":77,\"eventTime\":\"00:01:26.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":78,\"eventTime\":\"00:01:27.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":79,\"eventTime\":\"00:01:28.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":80,\"eventTime\":\"00:01:29.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":81,\"eventTime\":\"00:01:30.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":82,\"eventTime\":\"00:01:31.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":83,\"eventTime\":\"00:01:32.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":84,\"eventTime\":\"00:01:33.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":85,\"eventTime\":\"00:01:34.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":86,\"eventTime\":\"00:01:35.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":87,\"eventTime\":\"00:01:36.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":88,\"eventTime\":\"00:01:37.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":89,\"eventTime\":\"00:01:38.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":90,\"eventTime\":\"00:01:39.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":91,\"eventTime\":\"00:01:40.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":92,\"eventTime\":\"00:01:41.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":93,\"eventTime\":\"00:01:42.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":94,\"eventTime\":\"00:01:43.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":95,\"eventTime\":\"00:01:44.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":96,\"eventTime\":\"00:01:45.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":97,\"eventTime\":\"00:01:42.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":98,\"eventTime\":\"00:01:42.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":99,\"eventTime\":\"00:01:42.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":100,\"eventTime\":\"00:01:42.000\"}]}}";
    String metadataWithEmptyTrackingUrls = "{\"remoteAudioData\":{\"trackingUrls\":[],\"events\":[{\"load_test\":\"Zapuc Ciprian\",\"count\":1,\"eventTime\":\"00:00:00.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":2,\"eventTime\":\"00:00:01.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":3,\"eventTime\":\"00:00:02.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":4,\"eventTime\":\"00:00:03.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":5,\"eventTime\":\"00:00:04.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":6,\"eventTime\":\"00:00:05.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":7,\"eventTime\":\"00:00:06.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":8,\"eventTime\":\"00:00:07.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":9,\"eventTime\":\"00:00:08.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":10,\"eventTime\":\"00:00:09.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":11,\"eventTime\":\"00:00:10.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":12,\"eventTime\":\"00:00:11.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":13,\"eventTime\":\"00:00:12.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":14,\"eventTime\":\"00:00:13.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":15,\"eventTime\":\"00:00:14.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":16,\"eventTime\":\"00:00:15.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":17,\"eventTime\":\"00:00:16.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":18,\"eventTime\":\"00:00:17.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":19,\"eventTime\":\"00:00:18.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":20,\"eventTime\":\"00:00:19.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":21,\"eventTime\":\"00:00:20.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":22,\"eventTime\":\"00:00:21.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":23,\"eventTime\":\"00:00:22.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":24,\"eventTime\":\"00:00:23.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":25,\"eventTime\":\"00:00:24.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":26,\"eventTime\":\"00:00:25.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":27,\"eventTime\":\"00:00:26.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":28,\"eventTime\":\"00:00:27.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":29,\"eventTime\":\"00:00:28.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":30,\"eventTime\":\"00:00:29.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":31,\"eventTime\":\"00:00:30.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":32,\"eventTime\":\"00:00:31.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":33,\"eventTime\":\"00:00:32.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":34,\"eventTime\":\"00:00:33.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":35,\"eventTime\":\"00:00:34.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":36,\"eventTime\":\"00:00:35.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":37,\"eventTime\":\"00:00:36.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":38,\"eventTime\":\"00:00:37.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":39,\"eventTime\":\"00:00:38.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":40,\"eventTime\":\"00:00:39.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":41,\"eventTime\":\"00:00:40.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":42,\"eventTime\":\"00:00:41.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":43,\"eventTime\":\"00:00:42.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":44,\"eventTime\":\"00:00:43.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":45,\"eventTime\":\"00:00:44.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":46,\"eventTime\":\"00:00:45.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":47,\"eventTime\":\"00:00:46.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":48,\"eventTime\":\"00:00:47.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":49,\"eventTime\":\"00:00:48.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":50,\"eventTime\":\"00:00:49.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":51,\"eventTime\":\"00:01:00.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":52,\"eventTime\":\"00:01:01.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":53,\"eventTime\":\"00:01:02.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":54,\"eventTime\":\"00:01:03.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":55,\"eventTime\":\"00:01:04.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":56,\"eventTime\":\"00:01:05.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":57,\"eventTime\":\"00:01:06.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":58,\"eventTime\":\"00:01:07.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":59,\"eventTime\":\"00:01:08.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":60,\"eventTime\":\"00:01:09.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":61,\"eventTime\":\"00:01:10.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":62,\"eventTime\":\"00:01:11.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":63,\"eventTime\":\"00:01:12.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":64,\"eventTime\":\"00:01:13.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":65,\"eventTime\":\"00:01:14.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":66,\"eventTime\":\"00:01:15.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":67,\"eventTime\":\"00:01:16.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":68,\"eventTime\":\"00:01:17.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":69,\"eventTime\":\"00:01:18.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":70,\"eventTime\":\"00:01:19.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":71,\"eventTime\":\"00:01:20.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":72,\"eventTime\":\"00:01:21.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":73,\"eventTime\":\"00:01:22.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":74,\"eventTime\":\"00:01:23.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":75,\"eventTime\":\"00:01:24.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":76,\"eventTime\":\"00:01:25.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":77,\"eventTime\":\"00:01:26.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":78,\"eventTime\":\"00:01:27.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":79,\"eventTime\":\"00:01:28.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":80,\"eventTime\":\"00:01:29.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":81,\"eventTime\":\"00:01:30.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":82,\"eventTime\":\"00:01:31.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":83,\"eventTime\":\"00:01:32.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":84,\"eventTime\":\"00:01:33.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":85,\"eventTime\":\"00:01:34.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":86,\"eventTime\":\"00:01:35.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":87,\"eventTime\":\"00:01:36.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":88,\"eventTime\":\"00:01:37.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":89,\"eventTime\":\"00:01:38.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":90,\"eventTime\":\"00:01:39.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":91,\"eventTime\":\"00:01:40.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":92,\"eventTime\":\"00:01:41.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":93,\"eventTime\":\"00:01:42.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":94,\"eventTime\":\"00:01:43.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":95,\"eventTime\":\"00:01:44.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":96,\"eventTime\":\"00:01:45.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":97,\"eventTime\":\"00:01:42.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":98,\"eventTime\":\"00:01:42.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":99,\"eventTime\":\"00:01:42.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":100,\"eventTime\":\"00:01:42.000\"}]}}";
    String metadataWithInvalidTrackingUrls = "{\"remoteAudioData\":{\"podcastId\":\"1234\",\"trackingUrls\":[\"aaaa\",\"bbbb\"],\"events\":[{\"load_test\":\"Zapuc Ciprian\",\"count\":1,\"eventTime\":\"00:00:00.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":2,\"eventTime\":\"00:00:01.000\"}]}}";
    String metadataWithNoEvents = "{\"remoteAudioData\":{\"trackingUrls\":[\"Https://remoteaudio.npr.org\",\"Https://tracking.npr.org\"]}}";
    String metadataWithOneInvalidEvent = "{\"remoteAudioData\":{\"trackingUrls\":[\"Https://remoteaudio.npr.org\",\"Https://tracking.npr.org\"],\"events\":[{\"load_test\":\"Zapuc Ciprian\",\"count\":1,\"eventime\":\"00:00:00.000\"}]}}";
    String metadataWithValidAndInvalidEvents = "{\"remoteAudioData\":{\"trackingUrls\":[\"Https://remoteaudio.npr.org\",\"Https://tracking.npr.org\"],\"events\":[{\"load_test\":\"Zapuc Ciprian\",\"count\":1,\"eventTime\":\"00:00:00.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":2,\"eventime\":\"00:00:01.000\"}]}}";
    String metadataWithInvalidField = "{\"remoteAudioData\":{\"podcastId:\",\"trackingUrls\":[\"Https://remoteaudio.npr.org\",\"Https://tracking.npr.org\"],\"events\":[{\"load_test\":\"Zapuc Ciprian\",\"count\":1,\"eventTime\":\"00:00:00.000\"},{\"load_test\":\"Zapuc Ciprian\",\"count\":2,\"eventTime\":\"00:00:01.000\"}]}}";


    @Test
    public void testRadDefaults() {
        Rad.with(ApplicationProvider.getApplicationContext());
        assertEquals(Rad.getInstance().getUserAgent(), DEFAULT_USER_AGENT);
        assertEquals(Rad.getInstance().getExpirationTimeInterval(), DEFAULT_EXPIRATION_TIME_INTERVAL);
        assertEquals(Rad.getInstance().getSessionExpirationTimeInterval(), DEFAULT_SESSION_EXPIRATION_TIME_INTERVAL);
        assertEquals(Rad.getInstance().getSubmissionTimeInterval(), DEFAULT_SUBMISSION_TIME_INTERVAL);
        assertEquals(Rad.getInstance().getBatchSize(), DEFAULT_BATCH_SIZE);
    }

    @Test
    public void testRadConfiguration() {
        Rad.Configuration config = new Rad.Configuration();
        config.userAgent = userAgent;
        config.expirationTimeInterval = expirationTimeInterval;
        config.sessionExpirationTimeInterval = sessionExpirationTimeInterval;
        config.submissionTimeInterval = submissionTimeInterval;
        config.batchSize = batchSize;
        Rad.with(ApplicationProvider.getApplicationContext(), config);
        assertEquals(Rad.getInstance().getUserAgent(), userAgent);
        assertEquals(Rad.getInstance().getExpirationTimeInterval(), expirationTimeInterval);
        assertEquals(Rad.getInstance().getSessionExpirationTimeInterval(), sessionExpirationTimeInterval);
        assertEquals(Rad.getInstance().getSubmissionTimeInterval(), submissionTimeInterval);
        assertEquals(Rad.getInstance().getBatchSize(), batchSize);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullApplicationContextInit() {
        Rad.with(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidFrameworkConfiguration() {
        Rad.Configuration config = new Rad.Configuration();
        config.userAgent = userAgent;
        config.expirationTimeInterval = -expirationTimeInterval;
        config.sessionExpirationTimeInterval = -sessionExpirationTimeInterval;
        config.submissionTimeInterval = -submissionTimeInterval;
        config.batchSize = -batchSize;
        Rad.with(ApplicationProvider.getApplicationContext(), config);
    }

    @Test
    public void testRadStart() {
        Rad.with(ApplicationProvider.getApplicationContext());
        TrackSelection.Factory trackSelectionFactory = new AdaptiveTrackSelection.Factory(new DefaultBandwidthMeter());
        DefaultTrackSelector trackSelector = new DefaultTrackSelector(trackSelectionFactory);
        ExoPlayer mediaPlayer = ExoPlayerFactory.newSimpleInstance(ApplicationProvider.getApplicationContext(), trackSelector);
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(ApplicationProvider.getApplicationContext(),
                Util.getUserAgent(ApplicationProvider.getApplicationContext(), "Rad"), new DefaultBandwidthMeter());
        String path = Uri.parse("android.resource://com.npr.rad/raw/test.mp3").getPath();
        MediaSource mediaSource = new ExtractorMediaSource(Uri.parse(path), dataSourceFactory, new DefaultExtractorsFactory(), null, null);
        mediaPlayer.prepare(mediaSource);
        mediaPlayer.setPlayWhenReady(true);
        Rad.start(mediaPlayer, new DefaultTrackSelector());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullArgFrameworkStart() {
        Rad.with(ApplicationProvider.getApplicationContext());
        Rad.start(null, null);
    }

    @Test
    public void testStoreMetadata() {
        Rad.with(ApplicationProvider.getApplicationContext());
        Intent i = new Intent(ApplicationProvider.getApplicationContext(), PersistenceService.class);
        i.setAction(METADATA);
        i.putExtra(METADATA, metadata);
        Robolectric.setupService(PersistenceService.class).onHandleWork(i);
        ReportingData data = Rad.getInstance().getReportingData();
        assertTrue(data != null);
    }

    @Test
    public void testStoreMetadataWithInvalidJson() {
        Rad.with(ApplicationProvider.getApplicationContext());
        Intent i = new Intent(ApplicationProvider.getApplicationContext(), PersistenceService.class);
        i.setAction(METADATA);
        i.putExtra(METADATA, invalidJson);
        Robolectric.setupService(PersistenceService.class).onHandleWork(i);
        ReportingData data = Rad.getInstance().getReportingData();
        assertTrue(data == null);
    }

    @Test
    public void testStoreEmptyMetadata() {
        Rad.with(ApplicationProvider.getApplicationContext());
        Intent i = new Intent(ApplicationProvider.getApplicationContext(), PersistenceService.class);
        i.setAction(METADATA);
        i.putExtra(METADATA, emptyMetadata);
        Robolectric.setupService(PersistenceService.class).onHandleWork(i);
        ReportingData data = Rad.getInstance().getReportingData();
        assertTrue(data == null);
    }

    @Test
    public void testStoreMetadataWithMissingUrls() {
        Rad.with(ApplicationProvider.getApplicationContext());
        Intent i = new Intent(ApplicationProvider.getApplicationContext(), PersistenceService.class);
        i.setAction(METADATA);
        i.putExtra(METADATA, metadataWithMissingTrackingUrls);
        Robolectric.setupService(PersistenceService.class).onHandleWork(i);
        ReportingData data = Rad.getInstance().getReportingData();
        assertTrue(data == null);
    }

    @Test
    public void testStoreMetadataWithEmptyTrackingUrls() {
        Rad.with(ApplicationProvider.getApplicationContext());
        Intent i = new Intent(ApplicationProvider.getApplicationContext(), PersistenceService.class);
        i.setAction(METADATA);
        i.putExtra(METADATA, metadataWithEmptyTrackingUrls);
        Robolectric.setupService(PersistenceService.class).onHandleWork(i);
        ReportingData data = Rad.getInstance().getReportingData();
        assertTrue(data == null);
    }

    @Test
    public void testStoreMetadataNoEvents() {
        Rad.with(ApplicationProvider.getApplicationContext());
        Intent i = new Intent(ApplicationProvider.getApplicationContext(), PersistenceService.class);
        i.setAction(METADATA);
        i.putExtra(METADATA, metadataWithNoEvents);
        Robolectric.setupService(PersistenceService.class).onHandleWork(i);
        ReportingData data = Rad.getInstance().getReportingData();
        assertTrue(data == null);
    }

    @Test
    public void testStoreMetadataWithOneInvalidEvent() {
        Rad.with(ApplicationProvider.getApplicationContext());
        Intent i = new Intent(ApplicationProvider.getApplicationContext(), PersistenceService.class);
        i.setAction(METADATA);
        i.putExtra(METADATA, metadataWithOneInvalidEvent);
        Robolectric.setupService(PersistenceService.class).onHandleWork(i);
        ReportingData data = Rad.getInstance().getReportingData();
        assertTrue(data == null);
    }

    @Test
    public void testStoreMetadataWithValidAndInvalidEvents() {
        Rad.with(ApplicationProvider.getApplicationContext());
        Intent i = new Intent(ApplicationProvider.getApplicationContext(), PersistenceService.class);
        i.setAction(METADATA);
        i.putExtra(METADATA, metadataWithValidAndInvalidEvents);
        Robolectric.setupService(PersistenceService.class).onHandleWork(i);
        ReportingData data = Rad.getInstance().getReportingData();
        assertTrue(data != null);
    }

    @Test
    public void testStoreMetadataWithInvalidField() {
        Rad.with(ApplicationProvider.getApplicationContext());
        Intent i = new Intent(ApplicationProvider.getApplicationContext(), PersistenceService.class);
        i.setAction(METADATA);
        i.putExtra(METADATA, metadataWithInvalidField);
        Robolectric.setupService(PersistenceService.class).onHandleWork(i);
        ReportingData data = Rad.getInstance().getReportingData();
        assertTrue(data == null);
    }

    @Test
    public void testStoreMetadataWithInvalidTrackingUrls() {
        Rad.with(ApplicationProvider.getApplicationContext());
        Intent i = new Intent(ApplicationProvider.getApplicationContext(), PersistenceService.class);
        i.setAction(METADATA);
        i.putExtra(METADATA, metadataWithInvalidTrackingUrls);
        Robolectric.setupService(PersistenceService.class).onHandleWork(i);
        ReportingData data = Rad.getInstance().getReportingData();
        assertTrue(data == null);
    }

    @Test
    public void testStoreEvents() {

        Rad.with(ApplicationProvider.getApplicationContext());

        Intent i = new Intent(ApplicationProvider.getApplicationContext(), PersistenceService.class);
        i.setAction(METADATA);
        i.putExtra(METADATA, metadata);
        Robolectric.setupService(PersistenceService.class).onHandleWork(i);

        ReportingData radData = Rad.getInstance().getReportingData();

        Intent intent = new Intent(EVENTS);
        intent.putExtra(SESSION, radData.getSession().getSessionId());
        intent.putParcelableArrayListExtra(TRACKING_URLS, new ArrayList<Parcelable>(radData.getTrackingUrls()));
        intent.putParcelableArrayListExtra(EVENTS, (ArrayList<? extends Parcelable>) radData.getEvents());
        Robolectric.setupService(PersistenceService.class).onHandleWork(intent);

        int persistedRadDataSize = 0;
        for (ReportingData persistedRadData : DaoMaster.getInstance().getReportingData()) {
            persistedRadDataSize += persistedRadData.getEvents().size();
        }
        assertEquals(persistedRadDataSize, radData.getTrackingUrls().size()
                * radData.getEvents().size());
    }

    private int countRecords(String tableName, String data) {
        Pattern pattern = Pattern.compile(tableName + " TABLE(.+):");
        Matcher matcher = pattern.matcher(data);

        while (matcher.find()) {
            return Integer.parseInt(matcher.group(1).split("\\[|\\]|\\s")[2]);
        }
        return -1;
    }

    @Test
    public void testCleanup_MetadataWithNothingToReport_ActiveSession() {

        Rad.with(ApplicationProvider.getApplicationContext());

        Intent i = new Intent(ApplicationProvider.getApplicationContext(), PersistenceService.class);
        i.setAction(METADATA);
        i.putExtra(METADATA, metadata);
        Robolectric.setupService(PersistenceService.class).onHandleWork(i);

        DaoMaster.getInstance().cleanUpDb(null);
        assertEquals(100, countRecords("EVENT", DaoMaster.getInstance().printData()));
    }

    @Test
    public void testCleanup_MetadataWithNothingToReport_ExpiredSession() {

        Rad.with(ApplicationProvider.getApplicationContext());
        Rad.getInstance().setSessionExpirationTimeInterval(10);

        Intent i = new Intent(ApplicationProvider.getApplicationContext(), PersistenceService.class);
        i.setAction(METADATA);
        i.putExtra(METADATA, metadata);
        Robolectric.setupService(PersistenceService.class).onHandleWork(i);

        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        DaoMaster.getInstance().cleanUpDb(null);
        assertEquals(0, countRecords("EVENT", DaoMaster.getInstance().printData()));
    }

    @Test
    public void testMetadataWithReportCleanup() {
        Rad.with(ApplicationProvider.getApplicationContext());
        Intent i = new Intent(ApplicationProvider.getApplicationContext(), PersistenceService.class);
        i.setAction(METADATA);
        i.putExtra(METADATA, metadata);
        Robolectric.setupService(PersistenceService.class).onHandleWork(i);

        Intent intent = new Intent(EVENTS);
        intent.putExtra(SESSION, Rad.getInstance().getReportingData().getSession().getSessionId());
        intent.putParcelableArrayListExtra(TRACKING_URLS, new ArrayList<Parcelable>(Rad.getInstance().getReportingData().getTrackingUrls()));
        intent.putParcelableArrayListExtra(EVENTS, (ArrayList<? extends Parcelable>) Rad.getInstance().getReportingData().getEvents());
        Robolectric.setupService(PersistenceService.class).onHandleWork(intent);

        DaoMaster.getInstance().cleanUpDb(null);
        assertEquals(countRecords("EVENT", DaoMaster.getInstance().printData()), 100);
    }


    @Test
    public void testReporting() {
        Rad.with(ApplicationProvider.getApplicationContext());
        Intent i = new Intent(ApplicationProvider.getApplicationContext(), PersistenceService.class);
        i.setAction(METADATA);
        i.putExtra(METADATA, metadata);
        Robolectric.setupService(PersistenceService.class).onHandleWork(i);

        Intent intent = new Intent(EVENTS);
        intent.putExtra(SESSION, Rad.getInstance().getReportingData().getSession().getSessionId());
        intent.putParcelableArrayListExtra(TRACKING_URLS, new ArrayList<Parcelable>(Rad.getInstance().getReportingData().getTrackingUrls()));
        intent.putParcelableArrayListExtra(EVENTS, (ArrayList<? extends Parcelable>) Rad.getInstance().getReportingData().getEvents());
        Robolectric.setupService(PersistenceService.class).onHandleWork(intent);

        Robolectric.setupService(HTTPService.class).onHandleWork(new Intent());

        assertEquals(countRecords("EVENT", DaoMaster.getInstance().printData()), 100);
    }

}
