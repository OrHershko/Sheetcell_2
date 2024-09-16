package components.sortandfilter;

import javafx.scene.control.ChoiceBox;

public interface ColumnActionController {
    void setChoiceBox(ChoiceBox<String> choiceBox);
    void setGetRangePopUpController(GetRangePopUpController getRangePopUpController);
}
