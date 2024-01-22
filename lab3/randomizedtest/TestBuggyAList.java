package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  private AListNoResizing<Integer> simple;
  private BuggyAList<Integer> buggy;

  @Before
  public void setUp() {
    simple = new AListNoResizing<>();
    buggy = new BuggyAList<>();
  }

  @Test
  public void testThreeAddThreeRemove() {
    for (int i = 4; i < 7; i++) {
      simple.addLast(i);
      buggy.addLast(i);
    }
    assertEquals(simple.size(), buggy.size());
    for (int i = 0; i < 3; i++) {
      assertEquals(simple.removeLast(), buggy.removeLast());
    }
  }

  @Test
  public void randomizedTest() {
    int N = 5000;
    for (int i = 0; i < N; i += 1) {
      int operationNumber = StdRandom.uniform(0, 4);
      if (operationNumber == 0) {  // addLast
        int randVal = StdRandom.uniform(0, 100);
        simple.addLast(randVal);
        buggy.addLast(randVal);
        // System.out.println("addLast(" + randVal + ")");
      } else if (operationNumber == 1 && simple.size() > 0) {  // getLast
        int lastElem1 = simple.getLast();
        int lastElem2 = buggy.getLast();
        assertEquals(lastElem1, lastElem2);
      } else if (operationNumber == 2 && simple.size() > 0) {  // removeLast
        int lastElem1 = simple.removeLast();
        int lastElem2 = buggy.removeLast();
        assertEquals(lastElem1, lastElem2);
      } else if (operationNumber == 3) {  // size
        int size1 = simple.size();
        int size2 = buggy.size();
        assertEquals(size1, size2);
      }
    }
  }
}
