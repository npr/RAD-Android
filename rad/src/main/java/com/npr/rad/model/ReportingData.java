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
package com.npr.rad.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import androidx.annotation.Nullable;

public class ReportingData {

    private List<TrackingUrl> trackingUrls;
    private Metadata metadata;
    private Session session;
    private List<Event> events;

    public ReportingData() {
        events = new ArrayList<>();
        trackingUrls = new ArrayList<>();
    }

    public ReportingData(ReportingData data, List<Event> events) {
        this();
        trackingUrls = data.trackingUrls;
        metadata = data.metadata;
        session = data.session;
        this.events = events;
    }

    public List<TrackingUrl> getTrackingUrls() {
        return trackingUrls;
    }

    public void setTrackingUrls(List<TrackingUrl> trackingUrls) {
        this.trackingUrls = trackingUrls;
    }

    public void addTrackingUrl(TrackingUrl url) {
        trackingUrls.add(url);
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public List<Event> getEvents() {
        return events;
    }

    public List<Event> getEvents(long startTime, long stopTime) {
        ArrayList<Event> results = new ArrayList<>();
        if (startTime > stopTime) {
            return results;
        }
        Event event;
        for (int i = 0; i < events.size(); i++) {
            event = events.get(i);
            if (event.getEventTime() >= startTime && event.getEventTime() <= stopTime) {
                results.add(event);
            }
        }
        return results;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public void addEvent(Event event) {
        events.add(event);
    }

    public void sortEvents() {
        Collections.sort(events);
    }

    public void removeEvents(Event[] args) {
        events.removeAll(Arrays.asList(args));
    }

    public List<ReportingData> split(int batchSize) {
        List<ReportingData> results = new ArrayList<>();
        Event[] eventsArray = events.toArray(new Event[]{});
        for (int i = 0; i < eventsArray.length; i += batchSize) {
            ReportingData rad = new ReportingData();
            rad.setTrackingUrls(trackingUrls);
            rad.setMetadata(metadata);
            rad.setSession(session);
            rad.setEvents(Arrays.asList(Arrays.copyOfRange(eventsArray, i, Math.min(eventsArray.length, i + batchSize))));
            results.add(rad);
        }
        return results;
    }

    @Nullable
    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("TRACKING URLS:");
        for (TrackingUrl url : trackingUrls) {
            strBuilder.append("\n").append(url.getTrackingUrlString());
        }
        strBuilder.append("\n\nMETADATA:\n").append(metadata.getFields());
        strBuilder.append("\n\nSESSION:").append(session.getSessionUuid());
        strBuilder.append("\n\nEVENTS:");
        for (Event event : events) {
            strBuilder.append("\n").append(event.getFields());
        }
        return strBuilder.toString();
    }
}
