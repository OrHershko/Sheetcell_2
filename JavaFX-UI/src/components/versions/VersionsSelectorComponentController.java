package components.versions;

import api.DTO;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import main.AppController;

import java.util.Map;

public class VersionsSelectorComponentController{

    @FXML
    private ChoiceBox<String> versionsSelectorComponent;

    private AppController appController;

    @FXML
    private void initialize() {
        versionsSelectorComponent.setValue("Version Selector");
        versionsSelectorComponent.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                int selectedVersion = Integer.parseInt(newValue);
                appController.loadPreviousVersion(selectedVersion);
            }
        });
    }

    public void setAppController(AppController appController) {
        this.appController = appController;
    }

    @FXML
    public void openVersionsSelectorOnClick() {

        Map<Integer, DTO> previousVersionsDTO;

        try{
            previousVersionsDTO = appController.getSheetsPreviousVersionsDTO();
        }
        catch(Exception e){
            return;
        }

        versionsSelectorComponent.getItems().clear();

        for (Map.Entry<Integer, DTO> entry : previousVersionsDTO.entrySet()) {
            versionsSelectorComponent.getItems().add(String.valueOf(entry.getKey()));
        }

    }



}
