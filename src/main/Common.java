package main;

import db.ConnectionToSQL;
import db.Predefined;

import java.sql.Connection;

public class Common {
    public static final String PATH_Database = "Database.db";
    private static ConnectionToSQL access;
    public static final String NULL_ELEMENT = "<null>";
    public static final String NOW = "now";
    public static final String Morning_Start = "7";
    public static final String Morning_End = "10";
    public static final String Afternoon_Start = "12";
    public static final String Afternoon_End = "14";
    public static final String Evening_Start = "17";
    public static final String Evening_End = "19";

    public static void establishConnection() {
        access = new ConnectionToSQL(PATH_Database);
    }

    public static Connection connection() {
        return access.connection;
    }

    public static void closeConnection() {
        access.closeConnection();
    }

    public static String int2Hour(int h){
        if (h < 10) return "0" + Integer.toString(h);
        if (h == 24) return "00";
        return Integer.toString(h);
    }

    public static void main(String[] args) {
        establishConnection();
        Table t = Predefined.findCar("White AN");
//        Table t = Predefined.socketsPerHour("2017-12-01");
//        Table t = Predefined.busyPerPeriod("2017-01-04");
        System.out.println(t.toString());
    }
}
