package me.scidev5.drawASCII.charInfo;

public class CharInfoHumanMade implements CharInfo {

    public static final CharInfoHumanMade CHAR_SPACE =      new CharInfoHumanMade(' ',0.05f);
    public static final CharInfoHumanMade CHAR_HASHTAG =    new CharInfoHumanMade('#',0.95f);
    public static final CharInfoHumanMade CHAR_MINUS =      new CharInfoHumanMade('-',0.4f,0f,0f, 1f,0f);
    public static final CharInfoHumanMade CHAR_PERIOD =     new CharInfoHumanMade('.',0.3f,0f,-0.9f);
    public static final CharInfoHumanMade CHAR_ASTERISK =   new CharInfoHumanMade('*',0.8f);
    public static final CharInfoHumanMade CHAR_PLUS =       new CharInfoHumanMade('+',0.6f);
    public static final CharInfoHumanMade CHAR_SLASH_FWD =  new CharInfoHumanMade('/',0.6f,0f,0f, 1f, 1f);
    public static final CharInfoHumanMade CHAR_SLACK_BK =   new CharInfoHumanMade('\\',0.6f,0f,0f, 1f, -1f);
    public static final CharInfoHumanMade CHAR_QUOT =       new CharInfoHumanMade('\'',0.3f,0f,0.9f);
    public static final CharInfoHumanMade CHAR_QUOTDOUBLE = new CharInfoHumanMade('"',0.4f,0f,0.9f);
    public static final CharInfoHumanMade CHAR_BAR =        new CharInfoHumanMade('|',0.6f,0f,0f,0f,1f);
    public static final CharInfoHumanMade[] getSimpleCharset() {
        return new CharInfoHumanMade[] {
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

    public CharInfoHumanMade(char character, float density) {
        this.character = character;
        this.density = density;
        this.focusX = 0;
        this.focusY = 0;
        this.anisotropicX = 0;
        this.anisotropicY = 0;
    }
    public CharInfoHumanMade(char character, float density, float focusX, float focusY) {
        this.character = character;
        this.density = density;
        this.focusX = focusX;
        this.focusY = focusY;
        this.anisotropicX = 0;
        this.anisotropicY = 0;
    }
    public CharInfoHumanMade(char character, float density, float focusX, float focusY, float anisotropicX, float anisotropicY) {
        this.character = character;
        this.density = density;
        this.focusX = focusX;
        this.focusY = focusY;
        this.anisotropicX = anisotropicX;
        this.anisotropicY = anisotropicY;
    }

    private float sample(int i, int j, int li, int lj) {
        float x = li > 1 ? i / (li - 1f) * 2 - 1 : 0f;
        float y = lj > 1 ? j / (lj - 1f) * 2 - 1 : 0f;
        float anisotropy = 1 - Math.abs(x*anisotropicY - y*anisotropicX);
        float focus = x*focusX + y*focusY;
        return Math.min(1,Math.max(0, anisotropy * (focus + 1) * density));
    }
    private float sampleCache(int i, int j, int li, int lj) {
        if (li == 0 || lj == 0) return 0f;
        if (cachedWidth != li || cachedHeight != lj) this.cache(li,lj);
        return this.cache[i][j];
    }

    /**
     * See how well a given luminosity map matches this <code>CharInfo</code>.
     * @param data The luminosity map.
     * @return How badly the values match. (eg. 0 -> nearly perfect match; 1 -> pretty bad match)
     */
    @Override
    public float test(double[][] data) {
        int amount = 0; float sum = 0;
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                sum += Math.abs(data[i][j] - this.sampleCache(i,j,data.length,data[i].length));
                amount++;
            }
        }
        return amount > 0 ? sum/amount : Float.POSITIVE_INFINITY;
    }

    private int cachedWidth = 0; private int cachedHeight = 0;
    private float[][] cache = null;
    /**
     * Cache the computed values of this <code>CharInfo</code> to a bitmap.
     * @param w The width of the bitmap. (Should be equal to the sampleWidth of the ISC you put this into.)
     * @param h The height of the bitmap. (Should be equal to the sampleHeight of the ISC you put this into.)
     */
    @Override
    public void cache(int w, int h) {
        if (cachedWidth == w && cachedHeight == h) return;
        cachedWidth = w; cachedHeight = h;
        this.cache = new float[w][h];
        for (int i = 0; i < w; i++)
            for (int j = 0; j < h; j++)
                cache[i][j] = this.sample(i,j,w,h);
    }

    /**
     * Get the text character associated with this <code>CharInfo</code>.
     * @return The character for this <code>CharInfo</code>
     */
    @Override
    public char getChar() {
        return character;
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
