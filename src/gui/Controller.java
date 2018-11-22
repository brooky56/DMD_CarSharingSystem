package gui;

import db.Predefined;
import db.SQLQuery;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import main.Common;
import main.Table;

import java.sql.SQLException;

public class Controller {

    @FXML
    private CheckBox updateQueryCheckBox;

    @FXML
    private HBox CustomFieldHBox;

    @FXML
    private Label predefinedQueryLabel;

    @FXML
    private VBox tableListVBox;

    @FXML
    private Label tableListLabel;

    @FXML
    private VBox CustomVBox;

    @FXML
    private Button predefinedResultButton;

    @FXML
    private VBox predefinedQueryVBox;

    @FXML
    private TextArea textAreaForInput;

    @FXML
    private ComboBox<String> predefinedQueryBox;

    @FXML
    private Button buttonShowTable;

    @FXML
    private Button executeButton;

    @FXML
    private TextField queryField;

    @FXML
    private ComboBox<String> dbTableBox = new ComboBox<>();

    @FXML
    private Label queryLabel;

    @FXML
    private TableView<ObservableList> table;

    private void fullFillTableList() {
        ObservableList<String> tableList = FXCollections.observableArrayList();
        Table t = SQLQuery.executeQueryWithOutput(
                "SELECT name FROM sqlite_master WHERE type = 'table' AND name <> 'sqlite_master' AND name <> 'sqlite_sequence'");
        for (int i = 1; i < t.height; ++i) {
            tableList.add(t.getCell(i, 0).toString());
            System.out.println(t.getCell(i, 0).toString());
        }
        dbTableBox.setItems(tableList);
    }

    private void fullFillPredefinedQueryList() {
        ObservableList<String> queryList = FXCollections.observableArrayList();
        for (int i = 1; i < 11; i++) {
            queryList.add("Query " + i);
        }
        predefinedQueryBox.setItems(queryList);
    }

    private void initData() throws SQLException {
        fullFillTableList();
        fullFillPredefinedQueryList();
    }

    @FXML
    private void handleTextFieldAction() {
        if (!queryField.getText().equals("")) {
            executeButton.setDisable(false);
        } else {
            executeButton.setDisable(true);
        }
    }

    @FXML
    private void handlePredefinedComboBoxAction() {
        if (!predefinedQueryBox.getValue().equals("")) {
            predefinedResultButton.setDisable(false);
        } else {
            predefinedResultButton.setDisable(true);
        }
    }

    @FXML
    private void handleComboBoxAction() {
        if (!dbTableBox.getValue().equals("")) {
            buttonShowTable.setDisable(false);
        } else {
            buttonShowTable.setDisable(true);
        }
    }

    @FXML
    private void onButtonShowClick() {
        clear();
        String tableNeeded = dbTableBox.getValue();
        String selectQuery = "SELECT * FROM " + tableNeeded;
        buildViewByCommand(selectQuery);
    }

    @FXML
    private void onButtonShowQueryResultClick() {
        clear();
        String input = textAreaForInput.getText();
        String numberOfQuery = predefinedQueryBox.getValue();

        Table t = null;
        switch (numberOfQuery) {
            case "Query 1":
                t = Predefined.findCar(input);
                break;
            case "Query 2":
                t = Predefined.socketsPerHour(input);
                break;
        }
        buildViewFromTable(t);
    }

    private void clear() {
        for (int i = 0; i < table.getItems().size(); i++) {
            table.getItems().clear();
        }
        table.getColumns().clear();
    }

    @FXML
    private void onButtonExecuteQueryClick() {
        clear();
        String query = queryField.getText();
        boolean f = updateQueryCheckBox.isSelected();
        System.out.println(f);
        if (query != null && !f) {
            System.out.println(query + " " + f);
            buildViewByCommand(query);
        } else if (query != null && f) {
            System.out.println(query + " " + f);
            SQLQuery.executeQueryNoOutput(query);
        } else
            executeButton.setDisable(false);
    }

    @FXML
    private void initialize() throws SQLException {
        initData();
        table.setEditable(false);
        executeButton.setDisable(true);
        buttonShowTable.setDisable(true);
        predefinedResultButton.setDisable(true);
    }

    private ObservableList<ObservableList> data;
    private ObservableList<String> row;
    private TableColumn col;

    private void buildViewByCommand(String SQL) {
        buildViewFromTable(SQLQuery.executeQueryWithOutput(SQL));
    }

    private void buildViewFromTable(Table t) {
        if (t == null) return;

        data = FXCollections.observableArrayList();
        for (int i = 0; i < t.width; i++) {
            col = new TableColumn(t.getTitle(i).toString());
            final int j = i;
            col.setCellValueFactory((Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>) param -> new SimpleStringProperty(param.getValue().get(j).toString()));
            table.getColumns().add(col);
            System.out.println("Column [" + i + "] ");
        }

        for (int i = 1; i < t.height; i++) {
            row = FXCollections.observableArrayList();
            for (int j = 0; j < t.width; j++) {
                if (t.getCell(i, j) == null) {
                    row.add(Common.NULL_ELEMENT);
                } else {
                    row.add(t.getCell(i, j).toString());
                }
            }
            System.out.println("Row [" + i + "] added " + row);
            data.add(row);
        }
        table.setItems(data);
    }
}
