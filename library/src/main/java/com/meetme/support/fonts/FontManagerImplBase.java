package com.meetme.support.fonts;

import com.meetme.support.fonts.internal.FontListParser;
import com.meetme.support.fonts.util.StreamUtils;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RawRes;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author jhansche
 * @since 3/17/16
 */
public class FontManagerImplBase extends FontManager {
    @Override
    boolean init(@NonNull Context context, @RawRes int fontsRes) {
        return false;
    }

    @Nullable
    public final FontListParser.Config readFontConfig(@NonNull Context context, @RawRes int resId) {
        InputStream stream = context.getResources().openRawResource(resId);

        try {
            return FontListParser.parse(stream);
        } catch (Exception e) {
            Log.w(TAG, "TODO", e);
        }

        return null;
    }

    protected void extractToCache(@NonNull AssetManager assets, @NonNull File cacheDir, @NonNull FontListParser.Config config) {
        for (FontListParser.Family family : config.families) {
            for (FontListParser.Font font : family.fonts) {
                File target = new File(cacheDir, font.fontName);
                File parent = target.getParentFile();
                Log.v(TAG, "target path = " + target + ", exists=" + target.exists());

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

    protected boolean extractFontToCache(AssetManager assets, File target, String fontName) {
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
    // https://github.com/android/platform_frameworks_base/blob/kitkat-release/graphics/java/android/graphics/Typeface.java
    // There is no FontFamily, FontListParser, etc...  This was pre-Minikin

    // TODO: we can set the sTypefaceCache.get(0) SparseArray (0..3 for styles) to each individual style. That will
    // work for Typeface.create((Typeface)null, int)
    // XXX: but calling Typeface.create(String, int) will always create a new instance, even with (String)null!

    // Replacing sDefaults[0..3] will work for Typeface.defaultFromStyle(int)

    // Replacing DEFAULT, DEFAULT_BOLD, etc, will work for most defaults, but not if trying to inflate from layouts
}
