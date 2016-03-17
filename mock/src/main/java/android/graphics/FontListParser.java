package android.graphics;

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

    public static class Font {
        Font(String fontName, int weight, boolean isItalic) {
        }

        public String fontName;
        public int weight;
        public boolean isItalic;
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
