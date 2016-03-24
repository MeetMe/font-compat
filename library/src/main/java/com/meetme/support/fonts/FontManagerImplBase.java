package com.meetme.support.fonts;

import com.meetme.support.fonts.internal.FontListParser;
import com.meetme.support.fonts.internal.ReflectionUtils;
import com.meetme.support.fonts.util.StreamUtils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RawRes;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

/**
 * This is the base implementation that replaces Typeface instances via Reflection (see {@code @see} for inspiration).
 * <p/>
 * To do this, we pull out the "regular", "bold", "italic", and "bold+italic" fonts from the {@code fonts.xml} configuration. We then perform the
 * following replacements:
 * <ul>
 * <li>{@link Typeface#DEFAULT} {@code => regular}</li>
 * <li>{@link Typeface#DEFAULT_BOLD} {@code => bold}</li>
 * <li>{@link Typeface#SANS_SERIF} {@code => regular}</li>
 * </ul>
 * <p/>
 * That takes care of anyone using the {@link Typeface#DEFAULT}, et al, constants directly. Then in order to get the defaults from {@link
 * Typeface#defaultFromStyle(int)}, we also have to replace {@link Typeface#sDefaults} array (which is keyed on the {@link android.R.attr#textStyle}
 * enum ordinal, which are matched up with {@link Typeface#NORMAL}, {@link Typeface#BOLD}, {@link Typeface#ITALIC}, and {@link
 * Typeface#BOLD_ITALIC}).
 * <p/>
 * One important thing to note: this does work <u>automatically</u>, but only when {@link android.R.attr#fontFamily} is {@code @null} <b>and</b>
 * either a) {@link android.R.attr#typeface} {@code = sans}, or b) {@link android.R.attr#textStyle} {@code != normal}.
 * <p/>
 * In any other scenario (i.e., if you are setting {@code fontFamily}), you would have to use one of these from Java code:
 * <code>
 * textView.setTypeface((Typeface) null, Typeface.BOLD);
 * textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
 * </code>
 *
 * @author jhansche
 * @see <a href="http://stackoverflow.com/a/16883281/231078">Is it possible to set font for entire Application?</a>
 * @since 3/17/16
 */
class FontManagerImplBase extends FontManager {

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

        // https://github.com/android/platform_frameworks_base/blob/kitkat-release/graphics/java/android/graphics/Typeface.java
        // There is no FontFamily, FontListParser, etc...  This was pre-Minikin

        // TODO: we can set the sTypefaceCache.get(0) SparseArray (0..3 for styles) to each individual style. That will
        // work for Typeface.create((Typeface)null, int)
        // XXX: but calling Typeface.create(String, int) will always create a new instance, even with (String)null!

        // Replacing sDefaults[0..3] will work for Typeface.defaultFromStyle(int)

        // Replacing DEFAULT, DEFAULT_BOLD, etc, will work for most defaults, but not if trying to inflate from layouts

        // These replace the Typeface.<constant> instances
        Reflex.replaceFont("SANS_SERIF", regular);
        Reflex.replaceFont("DEFAULT", regular);
        Reflex.replaceFont("DEFAULT_BOLD", bold);

        // These replace the sDefaults default typefaces for normal,bold,italic,bold+italic
        Reflex.setDefaultsOverride(regular, bold, italic, boldItalic);

        // XXX: using this only works when fontFamily is @null, and (typeface=sans OR textStyle != normal).

        return true;
    }

    protected Typeface loadFont(AssetManager assets, String fontName) {
        return Typeface.createFromAsset(assets, fontName);
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

    private static class Reflex {
        private static Field Typeface_sDefaults;

        static {
            try {
                Typeface_sDefaults = Typeface.class.getDeclaredField("sDefaults");
                Typeface_sDefaults.setAccessible(true);
            } catch (NoSuchFieldException e) {
                Log.w(TAG, "TODO", e);
            }
        }

        public static void replaceFont(String staticTypefaceFieldName, Typeface newTypeface) {
            try {
                final Field staticField = Typeface.class.getDeclaredField(staticTypefaceFieldName);
                staticField.setAccessible(true);

                Object existing = staticField.get(null);

                if (existing instanceof Typeface) {
                    Log.v(TAG, "Saving reference to the old font: " + staticTypefaceFieldName + " ~> " + existing);
                    ReflectionUtils.onReplaced((Typeface) existing, newTypeface);
                }

                staticField.set(null, newTypeface);
            } catch (NoSuchFieldException e) {
                Log.e(TAG, "Failed replacing font " + staticTypefaceFieldName + " with " + newTypeface, e);
            } catch (IllegalAccessException e) {
                Log.e(TAG, "Failed replacing font " + staticTypefaceFieldName + " with " + newTypeface, e);
            }
        }

        public static void setDefaultsOverride(Typeface... overrides) {
            if (Typeface_sDefaults == null) {
                Log.w(TAG, "Unable to override sDefaults!");
                return;
            }

            try {
                // Get the current defaults so we can replace the overrides inline, directly into the existing array.
                Typeface[] currentDefaults = (Typeface[]) Typeface_sDefaults.get(null);

                if (currentDefaults != null) {
                    for (int i = 0; i < currentDefaults.length && i < overrides.length; i++) {
                        Log.v(TAG, "Saving reference to the old font: sDefaults[" + i + "] ~> " + currentDefaults[i]);
                        // hold references to the existing defaults so that they don't get mistakenly GC'ed
                        ReflectionUtils.onReplaced(currentDefaults[i], overrides[i]);
                        currentDefaults[i] = overrides[i];
                    }

                    Log.v(TAG, "Replaced sDefaults with overrides: " + TextUtils.join(", ", currentDefaults));
                } else {
                    Log.w(TAG, "sDefaults is not yet initialized...");
                }
            } catch (Exception e) {
                Log.w(TAG, "Failed replacing sDefaults", e);
            }
        }
    }
}
