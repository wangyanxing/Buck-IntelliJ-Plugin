package com.intellij.plugin.buck.utils;

import com.google.gson.Gson;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.plugin.buck.ui.BuckToolWindowFactory;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Map;

public class BuckProgressListener extends WebSocketClient {

  private String mCurrentBuildID;
  private int mTotalNumRules = 0;
  private int mBuiltNumRules = 0;

  public BuckProgressListener(URI serverURI) {
    super(serverURI);
  }

  @Override
  public void onOpen(ServerHandshake serverHandshake) {
    System.out.println("opened connection");

    ApplicationManager.getApplication().invokeLater(new Runnable() {
      @Override
      public void run() {
        BuckBuildManager.getInstance().processPostponingCommand();
      }
    });
  }

  @Override
  public void onMessage(String message) {
    //System.out.println(message);
    Gson gson = new Gson();
    Map messageMap = gson.fromJson(message, Map.class);
    String type = (String) messageMap.get("type");

    String buildID = (String) messageMap.get("buildId");
    if (mCurrentBuildID == null) {
      if (type.equals("BuildStarted")) {
        mCurrentBuildID = buildID;
        mTotalNumRules = 0;
        mBuiltNumRules = 0;

        BuckToolWindowFactory.outputConsoleMessage(
            "Build started\n", ConsoleViewContentType.NORMAL_OUTPUT);
      } else {
        return;
      }
    }

    if (mCurrentBuildID == null || !mCurrentBuildID.equals(buildID)) {
      return;
    }

    if (type.equals("RuleCountCalculated")) {
      Double numRules = (Double) messageMap.get("numRules");
      mTotalNumRules = numRules.intValue();
    } else if (type.equals("BuildRuleFinished")) {
      mBuiltNumRules++;
    } else if (type.equals("BuildFinished")) {
      mCurrentBuildID = null;
      mTotalNumRules = 0;
      mBuiltNumRules = 0;

      BuckToolWindowFactory.outputConsoleMessage(
          "Build finished\n", ConsoleViewContentType.NORMAL_OUTPUT);
    }
  }

  @Override
  public void onClose(int i, String s, boolean b) {
    System.out.println("Connection closed");
  }

  @Override
  public void onFragment( Framedata fragment ) {
    System.out.println("received fragment:" + new String(fragment.getPayloadData().array()));
  }

  @Override
  public void onError(Exception e) {
    e.printStackTrace();
    System.out.println(e.getMessage());
  }

}
