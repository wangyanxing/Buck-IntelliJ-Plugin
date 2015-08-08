import com.intellij.openapi.diagnostic.Logger;
import com.intellij.plugin.buck.config.BuckCodeStyleSettings;
import com.intellij.plugin.buck.lang.BuckLanguage;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CodeStyleSettingsManager;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.testFramework.TestLoggerFactory;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;

public abstract class BuckTestCase extends LightCodeInsightFixtureTestCase {
  static {
    Logger.setFactory(TestLoggerFactory.class);
  }

  protected CodeStyleSettings getCodeStyleSettings() {
    return CodeStyleSettingsManager.getSettings(getProject());
  }

  protected CommonCodeStyleSettings getCommonCodeStyleSettings() {
    return getCodeStyleSettings().getCommonSettings(BuckLanguage.INSTANCE);
  }

  protected BuckCodeStyleSettings getCustomCodeStyleSettings() {
    return getCodeStyleSettings().getCustomSettings(BuckCodeStyleSettings.class);
  }

  protected CommonCodeStyleSettings.IndentOptions getIndentOptions() {
    final CommonCodeStyleSettings.IndentOptions options =
        getCommonCodeStyleSettings().getIndentOptions();
    assertNotNull(options);
    return options;
  }

  public String getTestDataPath() {
    return "testData";
  }
}
