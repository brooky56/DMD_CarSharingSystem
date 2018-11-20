package GUI;

import db.AccessToSQL;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;

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
        ResultSet tableSet = AccessToSQL.getConnection().createStatement().executeQuery("SELECT name FROM sqlite_master WHERE type = 'table'");
        while (tableSet.next()) {
            for (int i = 0; i < tableSet.getMetaData().getColumnCount(); i++) {
                tableList.add(tableSet.getString(i + 1));
                System.out.println(tableSet.getString(i + 1));
            }
        }
        dbTableBox.setItems(tableList);

    }

    @FXML
    private void onButtonShowClick() {
        String tableNeeded = dbTableBox.getValue();
        String selectQuery = "SELECT * FROM " + tableNeeded;
        buildData(selectQuery);
    }

    @FXML
    private void onButtonClearClick() {
        for (int i = 0; i < table.getItems().size(); i++) {
            table.getItems().clear();
        }
        table.getColumns().clear();
    }

    @FXML
    private void initialize() throws SQLException {
        initData();
    }

    private ObservableList<ObservableList> data;
    private ObservableList<String> row;
    private TableColumn col;

    public void buildData(String SQL) {
        Connection c;

        data = FXCollections.observableArrayList();
        try {
            c = AccessToSQL.getConnection();
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
