package com.intellij.plugin.buck.command;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.plugin.buck.notification.EventLogger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by longma on 6/18/15.
 */
public class BuckCommandUtils {
    public static String ENV_DIR = "//Users//longma//fbandroid-hg";
    public static String PROJECT_NAME = "fb4a";
    public static Project sProject;

    public static enum ProcessStatus {
        NONE,
        STARTED,
        PARSING_BUCK_FILES_FINISHED,
        BUILDING_FINISHED,
        INSTALLING_FINISHED,
    }

    public static enum COMMAND_TYPE{
        COMMAND_BUILD,
        COMMAND_INSTALL,
        COMMAND_UNINSTALL,
        COMMAND_TEST
    }

    public static String[] getCommand(COMMAND_TYPE type, String projectName){
        switch (type) {
            case COMMAND_INSTALL:
                return new String[]{"buck", "install", projectName};
            case COMMAND_UNINSTALL:
                return new String[]{"buck", "uninstall", projectName};
            case COMMAND_BUILD:
                return new String[]{"buck", "build", projectName};
            case COMMAND_TEST:
                return new String[]{"buck", "test", projectName};
            default:
                break;
        }
        return null;
    }

    public static boolean contains(String src, String word) {
        if (src.matches(".*\\b" + word + "\\b.*")) {
            return true;
        }
        return false;
    }

    public static String getNumJobDone(String src) {
        Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(src);
        while(m.find()) {
            if(contains(m.group(1), "JOBS")) {
                return m.group(1);
            }
            return null;
        }
        return null;
    }

    public static void outputLog(String msg) {
        EventLogger.showOverChangesView(sProject, msg, MessageType.ERROR);
    }
}
