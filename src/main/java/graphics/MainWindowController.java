package graphics;

import core.FileBindings;
import core.Helper;
import enums.SamplingType;
import enums.TransformType;
import javafx.beans.property.Property;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable {

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        combobox_sampling.getItems().addAll(SamplingType.values());
        combobox_transform.getItems().addAll(TransformType.values());

        combobox_sampling.getSelectionModel().select(SamplingType.S_4_1_1);
        combobox_transform.getSelectionModel().select(TransformType.DCT);
        slider.setValue(50);

        ObservableList<Integer> blocks = FXCollections.observableArrayList(2, 4, 8, 16, 32, 64, 128, 256, 512);
        SpinnerValueFactory<Integer> spinnerValues = new SpinnerValueFactory.ListSpinnerValueFactory<>(blocks);
        spinnerValues.setValue(8);
        spinner_encode.setValueFactory(spinnerValues);

        textfield_encode.setTextFormatter(new TextFormatter<>(Helper.NUMBER_FORMATTER));

        textfield_encode.textProperty().bindBidirectional(slider.valueProperty(), NumberFormat.getIntegerInstance());
    }

    public void close() {
        Stage stage = ((Stage) button_downsample.getScene().getWindow());
        stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    public void closeWindows() {
        Dialogs.closeAllWindows();
    }

    public void changeImage() {
    }

    public void reset() {
    }

    public void showOriginal() {
        File f = new File(FileBindings.defaultImage);

        try {
            Dialogs.showImageInWindow(ImageIO.read(f), "Original", true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private ComboBox<SamplingType> combobox_sampling;

    @FXML
    private ComboBox<TransformType> combobox_transform;

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
    private TextField textfield_psnr;

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
    private Button button_morgb;

    @FXML
    private Button button_moy;

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
    void acctionmodblue(ActionEvent event) {

    }

    @FXML
    void acctionmodred(ActionEvent event) {

    }

    @FXML
    void actionmodcb(ActionEvent event) {

    }

    @FXML
    void actionmodcr(ActionEvent event) {

    }

    @FXML
    void actionmody(ActionEvent event) {

    }

    @FXML
    void actionrbg(ActionEvent event) {

    }

    @FXML
    void actionycvcr(ActionEvent event) {

    }

    @FXML
    void cbaction(ActionEvent event) {

    }

    @FXML
    void combtransform(ActionEvent event) {

    }

    @FXML
    void count(ActionEvent event) {

    }

    @FXML
    void craction(ActionEvent event) {

    }

    @FXML
    void shadesofgray(ActionEvent event){

    }

    @FXML
    void downsample(ActionEvent event) {

    }

    @FXML
    void iquantize(ActionEvent event) {

    }

    @FXML
    void itransform(ActionEvent event) {

    }

    @FXML
    void oversample(ActionEvent event) {

    }

    @FXML
    void quantize(ActionEvent event) {

    }

    @FXML
    void sampling(ActionEvent event) {

    }

    @FXML
    void showimages(ActionEvent event) throws IOException {
        File file = new File(FileBindings.defaultImage);
        Dialogs.showImageInWindow(ImageIO.read(file), "Original, true");

    }

    @FXML
    void showsteps(ActionEvent event) {

    }

    @FXML
    void transform(ActionEvent event) {

    }

    @FXML
    void yaction(ActionEvent event) {

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
    void spinner(ActionEvent event) {

    }

    @FXML
    void slider(ActionEvent event) {

    }

}
