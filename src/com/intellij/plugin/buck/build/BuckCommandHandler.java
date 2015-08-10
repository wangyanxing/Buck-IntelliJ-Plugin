package com.intellij.plugin.buck.build;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessListener;
import com.intellij.execution.process.ProcessOutputTypes;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vcs.LineHandlerHelper;
import com.intellij.openapi.vfs.CharsetToolkit;
import com.intellij.plugin.buck.config.BuckSettingsProvider;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Iterator;

/**
 * The handler for buck commands with text outputs
 */
public abstract class BuckCommandHandler {

  protected static final Logger LOG = Logger.getInstance(BuckCommandHandler.class);
  private static final long LONG_TIME = 10 * 1000;

  protected final Project myProject;
  protected final BuckCommand myCommand;

  private final File myWorkingDirectory;
  private final GeneralCommandLine myCommandLine;
  private final Object myProcessStateLock = new Object();
  private volatile boolean myIsDestroyed;
  private Integer myExitCode; // exit code or null if exit code is not yet available

  @SuppressWarnings({"FieldAccessedSynchronizedAndUnsynchronized"})
  private Process myProcess;

  @SuppressWarnings({"FieldAccessedSynchronizedAndUnsynchronized"})
  private OSProcessHandler myHandler;

  @SuppressWarnings({"FieldAccessedSynchronizedAndUnsynchronized"})
  @NonNls
  @NotNull
  private Charset myCharset = CharsetToolkit.UTF8_CHARSET; // Character set to use for IO

  private long myStartTime; // buck execution start timestamp

  // The partial line from stderr stream
  private final StringBuilder myStderrLine = new StringBuilder();

  /**
   * @param project   a project
   * @param directory a process directory
   * @param command   a command to execute (if empty string, the parameter is ignored)
   */
  public BuckCommandHandler(@NotNull Project project,
                               @NotNull File directory,
                               @NotNull BuckCommand command) {

    String buckExecutable = BuckSettingsProvider.getInstance().getState().buckExecutable;

    myProject = project;
    myCommand = command;
    myCommandLine = new GeneralCommandLine();
    myCommandLine.setExePath(buckExecutable);
    myWorkingDirectory = directory;
    myCommandLine.withWorkDirectory(myWorkingDirectory);
    myCommandLine.addParameter(command.name());
  }

  /**
   * Start process
   */
  public synchronized void start() {
    checkNotStarted();

    try {
      myStartTime = System.currentTimeMillis();
      myProcess = startProcess();
      startHandlingStreams();
    } catch (ProcessCanceledException e) {

    } catch (Throwable t) {
      if (!myProject.isDisposed()) {
        LOG.error(t);
      }
    }
  }

  /**
   * @return true if process is started
   */
  public final synchronized boolean isStarted() {
    return myProcess != null;
  }

  /**
   * check that process is not started yet
   *
   * @throws IllegalStateException if process has been already started
   */
  private void checkNotStarted() {
    if (isStarted()) {
      throw new IllegalStateException("The process has been already started");
    }
  }

  /**
   * check that process is started
   *
   * @throws IllegalStateException if process has not been started
   */
  protected final void checkStarted() {
    if (!isStarted()) {
      throw new IllegalStateException("The process is not started yet");
    }
  }

  /**
   * @return exit code for process if it is available
   */
  public synchronized int getExitCode() {
    if (myExitCode == null) {
      throw new IllegalStateException("Exit code is not yet available");
    }
    return myExitCode.intValue();
  }

  /**
   * @param exitCode a exit code for process
   */
  protected synchronized void setExitCode(int exitCode) {
    myExitCode = exitCode;
  }

  public GeneralCommandLine command() {
    return myCommandLine;
  }

  /**
   * @return a context project
   */
  public Project project() {
    return myProject;
  }

  /**
   * Start the buck process
   */
  @Nullable
  protected Process startProcess() throws ExecutionException {
    synchronized (myProcessStateLock) {
      if (myIsDestroyed) {
        return null;
      }
      final ProcessHandler processHandler = createProcess(myCommandLine);
      myHandler = (OSProcessHandler)processHandler;
      return myHandler.getProcess();
    }
  }

