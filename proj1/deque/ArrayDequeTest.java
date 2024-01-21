package deque;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

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
    assertEquals(0, (Object) alist.get(0));
    assertEquals(3, (Object) alist.get(3));

    alist.removeFirst();
    alist.removeLast();
    assertEquals(5, alist.size());
    assertFalse(alist.isEmpty());
  }

  @Test
  public void testPrintDeque() {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    PrintStream printed = new PrintStream(output);
    System.setOut(printed);
    alist.printDeque();
    assertEquals("1 2 3 4 \n", output.toString());
  }

  @Test
  public void testResize() {
    alist.addFirst(0);
    for (int i = 5; i < 32; i++) {
      alist.addLast(i);
    }
    assertEquals(32, alist.size());
    for (int i = 0; i < 32; i++) {
      assertEquals(i, (Object) alist.get(i));
    }

    for (int i = 0; i < 30; i++) {
      alist.removeLast();
    }
    assertEquals(2, alist.size());
    for (int i = 0; i < 2; i++) {
      assertEquals(i, (Object) alist.get(i));
    }
  }
}
