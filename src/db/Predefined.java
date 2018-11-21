package db;

import main.Common;
import main.Table;

public class Predefined {

    // First SELECT query
    public static Table findCar(String RegNumberPart, String color) {
        return SQLQuery.executeQueryWithOutput(
                "SELECT CarID, Manufacturer, Name FROM Cars NATURAL JOIN CarModels WHERE Color = '" + color +
                        "' AND Reg_number LIKE('" + RegNumberPart + "%');");
    }

    // Second SELECT query
    public static Table socketsPerHour(String date) {
        Table t = SQLQuery.executeQueryWithOutput(
                "SELECT CAST(strftime('%H', DateTime_start) AS INTEGER), count(DateTime_start) " +
                        "FROM ChargingHistory WHERE date(DateTime_start) = date('" + date +
                        "') GROUP BY strftime('%H', DateTime_start);");
        Table res = new Table(13, 2);
        res.setTitle("Before noon", 0);
        res.setTitle("After noon", 1);
        for (int i = 1; i < 13; ++i) {
            res.setCell(buildHourCell(i - 1, t), i, 0);
            res.setCell(buildHourCell(i + 11, t), i, 1);
        }
        return res;
    }

    private static String buildHourCell(int hour, Table t) {
        String out = Common.int2Hour(hour) + "h - " + Common.int2Hour(hour + 1) + "h: ";
        for (int a = 1; a < t.height; ++a) {
            if ((int) t.getCell(a, 0) == hour) {
                out += t.getCell(a, 1).toString();
                return out;
            }
        }
        return out + "0";
    }
}
