package me.scidev5.drawASCII.charInfo;

import me.scidev5.drawASCII.util.ImageUtils;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class CharInfoFontMade implements CharInfo {

    private final char character;
    private final float[][] bitmap;
    private float[][] cache;
    private CharInfoFontMade(float[][] bitmap, char charIn) {
        this.character = charIn;
        this.bitmap = bitmap;
    }


    @Override
    public char getChar() {
        return this.character;
    }

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
        float sum = 0;
        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++)
                sum += bitmap[i][j]*(Math.max(Math.min(i-x,1),0)*Math.max(Math.min(x+w-i,1),0) * Math.max(Math.min(j-y,1),0)*Math.max(Math.min(y+h-j,1),0));
        return sum;
    }

    int cachedWidth = 0; int cachedHeight = 0;
    @Override
    public void cache(int w, int h) {
        if (cachedWidth == w && cachedHeight == h) return;
        cachedWidth = w; cachedHeight = h;
        this.cache = new float[w][h];
        for (int i = 0; i < w; i++)
            for (int j = 0; j < h; j++)
                this.cache[i][j] = this.getRectSum(i/(float)w,j/(float)h,1f/w,1f/h);
    }


    public static CharInfo[] buildCharsetForFont(Font font, char[] charset) {
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
        for (int i = 0; i < charset.length; i++) {
            draw.clearRect(0,0, w, h);
            draw.drawString(charset[i]+"",0, -(int)Math.ceil(charBounds.getY()));

            double[][] light = ImageUtils.toLuminanceMap(ImageUtils.getRGBMap(characterImage,0,0, w, h),true);
            float [][] resultMap = new float[w][h];
            for (int j = 0; j < light.length; j++)
                for (int k = 0; k < light[j].length; k++)
                    resultMap[j][k] = (float) light[j][k];
            charInfos[i] = new CharInfoFontMade(resultMap,charset[i]);
        }
        draw.dispose();
        return charInfos;
    }
}
