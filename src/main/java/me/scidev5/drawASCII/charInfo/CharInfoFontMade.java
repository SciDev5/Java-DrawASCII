package me.scidev5.drawASCII.charInfo;

import me.scidev5.drawASCII.util.ImageUtils;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class CharInfoFontMade implements CharInfo {

    private final char character;
    private final float[][] bitmap;
    private float[][] cache;
    private float scale;
    private float density;
    private CharInfoFontMade(float[][] bitmap, char charIn) {
        this.character = charIn;
        this.bitmap = bitmap;
    }


    /**
     * Get the text character associated with this <code>CharInfo</code>.
     * @return The character for this <code>CharInfo</code>
     */
    @Override
    public char getChar() {
        return this.character;
    }

    /**
     * Test this <code>CharInfo</code> against a section of an image.
     * @param map The section of the image to compare with.
     * @return A score of how badly the image matches (0: perfect match; 1+: bad match)
     */
    @Override
    public float test(double[][] map) {
        if (map.length == 0) return 0f;
        if (cachedWidth == 0 || cachedHeight == 0) this.cache(map.length, map[0].length);
        int amount = 0; float sum = 0;
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                sum += Math.abs(map[i][j] - this.cache[i][j]);
                amount++;
            }
        }
        return amount > 0 ? sum/amount : Float.POSITIVE_INFINITY;
    }


    private float getRectSum(float x, float y, float w, float h) {
        int width = bitmap.length; int height = bitmap[0].length;
        x *= width; w *= width;
        y *= height; h *= height;
        float sum = 0; int amount = 0;
        for (int i = (int)Math.floor(x); i < width && i < x+w; i++)
            for (int j = (int)Math.floor(y); j < height && j < y+h; j++) {
                sum += bitmap[i][j];
                amount++;
            }
        return sum/amount;
    }

    private int cachedWidth = 0; private int cachedHeight = 0;

    /**
     * Cache the computed values of this <code>CharInfo</code> to a bitmap.
     * @param w The width of the bitmap. (Should be equal to the sampleWidth of the ISC you put this into.)
     * @param h The height of the bitmap. (Should be equal to the sampleHeight of the ISC you put this into.)
     */
    @Override
    public void cache(int w, int h) {
        if (cachedWidth == w && cachedHeight == h) return;
        cachedWidth = w; cachedHeight = h;
        this.cache = new float[w][h];
        for (int i = 0; i < w; i++)
            for (int j = 0; j < h; j++)
                this.cache[i][j] = scale * this.getRectSum(i/(float)w,j/(float)h,1f/w,1f/h);
    }

    /**
     * Build an array of <code>CharInfo</code> that can be used by an <code>ImageStringConverter</code>.
     * @param font The <code>Font</code> object to use.
     * @param charset An array of <code>char</code> describing what characters to calculate.
     * @param scale A scale for the calculated values of the charset (best left at 1 unless the image comes out too bright/dark).
     * @param densityBitmapBlend (best around 0.3) An option to blend between matching just by the shape of the char (0) or the average brightness (1).
     * @return An array of <code>CharInfo</code> that describe the given font and given characters.
     */
    public static CharInfo[] buildCharsetForFont(Font font, char[] charset, float scale, float densityBitmapBlend) {
        CharInfo[] charInfos = new CharInfo[charset.length];

        BufferedImage dummyImg = new BufferedImage(1,1,BufferedImage.TYPE_BYTE_GRAY);
        Graphics dummyImgGraphics = dummyImg.getGraphics();
        dummyImgGraphics.setFont(font);
        FontRenderContext frc = dummyImgGraphics.getFontMetrics().getFontRenderContext();
        Rectangle2D charBounds = font.getStringBounds("*",frc);
        dummyImgGraphics.dispose();

        int w = (int) Math.ceil(charBounds.getWidth());
        int h = (int) Math.ceil(charBounds.getHeight());

        BufferedImage characterImage = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
        Graphics draw = characterImage.getGraphics();
        draw.setFont(font);
        float charsetDensityMax = 0.0001f;
        for (int i = 0; i < charset.length; i++) {
            draw.clearRect(0,0, w, h);
            draw.drawString(charset[i]+"",0, -(int)Math.ceil(charBounds.getY()));

            double[][] light = ImageUtils.toLuminanceMap(ImageUtils.getRGBMap(characterImage,0,0, w, h),true);
            float [][] resultMap = new float[w][h];
            float density = 0; int amount = 0;
            for (double[] lc : light) for (double value : lc) {
                amount++;
                density += value;
            }
            density /= amount;
            charsetDensityMax = Math.max(charsetDensityMax, density);
            for (int j = 0; j < light.length; j++)
                for (int k = 0; k < light[j].length; k++)
                    resultMap[j][k] = (float) light[j][k];
            charInfos[i] = new CharInfoFontMade(resultMap,charset[i]);
            ((CharInfoFontMade)charInfos[i]).density = density;
        }
        for (CharInfo charInfo : charInfos) {
            float[][] bitmap = ((CharInfoFontMade)charInfo).bitmap;
            float density = ((CharInfoFontMade)charInfo).density;

            float relDensity = density / charsetDensityMax;
            for (int j = 0; j < bitmap.length; j++)
                for (int k = 0; k < bitmap[j].length; k++)
                    bitmap[j][k] = relDensity * densityBitmapBlend + (1-densityBitmapBlend) * bitmap[j][k];
            ((CharInfoFontMade)charInfo).scale = scale;
        }
        draw.dispose();
        return charInfos;
    }

    /**
     * Build an array of <code>CharInfo</code> that can be used by an <code>ImageStringConverter</code>.
     * @param font The <code>Font</code> object to use.
     * @param charset An array of <code>char</code> describing what characters to calculate.
     * @return An array of <code>CharInfo</code> that describe the given font and given characters.
     */
    public static CharInfo[] buildCharsetForFont(Font font, char[] charset) {
        return buildCharsetForFont(font, charset, 1, 0.3f);
    }
}
