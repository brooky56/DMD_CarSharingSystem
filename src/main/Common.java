package main;

import db.ConnectionToSQL;

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

    public static String int2Hour(int h) {
        if (h < 10) return "0" + Integer.toString(h);
        if (h == 24) return "00";
        return Integer.toString(h);
    }

    public static double square(double d) {
        return d * d;
    }

    public static double distanceBetweenGPSlocs(String l1, String l2) {
        String[] loc1 = l1.split(", ");
        String[] loc2 = l2.split(", ");
        double lat1 = Math.toRadians(Double.parseDouble(loc1[0]));
        double lat2 = Math.toRadians(Double.parseDouble(loc2[0]));
        double long1 = Math.toRadians(Double.parseDouble(loc1[1]));
        double long2 = Math.toRadians(Double.parseDouble(loc2[1]));

        double dlon = long2 - long1;
        double y = Math.sqrt(square(Math.cos(lat2) * Math.sin(dlon))
                + square(Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dlon)));
        double x = Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lat2);
        return 6371.302 * Math.atan2(y, x);
    }
}
