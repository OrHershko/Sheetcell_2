package components.maingrid;

import components.maingrid.cell.CellComponentController;
import dto.CellDTO;
import dto.SheetDTO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import main.AppController;

import java.io.IOException;

public class MainGridController {

    @FXML
    private GridPane mainGrid;

    private AppController appController;

    public void setAppController(AppController appController) {
        this.appController = appController;
    }

    public void buildGridBoundaries(SheetDTO sheetDTO) throws IOException {

        int numOfRows = sheetDTO.getNumOfRows();
        int numOfCols = sheetDTO.getNumOfCols();

        createCell("",0,0);

        for (int i = 1; i <= numOfRows; i++) {
            String row = String.format("%02d", i);
            createCell(row, i,0);
        }

        for (int i = 1; i <= numOfCols; i++) {
            String col = String.format("%c", i - 1 + 'A');
            createCell(col, 0, i);
        }


        for(CellDTO cellDTO : sheetDTO.getActiveCells().values())
        {
            createCell(cellDTO.getEffectiveValue().getEffectiveValue().toString(),cellDTO.getRowNumberFromCellId(),cellDTO.getColumnNumberFromCellId());
        }


    }

    private void createCell(String effectiveValue, int row, int column) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainGridController.class.getResource("/components/maingrid/cell/CellComponent.fxml"));
        Node newCell = loader.load();
        CellComponentController cellComponentController = loader.getController();
        cellComponentController.setEffectiveValue(effectiveValue);
        GridPane.setColumnIndex(newCell, column + 1);
        GridPane.setRowIndex(newCell, row + 1);
        mainGrid.getChildren().add(newCell);
    }


    // פונקציה להמרת מזהה השורה למספר שורה
    private int getRow(String cellId) {
        return Integer.parseInt(cellId.substring(1)) - 1; // לדוגמה "A1" -> 0
    }

    // פונקציה להמרת מזהה העמודה למספר עמודה
    private int getCol(String cellId) {
        return cellId.charAt(0) - 'A'; // לדוגמה "A1" -> 0, "B2" -> 1
    }
}
