package components.commands;

import components.maingrid.cell.CellComponentController;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.AppController;

import java.util.Optional;

import static main.AppController.showErrorDialog;

public class CommandsComponentController {
    @FXML
    private Button setRowWidthButton;

    @FXML
    private Button setColWidthButton;

    @FXML
    private Button setColAlignmentButton;

    @FXML
    private Button designCell;

    private AppController appController;

    @FXML
    private void initialize() {
        setColWidthButton.setDisable(true);
        setRowWidthButton.setDisable(true);
        setColAlignmentButton.setDisable(true);
    }

    public void setAppController(AppController appController) {
        this.appController = appController;
    }

    public void disableButtons(boolean disable) {
        setRowWidthButton.setDisable(disable);
        setColWidthButton.setDisable(disable);
        setColAlignmentButton.setDisable(disable);
    }

    @FXML
    public void designCellOnClick() {
        Optional<String> result = getCellIDFromUser();

        if (result.isPresent()) {
            String cellId = result.get().toUpperCase(); // מקבל את שם התא

            // קבלת CellComponentController מה-AppController
            CellComponentController cell = appController.getCellControllerById(cellId);

            if (cell != null) {
                // אם התא קיים, הצגת פופ-אפ לבחירת צבעים
                Stage styleStage = new Stage();
                styleStage.setTitle("Set Style for Cell " + cellId);

                // יצירת ColorPickers עבור צבע רקע וצבע טקסט
                ColorPicker backgroundColorPicker = new ColorPicker();
                ColorPicker textColorPicker = new ColorPicker();

                Button applyButton = createApplyButton(backgroundColorPicker, cell, textColorPicker, styleStage);
                Button resetButton = createResetButton(cell, styleStage);

                // הוספת כל הרכיבים לפריסת VBox
                VBox vbox = new VBox(10);
                vbox.getChildren().addAll(new Label("Background Color:"), backgroundColorPicker,
                        new Label("Text Color:"), textColorPicker,
                        applyButton, resetButton);

                // הגדרת הפריסה של החלון
                Scene scene = new Scene(vbox, 300, 200);
                styleStage.setScene(scene);

                // הגדרת החלון כמודאלי (modal)
                styleStage.initModality(Modality.APPLICATION_MODAL);

                // הצגת הפופ-אפ
                styleStage.showAndWait();
            }
            else {
                AppController.showErrorDialog("Error", "The cell ID you entered does not exist.");
            }
        }
    }

    private static Button createResetButton(CellComponentController cell, Stage styleStage) {
        // כפתור לאיפוס הסגנון של התא
        Button resetButton = new Button("Reset Cell Style");
        resetButton.setOnAction(event -> {
            cell.getCellLabel().setStyle(""); // איפוס העיצוב
            styleStage.close();
        });
        return resetButton;
    }

    private Button createApplyButton(ColorPicker backgroundColorPicker, CellComponentController cell, ColorPicker textColorPicker, Stage styleStage) {
        // כפתור להחלת צבעים
        Button applyButton = new Button("Apply Colors");
        applyButton.setOnAction(event -> {
            // קביעת צבע רקע
            String backgroundColor = toRgbString(backgroundColorPicker.getValue());
            cell.getCellLabel().setStyle("-fx-background-color: " + backgroundColor + ";");

            // קביעת צבע טקסט
            String textColor = toRgbString(textColorPicker.getValue());
            cell.getCellLabel().setStyle(cell.getCellLabel().getStyle() + "-fx-text-fill: " + textColor + ";");

            // סגירת החלון
            styleStage.close();
        });
        return applyButton;
    }

    private static Optional<String> getCellIDFromUser() {
        // יצירת חלון פופ-אפ חדש
        Stage popupStage = new Stage();
        popupStage.setTitle("Select Cell and Set Style");

        // בקשת הזנת שם התא (למשל "A1")
        TextInputDialog inputDialog = new TextInputDialog();
        inputDialog.setTitle("Select Cell");
        inputDialog.setHeaderText("Enter the cell ID (e.g., A1, B2) to modify:");
        inputDialog.setContentText("Cell ID:");

        // הצגת הדיאלוג וקבלת תוצאת המשתמש
        return inputDialog.showAndWait();
    }

