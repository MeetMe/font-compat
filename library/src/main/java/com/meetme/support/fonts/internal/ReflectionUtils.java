package com.meetme.support.fonts.internal;

import android.graphics.Typeface;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author jhansche
 * @since 3/15/16
 */
@SuppressWarnings("TryWithIdenticalCatches")
public class ReflectionUtils {
    private static final String TAG = "FontManagerReflection";


    /* One theory behind native crashes with a replaced font is that the original fonts may be getting GC'ed after their references get replaced.
     * So this is here to hold onto the original Typeface instances, to prevent them from being collected.
     */
    private static final Set<Typeface> sBackupDefaults = new LinkedHashSet<>();

    /** Returns {@code true} if the supplied {@link Typeface} is one of those that has been replaced by an override. */
    public static boolean wasReplaced(Typeface tf) {
        return tf != null && sBackupDefaults.contains(tf);
    }

    public static void onReplaced(Typeface old, Typeface replacement) {
        sBackupDefaults.add(old);
    }
}
