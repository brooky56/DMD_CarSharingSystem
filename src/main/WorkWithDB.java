package main;

import db.AccessToSQL;

class WorkWithDB {
    private AccessToSQL accessToSQL;

    WorkWithDB() {
        initAccessToLite();
        closeAccessToLite();
    }

    private void initAccessToLite() {
        accessToSQL = new AccessToSQL("testDB.db");
        if (accessToSQL.getAccess()) {
            System.out.println("доступ к БД получен");
        } else {
            System.out.println("ошибка доступа к БД");
            System.exit(1);
        }
    }

    private void closeAccessToLite() {
        accessToSQL.closeAccess();
        System.out.println("доступ к БД отключен");
    }
}