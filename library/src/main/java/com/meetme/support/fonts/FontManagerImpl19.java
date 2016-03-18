package com.meetme.support.fonts;

import com.meetme.support.fonts.internal.FontListParser;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.RawRes;
import android.util.Log;

/**
 * @author jhansche
 * @since 3/17/16
 */
public class FontManagerImpl19 extends FontManagerImplBase {
    // https://github.com/android/platform_frameworks_base/blob/kitkat-release/graphics/java/android/graphics/Typeface.java
    // There is no FontFamily, FontListParser, etc...  This was pre-Minikin

    // TODO: we can set the sTypefaceCache.get(0) SparseArray (0..3 for styles) to each individual style. That will
    // work for Typeface.create((Typeface)null, int)
    // XXX: but calling Typeface.create(String, int) will always create a new instance, even with (String)null!

    // Replacing sDefaults[0..3] will work for Typeface.defaultFromStyle(int)

    // Replacing DEFAULT, DEFAULT_BOLD, etc, will work for most defaults, but not if trying to inflate from layouts

    @Override
    boolean init(@NonNull Context context, @RawRes int fontsRes) {
        FontListParser.Config config = readFontConfig(context, fontsRes);
        if (config == null) return false;

        final AssetManager assets = context.getAssets();

        Typeface regular = null;
        Typeface bold = null;
        Typeface italic = null;
        Typeface boldItalic = null;

        // now loop through the config and pull out the fonts we're interested in
        for (FontListParser.Family family : config.families) {
            if (family.fonts != null) {
                for (FontListParser.Font font : family.fonts) {
                    if (font.weight == 400) {
                        if (font.isItalic) {
                            italic = loadFont(assets, font.fontName);
                        } else {
                            regular = loadFont(assets, font.fontName);
                        }
                    } else if (font.weight >= 600) {
                        if (font.isItalic) {
                            boldItalic = loadFont(assets, font.fontName);
                        } else {
                            bold = loadFont(assets, font.fontName);
                        }
                    }
                }
            }
        }

        Log.v(TAG, "Parsed fonts: regular=" + regular + ", italic=" + italic + ", bold=" + bold + ", boldItalic=" + boldItalic);

        // TODO Set sDefaults, set constants, add to sTypefaceCache ?

        return false;
    }

    private Typeface loadFont(AssetManager assets, String fontName) {
        return Typeface.createFromAsset(assets, fontName);
    }
}
