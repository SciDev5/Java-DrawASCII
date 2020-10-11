package me.scidev5.drawASCII.demo;

import me.scidev5.drawASCII.charInfo.CharInfoHumanMade;
import me.scidev5.drawASCII.ImageStringConverter;
import me.scidev5.drawASCII.StringImageConverter;
import me.scidev5.drawASCII.util.FontUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class Test0 {

    //*
    public static void main(String... args) throws IOException, FontFormatException {
        String imgName = "/doggo_smol.jpg";
        //String imgName = "/testimg.png";
        URL doggoImgFileURL = Test0.class.getResource(imgName);
        System.out.println("IMAGE: "+doggoImgFileURL);
        BufferedImage doggoImg = ImageIO.read(doggoImgFileURL);

        FontUtils.loadFont("scp",Font.TRUETYPE_FONT,Test0.class.getResource("/fonts/sourcecodepro/SourceCodePro-Black.ttf"));
        Font font = FontUtils.getFont(FontUtils.deriveFont("scp",8f));

        ImageStringConverter iscDoggo = new ImageStringConverter(doggoImg);

        CharInfoHumanMade[] charset = CharInfoHumanMade.getSimpleCharset();
        //for (CharInfo charInfo : charset) System.out.println(charInfo);

        iscDoggo.setCharset(charset);
        iscDoggo.setMode(ImageStringConverter.CalculateMode.RGB);

        String[] strs = iscDoggo.toStringArr();
        System.out.println(strs[0]);

        StringImageConverter sicTest = new StringImageConverter(strs[0],strs[1],strs[2]);
        sicTest.setFont(font);

        BufferedImage image = sicTest.toImage();
        ImageIO.write(image,"png", new File("out.png"));
    }
    /*/
    public static void main(String... args) throws IOException {
        //StringImageConverter sicTest = new StringImageConverter("Yeet-*#_-^+./\\'\"");

        //BufferedImage image = sicTest.toImage();
        //ImageIO.write(image,"png", new File("out.png"));


        for (CharInfo charInfo : CharInfo.buildCharsetInfo(StringImageConverter.fixedsys,"#*+-.,'\"|".toCharArray(),2.5f))
            System.out.println(charInfo);
    }
    //*/
}
