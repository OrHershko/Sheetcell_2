package components.maingrid;

import dto.SheetDTO;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import main.AppController;

public class MainGridController {

    @FXML
    private GridPane mainGrid;

    private AppController appController;

    public void setAppController(AppController appController) {
        this.appController = appController;
    }

    public void buildGridBoundaries(SheetDTO sheetDTO) {
        // מחיקת תוכן קיים ב-GridPane אם יש כזה
        mainGrid.getChildren().clear();
        mainGrid.getColumnConstraints().clear();
        mainGrid.getRowConstraints().clear();

        // קבלת מספר השורות והעמודות מ-SheetDTO
        int numRows = sheetDTO.getNumOfRows();
        int numCols = sheetDTO.getNumOfCols();

        // הגדרת עמודות עם מאפיינים דינאמיים
        for (int i = 0; i <= numCols + 1; i++) { // הוספת עמודות קצה נוספות
            ColumnConstraints colConst = new ColumnConstraints();
            colConst.setMinWidth(50);
            colConst.setPrefWidth(100);
            if (i == 0 || i == numCols + 1) {
                colConst.setHgrow(Priority.ALWAYS); // העמודות הראשונות והאחרונות יתמתחו
            }
            mainGrid.getColumnConstraints().add(colConst);
        }

        // הגדרת שורות עם מאפיינים דינאמיים
        for (int i = 0; i <= numRows + 1; i++) { // הוספת שורות קצה נוספות
            RowConstraints rowConst = new RowConstraints();
            rowConst.setMinHeight(30);
            rowConst.setPrefHeight(40);
            if (i == 0 || i == numRows + 1) {
                rowConst.setVgrow(Priority.ALWAYS); // השורות הראשונות והאחרונות יתמתחו
            }
            mainGrid.getRowConstraints().add(rowConst);
        }

        // יצירת תוויות לעמודות (A, B, C וכו')
        for (int col = 1; col <= numCols; col++) {
            String columnName = String.valueOf((char) ('A' + col - 1));
            Label columnHeader = new Label(columnName);
            columnHeader.setStyle("-fx-font-weight: bold; -fx-border-color: black; -fx-border-width: 1;");
            columnHeader.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            columnHeader.setAlignment(javafx.geometry.Pos.CENTER);
            mainGrid.add(columnHeader, col, 1); // מיקום התווית בעמודה col ושורה 1
        }

        // יצירת תוויות לשורות (1, 2, 3 וכו')
        for (int row = 1; row <= numRows; row++) {
            String rowNumber = String.format("%02d", row);
            Label rowHeader = new Label(rowNumber);
            rowHeader.setStyle("-fx-font-weight: bold; -fx-border-color: black; -fx-border-width: 1;");
            rowHeader.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            rowHeader.setAlignment(javafx.geometry.Pos.CENTER);
            mainGrid.add(rowHeader, 1, row + 1); // מיקום התווית בשורה row ועמודה 1
        }

        // יצירת תאים ריקים
        for (int row = 1; row <= numRows; row++) {
            for (int col = 1; col <= numCols; col++) {
                Label cell = new Label();
                cell.setStyle("-fx-border-color: black; -fx-border-width: 1;");
                cell.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                mainGrid.add(cell, col, row + 1);
            }
        }
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
