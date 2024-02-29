package jpeg;

import Jama.Matrix;
import enums.SamplingType;

public class Sampling {

    public static Matrix sampleDown(Matrix inputMatrix, SamplingType samplingType){

        Matrix sampledMatrix = null;

        switch (samplingType) {
            case S_4_4_4:
                return inputMatrix;
            case S_4_2_2:
                sampledMatrix = downSample(inputMatrix);
                break;
            case S_4_2_0:
                sampledMatrix = downSample(inputMatrix);
                sampledMatrix = sampledMatrix.transpose();
                sampledMatrix = downSample(sampledMatrix);
                sampledMatrix = sampledMatrix.transpose();
                break;
            case S_4_1_1:
                sampledMatrix = downSample(inputMatrix);
                sampledMatrix = downSample(sampledMatrix);
                break;
        }
        return sampledMatrix;
    }

    public static Matrix sampleUp(Matrix inputMatrix, SamplingType samplingType){

        Matrix sampledMatrix = null;

        switch (samplingType) {
            case S_4_4_4:
                return inputMatrix;
            case S_4_2_2:
                sampledMatrix = upSample(inputMatrix);
                break;
            case S_4_2_0:
                sampledMatrix = upSample(inputMatrix);
                sampledMatrix = sampledMatrix.transpose();
                sampledMatrix = upSample(sampledMatrix);
                sampledMatrix = sampledMatrix.transpose();
                break;
            case S_4_1_1:
                sampledMatrix = upSample(inputMatrix);
                sampledMatrix = upSample(sampledMatrix);
                break;
        }
        return sampledMatrix;
    }

    public static Matrix downSample(Matrix mat) {
        Matrix newMatrix = new Matrix(mat.getRowDimension(), mat.getColumnDimension() / 2);
        for (int i = 0; i < mat.getColumnDimension(); i += 2) {
            Matrix submatrix = mat.getMatrix(0, mat.getRowDimension() - 1, i, i);
            newMatrix.setMatrix(0, mat.getRowDimension() - 1, i / 2, i / 2, submatrix);
        }
        return newMatrix;
    }

    public static Matrix upSample(Matrix mat) {
        Matrix newMatrix = new Matrix(mat.getRowDimension(), mat.getColumnDimension() * 2);
        for (int i = 0; i < mat.getColumnDimension(); i++) {
            Matrix submatrix = mat.getMatrix(0, mat.getRowDimension() - 1, i, i);
            newMatrix.setMatrix(0, newMatrix.getRowDimension() - 1, i * 2, i * 2, submatrix);
            newMatrix.setMatrix(0, newMatrix.getRowDimension() - 1, (i * 2) + 1, (i * 2) + 1, submatrix);
        }
        return newMatrix;
    }
}
