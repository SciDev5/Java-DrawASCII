package me.scidev5.drawASCII;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.ArrayList;

public class StringImageConverter {

    private final String[] text;
    private Color colorBG;
    private Color[] colorFG;

    private boolean additiveComposite;

    public Font font;

    public StringImageConverter(@NotNull String text, Color bg, Color fg, boolean additiveComposite) {
        this.text = new String[]{text};
        this.colorBG = bg == null ? new Color(0xff000000) : bg;
        this.colorFG = new Color[]{fg == null ? new Color(0xffffffff) : fg};
        this.additiveComposite = additiveComposite;
        setup();
    }
    public StringImageConverter(@NotNull String text, boolean darkBG) {
        this.text = new String[]{text};
        this.colorBG = darkBG ? new Color(0xff000000) : new Color(0xffffffff);
        this.colorFG = new Color[]{darkBG ? new Color(0xffffffff) : new Color(0xff000000)};
        this.additiveComposite = darkBG;
        setup();
    }
    public StringImageConverter(@NotNull String redText, @NotNull String greenText, @NotNull String blueText, boolean additiveComposite) {
        this.text = new String[]{redText,greenText,blueText};
        this.colorBG = additiveComposite ? new Color(0xff000000) : new Color(0xffffffff);
        this.colorFG = additiveComposite ?
                new Color[]{new Color(0xffff0000),new Color(0xff00ff00),new Color(0xff0000ff)} :
                new Color[]{new Color(0xff00ffff),new Color(0xffff00ff),new Color(0xffffff00)};
        this.additiveComposite = additiveComposite;
        setup();
    }
    private void setup() {
        font = new Font(Font.MONOSPACED,Font.BOLD,1);//Font.createFont(Font.TRUETYPE_FONT, fontFileStream);
        font = font.deriveFont(16f);
    }

    public void setFont(@NotNull Font font) {
        this.font = font;
    }

    @Nullable
    public BufferedImage toImage() {
        java.util.List<String[]> lines = new ArrayList<>();
        int maxTxtLen = 0;
        for (String channel : text) {
            String[] channelLines = channel.split("\n");
            lines.add(channelLines);
            maxTxtLen = Math.max(maxTxtLen, channelLines.length);
        }

        if (lines.size() == 0 || maxTxtLen == 0) return null;

        BufferedImage dummyImg = new BufferedImage(1,1,BufferedImage.TYPE_BYTE_GRAY);
        Graphics dummyImgGraphics = dummyImg.getGraphics();
        dummyImgGraphics.setFont(font);
        FontRenderContext frc = dummyImgGraphics.getFontMetrics().getFontRenderContext();
        Rectangle2D stringBounds = font.getStringBounds(lines.get(0)[0],frc);
        dummyImgGraphics.dispose();

        int w = (int) Math.ceil(stringBounds.getWidth());
        int h = maxTxtLen * (int) Math.ceil(stringBounds.getHeight());

        BufferedImage image = new BufferedImage(w,h,BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = (Graphics2D) image.getGraphics();

        g.setFont(font);
        g.setColor(colorBG);
        g.fillRect(0,0,w,h);

        if (additiveComposite)
            g.setComposite((srcColorModel, dstColorModel, hints) -> new CompositeContext() {
                @Override public void dispose() {}
                @Override
                public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {
                    int[] oarr = new int[dstColorModel.getNumComponents()];
                    int[] iarrs = new int[srcColorModel.getNumComponents()];
                    int[] iarrd = new int[srcColorModel.getNumComponents()];
                    outer:
                    for (int x = Math.max(src.getMinX(), dstIn.getMinX()); x < src.getWidth() && x < dstIn.getWidth(); x++) {
                        for (int y = Math.max(src.getMinY(), dstIn.getMinY()); y < src.getHeight() && y < dstIn.getHeight(); y++) {
                            try {
                                src.getPixel(x, y, iarrs);
                                dstIn.getPixel(x, y, iarrd);
                                for (int i = 0; i < oarr.length && i < iarrs.length; i++)
                                    oarr[i] = Math.max(0, Math.min(255, iarrs[i] + iarrd[i]));
                                dstOut.setPixel(x, y, oarr);
                            } catch (ArrayIndexOutOfBoundsException e) {
                                System.out.println("(" + x + "," + y + ") OUT OF BOUNDS");
                                break outer;
                            }
                        }
                    }
                }
            });
        else
            g.setComposite((srcColorModel, dstColorModel, hints) -> new CompositeContext() {
                @Override public void dispose() {}
                @Override
                public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {
                    int[] oarr = new int[dstColorModel.getNumComponents()];
                    int[] iarrs = new int[srcColorModel.getNumComponents()];
                    int[] iarrd = new int[srcColorModel.getNumComponents()];
                    outer:
                    for (int x = Math.max(src.getMinX(), dstIn.getMinX()); x < src.getWidth() && x < dstIn.getWidth(); x++) {
                        for (int y = Math.max(src.getMinY(), dstIn.getMinY()); y < src.getHeight() && y < dstIn.getHeight(); y++) {
                            try {
                                src.getPixel(x, y, iarrs);
                                dstIn.getPixel(x, y, iarrd);
                                for (int i = 0; i < oarr.length && i < iarrs.length; i++)
                                    oarr[i] = Math.max(0, Math.min(255, iarrs[i] * iarrd[i] / 255));
                                dstOut.setPixel(x, y, oarr);
                            } catch (ArrayIndexOutOfBoundsException e) {
                                System.out.println("(" + x + "," + y + ") OUT OF BOUNDS");
                                break outer;
                            }
                        }
                    }
                }
            });

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
