package utils;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.logging.*;

import static utils.ConstantForAll.*;

public class UtilsForAll {

    public static Class getMainClass() {
        return main.Main.class;
    }

    public static void exitFromProgram() {
        System.exit(0);
    }

    public static boolean setLoggerFileHandler(Logger logger) {
        //удалить все хэндлерсы для логгера
        Handler[] handlers = logger.getHandlers();
        for (Handler handler : handlers) {
            logger.removeHandler(handler);
        }
        //добавить новый
        FileHandler fh;
        File fileLog = createDirForLog();
        if (fileLog == null) return false;
        String logFileName = fileLog.getAbsolutePath();
        logFileName += "\\" + LOG_FILENAME;
        try {
            fh = new FileHandler(logFileName, false);
            fh.setFormatter(new Formatter() {
                @Override
                public String format(LogRecord record) {
                    Date iDate = java.sql.Timestamp.valueOf(LocalDateTime.now());
                    String itemDateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(iDate);
                    return String.format("[%s] %s: %s%n",
                            itemDateStr,
                            fh.getLevel().getName(),
                            record.getMessage()
                    );
                }
            });

            logger.addHandler(fh);
            logger.setUseParentHandlers(false);
        } catch (SecurityException | IOException e) {
            logger.log(Level.SEVERE, "Exception: ", e);
            return false;
        }
        return true;
    }


    public static String getFileNameXMLParams() {
        File fileParams = createDirForXMLParams();
        if (fileParams == null) return "";
        String strFileName = fileParams.getAbsolutePath();
        strFileName += "\\" + FILE_XML_PARAMS;
        return strFileName;
    }

    private static File createDirForLog() {
        if (UtilsForAll.createDirectoryInMyDocuments(DIRECTORY_USER_PROG) == null) return null;
        return UtilsForAll.createDirectoryInMyDocuments(DIRECTORY_USER_PROG_LOG);
    }

    private static File createDirForXMLParams() {
        if (UtilsForAll.createDirectoryInMyDocuments(DIRECTORY_USER_PROG) == null) return null;
        return UtilsForAll.createDirectoryInMyDocuments(DIRECTORY_USER_PROG_SET);
    }

    private static File createDirectoryInMyDocuments(String strDirName) {
        String strDir = utils.UtilsForAll.getUserDirectoryMyDocuments(strDirName);
        final File fileDir = new File(strDir);
        if (!fileDir.exists())
            if (!fileDir.mkdir()) return null;
        return fileDir;
    }

    private static String getUserDirectoryMyDocuments(String strDirName) {
        return getUserDirectoryMyDocuments() + "\\" + strDirName;
    }

    private static String getUserDirectoryMyDocuments() {
        return new JFileChooser().getFileSystemView().getDefaultDirectory().toString();
    }

}