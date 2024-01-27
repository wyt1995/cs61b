package deque;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;

public class ArrayDequeTest {
  ArrayDeque<Integer> alist = new ArrayDeque<>();

  @Before
  public void setUp() {
    alist.addFirst(1);
    alist.addLast(2);
    alist.addLast(3);
    alist.addLast(4);
  }

  @Test
  public void testSimpleList() {
    alist.addFirst(0);
    alist.addLast(5);
    alist.addLast(6);
    assertEquals(7, alist.size());
    assertEquals((Integer) 0, alist.get(0));
    assertEquals((Integer) 3, alist.get(3));

    alist.removeFirst();
    alist.removeLast();
    assertEquals(5, alist.size());
    assertFalse(alist.isEmpty());
  }

  @Test
  public void testEquals() {
    ArrayDeque<Integer> other = new ArrayDeque<>();
    other.addFirst(1);
    other.addLast(2);
    other.addLast(3);
    other.addLast(4);

    assertFalse(alist == other);
    assertTrue(alist.equals(other));

    other.removeLast();
    assertFalse(alist.equals(other));
  }

  @Test
  public void testIterator() {
    int i = 1;
    for (Integer element : alist) {
      assertEquals((Integer) i, element);
      i += 1;
    }
  }

  @Test
  public void testResize() {
    alist.addFirst(0);
    for (int i = 5; i < 32; i++) {
      alist.addLast(i);
    }
    assertEquals(32, alist.size());
    for (int i = 0; i < 32; i++) {
      assertEquals((Integer) i, alist.get(i));
    }

    for (int i = 0; i < 30; i++) {
      alist.removeLast();
    }
    assertEquals(2, alist.size());
    for (int i = 0; i < 2; i++) {
      assertEquals((Integer) i, alist.get(i));
    }
  }
}
