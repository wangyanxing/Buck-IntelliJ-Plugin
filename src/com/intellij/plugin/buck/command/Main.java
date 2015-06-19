package com.intellij.plugin.buck.command;
/**
 * Created by longma on 6/18/15.
 */
public class Main {
    public static void main(String[] args) {
        BuckCommandController buckCommandController = new BuckCommandController();
        buckCommandController.executeBuckCommand(BuckCommandUtils.COMMAND_TYPE.COMMAND_INSTALL);
    }
}
