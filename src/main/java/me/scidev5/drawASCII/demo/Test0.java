package me.scidev5.drawASCII.demo;

import me.scidev5.drawASCII.CharInfo;
import me.scidev5.drawASCII.ImageStringConverter;
import me.scidev5.drawASCII.StringImageConverter;
import me.scidev5.drawASCII.util.ImageUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

public class Test0 {

    //*
    public static void main(String... args) throws IOException {
        String imgName = "/doggo_smol.jpg";
        //String imgName = "/testimg.png";
        URL doggoImgFileURL = Test0.class.getResource(imgName);
        System.out.println("IMAGE: "+doggoImgFileURL);
        BufferedImage doggoImg = ImageIO.read(doggoImgFileURL);

        ImageStringConverter iscDoggo = new ImageStringConverter(doggoImg);
        CharInfo[] charset = CharInfo.buildCharsetInfo(StringImageConverter.fixedsys," :ABCDEFGHIJKLMNOPQRSTUVWXYZ#*+-.,'/\"|".toCharArray(),7f);
        for (CharInfo charInfo : charset)
            System.out.println(charInfo);
        iscDoggo.setCharset(charset);

        String str = iscDoggo.toString();
        System.out.println(str);


        StringImageConverter sicTest = new StringImageConverter(str);

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
