import com.intellij.plugin.buck.lang.BuckParserDefinition;
import com.intellij.testFramework.ParsingTestCase;

public class BuckParsingTest extends ParsingTestCase {

  public BuckParsingTest() {
    super("psi", "BUCK", new BuckParserDefinition());
  }

  @Override
  protected String getTestDataPath() {
    return "testData";
  }

  @Override
  protected boolean skipSpaces() {
    return true;
  }

  private void doTest() {
    doTest(true);
  }

  public void testSimple1() {
    doTest();
  }

  public void testSimple2() {
    doTest();
  }

  public void testGlob1() {
    doTest();
  }

  public void testGlob2() {
    doTest();
  }

  public void testGlob3() {
    doTest();
  }

  public void testInclude() {
    doTest();
  }

  public void testObject() {
    doTest();
  }

  public void testNested() {
    doTest();
  }

  public void testLineComments() {
    doTest();
  }

  public void testTwoRules1() {
    doTest();
  }

  public void testTwoRules2() {
    doTest();
  }

  public void testPutTogether() {
    doTest();
  }

}
