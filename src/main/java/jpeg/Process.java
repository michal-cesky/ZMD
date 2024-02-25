package jpeg;

import Jama.Matrix;
import enums.ColorType;
import graphics.Dialogs;

import java.awt.*;
import java.awt.image.BufferedImage;

import static core.Helper.checkValue;

public class Process {

    private BufferedImage originalImage;

    private int [][] originalRed, modifiedRed;
    private int [][] originalGreen, modifiedGreen;
    private int [][] originalBlue, modifiedBlue;

    private Matrix originalY, modifiedY;
    private Matrix originalCb, modifiedCb;
    private Matrix originalCr, modifiedCr;

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
    public int[][] getOriginalRed() {
        return originalRed;
    }
    public int[][] getModifiedRed() {
        return modifiedRed;
    }
    public int[][] getOriginalGreen() {
        return originalGreen;
    }
    public int[][] getModifiedGreen() {
        return modifiedGreen;
    }
    public int[][] getOriginalBlue() {
        return originalBlue;
    }
    public int[][] getModifiedBlue() {
        return modifiedBlue;
    }
    public Matrix getOriginalY() {
        return originalY;
    }
    public Matrix getModifiedY() {
        return modifiedY;
    }
    public Matrix getOriginalCb() {
        return originalCb;
    }
    public Matrix getModifiedCb() {
        return modifiedCb;
    }
    public Matrix getOriginalCr() {
        return originalCr;
    }
    public Matrix getModifiedCr() {
        return modifiedCr;
    }
    public int getImageHeight() {
        return imageHeight;
    }
    public int getImageWidth() {
        return imageWidth;
    }
}
