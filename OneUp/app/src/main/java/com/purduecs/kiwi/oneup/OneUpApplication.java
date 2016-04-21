package com.purduecs.kiwi.oneup;

import android.app.Application;
import android.content.Context;

/**
 * Main Application class for OneUp!
 */
public class OneUpApplication extends Application {

    public static String USER_ID;

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        OneUpApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return OneUpApplication.context;
    }
}
