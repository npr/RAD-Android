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

public class PlayBackEvent {

    private State state;
    private long millis;

    public PlayBackEvent(State state, long milis) {
        this.state = state;
        this.millis = milis;
    }

    public State getState() {
        return state;
    }

    public long getMillis() {
        return millis;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PlayBackEvent) {
            return ((PlayBackEvent) obj).getState() == state && ((PlayBackEvent) obj).getMillis() == millis;
        }
        return false;
    }

    public enum State {
        STARTED,
        STOPPED
    }
}
