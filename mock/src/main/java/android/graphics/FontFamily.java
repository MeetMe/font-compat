
package android.graphics;

import android.annotation.TargetApi;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * A family of typefaces with different styles.
 * <p/>
 * This is here just so we can use it as a {@code provided} dependency, so we
 * can reference the classes strongly, instead of accessing them via reflection.
 *
 * @hide
 */
public class FontFamily {
    /**
     * @hide
     */
    public long mNativePtr;

    public FontFamily() {
        throw new RuntimeException();
    }

    public FontFamily(String lang, String variant) {
        throw new RuntimeException();
    }

    @Override
    protected void finalize() throws Throwable {
        throw new RuntimeException();
    }

    /** use {@link #addFont(String, int)} on API level 24+. */
    public boolean addFont(String path) {
        throw new RuntimeException();
    }

    /** use {@link #addFontWeightStyle(ByteBuffer, int, List, int, boolean)} on API level 24+. */
    public boolean addFontWeightStyle(String path, int weight, boolean style) {
        throw new RuntimeException();
    }

    /**
     * <pre>addFontWeightStyle(ByteBuffer, int ttcIndex, List&lt;axes&gt;, int weight, boolean isItalic)</pre>
     *
     * @since API Level 24
     */
    @TargetApi(24)
    public boolean addFontWeightStyle(ByteBuffer buffer, int ttcIndex, List axes, int weight, boolean isItalic) {
        // Acceptable:  ttcIndex==0, axes==emptyList
        throw new RuntimeException();
    }

    /** @since API Level 24 */
    @TargetApi(24)
    public boolean addFont(String s1, int i1) {
        throw new RuntimeException();
    }

//    public boolean addFontFromAsset(AssetManager mgr, String path) {
//        throw new RuntimeException();
//    }
}