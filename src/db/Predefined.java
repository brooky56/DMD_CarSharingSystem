package db;

import main.Common;
import main.Table;

public class Predefined {

    // 1st SELECT query
    // MODIFIED: input - color and part of registration number
    //           output - cars with given color and registration number which contains given part
    public static Table findCar(String input) {
        if (input.isEmpty()) return null;
        String[] in = input.split("\\s");
        if (in.length < 2) return null;
        return SQLQuery.executeQueryWithOutput(
                "SELECT CarID, Color, Reg_number FROM Cars WHERE Color LIKE '" + in[0] +
                        "' AND Reg_number LIKE '%" + in[1] + "%' ORDER BY CarID;");
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
        return "CAST(strftime('%s', " + name + ", 'start of day') AS INTEGER) >= " +
                "CAST(strftime('%s', 'now', 'start of day', '-" + daysago + " days') AS INTEGER)";
    }

    private static String timeOfDayConstraint(String start, String end) {
        return "CAST(strftime('%H', DateTime_start) AS INTEGER) >= " + start +
                " AND CAST(strftime('%H', DateTime_start) AS INTEGER) < " + end;
    }

    // 4th SELECT query
    public static Table userPayments(String input) {
        if (input.isEmpty()) return null;
        return SQLQuery.executeQueryWithOutput(
                "SELECT DateTime, Paid AS 'Paid ($)' FROM Payments WHERE UserID = " + input + " AND " +
                        dateConstraint("DateTime", 30) + ";");
    }

    // 5th SELECT query
    public static Table rentStatistics(String input) {
        if (input.isEmpty()) return null;
        return SQLQuery.executeQueryWithOutput(
                "SELECT printf('%.2f', avg(DistanceKM)) AS 'Avg. distance (km)', " +
                        "printf('%.2f', avg((CAST(strftime('%s', DateTime_end) AS REAL) - " +
                        "CAST(strftime('%s', DateTime_start) AS REAL)) / 3600.0)) " +
                        "AS 'Avg. duration (hours)' FROM Rents " +
                        "WHERE date(DateTime_start) = date('" + input + "');");
    }

    // 6th SELECT query
    // MODIFIED: input - amount of days (before now) within to gather stats
    public static Table popularPlaces(String input) {
        if (input.isEmpty() || Common.isntInt(input)) return null;
        int days = Integer.parseInt(input);
        String dc = dateConstraint("DateTime_start", days) + " AND ";
        Table[][] tt = new Table[3][2];
        getTables("start", dc, tt, 0);
        getTables("end", dc, tt, 1);


        int mrows = Math.max(tt[0][0].height, tt[0][1].height) - 1;
        int arows = Math.max(tt[1][0].height, tt[1][1].height) - 1;
        int erows = Math.max(tt[2][0].height, tt[2][1].height) - 1;
        int rowspertime = 0;
        for (int i = 0; i < 3; ++i) {
            if (tt[i][0].height > rowspertime) rowspertime = tt[i][0].height;
            if (tt[i][1].height > rowspertime) rowspertime = tt[i][1].height;
        }
        --rowspertime;

        Table res = new Table(1 + rowspertime * 3, 3);
        res.setTitle("Time of day", 0);
        res.setTitle("Top of pick-ups", 1);
        res.setTitle("Top of destinations", 2);

        fillPopularPlacesTable(res, 1, tt[0], "Morning", rowspertime);
        fillPopularPlacesTable(res, 1 + rowspertime, tt[1], "Afternoon", rowspertime);
        fillPopularPlacesTable(res, 1 + rowspertime + rowspertime, tt[2], "Evening", rowspertime);
        return res;
    }

    private static void getTables(String type, String dc, Table[][] tt, int j) {
        String s = "SELECT GPSloc_" + type + " FROM Rents WHERE ";
        String g = " GROUP BY GPSloc_" + type + " ORDER BY count(GPSloc_" + type + ") DESC LIMIT 3;";
        tt[0][j] = SQLQuery.executeQueryWithOutput(
                s + dc + timeOfDayConstraint(Common.Morning_Start, Common.Morning_End) + g
        );
        tt[1][j] = SQLQuery.executeQueryWithOutput(
                s + dc + timeOfDayConstraint(Common.Afternoon_Start, Common.Afternoon_End) + g
        );
        tt[2][j] = SQLQuery.executeQueryWithOutput(
                s + dc + timeOfDayConstraint(Common.Evening_Start, Common.Evening_End) + g
        );
    }

    private static void fillPopularPlacesTable(Table res, int row, Table[] t, String time, int rowspertime) {
        res.setCell(time, row, 0);
        for (int i = 1; i < rowspertime; ++i) {
            res.setCell("", i + row, 0);
        }
        res.setCell(time, row, 0);
        for (int j = 0; j < 2; ++j) {
            for (int i = 1; i < t[j].height; ++i) {
                int brow = row + i - 1;
                res.setCell(t[j].getCell(i, 0), brow, j + 1);
            }
        }
    }

