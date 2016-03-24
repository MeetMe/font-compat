package com.meetme.support.fonts.sample;

import com.meetme.support.fonts.FontManager;

import android.app.Application;
import android.os.Build;
import android.util.Log;

/**
 * @author jhansche
 * @since 3/15/16
 */
public class MyApplication extends Application {
    private static final String TAG = "FontsApp";

    @Override
    public void onCreate() {
        super.onCreate();

        Log.v(TAG, "onCreate: SDK INT=" + Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) Log.v(TAG, "onCreate: PREVIEW_SDK_INT=" + Build.VERSION.PREVIEW_SDK_INT);

        boolean installed = FontManager.install(this, R.raw.fonts);
        Log.v(TAG, "FontManager.install=" + installed);
    }
}
