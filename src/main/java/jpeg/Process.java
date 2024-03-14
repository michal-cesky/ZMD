package jpeg;

import Jama.Matrix;
import enums.ColorType;
import enums.SamplingType;
import graphics.Dialogs;

import java.awt.*;
import java.awt.image.BufferedImage;

import static core.Helper.checkValue;

public class Process {

    private BufferedImage originalImage;

    private static int [][] originalRed;
    private static int [][] modifiedRed;
    private static int [][] originalGreen;
    private static int [][] modifiedGreen;
    private static int [][] originalBlue;
    private static int [][] modifiedBlue;

    private static Matrix originalY;
    private static Matrix modifiedY;
    private static Matrix originalCb;
    private static Matrix modifiedCb;
    private static Matrix originalCr;
    private static Matrix modifiedCr;

    private int imageHeight;
    private int imageWidth;

    public Process(String pathToIImage) {
        init(Dialogs.loadImageFromPath(pathToIImage));
    }

    private void init(BufferedImage originalImage) {
        this.originalImage = originalImage;

        imageHeight = originalImage.getHeight();
        imageWidth = originalImage.getWidth();

        initMatrix();
        setOriginalRGB();
    }

    private void initMatrix() {
        originalRed = new int [imageHeight][imageWidth];
        originalGreen = new int [imageHeight][imageWidth];
        originalBlue = new int [imageHeight][imageWidth];

        originalY = new Matrix(imageHeight, imageWidth);
        originalCb = new Matrix(imageHeight, imageWidth);
        originalCr = new Matrix(imageHeight, imageWidth);

        modifiedRed = new int [imageHeight][imageWidth];
        modifiedGreen = new int [imageHeight][imageWidth];
        modifiedBlue = new int [imageHeight][imageWidth];

        modifiedY = new Matrix(imageHeight, imageWidth);
        modifiedCb = new Matrix(imageHeight, imageWidth);
        modifiedCr = new Matrix(imageHeight, imageWidth);
    }
    private void setOriginalRGB() {
        for (int h = 0; h < imageHeight; h++) {
            for (int w = 0; w < imageWidth; w++) {
                Color color = new Color(originalImage.getRGB(w, h));
                originalRed[h][w] = color.getRed();
                originalGreen[h][w] = color.getGreen();
                originalBlue[h][w] = color.getBlue();
            }
        }
    }

    public BufferedImage getImageFromModifiedRGB() {
        BufferedImage bfImage = new BufferedImage(
                imageWidth, imageHeight,
                BufferedImage.TYPE_INT_RGB);

        for (int h = 0; h < imageHeight; h++) {
            for (int w = 0; w < imageWidth; w++) {
                bfImage.setRGB(w, h,
                        (new Color(modifiedRed[h][w],
                                modifiedGreen[h][w],
                                modifiedBlue[h][w])).getRGB());
            }
        }
        return bfImage;
    }

    public void convertRGBToYCbCr(){
        Matrix [] colors = ColorTransform.convertOriginalRGBtoYcBcR(originalRed, originalGreen, originalBlue);
        originalY = colors[0];
        originalCb = colors[1];
        originalCr = colors[2];

        modifiedY = originalY.copy();
        modifiedCb = originalCb.copy();
        modifiedCr = originalCr.copy();
    }

    public void convertYCbCrToRGB(){
        var colors = ColorTransform.convertModifiedYcBcRtoRGB(modifiedY, modifiedCb, modifiedCr);
        modifiedRed = colors[0];
        modifiedGreen = colors[1];
        modifiedBlue = colors[2];
    }

    public BufferedImage getOneColorImageFromRGB(int [][] color, ColorType type, boolean greyScale) {
        int height = color.length, width = color[0].length;

        BufferedImage bfImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                int pixel = color[h][w];
                bfImage.setRGB(w, h,
                        (new Color(( (type==ColorType.RED || greyScale)?pixel:0),
                                ( (type==ColorType.GREEN || greyScale)?pixel:0),
                                ( (type==ColorType.BLUE || greyScale)?pixel:0))).getRGB());
            }
        }
        return bfImage;
    }

    public BufferedImage getOneColorImageFromYCbCr(Matrix color) {
        int height = color.getRowDimension(), width = color.getColumnDimension();

        BufferedImage bfImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                int pixel = checkValue(color.get(h, w));
                bfImage.setRGB(w, h,(new Color(pixel, pixel, pixel)).getRGB());
            }
        }
        return bfImage;
    }


    public BufferedImage getOriginalImage() {
        return originalImage;
    }
    public static int[][] getOriginalRed() {
        return originalRed;
    }
    public static int[][] getModifiedRed() {
        return modifiedRed;
    }
    public static int[][] getOriginalGreen() {
        return originalGreen;
    }
    public static int[][] getModifiedGreen() {
        return modifiedGreen;
    }
    public static int[][] getOriginalBlue() {
        return originalBlue;
    }
    public static int[][] getModifiedBlue() {
        return modifiedBlue;
    }
    public static Matrix getOriginalY() {return originalY; }
    public static Matrix getModifiedY() {
        return modifiedY;
    }
    public static Matrix getOriginalCb() {
        return originalCb;
    }
    public static Matrix getModifiedCb() {
        return modifiedCb;
    }
    public static Matrix getOriginalCr() {
        return originalCr;
    }
    public static Matrix getModifiedCr() {
        return modifiedCr;
    }
    public int getImageHeight() {
        return imageHeight;
    }
    public int getImageWidth() {
        return imageWidth;
    }

    public static void setModifiedY(Matrix modifiedY) {
        Process.modifiedY = modifiedY;
    }

    public static void setModifiedCb(Matrix modifiedCb) {
        Process.modifiedCb = modifiedCb;
    }

    public static void setModifiedCr(Matrix modifiedCr) {
        Process.modifiedCr = modifiedCr;
    }
}
