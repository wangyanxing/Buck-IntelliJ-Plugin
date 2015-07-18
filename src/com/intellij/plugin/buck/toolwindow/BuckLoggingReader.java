package com.intellij.plugin.buck.toolwindow;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Reader;

public abstract class BuckLoggingReader extends Reader {
  @NotNull
  protected abstract Object getLock();

  @Nullable
  protected abstract Reader getReader();

  @Override
  public int read(char[] cbuf, int off, int len) throws IOException {
    Reader reader;
    synchronized (getLock()) {
      reader = getReader();
    }
    return reader != null ? reader.read(cbuf, off, len) : -1;
  }

  @Override
  public boolean ready() throws IOException {
    Reader reader = getReader();
    return reader != null && reader.ready();
  }

  @Override
  public void close() throws IOException {
    Reader reader = getReader();
    if (reader != null) reader.close();
  }
}
