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

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

abstract class BaseDao {

    protected SQLiteDatabase db;

    BaseDao(SQLiteDatabase dataBase) {
        this.db = dataBase;
    }


    abstract String getTableName();

    String getTableAsString() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(getTableName()).append(" TABLE").append(" [").append(DatabaseUtils.queryNumEntries(db, getTableName())).append(" records]:\n");
        Cursor allRows = db.rawQuery("SELECT * FROM " + getTableName(), null);
        if (allRows.moveToFirst()) {
            String[] columnNames = allRows.getColumnNames();
            do {
                for (String name : columnNames) {
                    strBuilder.append(String.format("%s: %s\n", name,
                            allRows.getString(allRows.getColumnIndex(name))));
                }
                strBuilder.append("\n");

            } while (allRows.moveToNext());
        }
        allRows.close();
        return strBuilder.toString();
    }

    public void deleteAll() {
        db.execSQL(" DELETE FROM " + getTableName());
    }
}
