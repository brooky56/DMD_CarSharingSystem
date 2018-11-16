package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;

import static utils.ConstantForAll.LOGGER_PARAM;

public class UtilsForAll {

    public static Class getMainClass() {
        return main.Main.class;
    }

    public static void exitFromProgram() {
        System.exit(0);
    }

    public static boolean setMyLoggerConfiguration() {
        try {
            InputStream inputStream = getMainClass().getResourceAsStream(LOGGER_PARAM);
            if (inputStream != null) {
                LogManager.getLogManager().readConfiguration(inputStream);
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}