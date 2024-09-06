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

    public void setCellSize(){
        effectiveValue.setMinHeight(appController.getPrefRowHeight());
        effectiveValue.setMinWidth(appController.getPrefColWidth());
    }

    public Label getCellLabel() {
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
    public void onMouseClicked() {
        appController.displayCellDataOnActionLine(cell);
        appController.colorDependencies(cell.getCellsImDependentOn(),"DependentOn");
        appController.colorDependencies(cell.getCellsImInfluencing(),"Influencing");
    }


}

