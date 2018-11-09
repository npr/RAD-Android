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
package com.npr.rad.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.npr.rad.model.Event;
import com.npr.rad.model.Metadata;
import com.npr.rad.model.ReportingData;
import com.npr.rad.model.Session;
import com.npr.rad.model.TrackingUrl;

import java.util.ArrayList;
import java.util.List;


public class DaoMaster {


    private EventDao eventDao;
    private TrackingUrlDao trackingUrlDao;
    private SessionDao sessionDao;
    private MetadataDao metadataDao;
    private ReportingDao reportingDao;
    private static DaoMaster instance;

    private DaoMaster() {
    }

    public static synchronized DaoMaster getInstance() {
        if (instance == null) {
            instance = new DaoMaster();
        }
        return instance;
    }

    public synchronized void setContext(Context context) {
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        trackingUrlDao = new TrackingUrlDao(database);
        metadataDao = new MetadataDao(database);
        sessionDao = new SessionDao(database);
        eventDao = new EventDao(database);
        reportingDao = new ReportingDao(database);
    }

    public synchronized void storeEvents(long sessionId, List<TrackingUrl> trackingUrls, List<Event> events) {
        reportingDao.create(sessionId, trackingUrls, events);
    }

    public synchronized ReportingData storeReportingData(ReportingData reportingData) {
        List<TrackingUrl> storedTrackingUrls = new ArrayList<>();
        for (TrackingUrl trackingUrl : reportingData.getTrackingUrls()) {
            storedTrackingUrls.add(trackingUrlDao.create(trackingUrl));
        }
        reportingData.setTrackingUrls(storedTrackingUrls);
        reportingData.setMetadata(metadataDao.create(reportingData.getMetadata()));
        reportingData.getSession().setMetadataId(reportingData.getMetadata().getMetadataId());
        reportingData.setSession(sessionDao.create(reportingData.getSession()));
        reportingData.setEvents(eventDao.create(reportingData.getMetadata().getHash(), reportingData.getEvents()));
        return reportingData;
    }

    public synchronized String printData() {
        if (null == sessionDao || null == trackingUrlDao || null == eventDao) {
            return "Please initialize framework!";
        }
        return trackingUrlDao.getTableAsString() + "\n" +
                metadataDao.getTableAsString() + "\n" +
                sessionDao.getTableAsString() + "\n" +
                eventDao.getTableAsString() + "\n" +
                reportingDao.getTableAsString();
    }

    public synchronized void cleanUpDb(ReportingData reportingData) {
        reportingDao.deleteExpiredEvents();

        List<Long> sessionIds = reportingDao.getSessions();

        if (reportingData != null && reportingData.getSession() != null && !sessionIds.contains(reportingData.getSession().getSessionId())) {
            sessionIds.add(reportingData.getSession().getSessionId());
        }

        if (sessionIds.isEmpty()) {
            sessionDao.deleteAll();
        } else {
            sessionDao.deleteNotIn(sessionIds);
        }

        List<Long> metadataIds = sessionDao.getMetadatas();
        if (reportingData != null && reportingData.getMetadata() != null && !metadataIds.contains(reportingData.getMetadata().getMetadataId())) {
            metadataIds.add(reportingData.getMetadata().getMetadataId());
        }

        if (!metadataIds.isEmpty()) {
            metadataDao.deleteNotIn(metadataIds);
            List<String> hashes = new ArrayList<>();
            for (long id : metadataIds) {
                hashes.add(metadataDao.getMetadata(id).getHash());
            }
            eventDao.deleteNotIn(hashes.toArray(new String[]{}));
        } else {
            metadataDao.deleteAll();
            eventDao.deleteAll();
        }

        List<Long> trackingUrls = reportingDao.getTrackingUrls();
        if (reportingData != null && reportingData.getTrackingUrls() != null) {
            for (TrackingUrl url : reportingData.getTrackingUrls()) {
                if (!trackingUrls.contains(url.getTrackingUrlId())) {
                    trackingUrls.add(url.getTrackingUrlId());
                }
            }
        }

        if (!trackingUrls.isEmpty()) {
            trackingUrlDao.deleteNotIn(trackingUrls);
        } else {
            trackingUrlDao.deleteAll();
        }
    }

    public synchronized List<ReportingData> getReportingData() {
        ArrayList<ReportingData> results = new ArrayList<>();
        List<TrackingUrl> trackingUrls = trackingUrlDao.read();
        for (TrackingUrl url : trackingUrls) {
            List<Long> sessionIds = reportingDao.getSessions(url);
            for (Long sessionId : sessionIds) {
                ReportingData data = new ReportingData();
                data.addTrackingUrl(url);
                List<Event> events = reportingDao.getEvents(url.getTrackingUrlId(), sessionId);
                for (Event e : events) {
                    Event storedEvent = eventDao.getEvent(e.getEventId());
                    e.setEventTime(storedEvent.getEventTime());
                    e.setFields(storedEvent.getFields());
                    data.addEvent(e);
                }
                Session session = sessionDao.getSession(sessionId);
                if (session == null) {
                    continue;
                }
                Metadata metadata = metadataDao.getMetadata(session.getMetadataId());
                data.setSession(session);
                data.setMetadata(metadata);
                results.add(data);
            }
        }
        return results;
    }

    public synchronized void deleteReportingData(ReportingData rad) {
        for (Event event : rad.getEvents()) {
            reportingDao.delete(rad.getTrackingUrls().get(0).getTrackingUrlId(), rad.getSession().getSessionId(), event.getEventId());
        }
    }
}
