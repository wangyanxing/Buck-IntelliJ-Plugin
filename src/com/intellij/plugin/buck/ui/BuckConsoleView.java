package com.intellij.plugin.buck.ui;

import com.intellij.diagnostic.logging.DefaultLogFilterModel;
import com.intellij.diagnostic.logging.LogConsoleBase;
import com.intellij.diagnostic.logging.LogConsoleListener;
import com.intellij.diagnostic.logging.LogFilterModel;
import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.Key;
import com.intellij.plugin.buck.toolwindow.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public abstract class BuckConsoleView implements Disposable {
    private static final Logger LOG = Logger.getInstance("#com.intellij.plugin.buck.ui.BuckConsoleView");

    private final Project myProject;

    private JPanel myPanel;

    public static final Key<BuckConsoleView> BUCK_CONSOLE_VIEW_KEY = Key.create("BUCK_CONSOLE_VIEW_KEY");

    private final Object myLock = new Object();
    private final LogConsoleBase myLogConsole;

    private volatile Reader myCurrentReader;
    private volatile Writer myCurrentWriter;

    private void updateInUIThread() {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                if (myProject.isDisposed()) {
                    return;
                }
                updateLogConsole();
            }
        });
    }

    Project getProject() {
        return myProject;
    }

    @NotNull
    public LogConsoleBase getLogConsole() {
        return myLogConsole;
    }

    private class MyLoggingReader extends BuckLoggingReader {
        @Override
        @NotNull
        protected Object getLock() {
            return myLock;
        }

        @Override
        protected Reader getReader() {
            return myCurrentReader;
        }
    }

    public BuckConsoleView(final Project project) {
        myProject = project;

        Disposer.register(myProject, this);

        myLogConsole = new BuckLogConsole(project, new DefaultLogFilterModel(project));
        myLogConsole.addListener(new LogConsoleListener() {
            @Override
            public void loggingWillBeStopped() {
                if (myCurrentWriter != null) {
                    try {
                        myCurrentWriter.close();
                    }
                    catch (IOException e) {
                        LOG.error(e);
                    }
                }
            }
        });

        JComponent consoleComponent = myLogConsole.getComponent();

        final ConsoleView console = myLogConsole.getConsole();
        if (console != null) {
            final ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.UNKNOWN,
                    myLogConsole.getOrCreateActions(), false);
            toolbar.setTargetComponent(console.getComponent());
            final JComponent tbComp1 = toolbar.getComponent();
            myPanel.add(tbComp1, BorderLayout.WEST);
        }

        myPanel.add(consoleComponent, BorderLayout.CENTER);
        Disposer.register(this, myLogConsole);

        updateLogConsole();

        //selectFilter(AndroidLogcatFiltersPreferences.getInstance(myProject).TOOL_WINDOW_CONFIGURED_FILTER);
    }

    private void updateLogConsole() {
        /*
        final ConsoleView console = myLogConsole.getConsole();
        if (console != null) {
            console.clear();
        }
        */
    }

    @NotNull
    public ActionGroup getTopToolbarActions() {
        DefaultActionGroup group = new DefaultActionGroup();
        group.add(new ChooseTargetAction());
        return group;
    }

    @NotNull
    public ActionGroup getLeftToolbarActions() {
        DefaultActionGroup group = new DefaultActionGroup();
        group.add(new BuckInstallAction());
        group.add(new BuckBuildAction());
        group.add(new BuckUninstallAction());
        return group;
    }

    protected abstract boolean isActive();

    public void activate() {
        if (isActive()) {
            updateLogConsole();
        }
        if (myLogConsole != null) {
            myLogConsole.activate();
        }
    }

    /** Returns true if there are any filters applied currently. */
    public boolean isFiltered() {
        return false;
    }

    public JPanel getContentPanel() {
        return myPanel;
    }

    @Override
    public void dispose() {
    }

    public class BuckLogConsole extends LogConsoleBase {
        @SuppressWarnings("IOResourceOpenedButNotSafelyClosed")
        public BuckLogConsole(Project project, LogFilterModel logFilterModel) {
            super(project, new MyLoggingReader(), "", false, logFilterModel);
            ConsoleView console = getConsole();
            if (console instanceof ConsoleViewImpl) {
                ConsoleViewImpl c = ((ConsoleViewImpl)console);
                c.addCustomConsoleAction(new Separator());
            }
        }

        @Override
        public boolean isActive() {
            return BuckConsoleView.this.isActive();
        }
    }
}
