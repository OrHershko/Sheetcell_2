package components.bonuses;

import javafx.fxml.FXML;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import main.AppController;

public class BonusesController {

    private AppController appController;

    @FXML
    private MenuButton skinsMenuButton;

    @FXML
    private MenuButton animationsMenuButton;

    @FXML
    private MenuItem defaultSkinMenuItem;

    @FXML
    private MenuItem darkSkinMenuItem;

    @FXML
    private MenuItem lightSkinMenuItem;

    public void setAppController(AppController appController) {
        this.appController = appController;
    }

    @FXML
    public void initialize() {
        defaultSkinMenuItem.setOnAction(event -> appController.applySkin("default"));
        darkSkinMenuItem.setOnAction(event -> appController.applySkin("dark"));
        lightSkinMenuItem.setOnAction(event -> appController.applySkin("light"));
    }
}
