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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jhansche
 * @since 3/15/16
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class FontManagerImpl21 extends FontManagerImpl19 {
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

        Log.v(TAG, "read fonts: " + config);
        Log.v(TAG, "Aliases=" + config.aliases);
        Log.v(TAG, "Families=" + config.families);

        init(config);
        return true;
    }

    void init(@NonNull FontListParser.Config config) {
        Map<String, Typeface> systemFonts = Reflex.getSystemFontsMap();

        for (int i = 0; i < config.families.size(); i++) {
            FontListParser.Family f = config.families.get(i);

            try {
                FontFamily family = makeFamilyFromParsed(f);
                Typeface typeface = Reflex.createTypefaceFromFamily(family);
                if (systemFonts != null) systemFonts.put(f.name, typeface);
            } catch (Exception e) {
                Log.e(TAG, "TODO", e);
            }
        }
    }

    protected FontFamily makeFamilyFromParsed(FontListParser.Family family) throws NoSuchMethodError {
        FontFamily fontFamily = new FontFamily(family.lang, family.variant);
        for (FontListParser.Font font : family.fonts) {
            Log.v(TAG, "makeFamilyFromParsed: " + font.fontName + ", " + font.weight + ",  " + font.isItalic);
            boolean result = fontFamily.addFontWeightStyle(font.fontName, font.weight, font.isItalic);
            Log.v(TAG, "addFontWeightStyle: result=" + result);
        }
        return fontFamily;
    }

    private static class Reflex {
        /** A reference to the {@code @hide} method {@link Typeface#createFromFamiles(FontFamily[])} */
        public static Method Typeface_createFromFamilies;

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
    }
}
