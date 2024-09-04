package components.maingrid.cell;


import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class CellComponentController {

    @FXML
    private Label effectiveValue;

    public Label getEffectiveValue() {
        return effectiveValue;
    }

    public void setEffectiveValue(String effectiveValue) {
        this.effectiveValue.setText(effectiveValue);
    }
}

