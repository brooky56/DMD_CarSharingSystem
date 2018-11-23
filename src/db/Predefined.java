package db;

import main.Common;
import main.Table;

public class Predefined {

    // First SELECT query
    public static Table findCar(String input) {
        if (input.isEmpty()) return null;
        String[] in = input.split("\\s");
        String color, regnum;
        if (in.length >= 2) {
            color = in[0];
            regnum = in[1];
        } else {
            color = "";
            regnum = "";
        }
        return SQLQuery.executeQueryWithOutput(
                "SELECT CarID, Reg_number FROM Cars NATURAL JOIN CarModels WHERE Color LIKE '" + color +
                        "' AND Reg_number LIKE '%" + regnum + "%';");
    }

    // Second SELECT query
    public static Table socketsPerHour(String input) {
        if (input.isEmpty()) return null;
        Table t = SQLQuery.executeQueryWithOutput(
                "SELECT CAST(strftime('%H', DateTime_start) AS INTEGER), count(DateTime_start) " +
                        "FROM ChargingHistory WHERE date(DateTime_start) = date('" + input +
                        "') GROUP BY strftime('%H', DateTime_start);");
        if (t == null) return null;

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

    // Third SELECT query
    public static Table busyPerPeriod(String input) {
        if (input.isEmpty()) return null;
        String command = "SELECT count(DISTINCT CarID) * 100 / (SELECT count(CarID) FROM Cars) FROM Rents " +
                "WHERE CAST(strftime('%s', DateTime_start) AS INTEGER) >= CAST(strftime('%s', '" + input + "', '-6 days') AS INTEGER) " +
                "AND CAST(strftime('%s', DateTime_start) AS INTEGER) <= CAST(strftime('%s', '" + input + "', '+1 day') AS INTEGER)" +
                " AND CAST(strftime('%H', DateTime_start) AS INTEGER) >= ";
        String scnd = " AND CAST(strftime('%H', DateTime_start) AS INTEGER) <= ";

        Table morning = SQLQuery.executeQueryWithOutput(
                command + Common.Morning_Start + scnd + Common.Morning_End + ";");
        if (morning == null) return null;

        Table afternoon = SQLQuery.executeQueryWithOutput(
                command + Common.Afternoon_Start + scnd + Common.Afternoon_End + ";");
        if (afternoon == null) return null;

        Table evening = SQLQuery.executeQueryWithOutput(
                command + Common.Evening_Start + scnd + Common.Evening_End + ";");
        if (evening == null) return null;

        Table res = new Table(2, 3);
        res.setTitle("Morning (%)", 0);
        res.setTitle("Afternoon (%)", 1);
        res.setTitle("Evening (%)", 2);
        res.setCell(morning.getCell(1, 0), 1, 0);
        res.setCell(afternoon.getCell(1, 0), 1, 1);
        res.setCell(evening.getCell(1, 0), 1, 2);

        return res;
    }

    // Forth SELECT query
    public static Table userPayments(String input) {
        if (input.isEmpty()) return null;
        return SQLQuery.executeQueryWithOutput(
                "SELECT Paid, DateTime FROM Payments WHERE UserID =  " + input + ";");
    }

    // Forth SELECT query
    public static Table rentStatistics(String input) {
        if (input.isEmpty()) return null;
        return SQLQuery.executeQueryWithOutput(
                "SELECT avg(DistanceKM) AS 'Distance (km)'," +
                        "avg((CAST(strftime('%s', DateTime_end) AS REAL)" +
                        "- CAST(strftime('%s', DateTime_start) AS REAL)) / 3600) AS 'Duration (hours)'" +
                        "FROM Rents WHERE date(DateTime_start) = '" + input + "';");
    }
}
