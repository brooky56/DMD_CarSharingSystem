package GUI;

import Objects.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;

public class Controller {


    private ObservableList<Customer> usersData = FXCollections.observableArrayList();

    @FXML
    private Button buttonClick;
    @FXML
    private TableView<Customer> tableUsers;

    @FXML
    private TableColumn<Customer, Integer> idColumn;

    @FXML
    private TableColumn<Customer, String> loginColumn;

    @FXML
    private TableColumn<Customer, String> passwordColumn;

    @FXML
    private TableColumn<Customer, String> emailColumn;

    // инициализируем форму данными

    private void initTable() throws SQLException {
        initData();

        // устанавливаем тип и значение которое должно хранится в колонке
        idColumn.setCellValueFactory(new PropertyValueFactory<Customer, Integer>("id"));
        loginColumn.setCellValueFactory(new PropertyValueFactory<Customer, String>("login"));
        passwordColumn.setCellValueFactory(new PropertyValueFactory<Customer, String>("password"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<Customer, String>("email"));

        // заполняем таблицу данными
        tableUsers.setItems(usersData);
    }

    // подготавливаем данные для таблицы
    // вы можете получать их с базы данных
    private void initData() {
        usersData.add(new Customer(1, "Alex", "qwerty", "alex@mail.com"));
        usersData.add(new Customer(2, "Bob", "dsfsdfw", "bob@mail.com"));
        usersData.add(new Customer(3, "Jeck", "dsfdsfwe", "Jeck@mail.com"));
        usersData.add(new Customer(4, "Mike", "iueern", "mike@mail.com"));
        usersData.add(new Customer(5, "colin", "woeirn", "colin@mail.com"));
    }


    @FXML
    private void onButtonClick() throws SQLException {
        initTable();
    }
}
