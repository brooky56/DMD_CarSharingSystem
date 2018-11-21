package Objects;

import java.util.LinkedList;

public class Table {
    private Object[][] table;
    private int width;
    private int height;
    public String separator = ", ";

    public Table(int rowsN, int columnsN) {
        height = rowsN;
        width = columnsN;
        table = new Object[height][width];
    }

    public Table(LinkedList<Object[]> list) {
        height = list.size();
        width = list.getFirst().length;
        for (int row = 1; row < height; ++row) {
            if (list.get(row).length != width) {
                throw new IllegalArgumentException("Several rows in passed list have different length");
            }
        }
        table = new Object[height][width];
        for (int row = 0; row < height; ++row) {
            for (int column = 0; column < width; ++column) {
                table[row][column] = list.get(row)[column];
            }
        }
    }

    public int getColumnsCount() {
        return width;
    }

    public int getRowsCount() {
        return height;
    }

    public Object[] getRow(int row) {
        checkRowIndex(row);
        return table[row];
    }

    public Object[] getTitles() {
        return getRow(0);
    }

    public Object getCell(int row, int column) {
        checkCellIndex(row, column);
        return table[row][column];
    }

    public Object getTitle(int column) {
        return getCell(0, column);
    }

    public void setRow(Object[] o, int row) {
        checkRowIndex(row);
        if (o.length != width) {
            throw new IllegalArgumentException("Passed row has inappropriate amount of columns");
        }
        table[row] = o;
    }

    public void setTitles(Object[] o) {
        setRow(o, 0);
    }

    public void setCell(Object o, int row, int column) {
        checkCellIndex(row, column);
        table[row][column] = o;
    }

    public void setTitle(Object o, int column) {
        setCell(o, 0, column);
    }

    private void checkRowIndex(int row) {
        if (row < 0 || row >= height) {
            throw new IndexOutOfBoundsException("Passed row index is out of range");
        }
    }

    private void checkCellIndex(int row, int column) {
        checkRowIndex(row);
        if (column < 0 || column >= width) {
            throw new IndexOutOfBoundsException("Passed column index is out of range");
        }
    }

    private static String addSpaces(String s, int amount) {
        for (int row = 0; row < amount; ++row) {
            s = " " + s;
        }
        return s;
    }

    public String toString() {
        int[] max = new int[width];
        for (int column = 0; column < width; ++column) {
            for (int row = 0; row < height; ++row) {
                int l = (table[row][column].toString()).length();
                if (l > max[column]) {
                    max[column] = l;
                }
            }
        }
        String out = "";
        for (int row = 0; row < height; ++row) {
            for (int column = 0; column < width; ++column) {
                String s = table[row][column].toString();
                if (column == width - 1) {
                    out += addSpaces(s, max[column] - s.length());
                } else {
                    out += addSpaces(s, max[column] - s.length()) + separator;
                }
            }
            out += "\n";
        }
        return out;
    }

//    public static void main(String[] args) {
//        Table t = new Table(5, 3);
//        String[] s = {"ID", "Name", "Phone"};
//        t.setTitles(s);
//        Object[] o1 = {1, "Vas", "10-20"};
//        t.setRow(o1, 1);
//        Object[] o2 = {2, "Peta", "45-27"};
//        t.setRow(o2, 2);
//        Object[] o3 = {3, "Andrey", "+375"};
//        t.setRow(o3, 3);
//        Object[] o4 = {4, "Sash", "+8 029"};
//        t.setRow(o4, 4);
//        System.out.println(t.toString());
//    }
}
