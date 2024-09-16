package components.sortandfilter;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import main.AppController;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;


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
    private void initialize() {
        sortButton.setDisable(true);
        filterButton.setDisable(true);
    }

    public void disableButtons(boolean disable) {
        sortButton.setDisable(disable);
        filterButton.setDisable(disable);
    }

    @FXML
    private void sortOnClick() {
        try {
            openGetRangeDialog(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openGetRangeDialog(boolean isSorting) throws IOException {
        // Load the FXML file for range input
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/components/sortandfilter/GetRangePopUp.fxml"));
        Parent root = fxmlLoader.load();

        GetRangePopUpController controller = fxmlLoader.getController();
        controller.setSortingOperation(isSorting);
        controller.setSortAndFilterController(this);

        // Create a new stage (window)
        Stage stage = new Stage();
        stage.setTitle("Enter Range");
        stage.setScene(new Scene(root, 400, 200)); // Adjust the scene size if needed
        stage.show();
    }

    @FXML
    private void filterOnClick() {
        try {
            openGetRangeDialog(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public Set<String> getValuesFromColumn(String column, String topLeft, String bottomRight) {
        return appController.getValuesFromColumn(column,topLeft,bottomRight);
    }

    public void filter(Map<String, Set<String>> colToSelectedValues, String topLeft, String bottomRight) {
        appController.filter(colToSelectedValues,topLeft,bottomRight);
    }
}
