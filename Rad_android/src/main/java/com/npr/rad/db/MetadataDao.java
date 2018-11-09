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

import com.npr.rad.model.Metadata;

import java.util.List;

class MetadataDao extends BaseDao {

    private MetadataTable metadataTable;

    MetadataDao(SQLiteDatabase db) {
        super(db);
        metadataTable = new MetadataTable(db);
    }

    @Override
    String getTableName() {
        return MetadataTable.TABLE_NAME;
    }

    public Metadata create(Metadata metadata) {
        return metadataTable.create(metadata);
    }

    public void delete(String trackingUrl) {
        metadataTable.delete(trackingUrl);
    }

    public void deleteNotIn(List<Long> metadataIds) {
        if (metadataIds.isEmpty()) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(");
        if (metadataIds.size() > 1) {
            for (int i = 0; i < metadataIds.size() - 1; i++) {
                stringBuilder.append(metadataIds.get(i)).append(", ");
            }
        }
        stringBuilder.append(metadataIds.get(metadataIds.size() - 1)).append(")");
        metadataTable.deleteNotIn(stringBuilder.toString());
    }

    public Metadata getMetadata(long metadataId) {
        return metadataTable.getMetadata(metadataId);
    }
}
