package components.commands;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.RowConstraints;
import main.AppController;

import static main.AppController.showErrorDialog;

public class CommandsComponentController {
    @FXML
    private Button setRowWidthButton;

    @FXML
    private Button setColWidthButton;

    private AppController appController;

    @FXML
    private void initialize() {
        setColWidthButton.setDisable(true);
        setRowWidthButton.setDisable(true);
    }

    public void setAppController(AppController appController) {
        this.appController = appController;
    }

    public void disableButtons(boolean disable) {
        setRowWidthButton.setDisable(disable);
        setColWidthButton.setDisable(disable);
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



}
