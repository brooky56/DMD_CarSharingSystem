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
    public CheckBox updateQueryCheckBox;

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
            String numberOfQuery = predefinedQueryBox.getValue();
            switch (numberOfQuery) {
                case "Query 1":
                    textAreaForInput.setPromptText("White AN122131");
                    break;
                case "Query 2":
                    textAreaForInput.setPromptText("2017-12-01");
                    break;
                case "Query 3":
                    textAreaForInput.setPromptText("2018-01-01 07:00:00");
                    break;
                case "Query 4":
                    break;
                case "Query 5":
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
        String numberOfQuery = predefinedQueryBox.getValue();
        String input = textAreaForInput.getText();
        ;
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
                //t = Predefined.socketsPerHour(input);
                break;
            case "Query 5":
                //t = Predefined.socketsPerHour(input);
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
    private void onButtonExecuteQueryClick() {
        clear();
        String query = queryField.getText();
        boolean f = updateQueryCheckBox.isSelected();
        System.out.println(f);
        if (query != null && !f) {
            System.out.println(query + " " + f);
            buildViewByCommand(query);
        } else if (query != null && f) {
            clear();
            System.out.println(query + " " + f);
            SQLQuery.executeQueryNoOutput(query);
            dbTableBox.getItems().clear();
            fullFillTableList();
        } else
            executeButton.setDisable(false);
    }

    @FXML
    private void initialize() throws SQLException {
        initData();
        table.setEditable(false);
        buttonShowTable.setDisable(true);
        predefinedResultButton.setDisable(true);
    }

    private ObservableList<ObservableList> data;
    private ObservableList<String> row;
    private TableColumn col;

    private void clear() {
        for (int i = 0; i < table.getItems().size(); i++) {
            table.getItems().clear();
        }
        table.getColumns().clear();
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
