package com.meetme.support.fonts.util;

import android.support.annotation.Nullable;
import android.util.Log;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author jhansche
 * @since 3/17/16
 */
public class StreamUtils {
    private static final String TAG = StreamUtils.class.getSimpleName();

    public static void copyStream(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;

        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    public static void closeQuietly(@Nullable Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException e) {
                Log.w(TAG, "TODO", e);
            }
        }
    }
}
