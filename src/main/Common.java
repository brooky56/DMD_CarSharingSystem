package main;

import db.ConnectionToSQL;

import java.sql.Connection;

public class Common {
    public static final String PATH_Database = "Database.db";
    public static final String NULL_ELEMENT = "<null>";
    private static ConnectionToSQL access;

    public static void establishConnection() {
        access = new ConnectionToSQL(PATH_Database);
    }

    public static Connection connection() {
        return access.connection;
    }

    public static boolean isntInt(String s) {
        try {
            Integer.parseInt(s);
            return false;
        } catch (NumberFormatException e) {
            return true;
        }
    }

    public static void debugMessage(String s) {
//        System.out.println(s);
    }

    public static void debugMessage(Exception e) {
//        e.printStackTrace();
    }

//    public static boolean isSpaceChar(int code) {
//        return code == 32 || (9 <= code && code <= 13);
//    }
//
//    public static double square(double d) {
//        return d * d;
//    }
//
//    public static double distanceBetweenGPSlocs(String l1, String l2) {
//        String[] loc1 = l1.split(", ");
//        String[] loc2 = l2.split(", ");
//        double lat1 = Math.toRadians(Double.parseDouble(loc1[0]));
//        double lat2 = Math.toRadians(Double.parseDouble(loc2[0]));
//        double long1 = Math.toRadians(Double.parseDouble(loc1[1]));
//        double long2 = Math.toRadians(Double.parseDouble(loc2[1]));
//
//        double dlon = long2 - long1;
//        double y = Math.sqrt(square(Math.cos(lat2) * Math.sin(dlon))
//                + square(Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dlon)));
//        double x = Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lat2);
//        return 6371.302 * Math.atan2(y, x);
//    }
}
