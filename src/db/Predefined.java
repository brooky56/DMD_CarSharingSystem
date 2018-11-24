package db;

import main.Common;
import main.Table;

public class Predefined {

    // 1st SELECT query
    public static Table findCar(String input) {
        if (input.isEmpty()) return null;
        String[] in = input.split("\\s");
        if (in.length < 2) return null;
        return SQLQuery.executeQueryWithOutput(
                "SELECT CarID, Color, Reg_number FROM Cars WHERE Color LIKE '" + in[0] +
                        "' AND Reg_number LIKE '%" + in[1] + "%';");
    }

    // 2nd SELECT query
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

    // 3rd SELECT query
    public static Table busyPerPeriod() {
        String subquery = "SELECT count(DISTINCT CarID) * 100 / (SELECT count(CarID) FROM Cars) FROM Rents WHERE " +
                dateConstraint("DateTime_start", 7) + " AND ";
        Table t = SQLQuery.executeQueryWithOutput(
                "SELECT (" +
                        subquery + timeOfDayConstraint(Common.Morning_Start, Common.Morning_End) + ") AS Morning, (" +
                        subquery + timeOfDayConstraint(Common.Afternoon_Start, Common.Afternoon_End) + ") AS Afternoon, (" +
                        subquery + timeOfDayConstraint(Common.Evening_Start, Common.Evening_End) + ") AS Evening;"
        );
        if (t == null) return null;

        Table res = new Table(4, 2);
        res.setTitle("Time of day", 0);
        res.setTitle("% of the total amount of cars", 1);
        res.setCell("Morning", 1, 0);
        res.setCell("Afternoon", 2, 0);
        res.setCell("Evening", 3, 0);

        res.setCell(t.getCell(1, 0), 1, 1);
        res.setCell(t.getCell(1, 1), 2, 1);
        res.setCell(t.getCell(1, 2), 3, 1);

        return res;
    }

    private static String dateConstraint(String name, int daysago) {
        return "CAST(strftime('%s', " + name + ") AS INTEGER) >= " +
                "CAST(strftime('%s', 'now', '-" + (daysago - 1) + " days') AS INTEGER)"/* +
                " AND CAST(strftime('%s', " + name + ") AS INTEGER) < " +
                "CAST(strftime('%s', 'now', '+1 day') AS INTEGER)"*/;
    }

    private static String timeOfDayConstraint(String start, String end) {
        return "CAST(strftime('%H', DateTime_start) AS INTEGER) >= " + start +
                " AND CAST(strftime('%H', DateTime_start) AS INTEGER) < " + end;
    }

    // 4th SELECT query
    public static Table userPayments(String input) {
        if (input.isEmpty()) return null;
        return SQLQuery.executeQueryWithOutput(
                "SELECT Paid AS 'Paid ($)', DateTime FROM Payments WHERE UserID = " + input + ";");
    }

    // 5th SELECT query
    public static Table rentStatistics(String input) {
        if (input.isEmpty()) return null;
        return SQLQuery.executeQueryWithOutput(
                "SELECT avg(DistanceKM) AS 'Avg. distance (km)', avg(" +
                        "(CAST(strftime('%s', DateTime_end) AS REAL) - " +
                        "CAST(strftime('%s', DateTime_start) AS REAL)) / 3600) " +
                        "AS 'Avg. duration (hours)' FROM Rents " +
                        "WHERE date(DateTime_start) = date('" + input + "');");
    }

