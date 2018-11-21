package gui;

import db.SQLQuery;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;
import main.Common;
import objects.Table;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Controller {

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

    private void initData() throws SQLException {
        ObservableList<String> tableList = FXCollections.observableArrayList();
        ResultSet tableSet = main.Common.connection().createStatement().executeQuery("SELECT name FROM sqlite_master WHERE type = 'table' AND name <> 'sqlite_master' AND name <> 'sqlite_sequence'");
        while (tableSet.next()) {
            for (int i = 0; i < tableSet.getMetaData().getColumnCount(); i++) {
                tableList.add(tableSet.getString(i + 1));
                System.out.println(tableSet.getString(i + 1));
            }
        }
        dbTableBox.setItems(tableList);
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
        buildTable(selectQuery);
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
        if (query != null) {
            System.out.println(query);
            buildTable(query);
        } else {
            executeButton.setDisable(false);
        }
    }

    @FXML
    private void initialize() throws SQLException {
        initData();
        table.setEditable(false);
        executeButton.setDisable(true);
        buttonShowTable.setDisable(true);
    }

    private ObservableList<ObservableList> data;
    private ObservableList<String> row;
    private TableColumn col;

    public void buildTable(String SQL) {
        Table t = SQLQuery.executeQueryWithOutput(SQL);
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

    public void buildData(String SQL) {
        Connection c;

        data = FXCollections.observableArrayList();
        try {
            c = Common.connection();
            //ResultSet
            ResultSet rs = c.createStatement().executeQuery(SQL);

            //TABLE COLUMN ADDED DYNAMICALLY
            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                //We are using non property style for making dynamic table
                final int j = i;
                col = new TableColumn(rs.getMetaData().getColumnName(i + 1));
                col.setCellValueFactory((Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>) param -> new SimpleStringProperty(param.getValue().get(j).toString()));
                table.getColumns().addAll(col);
                System.out.println("Column [" + i + "] ");
            }

            // Data added to ObservableList
            while (rs.next()) {
                //Iterate Row
                row = FXCollections.observableArrayList();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    //Iterate Column
                    row.add(rs.getString(i));
                }
                System.out.println("Row [1] added " + row);
                data.add(row);
            }

            //FINALLY ADDED TO TableView
            table.setItems(data);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error on Building Data");
        }
    }
}
