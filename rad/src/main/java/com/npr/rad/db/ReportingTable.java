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

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.npr.rad.Rad;
import com.npr.rad.model.Event;
import com.npr.rad.model.TrackingUrl;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class ReportingTable {

    public static final String TABLE_NAME = "REPORTING";
    private static final String TRACKING_URL_ID = "TRACKING_URL_ID";
    private static final String SESSION_ID = "SESSION_ID";
    private static final String EVENT_ID = "EVENT_ID";
    private static final String TIMESTAMP = "TIMESTAMP";
    private static final String TIMEZONE_OFFSET = "TIMEZONE_OFFSET";

    private SQLiteDatabase db;

    ReportingTable(SQLiteDatabase db) {
        this.db = db;
    }

    static void createTable(SQLiteDatabase db) {
        db.execSQL(
                "create table " + TABLE_NAME + " ("
                        + SESSION_ID + " integer not null,"
                        + TRACKING_URL_ID + " integer not null,"
                        + EVENT_ID + " integer not null,"
                        + TIMESTAMP + " integer not null,"
                        + TIMEZONE_OFFSET + " integer not null,"
                        + " FOREIGN KEY (" + TRACKING_URL_ID + ") REFERENCES " + TrackingUrlTable.TABLE_NAME + "(" + TRACKING_URL_ID + "),"
                        + " FOREIGN KEY (" + SESSION_ID + ") REFERENCES " + SessionTable.TABLE_NAME + "(" + SESSION_ID + "),"
                        + " FOREIGN KEY (" + EVENT_ID + ") REFERENCES " + EventTable.TABLE_NAME + "(" + EVENT_ID + ")"
                        + ")"
        );
    }

    public void create(long sessionId, List<TrackingUrl> trackingUrls, List<Event> events) {
        ContentValues values = new ContentValues();
        for (TrackingUrl trackingUrl : trackingUrls) {
            for (Event event : events) {
                values.put(TRACKING_URL_ID, trackingUrl.getTrackingUrlId());
                values.put(SESSION_ID, sessionId);
                values.put(EVENT_ID, event.getEventId());
                values.put(TIMESTAMP, System.currentTimeMillis());
                values.put(TIMEZONE_OFFSET, TimeZone.getDefault().getRawOffset());
                db.insert(TABLE_NAME, null, values);
            }
        }
    }

    List<Long> getSessions() {
        ArrayList<Long> results = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT DISTINCT " + SESSION_ID + " FROM " + TABLE_NAME, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    results.add(cursor.getLong(cursor.getColumnIndex(SESSION_ID)));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return results;
    }

    List<Long> getTrackingUrls() {
        ArrayList<Long> results = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT DISTINCT " + TRACKING_URL_ID + " FROM " + TABLE_NAME, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    results.add(cursor.getLong(cursor.getColumnIndex(TRACKING_URL_ID)));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return results;
    }

    List<Long> getSessions(TrackingUrl url) {
        ArrayList<Long> results = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT DISTINCT " + SESSION_ID + " FROM " + TABLE_NAME + " WHERE " + TRACKING_URL_ID + " =?",
                new String[]{String.valueOf(url.getTrackingUrlId())});
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    results.add(cursor.getLong(cursor.getColumnIndex(SESSION_ID)));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return results;
    }

    void deleteExpiredEvents() {
        long age = System.currentTimeMillis() - Rad.getInstance().getExpirationTimeInterval();
        db.delete(TABLE_NAME, TIMESTAMP + " <=?", new String[]{String.valueOf(age)});
    }

    List<Event> read(long trackingUrlId, Long sessionId) {
        ArrayList<Event> results = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE "
                        + TRACKING_URL_ID + " =? AND "
                        + SESSION_ID + " =? "
                , new String[]{String.valueOf(trackingUrlId), String.valueOf(sessionId)});

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Event event = new Event();
                    event.setEventId(cursor.getLong(cursor.getColumnIndex(EVENT_ID)));
                    event.setTimestamp(cursor.getLong(cursor.getColumnIndex(TIMESTAMP)));
                    event.setTimezoneOffset(cursor.getLong(cursor.getColumnIndex(TIMEZONE_OFFSET)));

                    results.add(event);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return results;
    }

    public void delete(long trackingUrlId, long sessionId, long eventId) {
        String[] whereArgs = new String[]{String.valueOf(trackingUrlId), String.valueOf(sessionId), String.valueOf(eventId)};
        db.delete(TABLE_NAME, TRACKING_URL_ID + " =? AND " + SESSION_ID + " =? AND "
                + EVENT_ID + " =?", whereArgs);
    }
}
