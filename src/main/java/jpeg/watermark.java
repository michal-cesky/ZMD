package jpeg;

import Jama.Matrix;
import enums.Rotations;
import enums.TransformType;
import enums.YCbCrType;
import graphics.Dialogs;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class watermark {

    private BufferedImage watermarkImage;

    private static int watermarkHeight;
    private static int watermarkWidth;

    private static int [][] originalRed;
    private static int [][] originalBlue;
    private static int [][] originalGreen;

    private static int [][] modifiedRed;
    private static int [][] modifiedBlue;
    private static int [][] modifiedGreen;

    private static Matrix originalY;
    private static Matrix originalCb;
    private static Matrix originalCr;

    private static Matrix modifiedCb;
    private static Matrix modifiedY;
    private static Matrix modifiedCr;

    private static Matrix extractedMatrix;

    private static BufferedImage attackImage;
    private static BufferedImage newImage;


    private static final int KEY = 500;




    public watermark(String PathToWatermark){

        init(Dialogs.loadImageFromPath(PathToWatermark));

        setOriginalRGB(watermarkImage);
        convertRGBToYCbCr();
    }

    private void init(BufferedImage originalImage) {
        this.watermarkImage = originalImage;

        watermarkHeight = originalImage.getHeight();
        watermarkWidth = originalImage.getWidth();

        originalRed = new int [watermarkHeight][watermarkWidth];
        originalGreen = new int [watermarkHeight][watermarkWidth];
        originalBlue = new int [watermarkHeight][watermarkWidth];

        originalY = new Matrix(watermarkHeight, watermarkWidth);
        originalCb = new Matrix(watermarkHeight, watermarkWidth);
        originalCr = new Matrix(watermarkHeight, watermarkWidth);

    }

    public static void setOriginalRGB(BufferedImage x) {
        for (int h = 0; h < watermarkHeight; h++) {
            for (int w = 0; w < watermarkWidth; w++) {
                Color color = new Color(x.getRGB(w, h));
                originalRed[h][w] = color.getRed();
                originalGreen[h][w] = color.getGreen();
                originalBlue[h][w] = color.getBlue();
            }
        }
    }

    public static void convertRGBToYCbCr(){
        Matrix [] colors = ColorTransform.convertOriginalRGBtoYcBcR(originalRed, originalGreen, originalBlue);
        originalY = colors[0];
        originalCb = colors[1];
        originalCr = colors[2];

        modifiedY = originalY.copy();
        modifiedCb = originalCb.copy();
        modifiedCr = originalCr.copy();
    }



    public void selectInsColorSpaceLSB(YCbCrType type, double slider, boolean multipleW){

        switch (type){
            case Y:
                jpeg.Process.setModifiedY(insertWatermarkLSB(jpeg.Process.getModifiedY(), getModifiedY(), slider, multipleW));
                break;
            case Cb:
                jpeg.Process.setModifiedCb(insertWatermarkLSB(jpeg.Process.getModifiedCb(), getModifiedY(), slider, multipleW));
                break;
            case Cr:
                jpeg.Process.setModifiedCr(insertWatermarkLSB(jpeg.Process.getModifiedCr(), getModifiedY(), slider, multipleW));
                break;
        }
    }

    private static Matrix insertWatermarkLSB(Matrix ImageMatrix, Matrix WatermarkMatrix, double h, boolean multipleW) {
        int ImageHeight = ImageMatrix.getRowDimension();
        int ImageWidth = ImageMatrix.getColumnDimension();

        // Resize watermark matrix
        Matrix adjustedWatermark;
        if (WatermarkMatrix.getRowDimension() < ImageHeight || WatermarkMatrix.getColumnDimension() < ImageWidth) {
            if (multipleW) {
                adjustedWatermark = expandWatermarkMatrix(WatermarkMatrix, ImageHeight, ImageWidth);
            } else {
                adjustedWatermark = adjustWatermarkMatrix(WatermarkMatrix, ImageHeight, ImageWidth);
            }
        } else {
            adjustedWatermark = WatermarkMatrix;
        }

        // Insert watermark to image
        for (int i = 0; i < ImageHeight; i++) {
            for (int j = 0; j < ImageWidth; j++) {
                int imageValue = (int) ImageMatrix.get(i, j);
                int watermarkValue = (int) adjustedWatermark.get(i, j);

                int finalValue = insertLSB(imageValue, watermarkValue, h);

                ImageMatrix.set(i, j, finalValue);

            }
        }

        return ImageMatrix;
    }

    private static int insertLSB(int imageValue, int watermarkValue, double h) {

        // Convert numbers to binary
        String binaryImageValue = String.format("%8s", Integer.toBinaryString(imageValue)).replace(' ', '0');
        String binaryWatermarkValue = String.format("%8s", Integer.toBinaryString(watermarkValue)).replace(' ', '0');


        // Nullify bits in level h
        binaryImageValue = binaryImageValue.substring(0, binaryImageValue.length() - (int)h) + "0".repeat((int)h);

        // Permute watermark bits
        binaryWatermarkValue = binaryWatermarkValue.substring(KEY % 8) + binaryWatermarkValue.substring(0, KEY % 8);

        // Move significant bits
        binaryWatermarkValue = "0".repeat(8 - (int)h) + binaryWatermarkValue.substring(binaryWatermarkValue.length() - (int)h);

        int finalValue = Integer.parseInt(binaryImageValue, 2) + Integer.parseInt(binaryWatermarkValue, 2);

        return finalValue;
    }

    private static Matrix expandWatermarkMatrix(Matrix WatermarkMatrix, int ImageRow, int ImageColumn) {
        Matrix expandedWatermark = new Matrix(ImageRow, ImageColumn);
        int WatermarkHeight = WatermarkMatrix.getRowDimension();
        int WatermarkWidth = WatermarkMatrix.getColumnDimension();

        for (int i = 0; i < ImageRow; i++) {
            for (int j = 0; j < ImageColumn; j++) {
                expandedWatermark.set(i, j, WatermarkMatrix.get(i % WatermarkHeight, j % WatermarkWidth));
            }
        }

        return expandedWatermark;
    }

    private static Matrix adjustWatermarkMatrix(Matrix WatermarkMatrix, int ImageRow, int ImageColumn) {
        Matrix adjustedWatermark = new Matrix(ImageRow, ImageColumn);

        int WatermarkHeight = WatermarkMatrix.getRowDimension();
        int WatermarkWidth = WatermarkMatrix.getColumnDimension();

        for (int i = 0; i < ImageRow; i++) {
            for (int j = 0; j < ImageColumn; j++) {
                if (i < WatermarkHeight && j < WatermarkWidth) {
                    adjustedWatermark.set(i, j, WatermarkMatrix.get(i, j));
                } else {
                    adjustedWatermark.set(i, j, 16);
                }
            }
        }

        return adjustedWatermark;
    }



    public Matrix selectExtrColorSpaceLSB(YCbCrType type, double slider){

        switch (type){
            case Y:
                extractedMatrix = (extractWatermarkLSB(jpeg.Process.getModifiedY(), slider));
                return extractedMatrix;
            case Cb:
                extractedMatrix = (extractWatermarkLSB(jpeg.Process.getModifiedCb(), slider));
                return extractedMatrix;
            case Cr:
                extractedMatrix = (extractWatermarkLSB(jpeg.Process.getModifiedCr(), slider));
                return extractedMatrix;
        }
        return null;
    }

    private static Matrix extractWatermarkLSB(Matrix ImageMatrix, double h) {
        int ImageHeight = ImageMatrix.getRowDimension();
        int ImageWidth = ImageMatrix.getColumnDimension();

        Matrix extractedWatermarkMatrix = new Matrix(ImageHeight, ImageWidth);


        for (int i = 0; i < ImageHeight; i++) {
            for (int j = 0; j < ImageWidth; j++) {
                // Extract pixels
                double pixelValue = ImageMatrix.get(i, j);

                // Extract watermark LSB
                int watermarkValue = extractLSB((int) pixelValue, h);

                extractedWatermarkMatrix.set(i, j, watermarkValue);
            }
        }

        return extractedWatermarkMatrix;
    }

    private static int extractLSB(int pixelValue, double h) {
        // Convert pixel value to binary
        String binaryPixelValue = String.format("%8s", Integer.toBinaryString(pixelValue)).replace(' ', '0');

        // Permute pixel bits
        binaryPixelValue = binaryPixelValue.substring(KEY % 8) + binaryPixelValue.substring(0, KEY % 8);

        // Extract significant bits
        binaryPixelValue = binaryPixelValue.substring(binaryPixelValue.length() - (int)h);

        // Convert these values back to integer
        int watermarkValue = Integer.parseInt(binaryPixelValue, 2);

        return watermarkValue;
    }



    public static void selectInsDCT(int blocksize, int u1, int v1, int u2, int v2, double h, Boolean multipleW){
        jpeg.Process.setModifiedY(insertWatermarkDCT(jpeg.Process.getModifiedY(), modifiedY, blocksize, u1, v1, u2, v2, h, multipleW));
    }

    private static Matrix insertWatermarkDCT(Matrix ImageMatrix, Matrix WatermarkMatrix, int blockSize, int u1, int v1, int u2, int v2, double h, boolean multipleW) {
        int ImageHeight = ImageMatrix.getRowDimension();
        int ImageWidth = ImageMatrix.getColumnDimension();
        Matrix watermarkedImageMatrix = new Matrix(ImageHeight, ImageWidth);


        for (int i = 0; i < ImageHeight; i += blockSize) {
            for (int j = 0; j < ImageWidth; j += blockSize) {
                // Extract blocks
                Matrix block = ImageMatrix.getMatrix(i, Math.min(i + blockSize - 1, ImageHeight - 1), j, Math.min(j + blockSize - 1, ImageWidth - 1));

                // DCT to blocks
                Matrix dctBlock = applyDCT(block, blockSize);

                // Apply watermark
                if (!multipleW && i == 0 && j == 0) {
                    double watermarkBit = WatermarkMatrix.get(i / blockSize, j / blockSize);
                    applyWatermarkDCT(dctBlock, watermarkBit, u1, v1, u2, v2, h);
                }
                if (multipleW) {
                    double watermarkBit = WatermarkMatrix.get(i / blockSize, j / blockSize);
                    applyWatermarkDCT(dctBlock, watermarkBit, u1, v1, u2, v2, h);
                }

                // Inverse DCT to blocks
                Matrix idctBlock = applyInverseDCT(dctBlock, blockSize);

                watermarkedImageMatrix.setMatrix(i, Math.min(i + blockSize - 1, ImageHeight - 1), j, Math.min(j + blockSize - 1, ImageWidth - 1), idctBlock);
            }
        }
        return watermarkedImageMatrix;
    }

    private static void applyWatermarkDCT(Matrix dctBlock, double watermarkBit, int u1, int v1, int u2, int v2, double h) {
        double adjustment = h / 2;
        if (watermarkBit == 0) {
            dctBlock.set(u1, v1, dctBlock.get(u1, v1) + adjustment);
            dctBlock.set(u2, v2, dctBlock.get(u2, v2) - adjustment);
        } else if (watermarkBit == 1) {
            dctBlock.set(u1, v1, dctBlock.get(u1, v1) - adjustment);
            dctBlock.set(u2, v2, dctBlock.get(u2, v2) + adjustment);
        }


        if (watermarkBit == 0) {
            if (!(dctBlock.get(u1, v1) > dctBlock.get(u2, v2))) {
                dctBlock.set(u1, v1, dctBlock.get(u2, v2));
                dctBlock.set(u2, v2, dctBlock.get(u1, v1));
            }
        } else {
            if (!(dctBlock.get(u1, v1) <= dctBlock.get(u2, v2))) {
                dctBlock.set(u1, v1, dctBlock.get(u2, v2));
                dctBlock.set(u2, v2, dctBlock.get(u1, v1));
            }
        }

        if (!(Math.abs(dctBlock.get(u1, v1) - dctBlock.get(u2, v2)) > h)) {
            if (dctBlock.get(u1, v1) < dctBlock.get(u2, v2)) {
                dctBlock.set(u1, v1, dctBlock.get(u1, v1) - adjustment);
                dctBlock.set(u2, v2, dctBlock.get(u2, v2) + adjustment);
            } else {
                dctBlock.set(u1, v1, dctBlock.get(u1, v1) + adjustment);
                dctBlock.set(u2, v2, dctBlock.get(u2, v2) - adjustment);
            }
        }
    }



    private static Matrix applyDCT(Matrix matrix,int blockSize) {
        matrix = Transform.transform(matrix, TransformType.DCT, blockSize);
        return matrix;
    }

    private static Matrix applyInverseDCT(Matrix matrix,int blockSize) {
        matrix = Transform.inverseTransform(matrix, TransformType.DCT, blockSize);
        return matrix;
    }



    public Matrix selectExtrDCT(int blockSize, int u1, int v1, int u2, int v2, double h, boolean multipleW){
        extractedMatrix = (extractWatermarkDCT(jpeg.Process.getModifiedY(), blockSize, u1, v1, u2, v2, h, multipleW));
        return extractedMatrix;
    }

    private static Matrix extractWatermarkDCT(Matrix WatermarkedImageMatrix, int blockSize, int u1, int v1, int u2, int v2, double h, boolean multipleW) {
        int Height = WatermarkedImageMatrix.getRowDimension();
        int Width = WatermarkedImageMatrix.getColumnDimension();
        Matrix WatermarkMatrix = new Matrix(Height, Width);


        for (int i = 0; i < Height; i += blockSize) {
            for (int j = 0; j < Width; j += blockSize) {
                // Extract blocks
                Matrix block = WatermarkedImageMatrix.getMatrix(i, Math.min(i + blockSize - 1, Height - 1), j, Math.min(j + blockSize - 1, Width - 1));

                // DCT to blocks
                Matrix dctBlock = applyDCT(block, blockSize);

                // Extract watermark
                if (!multipleW) {
                    double extractedBit = extractWatermarkFromBlock(dctBlock, u1, v1, u2, v2, h);

                    for (int x = i; x < Math.min(i + blockSize, Height); x++) {
                        for (int y = j; y < Math.min(j + blockSize, Width); y++) {
                            WatermarkMatrix.set(x, y, extractedBit);
                        }
                    }
                    WatermarkMatrix = convertBinaryToOriginal(originalY, WatermarkMatrix, multipleW);
                    return WatermarkMatrix;

                }
                double extractedBit = extractWatermarkFromBlock(dctBlock, u1, v1, u2, v2, h);

                for (int x = i; x < Math.min(i + blockSize, Height); x++) {
                    for (int y = j; y < Math.min(j + blockSize, Width); y++) {
                        WatermarkMatrix.set(x, y, extractedBit);
                    }
                }
            }
        }
        WatermarkMatrix = convertBinaryToOriginal(originalY, WatermarkMatrix, multipleW);

        return WatermarkMatrix;
    }

    private static double extractWatermarkFromBlock(Matrix dctBlock, int u1, int v1, int u2, int v2, double h) {
        double diff = Math.abs(dctBlock.get(u1, v1) - dctBlock.get(u2, v2));

        return diff <= h ? 0 : 1;
    }

    private static Matrix convertBinaryToOriginal(Matrix OriginalWatermarkMatrix, Matrix BinaryMatrix, boolean multipleW) {
        int Height = OriginalWatermarkMatrix.getRowDimension();
        int Width = OriginalWatermarkMatrix.getColumnDimension();
        Matrix ExtractedWatermarkMatrix = new Matrix(Height, Width);


        for (int i = 0; i < Height; i++) {
            for (int j = 0; j < Width; j++) {
                if (BinaryMatrix.get(i, j) == 1) {
                    ExtractedWatermarkMatrix.set(i, j, OriginalWatermarkMatrix.get(i, j));
                } else {
                    ExtractedWatermarkMatrix.set(i, j, 16);
                }
            }
        }

        if (multipleW) {
            ExtractedWatermarkMatrix = expandWatermarkMatrix(ExtractedWatermarkMatrix, Height, Width);
        }

        return ExtractedWatermarkMatrix;
    }



    public static BufferedImage atPNG(Float CompressionQuality) throws IOException {
        Image(jpeg.Process.getModifiedY(), jpeg.Process.getModifiedCb(), jpeg.Process.getModifiedCr());
        newImage = attackPNG(attackImage, CompressionQuality);
        return newImage;
    }

    private static BufferedImage attackPNG(BufferedImage WatermarkedImage, float CompressionQuality) throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(WatermarkedImage, "jpeg", baos);
        baos.flush();
        byte[] imageInByte = baos.toByteArray();
        baos.close();

        java.util.Iterator<ImageWriter> writers =  ImageIO.getImageWritersByFormatName("jpeg");
        ImageWriter writer = (ImageWriter) writers.next();
        ImageWriteParam param = writer.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(CompressionQuality);

        ByteArrayOutputStream compressedBaos = new ByteArrayOutputStream();
        ImageOutputStream outputStream = ImageIO.createImageOutputStream(compressedBaos);
        writer.setOutput(outputStream);
        writer.write(null, new IIOImage(WatermarkedImage, null, null), param);


        ByteArrayInputStream bais = new ByteArrayInputStream(compressedBaos.toByteArray());
        BufferedImage decompressedImage = ImageIO.read(bais);

        return decompressedImage;
    }

    private static void Image(Matrix Y, Matrix Cb, Matrix Cr){
        var colors = ColorTransform.convertModifiedYcBcRtoRGB(Y, Cb, Cr);
        modifiedRed = colors[0];
        modifiedGreen = colors[1];
        modifiedBlue = colors[2];

        int Height = Y.getRowDimension();
        int Width = Y.getColumnDimension();

        attackImage = new BufferedImage(Width, Height, BufferedImage.TYPE_INT_RGB);

        for (int h = 0; h < Height; h++) {
            for (int w = 0; w < Width; w++) {
                attackImage.setRGB(w, h,
                        (new Color(modifiedRed[h][w],
                                modifiedGreen[h][w],
                                modifiedBlue[h][w])).getRGB());
            }
        }
    }


    public static BufferedImage rotation(Rotations Rotation){
        Image(jpeg.Process.getModifiedY(), jpeg.Process.getModifiedCb(), jpeg.Process.getModifiedCr());
        switch (Rotation){
            case R45:
                newImage = rotate(45);
                return newImage;
            case R90:
                newImage = rotate(90);
                return newImage;
        }
        return null;
    }

    private static BufferedImage rotate(int Angle){

        double rads = Math.toRadians(Angle);
        double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));
        int Width = attackImage.getWidth();
        int Height = attackImage.getHeight();
        int newWidth = (int) Math.floor(Width * cos + Height * sin);
        int newHeight = (int) Math.floor(Height * cos + Width * sin);

        BufferedImage rotated = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = rotated.createGraphics();
        AffineTransform at = new AffineTransform();
        at.translate((newWidth - Width) / 2, (newHeight - Height) / 2);

        int x = Width / 2;
        int y = Height / 2;

        at.rotate(rads, x, y);
        g2d.setTransform(at);
        g2d.drawImage(attackImage, 0, 0, null);
        g2d.dispose();

        return rotated;
    }


    public static BufferedImage croppingImage(int x, int y, int Width, int Height){
        newImage = cropImage(x, y, Width, Height);
        return newImage;
    }

    private static BufferedImage cropImage(int x, int y, int Width, int Height) {
        Image(jpeg.Process.getModifiedY(), jpeg.Process.getModifiedCb(), jpeg.Process.getModifiedCr());
        int imageWidth = attackImage.getWidth();
        int imageHeight = attackImage.getHeight();
        BufferedImage CroppedImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);

        for (int h = 0; h < imageHeight; h++) {
            for (int w = 0; w < imageWidth; w++) {

                if (w >= x && w < x + Width && h >= y && h < y + Height) {
                    continue;
                }
                CroppedImage.setRGB(w, h, attackImage.getRGB(w, h));
            }
        }
        return CroppedImage;
    }





    public static Matrix getModifiedY() {
        return modifiedY;
    }

    public static Matrix getModifiedCb() {
        return modifiedCb;
    }

    public static Matrix getModifiedCr() {
        return modifiedCr;
    }

    public static BufferedImage getNewImage() {
        return newImage;
    }
}
