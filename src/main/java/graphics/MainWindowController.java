package graphics;

import core.FileBindings;
import core.Helper;
import enums.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Pair;
import jpeg.*;
import jpeg.Process;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Objects;
import java.util.ResourceBundle;

import static enums.PsnrTypeMethod.*;
import static enums.Rotations.R45;
import static enums.Rotations.R90;
import static enums.SsimTypeMtehod.MSSIM;
import static enums.SsimTypeMtehod.SSIM;

public class MainWindowController implements Initializable {

    private String Image = null;
    private String pathWatermark = null;

    private boolean LSB = false;

    private Process Process;
    private watermark watermark;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        combobox_sampling.getItems().addAll(SamplingType.values());
        combobox_transform.getItems().addAll(TransformType.values());
        combobox_psnr.getItems().addAll(PsnrType.values());
        combobox_ssim.getItems().addAll(YCbCrType.values());
        combobox_LSB.getItems().addAll(YCbCrType.values());

        combobox_sampling.getSelectionModel().select(SamplingType.S_4_1_1);
        combobox_transform.getSelectionModel().select(TransformType.DCT);
        combobox_psnr.getSelectionModel().select(PsnrType.Red);
        combobox_ssim.getSelectionModel().select(YCbCrType.Y);
        slider.setValue(50);
        combobox_LSB.getSelectionModel().select(YCbCrType.Y);
        sliderLSB.setValue(5);

        ObservableList<Integer> blocks = FXCollections.observableArrayList(2, 4, 8, 16, 32, 64, 128, 256, 512);
        ObservableList<Integer> blocksDCT = FXCollections.observableArrayList(8, 16, 32, 64, 128, 256, 512);
        ObservableList<Float> compressionQualityJPEG = FXCollections.observableArrayList(0.0f, 0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f);

        SpinnerValueFactory<Integer> spinnerValues = new SpinnerValueFactory.ListSpinnerValueFactory<>(blocks);
        spinnerValues.setValue(8);
        spinner_encode.setValueFactory(spinnerValues);

        SpinnerValueFactory<Integer> spinnerValuesDCT = new SpinnerValueFactory.ListSpinnerValueFactory<>(blocksDCT);
        spinnerValuesDCT.setValue(8);
        spinner_blocksize2D.setValueFactory(spinnerValuesDCT);

        SpinnerValueFactory<Float> spinnerValuesQuality = new SpinnerValueFactory.ListSpinnerValueFactory<>(compressionQualityJPEG);
        spinnerValuesQuality.setValue(0.2f);
        compressionQuality.setValueFactory(spinnerValuesQuality);

        textfield_encode.setTextFormatter(new TextFormatter<>(Helper.NUMBER_FORMATTER));
        textfield_encode.textProperty().bindBidirectional(slider.valueProperty(), NumberFormat.getIntegerInstance());

        textfieldLSB.setTextFormatter(new TextFormatter<>(Helper.NUMBER_FORMATTER));
        textfieldLSB.textProperty().bindBidirectional(sliderLSB.valueProperty(), NumberFormat.getIntegerInstance());

        textfield_depthDCT.setTextFormatter(new TextFormatter<>(Helper.NUMBER_FORMATTER));
        textfield_depthDCT.textProperty().bindBidirectional(slider_DCT.valueProperty(), NumberFormat.getIntegerInstance());

        textfield_u1DCT.setTextFormatter(new TextFormatter<>(Helper.NUMBER_FORMATTER));
        textfield_v1DCT.setTextFormatter(new TextFormatter<>(Helper.NUMBER_FORMATTER));
        textfield_u2DCT.setTextFormatter(new TextFormatter<>(Helper.NUMBER_FORMATTER));
        textfield_v2DCT.setTextFormatter(new TextFormatter<>(Helper.NUMBER_FORMATTER));

        attackX.setTextFormatter(new TextFormatter<>(Helper.NUMBER_FORMATTER));
        attackY.setTextFormatter(new TextFormatter<>(Helper.NUMBER_FORMATTER));
        attackWidth.setTextFormatter(new TextFormatter<>(Helper.NUMBER_FORMATTER));
        attackHeight.setTextFormatter(new TextFormatter<>(Helper.NUMBER_FORMATTER));

