package com.aiddtg;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;

public class EtasManager extends ReactContextBaseJavaModule {
    private static ReactApplicationContext reactContext;
    private Activity activity;

    EtasManager(ReactApplicationContext context) {
        super(context);
        reactContext = context;
    }

    @NonNull
    @Override
    public String getName() {
        return "EtasManager";
    }
}
