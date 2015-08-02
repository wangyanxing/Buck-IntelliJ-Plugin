package com.intellij.plugin.buck.depsconverter;


import com.intellij.codeInspection.InspectionToolProvider;

public class BuckDepsConverterInspectionProvider implements InspectionToolProvider {
  public Class[] getInspectionClasses() {
    return new Class[] { BuckDepsConverterInspection.class};
  }
}
