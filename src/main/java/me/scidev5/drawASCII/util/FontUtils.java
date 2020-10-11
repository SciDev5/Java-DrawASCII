package me.scidev5.drawASCII.util;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

public class FontUtils {
    private static HashMap<String, Font> loadedFonts;

    static {
        loadedFonts = new HashMap<>();
    }

    public static void loadFont(String id, int fontType, URL resourceLocation) throws IOException, FontFormatException {
        Font font = Font.createFont(fontType,resourceLocation.openStream());
        if (font != null)
            loadedFonts.put(id,font);
    }

    public static Font deriveFont(String id, float size) {
        Font rawFont = loadedFonts.get(id);
        if (rawFont == null) return null;
        Font derivedFont = rawFont.deriveFont(size);
        loadedFonts.put(String.format("%s:%.2fpx",id,size),derivedFont);
        return derivedFont;
    }
    public static Font getFont(String id) {
        return loadedFonts.get(id);
    }

}