    // פונקציה להמרת צבע לערכי RGB
    private String toRgbString(Color color) {
        return String.format("rgb(%d, %d, %d)",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }
    @FXML
    private void setRowsWidthOnClick() {
        // יצירת דיאלוג להזנת רוחב
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Set Row Width");
        dialog.setHeaderText("Set the width for the rows");
        dialog.setContentText("Please enter the desired row width:");

        // הצגת הדיאלוג וחכות לתשובת המשתמש
        dialog.showAndWait().ifPresent(input -> {
            try {
                // המרת הקלט ממחרוזת למספר
                int width = Integer.parseInt(input);

                // בדוק שהרוחב שהוזן תקין (לא שלילי או קטן מדי)
                if (width > 0) {
                    appController.changeRowsWidth(width);
                } else {
                    // הצג הודעת שגיאה אם הערך אינו תקין
                    showErrorDialog("Invalid input", "Row width must be a positive number.");
                }
            } catch (NumberFormatException e) {
                // הצגת הודעת שגיאה במקרה של קלט לא חוקי
                showErrorDialog("Invalid input", "Please enter a valid number.");
            }
        });
    }

    @FXML
    private void setColsWidthOnClick() {
        // יצירת דיאלוג להזנת רוחב
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Set Columns Width");
        dialog.setHeaderText("Set the width for the columns");
        dialog.setContentText("Please enter the desired columns width:");

        // הצגת הדיאלוג וחכות לתשובת המשתמש
        dialog.showAndWait().ifPresent(input -> {
            try {
                // המרת הקלט ממחרוזת למספר
                int width = Integer.parseInt(input);

                // בדוק שהרוחב שהוזן תקין (לא שלילי או קטן מדי)
                if (width > 0) {
                    appController.changeColsWidth(width);
                } else {
                    // הצג הודעת שגיאה אם הערך אינו תקין
                    showErrorDialog("Invalid input", "Column width must be a positive number.");
                }
            } catch (NumberFormatException e) {
                // הצגת הודעת שגיאה במקרה של קלט לא חוקי
                showErrorDialog("Invalid input", "Please enter a valid number.");
            }
        });
    }

    @FXML
    public void setColsAlignmentOnClick() {
        // יצירת תיבת טקסט לקבלת שם העמודה
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Set Columns Alignment");
        dialog.setHeaderText("Set the alignment for the columns");
        dialog.setContentText("Please enter the column name (e.g., A, B, C):");

        // הצגת הדיאלוג וחילוץ הערך שהמשתמש הכניס
        Optional<String> result = dialog.showAndWait();

        if (result.isPresent()) {
            String columnName = result.get().toUpperCase();

            // יצירת תיבת בחירה (ComboBox) עבור יישור העמודה
            ChoiceDialog<String> alignmentDialog = new ChoiceDialog<>("Alignment", "Left", "Center", "Right");
            alignmentDialog.setTitle("Select Alignment");
            alignmentDialog.setHeaderText("Choose the alignment for column " + columnName);
            alignmentDialog.setContentText("Select alignment:");

            // הצגת תיבת הבחירה וחילוץ הבחירה
            Optional<String> alignmentResult = alignmentDialog.showAndWait();
            if (alignmentResult.isPresent()) {
                String alignment = alignmentResult.get();

                // המרת שם העמודה לאינדקס העמודה
                int columnIndex = columnName.charAt(0) - 'A' + 1; // הנחת שהעמודה היא A, B, C וכו'

                // עדכון היישור של כל התאים בעמודה שנבחרה
                appController.updateColumnAlignment(columnIndex, alignment);
            }
        }
    }


}
