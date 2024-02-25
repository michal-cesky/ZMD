package jpeg;

import Jama.Matrix;

public class ColorTransform {

    public static Matrix[] convertOriginalRGBtoYcBcR(int[][] red, int[][] green, int[][] blue) {

        Matrix Y = new Matrix(red.length, red[0].length);
        Matrix Cb = new Matrix(red.length, red[0].length);
        Matrix Cr = new Matrix(red.length, red[0].length);

        for (int i = 0; i < red.length; i++) {
            for (int j = 0; j < red[0].length; j++) {
                Y.set(i, j, 0.257 * red[i][j] + 0.504 * green[i][j] + 0.098 * blue[i][j] + 16);
                Cb.set(i, j, -0.148 * red[i][j] - 0.291 * green[i][j] + 0.439 * blue[i][j] + 128);
                Cr.set(i, j, 0.439 * red[i][j] - 0.368 * green[i][j] - 0.071 * blue[i][j] + 128);
            }
        }
        return new Matrix[]{Y, Cb, Cr};
    }

    public static int[][][] convertModifiedYcBcRtoRGB(Matrix Y, Matrix Cb, Matrix Cr) {

        int[][] Red = new int[Y.getColumnDimension()][Y.getRowDimension()];
        int[][] Green = new int[Y.getColumnDimension()][Y.getRowDimension()];;
        int[][] Blue = new int[Y.getColumnDimension()][Y.getRowDimension()];;

        for (int i = 0; i < Y.getColumnDimension(); i++) {
            for (int j = 0; j < Y.getRowDimension(); j++) {
                int red = Math.round((float)(1.164*(Y.get(i,j)-16) + 1.596*(Cr.get(i,j)-128)));
                red = red > 255 ? 255 : red;
                red = red < 0 ? 0 : red;

                int green = Math.round((float)((1.164*(Y.get(i,j) - 16) - 0.813*(Cr.get(i,j)-128)) - 0.391*(Cb.get(i,j) -128)));
                green = green > 255 ? 255 : green;
                green = green < 0 ? 0 : green;

                int blue = Math.round((float)(1.164*(Y.get(i,j) -16) + 2.018*(Cb.get(i,j)-128)));
                blue = blue > 255 ? 255 : blue;
                blue = blue < 0 ? 0 : blue;

                Red[i][j] = red;
                Green[i][j] = green;
                Blue[i][j] = blue;
            }
        }
        return new int[][][]{Red, Green, Blue};
    }

}
