package com.meetme.support.fonts;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.RawRes;

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
        // TODO: is there anything v19+ that has to happen here instead of in base?
        return super.init(context, fontsRes);
    }

    // XXX: using this only works when fontFamily is @null, and (typeface=sans OR textStyle != normal).
}
