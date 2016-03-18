package com.meetme.support.fonts.internal;

import android.annotation.TargetApi;
import android.graphics.FontFamily;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jhansche
 * @since 3/15/16
 */
@SuppressWarnings("TryWithIdenticalCatches")
public class ReflectionUtils {
    private static final String TAG = ReflectionUtils.class.getSimpleName();

    /** A reference to the {@code @hide} method {@link Typeface#createFromFamiles(FontFamily[])} */
    private static final Method Typeface_createFromFamilies;

    static {
        try {
            Typeface_createFromFamilies = Typeface.class.getMethod("createFromFamilies", FontFamily[].class);
        } catch (NoSuchMethodException e) {
            Log.w(TAG, "TODO", e);
            throw new RuntimeException(e);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    public static Map<String, Typeface> getSystemFontsMap() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return null;

        try {
            final Field sysFontMap = Typeface.class.getDeclaredField("sSystemFontMap");
            sysFontMap.setAccessible(true);
            @SuppressWarnings("unchecked") Map<String, Typeface> map = (Map<String, Typeface>) sysFontMap.get(null);

            if (map == null) {
                map = new HashMap<>();
                sysFontMap.set(null, map);
            }

            return map;
        } catch (NoSuchFieldException nsfe) {
            Log.w(TAG, "Unable to fetch sSystemFontMap", nsfe);
        } catch (IllegalAccessException iae) {
            Log.w(TAG, "Unable to fetch sSystemFontMap", iae);
        }

        return null;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static Typeface createTypefaceFromFamily(FontFamily family) {
        try {
            return (Typeface) Typeface_createFromFamilies.invoke(null, new Object[] { new FontFamily[] { family } });
        } catch (IllegalAccessException e) {
            Log.w(TAG, "TODO", e);
        } catch (InvocationTargetException e) {
            Log.w(TAG, "TODO", e);
        }

        return null;
    }
}
