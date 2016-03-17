package com.meetme.support.fonts.sample;

import com.meetme.support.fonts.FontManagerImpl21;

import android.app.Application;
import android.graphics.FontListParser;
import android.os.Build;
import android.util.Log;

/**
 * @author jhansche
 * @since 3/15/16
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Log.v("JHH", "onCreate");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            FontListParser.Config config = FontManagerImpl21.readFontsXml(this, R.raw.fonts);
            FontManagerImpl21.extractToCache(getAssets(), getCodeCacheDir(), config);

            Log.v("JHH", "read fonts: " + config);
            Log.v("JHH", "Aliases=" + config.aliases);
            Log.v("JHH", "Families=" + config.families);

            FontManagerImpl21.init(config);
        }
    }
}
