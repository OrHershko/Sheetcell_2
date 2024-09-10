package components.actionline;

import dto.CellDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import main.AppController;

import java.io.IOException;

public class ActionLineController {

    @FXML
    private Label selectedCellId;

    @FXML
    private Label originalCellValue;

    @FXML
    private Label lastUpdateCellVersion;

    @FXML
    private TextField textField;

    @FXML
    private Button updateCellButton;

    private String currentCellId;

    private AppController appController;

    public void setAppController(AppController appController) {
        this.appController = appController;
    }

    @FXML
    private void initialize() {
        updateCellButton.setDisable(true);
        textField.setDisable(true);
    }


    public void displayCellData(CellDTO cell) {
        currentCellId = cell.getIdentity();
        selectedCellId.setText("Selected Cell ID: " + currentCellId);
        originalCellValue.setText("Original Cell Value: " + cell.getOriginalValue());
        if(cell.getVersion() != 0)
        {
            lastUpdateCellVersion.setText("Last Update Cell Version: " + cell.getVersion());
        }
        else {
            lastUpdateCellVersion.setText("Last Update Cell Version: ");
        }
        updateCellButton.setDisable(false);
        textField.setDisable(false);
    }

    @FXML
    public void updateCell(){
        try {
            appController.updateCellDataToEngine(currentCellId ,textField.getText());
        }
        catch (Exception e) {
            AppController.showErrorDialog("Update Cell Error", e.getMessage());
        }
    }
}
