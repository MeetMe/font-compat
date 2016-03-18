package com.meetme.support.fonts.sample;

import com.meetme.support.fonts.FontManagerImpl21;
import com.meetme.support.fonts.FontManagerImpl24;

import android.app.Application;
import android.graphics.FontListParser;
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

        Log.v(TAG, "onCreate: SDK INT=" + Build.VERSION.SDK_INT + "; preview=" + Build.VERSION.PREVIEW_SDK_INT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.PREVIEW_SDK_INT > 0) {
            FontListParser.Config config = FontManagerImpl24.readFontsXml(this, R.raw.fonts);
            FontManagerImpl24.extractToCache(getAssets(), getCodeCacheDir(), config);

            Log.v(TAG, "read fonts: " + config);
            Log.v(TAG, "Aliases=" + config.aliases);
            Log.v(TAG, "Families=" + config.families);

            FontManagerImpl24.init(config);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            FontListParser.Config config = FontManagerImpl21.readFontsXml(this, R.raw.fonts);
            FontManagerImpl21.extractToCache(getAssets(), getCodeCacheDir(), config);

            Log.v(TAG, "read fonts: " + config);
            Log.v(TAG, "Aliases=" + config.aliases);
            Log.v(TAG, "Families=" + config.families);

            FontManagerImpl21.init(config);
        }
    }
}
