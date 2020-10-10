package me.scidev5.drawASCII;

import com.sun.istack.internal.NotNull;
import me.scidev5.drawASCII.util.ImageUtils;

import java.awt.image.BufferedImage;

public class ImageStringConverter {

    private BufferedImage image;
    private int sampleWidth = 8;
    private int sampleHeight = 16;

    public ImageStringConverter(@NotNull BufferedImage image) {
        this.image = image;
    }

    private char calculateAt(int i, int j) {
        double[][] luminosityMap = ImageUtils.toLuminanceMap(ImageUtils.getRGBMap(image,i*sampleWidth,j*sampleHeight,sampleWidth,sampleHeight));

        CharInfo[] chars = CharInfo.getArray();
        char bestMatch = 'x'; float bestValue = Float.POSITIVE_INFINITY;
        for (CharInfo info : chars) {
            float value = info.test(luminosityMap);
            if (value < bestValue) {
                bestMatch = info.character;
                bestValue = value;
            }
        }
        return bestMatch;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        int w = image.getWidth()/sampleWidth;
        int h = image.getHeight()/sampleHeight;
        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++)
                str.append(this.calculateAt(i, j));
            str.append('\n');
        }
        return str.toString();
    }
}
