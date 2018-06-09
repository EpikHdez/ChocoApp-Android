package com.example.ximena.nomnom;

import android.app.Application;

import com.cloudinary.android.MediaManager;
import com.example.ximena.nomnom.services.HerokuService;

/**
 * Created by erickhdez on 27/3/18.
 */

public class MyCustomApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        MediaManager.init(getApplicationContext());
        HerokuService.init(getApplicationContext());
    }
}
