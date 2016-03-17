
package android.graphics;

/**
 * A family of typefaces with different styles.
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

    public boolean addFont(String path) {
        throw new RuntimeException();
    }

    public boolean addFontWeightStyle(String path, int weight, boolean style) {
        throw new RuntimeException();
    }

//    public boolean addFontFromAsset(AssetManager mgr, String path) {
//        throw new RuntimeException();
//    }
}