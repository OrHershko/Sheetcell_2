package components.maingrid.cell;


import dto.CellDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import main.AppController;

public class CellComponentController {

    @FXML
    private Label effectiveValue;

    private CellDTO cell;

    private AppController appController;

    public Label getEffectiveValue() {
        return effectiveValue;
    }

    public void setEffectiveValue(String effectiveValue) {
        this.effectiveValue.setText(effectiveValue);
    }

    public void setCell(CellDTO cell) {
        this.cell = cell;
    }

    public void setAppController(AppController appController) {
        this.appController = appController;
    }

    @FXML
    public void showCellOnActionLine() {
        appController.displayCellDataOnActionLine(cell);
    }
}

