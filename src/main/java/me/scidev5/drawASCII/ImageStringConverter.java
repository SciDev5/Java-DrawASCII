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

    public ImageStringConverter(@NotNull BufferedImage image) {
        this.image = image;
    }

    public void setCharset(@NotNull CharInfoHumanMade[] charset) {
        this.charset = charset;
    }
    public void setMode(@NotNull CalculateMode mode) {
        this.mode = mode;
    }
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
                break;
            case RGB:
                map = new double[][][]{
                    ImageUtils.isolateRGBMapChannel(rgbaMap,0),
                    ImageUtils.isolateRGBMapChannel(rgbaMap,1),
                    ImageUtils.isolateRGBMapChannel(rgbaMap,2)
                };
                break;
            case CMY:
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
