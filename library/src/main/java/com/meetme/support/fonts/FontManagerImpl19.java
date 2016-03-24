package com.meetme.support.fonts;

import android.annotation.TargetApi;
import android.os.Build;

/**
 * @author jhansche
 * @since 3/17/16
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
class FontManagerImpl19 extends FontManagerImplBase {
    // https://github.com/android/platform_frameworks_base/blob/kitkat-release/graphics/java/android/graphics/Typeface.java
    // There is no FontFamily, FontListParser, etc...  This was pre-Minikin

    // TODO: we can set the sTypefaceCache.get(0) SparseArray (0..3 for styles) to each individual style. That will
    // work for Typeface.create((Typeface)null, int)
    // XXX: but calling Typeface.create(String, int) will always create a new instance, even with (String)null!

    // Replacing sDefaults[0..3] will work for Typeface.defaultFromStyle(int)

    // Replacing DEFAULT, DEFAULT_BOLD, etc, will work for most defaults, but not if trying to inflate from layouts

    // TODO: anything special for kitkat that we need to do in addition to the Base impl?
}
