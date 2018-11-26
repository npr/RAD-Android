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

import android.database.sqlite.SQLiteDatabase;

import com.npr.rad.model.Event;
import com.npr.rad.model.TrackingUrl;

import java.util.List;

class ReportingDao extends BaseDao {

    private ReportingTable reportingTable;

    ReportingDao(SQLiteDatabase db) {
        super(db);
        reportingTable = new ReportingTable(db);
    }

    @Override
    String getTableName() {
        return ReportingTable.TABLE_NAME;
    }

    public void create(long sessionId, List<TrackingUrl> trackingUrls, List<Event> events) {
        reportingTable.create(sessionId, trackingUrls, events);
    }

    List<Long> getSessions() {
        return reportingTable.getSessions();
    }

    List<Long> getTrackingUrls() {
        return reportingTable.getTrackingUrls();
    }

    List<Long> getSessions(TrackingUrl url) {
        return reportingTable.getSessions(url);
    }

    void deleteExpiredEvents() {
        reportingTable.deleteExpiredEvents();
    }

    List<Event> getEvents(long trackingUrlId, Long sessionId) {
        return reportingTable.read(trackingUrlId, sessionId);
    }

    public void delete(long trackingUrlId, long sessionId, long eventId, long eventTimestamp) {
        reportingTable.delete(trackingUrlId, sessionId, eventId, eventTimestamp);
    }
}
