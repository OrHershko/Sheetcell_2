package main;


import api.Engine;
import components.loadfile.LoadFileController;
import components.maingrid.MainGridController;
import dto.DTOFactoryImpl;
import dto.SheetDTO;
import impl.EngineImpl;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import java.io.File;
import java.io.IOException;
import javafx.scene.control.ScrollPane;


public class AppController {

    @FXML
    private BorderPane rootPane;

    @FXML
    private HBox hboxContainer;

    @FXML
    private GridPane loadFileComponent;

    @FXML
    private LoadFileController loadFileComponentController;

    @FXML
    private ScrollPane mainGridComponent;

    @FXML
    private MainGridController mainGridComponentController;

    private final Engine engine = new EngineImpl(new DTOFactoryImpl());

    @FXML
    public void initialize() {
        loadFileComponentController.setAppController(this);
        mainGridComponentController.setAppController(this);
    }

    public void loadFileToEngine(File selectedFile) throws IOException {
        engine.loadFile(selectedFile.getAbsolutePath());
        Platform.runLater(() -> {
            mainGridComponentController.buildGridBoundaries((SheetDTO) engine.getSheetDTO());
        });
    }
}
