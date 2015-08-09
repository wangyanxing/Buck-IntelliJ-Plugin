import com.intellij.openapi.application.ApplicationManager;
import com.intellij.psi.codeStyle.CodeStyleManager;
import org.jetbrains.annotations.Nullable;

public class BuckFormatterTest extends BuckTestCase {

  public void testSimple1(){
    doTest();
  }

  public void testSimple2(){
    doTest();
  }

  public void testSimple3(){
    doTest();
  }

  public void testSimple4(){
    doTest();
  }

  public void testSimple5(){
    doTest();
  }

  public void testSimple6(){
    doTest();
  }

  public void testBlankLine1(){
    doTest();
  }

  public void testBlankLine2(){
    doTest();
  }

  public void testBlankLine3(){
    doTest();
  }

  public void testBlankLine4(){
    doTest();
  }

  public void testEnterAfterComma1() {
    doTestEnter();
  }

  public void testEnterAfterComma2() {
    doTestEnter();
  }

  public void testEnterAfterComma3() {
    doTestEnter();
  }

  public void testEnterAfterComma4() {
    doTestEnter();
  }

  public void testEnterAfterLBracket1() {
    doTestEnter();
  }

  public void testEnterAfterLBracket2() {
    doTestEnter();
  }

  public void testEnterAfterRBracket1() {
    doTestEnter();
  }

  public void testEnterAfterRBracket2() {
    doTestEnter();
  }

  public void testEnterAfterLParentheses1() {
    doTestEnter();
  }

  public void testEnterAfterLParentheses2() {
    doTestEnter();
  }

  public void testEnterAfterRParentheses1() {
    doTestEnter();
  }

  public void doTest() {
    doTest(null);
  }

  public void doTestEnter() {
    doTest('\n');
  }

  public void doTest(@Nullable Character c) {
    String testName = getTestName(true);
    myFixture.configureByFile("formatter/" + getTestName(false) + "/BUCK");
    doTest(c, testName);
    assertSameLinesWithFile(getTestDataPath() + "/formatter/" + getTestName(true) + "/after.BUCK", myFixture.getFile().getText());
  }

  private String doTest(@Nullable Character c, String testName) {
    if (c == null) {
      ApplicationManager.getApplication().runWriteAction(new Runnable() {
        @Override
        public void run() {
          CodeStyleManager.getInstance(getProject()).reformat(myFixture.getFile());
        }
      });
    }
    else {
      myFixture.type(c);
    }
    return String.format("%s-after.BUCK", testName);
  }
}
