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

import com.npr.rad.model.Metadata;


public class MetadataTable {

    public static final String TABLE_NAME = "METADATA";
    private static final String FIELDS = "FIELDS";
    private static final String HASH = "HASH";
    private static final String METADATA_ID = "METADATA_ID";
    private static final String WHERE_CLAUSE = "=?";

    private SQLiteDatabase db;

    MetadataTable(SQLiteDatabase db) {
        this.db = db;
    }

    public static void createTable(SQLiteDatabase db) {
        db.execSQL(
                "create table " + TABLE_NAME + " ("
                        + FIELDS + " text unique not null,"
                        + HASH + " text unique not null,"
                        + METADATA_ID + " integer primary key autoincrement"
                        + ")"
        );
    }

    public Metadata create(Metadata metadata) {
        String hash = metadata.getHash();
        String fields = metadata.getFields();
        Metadata data = getMetadata(hash);
        if (null != data) {
            return data;
        }
        data = new Metadata(fields);
        data.setHash(hash);
        ContentValues values = new ContentValues();
        values.put(FIELDS, fields);
        values.put(HASH, hash);
        data.setMetadataId(db.insert(TABLE_NAME, null, values));
        return data;
    }

    private Metadata getMetadata(String hash) {
        Metadata metadata = null;
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + HASH + "=?", new String[]{hash});
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                metadata = new Metadata();
                metadata.setHash(cursor.getString(cursor.getColumnIndex(HASH)));
                metadata.setMetadataId(cursor.getLong(cursor.getColumnIndex(METADATA_ID)));
                metadata.setFields(cursor.getString(cursor.getColumnIndex(FIELDS)));
            }
            cursor.close();
        }
        return metadata;
    }

    public Metadata getMetadata(long metadataId) {
        Metadata metadata = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + METADATA_ID + "=?"
                , new String[]{String.valueOf(metadataId)});

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                metadata = new Metadata();
                metadata.setHash(cursor.getString(cursor.getColumnIndex(HASH)));
                metadata.setMetadataId(cursor.getLong(cursor.getColumnIndex(METADATA_ID)));
                metadata.setFields(cursor.getString(cursor.getColumnIndex(FIELDS)));
            }
            cursor.close();
        }
        return metadata;
    }

    public void delete(String hash) {
        String[] whereArgs = new String[]{hash};
        db.delete(TABLE_NAME, HASH + WHERE_CLAUSE, whereArgs);
    }

    public void deleteNotIn(String metadataIds) {
        db.delete(TABLE_NAME, METADATA_ID + " NOT IN " + metadataIds, null);
    }
}
