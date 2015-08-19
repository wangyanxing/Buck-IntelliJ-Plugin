import com.intellij.codeInsight.actions.OptimizeImportsAction;
import com.intellij.ide.DataManager;

public class BuckDependencyOptimizerTest extends BuckTestCase {
  public void testSimple() {
    doTest();
  }

  public void testLintRule() {
    doTest();
  }

  private void doTest() {
    myFixture.configureByFile("dependencyOptimizer/" + getTestName(false) + "/before.BUCK");
    OptimizeImportsAction.actionPerformedImpl(DataManager.getInstance().getDataContext(
        myFixture.getEditor().getContentComponent()));
    myFixture.checkResultByFile("dependencyOptimizer/" + getTestName(true) + "/after.BUCK");
  }
}