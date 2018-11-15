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

public class TrackingUrl implements Parcelable {

    private String trackingUrlString;
    private long trackingUrlId;

    public TrackingUrl(String trackingUrlString) {
        this.trackingUrlString = trackingUrlString;
        trackingUrlId = -1;
    }

    public String getTrackingUrlString() {
        return trackingUrlString;
    }

    public void setTrackingUrlString(String trackingUrlString) {
        this.trackingUrlString = trackingUrlString;
    }

    public long getTrackingUrlId() {
        return trackingUrlId;
    }

    public void setTrackingUrlId(long trackingUrlId) {
        this.trackingUrlId = trackingUrlId;
    }

    public TrackingUrl(Parcel in) {
        trackingUrlId = in.readLong();
        trackingUrlString = in.readString();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(trackingUrlId);
        dest.writeString(trackingUrlString);
    }

    public static final Creator CREATOR = new Creator() {
        public TrackingUrl createFromParcel(Parcel in) {
            return new TrackingUrl(in);
        }

        public TrackingUrl[] newArray(int size) {
            return new TrackingUrl[size];
        }
    };

}
