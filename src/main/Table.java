package main;

import java.util.LinkedList;

public class Table {
    private Object[][] table;
    public final int width;
    public final int height;
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

    public Object[] getRow(int row) {
        checkRowIndex(row);
        Object[] o = new Object[width];
        for (int j = 0; j < height; ++j) {
            o[j] = table[row][j];
        }
        return o;
    }

    public Object[] getColumn(int column) {
        checkColumnIndex(column);
        Object[] o = new Object[height];
        for (int i = 0; i < height; ++i) {
            o[i] = table[i][column];
        }
        return o;
    }

    public Object getCell(int row, int column) {
        checkCellIndex(row, column);
        return table[row][column];
    }

    public void setRow(Object[] o, int row) {
        checkRowIndex(row);
        if (o.length != width) {
            throw new IllegalArgumentException("Passed row has inappropriate amount of columns");
        }
        for (int j = 0; j < height; ++j) {
            table[row][j] = o[j];
        }
    }

    public void setColumn(Object[] o, int column) {
        checkColumnIndex(column);
        if (o.length != height) {
            throw new IllegalArgumentException("Passed column has inappropriate amount of rows");
        }
        for (int i = 0; i < height; ++i) {
            table[i][column] = o[i];
        }
    }

    public void setCell(Object o, int row, int column) {
        checkCellIndex(row, column);
        table[row][column] = o;
    }

    public Object[] getTitles() {
        return getRow(0);
    }

    public void setTitles(Object[] o) {
        setRow(o, 0);
    }

    public Object getTitle(int column) {
        return getCell(0, column);
    }

    public void setTitle(Object o, int column) {
        setCell(o, 0, column);
    }

    private void checkRowIndex(int row) {
        if (row < 0 || row >= height) {
            throw new IndexOutOfBoundsException("Passed row index is out of range");
        }
    }

    private void checkColumnIndex(int column) {
        if (column < 0 || column >= width) {
            throw new IndexOutOfBoundsException("Passed column index is out of range");
        }
    }

    private void checkCellIndex(int row, int column) {
        checkRowIndex(row);
        checkColumnIndex(column);
    }

    private static String addSpaces(String s, int amount) {
        for (int a = 0; a < amount; ++a) {
            s = " " + s;
        }
        return s;
    }

    public String toString() {
        int[] max = new int[width];
        Table temp = new Table(height, width);
        for (int j = 0; j < width; ++j) {
            for (int i = 0; i < height; ++i) {
                if (table[i][j] == null) {
                    temp.setCell(Common.NULL_ELEMENT, i, j);
                } else {
                    temp.setCell(table[i][j].toString(), i, j);
                }
                int l = ((String) temp.table[i][j]).length();
                if (l > max[j]) {
                    max[j] = l;
                }
            }
        }
        String out = "";
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                String s = (String) temp.table[i][j];
                if (j == width - 1) {
                    out += addSpaces(s, max[j] - s.length());
                } else {
                    out += addSpaces(s, max[j] - s.length()) + separator;
                }
            }
            out += "\n";
        }
        return out;
    }
}
