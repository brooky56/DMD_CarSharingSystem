package Main;

import db.ConnectionToSQL;

import java.sql.Connection;

public class Common {
    public static final String PATH_Database = "Database.db";
    private static ConnectionToSQL access;

    public static void establishConnection() {
        access = new ConnectionToSQL(PATH_Database);
    }

    public static Connection connection() {
        return access.connection;
    }

    public static void closeConnection() {
        access.closeConnection();
    }
}
