package jpeg;

import Jama.Matrix;
import enums.PsnrType;
import enums.PsnrTypeMethod;
import enums.SsimType;
import enums.SsimTypeMtehod;

import java.util.ArrayList;
import java.util.List;

public class Quality {

    public static double countMSE(double[][] original, double[][] modified) {
        double mse = 0;

        int width = original.length;
        int height = original[0].length;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                mse += Math.pow(original[i][j] - modified[i][j], 2);
            }
        }
        return mse / (width * height);
    }

    public static double countMAE(double[][] original, double[][] modified) {
        double mae = 0;

        int width = original.length;
        int height = original[0].length;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                mae += Math.abs(original[i][j] - modified[i][j]);
            }
        }
        return mae / (width * height);
    }

    public static double countSAE(double[][] original, double[][] modified) {
        double sae = 0;

        int width = original.length;
        int height = original[0].length;

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                sae += Math.abs(original[i][j] - modified[i][j]);
            }
        }
        return sae;
    }

    public static double countPSNR(double MSE) {
        double psnr = 0;

        psnr = 10 * Math.log10(Math.pow(Math.pow(2, 8) - 1, 2) / MSE);

        return psnr;
    }

    public static double countPSNRforRGB(double mseRed, double mseGreen, double mseBlue) {
        double psnrRGB = 0;
        double x = (mseRed + mseBlue + mseGreen) / 3;

        psnrRGB = 10 * Math.log10(Math.pow(Math.pow(2, 8) - 1, 2) / x);

        return psnrRGB;
    }

    public static double countPSNRforYCbCr(double mseY, double mseCb, double mseCr) {
        double psnrRGB = 0;
        double x = (mseY + mseCb + mseCr) /3;

        psnrRGB = 10 * Math.log10(Math.pow(Math.pow(2, 8) - 1, 2) / x);

        return psnrRGB;
    }

    public static double countSSIM(Matrix original, Matrix modified) {

        double MIx = 0;
        double MIy = 0;

        double Sigmax = 0;
        double Sigmay = 0;
        double Sigmaxy = 0;

        double C1 = Math.pow((0.01 * 255), 2);
        double C2 = Math.pow((0.03 * 255), 2);
;
        double n = original.getRowDimension() * original.getColumnDimension();

        for (int i = 0; i < original.getRowDimension(); i++){
            for (int t = 0; t < original.getColumnDimension(); t++){
                MIx += original.get(i, t);
            }
        }
        MIx = MIx / n;

        for (int i = 0; i < modified.getRowDimension(); i++){
            for (int t = 0; t < modified.getColumnDimension(); t++){
                MIy += modified.get(i, t);
            }
        }
        MIy = MIy / n;

        for (int i = 0; i < original.getRowDimension(); i++){
            for (int t = 0; t < original.getColumnDimension(); t++){
                Sigmax += Math.pow((original.get(i, t) - MIx), 2);
            }
        }
        Sigmax = Math.sqrt(Sigmax / (n - 1));

        for (int i = 0; i < modified.getRowDimension(); i++){
            for (int t = 0; t < modified.getColumnDimension(); t++){
                Sigmay += Math.pow((modified.get(i, t) - MIy), 2);
            }
        }
        Sigmay = Math.sqrt(Sigmay / (n - 1));

        for (int i = 0; i < original.getRowDimension(); i++){
            for (int t = 0; t < original.getColumnDimension(); t++){
                Sigmaxy += (original.get(i, t) - MIx) * (modified.get(i, t) - MIy);
            }
        }
        Sigmaxy = Sigmaxy / (n - 1);

        double ssim = ((2 * MIx * MIy + C1) * (2 * Sigmaxy + C2)) / ((Math.pow(MIx, 2) + Math.pow(MIy, 2) + C1) * (Math.pow(Sigmax, 2) + Math.pow(Sigmay, 2) + C2));

        return ssim;
    }

    public static double countMSSIM(Matrix original, Matrix modified) {
        double mssim = 0;

        int blockSize = 8;
        List<Double> ssimValues = new ArrayList<>();

        int numRows = original.getRowDimension();
        int numCols = original.getColumnDimension();

        for (int i = 0; i < numRows; i += blockSize) {
            for (int j = 0; j < numCols; j += blockSize) {

                Matrix originalBlock = original.getMatrix(i, i + blockSize - 1, j, j + blockSize - 1);
                Matrix modifiedBlock = modified.getMatrix(i, i + blockSize - 1, j, j + blockSize - 1);

                double ssim = countSSIM(originalBlock, modifiedBlock);
                ssimValues.add(ssim);
            }
        }

        for (Double d: ssimValues) {
            mssim += d;
        }

        return mssim / ssimValues.size();
    }

    public static double[][] convertIntToDouble(int[][] intArray) {
        double[][] doubleArray = new double[intArray.length][intArray[0].length];
        for (int i = 0; i < intArray.length; i++) {
            for (int j = 0; j < intArray[0].length; j++) {
                doubleArray[i][j] = (double) intArray[i][j];
            }
        }
        return doubleArray;
    }

    public static double Quality_1(PsnrType psnrType, PsnrTypeMethod psnrTypeMethod) {
        switch (psnrType) {
            case Red:
                switch (psnrTypeMethod) {
                    case MAE:
                        return countMAE(convertIntToDouble(Process.getOriginalRed()), convertIntToDouble(Process.getModifiedRed()));
                    case MSE:
                        return countMSE(convertIntToDouble(Process.getOriginalRed()), convertIntToDouble(Process.getModifiedRed()));
                    case PSNR:
                        return countPSNR(countMSE(convertIntToDouble(Process.getOriginalRed()), convertIntToDouble(Process.getModifiedRed())));
                    case SAE:
                        return countSAE(convertIntToDouble(Process.getOriginalRed()), convertIntToDouble(Process.getModifiedRed()));}
            case Blue:
                switch (psnrTypeMethod) {
                    case MAE:
                        return countMAE(convertIntToDouble(Process.getOriginalBlue()), convertIntToDouble(Process.getModifiedBlue()));
                    case MSE:
                        return countMSE(convertIntToDouble(Process.getOriginalBlue()), convertIntToDouble(Process.getModifiedBlue()));
                    case PSNR:
                        return countPSNR(countMSE(convertIntToDouble(Process.getOriginalBlue()), convertIntToDouble(Process.getModifiedBlue())));
                    case SAE:
                        return countSAE(convertIntToDouble(Process.getOriginalBlue()), convertIntToDouble(Process.getModifiedBlue()));}
            case Green:
                switch (psnrTypeMethod) {
                    case MAE:
                        return countMAE(convertIntToDouble(Process.getOriginalGreen()), convertIntToDouble(Process.getModifiedGreen()));
                    case MSE:
                        return countMSE(convertIntToDouble(Process.getOriginalGreen()), convertIntToDouble(Process.getModifiedBlue()));
                    case PSNR:
                        return countPSNR(countMSE(convertIntToDouble(Process.getOriginalGreen()), convertIntToDouble(Process.getModifiedBlue())));
                    case SAE:
                        return countSAE(convertIntToDouble(Process.getOriginalGreen()), convertIntToDouble(Process.getModifiedBlue()));}
            case RGB:
                switch (psnrTypeMethod) {
                    case MAE:
                        double Rmae = countMAE(convertIntToDouble(Process.getOriginalRed()), convertIntToDouble(Process.getModifiedRed()));
                        double Gmae = countMAE(convertIntToDouble(Process.getOriginalGreen()), convertIntToDouble(Process.getModifiedGreen()));
                        double Bmae = countMAE(convertIntToDouble(Process.getOriginalBlue()), convertIntToDouble(Process.getModifiedBlue()));
                        return (Rmae + Gmae + Bmae) / 3;
                    case MSE:
                        double Rmse = countMSE(convertIntToDouble(Process.getOriginalRed()), convertIntToDouble(Process.getModifiedRed()));
                        double Gmse = countMSE(convertIntToDouble(Process.getOriginalGreen()), convertIntToDouble(Process.getModifiedGreen()));
                        double Bmse = countMSE(convertIntToDouble(Process.getOriginalBlue()), convertIntToDouble(Process.getModifiedBlue()));
                        return (Rmse + Gmse + Bmse) / 3;
                    case PSNR:
                        return countPSNRforRGB(countMSE(convertIntToDouble(Process.getOriginalRed()), convertIntToDouble(Process.getModifiedRed())), countMSE(convertIntToDouble(Process.getOriginalGreen()), convertIntToDouble(Process.getModifiedGreen())), countMSE(convertIntToDouble(Process.getOriginalBlue()), convertIntToDouble(Process.getModifiedBlue())));
                    case SAE:
                        double Rsae = countSAE(convertIntToDouble(Process.getOriginalRed()), convertIntToDouble(Process.getModifiedRed()));
                        double Gsae = countSAE(convertIntToDouble(Process.getOriginalGreen()), convertIntToDouble(Process.getModifiedGreen()));
                        double Bsae = countSAE(convertIntToDouble(Process.getOriginalBlue()), convertIntToDouble(Process.getModifiedBlue()));
                        return (Rsae + Gsae + Bsae) / 3;}
            case Y:
                switch (psnrTypeMethod){
                    case MAE:
                        return countMAE(Process.getModifiedY().getArray(), Process.getModifiedY().getArray());
                    case MSE:
                        return countMSE(Process.getModifiedY().getArray(), Process.getModifiedY().getArray());
                    case PSNR:
                        return countPSNR(countMSE(Process.getModifiedY().getArray(), Process.getModifiedY().getArray()));
                    case SAE:
                        return countSAE(Process.getModifiedY().getArray(), Process.getModifiedY().getArray());}
            case Cb:
                switch (psnrTypeMethod){
                    case MAE:
                        return countMAE(Process.getOriginalCb().getArray(), Process.getModifiedCb().getArray());
                    case MSE:
                        return countMSE(Process.getOriginalCb().getArray(), Process.getModifiedCb().getArray());
                    case PSNR:
                        return countPSNR(countMSE(Process.getOriginalCb().getArray(), Process.getModifiedCb().getArray()));
                    case SAE:
                        return countSAE(Process.getOriginalCb().getArray(), Process.getModifiedCb().getArray());}
            case Cr:
                switch (psnrTypeMethod){
                    case MAE:
                        return countMAE(Process.getOriginalCr().getArray(), Process.getModifiedCr().getArray());
                    case MSE:
                        return countMSE(Process.getOriginalCr().getArray(), Process.getModifiedCr().getArray());
                    case PSNR:
                        return countPSNR(countMSE(Process.getOriginalCr().getArray(), Process.getModifiedCr().getArray()));
                    case SAE:
                        return countSAE(Process.getOriginalCr().getArray(), Process.getModifiedCr().getArray());}
            case YCbCr:
                switch (psnrTypeMethod) {
                    case MAE:
                        double Ymae = countMAE(Process.getModifiedY().getArray(), Process.getModifiedY().getArray());
                        double Cbmae = countMAE(Process.getOriginalCb().getArray(), Process.getModifiedCb().getArray());
                        double Crmae = countMAE(Process.getOriginalCr().getArray(), Process.getModifiedCr().getArray());
                        return (Ymae + Cbmae + Crmae) / 3;
                    case MSE:
                        double Ymse = countMSE(Process.getModifiedY().getArray(), Process.getModifiedY().getArray());
                        double Cbmse = countMSE(Process.getOriginalCb().getArray(), Process.getModifiedCb().getArray());
                        double Crmse = countMSE(Process.getOriginalCr().getArray(), Process.getModifiedCr().getArray());
                        return (Ymse + Cbmse + Crmse) / 3;
                    case PSNR:
                        return countPSNRforYCbCr(countMSE(Process.getModifiedY().getArray(), Process.getModifiedY().getArray()), countMSE(Process.getOriginalCb().getArray(), Process.getModifiedCb().getArray()), countMSE(Process.getOriginalCr().getArray(), Process.getModifiedCr().getArray()));
                    case SAE:
                        double Ysae = countSAE(Process.getModifiedY().getArray(), Process.getModifiedY().getArray());
                        double Cbsae = countSAE(Process.getOriginalCb().getArray(), Process.getModifiedCb().getArray());
                        double Crsae = countSAE(Process.getOriginalCr().getArray(), Process.getModifiedCr().getArray());
                        return (Ysae + Cbsae + Crsae) / 3;}
        }
        return 0;
    }

    public static double Quality_2(SsimType ssimType, SsimTypeMtehod ssimTypeMethod) {
        switch (ssimType) {
            case Y:
                switch (ssimTypeMethod){
                    case SSIM:
                        return countSSIM(Process.getOriginalY(), Process.getModifiedY());
                    case MSSIM:
                        return countMSSIM(Process.getOriginalY(), Process.getModifiedY());}
            case Cb:
                switch (ssimTypeMethod){
                    case SSIM:
                        return countSSIM(Process.getOriginalCb(), Process.getModifiedCb());
                    case MSSIM:
                        return countMSSIM(Process.getOriginalCb(), Process.getModifiedCb());}
            case Cr:
                switch (ssimTypeMethod){
                    case SSIM:
                        return countSSIM(Process.getOriginalCr(), Process.getModifiedCr());
                    case MSSIM:
                        return countMSSIM(Process.getOriginalCr(), Process.getModifiedCr());}
        }
        return 0;
    }
}
