package main;

import db.AccessToSQL;
import db.RecordReport;
import db.SQLQuery;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;

class WorkWithDB {
    private Logger logger;
    private AccessToSQL accessToSQL;

    WorkWithDB(Logger logger) {
        this.logger = logger;
        initAccessToFB();

        SQLQuery sqlQuery = null;
        try {
            sqlQuery = new SQLQuery(logger, accessToSQL.getConnection());
        } catch (SQLException e) {
            logger.info("ошибка SQLQueryFromFB: " + e.getMessage());
        }
        String strTableName = "TableForTesting";
        try {
            //проверяем, есть ли такая таблица
            if (!sqlQuery.isTableExists(strTableName)) {
                //если её нет - создаём
                if (sqlQuery.createTable(strTableName))
                    logger.info("таблица " + strTableName + " создана");
            } else {
                logger.info("таблица " + strTableName + " найдена");
                logger.info("");
                logger.info("--- удаляем все данные из таблицы");
                sqlQuery.deleteAllDataFromTable(strTableName);
                logger.info("");
                logger.info("--- заносим новые данные в таблицу");
                /*sqlQuery.insertDataToTable(strTableName, 1, "Иконка Java", IMG_JAVA_PNG);
                sqlQuery.insertDataToTable(strTableName, 2, "Иконка FireBird", IMG_FIREBIRD_PNG);
                sqlQuery.insertDataToTable(strTableName, 3, "Иконка WordPress", IMG_WORDPRESS_PNG);*/
                logger.info("");
                logger.info("--- считываем все данные из таблицы");
                ArrayList<RecordReport> listRecord = sqlQuery.getReport_AllFieldsFromTable(strTableName);
                logger.info("--- выводим полученные данные в логгер");
                if (listRecord.size() != 0) {
                    for (RecordReport itemRecord : listRecord) {
                        logger.info(itemRecord.toString());
                    }
                }
                logger.info("");
                logger.info("--- добавляем в БД файлы изображений");
                /*sqlQuery.updateDataFileFromResourceToTable(strTableName, 1, "Img", IMG_JAVA_PNG_INRES);
                sqlQuery.updateDataFileFromResourceToTable(strTableName, 2, "Img", IMG_FIREBIRD_PNG_INRES);
                sqlQuery.updateDataFileFromResourceToTable(strTableName, 3, "Img", IMG_WORDPRESS_PNG_INRES);
                logger.info("");
                logger.info("--- извлекаем из БД файлы изображений и сохраняем их в корневой папке");
                sqlQuery.getFileFromTableAndSaveIt(strTableName, 1, "Img", IMG_JAVA_PNG);
                sqlQuery.getFileFromTableAndSaveIt(strTableName, 2, "Img", IMG_FIREBIRD_PNG);
                sqlQuery.getFileFromTableAndSaveIt(strTableName, 3, "Img", IMG_WORDPRESS_PNG);*/
            }
        } finally {
            closeAccessToFB();
        }
    }

    private void initAccessToFB() {
        accessToSQL = new AccessToSQL(logger,"");
        if (accessToSQL.getAccess()) {
            logger.info("доступ к БД получен");
        } else {
            logger.info("ошибка доступа к БД");
            System.exit(1);
        }
    }

    private void closeAccessToFB() {
        accessToSQL.closeAccess();
        logger.info("доступ к БД отключен");
    }
}