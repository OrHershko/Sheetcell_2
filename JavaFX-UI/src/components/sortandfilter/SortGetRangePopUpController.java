package components.sortandfilter;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import main.AppController;

import java.util.List;

public class SortGetRangePopUpController {

    @FXML
    private TextField topLeftTextBox;
    @FXML
    private TextField bottomRightTextBox;
    @FXML
    private Button nextButton;

    private SortAndFilterController sortAndFilterController;

    private String topLeft;
    private String bottomRight;

    public void setSortAndFilterController(SortAndFilterController sortAndFilterController) {
        this.sortAndFilterController = sortAndFilterController;
    }

    @FXML
    private void nextButtonOnClick() {
        topLeft = topLeftTextBox.getText().toUpperCase();
        bottomRight = bottomRightTextBox.getText().toUpperCase();

        if (topLeft.isEmpty() || bottomRight.isEmpty()) {
            AppController.showErrorDialog("Error","Please enter both top-left and bottom-right cells.");
            return;
        }

        try {
            if(!sortAndFilterController.isRangeValid(topLeft,bottomRight)){
                AppController.showErrorDialog("Error","Make sure the range in valid.");
                return;
            }
            openSortDialog(topLeft, bottomRight);

            Stage stage = (Stage) nextButton.getScene().getWindow();
            stage.close();
        }
        catch (Exception e) {
            AppController.showErrorDialog("Error",e.getMessage());
        }

    }

    private void openSortDialog(String topLeft, String bottomRight) {
        try {
            // Load the FXML file for range input
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/components/sortandfilter/SortGetColumnsPopUp.fxml"));
            Parent root = fxmlLoader.load();
            SortGetColumnsPopUpController controller = fxmlLoader.getController();
            controller.setSortGetRangePopUpController(this);
            controller.initChoiceBox(topLeft, bottomRight);

            // Create a new stage (window)
            Stage stage = new Stage();
            stage.setTitle("Sort");
            stage.setScene(new Scene(root)); // Adjust the scene size if needed
            stage.show();
            stage.sizeToScene();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sort(List<String> columnToSortBy) {
        sortAndFilterController.sort(columnToSortBy, topLeft, bottomRight);
    }
}
