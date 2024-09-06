package components.maingrid;

import components.maingrid.cell.CellComponentController;
import dto.CellDTO;
import dto.SheetDTO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.*;
import main.AppController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainGridController {

    @FXML
    private GridPane mainGrid;

    private AppController appController;

    private final Map<String, CellComponentController> cellComponentControllers = new HashMap<>();

    public void setAppController(AppController appController) {
        this.appController = appController;
    }

    public void buildGridBoundaries(SheetDTO sheetDTO) throws IOException {

        int numOfRows = sheetDTO.getNumOfRows();
        int numOfCols = sheetDTO.getNumOfCols();

        createCell("",0,0, true);

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
        Region newCell = loader.load();
        newCell.setDisable(isDisable);
        CellComponentController cellComponentController = loader.getController();
        cellComponentController.setEffectiveValue(effectiveValue);
        cellComponentController.setAppController(appController);
        cellComponentController.setCellSize();
        GridPane.setColumnIndex(newCell, column + 1);
        GridPane.setRowIndex(newCell, row + 1);
        mainGrid.getChildren().add(newCell);
        newCell.getStyleClass().add("grid-cell");

        if (row == 0 || column == 0 || row == mainGrid.getRowCount() + 1 || column == mainGrid.getColumnCount() + 1) {
            newCell.getStyleClass().add("edge");
        }

        return cellComponentController;
    }

    public void createDynamicGrid(SheetDTO sheetDTO) throws IOException {
        mainGrid.getChildren().clear();
        mainGrid.getColumnConstraints().clear();
        mainGrid.getRowConstraints().clear();
        cellComponentControllers.clear();

        int numOfRows = sheetDTO.getNumOfRows();
        int numOfCols = sheetDTO.getNumOfCols();

        createColsInGrid(numOfCols);
        createRowsInGrid(numOfRows);
        createEmptyCellsInGrid(numOfRows, numOfCols);
    }

    private void createEmptyCellsInGrid(int numOfRows, int numOfCols) throws IOException {
        for (int row = 1; row <= numOfRows; row++) {
            for (int col = 1; col <= numOfCols; col++) {
                String identity = String.format("%c", col + 'A' - 1) + row;
                CellComponentController cellComponentController = createCell("", row, col, false);
                cellComponentController.setCell(new CellDTO(identity));
                cellComponentControllers.put(identity, cellComponentController);
            }
        }
    }

    private void createRowsInGrid(int numOfRows) {
        for (int row = 0; row <= numOfRows + 2 ; row++) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setPrefHeight(Region.USE_COMPUTED_SIZE);
            rowConstraints.setMaxHeight(Double.MAX_VALUE);
            rowConstraints.setMinHeight(Region.USE_COMPUTED_SIZE);


            if (row == 0 || row == numOfRows + 2) {
                rowConstraints.setVgrow(Priority.ALWAYS);
            } else {
                rowConstraints.setVgrow(Priority.NEVER);
            }

            mainGrid.getRowConstraints().add(rowConstraints);
        }
    }

    private void createColsInGrid(int numOfCols) {
        for (int col = 0; col <= numOfCols + 2 ; col++) {
            ColumnConstraints colConstraints = new ColumnConstraints();
            colConstraints.setPrefWidth(Region.USE_COMPUTED_SIZE);
            colConstraints.setMaxWidth(Double.MAX_VALUE);
            colConstraints.setMinWidth(Region.USE_COMPUTED_SIZE);


            if (col == 0 || col == numOfCols + 2) {

                colConstraints.setHgrow(Priority.ALWAYS);
//                colConstraints.setMinWidth(40.0);
            } else {

                colConstraints.setHgrow(Priority.NEVER);
//                colConstraints.setMinWidth(75.0);
            }
            mainGrid.getColumnConstraints().add(colConstraints);
        }
    }

    public void activateMouseClickedOfCell(String selectedCellId) {
        cellComponentControllers.get(selectedCellId).onMouseClicked();
    }

    public void updateRowsConstraints(int width) {
        int rowCount = mainGrid.getRowConstraints().size();

        for (int i = 0; i < rowCount; i++) {

            if (i == 0 || i == rowCount - 1) {
                continue;
            }

            RowConstraints rowConstraints = mainGrid.getRowConstraints().get(i);
            rowConstraints.setPrefHeight(width);
        }
    }

    public void updateColsConstraints(int width) {
        int colCount = mainGrid.getColumnConstraints().size();

        for (int i = 0; i < colCount; i++) {

            if (i == 0 || i == colCount - 1) {
                continue;
            }

            ColumnConstraints columnConstraints = mainGrid.getColumnConstraints().get(i);
            columnConstraints.setPrefWidth(width);
        }
    }

    public void updateColAlignment(int columnIndex, String alignment) {
        for (Node node : mainGrid.getChildren()) {
            Integer col = GridPane.getColumnIndex(node);
            if (col != null && col == columnIndex + 1) {
                if (alignment.equals("Left")) {
                    node.getStyleClass().removeAll("align-center", "align-right");
                    node.getStyleClass().add("align-left");
                } else if (alignment.equals("Center")) {
                    node.getStyleClass().removeAll("align-left", "align-right");
                    node.getStyleClass().add("align-center");
                } else if (alignment.equals("Right")) {
                    node.getStyleClass().removeAll("align-left", "align-center");
                    node.getStyleClass().add("align-right");
                }
            }
        }
    }

}
