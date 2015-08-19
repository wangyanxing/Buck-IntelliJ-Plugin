import com.intellij.openapi.diagnostic.Logger;
import com.intellij.testFramework.TestLoggerFactory;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;

public abstract class BuckTestCase extends LightPlatformCodeInsightFixtureTestCase {
  static {
    Logger.setFactory(TestLoggerFactory.class);
  }

  public String getTestDataPath() {
    return "testData";
  }
}
