package android.graphics;

import android.annotation.TargetApi;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class FontListParser {
    public static class Config {
        Config() {
        }

        public List<Family> families;
        public List<Alias> aliases;
    }

    @TargetApi(24)
    public static class Axis {
        public int tag;
        public float styleValue;

        public Axis(int tag, float styleValue) {
        }
    }

    public static class Font {
        Font(String fontName, int weight, boolean isItalic) {
        }

        public String fontName;
        public int weight;
        public boolean isItalic;

        /** @since API Level 24 */
        @TargetApi(24)
        Font(String fontName, int ttcIndex, List<Axis> axes, int weight, boolean isItalic) {

        }

        // @TargetApi(24)
        public int ttcIndex;
        // @TargetApi(24)
        public List<Axis> axes;
    }

    public static class Alias {
        public String name;
        public String toName;
        public int weight;
    }

    public static class Family {
        public Family(String name, List<Font> fonts, String lang, String variant) {
        }

        public String name;
        public List<Font> fonts;
        public String lang;
        public String variant;
    }

    /* Parse fallback list (no names) */
    public static Config parse(InputStream in) throws IOException {
        throw new RuntimeException();
    }
}
