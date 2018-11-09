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

import com.npr.rad.Rad;
import com.npr.rad.model.Session;

import java.util.List;

class SessionDao extends BaseDao {

    private SessionTable sessionTable;

    SessionDao(SQLiteDatabase db) {
        super(db);
        sessionTable = new SessionTable(db);
    }

    @Override
    String getTableName() {
        return SessionTable.TABLE_NAME;
    }

    public Session create(Session session) {
        return sessionTable.create(session);
    }

    public void delete(long sessionId) {
        sessionTable.delete(sessionId);
    }

    public void deleteNotIn(List<Long> sessionIds) {
        sessionTable.deleteNotIn(sessionIds);
    }

    public void deleteAll() {
        sessionTable.deleteAll();
    }

    public List<Long> getMetadatas() {
        return sessionTable.getMetadataIds();
    }

    public Session getSession(Long sessionId) {
        return sessionTable.getSession(sessionId);
    }

}
