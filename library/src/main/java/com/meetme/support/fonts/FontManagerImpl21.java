package com.meetme.support.fonts;

import com.meetme.support.fonts.internal.FontListParser;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.FontFamily;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jhansche
 * @since 3/15/16
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class FontManagerImpl21 extends FontManagerImpl19 {
    private static final String TAG = FontManagerImpl21.class.getSimpleName();
    // https://github.com/android/platform_frameworks_base/blob/android-5.0.0_r1/graphics/java/android/graphics/FontListParser.java
    // https://github.com/android/platform_frameworks_base/blob/android-5.0.0_r1/graphics/java/android/graphics/FontFamily.java
    // https://github.com/android/platform_frameworks_base/blob/android-5.0.0_r1/graphics/java/android/graphics/Typeface.java

    @Override
    boolean init(@NonNull Context context, int fontsRes) {
        FontListParser.Config config = readFontConfig(context, fontsRes);
        if (config == null) return false;

        // this will also rewrite the Font#fontName to point to the extracted file(s)
        extractToCache(context.getAssets(), context.getCodeCacheDir(), config);

        if (BuildConfig.DEBUG) {
            Log.v(TAG, "read fonts: " + config);
            Log.v(TAG, "Aliases=" + config.aliases);
            Log.v(TAG, "Families=" + config.families);
        }

        init(config);
        return true;
    }

    void init(@NonNull FontListParser.Config config) {
        Map<String, Typeface> systemFonts = FontManagerImpl21.getSystemFontsMap();

        for (FontListParser.Family f : config.families) {
            try {
                FontFamily family = makeFamilyFromParsed(f);
                Typeface typeface = FontManagerImpl21.createTypefaceFromFamily(family);
                if (systemFonts != null && typeface != null) systemFonts.put(f.name, typeface);
            } catch (Exception e) {
                Log.e(TAG, "Failed to create Typeface from FontFamily", e);
            }
        }
    }

    protected FontFamily makeFamilyFromParsed(FontListParser.Family family) throws NoSuchMethodError {
        FontFamily fontFamily = new FontFamily(family.lang, family.variant);

        for (FontListParser.Font font : family.fonts) {
            if (BuildConfig.DEBUG) Log.v(TAG, "makeFamilyFromParsed: " + font.fontName + ", " + font.weight + ",  " + font.isItalic);

            boolean result = fontFamily.addFontWeightStyle(font.fontName, font.weight, font.isItalic);

            if (BuildConfig.DEBUG) Log.v(TAG, "addFontWeightStyle: result=" + result);
        }

        return fontFamily;
    }

    /** A reference to the {@code @hide} method {@link Typeface#createFromFamiles(FontFamily[])} */
    @Nullable
    private static Method Typeface_createFromFamilies;

    static {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) throw new RuntimeException();

        try {
            Typeface_createFromFamilies = Typeface.class.getMethod("createFromFamilies", FontFamily[].class);
            Typeface_createFromFamilies.setAccessible(true);
        } catch (NoSuchMethodException e) {
            // Could be a custom ROM, or a manufacturer fiddled with it?
            Log.w(TAG, "Unable to obtain Typeface.createFromFamilies(FontFamily[]) method", e);
        }
    }

    @Nullable
    private static Typeface createTypefaceFromFamily(FontFamily family) {
        if (Typeface_createFromFamilies != null) {
            try {
                return (Typeface) Typeface_createFromFamilies.invoke(null, new Object[] { new FontFamily[] { family } });
            } catch (ReflectiveOperationException e) {
                Log.w(TAG, "Failed to call createFromFamilies", e);
            }
        }

        return null;
    }

    @Nullable
    private static Map<String, Typeface> getSystemFontsMap() {
        try {
            final Field sysFontMap = Typeface.class.getDeclaredField("sSystemFontMap");
            sysFontMap.setAccessible(true);
            @SuppressWarnings("unchecked") Map<String, Typeface> map = (Map<String, Typeface>) sysFontMap.get(null);

            if (map == null) {
                map = new HashMap<>();
                sysFontMap.set(null, map);
            }

            return map;
        } catch (ReflectiveOperationException nsfe) {
            Log.w(TAG, "Unable to fetch sSystemFontMap", nsfe);
        }

        return null;
    }
}
