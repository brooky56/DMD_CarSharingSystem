package GUI;

import Objects.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class Controller {


    private ObservableList<Customer> usersData = FXCollections.observableArrayList();

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
    private TableView<Customer> table;
    // инициализируем форму данными

    private void initTable() {
    }

    // подготавливаем данные для таблицы
    // вы можете получать их с базы данных
    public void initData() {
        ObservableList<String> tableList = FXCollections.observableArrayList("Customer",
                "Car", "Workshop");
        dbTableBox.setItems(tableList);
    }
}
