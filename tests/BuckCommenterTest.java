import com.intellij.openapi.actionSystem.IdeActions;
import org.jetbrains.annotations.NotNull;

public class BuckCommenterTest extends BuckTestCase {

  private void doTest(@NotNull String actionId) {
    myFixture.configureByFile("commenter/" + getTestName(false) + "/BUCK");
    myFixture.performEditorAction(actionId);
    myFixture.checkResultByFile("commenter/" + getTestName(false) + "/after.BUCK", true);
  }

  public void testLineCommenter1() {
    doTest(IdeActions.ACTION_COMMENT_LINE);
  }

  public void testLineCommenter2() {
    doTest(IdeActions.ACTION_COMMENT_LINE);
  }

}
