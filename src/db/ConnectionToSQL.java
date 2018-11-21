package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionToSQL {
    public final String path;
    public final Connection connection;

    public ConnectionToSQL(String path) {
        this.path = path;
        this.connection = establishConnection();
    }

    public boolean isClosed() {
        try {
            return connection.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }

    public void closeConnection() {
        if (isClosed()) return;
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Connection establishConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection c = DriverManager.getConnection("jdbc:sqlite:" + path);
            c.setAutoCommit(false);
            System.out.println("connectToSQLite: the database is connected through JDBC driver");
            return c;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }
}
