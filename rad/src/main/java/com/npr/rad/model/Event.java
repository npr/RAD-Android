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
package com.npr.rad.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.TimeZone;

import androidx.annotation.NonNull;

public class Event implements Parcelable, Comparable<Event> {

    private long eventTime;
    private long timestamp;
    private long timezoneOffset;
    private String fields;
    private long eventId;

    public Event() {
        eventId = -1;
        timestamp = System.currentTimeMillis();
        timezoneOffset = TimeZone.getDefault().getRawOffset();
    }

    public long getEventTime() {
        return eventTime;
    }

    public void setEventTime(long eventTime) {
        this.eventTime = eventTime;
    }

    public String getFields() {
        return fields;
    }

    public void setFields(String fields) {
        this.fields = fields;
    }

    @NonNull
    @Override
    public String toString() {
        return fields;
    }

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTimezoneOffset() {
        return timezoneOffset;
    }

    public void setTimezoneOffset(long timezoneOffset) {
        this.timezoneOffset = timezoneOffset;
    }


    public Event(Parcel in) {
        eventTime = in.readLong();
        timestamp = in.readLong();
        timezoneOffset = in.readLong();
        eventId = in.readLong();
        fields = in.readString();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(eventTime);
        dest.writeLong(timestamp);
        dest.writeLong(timezoneOffset);
        dest.writeLong(eventId);
        dest.writeString(fields);
    }

    public static final Creator CREATOR = new Creator() {
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    @Override
    public int compareTo(@NonNull Event event) {
        return (int) (eventTime - event.eventTime);
    }
}


