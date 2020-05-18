package com.eliorcohen12345.locationproject.MainAndOtherPackage;

import android.app.Application;
import android.content.Context;

// Summary of getContext
public class ConApp extends Application {

    private static Application application;

    @Override
    public void onCreate() {
        super.onCreate();

        application = this;
    }

    public static Application getApplication() {
        return application;
    }

}
