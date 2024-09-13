package components.sortandfilter;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class SortGetColumnsPopUpController {

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox SortGetColumnsPopUp;

    @FXML
    private ChoiceBox<String> columnsChoiceBox;

    @FXML
    private Button addSortColumnButton;

    @FXML
    private Button sortButton;

    private SortGetRangePopUpController sortGetRangePopUpController;

    private List<ChoiceBox<String>> addedColumns = new ArrayList<>();

    public void setSortGetRangePopUpController(SortGetRangePopUpController sortGetRangePopUpController) {
        this.sortGetRangePopUpController = sortGetRangePopUpController;
    }


    public void initChoiceBox(String topLeft, String bottomRight) {

        String startColumn = topLeft.replaceAll("\\d", "");
        String endColumn = bottomRight.replaceAll("\\d", "");

        List<String> columnRange = getColumnRange(startColumn, endColumn);

        columnsChoiceBox.getItems().clear();
        for (String column : columnRange) {
            columnsChoiceBox.getItems().add("Column " + column); // Add "Column" before each column letter
        }
        columnsChoiceBox.setValue("Choose Column");
    }

    private List<String> getColumnRange(String start, String end) {
        List<String> columns = new ArrayList<>();

        int startChar = start.charAt(0);
        int endChar = end.charAt(0);

        for (int i = startChar; i <= endChar; i++) {
            columns.add(String.valueOf((char) i));
        }
        return columns;
    }



    @FXML
    private void addColumnToSortOnClick() {

        ChoiceBox<String> newChoiceBox = new ChoiceBox<>();
        addedColumns.add(newChoiceBox);
        newChoiceBox.getItems().addAll(columnsChoiceBox.getItems());

        HBox newHBox = new HBox(10);
        Label newLabel = new Label("Then by:");
        newHBox.getChildren().addAll(newLabel, newChoiceBox);
        Insets padding = new Insets(10.0, 10.0, 10.0, 10.0);
        newHBox.setPadding(padding);

        SortGetColumnsPopUp.getChildren().add(SortGetColumnsPopUp.getChildren().size() - 2, newHBox); // הוספתו לפני הכפתור "Sort"
        Stage stage = (Stage) scrollPane.getScene().getWindow();
        stage.sizeToScene();
    }

    @FXML
    private void sortOnClick(){
        List<String> columnToSortBy = new ArrayList<>();
        columnToSortBy.add(columnsChoiceBox.getValue());
        for (ChoiceBox<String> column : addedColumns) {
            columnToSortBy.add(column.getValue());
        }

        sortGetRangePopUpController.sort(columnToSortBy);
    }
}
