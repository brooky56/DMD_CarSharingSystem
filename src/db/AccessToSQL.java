package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class AccessToSQL {
    private String sConnect;
    private Connection connection = null;

    public AccessToSQL(String sConnect) {
        this.sConnect = sConnect;
    }

    public boolean getAccess() {
        return connectToSQLite();
    }

    private boolean connectToSQLite() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.out.println("connectToSQLite: SQLite JDBC драйвер не найден");
            return false;
        }
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + sConnect);
            System.out.println("connectToSQLite: БД подключена через JDBC драйвер");
        } catch (SQLException e) {
            System.out.println("connectToSQLite: ошибка подключения к БД через JDBC драйвер");
            return false;
        }
        return true;
    }

    public boolean closeAccess() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public Connection getConnection() {
        return connection;
    }
}
