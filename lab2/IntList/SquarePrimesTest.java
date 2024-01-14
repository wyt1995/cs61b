package IntList;

import static org.junit.Assert.*;
import org.junit.Test;

public class SquarePrimesTest {

    /**
     * Here is a test for isPrime method. Try running it.
     * It passes, but the starter code implementation of isPrime
     * is broken. Write your own JUnit Test to try to uncover the bug!
     */
    @Test
    public void testSquarePrimesSimple() {
        IntList lst = IntList.of(14, 15, 16, 17, 18);
        boolean changed = IntListExercises.squarePrimes(lst);
        assertEquals("14 -> 15 -> 16 -> 289 -> 18", lst.toString());
        assertTrue(changed);

        IntList lst2 = IntList.of(1, 2, 3, 4, 5);
        boolean changed2 = IntListExercises.squarePrimes(lst2);
        assertEquals("1 -> 4 -> 9 -> 4 -> 25", lst2.toString());
        assertTrue(changed2);

        IntList lst3 = IntList.of(14, 15, 16, 18, 20);
        boolean changed3 = IntListExercises.squarePrimes(lst3);
        assertEquals("14 -> 15 -> 16 -> 18 -> 20", lst3.toString());
        assertFalse(changed3);

        IntList lst4 = IntList.of(11, 13, 17, 19, 23);
        boolean changed4 = IntListExercises.squarePrimes(lst4);
        assertEquals("121 -> 169 -> 289 -> 361 -> 529", lst4.toString());
        assertTrue(changed2);
    }
}
