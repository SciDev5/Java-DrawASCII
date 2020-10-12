package me.scidev5.drawASCII.demo;

import me.scidev5.drawASCII.ImageStringConverter;
import me.scidev5.drawASCII.charInfo.CharInfo;
import me.scidev5.drawASCII.charInfo.CharInfoHumanMade;
import me.scidev5.drawASCII.util.FontUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class Demo1 {
    public static void main(String... args) throws IOException, FontFormatException {
        String imgName = "/doggo_smol.jpg";
        URL doggoImgFileURL = Demo0.class.getResource(imgName);
        System.out.println("IMAGE: "+doggoImgFileURL);
        BufferedImage doggoImg = ImageIO.read(doggoImgFileURL);

        FontUtils.loadFont("scp", Font.TRUETYPE_FONT, Demo0.class.getResource("/fonts/sourcecodepro/SourceCodePro-Black.ttf"));
        Font font = FontUtils.getFont(FontUtils.deriveFont("scp",16f));
        CharInfo[] charset = CharInfoHumanMade.getSimpleCharset();
        for (CharInfo charInfo : charset) System.out.println(charInfo);

        ImageStringConverter iscDoggo = new ImageStringConverter(doggoImg);
        iscDoggo.setCharset(charset);
        iscDoggo.setSampleDimensions(16,32);

        // For black text on white background.
        iscDoggo.setMode(ImageStringConverter.CalculateMode.NEG_LUMINANCE);
        String resultWhiteBG = iscDoggo.toStringArr()[0];
        System.out.println(resultWhiteBG);

        // For white text on black background.
        iscDoggo.setMode(ImageStringConverter.CalculateMode.LUMINANCE);
        String resultBlackBG = iscDoggo.toStringArr()[0];
        System.out.println(resultBlackBG);
    }
}
