package org.npr.demo;

import android.app.Application;

import com.npr.rad.Rad;

public class DemoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Rad.with(this);
        Rad.setDebugEnabled(true);
    }
}