        Process = new Process(FileBindings.defaultImage);
        watermark = new watermark(FileBindings.watermarkImage);
    }

    public void close() {
        Stage stage = ((Stage) button_downsample.getScene().getWindow());
        stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    public void closeWindows() {
        Dialogs.closeAllWindows();
    }

    public void changeImage() {
        final File file = Dialogs.openFile();
        assert file != null;
        Image = file.getAbsolutePath();
        Process = new Process(Image);
    }

    public void changeWatermark() {
        final File file = Dialogs.openFile();
        assert file != null;
        pathWatermark = file.getAbsolutePath();
        watermark = new watermark(pathWatermark);
    }

    public void reset() {
        Process = new Process(FileBindings.defaultImage);
        watermark = new watermark(FileBindings.watermarkImage);
    }

    public void showOriginal() throws IOException {
        File f;
        f = new File(Objects.requireNonNullElse(Image, FileBindings.defaultImage));
        Dialogs.showImageInWindow(ImageIO.read(f), "Original", true);
    }


    @FXML
    private ComboBox<SamplingType> combobox_sampling;

    @FXML
    private ComboBox<PsnrType> combobox_psnr;

    @FXML
    private ComboBox<TransformType> combobox_transform;

    @FXML
    private ComboBox<YCbCrType> combobox_ssim;

    @FXML
    private Button button_cb;

    @FXML
    private Button button_count;

    @FXML
    private Button button_cr;

    @FXML
    private Spinner<Integer> spinner_encode;

    @FXML
    private TextField textfield_encode;

    @FXML
    private TextField textfield_mse;

    @FXML
    private TextField textfield_mae;

    @FXML
    private TextField textfield_psnr;

    @FXML
    private TextField textfield_ssim;

    @FXML
    private TextField textfield_mssim;

    @FXML
    private TextField textfield_sae;

    @FXML
    private Button button_downsample;

    @FXML
    private Button button_iquantize;

    @FXML
    private Button button_moblue;

    @FXML
    private Button button_mocr;

    @FXML
    private Button button_mogreen;

    @FXML
    private Button button_mored;

    @FXML
    private Button button_moy;

    @FXML
    private Button button_modgreen;

    @FXML
    private Button button_oversample;

    @FXML
    private Button button_quantize;

    @FXML
    private Button button_rbg;

    @FXML
    private Button button_showimages;

    @FXML
    private Button button_transform;

    @FXML
    private Button button_rgb;

    @FXML
    private Button button_y;

    @FXML
    private Button button_ycbcr;

    @FXML
    private CheckBox chackbox_showsteps;

    @FXML
    private CheckBox button_shadesofgray;

    @FXML
    private Slider slider;

    @FXML
    private Button button_orred;

    @FXML
    private Button button_orgreen;

    @FXML
    private Button button_countssin;





    @FXML
    private Button showWatermark;

    @FXML
    private Button yWatermark;

    @FXML
    private Button cbWatermark;

    @FXML
    private Button crWatermark;

    @FXML
    private ComboBox combobox_LSB;

    @FXML
    private Slider sliderLSB;

    @FXML
    private TextField textfieldLSB;

    @FXML
    private CheckBox checkbox_multiplewatermarks;

    @FXML
    private Button button_insertWatermark;

    @FXML
    private Button button_extwatermarkLSB;

    @FXML
    private Spinner<Integer> spinner_blocksize2D;

    @FXML
    private TextField textfield_u1DCT;

    @FXML
    private TextField textfield_v1DCT;

    @FXML
    private TextField textfield_u2DCT;

    @FXML
    private TextField textfield_v2DCT;

    @FXML
    private Slider slider_DCT;

    @FXML
    private TextField textfield_depthDCT;

    @FXML
    private CheckBox checkbox_multiplewatermarksDCT;

    @FXML
    private Spinner compressionQuality;

    @FXML
    private TextField attackX;

    @FXML
    private TextField attackY;

    @FXML
    private TextField attackWidth;

    @FXML
    private TextField attackHeight;



    @FXML
    void showimages(ActionEvent event) throws IOException {
        File f;
        f = new File(Objects.requireNonNullElse(Image, FileBindings.defaultImage));
        Dialogs.showImageInWindow(ImageIO.read(f), "Original", false);
    }

    @FXML
    void orred(ActionEvent event) {
        Dialogs.showImageInWindow(Process.getOneColorImageFromRGB(Process.getOriginalRed(), ColorType.RED, button_shadesofgray.isSelected()), "Original Only Red Image");
    }

    @FXML
    void orgreen(ActionEvent event) {
        Dialogs.showImageInWindow(Process.getOneColorImageFromRGB(Process.getOriginalRed(), ColorType.GREEN, button_shadesofgray.isSelected()), "Original Only Green Image");
    }

    @FXML
    void orblue(ActionEvent event) {
        Dialogs.showImageInWindow(Process.getOneColorImageFromRGB(Process.getOriginalRed(), ColorType.BLUE, button_shadesofgray.isSelected()), "Original Only Blue Image");
    }

    @FXML
    void yaction(ActionEvent event) {
        Dialogs.showImageInWindow(Process.getOneColorImageFromYCbCr(Process.getOriginalY()), "Original Y Image");
    }

    @FXML
    void cbaction(ActionEvent event) {
        Dialogs.showImageInWindow(Process.getOneColorImageFromYCbCr(Process.getOriginalCb()), "Original Cb Image");
    }

    @FXML
    void craction(ActionEvent event) {
        Dialogs.showImageInWindow(Process.getOneColorImageFromYCbCr(Process.getOriginalCr()), "Original Cr Image");
    }

    @FXML
    public void actionrbg(ActionEvent event) {
        Process.convertRGBToYCbCr();
    }

    @FXML
    void actionycvcr(ActionEvent event) {
        Process.convertYCbCrToRGB();
    }

    @FXML
    void actionrgb(ActionEvent event) {
        Dialogs.showImageInWindow(Process.getImageFromModifiedRGB(),"RGB",false);
    }

    @FXML
    void downsample(ActionEvent event) {
        Process.setModifiedCb(jpeg.Sampling.sampleDown(Process.getModifiedCb(), combobox_sampling.getValue()));
        Process.setModifiedCr(jpeg.Sampling.sampleDown(Process.getModifiedCr(), combobox_sampling.getValue()));
    }

    @FXML
    void transform(ActionEvent event) {
        jpeg.Process.setModifiedY(Transform.transform(jpeg.Process.getModifiedY(), combobox_transform.getValue(), spinner_encode.getValue()));
        jpeg.Process.setModifiedCb(Transform.transform(jpeg.Process.getModifiedCb(), combobox_transform.getValue(), spinner_encode.getValue()));
        jpeg.Process.setModifiedCr(Transform.transform(jpeg.Process.getModifiedCr(), combobox_transform.getValue(), spinner_encode.getValue()));
    }

    @FXML
    void quantize(ActionEvent event) {
        jpeg.Process.setModifiedY(Quantization.inverseQuantize(jpeg.Process.getModifiedY(), spinner_encode.getValue(), slider.getValue(), false));
        jpeg.Process.setModifiedCb(Quantization.inverseQuantize(jpeg.Process.getModifiedCb(), spinner_encode.getValue(), slider.getValue(), false));
        jpeg.Process.setModifiedCr(Quantization.inverseQuantize(jpeg.Process.getModifiedCr(), spinner_encode.getValue(), slider.getValue(), false));
    }

    @FXML
    void iquantize(ActionEvent event) {
        jpeg.Process.setModifiedY(Quantization.quantize(jpeg.Process.getModifiedY(), spinner_encode.getValue(), slider.getValue(), false));
        jpeg.Process.setModifiedCb(Quantization.quantize(jpeg.Process.getModifiedCb(), spinner_encode.getValue(), slider.getValue(), false));
        jpeg.Process.setModifiedCr(Quantization.quantize(jpeg.Process.getModifiedCr(), spinner_encode.getValue(), slider.getValue(), false));
    }

    @FXML
    void itransform(ActionEvent event) {
        jpeg.Process.setModifiedY(Transform.inverseTransform(jpeg.Process.getModifiedY(), combobox_transform.getValue(), spinner_encode.getValue()));
        jpeg.Process.setModifiedCb(Transform.inverseTransform(jpeg.Process.getModifiedCb(), combobox_transform.getValue(), spinner_encode.getValue()));
        jpeg.Process.setModifiedCr(Transform.inverseTransform(jpeg.Process.getModifiedCr(), combobox_transform.getValue(), spinner_encode.getValue()));
    }

    @FXML
    void oversample(ActionEvent event) {
        Process.setModifiedCb(jpeg.Sampling.sampleUp(Process.getModifiedCb(), combobox_sampling.getValue()));
        Process.setModifiedCr(jpeg.Sampling.sampleUp(Process.getModifiedCr(), combobox_sampling.getValue()));
    }

    @FXML
    void count(ActionEvent event) {
        textfield_mse.setText(String.valueOf(Quality.Quality_1(combobox_psnr.getValue(),MSE)));
        textfield_mae.setText(String.valueOf(Quality.Quality_1(combobox_psnr.getValue(),MAE)));
        textfield_psnr.setText(String.valueOf(Quality.Quality_1(combobox_psnr.getValue(),PSNR)));
        textfield_sae.setText(String.valueOf(Quality.Quality_1(combobox_psnr.getValue(),SAE)));
    }

    @FXML
    void countssin(ActionEvent event) {
        textfield_ssim.setText(String.valueOf(Quality.Quality_2(combobox_ssim.getValue(), SSIM)));
        textfield_mssim.setText(String.valueOf(Quality.Quality_2(combobox_ssim.getValue(), MSSIM)));
    }

    @FXML
    void acctionmodred(ActionEvent event) {
        Dialogs.showImageInWindow(Process.getOneColorImageFromRGB(Process.getModifiedRed(), ColorType.RED, button_shadesofgray.isSelected()), "Modified Red Image");
    }

    @FXML
    void acctionmodblue(ActionEvent event) {
        Dialogs.showImageInWindow(Process.getOneColorImageFromRGB(Process.getModifiedBlue(), ColorType.BLUE, button_shadesofgray.isSelected()), "Modified Blue Image");
    }

    @FXML
    void actionmodgreen(ActionEvent event) {
        Dialogs.showImageInWindow(Process.getOneColorImageFromRGB(Process.getModifiedGreen(), ColorType.GREEN, button_shadesofgray.isSelected()), "Modified Green Image");
    }

    @FXML
    void actionmodcb(ActionEvent event) {
        Dialogs.showImageInWindow(Process.getOneColorImageFromYCbCr(Process.getModifiedCb()), "Modified Cb Image");
    }

    @FXML
    void actionmodcr(ActionEvent event) {
        Dialogs.showImageInWindow(Process.getOneColorImageFromYCbCr(Process.getModifiedCr()), "Modified Cr Image");
    }

    @FXML
    void actionmody(ActionEvent event) {
        Dialogs.showImageInWindow(Process.getOneColorImageFromYCbCr(Process.getModifiedY()), "Modified Y Image");
    }



    @FXML
    void showWatermark(ActionEvent event) throws IOException {
        File f;
        f = new File(Objects.requireNonNullElse(pathWatermark, FileBindings.watermarkImage));
        Dialogs.showImageInWindow(ImageIO.read(f), "Watermark", false);
    }

    @FXML
    void yWatermark(ActionEvent event) {
        Dialogs.showImageInWindow(Process.getOneColorImageFromYCbCr(watermark.getModifiedY()), "Modified Y Watermark");
    }

    @FXML
    void cbWatermark(ActionEvent event) {
        Dialogs.showImageInWindow(Process.getOneColorImageFromYCbCr(watermark.getModifiedCb()), "Modified Cb Watermark");
    }

    @FXML
    void crWatermark(ActionEvent event) {
        Dialogs.showImageInWindow(Process.getOneColorImageFromYCbCr(watermark.getModifiedCr()), "Modified Cr Watermark");
    }

    @FXML
    void insertwatermarkLSB(ActionEvent event) {
        LSB = true;
        watermark.selectInsColorSpaceLSB((YCbCrType) combobox_LSB.getValue(), sliderLSB.getValue(), checkbox_multiplewatermarks.isSelected());
    }

    @FXML
    void extwatermarkLSB(ActionEvent event) {
        Pair<BufferedImage, BufferedImage> images = extrLSB();
        Dialogs.showMultipleImageInWindow("Extract Image LSB", false, true,
                new Pair<>(images.getKey(), "Original Image"),
                new Pair<>(images.getValue(), "Extracted Watermark")
        );
    }

    private Pair<BufferedImage, BufferedImage> extrLSB() {
        BufferedImage extractImage = Process.getOneColorImageFromYCbCr(watermark.selectExtrColorSpaceLSB((YCbCrType) combobox_LSB.getValue(), sliderLSB.getValue()));
        watermark.selectExtrColorSpaceLSB((YCbCrType) combobox_LSB.getValue(), sliderLSB.getValue());
        Process.convertYCbCrToRGB();
        BufferedImage originalImage = Process.getImageFromModifiedRGB();

        return new Pair<>(originalImage, extractImage);
    }

    @FXML
    void insertWatermarkDCT(ActionEvent event) {
        watermark.selectInsDCT(spinner_blocksize2D.getValue(), Integer.parseInt(textfield_u1DCT.getText()), Integer.parseInt(textfield_v1DCT.getText()),
                Integer.parseInt(textfield_u2DCT.getText()), Integer.parseInt(textfield_v2DCT.getText()), slider_DCT.getValue(), checkbox_multiplewatermarksDCT.isSelected());
    }

    @FXML
    void extwatermarkDCT(ActionEvent event) {
        Pair<BufferedImage, BufferedImage> images = extrDCT();
        Dialogs.showMultipleImageInWindow("Extract Image LSB", false, true,
                new Pair<>(images.getKey(), "Original Image"),
                new Pair<>(images.getValue(), "Extracted Watermark")
        );
    }

    private Pair<BufferedImage, BufferedImage> extrDCT() {
        BufferedImage extractImage = Process.getOneColorImageFromYCbCr(watermark.selectExtrDCT(spinner_blocksize2D.getValue(), Integer.parseInt(textfield_u1DCT.getText()),
                Integer.parseInt(textfield_v1DCT.getText()), Integer.parseInt(textfield_u2DCT.getText()),
                Integer.parseInt(textfield_v2DCT.getText()), slider_DCT.getValue(), checkbox_multiplewatermarksDCT.isSelected()));
        Process.convertYCbCrToRGB();
        BufferedImage originalImage = Process.getImageFromModifiedRGB();

        return new Pair<>(originalImage, extractImage);
    }

    @FXML
    void compressionAttackJPEG(ActionEvent event) throws IOException {
        Pair<BufferedImage, BufferedImage> images = compress();
        Dialogs.showMultipleImageInWindow("Compress image attack", false, true,
                new Pair<>(images.getKey(), "Original Image"),
                new Pair<>(images.getValue(), "Compress Image")
        );
        attackHelperIF();
    }

    private Pair<BufferedImage, BufferedImage> compress() throws IOException {
        BufferedImage compressedImage = (jpeg.watermark.atPNG((Float) compressionQuality.getValue()));
        Process.convertYCbCrToRGB();
        BufferedImage originalImage = Process.getImageFromModifiedRGB();

        attackHelper();

        return new Pair<>(originalImage, compressedImage);

    }

    @FXML
    void rotation45(ActionEvent event){
        Pair<BufferedImage, BufferedImage> images = rotation(R45);
        Dialogs.showMultipleImageInWindow("Rotation image attack", false, true,
                new Pair<>(images.getKey(), "Original Image"),
                new Pair<>(images.getValue(), "Rotated Image")
        );
        attackHelperIF();
    }

    private Pair<BufferedImage, BufferedImage> rotation(Rotations rotation) {
        BufferedImage rotatedImage = jpeg.watermark.rotation(rotation);
        Process.convertYCbCrToRGB();
        BufferedImage originalImage = Process.getImageFromModifiedRGB();

        attackHelper();

        return new Pair<>(originalImage, rotatedImage);

    }

    @FXML
    void rotation90(ActionEvent event){
        Pair<BufferedImage, BufferedImage> images = rotation(R90);
        Dialogs.showMultipleImageInWindow("Rotation image attack", false, true,
                new Pair<>(images.getKey(), "Original Image"),
                new Pair<>(images.getValue(), "Rotated Image")
        );
        attackHelperIF();
    }

    @FXML
    void croppingtheimage(ActionEvent event) {
        Pair<BufferedImage, BufferedImage> images = croppingImage(Integer.parseInt(attackX.getText()), Integer.parseInt(attackY.getText()),
                Integer.parseInt(attackWidth.getText()), Integer.parseInt(attackHeight.getText()));
        Dialogs.showMultipleImageInWindow("Cropping image attack", false, true,
                new Pair<>(images.getKey(), "Original Image"),
                new Pair<>(images.getValue(), "Cropped Image")
        );
        attackHelperIF();
    }

    private Pair<BufferedImage, BufferedImage> croppingImage(int x, int y, int width, int height) {
        BufferedImage croppedImage = jpeg.watermark.croppingImage(x, y, width, height);
        Process.convertYCbCrToRGB();
        BufferedImage originalImage = Process.getImageFromModifiedRGB(); // získání původního obrázku

        attackHelper();

        return new Pair<>(originalImage, croppedImage);
    }

    @FXML
    void restart(ActionEvent event) {
        Process = new Process(FileBindings.defaultImage);
        watermark = new watermark(FileBindings.watermarkImage);
    }

    @FXML
    void combtransform(ActionEvent event) {

    }

    @FXML
    void textssim(ActionEvent event){

    }

    @FXML
    void shadesofgray(ActionEvent event){

    }

    @FXML
    void psnrType(ActionEvent event) {

    }

    @FXML
    void sampling(ActionEvent event) {

    }

    @FXML
    void showsteps(ActionEvent event) {

    }

    @FXML
    void sliderLSB(ActionEvent event) {

    }

    @FXML
    void textfieldLSB(ActionEvent event) {

    }

    @FXML
    void combssim(ActionEvent event) {

    }

    @FXML
    void combobox_LSB(ActionEvent event) {


    }

    @FXML
    void combpsnr(ActionEvent event) {

    }

    @FXML
    void fieldencode(ActionEvent event) {

    }

    @FXML
    void mse(ActionEvent event) {

    }

    @FXML
    void psnr(ActionEvent event) {

    }

    @FXML
    void mae(ActionEvent event) {

    }
    @FXML
    void sae(ActionEvent event) {

    }

    @FXML
    void spinner(ActionEvent event) {

    }

    @FXML
    void slider(ActionEvent event) {

    }

    private void attackHelper(){
        jpeg.Process.setOriginalRGB(jpeg.watermark.getNewImage());
        jpeg.Process.convertRGBToYCbCr();
        jpeg.Process.setOriginalRGB(jpeg.watermark.getNewImage());
        jpeg.watermark.convertRGBToYCbCr();
    }

    private void attackHelperIF(){
        if(LSB){
            Dialogs.showImageInWindow(Process.getOneColorImageFromYCbCr(watermark.selectExtrColorSpaceLSB((YCbCrType) combobox_LSB.getValue(), sliderLSB.getValue())), "Extracted watermark");
            watermark.selectExtrColorSpaceLSB((YCbCrType) combobox_LSB.getValue(), sliderLSB.getValue());
        }
        else{
            Dialogs.showImageInWindow(Process.getOneColorImageFromYCbCr(watermark.selectExtrDCT(spinner_blocksize2D.getValue(), Integer.parseInt(textfield_u1DCT.getText()), Integer.parseInt(textfield_v1DCT.getText()),
                    Integer.parseInt(textfield_u2DCT.getText()), Integer.parseInt(textfield_v2DCT.getText()), slider_DCT.getValue(), checkbox_multiplewatermarksDCT.isSelected())), "Extracted watermark");
        }
    }
}
