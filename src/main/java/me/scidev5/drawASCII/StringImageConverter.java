package me.scidev5.drawASCII;

import com.sun.istack.internal.NotNull;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Arrays;

public class StringImageConverter {

    private final String text;

    public static Font fixedsys; // 8px x 14px

    static {
        //try {
            //InputStream fontFileStream = StringImageConverter.class.getResourceAsStream("/FSEX300.ttf");
            fixedsys = new Font(Font.MONOSPACED,Font.BOLD,1);//Font.createFont(Font.TRUETYPE_FONT, fontFileStream);
            fixedsys = fixedsys.deriveFont(16f);
        /*} catch (FontFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    public StringImageConverter(@NotNull String text) {
        this.text = text;
    }

    public BufferedImage toImage() {
        String[] lines = text.split("\n");

        BufferedImage dummyImg = new BufferedImage(1,1,BufferedImage.TYPE_BYTE_GRAY);
        Graphics dummyImgGraphics = dummyImg.getGraphics();
        dummyImgGraphics.setFont(fixedsys);
        FontRenderContext frc = dummyImgGraphics.getFontMetrics().getFontRenderContext();
        Rectangle2D stringBounds = fixedsys.getStringBounds(lines[0],frc);
        dummyImgGraphics.dispose();

        int w = (int) Math.ceil(stringBounds.getWidth());
        int h = lines.length * (int) Math.ceil(stringBounds.getHeight());

        BufferedImage image = new BufferedImage(w,h,BufferedImage.TYPE_BYTE_GRAY);
        Graphics g = image.getGraphics();

        g.setFont(fixedsys);
        g.setColor(new Color(0xff000000));
        g.fillRect(0,0,w,h);

        g.setColor(new Color(0xffffffff));
        for (int i = 0; i < lines.length; i++)
            g.drawString(lines[i], 0, i * h/lines.length - (int) Math.ceil(stringBounds.getY()));

        g.dispose();

        return image;
    }

}
