package me.scidev5.drawASCII.util;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;

public class ImageUtils {

    /**
     * Get the integer representation for a pixel in an image.
     * @param image The <code>BufferedImage</code> to read from.
     * @param x The x-coordinate of the pixel to read.
     * @param y The y-coordinate of the pixel to read.
     * @return The color represented as an <code>int</code>.
     * @throws IndexOutOfBoundsException If the pixel is sampled outside the image.
     */
    public static int getRGBIntPixel(BufferedImage image, int x, int y) {
        return getRGBIntPixel(image.getRaster(), image.getColorModel(), x, y);
    }

    /**
     * Get the integer representation for a pixel in an image in terms of a <code>Raster</code> and <code>ColorModel</code>.
     * @param raster The <code>Raster</code> to read from.
     * @param colorModel The <code>ColorModel</code> to interpret the raster's result.
     * @param x The x-coordinate of the pixel to read.
     * @param y The y-coordinate of the pixel to read.
     * @return The color represented as an <code>int</code>.
     * @throws IndexOutOfBoundsException If the pixel is sampled outside the image or the colorModel does not match the raster.
     */
    public static int getRGBIntPixel(Raster raster, ColorModel colorModel, int x, int y) {
        int[] data_ = raster.getPixel(x,y,(int[])null);
        byte[] data = new byte[data_.length];
        for (int i = 0; i < data_.length; i++) data[i] = (byte)data_[i];
        int red = colorModel.getRed(data) << 16;
        int green = colorModel.getGreen(data) << 8;
        int blue = colorModel.getBlue(data);
        int alpha = colorModel.hasAlpha() ? colorModel.getAlpha(data) << 24 : 0xff000000;
        return alpha | red | green | blue;
    }

    /**
     * Get the apparent brightness of a color based on its rgb components. (Fast)
     * @param colorRGB The color in ARGB integer format.
     * @return The luminance of the color.
     */
    public static double getLuminanceFast(int colorRGB) {
        double r = (colorRGB >> 16 & 0xff) / 255.0;
        double g = (colorRGB >>  8 & 0xff) / 255.0;
        double b = (colorRGB       & 0xff) / 255.0;
        double a = (colorRGB >> 24 & 0xff) / 255.0;
        return (0.2126 * r + 0.7152 * g + 0.0722 * b) * a;
    }

    /**
     * Get the apparent brightness of a color based on its rgb components. (Slower but more accurate)
     * @param colorRGB The color in ARGB integer format.
     * @return The luminance of the color.
     */
    public static double getLuminance(int colorRGB) {
        double r = (colorRGB >> 16 & 0xff) / 255.0;
        double g = (colorRGB >>  8 & 0xff) / 255.0;
        double b = (colorRGB       & 0xff) / 255.0;
        double a = (colorRGB >> 24 & 0xff) / 255.0;
        return Math.sqrt(0.299 * r * r + 0.587 * g * g + 0.114 * b * b) * a;
    }

    /**
     * Get a subsection of the image as an array of color integers.
     * @param image The image to sample.
     * @param x The x-coordinate of the top-left corner of the section to sample.
     * @param y The y-coordinate of the top-left corner of the section to sample.
     * @param w The width of the section to sample.
     * @param h The height of the section to sample.
     * @return A 2-dimensional array of color integers representing part of the image.
     * @throws IndexOutOfBoundsException If the pixel is sampled outside the image.
     */
    public static int[][] getRGBMap(BufferedImage image, int x, int y, int w, int h) {
        return getRGBMap(image.getRaster(), image.getColorModel(), x, y, w, h);
    }
    /**
     * Get a subsection of the image as an array of color integers.
     * @param raster The <code>Raster</code> to read from.
     * @param colorModel The <code>ColorModel</code> to interpret the raster's result.
     * @param x The x-coordinate of the top-left corner of the section to sample.
     * @param y The y-coordinate of the top-left corner of the section to sample.
     * @param w The width of the section to sample.
     * @param h The height of the section to sample.
     * @return A 2-dimensional array of color integers representing part of the image.
     * @throws IndexOutOfBoundsException If the pixel is sampled outside the image or the colorModel does not match the raster.
     */
    public static int[][] getRGBMap(Raster raster, ColorModel colorModel, int x, int y, int w, int h) {
        int[][] map = new int[w][h];
        for (int i = 0; i < w; i++)
            for (int j = 0; j < h; j++)
                map[i][j] = getRGBIntPixel(raster,colorModel,x+i,y+j);
        return map;
    }

    /**
     * Convert an RGBA color map to luminance.
     * @param rgbMap The RGB map to use.
     * @return A 2-dimensional array of luminance values.
     */
    public static double[][] toLuminanceMap(int[][] rgbMap) {
        return toLuminanceMap(rgbMap, false);
    }
    /**
     * Convert an RGBA color map to luminance.
     * @param rgbMap The RGB map to use.
     * @param fast Whether or not to use the fast luminance function.
     * @return A 2-dimensional array of luminance values.
     */
    public static double[][] toLuminanceMap(int[][] rgbMap, boolean fast) {
        if (rgbMap.length == 0) return new double[0][0];
        double[][] map = new double[rgbMap.length][rgbMap[0].length];
        for (int i = 0; i < rgbMap.length; i++) {
            if (rgbMap[i].length != rgbMap[0].length) return new double[0][0];
            for (int j = 0; j < rgbMap[i].length; j++)
                if (fast)
                    map[i][j] = getLuminanceFast(rgbMap[i][j]);
                else
                    map[i][j] = getLuminance(rgbMap[i][j]);
        }
        return map;
    }
}
