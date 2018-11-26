package db;

import main.Common;

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

//    public boolean isClosed() {
//        try {
//            return connection.isClosed();
//        } catch (SQLException e) {
//            Common.debugMessage(e);
//            return true;
//        }
//    }
//
//    public void closeConnection() {
//        if (isClosed()) return;
//        try {
//            connection.close();
//        } catch (SQLException e) {
//            Common.debugMessage(e);
//        }
//    }

    private Connection establishConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection c = DriverManager.getConnection("jdbc:sqlite:" + path);
            c.setAutoCommit(false);
            Common.debugMessage("connectToSQLite: the database is connected through JDBC driver");
            return c;
        } catch (ClassNotFoundException | SQLException e) {
            Common.debugMessage(e);
            System.exit(1);
            return null;
        }
    }
}
