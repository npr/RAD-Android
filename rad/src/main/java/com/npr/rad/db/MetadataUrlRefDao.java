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

import java.util.List;

class MetadataUrlRefDao extends BaseDao {

    private MetadataUrlRefTable metadataUrlRefTable;

    MetadataUrlRefDao(SQLiteDatabase db) {
        super(db);
        metadataUrlRefTable = new MetadataUrlRefTable(db);
    }

    @Override
    String getTableName() {
        return MetadataUrlRefTable.TABLE_NAME;
    }

    public void create(long urlId, long metadataId) {
        metadataUrlRefTable.create(metadataId, urlId);
    }

    public void delete(long trackingUrlId, long metadataId) {
        metadataUrlRefTable.delete(String.valueOf(trackingUrlId), String.valueOf(metadataId));
    }

    List<Long> read() {
        return metadataUrlRefTable.read();
    }

    public void deleteNotIn(List<Long> remainingMetaDataIds) {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("(");
        for (int i = 0; i < remainingMetaDataIds.size() - 1; i++) {
            strBuilder.append(remainingMetaDataIds.get(i)).append(",");
        }
        strBuilder.append(remainingMetaDataIds.get(remainingMetaDataIds.size() - 1)).append(")");

        metadataUrlRefTable.deleteNotInt(strBuilder.toString());
    }
}