    // 6th SELECT query
    public static Table popularPlaces(String input) {
        if (input.isEmpty() || Common.isntInt(input)) return null;
        int days = Integer.parseInt(input);
        Table t = SQLQuery.executeQueryWithOutput(
                "SELECT * FROM (" +
                        selectPlaces(Common.Morning_Start, Common.Morning_End) + "), (" +
                        selectPlaces(Common.Afternoon_Start, Common.Afternoon_End) + "), (" +
                        selectPlaces(Common.Evening_Start, Common.Evening_End) + ");"
        );
        if (t == null) return null;

        int rowspertime = Math.min(t.height - 1, 3);
        Table res = new Table(1 + rowspertime * 3, 3);
        res.setTitle("Time of day", 0);
        res.setTitle("Top of pick-ups", 1);
        res.setTitle("Top of destinations", 2);

        fillPopularPlacesTable(res, 1, t, 0, "Morning", rowspertime);
        fillPopularPlacesTable(res, 1 + rowspertime, t, 2, "Afternoon", rowspertime);
        fillPopularPlacesTable(res, 1 + rowspertime + rowspertime, t, 4, "Evening", rowspertime);
        return res;
    }

    private static String selectPlaces(String start, String end) {
        String c = timeOfDayConstraint(start, end);
        return "SELECT " +
                "(SELECT GPSloc_start FROM Rents WHERE " + c +
                " GROUP BY GPSloc_start ORDER BY count(GPSloc_start)) AS 'Top of pick-ups', " +
                "(SELECT GPSloc_end FROM Rents WHERE " + c +
                " GROUP BY GPSloc_end ORDER BY count(GPSloc_end)) AS 'Top of destinations'";
    }

    private static void fillPopularPlacesTable(Table res, int row, Table t, int col, String time, int rowspertime) {
        res.setCell(time, row, 0);
        for (int i = 1; i <= rowspertime; ++i) {
            int brow = row + i - 1;
            res.setCell(t.getCell(i, col), brow, 1);
            res.setCell(t.getCell(i, col + 1), brow, 2);
        }
    }

    // 7th SELECT query
    public static Table unpopularCars() {
        return SQLQuery.executeQueryWithOutput(
                "SELECT CarID, count(CarID) AS 'Amount of orders' FROM Rents WHERE " +
                        dateConstraint("DateTime_start", 90) +
                        " GROUP BY CarID ORDER BY count(CarID) ASC " +
                        "LIMIT CEIL((SELECT count(CarID) FROM Cars) * 0.1);");
    }

    // 9th SELECT query
    public static Table oftenRequiredParts(String input) {
        if (input.isEmpty() || Common.isntInt(input)) return null;
        int days = Integer.parseInt(input);
        return SQLQuery.executeQueryWithOutput(
                "SELECT CarID, count(CarID) AS 'Amount of orders' FROM Rents WHERE " +
                        dateConstraint("DateTime_start", 90) +
                        " GROUP BY CarID ORDER BY count(CarID) ASC " +
                        "LIMIT CEIL((SELECT count(CarID) FROM Cars) * 0.1);");
    }

    // 10th SELECT query
    public static Table mostExpensiveCarModel(String input) {
        if (input.isEmpty() || Common.isntInt(input)) return null;
        int days = Integer.parseInt(input);
        return SQLQuery.executeQueryWithOutput(
                "SELECT ModelID, (ChargingCost + RepairCost) / " + days + " AS 'Avg. paid per day ($)' FROM " +
                        "(SELECT ModelID, sum(Cost) AS ChargingCost FROM Cars NATURAL JOIN ChargingHistory WHERE " +
                        dateConstraint("DateTime_start", days) + "GROUP BY ModelID) " +
                        "NATURAL JOIN " +
                        "(SELECT ModelID, sum(Cost) AS RepairCost FROM Cars NATURAL JOIN Repairs NATURAL JOIN " +
                        "(SELECT RepairID, sum(Paid) AS Cost FROM Parts NATURAL JOIN PartsUsed GROUP BY RepairID) WHERE " +
                        dateConstraint("Date_start", days) + "GROUP BY ModelID) " +
                        "ORDER BY 'Avg. paid per day ($)' DESC LIMIT 1;");
    }

    public static void main(String[] args) {
        Common.establishConnection();
        System.out.println(popularPlaces("90").toString());
    }
}
