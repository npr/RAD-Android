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

import java.util.ArrayList;
import java.util.List;

public class MetadataUrlRefTable {

    public static final String TABLE_NAME = "METADATA_URL_REF";
    private static final String TRACKING_URL_ID = "TRACKING_URL_ID";
    private static final String METADATA_ID = "METADATA_ID";


    private SQLiteDatabase db;

    MetadataUrlRefTable(SQLiteDatabase db) {
        this.db = db;
    }

    static void createTable(SQLiteDatabase db) {
        db.execSQL(
                "create table " + TABLE_NAME + " ("
                        + TRACKING_URL_ID + " integer not null,"
                        + METADATA_ID + " integer not null,"
                        + " FOREIGN KEY (" + TRACKING_URL_ID + ") REFERENCES " + TrackingUrlTable.TABLE_NAME + "(" + TRACKING_URL_ID + "),"
                        + " FOREIGN KEY (" + METADATA_ID + ") REFERENCES " + SessionTable.TABLE_NAME + "(" + METADATA_ID + ")"
                        + ")"
        );
    }

    public void create(long metadataId, long trackingUrlId) {
        List<Long> trackingUrlIds = getTrackingUrlId(metadataId);
        if (!trackingUrlIds.contains(trackingUrlId)) {
            ContentValues values = new ContentValues();
            values.put(METADATA_ID, metadataId);
            values.put(TRACKING_URL_ID, trackingUrlId);
            db.insert(TABLE_NAME, null, values);
        }
    }

    private List<Long> getTrackingUrlId(long metadataId) {
        ArrayList<Long> results = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT  " + TRACKING_URL_ID + "  FROM " + TABLE_NAME + " WHERE " + METADATA_ID + "=?", new String[]{String.valueOf(metadataId)});
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

    public void delete(String trackingUrlId, String metadataId) {
        String[] whereArgs = new String[]{trackingUrlId, metadataId};
        db.delete(TABLE_NAME, TRACKING_URL_ID + "=? and " + METADATA_ID + "=?", whereArgs);
    }

    List<Long> read() {
        ArrayList<Long> results = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT " + TRACKING_URL_ID + " FROM " + TABLE_NAME, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    long id = cursor.getLong(cursor.getColumnIndex(TRACKING_URL_ID));
                    if (!results.contains(id)) {
                        results.add(id);
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return results;
    }

    void deleteNotInt(String remainingMetaDataIds) {
        String whereClause = METADATA_ID + " NOT IN " + remainingMetaDataIds;
        db.delete(TABLE_NAME, whereClause, null);
    }
}