    // 7th SELECT query
    public static Table unpopularCars() {
        return SQLQuery.executeQueryWithOutput(
                "SELECT CarID, count(CarID) AS 'Amount of rents' FROM Rents WHERE " +
                        dateConstraint("DateTime_start", 90) +
                        " GROUP BY CarID ORDER BY count(CarID) ASC " +
                        "LIMIT CEIL((SELECT count(CarID) FROM Cars) * 0.1);");
    }

    // 8th SELECT query
    // CHANGED: input - amount of days (before now) within to gather stats
    // output - average per day distance travelled, income from rents, outlays for charging and amount of charging
    // of every car within last days
    public static Table carStats(String input) {
        if (input.isEmpty() || Common.isntInt(input)) return null;
        int days = Integer.parseInt(input);
        return SQLQuery.executeQueryWithOutput(
                "SELECT CarID, " +
                        avgPer("sum(D)", days, "Avg. distance per day (km)") + ", " +
                        avgPer("sum(C1)", days, "Avg. income from rents per day ($)") + ", " +
                        avgPer("sum(C2)", days, "Avg. outlays for charging per day ($)") + ", " +
                        avgPer("sum(A)", days, "Avg. amount of charging per day") + " FROM " +
                        "(SELECT CarID, date(DateTime_end) AS Date, sum(DistanceKM) AS D, sum(Cost) AS C1 " +
                        "FROM Rents GROUP BY CarID, Date) LEFT OUTER JOIN " +
                        "(SELECT CarID, date(DateTime_start) AS Date, sum(Cost) AS C2, count(*) AS A " +
                        "FROM ChargingHistory GROUP BY CarID, Date) USING (CarID, Date) WHERE " +
                        dateConstraint("Date", days) + " GROUP BY CarID ORDER BY CarID");
    }

    private static String avgPer(String in, int num, String name) {
        return "printf('%.2f', " + in + " / " + num + ".0) AS '" + name + "'";
    }

    // 9th SELECT query
    // MODIFIED: input - amount of weeks (before now) within to gather stats
    //           output - the most used per week and expensive part types by every workshop
    public static Table oftenRequiredParts(String input) {
        if (input.isEmpty() || Common.isntInt(input)) return null;
        int weeks = Integer.parseInt(input);
        return SQLQuery.executeQueryWithOutput(
                "SELECT WID, PartTypeId, Name, max(Amount) AS 'Avg. amount used per week' FROM " +
                        "(SELECT WID, PartTypeId, Name, CEIL(count(PartID) / " + weeks + ") " +
                        "AS Amount, sum(Paid) AS P FROM Repairs NATURAL JOIN " +
                        "(SELECT RepairID, PartID, PartTypeID, Name, Paid " +
                        "FROM PartTypes NATURAL JOIN Parts NATURAL JOIN PartsUsed) WHERE " +
                        dateConstraint("Date_start", weeks * 7) +
                        " GROUP BY WID, PartTypeID ORDER BY P DESC) GROUP BY WID ORDER BY WID;");
    }

    // 10th SELECT query
    // MODIFIED: input - amount of days (before now) within to gather stats
    public static Table mostExpensiveCarModel(String input) {
        if (input.isEmpty() || Common.isntInt(input)) return null;
        int days = Integer.parseInt(input);
        return SQLQuery.executeQueryWithOutput(
                "SELECT ModelID, " +
                        avgPer("(ChargingCost + RepairCost)", days, "Avg. paid per day ($)") +
                        " FROM " +
                        "(SELECT ModelID, sum(Cost) AS ChargingCost FROM Cars NATURAL JOIN ChargingHistory WHERE " +
                        dateConstraint("DateTime_start", days) + " GROUP BY ModelID) " +
                        "NATURAL JOIN " +
                        "(SELECT ModelID, sum(Cost) AS RepairCost FROM Cars NATURAL JOIN Repairs NATURAL JOIN " +
                        "(SELECT RepairID, sum(Paid) AS Cost FROM Parts NATURAL JOIN PartsUsed GROUP BY RepairID) WHERE " +
                        dateConstraint("Date_start", days) + " GROUP BY ModelID) " +
                        "ORDER BY 'Avg. outlay per day ($)' DESC LIMIT 1;");
    }

    private static String ternaryFullOuterJoin(String t1, String t2, String t3, String keys) {
        return "WITH C AS (" + fullOuterJoin(t1, t2, keys) + ") " + fullOuterJoin("C", t3, keys);
    }

    private static String fullOuterJoin(String t1, String t2, String keys) {
        keys = " USING (" + keys + ")";
        String s = "SELECT " + t1 + ".*, " + t2 + ".* FROM ";
        return s + t1 + " LEFT JOIN " + t2 + keys + " UNION " + s + t2 + " LEFT JOIN " + t1 + keys;
    }
}
