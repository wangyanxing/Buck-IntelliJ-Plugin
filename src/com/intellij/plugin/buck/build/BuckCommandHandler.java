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
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Iterator;

/**
 * The handler for buck commands with text outputs.
 */
public abstract class BuckCommandHandler {

  protected static final Logger LOG = Logger.getInstance(BuckCommandHandler.class);
  private static final long LONG_TIME = 10 * 1000;

  protected final Project mProject;
  protected final BuckCommand mCommand;

  private final File mWorkingDirectory;
  private final GeneralCommandLine mCommandLine;
  private final Object mProcessStateLock = new Object();

  @SuppressWarnings({"FieldAccessedSynchronizedAndUnsynchronized"})
  private Process mProcess;

  @SuppressWarnings({"FieldAccessedSynchronizedAndUnsynchronized"})
  private OSProcessHandler mHandler;

  /**
   * Character set to use for IO.
   */
  @SuppressWarnings({"FieldAccessedSynchronizedAndUnsynchronized"})
  private Charset mCharset = CharsetToolkit.UTF8_CHARSET;

  /**
   * Buck execution start timestamp.
   */
  private long mStartTime;

  /**
   * The partial line from stderr stream.
   */
  private final StringBuilder mStderrLine = new StringBuilder();

  /**
   * @param project   a project
   * @param directory a process directory
   * @param command   a command to execute (if empty string, the parameter is ignored)
   */
  public BuckCommandHandler(
      Project project,
      File directory,
      BuckCommand command) {

    String buckExecutable = BuckSettingsProvider.getInstance().getState().buckExecutable;

    mProject = project;
    mCommand = command;
    mCommandLine = new GeneralCommandLine();
    mCommandLine.setExePath(buckExecutable);
    mWorkingDirectory = directory;
    mCommandLine.withWorkDirectory(mWorkingDirectory);
    mCommandLine.addParameter(command.name());
  }

  /**
   * Start process
   */
  public synchronized void start() {
    checkNotStarted();

    try {
      mStartTime = System.currentTimeMillis();
      mProcess = startProcess();
      startHandlingStreams();
    } catch (ProcessCanceledException e) {

    } catch (Throwable t) {
      if (!mProject.isDisposed()) {
        LOG.error(t);
      }
    }
  }

  /**
   * @return true if process is started.
   */
  public final synchronized boolean isStarted() {
    return mProcess != null;
  }

  /**
   * Check that process is not started yet.
   *
   * @throws IllegalStateException if process has been already started
   */
  private void checkNotStarted() {
    if (isStarted()) {
      throw new IllegalStateException("The process has been already started");
    }
  }

  /**
   * Check that process is started.
   *
   * @throws IllegalStateException if process has not been started
   */
  protected final void checkStarted() {
    if (!isStarted()) {
      throw new IllegalStateException("The process is not started yet");
    }
  }

  public GeneralCommandLine command() {
    return mCommandLine;
  }

  /**
   * @return a context project
   */
  public Project project() {
    return mProject;
  }

  /**
   * Start the buck process.
   */
  @Nullable
  protected Process startProcess() throws ExecutionException {
    synchronized (mProcessStateLock) {
      final ProcessHandler processHandler = createProcess(mCommandLine);
      mHandler = (OSProcessHandler) processHandler;
      return mHandler.getProcess();
    }
  }

  /**
   * Start handling process output streams for the handler.
   */
  protected void startHandlingStreams() {
    if (mHandler == null) {
      return;
    }
    mHandler.addProcessListener(new ProcessListener() {
      public void startNotified(final ProcessEvent event) {
      }

      public void processTerminated(final ProcessEvent event) {
        BuckCommandHandler.this.processTerminated();
      }

      public void processWillTerminate(
          final ProcessEvent event,
          final boolean willBeDestroyed) {
      }

      public void onTextAvailable(final ProcessEvent event, final Key outputType) {
        BuckCommandHandler.this.onTextAvailable(event.getText(), outputType);
      }
    });
    mHandler.startNotify();
  }

  /**
   * Wait for process termination.
   */
  public void waitFor() {
    checkStarted();
    if (mHandler != null) {
      mHandler.waitFor();
    }
  }

  public ProcessHandler createProcess(GeneralCommandLine commandLine)
      throws ExecutionException {
    Process process = commandLine.createProcess();
    return new MyOSProcessHandler(process, commandLine, getCharset());
  }

  private static class MyOSProcessHandler extends OSProcessHandler {
    private final Charset myCharset;

    public MyOSProcessHandler(
        Process process,
        GeneralCommandLine commandLine,
        Charset charset) {
      super(process, commandLine.getCommandLineString());
      myCharset = charset;
    }

    @Override
    public Charset getCharset() {
      return myCharset;
    }
  }

  /**
   * @return a character set to use for IO.
   */
  public Charset getCharset() {
    return mCharset;
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
    if (mStartTime > 0) {
      long time = System.currentTimeMillis() - mStartTime;
      if (!LOG.isDebugEnabled() && time > LONG_TIME) {
        LOG.info(String.format("buck %s took %s ms. Command parameters: %n%s",
            mCommand,
            time,
            mCommandLine.getCommandLineString()));
      } else {
        LOG.debug(String.format("buck %s took %s ms", mCommand, time));
      }
    } else {
      LOG.debug(String.format("buck %s finished.", mCommand));
    }
  }

  protected void processTerminated() {
    if (mStderrLine.length() != 0) {
      onTextAvailable("\n\r", ProcessOutputTypes.STDERR);
    }
  }

  protected void onTextAvailable(final String text, final Key outputType) {
    Iterator<String> lines = LineHandlerHelper.splitText(text).iterator();
    // We only care about STDERR for buck outputs
    if (ProcessOutputTypes.STDERR == outputType) {
      notifyLines(outputType, lines, mStderrLine);
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
  protected abstract void notifyLines(
      final Key outputType,
      final Iterator<String> lines,
      final StringBuilder lineBuilder);

  protected abstract boolean beforeCommand();

  protected abstract void afterCommand();
}
