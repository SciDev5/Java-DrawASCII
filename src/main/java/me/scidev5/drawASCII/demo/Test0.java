package me.scidev5.drawASCII.demo;

import me.scidev5.drawASCII.CharInfo;
import me.scidev5.drawASCII.ImageStringConverter;
import me.scidev5.drawASCII.util.ImageUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class Test0 {

    public static void main(String... args) throws IOException {
        String imgName = "/doggo_smol.jpg";
        //String imgName = "/testimg.png";
        URL doggoImgFileURL = Test0.class.getResource(imgName);
        System.out.println("IMAGE: "+doggoImgFileURL);
        BufferedImage doggoImg = ImageIO.read(doggoImgFileURL);

        ImageStringConverter iscDoggo = new ImageStringConverter(doggoImg);

        System.out.println(iscDoggo);
    }

}
