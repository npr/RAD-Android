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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "RAD_DB";

    private static final int DATABASE_VERSION = 1;


    DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        TrackingUrlTable.createTable(db);
        MetadataTable.createTable(db);
        SessionTable.createTable(db);
        EventTable.createTable(db);
        ReportingTable.createTable(db);
    }

    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        /*  Method must be implemented by adopters who wish to support versioning and schema migrations. */
    }
}