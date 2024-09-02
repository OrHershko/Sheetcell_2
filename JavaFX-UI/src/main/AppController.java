package main;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class AppController {

    @FXML
    private BorderPane rootPane;  // שורש ה-FXML שלך, כנראה BorderPane מבוסס על התמונה ו-FXML שסיפקת

    @FXML
    private HBox hboxContainer; // HBox שמכיל את LoadFile.fxml ו-VersionsSelector.fxml

    @FXML
    public void initialize() {
        // הטעינה של ה-LoadFile.fxml ו-VersionsSelector.fxml נעשתה כבר דרך ה-`fx:include`
        // כאן אפשר לבצע אינטראקציות או לוודא שהכל נטען כראוי

        // אפשרות לטפל באינטראקציות בין ה-Controllers של הקומפוננטות השונות
        // לדוגמה: loadFileController.setAppController(this);
    }
}
