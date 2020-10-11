package me.scidev5.drawASCII.util;

public class ArrayUtils {

    public static <T> T[][] getChunkOf2dArr(T[][] largeArr, int x, int y, int w, int h) {
        T[][] cutArr = (T[][]) new Object[w][h];
        for (int i = 0; i < w; i++)
            for (int j = 0; j < h; j++)
                cutArr[i][j] = (T)largeArr[i+x][j+y];
        return cutArr;
    }
    public static double[][] getChunkOf2dArr(double[][] largeArr, int x, int y, int w, int h) {
        double[][] cutArr = new double[w][h];
        for (int i = 0; i < w; i++)
            for (int j = 0; j < h; j++)
                cutArr[i][j] = largeArr[i+x][j+y];
        return cutArr;
    }

}
