package tester;

import static org.junit.Assert.*;
import org.junit.Test;

import edu.princeton.cs.algs4.StdRandom;
import student.StudentArrayDeque;

public class TestArrayDequeEC {

    @Test
    public void randomizedTest() {
        StudentArrayDeque<Integer> student = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> expected = new ArrayDequeSolution<>();

        int total = 500;
        for (int i = 0; i < total; i++) {
            StringBuilder errorMsg = new StringBuilder();
            errorMsg.append("\n");

            for (int j = 0; j < 10; j++) {
                int randomOperation = StdRandom.uniform(0, 6);
                if (randomOperation < 2) {
                    int randomNumber = StdRandom.uniform(0, 1000);
                    student.addFirst(randomNumber);
                    expected.addFirst(randomNumber);
                    errorMsg.append(String.format("addFirst(%s)\n", randomNumber));
                    assertEquals(errorMsg.toString(), expected.size(), student.size());
                } else if (randomOperation < 4) {
                    int randomNumber = StdRandom.uniform(0, 1000);
                    student.addLast(randomNumber);
                    expected.addLast(randomNumber);
                    errorMsg.append(String.format("addLast(%s)\n", randomNumber));
                    assertEquals(errorMsg.toString(), expected.size(), student.size());
                } else if (randomOperation == 4 && !(student.isEmpty()) && !(expected.isEmpty())) {
                    Integer studentItem = student.removeFirst();
                    Integer expectedItem = expected.removeFirst();
                    errorMsg.append("removeFirst()\n");
                    assertEquals(errorMsg.toString(), expectedItem, studentItem);
                } else if (randomOperation == 5 && !(student.isEmpty()) && !(expected.isEmpty())) {
                    Integer studentItem = student.removeLast();
                    Integer expectedItem = expected.removeLast();
                    errorMsg.append("removeLast()\n");
                    assertEquals(errorMsg.toString(), expectedItem, studentItem);
                }
            }
        }
    }
}
