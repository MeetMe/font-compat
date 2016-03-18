package com.meetme.support.fonts;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RawRes;
import android.util.Log;

/**
 * @author jhansche
 * @since 3/15/16
 */
public abstract class FontManager {
    protected static final String TAG = FontManager.class.getSimpleName();
    private static final FontManager IMPL;

    static {
        // TODO: SDK_INT == VERSION_CODES.N
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M && Build.VERSION.PREVIEW_SDK_INT > 0) {
            Log.v("JHH", "Creating FontManager-24");
            // This is Android N Preview
            IMPL = new FontManagerImpl24();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.v("JHH", "Creating FontManager-21");
            IMPL = new FontManagerImpl21();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Log.v("JHH", "Creating FontManager-19");
            IMPL = new FontManagerImpl19();
        } else {
            Log.v("JHH", "Creating FontManager-Base");
            IMPL = new FontManagerImplBase();
        }
    }

    abstract boolean init(@NonNull Context context, @RawRes int fontsRes);

    public static boolean install(Context context, @RawRes int fontsRes) {
        return IMPL.init(context, fontsRes);
    }
}
