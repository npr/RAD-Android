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

import com.npr.rad.model.TrackingUrl;

import java.util.ArrayList;
import java.util.List;

public class TrackingUrlTable {

    public static final String TABLE_NAME = "TRACKING_URL";
    private static final String TRACKING_URL = "TRACKING_URL";
    private static final String TRACKING_URL_ID = "TRACKING_URL_ID";
    private static final String WHERE_CLAUSE = "=?";

    private SQLiteDatabase db;

    TrackingUrlTable(SQLiteDatabase db) {
        this.db = db;
    }

    public static void createTable(SQLiteDatabase db) {
        db.execSQL(
                "create table " + TABLE_NAME + " ("
                        + TRACKING_URL + " text unique not null,"
                        + TRACKING_URL_ID + " integer primary key autoincrement"
                        + ")"
        );
    }

    public TrackingUrl create(String trackingUrl) {
        TrackingUrl url = getTrackingUrl(trackingUrl);
        if (null == url) {
            url = new TrackingUrl(trackingUrl);
            ContentValues values = new ContentValues();
            values.put(TRACKING_URL, trackingUrl);
            url.setTrackingUrlId(db.insert(TABLE_NAME, null, values));
        }
        return url;
    }

    public List<TrackingUrl> read() {
        ArrayList<TrackingUrl> results = new ArrayList<>();
        TrackingUrl trackingUrl;
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    trackingUrl = new TrackingUrl(cursor.getString(cursor.getColumnIndex(TRACKING_URL)));
                    trackingUrl.setTrackingUrlId(cursor.getLong(cursor.getColumnIndex(TRACKING_URL_ID)));
                    results.add(trackingUrl);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return results;
    }

    private TrackingUrl getTrackingUrl(String trackingUrl) {
        TrackingUrl url = null;
        Cursor cursor = db.rawQuery("SELECT  *  FROM " + TABLE_NAME + " WHERE " + TRACKING_URL + "=?", new String[]{trackingUrl});
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                url = new TrackingUrl(trackingUrl);
                url.setTrackingUrlId(cursor.getLong(cursor.getColumnIndex(TRACKING_URL_ID)));
            }
            cursor.close();
        }
        return url;
    }

    public void delete(String trackingUrl) {
        String[] whereArgs = new String[]{trackingUrl};
        db.delete(TABLE_NAME, TRACKING_URL + WHERE_CLAUSE, whereArgs);
    }

    public void deleteNotIn(String trackingUrl) {
        db.delete(TABLE_NAME, TRACKING_URL_ID + " NOT IN " + trackingUrl, null);
    }
}
