package me.scidev5.drawASCII.util;

public class ArrayUtils {

    /*
    public static <T> T[][] getChunkOf2dArr(T[][] largeArr, int x, int y, int w, int h) {
        T[][] cutArr = (T[][]) new Object[w][h];
        for (int i = 0; i < w; i++)
            for (int j = 0; j < h; j++)
                cutArr[i][j] = (T)largeArr[i+x][j+y];
        return cutArr;
    }*/

    /**
     * Internal utility used to cut a given rectangle out of a 2d array.
     * @param largeArr The array to cut from.
     * @param x start x
     * @param y start y
     * @param w width
     * @param h height
     * @return <code>largeArr[x:x+w][y:y+w]</code>
     */
    public static double[][] getChunkOf2dArr(double[][] largeArr, int x, int y, int w, int h) {
        double[][] cutArr = new double[w][h];
        for (int i = 0; i < w; i++)
            if (h >= 0) System.arraycopy(largeArr[i + x], y, cutArr[i], 0, h);
        return cutArr;
    }

}