  /**
   * Destroy process
   */
  public void destroyProcess() {
    synchronized (myProcessStateLock) {
      myIsDestroyed = true;
      if (myHandler != null) {
        myHandler.destroyProcess();
      }
    }
  }

  /**
   * Cancel activity
   */
  public synchronized void cancel() {
    checkStarted();
    destroyProcess();
  }

  /**
   * Start handling process output streams for the handler.
   */
  protected void startHandlingStreams() {
    if (myHandler == null) {
      return;
    }
    myHandler.addProcessListener(new ProcessListener() {
      public void startNotified(final ProcessEvent event) {
        // do nothing
      }

      public void processTerminated(final ProcessEvent event) {
        final int exitCode = event.getExitCode();
        setExitCode(exitCode);
        BuckCommandHandler.this.processTerminated(exitCode);
      }

      public void processWillTerminate(final ProcessEvent event, final boolean willBeDestroyed) {
        // do nothing
      }

      public void onTextAvailable(final ProcessEvent event, final Key outputType) {
        BuckCommandHandler.this.onTextAvailable(event.getText(), outputType);
      }
    });
    myHandler.startNotify();
  }

  /**
   * Wait for process termination
   */
  public void waitFor() {
    checkStarted();
    if (myHandler != null) {
      myHandler.waitFor();
    }
  }

  public ProcessHandler createProcess(@NotNull GeneralCommandLine commandLine)
      throws ExecutionException {
    Process process = commandLine.createProcess();
    return new MyOSProcessHandler(process, commandLine, getCharset());
  }

  private static class MyOSProcessHandler extends OSProcessHandler {
    @NotNull
    private final Charset myCharset;

    public MyOSProcessHandler(Process process,
                              GeneralCommandLine commandLine,
                              @NotNull Charset charset) {
      super(process, commandLine.getCommandLineString());
      myCharset = charset;
    }

    @NotNull
    @Override
    public Charset getCharset() {
      return myCharset;
    }
  }

  /**
   * @return a character set to use for IO
   */
  @NotNull
  public Charset getCharset() {
    return myCharset;
  }

  public void runInCurrentThread(@Nullable Runnable postStartAction) {
    if (!beforeCommand()) {
      return;
    }

    start();
    if (isStarted()) {
      if (postStartAction != null) {
        postStartAction.run();
      }
      waitFor();
    }
    afterCommand();
    logTime();
  }

  private void logTime() {
    if (myStartTime > 0) {
      long time = System.currentTimeMillis() - myStartTime;
      if (!LOG.isDebugEnabled() && time > LONG_TIME) {
        LOG.info(String.format("buck %s took %s ms. Command parameters: %n%s",
            myCommand,
            time,
            myCommandLine.getCommandLineString()));
      }
      else {
        LOG.debug(String.format("buck %s took %s ms", myCommand, time));
      }
    }
    else {
      LOG.debug(String.format("buck %s finished.", myCommand));
    }
  }

  protected void processTerminated(final int exitCode) {
    if (myStderrLine.length() != 0) {
      onTextAvailable("\n\r", ProcessOutputTypes.STDERR);
    }
  }

  protected void onTextAvailable(final String text, final Key outputType) {
    Iterator<String> lines = LineHandlerHelper.splitText(text).iterator();
    // We only care about STDERR for buck outputs
    if (ProcessOutputTypes.STDERR == outputType) {
      notifyLines(outputType, lines, myStderrLine);
    }
  }

  /**
   * Notify listeners for each complete line. Note that in the case of stderr,
   * the last line is saved.
   *
   * @param outputType  output type
   * @param lines       line iterator
   * @param lineBuilder a line builder
   */
  protected abstract void notifyLines(final Key outputType,
                           final Iterator<String> lines,
                           final StringBuilder lineBuilder);

  protected abstract boolean beforeCommand();

  protected abstract void afterCommand();
}
