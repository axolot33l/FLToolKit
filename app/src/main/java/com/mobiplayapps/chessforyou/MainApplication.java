package com.mobiplayapps.chessforyou;

import android.app.Application;

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ErrorHandler.init(this);
    }
}
