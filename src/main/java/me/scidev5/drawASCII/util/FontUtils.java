package me.scidev5.drawASCII.util;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

public class FontUtils {
    private static final HashMap<String, Font> loadedFonts;

    static {
        loadedFonts = new HashMap<>();
    }

    /**
     * Load a font into the utility.
     * @param id The text id to load as.
     * @param fontType The font type, use <code>Font.(...);</code>, ttf -> <code>Font.TRUETYPE_FONT;</code>
     * @param resourceLocation A URL describing the location of the font file.
     * @throws IOException If there is a problem reading the font file.
     * @throws FontFormatException If the fontType provided does not match the file given.
     */
    public static void loadFont(String id, int fontType, URL resourceLocation) throws IOException, FontFormatException {
        Font font = Font.createFont(fontType,resourceLocation.openStream());
        loadedFonts.put(id,font);
    }

    /**
     * Derive a font's size by id.
     * @param id The id of the font to derive.
     * @param size The font size to derive.
     * @return The id of the derived font. (<id in>:<font size with 2 decimal points>px)
     */
    public static String deriveFont(String id, float size) {
        Font rawFont = loadedFonts.get(id);
        if (rawFont == null) return null;
        Font derivedFont = rawFont.deriveFont(size);
        String newId = String.format("%s:%.2fpx",id,size);
        loadedFonts.put(newId,derivedFont);
        return newId;
    }

    /**
     * Get a loaded font by id.
     * @param id The id of the font to load.
     * @return The font object or null if the font was not loaded.
     */
    public static Font getFont(String id) {
        return loadedFonts.get(id);
    }

}
