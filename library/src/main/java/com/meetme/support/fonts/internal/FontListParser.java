package com.meetme.support.fonts.internal;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Parser for font config files.
 * <p/>
 * This class is copied from <a
 * href="https://github.com/android/platform_frameworks_base/blob/master/graphics/java/android/graphics/FontListParser.java">
 * platform_frameworks_base/+/master/graphics/java/android/graphics/FontListParser.java</a>
 */
public class FontListParser {
    public static class Config {
        Config() {
            families = new ArrayList<>();
            aliases = new ArrayList<>();
        }

        public List<Family> families;
        public List<Alias> aliases;

        @Override
        public String toString() {
            return "FontListParser{aliases=" + aliases + ", families=" + families + "}";
        }
    }

    public static class Font {
        Font(String fontName, int weight, boolean isItalic) {
            this.fontName = fontName;
            this.weight = weight;
            this.isItalic = isItalic;
        }

        public String fontName;
        public int weight;
        public boolean isItalic;

        @Override
        public String toString() {
            return "Font[" + fontName + ":" + weight + "/" + isItalic + "]";
        }
    }

    public static class Alias {
        public String name;
        public String toName;
        public int weight;

        @Override
        public String toString() {
            return "Alias[" + name + "=>" + toName + "]";
        }
    }

    public static class Family {
        public Family(String name, List<Font> fonts, String lang, String variant) {
            this.name = name;
            this.fonts = fonts;
            this.lang = lang;
            this.variant = variant;
        }

        public String name;
        public List<Font> fonts;
        public String lang;
        public String variant;

        @Override
        public String toString() {
            return "Family{name=" + name + ", fonts=" + fonts + "}";
        }
    }

    /* Parse fallback list (no names) */
    public static Config parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(in, null);
            parser.nextTag();
            return readFamilies(parser);
        } finally {
            in.close();
        }
    }

    private static Config readFamilies(XmlPullParser parser)
            throws XmlPullParserException, IOException {
        Config config = new Config();
        parser.require(XmlPullParser.START_TAG, null, "familyset");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) continue;
            if (parser.getName().equals("family")) {
                config.families.add(readFamily(parser));
            } else if (parser.getName().equals("alias")) {
                config.aliases.add(readAlias(parser));
            } else {
                skip(parser);
            }
        }
        return config;
    }

    private static Family readFamily(XmlPullParser parser)
            throws XmlPullParserException, IOException {
        String name = parser.getAttributeValue(null, "name");
        String lang = parser.getAttributeValue(null, "lang");
        String variant = parser.getAttributeValue(null, "variant");
        List<Font> fonts = new ArrayList<>();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) continue;
            String tag = parser.getName();
            if (tag.equals("font")) {
                String weightStr = parser.getAttributeValue(null, "weight");
                int weight = weightStr == null ? 400 : Integer.parseInt(weightStr);
                boolean isItalic = "italic".equals(parser.getAttributeValue(null, "style"));
                String filename = parser.nextText();
                fonts.add(new Font(filename, weight, isItalic));
            } else {
                skip(parser);
            }
        }
        return new Family(name, fonts, lang, variant);
    }

    private static Alias readAlias(XmlPullParser parser)
            throws XmlPullParserException, IOException {
        Alias alias = new Alias();
        alias.name = parser.getAttributeValue(null, "name");
        alias.toName = parser.getAttributeValue(null, "to");
        String weightStr = parser.getAttributeValue(null, "weight");
        if (weightStr == null) {
            alias.weight = 400;
        } else {
            alias.weight = Integer.parseInt(weightStr);
        }
        skip(parser);  // alias tag is empty, ignore any contents and consume end tag
        return alias;
    }

    private static void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        int depth = 1;
        while (depth > 0) {
            switch (parser.next()) {
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
            }
        }
    }
}