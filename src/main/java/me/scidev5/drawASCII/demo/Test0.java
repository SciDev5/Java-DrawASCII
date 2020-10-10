package me.scidev5.drawASCII.demo;

import me.scidev5.drawASCII.ImageStringConverter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class Test0 {

    /*public static void main(String... args) throws IOException {
        String imgName = "/doggo_smol.jpg";
        //String imgName = "/testimg.png";
        URL doggoImgFileURL = Test0.class.getResource(imgName);
        System.out.println("IMAGE: "+doggoImgFileURL);
        BufferedImage doggoImg = ImageIO.read(doggoImgFileURL);

        ImageStringConverter iscDoggo = new ImageStringConverter(doggoImg);

        System.out.println(iscDoggo);
    }*/


    public static void main(String... args) {
        Test0 o = new Test0();
        o.density = 0.5f;
        o.focusX = 1f;
        o.focusY = 0f;
        o.anisotropicX = 0f;
        o.anisotropicY = 0f;
        String str = "";
        for (float y = 0; y <= 1; y += 0.125f) {
            for (float x = 0; x <= 1; x += 0.125f)
                str += String.format("%.2f", o.sample(x, y)) + " ";
            str += "\n";
        }
        System.out.println(str);
    }

    private float density;
    private float focusX;
    private float focusY;
    private float anisotropicX;
    private float anisotropicY;
    private float sample(float x, float y) {
        float signedX = 2*x-1;
        float signedY = 2*y-1;
        float anisotropy = 1 - Math.abs(signedX*anisotropicY - signedY*anisotropicX);
        float focus = signedX*focusX + signedY*focusY;
        return Math.min(1,Math.max(0, anisotropy * (focus + 1) * density));
    }

}
