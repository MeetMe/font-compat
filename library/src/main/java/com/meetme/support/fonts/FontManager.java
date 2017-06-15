package com.meetme.support.fonts;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.RawRes;
import android.util.Log;

/**
 * This is the entry point for the Font compatibility support library
 * <p/>
 * First create a {@code fonts.xml} file in your project's `raw` resources folder
 * (i.e., {@code src/main/res/raw-v21/fonts.xml}):
 * <pre>{@literal
 * <familyset>
 * <!-- by using a custom font family name here, we can leverage the system's built-in fontFamily="lato" attribute to access these fonts -->
 * <family name="lato">
 * <font style="normal" weight="300">fonts/Lato-Light.ttf</font>
 * <font style="normal" weight="400">fonts/Lato-Regular.ttf</font>
 * <font style="italic" weight="400">fonts/Lato-Italic.ttf</font>
 * <font style="normal" weight="500">fonts/Lato-Medium.ttf</font>
 * <font style="normal" weight="700">fonts/Lato-Bold.ttf</font>
 * <font style="italic" weight="700">fonts/Lato-BoldItalic.ttf</font>
 * </family>
 * }
 * </pre>
 * <p/>
 * Then initialize the manager during your Application's {@link Application#onCreate()}
 * method:
 * <pre>
 * boolean installed = FontManager.install(this, R.raw.fonts);
 * </pre>
 *
 * @author jhansche
 * @since 3/15/16
 */
@Keep
public final class FontManager {
    private static final String TAG = FontManager.class.getSimpleName();

    private static final FontManagerImpl IMPL;

    static {
        if (Build.VERSION.SDK_INT >= 24) {
            if (BuildConfig.DEBUG) Log.v(TAG, "Creating FontManager-24");
            IMPL = new FontManagerImpl24();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (BuildConfig.DEBUG) Log.v(TAG, "Creating FontManager-21");
            IMPL = new FontManagerImpl21();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (BuildConfig.DEBUG) Log.v(TAG, "Creating FontManager-Base");
            // Note: this is the same as the pre-KitKat implementation, but a stronger warning is issued pre-KitKat
            IMPL = new FontManagerImplBase();
        } else {
            if (BuildConfig.DEBUG) Log.w(TAG, "Creating FontManager-Base; WARNING: this is untested");
            IMPL = new FontManagerImplBase();
        }
    }

    @Keep
    public static boolean install(Context context, @RawRes int fontsRes) {
        return IMPL.init(context, fontsRes);
    }

    private FontManager() {
        // Not instantiated
    }

    interface FontManagerImpl {
        boolean init(@NonNull Context context, @RawRes int fontsRes);
    }
}
