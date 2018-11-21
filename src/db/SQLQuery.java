package db;

import java.sql.*;
import java.util.LinkedList;

import main.Common;
import main.Table;

public class SQLQuery {
    private SQLQuery() {
    }

    public static void executeQueryNoOutput(String command) {
        try {
            Connection c = Common.connection();
            Statement stmt = c.createStatement();
            stmt.executeUpdate(command);
            c.commit();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Table executeQueryWithOutput(String command) {
        try {
            // Creating database objects
            Connection c = Common.connection();
            Statement stmt = c.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = stmt.executeQuery(command);
            ResultSetMetaData data = rs.getMetaData();

            // Getting names of columns
            int columns = data.getColumnCount();
            String[] names = new String[columns];
            for (int j = 0; j < columns; ++j) {
                names[j] = data.getColumnName(j + 1);
            }

            // Putting row info into linked list
            LinkedList<Object[]> list = new LinkedList<>();
            list.addFirst(names);
            while (rs.next()) {
                Object[] row = new Object[columns];
                for (int j = 0; j < columns; ++j) {
                    row[j] = rs.getObject(j + 1);
                }
                list.add(row);
            }

            // Releasing database objects
            stmt.close();
            rs.close();

            // Convert list to table and return
            return new Table(list);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
