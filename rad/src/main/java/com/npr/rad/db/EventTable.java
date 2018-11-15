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

import com.npr.rad.model.Event;

import java.util.List;

public class EventTable {

    public static final String TABLE_NAME = "EVENT";
    private static final String EVENT_TIME = "EVENT_TIME";
    private static final String FIELDS = "FIELDS";
    private static final String METADATA_HASH = "METADATA_HASH";
    private static final String EVENT_ID = "EVENT_ID";

    private SQLiteDatabase db;

    EventTable(SQLiteDatabase db) {
        this.db = db;
    }

    static void createTable(SQLiteDatabase db) {
        db.execSQL(
                "create table " + TABLE_NAME + " ("
                        + EVENT_ID + " integer primary key autoincrement,"
                        + EVENT_TIME + " text not null,"
                        + METADATA_HASH + " text not null,"
                        + FIELDS + " text,"
                        + "UNIQUE(" + METADATA_HASH + "," + FIELDS + ")"
                        + ")"
        );
    }

    public List<Event> create(String metadataHash, List<Event> events) {
        for (Event event : events) {
            ContentValues values = new ContentValues();
            values.put(EVENT_TIME, event.getEventTime());
            values.put(METADATA_HASH, metadataHash);
            values.put(FIELDS, event.getFields());
            long eventId = db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
            if (-1 == eventId) {
                event.setEventId(getEventId(event.getEventTime(), metadataHash, event.getFields()));
            } else {
                event.setEventId(eventId);
            }
        }
        return events;
    }

    private long getEventId(long eventTime, String metadataHash, String fields) {
        long id = -1;
        Cursor cursor = db.rawQuery("SELECT " + EVENT_ID + " FROM " + TABLE_NAME + " WHERE "
                        + EVENT_TIME + "=? AND "
                        + METADATA_HASH + "=? AND "
                        + FIELDS + "=? "
                , new String[]{String.valueOf(eventTime), metadataHash, fields});
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                id = cursor.getLong(cursor.getColumnIndex(EVENT_ID));
            }
            cursor.close();
        }
        return id;
    }


    public void delete(String notInArgs) {
        db.delete(TABLE_NAME, METADATA_HASH + " NOT IN " + notInArgs, null);
    }

    Event getEvent(long eventId) {
        Event e = null;
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + EVENT_ID + "=?", new String[]{String.valueOf(eventId)});
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                e = new Event();
                e.setEventId(cursor.getLong(cursor.getColumnIndex(EVENT_ID)));
                e.setEventTime(cursor.getLong(cursor.getColumnIndex(EVENT_TIME)));
                e.setFields(cursor.getString(cursor.getColumnIndex(FIELDS)));
            }
            cursor.close();
        }

        return e;
    }
}
