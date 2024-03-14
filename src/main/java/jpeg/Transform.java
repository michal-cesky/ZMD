package jpeg;

import Jama.Matrix;
import enums.TransformType;

public class Transform {

    public static Matrix transform(Matrix input, TransformType type, int blockSize) {
        Matrix At = getTransformMatrix(type, blockSize);
        Matrix matrix = new Matrix(input.getRowDimension(), input.getColumnDimension());

        double height = input.getRowDimension();
        double width = input.getColumnDimension();

        for (int h = 0; h < height; h += blockSize){
            for (int w = 0; w < width; w += blockSize){
                matrix.setMatrix(h, h + blockSize - 1 , w, w + blockSize - 1, Transform.countTransform(At, input.getMatrix(h, h + blockSize - 1, w, w + blockSize - 1)));
            }
        }
        return matrix;
    }

    public static Matrix inverseTransform(Matrix input, TransformType type, int blockSize){
        Matrix At = getTransformMatrix(type, blockSize);
        Matrix matrix = new Matrix(input.getRowDimension(), input.getColumnDimension());

        double height = input.getRowDimension();
        double width = input.getColumnDimension();

        for (int h = 0; h < height; h += blockSize){
            for (int w = 0; w < width; w += blockSize){
                matrix.setMatrix(h, h + blockSize - 1 , w, w + blockSize - 1, Transform.countInverseTransform(At, input.getMatrix(h, h + blockSize - 1, w, w + blockSize - 1)));
            }
        }
        return matrix;
    }

    public static Matrix getTransformMatrix(TransformType type, int blockSize){
        Matrix Matrix = new Matrix(blockSize, blockSize);

        switch (type){
            case DCT:
                double c;
                for (int i = 0; i < blockSize; i++) {
                    for (int j = 0; j < blockSize; j++) {
                        if (i == 0) {
                            Matrix.set(i, j, Math.sqrt(1.0 / blockSize));
                        }
                        else {
                            c = Math.sqrt( 2.0 / (double)blockSize) * Math.cos((Math.PI * (2 * j + 1) * i) / (2 * (double)blockSize));
                            Matrix.set(i, j, c);
                        }
                    }
                }
                return Matrix;
            case WHT:
                blockSize = (int) (Math.log(blockSize) / Math.log(2));
                Matrix oldMatrix = new Matrix(1, 1, 1);
                if (blockSize != 0) {
                    for (int i = 1; i <= blockSize; i++) {
                        Matrix = new Matrix((int) Math.pow(2, i), (int) Math.pow(2, i));
                        int pomMax = (int) Math.pow(2, i);
                        Matrix.setMatrix(0, pomMax / 2 - 1, 0, pomMax / 2 - 1, oldMatrix);
                        Matrix.setMatrix(0, pomMax / 2 - 1 , pomMax / 2, pomMax - 1 , oldMatrix);
                        Matrix.setMatrix(pomMax / 2, pomMax - 1, 0, pomMax / 2 - 1, oldMatrix);
                        Matrix.setMatrix(pomMax / 2, pomMax - 1, pomMax / 2, pomMax - 1, oldMatrix.times(-1));
                        oldMatrix = Matrix.copy();
                    }
                }
                Matrix = oldMatrix.times(1 / Math.sqrt(Math.pow(2, blockSize)));
        }
        return Matrix;
    }

    private static Matrix countTransform(Matrix At, Matrix input) {

        return At.times(input).times(At.transpose());
    }

    private static Matrix countInverseTransform(Matrix At, Matrix input) {

        return At.transpose().times(input).times(At);
    }
}
