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
package com.npr.rad;

import android.content.Intent;
import android.os.Handler;

import androidx.core.app.JobIntentService;

public class SubmissionScheduler {

    private Handler handler;

    SubmissionScheduler() {
        handler = new Handler();
    }

    public synchronized void scheduleSubmission() {
        if (Rad.getInstance() == null) {
            return;
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                scheduleSubmission();
                JobIntentService.enqueueWork(Rad.getInstance().getApplicationContext(), HTTPService.class, 1, new Intent());
            }
        }, Rad.getInstance().getSubmissionTimeInterval());
    }

    public synchronized void cancel() {
        handler.removeCallbacksAndMessages(null);
    }

}

