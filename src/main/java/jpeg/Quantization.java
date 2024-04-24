package jpeg;

import Jama.Matrix;

public class Quantization {

    public static Matrix quantize(Matrix input, int blockSize, double quality, boolean matrixY) {
        int height = input.getRowDimension();
        int width = input.getColumnDimension();

        Matrix output = new Matrix(height, width);
        Matrix quantizationMatrix = getQuantizationMatrix(blockSize, quality, matrixY);

        for (int i = 0; i < height; i++){
            for (int j = 0; j < width; j++){
                double indexInputValue = input.get(i,j);
                double indexQMatrixValue = quantizationMatrix.get(i % blockSize, j % blockSize);
                double Squv = indexInputValue / indexQMatrixValue;


                double Valueround;
                if (Squv >= -0.2 && Squv <= 0.2) {
                    Valueround = Math.round(Squv * 100.0) / 100.0;
                } else {
                    Valueround = Math.round(Squv * 10.0) / 10.0;
                }

                output.set(i, j, Valueround);
            }
        }
        return output;
    }

    public static Matrix inverseQuantize(Matrix input, int blockSize, double quality, boolean matrixY) {
        int height = input.getRowDimension();
        int width = input.getColumnDimension();

        Matrix output = new Matrix(height, width);
        Matrix quantizationMatrix = getQuantizationMatrix(blockSize, quality, matrixY);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                double indexInputValue = input.get(i, j);
                double indexQMatrixValue = quantizationMatrix.get(i % blockSize, j % blockSize);
                double Squv = indexInputValue * indexQMatrixValue;

                output.set(i, j, Squv);
            }
        }
        return output;
    }

    public static Matrix getQuantizationMatrix(int blockSize, double quality, boolean matrixY) {
        Matrix quantizationMatrix = new Matrix(blockSize, blockSize);

        double Alpha;
        double[][] QUmatrixArray;

        if(quality == 100){
            double[][] c = new double[blockSize][blockSize];
            for (int h = 0; h < blockSize; h++){
                for (int w = 0; w < blockSize; w++){
                    c[h][w] = 1.0;
                }
            }
            return new Matrix(c);
        }

        if (quality <= 50) {
            Alpha = 50.0 / quality;
        } else {
            Alpha = 2.0 - (2.0 * quality / 100.0);
        }

        if(matrixY){
            QUmatrixArray = getQuantizationMatrix8Y();
        }
        else{
            QUmatrixArray = getQuantizationMatrix8C();
        }

        int QUMArraylength = QUmatrixArray.length;
        double[][] DataArray = new double[QUMArraylength][QUMArraylength];

        for (int i = 0; i < QUMArraylength; i++) {
            for (int j = 0; j < QUMArraylength; j++) {
                DataArray[i][j] = QUmatrixArray[i][j] * Alpha;
            }
        }

        Matrix Matrix = new Matrix(DataArray);

        if (blockSize != QUMArraylength && !(blockSize == 8 && quality == 50)) {
            return changeMatrixSize(Matrix, blockSize);
        }
        return Matrix;
    }

    private static Matrix changeMatrixSize(Matrix matrix, int blockSize){
        int size = matrix.getRowDimension();
        double[][] newMatrixArray = new double[blockSize][blockSize];

        for (int i = 0; i < blockSize; i++){
            for (int j = 0; j < blockSize; j++){
                newMatrixArray[i][j] = matrix.get((i * size) / blockSize, (j * size) / blockSize);
            }
        }
        return new Matrix(newMatrixArray);
    }

    private static final double[][] quantizationMatrix8Y = {
            {16, 11, 10, 16, 24, 40, 51, 61},
            {12, 12, 14, 19, 26, 58, 60, 55},
            {14, 13, 16, 24, 40, 57, 69, 56},
            {14, 17, 22, 29, 51, 87, 80, 62},
            {18, 22, 37, 56, 68, 109, 103, 77},
            {24, 35, 55, 64, 81, 104, 113, 92},
            {49, 64, 78, 87, 103, 121, 120, 101},
            {72, 92, 95, 98, 112, 100, 103, 99}};

    private static final double[][] quantizationMatrix8C = {
            {17, 18, 24, 47, 99, 99, 99, 99},
            {18, 21, 26, 66, 99, 99, 99, 99},
            {24, 26, 56, 99, 99, 99, 99, 99},
            {47, 66, 99, 99, 99, 99, 99, 99},
            {99, 99, 99, 99, 99, 99, 99, 99},
            {99, 99, 99, 99, 99, 99, 99, 99},
            {99, 99, 99, 99, 99, 99, 99, 99},
            {99, 99, 99, 99, 99, 99, 99, 99}};

    public static double [][] getQuantizationMatrix8Y() {
        return quantizationMatrix8Y;
    }

    public static double [][] getQuantizationMatrix8C() {
        return quantizationMatrix8C;
    }
}

