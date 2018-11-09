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
import android.os.Handler;
import android.os.Parcelable;

import com.npr.rad.model.Event;

import java.util.ArrayList;
import java.util.List;

import androidx.core.app.JobIntentService;

import static com.npr.rad.Constants.EVENTS;
import static com.npr.rad.Constants.SESSION;
import static com.npr.rad.Constants.TRACKING_URLS;

public class PlaybackEventScheduler {

    private Rad rad;
    private Handler handler;
    private long position;

    PlaybackEventScheduler(Rad rad) {
        this.rad = rad;
        handler = new Handler();
    }

    public synchronized void scheduleEvent(final long startTime) {

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                position = rad.getPlayer().getContentPosition();
                processEvents(rad.getReportingData().getEvents(startTime, position));
                scheduleEvent(position);
            }
        }, 50);
    }

    private void processEvents(List<Event> events) {

        if (null == events || events.isEmpty()) {
            return;
        }
        Intent i = new Intent(EVENTS);
        i.putExtra(SESSION, rad.getReportingData().getSession().getSessionId());
        i.putParcelableArrayListExtra(TRACKING_URLS, new ArrayList<Parcelable>(rad.getReportingData().getTrackingUrls()));
        i.putParcelableArrayListExtra(EVENTS, (ArrayList<? extends Parcelable>) events);

        JobIntentService.enqueueWork(rad.getApplicationContext(), PersistenceService.class, 1, i);
        rad.getReportingData().removeEvents(events.toArray(new Event[]{}));

        String eventData = "Events:" + events.toString();
        rad.onEventTriggered(eventData);
    }

    public synchronized void cancel() {
        handler.removeCallbacksAndMessages(null);
    }

}

