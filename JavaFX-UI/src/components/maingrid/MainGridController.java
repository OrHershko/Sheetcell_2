package components.maingrid;

import components.maingrid.cell.CellComponentController;
import dto.CellDTO;
import dto.SheetDTO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import main.AppController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainGridController {

    @FXML
    private GridPane mainGrid;

    private AppController appController;

    private Map<String, CellComponentController> cellComponentControllers = new HashMap<>();

    public void setAppController(AppController appController) {
        this.appController = appController;
    }

    public void buildGridBoundaries(SheetDTO sheetDTO) throws IOException {

        int numOfRows = sheetDTO.getNumOfRows();
        int numOfCols = sheetDTO.getNumOfCols();

        CellComponentController cellControllerCreated = createCell("",0,0, true);

        for (int i = 1; i <= numOfRows; i++) {
            String row = String.format("%02d", i);
            createCell(row, i,0, true);
        }

        for (int i = 1; i <= numOfCols; i++) {
            String col = String.format("%c", i - 1 + 'A');
            createCell(col, 0, i, true);
        }
    }

    public void createInnerCellsInGrid(SheetDTO sheetDTO) throws IOException {
        for(CellDTO cellDTO : sheetDTO.getActiveCells().values())
        {
            CellComponentController cell = cellComponentControllers.get(cellDTO.getIdentity());
            cell.setEffectiveValue(cellDTO.getEffectiveValue().getEffectiveValue().toString());
            cell.setCell(cellDTO);
        }
    }

    private CellComponentController createCell(String effectiveValue, int row, int column, boolean isDisable) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainGridController.class.getResource("/components/maingrid/cell/CellComponent.fxml"));
        Node newCell = loader.load();
        newCell.setDisable(isDisable);
        CellComponentController cellComponentController = loader.getController();
        cellComponentController.setEffectiveValue(effectiveValue);
        cellComponentController.setAppController(appController);
        GridPane.setColumnIndex(newCell, column + 1);
        GridPane.setRowIndex(newCell, row + 1);
        mainGrid.getChildren().add(newCell);

        newCell.getStyleClass().add("grid-cell");

        if (row == 0 || column == 0 || row == mainGrid.getRowCount() + 1 || column == mainGrid.getColumnCount() + 1) {
            newCell.getStyleClass().add("edge");
        }

        return cellComponentController;
    }


    // פונקציה להמרת מזהה השורה למספר שורה
    private int getRow(String cellId) {
        return Integer.parseInt(cellId.substring(1)) - 1; // לדוגמה "A1" -> 0
    }

    // פונקציה להמרת מזהה העמודה למספר עמודה
    private int getCol(String cellId) {
        return cellId.charAt(0) - 'A'; // לדוגמה "A1" -> 0, "B2" -> 1
    }

    public void createDynamicGrid(SheetDTO sheetDTO) throws IOException {
        mainGrid.getColumnConstraints().clear();
        mainGrid.getRowConstraints().clear();

        int numOfRows = sheetDTO.getNumOfRows();
        int numOfCols = sheetDTO.getNumOfCols();

        for (int col = 0; col <= numOfCols + 2 ; col++) {
            ColumnConstraints colConstraints = new ColumnConstraints();
            colConstraints.setPrefWidth(Region.USE_COMPUTED_SIZE);
            colConstraints.setMaxWidth(Double.MAX_VALUE);

            if (col == 0 || col == numOfCols + 2) {

                colConstraints.setHgrow(Priority.ALWAYS);
                colConstraints.setMinWidth(40.0);
            } else {

                colConstraints.setHgrow(Priority.NEVER);
                colConstraints.setMinWidth(75.0);
            }
            mainGrid.getColumnConstraints().add(colConstraints);
        }


        for (int row = 0; row <= numOfRows + 2 ; row++) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setPrefHeight(Region.USE_COMPUTED_SIZE);
            rowConstraints.setMaxHeight(Double.MAX_VALUE);
            rowConstraints.setMinHeight(40.0);


            if (row == 0 || row == numOfRows + 2) {
                rowConstraints.setVgrow(Priority.ALWAYS);
            } else {
                rowConstraints.setVgrow(Priority.NEVER);
            }

            mainGrid.getRowConstraints().add(rowConstraints);
        }

        for (int row = 1; row <= numOfRows; row++) {
            for (int col = 1; col <= numOfCols; col++) {
                String identity = String.format("%c", col + 'A' - 1) + row;
                CellComponentController cellComponentController = createCell("", row, col, false);
                cellComponentController.setCell(new CellDTO(identity));
                cellComponentControllers.put(identity, cellComponentController);
            }
        }
    }


}
