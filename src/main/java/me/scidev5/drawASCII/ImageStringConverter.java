package me.scidev5.drawASCII;

import com.sun.istack.internal.NotNull;
import me.scidev5.drawASCII.charInfo.CharInfo;
import me.scidev5.drawASCII.charInfo.CharInfoHumanMade;
import me.scidev5.drawASCII.util.ArrayUtils;
import me.scidev5.drawASCII.util.ImageUtils;

import java.awt.image.BufferedImage;

public class ImageStringConverter {

    private final BufferedImage image;
    private int sampleWidth = 8;
    private int sampleHeight = 16;
    private CalculateMode mode = CalculateMode.LUMINANCE;
    private CharInfo[] charset = CharInfoHumanMade.getSimpleCharset();

    public enum CalculateMode {
        LUMINANCE(1),NEG_LUMINANCE(1),RGB(3),CMY(3);
        public final int nChannels;
        CalculateMode(int nchannels) { this.nChannels = nchannels; }
    }

    /**
     * Create a new ISC with the given image.
     * @param image The image.
     */
    public ImageStringConverter(@NotNull BufferedImage image) {
        this.image = image;
    }

    /**
     * Set the charset used by the ISC.
     * @param charset An array of CharInfo taken from <code>CharInfoFontMade</code> or <code>CharInfoHumanMade</code>.
     */
    public void setCharset(@NotNull CharInfo[] charset) {
        this.charset = charset;
    }

    /**
     * Set the coloring mode for the ISC.
     * @param mode The mode: RGB, LUMINANCE, etc.
     */
    public void setMode(@NotNull CalculateMode mode) {
        this.mode = mode;
    }

    /**
     * Set the dimensions for each chunk that will be converted into a character.
     * @param width Width in pixels.
     * @param height Height in pixels.
     * @throws IllegalArgumentException If the width or height is less than or equal to 0.
     */
    public void setSampleDimensions(int width, int height) throws IllegalArgumentException {
        if (width <= 0) throw new IllegalArgumentException("Width must be greater than 0!");
        if (height <= 0) throw new IllegalArgumentException("Height must be greater than 0!");
        sampleWidth = width;
        sampleHeight = height;
        for (CharInfo info : charset) info.cache(width,height);
    }

    private char calculateAt(int i, int j, double[][] map) {
        char bestMatch = 'x'; float bestValue = Float.POSITIVE_INFINITY;
        double[][] mapSection = ArrayUtils.getChunkOf2dArr(map,i*sampleWidth,j*sampleHeight,sampleWidth,sampleHeight);
        for (CharInfo info : charset) {
            float value = info.test(mapSection);
            if (value < bestValue) {
                bestMatch = info.getChar();
                bestValue = value;
            }
        }
        return bestMatch;
    }

    /**
     * Compute the text that approximates this image.
     * @return An array of strings, one for each channel (luminance: 1 channel, RGB: 3 channels, CMY: 3 channels).
     */
    public String[] toStringArr() {
        int w = image.getWidth()/sampleWidth;
        int h = image.getHeight()/sampleHeight;
        int nChannels = this.mode.nChannels;

        int[][] rgbaMap = ImageUtils.getRGBMap(image,0,0,image.getWidth(),image.getHeight());
        double[][][] map = null;
        switch (this.mode) {
            case LUMINANCE:
                map = new double[][][]{ ImageUtils.toLuminanceMap(rgbaMap) };
                break;
            case NEG_LUMINANCE:
                map = new double[][][]{ ImageUtils.invertMap(ImageUtils.toLuminanceMap(rgbaMap)) };
                break;
            case RGB:
                map = new double[][][]{
                    ImageUtils.isolateRGBMapChannel(rgbaMap,0),
                    ImageUtils.isolateRGBMapChannel(rgbaMap,1),
                    ImageUtils.isolateRGBMapChannel(rgbaMap,2)
                };
                break;
            case CMY:
                map = new double[][][]{
                    ImageUtils.isolateCMYMapChannel(rgbaMap,0),
                    ImageUtils.isolateCMYMapChannel(rgbaMap,1),
                    ImageUtils.isolateCMYMapChannel(rgbaMap,2)
                };
                break;
        }

        String[] channelStrs = new String[nChannels];
        for (int k = 0; k < nChannels; k++) {
            StringBuilder str = new StringBuilder();
            for (int j = 0; j < h; j++) {
                for (int i = 0; i < w; i++)
                    str.append(this.calculateAt(i, j, map[k]));
                str.append('\n');
            }
            channelStrs[k] = str.toString();
        }
        return channelStrs;
    }
}
