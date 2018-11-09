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

import com.npr.rad.model.TrackingUrl;

import java.util.List;

class TrackingUrlDao extends BaseDao {

    private TrackingUrlTable trackingUrlTable;

    TrackingUrlDao(SQLiteDatabase db) {
        super(db);
        trackingUrlTable = new TrackingUrlTable(db);
    }

    @Override
    String getTableName() {
        return TrackingUrlTable.TABLE_NAME;
    }

    public TrackingUrl create(TrackingUrl url) {
        return trackingUrlTable.create(url.getTrackingUrlString());
    }

    public List<TrackingUrl> read() {
        return trackingUrlTable.read();
    }

    public void delete(String trackingUrl) {
        trackingUrlTable.delete(trackingUrl);
    }

    public void deleteNotIn(List<Long> trackingUrls) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(");
        for (int i = 0; i < trackingUrls.size() - 1; i++) {
            stringBuilder.append(trackingUrls.get(i)).append(", ");
        }
        stringBuilder.append(trackingUrls.get(trackingUrls.size() - 1)).append(")");
        trackingUrlTable.deleteNotIn(stringBuilder.toString());
    }
}
