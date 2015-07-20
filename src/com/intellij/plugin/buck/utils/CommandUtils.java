package com.intellij.plugin.buck.utils;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.*;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;

public class CommandUtils {

  private static final int TIMEOUT = 10 * 60 * 1000;

  public static String runBuckCommand(String projectDir, String[] command) {
    try {
      final GeneralCommandLine commandLine =
          new GeneralCommandLine(command).withWorkDirectory(projectDir);
      Process process = commandLine.createProcess();
      final ProgressIndicator indicator = ProgressManager.getInstance().getProgressIndicator();
      final ProcessOutput result;
      final CapturingProcessHandler handler = new CapturingProcessHandler(process);

      if (indicator != null) {
        handler.addProcessListener(new ProcessAdapter() {
          @Override
          public void onTextAvailable(ProcessEvent event, Key outputType) {
            if (outputType == ProcessOutputTypes.STDOUT) {
              for (String line : StringUtil.splitByLines(event.getText())) {
                final String trimmed = line.trim();
                if (isMeaningfulOutput(trimmed)) {
                  indicator.setText2(trimmed);
                }
              }
            }
          }
          private boolean isMeaningfulOutput(@NotNull String trimmed) {
            return trimmed.length() > 3;
          }
        });
        result = handler.runProcessWithProgressIndicator(indicator);
      }
      else {
        result = handler.runProcess(TIMEOUT);
      }
      return result.getStdout();
    } catch (ExecutionException e) {
      e.printStackTrace();
      return null;
    }
  }

}
