package com.meetme.support.fonts;

import com.meetme.support.fonts.internal.ReflectionUtils;
import com.meetme.support.fonts.util.StreamUtils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.FontFamily;
import android.graphics.FontListParser;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RawRes;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * @author jhansche
 * @since 3/15/16
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class FontManagerImpl21 implements FontManager {
    private static final String TAG = FontManagerImpl21.class.getSimpleName();
    // https://github.com/android/platform_frameworks_base/blob/android-5.0.0_r1/graphics/java/android/graphics/FontListParser.java
    // https://github.com/android/platform_frameworks_base/blob/android-5.0.0_r1/graphics/java/android/graphics/FontFamily.java
    // https://github.com/android/platform_frameworks_base/blob/android-5.0.0_r1/graphics/java/android/graphics/Typeface.java

    private static final String BOGUS_FONT_FILENAME = "fonts/Bogus.ttf";
    private static String sStripFontPrefix = null;

//    private static FontFamily makeFamilyFromParsed(FontListParser.Family family) {
//        FontFamily fontFamily = new FontFamily(family.lang, family.variant);
//        for (FontListParser.Font font : family.fonts) {
//            fontFamily.addFontWeightStyle(font.fontName, font.weight, font.isItalic);
//        }
//        return fontFamily;
//    }

    @Nullable
    public static FontListParser.Config readFontsXml(Context context, @RawRes int resId) {
        initializeTestConfig(context, R.raw.test);
        FontListParser.Config config = readFontConfig(context, resId);
        if (config != null) fixFontPaths(config);
        return config;
    }

    public static void init(FontListParser.Config config) {
        Map<String, Typeface> systemFonts = ReflectionUtils.getSystemFontsMap();

        for (int i = 0; i < config.families.size(); i++) {
            FontListParser.Family f = config.families.get(i);
            FontFamily family = makeFamilyFromParsed(f);
            Typeface typeface = ReflectionUtils.createTypefaceFromFamily(family);
            if (systemFonts != null) systemFonts.put(f.name, typeface);
        }
    }

    public static void extractToCache(@NonNull AssetManager assets, @NonNull File cacheDir, @NonNull FontListParser.Config config) {
        for (FontListParser.Family family : config.families) {
            for (FontListParser.Font font : family.fonts) {
                File target = new File(cacheDir, font.fontName);
                File parent = target.getParentFile();
                Log.v("JHH", "target path = " + target + ", exists=" + target.exists());

                if (target.exists()) {
                    Log.i(TAG, "File has already been extracted: " + target);
                } else if (!parent.exists() && !parent.mkdirs()) {
                    Log.w(TAG, "Unable to create parent path: " + parent + " for " + target);
                } else if (extractFontToCache(assets, target, font.fontName)) {
                    Log.i(TAG, "File extracted successfully: " + target);
                } else {
                    Log.w(TAG, "Unable to extract the font from cache");
                }

                font.fontName = target.getAbsolutePath();
            }
        }
    }

    private static boolean extractFontToCache(AssetManager assets, File target, String fontName) {
        InputStream stream = null;
        FileOutputStream out = null;

        try {
            stream = assets.open(fontName);
            out = new FileOutputStream(target);
            StreamUtils.copyStream(stream, out);
        } catch (IOException e) {
            Log.w(TAG, "TODO", e);
        } finally {
            StreamUtils.closeQuietly(stream);
            StreamUtils.closeQuietly(out);
        }

        return target.exists();
    }

    private static void fixFontPaths(@NonNull FontListParser.Config config) {
        for (FontListParser.Family family : config.families) {
            for (FontListParser.Font font : family.fonts) {
                if (font.fontName.contains(sStripFontPrefix)) {
                    font.fontName = font.fontName.replace(sStripFontPrefix, "");
                    Log.v("JHH", "Fixed font path: " + font);
                }
            }
        }
    }

    @Nullable
    private static FontListParser.Config readFontConfig(Context context, @RawRes int resId) {
        InputStream stream = context.getResources().openRawResource(resId);

        try {
            return FontListParser.parse(stream);
        } catch (Exception e) {
            Log.w(TAG, "TODO", e);
        }

        return null;
    }

    private static void initializeTestConfig(Context context, @RawRes int resId) {
        if (sStripFontPrefix != null) return;

        FontListParser.Config config = readFontConfig(context, resId);

        if (config != null) {
            // From res/raw/test.xml, we should expect to find a font name of "fonts/Bogus.ttf",
            // so anything else that was prepended to that needs to be stripped off.
            String fontName = config.families.get(0).fonts.get(0).fontName;
            sStripFontPrefix = fontName.replace(BOGUS_FONT_FILENAME, "");
            Log.v("JHH", "The font path prefix to be stripped off of font xml files: " + sStripFontPrefix);
        }
    }

    private static FontFamily makeFamilyFromParsed(FontListParser.Family family) {
        FontFamily fontFamily = new FontFamily(family.lang, family.variant);
        for (FontListParser.Font font : family.fonts) {
            Log.v("JHH", "makeFamilyFromParsed: " + font.fontName + ", " + font.weight + ",  " + font.isItalic);
            // TODO: we need to read each fontName out of Assets, move it into a File-accessible path, and then call this
            boolean result = fontFamily.addFontWeightStyle(font.fontName, font.weight, font.isItalic);
            Log.v("JHH", "addFontWeightStyle: result=" + result);
        }
        return fontFamily;
    }
}
