package me.scidev5.drawASCII;

public class CharInfo {

    public static final CharInfo CHAR_SPACE =      new CharInfo(' ',0.1f);
    public static final CharInfo CHAR_HASHTAG =    new CharInfo('#',0.8f);
    public static final CharInfo CHAR_MINUS =      new CharInfo('-',0.3f,0f,0f, 1f,0f);
    public static final CharInfo CHAR_PERIOD =     new CharInfo('.',0.3f,0f,-0.9f);
    public static final CharInfo CHAR_ASTERISK =   new CharInfo('*',0.5f);
    public static final CharInfo CHAR_PLUS =       new CharInfo('+',0.4f);
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
    }

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
        return Math.min(1,Math.max(0, anisotropy * (focus + density)));
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
                sum += Math.abs(1 - data[i][j] / 255f - this.sample(x,y));
                amount++;
            }
        }
        return amount > 0 ? sum/amount : Float.POSITIVE_INFINITY;
    }
}
