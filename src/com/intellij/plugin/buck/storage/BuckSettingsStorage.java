package com.intellij.plugin.buck.storage;

import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import org.jdom.Element;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

@State(
    name = "BuckSettings",
    storages = {
        @Storage(file = StoragePathMacros.PROJECT_FILE),
        @Storage(file = StoragePathMacros.PROJECT_CONFIG_DIR + "/buck_settings.xml", scheme = StorageScheme.DIRECTORY_BASED)
    }
)

public class BuckSettingsStorage extends AbstractProjectComponent implements PersistentStateComponent<Element> {

    public static Queue<String> HISTORICAL_PROJECT_NAMES = new LinkedList<String>();
    public static final int MAX_HISTORIES = 1;

    public static void addHistory(String project) {
        if (HISTORICAL_PROJECT_NAMES.contains(project)) {
            return;
        }

        HISTORICAL_PROJECT_NAMES.add(project);
        while (HISTORICAL_PROJECT_NAMES.size() > MAX_HISTORIES) {
            HISTORICAL_PROJECT_NAMES.remove();
        }
    }

    public static String peekHistory() {
        return HISTORICAL_PROJECT_NAMES.peek();
    }

    public BuckSettingsStorage(Project project) {
        super(project);
    }

    public Element getState() {
        final Element e = new Element("state");
        Element childHisProjects = new Element("histories");
        e.addContent(childHisProjects);

        for (String projName : HISTORICAL_PROJECT_NAMES) {
            Element child = new Element("project");
            child.setAttribute("name", projName);
            childHisProjects.addContent(child);
        }
        return e;
    }

    public void loadState(Element state) {
        HISTORICAL_PROJECT_NAMES.clear();

        Element his = state.getChild("histories");
        for (Element ch : his.getChildren()) {
            String name = ch.getAttributeValue("name");
            HISTORICAL_PROJECT_NAMES.add(name);
        }
    }

    @NonNls
    @NotNull
    public String getComponentName() {
        return "BuckSettings";
    }
}
