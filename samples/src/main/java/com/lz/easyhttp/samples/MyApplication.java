package com.lz.easyhttp.samples;

import android.app.Application;

import com.lz.easyhttp.request.Easy;

/**
 *
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Easy.init(getApplicationContext());

    }
}
