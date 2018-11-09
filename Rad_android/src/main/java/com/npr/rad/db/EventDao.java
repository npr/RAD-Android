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

import java.util.List;

class EventDao extends BaseDao {

    private EventTable eventTable;

    EventDao(SQLiteDatabase db) {
        super(db);
        eventTable = new EventTable(db);
    }

    @Override
    String getTableName() {
        return EventTable.TABLE_NAME;
    }

    public List<Event> create(String hash, List<Event> events) {
        return eventTable.create(hash, events);
    }

    public void deleteNotIn(String[] hashes) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(");
        for (int i = 0; i < hashes.length - 1; i++) {
            stringBuilder.append("'").append(hashes[i]).append("'").append(", ");
        }
        stringBuilder.append("'").append(hashes[hashes.length - 1]).append("'").append(")");
        eventTable.delete(stringBuilder.toString());
    }

    public Event getEvent(long eventId) {
        return eventTable.getEvent(eventId);
    }
}
