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
import com.npr.rad.model.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SessionTable {

    public static final String TABLE_NAME = "SESSION";
    private static final String SESSION_ID = "SESSION_ID";
    private static final String SESSION_UUID = "SESSION_UUID";
    private static final String METADATA_ID = "METADATA_ID";
    private static final String TIME_STAMP = "TIME_STAMP";

    private SQLiteDatabase db;

    SessionTable(SQLiteDatabase db) {
        this.db = db;
    }

    static void createTable(SQLiteDatabase db) {
        db.execSQL(
                "create table " + TABLE_NAME + " ("
                        + SESSION_ID + " integer primary key autoincrement,"
                        + SESSION_UUID + " text unique not null,"
                        + TIME_STAMP + " integer not null,"
                        + METADATA_ID + " integer not null,"
                        + " FOREIGN KEY (" + METADATA_ID + ") REFERENCES " + MetadataTable.TABLE_NAME + "(" + METADATA_ID + ")"
                        + ")"
        );
    }

    public Session create(Session session) {
        long metadataId = session.getMetadataId();
        long now = System.currentTimeMillis();
        List<Session> storedSessions = getSessions(metadataId);
        for (Session storedSession : storedSessions) {
            if (now - storedSession.getTimestamp() < Rad.getInstance().getSessionExpirationTimeInterval()) {
                return storedSession;
            }
        }
        long timeStamp = System.currentTimeMillis();
        String uuid = UUID.randomUUID().toString();

        ContentValues values = new ContentValues();
        values.put(TIME_STAMP, timeStamp);
        values.put(SESSION_UUID, uuid);
        values.put(METADATA_ID, metadataId);
        long sessionID = db.insert(TABLE_NAME, null, values);

        Session result = new Session();
        result.setSessionId(sessionID);
        result.setTimestamp(timeStamp);
        result.setMetadataId(metadataId);
        result.setSessionUuid(uuid);

        return result;
    }

    Session getSession(long sessionId) {
        Session session = null;
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + SESSION_ID + " =?",
                new String[]{String.valueOf(sessionId)});
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                session = new Session();
                session.setSessionId(cursor.getLong(cursor.getColumnIndex(SESSION_ID)));
                session.setMetadataId(cursor.getLong(cursor.getColumnIndex(METADATA_ID)));
                session.setTimestamp(cursor.getLong(cursor.getColumnIndex(TIME_STAMP)));
                session.setSessionUuid(cursor.getString(cursor.getColumnIndex(SESSION_UUID)));
            }

            cursor.close();
        }
        return session;
    }

    private List<Session> getSessions(long metadataId) {
        ArrayList<Session> results = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + METADATA_ID + " =?",
                new String[]{String.valueOf(metadataId)});
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Session session = new Session();
                    session.setSessionId(cursor.getLong(cursor.getColumnIndex(SESSION_ID)));
                    session.setMetadataId(cursor.getLong(cursor.getColumnIndex(METADATA_ID)));
                    session.setTimestamp(cursor.getLong(cursor.getColumnIndex(TIME_STAMP)));
                    session.setSessionUuid(cursor.getString(cursor.getColumnIndex(SESSION_UUID)));
                    results.add(session);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return results;
    }

    public void delete(long sessionId) {
        String[] whereArgs = new String[]{String.valueOf(sessionId)};
        db.delete(TABLE_NAME, SESSION_ID + " =?", whereArgs);
    }

    void deleteNotIn(List<Long> sessionIds) {
        String whereClause = "";
        if (null != sessionIds && !sessionIds.isEmpty()) {
            whereClause = SESSION_ID + " NOT IN " + listAsText(sessionIds) + " AND ";
        }
        long expiration = System.currentTimeMillis() - Rad.getInstance().getSessionExpirationTimeInterval();
        whereClause += TIME_STAMP + "<" + expiration;
        db.delete(TABLE_NAME, whereClause, null);
    }

    private static String listAsText(List<Long> args) {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("(");
        for (int i = 0; i < args.size() - 1; i++) {
            strBuilder.append(args.get(i)).append(",");
        }
        strBuilder.append(args.get(args.size() - 1)).append(")");
        return strBuilder.toString();
    }

    List<Long> getMetadataIds() {
        ArrayList<Long> ids = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT DISTINCT " + METADATA_ID + " FROM " + TABLE_NAME, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    ids.add(cursor.getLong(cursor.getColumnIndex(METADATA_ID)));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return ids;
    }

    void deleteAllExpired() {
        long expiration = System.currentTimeMillis() - Rad.getInstance().getSessionExpirationTimeInterval();
        String whereClause = " WHERE " + TIME_STAMP + "<" + expiration;
        db.execSQL(" DELETE FROM " + TABLE_NAME + " " + whereClause);
    }
}
