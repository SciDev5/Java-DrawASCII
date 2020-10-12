package me.scidev5.drawASCII.charInfo;

public interface CharInfo {
    /**
     * Get the text character associated with this <code>CharInfo</code>.
     * @return The character for this <code>CharInfo</code>
     */
    public char getChar();
    /**
     * Test this <code>CharInfo</code> against a section of an image.
     * @param map The section of the image to compare with.
     * @return A score of how badly the image matches (0: perfect match; 1+: bad match)
     */
    public float test(double[][] map);
    /**
     * Cache the computed values of this <code>CharInfo</code> to a bitmap.
     * @param w The width of the bitmap. (Should be equal to the sampleWidth of the ISC you put this into.)
     * @param h The height of the bitmap. (Should be equal to the sampleHeight of the ISC you put this into.)
     */
    public void cache(int w, int h);
}
