package components.sortandfilter;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import main.AppController;

import java.util.List;


public class SortAndFilterController {

    @FXML
    private Button sortButton;

    @FXML
    private Button filterButton;

    private AppController appController;

    public void setAppController(AppController appController) {
        this.appController = appController;
    }

    @FXML
    private void sortOnClick() {
        try {
            // Load the FXML file for range input
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/components/sortandfilter/SortGetRangePopUp.fxml"));
            Parent root = fxmlLoader.load();

            SortGetRangePopUpController controller = fxmlLoader.getController();
            controller.setSortAndFilterController(this);

            // Create a new stage (window)
            Stage stage = new Stage();
            stage.setTitle("Enter Range");
            stage.setScene(new Scene(root, 400, 200)); // Adjust the scene size if needed
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void filterOnClick() {

    }

    public boolean isRangeValid(String topLeft, String bottomRight) {
        try{
            return appController.checkRangeOfCells(topLeft,bottomRight);
        }
        catch (Exception e) {
            throw new RuntimeException("Error: Please enter a cell in the format A1, B2, etc.");
        }
    }

    public void sort(List<String> columnToSortBy, String topLeft, String bottomRight) {
        appController.sortSheetByColumns(columnToSortBy, topLeft, bottomRight);
    }
}
