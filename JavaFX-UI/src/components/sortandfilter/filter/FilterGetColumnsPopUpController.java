package components.sortandfilter.filter;

import components.sortandfilter.ColumnActionController;
import components.sortandfilter.GetRangePopUpController;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.*;

public class FilterGetColumnsPopUpController implements ColumnActionController {
    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox filterGetColumnsPopUp;

    @FXML
    private ChoiceBox<String> columnsChoiceBox;

//    @FXML
//    private ChoiceBox<String> valuesChoiceBox;

    @FXML
    private ListView<String> valuesChoiceBox;

    @FXML
    private Button addFilterColumnButton;

    @FXML
    private Button filterButton;

    private GetRangePopUpController getRangePopUpController;

    private List<ChoiceBox<String>> addedColumns = new ArrayList<>();

    //private List<ChoiceBox<String>> addedValues = new ArrayList<>();

    @FXML
    private ListView<String> valuesListView;

    private List<ListView<String>> addedValues = new ArrayList<>();


    @Override
    public void setGetRangePopUpController(GetRangePopUpController getRangePopUpController) {
        this.getRangePopUpController = getRangePopUpController;
    }


    @Override
    public void setChoiceBox(ChoiceBox<String> choiceBox) {
        this.columnsChoiceBox.setItems(choiceBox.getItems());
        this.columnsChoiceBox.setValue("Column");
        this.valuesChoiceBox.setValue("Value");

        addListenerToColumnsChoiceBox(columnsChoiceBox, valuesChoiceBox);
    }

    private void addListenerToColumnsChoiceBox(ChoiceBox<String> columns , ChoiceBox<String> values) {
        columns.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Set<String> valuesInCol = getRangePopUpController.getValuesFromColumn(newValue.replace("Column ", "").trim());
                values.getItems().clear();
                values.getItems().addAll(valuesInCol);
            }
        });
    }

    @FXML
    private void addColumnToFilterOnClick() {

        ChoiceBox<String> newChoiceBox = new ChoiceBox<>();
        newChoiceBox.setValue("Column");
        addedColumns.add(newChoiceBox);
        newChoiceBox.getItems().addAll(columnsChoiceBox.getItems());
        ChoiceBox<String> newValueChoiceBox = new ChoiceBox<>();
        newValueChoiceBox.setValue("Value");
        newValueChoiceBox.setMinWidth(90);
        newValueChoiceBox.setPrefWidth(90);
        addedValues.add(newValueChoiceBox);
        valuesChoiceBox.minWidthProperty().bind(newChoiceBox.widthProperty());
        addListenerToColumnsChoiceBox(newChoiceBox, newValueChoiceBox);

        HBox newHBox = new HBox(10);
        Label newLabel = new Label("Then by:");
        newHBox.getChildren().addAll(newLabel, newChoiceBox, newValueChoiceBox);
        Insets padding = new Insets(10.0, 10.0, 10.0, 10.0);
        newHBox.setPadding(padding);

        filterGetColumnsPopUp.getChildren().add(filterGetColumnsPopUp.getChildren().size() - 2, newHBox);
        Stage stage = (Stage) scrollPane.getScene().getWindow();
        stage.sizeToScene();
    }

    @FXML
    private void filterOnClick(){
        Map<String, String> colToSelectedValues = new HashMap<>();

        colToSelectedValues.put(columnsChoiceBox.getValue().replace("Column ", "").trim(), valuesChoiceBox.getValue());

        for (int i = 0; i < addedColumns.size(); i++) {
            colToSelectedValues.put(addedColumns.get(i).getValue(), addedValues.get(i).getValue());
        }

        getRangePopUpController.filter(colToSelectedValues);
    }
}
