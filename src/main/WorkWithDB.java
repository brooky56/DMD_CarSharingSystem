package main;

import db.*;
import java.sql.Connection;

public class WorkWithDB {
    private static final String PATH_Database = "Database.db";

    private WorkWithDB() {
    }

    public static void accessDatabase() {
        if (AccessToSQL.getAccess(PATH_Database)) {
            System.out.println("Success: access to the database is received");
        } else {
            System.out.println("Error: access to the database is denied");
            System.exit(1);
        }
    }

    public static void closeDatabase() {
        if (AccessToSQL.closeAccess()) {
            System.out.println("Success: access to the database is closed");
        }
    }

//    public static void main(String[] args) {
//        accessDatabase();
//        Connection c = AccessToSQL.getConnection();
//        System.out.println(SQLQuery.ExecuteQueryWithOutput(c,
//                "SELECT type,name,tbl_name,rootpage FROM sqlite_master").toString());
//    }
}