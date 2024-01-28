package tester;

import static org.junit.Assert.*;
import org.junit.Test;

import edu.princeton.cs.algs4.StdRandom;
import student.StudentArrayDeque;

public class TestArrayDequeEC {
    StudentArrayDeque<Integer> student = new StudentArrayDeque<>();
    ArrayDequeSolution<Integer> expected = new ArrayDequeSolution<>();

    @Test
    public void randomizedTest() {
        int total = 100;
        for (int i = 0; i < total; i++) {
            double randomOperation = StdRandom.uniform();
            StringBuilder errorMsg = new StringBuilder();
            errorMsg.append("\n");

            int randomTests = StdRandom.uniform(0, 20);
            for (int j = 1; j < randomTests; j++) {
                int randomNumber = StdRandom.uniform(0, 1000);
                student.addFirst(randomNumber);
                expected.addFirst(randomNumber);
                errorMsg.append(String.format("addFirst(%s)\n", randomNumber));

                randomNumber = StdRandom.uniform(0, 1000);
                student.addLast(randomNumber);
                expected.addLast(randomNumber);
                errorMsg.append(String.format("addLast(%s)\n", randomNumber));
            }
            assertEquals(errorMsg.toString(), expected.size(), student.size());

            for (int k = 1; k < randomTests; k++) {
                if (!(student.isEmpty()) && !(expected.isEmpty())) {
                    Integer studentItem;
                    Integer expectedItem;
                    if (randomOperation < 0.5) {
                        studentItem = student.removeFirst();
                        expectedItem = expected.removeFirst();
                        errorMsg.append(String.format("removeFirst(): %s\n", expectedItem));
                    } else {
                        studentItem = student.removeLast();
                        expectedItem = expected.removeLast();
                        errorMsg.append(String.format("removeLast(): %s\n", expectedItem));
                    }
                    assertEquals(errorMsg.toString(), expectedItem, studentItem);
                }
            }
        }
    }
}
