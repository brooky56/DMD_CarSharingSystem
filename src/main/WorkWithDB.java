package main;

import db.AccessToSQL;

public class WorkWithDB {
    private static final String PathToDatabase = "testDB.db";

    private AccessToSQL accessToSQL;

    public WorkWithDB() {
    }

    public void initAccessToLite() {
        accessToSQL = new AccessToSQL(PathToDatabase);
        if (accessToSQL.getAccess()) {
            System.out.println("Access to the databse is recieved");
        } else {
            System.out.println("Access error to the database");
            System.exit(1);
        }
    }

    public void closeAccessToLite() {
        if (accessToSQL.closeAccess()) {
            System.out.println("Access to the databse is closed");
        }
    }
}