package me.scidev5.drawASCII;

import com.sun.istack.internal.NotNull;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.ArrayList;

public class StringImageConverter {

    private final String[] text;
    private final Color colorBG;
    private final Color[] colorFG;

    private final boolean additiveComposite;
    private final boolean customComposite;

    private Font font;

    private static final Composite additiveCompositeObject = (srcColorModel, dstColorModel, hints) -> new CompositeContext() {
        @Override
        public void dispose() {
        }

        @Override
        public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {
            int[] dstOutPixel = new int[dstColorModel.getNumComponents()];
            int[] srcPixel = new int[srcColorModel.getNumComponents()];
            int[] dstInPixel = new int[srcColorModel.getNumComponents()];
            outer:
            for (int x = Math.max(src.getMinX(), dstIn.getMinX()); x < src.getWidth() && x < dstIn.getWidth(); x++) {
                for (int y = Math.max(src.getMinY(), dstIn.getMinY()); y < src.getHeight() && y < dstIn.getHeight(); y++) {
                    try {
                        src.getPixel(x, y, srcPixel);
                        dstIn.getPixel(x, y, dstInPixel);
                        for (int i = 0; i < dstOutPixel.length && i < srcPixel.length; i++)
                            dstOutPixel[i] = Math.max(0, Math.min(255, srcPixel[i] + dstInPixel[i]));
                        dstOut.setPixel(x, y, dstOutPixel);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.out.println("(" + x + "," + y + ") OUT OF BOUNDS");
                        break outer;
                    }
                }
            }
        }
    };
    private static final Composite multiplicativeCompositeObject = (srcColorModel, dstColorModel, hints) -> new CompositeContext() {
        @Override
        public void dispose() {
        }

        @Override
        public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {
            int[] dstOutPixel = new int[dstColorModel.getNumComponents()];
            int[] srcInPixel = new int[srcColorModel.getNumComponents()];
            int[] dstInPixel = new int[srcColorModel.getNumComponents()];
            outer:
            for (int x = Math.max(src.getMinX(), dstIn.getMinX()); x < src.getWidth() && x < dstIn.getWidth(); x++) {
                for (int y = Math.max(src.getMinY(), dstIn.getMinY()); y < src.getHeight() && y < dstIn.getHeight(); y++) {
                    try {
                        src.getPixel(x, y, srcInPixel);
                        dstIn.getPixel(x, y, dstInPixel);
                        for (int i = 0; i < dstOutPixel.length && i < srcInPixel.length; i++)
                            dstOutPixel[i] = Math.max(0, Math.min(255, srcInPixel[i] * dstInPixel[i] / 255));
                        dstOut.setPixel(x, y, dstOutPixel);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.out.println("(" + x + "," + y + ") OUT OF BOUNDS");
                        break outer;
                    }
                }
            }
        }
    };

    /**
     * Create a new SIC with the given text and single-channel colors.
     * @param text The text to render.
     * @param bg The image background.
     * @param fg The text color.
     */
    public StringImageConverter(@NotNull String text, Color bg, Color fg) {
        this.text = new String[]{text};
        this.colorBG = bg == null ? new Color(0xff000000) : bg;
        this.colorFG = new Color[]{fg == null ? new Color(0xffffffff) : fg};
        this.additiveComposite = false;
        this.customComposite = false;
        setup();
    }

    /**
     * Create a new SIC with the given text.
     * @param text The text to render.
     * @param darkBG If the background should be dark.
     */
    public StringImageConverter(@NotNull String text, boolean darkBG) {
        this.text = new String[]{text};
        this.colorBG = darkBG ? new Color(0xff000000) : new Color(0xffffffff);
        this.colorFG = new Color[]{darkBG ? new Color(0xffffffff) : new Color(0xff000000)};
        this.additiveComposite = false;
        this.customComposite = false;
        setup();
    }

    /**
     * Create a new SIC with the given text for red, green, and blue.
     * @param redText The text for red channel.
     * @param greenText The text for green channel.
     * @param blueText The text for blue channel.
     * @param additiveComposite True -> RGB, black bg; False -> CMYK, white bg;
     */
    public StringImageConverter(@NotNull String redText, @NotNull String greenText, @NotNull String blueText, boolean additiveComposite) {
        this.text = new String[]{redText,greenText,blueText};
        this.colorBG = additiveComposite ? new Color(0xff000000) : new Color(0xffffffff);
        this.colorFG = additiveComposite ?
                new Color[]{new Color(0xffff0000),new Color(0xff00ff00),new Color(0xff0000ff)} :
                new Color[]{new Color(0xff00ffff),new Color(0xffff00ff),new Color(0xffffff00)};
        this.additiveComposite = additiveComposite;
        this.customComposite = true;
        setup();
    }
    private void setup() {
        font = new Font(Font.MONOSPACED,Font.BOLD,1);//Font.createFont(Font.TRUETYPE_FONT, fontFileStream);
        font = font.deriveFont(16f);
    }

    /**
     * Set the font to render with.
     * @param font The font.
     */
    public void setFont(@NotNull Font font) {
        this.font = font;
    }

    /**
     * Write the text provided to an image in the font provided.
     * @return A <code>BufferedImage</code> with the text in this object.
     */
    public BufferedImage toImage() {
        if (font == null) throw new IllegalStateException("Font needs to be assigned before converting to image!");

        java.util.List<String[]> lines = new ArrayList<>();
        int maxTxtLen = 0; int maxTxtWidth = 0;
        for (String channel : text) {
            String[] channelLines = channel.split("\n");
            lines.add(channelLines);
            maxTxtLen = Math.max(maxTxtLen, channelLines.length);
            for (String line : channelLines) maxTxtWidth = Math.max(maxTxtWidth,line.length());
        }

        if (lines.size() == 0 || maxTxtLen == 0) return null;

        BufferedImage dummyImg = new BufferedImage(1,1,BufferedImage.TYPE_BYTE_GRAY);
        Graphics dummyImgGraphics = dummyImg.getGraphics();
        dummyImgGraphics.setFont(font);
        FontRenderContext frc = dummyImgGraphics.getFontMetrics().getFontRenderContext();
        Rectangle2D stringBounds = font.getStringBounds("-",frc);
        dummyImgGraphics.dispose();

        int w = maxTxtWidth * (int) Math.ceil(stringBounds.getWidth());
        int h = maxTxtLen * (int) Math.ceil(stringBounds.getHeight());

        BufferedImage image = new BufferedImage(w,h,BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = (Graphics2D) image.getGraphics();

        g.setFont(font);
        g.setColor(colorBG);
        g.fillRect(0,0,w,h);

        if (customComposite) {
            if (additiveComposite)
                g.setComposite(additiveCompositeObject);
            else
                g.setComposite(multiplicativeCompositeObject);
        }

        for (int n = 0; n < lines.size(); n++) {
            g.setColor(colorFG[n]);
            String[] channelLines = lines.get(n);
            for (int i = 0; i < channelLines.length; i++)
                g.drawString(channelLines[i], 0, i * h / maxTxtLen - (int) Math.ceil(stringBounds.getY()));
        }
        g.dispose();

        return image;
    }

}
