import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.util.ArrayUtil;

public class BuckCompletionTest extends BuckTestCase {

  private static final String[] NOTHING = ArrayUtil.EMPTY_STRING_ARRAY;

  private void doTest(String... variants) {
    myFixture.testCompletionVariants("completion/" + getTestName(false) + "/BUCK", variants);
  }

  private void doTestSingleVariant() {
    myFixture.configureByFile("completion/" + getTestName(false) + "/BUCK");
    final LookupElement[] variants = myFixture.completeBasic();
    assertNull(variants);
    myFixture.checkResultByFile("completion/" + getTestName(false) + "/after.BUCK" );
  }

  public void testKeywords1() {
    doTestSingleVariant();
  }

  public void testKeywords2() {
    doTestSingleVariant();
  }

  public void testInString() {
    doTest(NOTHING);
  }

  public void testVariants() {
    doTest("android_build_config", "android_binary");
  }
}
