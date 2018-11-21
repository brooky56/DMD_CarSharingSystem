package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class AccessToSQL {
    private static String path = null;
    private static Connection connection = null;

    private AccessToSQL() {
    }

    public static boolean isClosed() {
        return connection == null;
    }

    public static boolean getAccess(String path) {
        if (isClosed()) {
            AccessToSQL.path = path;
            return connectToSQLite();
        }
        return false;
    }

    public static boolean closeAccess() {
        if (isClosed()) return false;
        try {
            connection.close();
            connection = null;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean restoreAccess() {
        if (isClosed()) {
            return connectToSQLite();
        }
        return false;
    }

    public static Connection getConnection() {
        return connection;
    }

    private static boolean connectToSQLite() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + path);
            connection.setAutoCommit(false);
            System.out.println("connectToSQLite: the database is connected through JDBC driver");
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
