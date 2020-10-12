package me.scidev5.drawASCII.demo;

import me.scidev5.drawASCII.charInfo.CharInfo;
import me.scidev5.drawASCII.ImageStringConverter;
import me.scidev5.drawASCII.StringImageConverter;
import me.scidev5.drawASCII.charInfo.CharInfoFontMade;
import me.scidev5.drawASCII.util.FontUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class Demo0 {

    public static void main(String... args) throws IOException, FontFormatException {
        String imgName = "/doggo_smol.jpg";
        URL doggoImgFileURL = Demo0.class.getResource(imgName);
        System.out.println("IMAGE: "+doggoImgFileURL);
        BufferedImage doggoImg = ImageIO.read(doggoImgFileURL);

        FontUtils.loadFont("scp",Font.TRUETYPE_FONT, Demo0.class.getResource("/fonts/sourcecodepro/SourceCodePro-Black.ttf"));
        Font font = FontUtils.getFont(FontUtils.deriveFont("scp",16f));
        CharInfo[] charset = CharInfoFontMade.buildCharsetForFont(font," -*#%^.,'\":~|\\/+=<abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ>".toCharArray());

        ImageStringConverter iscDoggo = new ImageStringConverter(doggoImg);
        iscDoggo.setCharset(charset);
        iscDoggo.setMode(ImageStringConverter.CalculateMode.RGB);
        iscDoggo.setSampleDimensions(10,20);

        String[] strs = iscDoggo.toStringArr();

        StringImageConverter sicTest = new StringImageConverter(strs[0], strs[1], strs[2], true);
        sicTest.setFont(font);
        BufferedImage image = sicTest.toImage();
        ImageIO.write(image,"png", new File("out.png"));
    }
}
