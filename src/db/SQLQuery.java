package db;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;

import static utils.UtilsForAll.getMainClass;

public class SQLQuery {
    private Connection connection;
    private Statement statement;

    public SQLQuery(Connection connection) throws SQLException {
        this.connection = connection;
        statement = connection.createStatement();
    }


    public boolean isTableExists(String tableName) {
        String strSQL = "SELECT count(*) FROM sqlite_master WHERE type='table' AND name='" + tableName + "';";
        try {
            ResultSet resultSet = statement.executeQuery(strSQL);
            return (resultSet.getInt(1) > 0);
        } catch (SQLException e) {
            System.out.println("Ошибка обращения к таблице: " + tableName);
            return false;
        }
    }

    public boolean createTable(String tableName) {
        String strSQL = "CREATE TABLE if not exists '" + tableName + "' (" +
                "'Id' INTEGER PRIMARY KEY AUTOINCREMENT," +
                "'Description' VARCHAR (255)," +
                "'FileName' VARCHAR (255)," +
                "'Img' BLOB" +
                ");";
        return execStatement(strSQL);
    }

    public boolean deleteAllDataFromTable(String strTableName) {
        String strSQL = "";
        strSQL += "DELETE FROM " + strTableName;
        return execStatement(strSQL);
    }

    public boolean updateDataFileFromResourceToTable(String strTableName, int id, String strField, String strFileNameInResource) {
        String strSQL = "UPDATE " + strTableName + " " +
                "SET " + strField + " = ?" +
                "WHERE ID = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(strSQL);
            InputStream inputStream = getMainClass().getResourceAsStream(strFileNameInResource);

            //logger.info("-> добавление файла " + strFileNameInResource + "(" + inputStream.available() + ")");
            preparedStatement.setBinaryStream(1, inputStream, inputStream.available());
            preparedStatement.setInt(2, id);
            preparedStatement.executeUpdate();

            inputStream.close();
            preparedStatement.close();

        } catch (SQLException | IOException e) {
            //logger.info("SQLiteUpdateData: " + e.getMessage());
            return false;
        }
        return true;
    }

    public boolean insertDataToTable(String strTableName, String strDescr, String strFileNameImg) {
        String strSQL = "";
        strSQL += "INSERT INTO '" + strTableName + "' ";
        strSQL += " ('Description', 'FileName')";
        strSQL += " VALUES (";
        if (Objects.equals(strDescr, ""))
            strSQL += "null";
        else
            strSQL += "'" + strDescr + "',";
        if (Objects.equals(strFileNameImg, ""))
            strSQL += "null";
        else
            strSQL += "'" + strFileNameImg + "'";
        strSQL += ");";
        return execStatement(strSQL);
    }

    private ResultSet execSQLquery(ArrayList<String> sqlQuery) {
        ResultSet resultSQLquery;
        String strSQL = "";
        for (String iStr : sqlQuery) {
            strSQL += iStr + "\n";
        }
        //logger.info("SQL lite query: \n" + strSQL);

        try {
            resultSQLquery = statement != null ? statement.executeQuery(strSQL) : null;
        } catch (SQLException e) {
            //logger.info("execSQLquery: " + e.getMessage());
            return null;
        }
        return resultSQLquery;
    }

    private boolean execStatement(String strSQL) {
        //logger.info(strSQL);
        try {
            statement.execute(strSQL);
        } catch (SQLException e) {
            //logger.info("Ошибка statement.execute: " + e.getMessage());
            return false;
        }
        return true;
    }

    public boolean getFileFromTableAndSaveIt(String strTableName, int id, String strField, String strFileName) {
        String strSQL = "SELECT " + strField +
                " FROM " + strTableName +
                " WHERE Id = " + id;

        ResultSet resultSQLquery = null;
        try {
            resultSQLquery = statement != null ? statement.executeQuery(strSQL) : null;
        } catch (SQLException e) {
            //logger.info("getFileFromTableAndSaveIt: " + e.getMessage());
        }
        try {
            if (resultSQLquery != null && resultSQLquery.next()) {
                File fileBlob = new File(strFileName);
                FileOutputStream fos;
                try {
                    fos = new FileOutputStream(fileBlob);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    //logger.info("getFileFromTableAndSaveIt: ошибка FileOutputStream");
                    return false;
                }

                InputStream is = resultSQLquery.getBinaryStream(strField);
                ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
                BufferedInputStream bis = new BufferedInputStream(is);

                byte[] buffer = new byte[1024];
                int bytesread;
                try {
                    while ((bytesread = bis.read(buffer, 0, buffer.length)) != -1) {
                        baos.write(buffer, 0, bytesread);
                    }
                    fos.write(baos.toByteArray());

                } catch (IOException e) {
                    e.printStackTrace();
                    //logger.info("getFileFromTableAndSaveIt: ошибка чтения потока");
                    return false;
                }
                try {
                    bis.close();
                    baos.close();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    //logger.info("getFileFromTableAndSaveIt: ошибка закрытия потоков");
                    return false;
                }
                //logger.info("<- извлечен файл " + strFileName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            //logger.info("getFileFromTableAndSaveIt: " + e.getMessage());
            return false;
        }

        return true;
    }
}