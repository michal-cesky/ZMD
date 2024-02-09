package core;

import javafx.scene.image.Image;

import java.net.URL;

public class FileBindings {

    public static final String defaultImage = "images/Lenna(testImage).png";

    public static final URL GUIMain = FileBindings.class.getClassLoader().getResource("graphics/MainWindow.fxml");

    public static Image favicon = new Image(FileBindings.class.getClassLoader().getResourceAsStream("favicon.png"));

}
