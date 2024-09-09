package main;


import api.CellValue;
import api.DTO;
import api.Engine;
import components.actionline.ActionLineController;
import components.commands.CommandsComponentController;
import components.loadfile.LoadFileController;
import components.maingrid.MainGridController;
import components.maingrid.cell.CellComponentController;
import components.ranges.RangesController;
import components.versions.VersionsSelectorComponentController;
import dto.CellDTO;
import dto.DTOFactoryImpl;
import dto.RangeDTO;
import dto.SheetDTO;
import impl.EngineImpl;
import impl.Range;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import static impl.cell.Cell.getColumnFromCellID;
import static impl.cell.Cell.getRowFromCellID;


public class AppController {

    @FXML
    private BorderPane rootPane;

    @FXML
    private HBox hBoxContainer;

    @FXML
    private GridPane loadFileComponent;

    @FXML
    private LoadFileController loadFileComponentController;

    @FXML
    private GridPane mainGridComponent;

    @FXML
    private MainGridController mainGridComponentController;

    @FXML
    private GridPane actionLineComponent;

    @FXML
    private ActionLineController actionLineComponentController;

    @FXML
    private VBox commandsComponent;

    @FXML
    private CommandsComponentController commandsComponentController;

    @FXML
    private VBox rangesComponent;

    @FXML
    private RangesController rangesComponentController;

    @FXML
    private ChoiceBox<String> versionsSelectorComponent;

    @FXML
    private VersionsSelectorComponentController versionsSelectorComponentController;

    private final Engine engine = new EngineImpl(new DTOFactoryImpl());

    private Stage previousVersionStage;  // משתנה סינגלטון עבור ה-Stage


    @FXML
    public void initialize() {
        loadFileComponentController.setAppController(this);
        mainGridComponentController.setAppController(this);
        actionLineComponentController.setAppController(this);
        commandsComponentController.setAppController(this);
        rangesComponentController.setAppController(this);
        versionsSelectorComponentController.setAppController(this);
    }

    public void loadFileToEngine(File selectedFile) throws IOException {
        engine.loadFile(selectedFile.getAbsolutePath());
        Platform.runLater(() -> {
            try {
                mainGridComponentController.createDynamicGrid((SheetDTO) engine.getSheetDTO());
                mainGridComponentController.buildGridBoundaries((SheetDTO) engine.getSheetDTO());
                mainGridComponentController.createInnerCellsInGrid((SheetDTO) engine.getSheetDTO());
                commandsComponentController.disableButtons(false);
                rangesComponentController.disableButtons(false);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void displayCellDataOnActionLine(CellDTO cell) {
        actionLineComponentController.displayCellData(cell);
    }

    public void updateCellDataToEngine(String selectedCellId, String orgValue) throws IOException {

        CellValue newCellValue = EngineImpl.convertStringToCellValue(orgValue);
        engine.updateCellValue(selectedCellId, newCellValue, orgValue);
        mainGridComponentController.createInnerCellsInGrid((SheetDTO) engine.getSheetDTO());
        mainGridComponentController.activateMouseClickedOfCell(selectedCellId);

    }

    public void colorDependencies(Set<String> cells, String styleClass) {

        for (Node node : mainGridComponent.getChildren()) {
            node.getStyleClass().remove(styleClass);
        }


        for (String cellID : cells) {
            int column = getColumnFromCellID(cellID) + 1;
            int row = getRowFromCellID(cellID) + 1;

            for (Node node : mainGridComponent.getChildren()) {
                if (GridPane.getColumnIndex(node) == column && GridPane.getRowIndex(node) == row) {

                    node.getStyleClass().add(styleClass);
                    break;
                }
            }

        }
    }

    // פונקציה להצגת הודעת שגיאה
    public static void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public double getPrefRowHeight() {
        return ((SheetDTO)engine.getSheetDTO()).getRowHeight();
    }

    public double getPrefColWidth() {
        return ((SheetDTO)engine.getSheetDTO()).getColWidth();
    }

    public void updateColumnAlignment(int columnIndex, String alignment) {
        mainGridComponentController.updateColAlignment(columnIndex, alignment);
    }

    public CellComponentController getCellControllerById(String cellId) {
        return mainGridComponentController.getCellController(cellId);
    }

    public boolean checkIfRowExist(int rowIndex) {
        return rowIndex <= ((SheetDTO)engine.getSheetDTO()).getNumOfRows() && rowIndex >= 1;
    }

    public void setRowHeightInGrid(int rowIndex, int height) {
        mainGridComponentController.updateRowConstraints(rowIndex, height);
    }

    public boolean checkIfColExist(int colIndex) {
        return colIndex <= ((SheetDTO)engine.getSheetDTO()).getNumOfCols() && colIndex >= 1;
    }

    public void setColWidthInGrid(int rowIndex, int width) {
        mainGridComponentController.updateColConstraints(rowIndex, width);
    }

    public void addNewRange(String topLeftCell, String bottomRightCell, String rangeName) {
        engine.addNewRange(topLeftCell, bottomRightCell, rangeName);
    }

    public void markCellsInRange(String rangeName) {
        RangeDTO rangeDTO = (RangeDTO) engine.getRangeDTOFromSheet(rangeName);
        mainGridComponentController.markCellsInRange(rangeDTO.getCells());
    }

    public void unmarkCellsInRange(String rangeName) {
        RangeDTO rangeDTO = (RangeDTO) engine.getRangeDTOFromSheet(rangeName);
        mainGridComponentController.unmarkCellsInRange(rangeDTO.getCells());
    }

    public Map<Integer, DTO> getSheetsPreviousVersionsDTO() {
        return engine.getSheetsPreviousVersionsDTO();
    }

    public void loadPreviousVersion(int selectedVersion) {
        try {
            // אם ה-Stage לא קיים, ניצור אותו
            if (previousVersionStage == null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/components/maingrid/mainGrid.fxml"));
                Parent root = loader.load();
                MainGridController controller = loader.getController();
                controller.setAppController(this);

                previousVersionStage = new Stage();
                previousVersionStage.setTitle("Previous Sheet Version");
                previousVersionStage.initModality(Modality.APPLICATION_MODAL);
                Scene scene = new Scene(root);
                scene.getStylesheets().add(getClass().getResource("/components/maingrid/cell/CellComponent.css").toExternalForm());
                previousVersionStage.setScene(scene);
                root.setUserData(controller);
            }

            // טען את הגרסה הנבחרת לתוך ה-Grid הקיים
            MainGridController controller = (MainGridController) previousVersionStage.getScene().getRoot().getUserData();
            SheetDTO previousSheetDTO = (SheetDTO) engine.getSheetsPreviousVersionsDTO().get(selectedVersion);
            controller.createDynamicGrid(previousSheetDTO);
            controller.buildGridBoundaries(previousSheetDTO);
            controller.createInnerCellsInGrid(previousSheetDTO);
            controller.disableGrid(true);

            // הצג את החלון אם הוא לא מוצג
            if (!previousVersionStage.isShowing()) {
                previousVersionStage.show();
            }

        } catch (IOException e) {
            showErrorDialog("Error", "Failed to load previous version.");
        }
    }

}
