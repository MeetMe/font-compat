package com.meetme.support.fonts;

import com.meetme.support.fonts.internal.FontListParser;
import com.meetme.support.fonts.internal.ReflectionUtils;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.FontFamily;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;

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
        Map<String, Typeface> systemFonts = ReflectionUtils.getSystemFontsMap();

        for (int i = 0; i < config.families.size(); i++) {
            FontListParser.Family f = config.families.get(i);

            try {
                FontFamily family = makeFamilyFromParsed(f);
                Typeface typeface = ReflectionUtils.createTypefaceFromFamily(family);
                if (systemFonts != null) systemFonts.put(f.name, typeface);
            } catch (Exception e) {
                Log.e(TAG, "TODO", e);
            }
        }
    }

    private FontFamily makeFamilyFromParsed(FontListParser.Family family) throws NoSuchMethodError {
        FontFamily fontFamily = new FontFamily(family.lang, family.variant);
        for (FontListParser.Font font : family.fonts) {
            Log.v(TAG, "makeFamilyFromParsed: " + font.fontName + ", " + font.weight + ",  " + font.isItalic);
            boolean result = fontFamily.addFontWeightStyle(font.fontName, font.weight, font.isItalic);
            Log.v(TAG, "addFontWeightStyle: result=" + result);
        }
        return fontFamily;
    }
}
