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
    private Label predefinedQueryLabel;

    @FXML
    private VBox tableListVBox;

    @FXML
    private Label tableListLabel;

    @FXML
    private VBox CustomVBox;

    @FXML
    public Button predefinedResultButton;

    @FXML
    public VBox predefinedQueryVBox;

    @FXML
    public TextArea textAreaForInput;

    @FXML
    public ComboBox<String> predefinedQueryBox;

    @FXML
    public Button buttonShowTable;

    @FXML
    public Button executeButton;

    @FXML
    public TextArea queryField;

    @FXML
    public ComboBox<String> dbTableBox = new ComboBox<>();

    @FXML
    private Label queryLabel;

    @FXML
    public TableView<ObservableList> table;

    @FXML
    private void initialize() {
        // Data
        fullFillPredefinedQueryList();
        fullFillTableList();
        // Other
        table.setEditable(false);
        buttonShowTable.setDisable(true);
        predefinedResultButton.setDisable(true);
    }

    private void fullFillPredefinedQueryList() {
        ObservableList<String> queryList = FXCollections.observableArrayList();
        for (int i = 1; i < 11; i++) {
            queryList.add("Query " + i);
        }
        predefinedQueryBox.setItems(queryList);
    }

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

    @FXML
    private void onButtonExecuteQueryClick() {
        clear();
        String query = queryField.getText().toUpperCase();
        if (query.length() > 7) {
            if (query.startsWith("SELECT ")) {
                buildViewByCommand(query);
            } else if (query.startsWith("INSERT INTO ") || query.startsWith("UPDATE ") || query.startsWith("DELETE ")) {
                SQLQuery.executeQueryNoOutput(query);
            }
        }
    }

    @FXML
    private void handlePredefinedComboBoxAction() {
        if (predefinedQueryBox.getValue().isEmpty()) {
            textAreaForInput.setPromptText("Input for the query");
            predefinedResultButton.setDisable(true);
        } else {
            predefinedResultButton.setDisable(false);
            textAreaForInput.clear();
            String numberOfQuery = predefinedQueryBox.getValue();
            switch (numberOfQuery) {
                case "Query 1":
                    textAreaForInput.setPromptText("Sample input: White AN");
                    break;
                case "Query 2":
                    textAreaForInput.setPromptText("Sample input: 2017-12-01");
                    break;
                case "Query 3":
                    textAreaForInput.setPromptText("Sample input: 2018-01-01");
                    break;
                case "Query 4":
                    textAreaForInput.setPromptText("Sample input: 01");
                    break;
                case "Query 5":
                    textAreaForInput.setPromptText("Sample input: 2018-01-01");
                    break;
                case "Query 6":
                    break;
                case "Query 7":
                    break;
                case "Query 8":
                    break;
                case "Query 9":
                    break;
                case "Query 10":
                    break;
            }
        }
    }

    @FXML
    private void onButtonShowQueryResultClick() {
        clear();
        String numberOfQuery = predefinedQueryBox.getValue();
        String input = textAreaForInput.getText().trim();
        Table t = null;
        switch (numberOfQuery) {
            case "Query 1":
                t = Predefined.findCar(input);
                break;
            case "Query 2":
                t = Predefined.socketsPerHour(input);
                break;
            case "Query 3":
                t = Predefined.busyPerPeriod(input);
                break;
            case "Query 4":
                t = Predefined.userPayments(input);
                break;
            case "Query 5":
                t = Predefined.rentStatistics(input);
                break;
            case "Query 6":
                //t = Predefined.socketsPerHour(input);
                break;
            case "Query 7":
                //t = Predefined.socketsPerHour(input);
                break;
            case "Query 8":
                //t = Predefined.socketsPerHour(input);
                break;
            case "Query 9":
                //t = Predefined.socketsPerHour(input);
                break;
            case "Query 10":
                //t = Predefined.socketsPerHour(input);
                break;
        }
        buildViewFromTable(t);
    }

    @FXML
    private void handleComboBoxAction() {
        if (dbTableBox.getValue().isEmpty()) {
            buttonShowTable.setDisable(true);
        } else {
            buttonShowTable.setDisable(false);
        }
    }

    @FXML
    private void onButtonShowClick() {
        clear();
        String tableNeeded = dbTableBox.getValue();
        String selectQuery = "SELECT * FROM " + tableNeeded;
        buildViewByCommand(selectQuery);
    }

    private void clear() {
        for (int i = 0; i < table.getItems().size(); i++) {
            table.getItems().clear();
        }
        table.getColumns().clear();
    }

    private void buildViewByCommand(String SQL) {
        buildViewFromTable(SQLQuery.executeQueryWithOutput(SQL));
    }

    private void buildViewFromTable(Table t) {
        if (t == null) return;

        ObservableList<String> row;
        TableColumn col;
        ObservableList<ObservableList> data = FXCollections.observableArrayList();
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
