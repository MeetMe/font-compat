package com.meetme.support.fonts;

import com.meetme.support.fonts.internal.FontListParser;

import android.annotation.TargetApi;
import android.graphics.FontFamily;
import android.util.Log;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Collections;

/**
 * @author jhansche
 * @since 3/18/16
 */
@TargetApi(24)
class FontManagerImpl24 extends FontManagerImpl21 implements FontManager.FontManagerImpl {
    private static final String TAG = FontManagerImpl24.class.getSimpleName();

    @Override
    protected FontFamily makeFamilyFromParsed(FontListParser.Family family) throws NoSuchMethodError {
        final FontFamily fontFamily = new FontFamily(family.lang, family.variant);
        final int ttcIndex = 0;

        for (FontListParser.Font font : family.fonts) {
            if (BuildConfig.DEBUG) Log.v(TAG, "makeFamilyFromParsed: " + font.fontName + ", " + font.weight + ",  " + font.isItalic);

            try {
                FileInputStream fis = new FileInputStream(font.fontName);
                FileChannel channel = fis.getChannel();
                ByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
                boolean result = fontFamily.addFontWeightStyle(buffer, ttcIndex, Collections.EMPTY_LIST, font.weight, font.isItalic);
                if (BuildConfig.DEBUG) Log.v(TAG, "addFontWeightStyle: result=" + result);
            } catch (IOException e) {
                Log.w(TAG, "Failed to make FontFamily", e);
            }
        }

        return fontFamily;
    }
}
