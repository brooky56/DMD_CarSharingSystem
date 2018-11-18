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
            System.out.println("connectToSQLite: SQLite JDBC driver is not found");
            return false;
        }
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + sConnect);
            System.out.println("connectToSQLite: a database is connected through JDBC driver");
        } catch (SQLException e) {
            System.out.println("connectToSQLite: connection error to database through JDBC driver");
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
