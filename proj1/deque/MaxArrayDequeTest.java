package deque;

import java.util.Comparator;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class MaxArrayDequeTest {
  public static class StringComparator implements Comparator<String> {
    @Override
    public int compare(String a, String b) {
      if (a.compareTo(b) < 0) {
        return -1;
      } else if (a.compareTo(b) > 0) {
        return 1;
      }
      return 0;
    }
  }

  public static Comparator<String> comparator() {
    return new StringComparator();
  }


  public static class ReverseComparator implements Comparator<String> {
    @Override
    public int compare(String a, String b) {
      if (a.compareTo(b) < 0) {
        return 1;
      } else if (a.compareTo(b) > 0) {
        return -1;
      }
      return 0;
    }
  }

  public static Comparator<String> reverseComparator() {
    return new ReverseComparator();
  }


  private Comparator<String> stringComparator = comparator();
  private Comparator<String> reverseComparator = reverseComparator();
  private MaxArrayDeque<String> testDeque = new MaxArrayDeque<>(stringComparator);


  @Before
  public void setUp() {
    testDeque.addLast("Computer");
    testDeque.addLast("Science");
    testDeque.addLast("Data");
    testDeque.addLast("Structure");
  }

  @Test
  public void testMaxArrayDeque() {
    assertEquals(4, testDeque.size());
    assertEquals("Structure", testDeque.max());
    testDeque.removeLast();
    assertEquals(3, testDeque.size());
    assertEquals("Science", testDeque.max());
  }

  @Test
  public void testComparator() {
    assertEquals("Computer", testDeque.max(reverseComparator));
    testDeque.removeFirst();
    assertEquals("Data", testDeque.max(reverseComparator));
    testDeque.removeFirst();
    testDeque.removeFirst();
    testDeque.removeFirst();
    assertNull(testDeque.max());
  }
}
