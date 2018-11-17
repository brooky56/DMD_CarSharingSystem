package main;

import db.AccessToSQL;

public class WorkWithDB {
    private AccessToSQL accessToSQL;

    public WorkWithDB() {
    }

    public void initAccessToLite() {
        accessToSQL = new AccessToSQL("testDB.db");
        if (accessToSQL.getAccess()) {
            System.out.println("доступ к БД получен");
        } else {
            System.out.println("ошибка доступа к БД");
            System.exit(1);
        }
    }

    public void closeAccessToLite() {
        accessToSQL.closeAccess();
        System.out.println("доступ к БД отключен");
    }

}