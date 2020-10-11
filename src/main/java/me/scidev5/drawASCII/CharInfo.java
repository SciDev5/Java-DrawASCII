package me.scidev5.drawASCII;

import me.scidev5.drawASCII.util.ImageUtils;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class CharInfo {

    //*
    public static final CharInfo CHAR_SPACE =      new CharInfo(' ',0.05f);
    public static final CharInfo CHAR_HASHTAG =    new CharInfo('#',0.95f);
    public static final CharInfo CHAR_MINUS =      new CharInfo('-',0.4f,0f,0f, 1f,0f);
    public static final CharInfo CHAR_PERIOD =     new CharInfo('.',0.3f,0f,-0.9f);
    public static final CharInfo CHAR_ASTERISK =   new CharInfo('*',0.8f);
    public static final CharInfo CHAR_PLUS =       new CharInfo('+',0.6f);
    public static final CharInfo CHAR_SLASH_FWD =  new CharInfo('/',0.6f,0f,0f, 1f, 1f);
    public static final CharInfo CHAR_SLACK_BK =   new CharInfo('\\',0.6f,0f,0f, 1f, -1f);
    public static final CharInfo CHAR_QUOT =       new CharInfo('\'',0.3f,0f,0.9f);
    public static final CharInfo CHAR_QUOTDOUBLE = new CharInfo('"',0.4f,0f,0.9f);
    public static final CharInfo CHAR_BAR =        new CharInfo('|',0.6f,0f,0f,0f,1f);
    public static final CharInfo[] getArray() {
        return new CharInfo[] {
                CHAR_SPACE,
                CHAR_HASHTAG,       CHAR_MINUS,     CHAR_PERIOD,    CHAR_ASTERISK,
                CHAR_PLUS,          CHAR_SLASH_FWD, CHAR_SLACK_BK,  CHAR_QUOT,
                CHAR_QUOTDOUBLE,    CHAR_BAR
        };
    }//*/



    public final char character;
    private final float density;
    private final float focusX;
    private final float focusY;
    private final float anisotropicX;
    private final float anisotropicY;

    private CharInfo(char character, float density) {
        this.character = character;
        this.density = density;
        this.focusX = 0;
        this.focusY = 0;
        this.anisotropicX = 0;
        this.anisotropicY = 0;
    }
    private CharInfo(char character, float density, float focusX, float focusY) {
        this.character = character;
        this.density = density;
        this.focusX = focusX;
        this.focusY = focusY;
        this.anisotropicX = 0;
        this.anisotropicY = 0;
    }
    private CharInfo(char character, float density, float focusX, float focusY, float anisotropicX, float anisotropicY) {
        this.character = character;
        this.density = density;
        this.focusX = focusX;
        this.focusY = focusY;
        this.anisotropicX = anisotropicX;
        this.anisotropicY = anisotropicY;
    }

    private float sample(float x, float y) {
        float signedX = 2*x-1;
        float signedY = 2*y-1;
        float anisotropy = 1 - Math.abs(signedX*anisotropicY - signedY*anisotropicX);
        float focus = signedX*focusX + signedY*focusY;
        return Math.min(1,Math.max(0, anisotropy * (focus + 1) * density));
    }

    /**
     * See how well a given luminosity map matches this <code>CharInfo</code>.
     * @param data The luminosity map.
     * @return How badly the values match. (eg. 0 -> nearly perfect match; 1 -> pretty bad match)
     */
    public float test(double[][] data) {
        int amount = 0; float sum = 0;
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                float x = data.length > 1 ? i / (data.length - 1f) : 0.5f;
                float y = data[i].length > 1 ? j / (data[i].length - 1f) : 0.5f;
                sum += Math.abs(0 + data[i][j] - this.sample(x,1-y));
                amount++;
            }
        }
        return amount > 0 ? sum/amount : Float.POSITIVE_INFINITY;
    }


    public static CharInfo[] buildCharsetInfo(Font font, char[] charset, float densityScale) {
        CharInfo[] charInfos = new CharInfo[charset.length];
        Random jitter = new Random();
        float jitterAmount = 0.0001f;

        BufferedImage dummyImg = new BufferedImage(1,1,BufferedImage.TYPE_BYTE_GRAY);
        Graphics dummyImgGraphics = dummyImg.getGraphics();
        dummyImgGraphics.setFont(font);
        FontRenderContext frc = dummyImgGraphics.getFontMetrics().getFontRenderContext();
        Rectangle2D charBounds = font.getStringBounds("*",frc);
        dummyImgGraphics.dispose();

        int w = (int) Math.ceil(charBounds.getWidth());
        int h = (int) Math.ceil(charBounds.getHeight());

        BufferedImage characterImage = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
        Graphics draw = characterImage.getGraphics();
        draw.setFont(font);
        for (int i = 0; i < charset.length; i++) {
            draw.clearRect(0,0, w, h);
            draw.drawString(charset[i]+"",0, -(int)Math.ceil(charBounds.getY()));

            double[][] light = ImageUtils.toLuminanceMap(ImageUtils.getRGBMap(characterImage,0,0, w, h),true);
            int amount = 0;
            float focusAmount = 0.001f;
            float density = 0;
            float focusX = 0;
            float focusY = 0;
            java.util.List<Float> xs = new ArrayList<>();
            java.util.List<Float> ys = new ArrayList<>();
            float anisotropyAmount = 0.001f;
            for (int j = 0; j < light.length; j++)
                for (int k = 0; k < light[j].length; k++) {
                    float x = light.length > 1 ? j / (light.length - 1f) * 2 - 1 : 0;
                    float y = light[j].length > 1 ? k / (light[j].length - 1f) * 2 - 1 : 0;
                    double lval = light[j][k];
                    amount++;
                    density += lval;
                    focusX += x*lval;
                    focusY += y*lval;
                    focusAmount += lval;
                    if (lval > 0.5) {
                        xs.add(x+(jitter.nextFloat()-0.5f)*jitterAmount);
                        ys.add(y+(jitter.nextFloat()-0.5f)*jitterAmount);
                        anisotropyAmount += Math.sqrt(x*x+y*y);
                    }
                }
            float score = 0; float angle = 0;
            for (float a = 0; a < Math.PI; a += 0.1) {
                float currentScore = 0;
                float anx = (float) Math.cos(a);
                float any = (float) Math.sin(a);
                for (int j = 0; j < xs.size(); j++) {
                    currentScore += xs.get(j)*anx + ys.get(j)*any;
                }
                currentScore /= anisotropyAmount;
                if (currentScore > score) {
                    score = currentScore;
                    angle = a;
                }
            }
            float anisotropicX = (float) (Math.cos(angle)*score);
            float anisotropicY = (float) (Math.sin(angle)*score);


            charInfos[i] = new CharInfo(charset[i],densityScale*density/amount,focusX/focusAmount,focusY/focusAmount,anisotropicX,anisotropicY);
        }
        draw.dispose();
        return charInfos;
    }

    @Override
    public String toString() {
        return "CharInfo{" +
                "character='" + character +
                "', density=" + density +
                ", focus=(" + focusX + ", " + focusY +
                "), anisotropy=(" + anisotropicX + ", " + anisotropicY +
                ")}";
    }
}
