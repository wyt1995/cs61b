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
        int total = 500;
        StringBuilder errorMsg = new StringBuilder();
        errorMsg.append("Incorrect result after calling \n");
        for (int i = 0; i < total; i++) {
            int randomOperation = StdRandom.uniform(0, 4);
            if (randomOperation == 0) {
                int randomNumber = StdRandom.uniform(0, 1000);
                student.addFirst(randomNumber);
                expected.addFirst(randomNumber);
                errorMsg.append(String.format("addFirst(%s)\n", randomNumber));
                assertEquals(errorMsg.toString(), expected.size(), student.size());
            } else if (randomOperation == 1) {
                int randomNumber = StdRandom.uniform(0, 1000);
                student.addLast(randomNumber);
                expected.addLast(randomNumber);
                errorMsg.append(String.format("addLast(%s)\n", randomNumber));
                assertEquals(errorMsg.toString(), expected.size(), student.size());
            } else if (randomOperation == 2 && !(student.isEmpty()) && !(expected.isEmpty())) {
                Integer studentItem = student.removeFirst();
                Integer expectedItem = expected.removeFirst();
                errorMsg.append(String.format("removeFirst(): %s\n", expectedItem));
                assertEquals(errorMsg.toString(), expectedItem, studentItem);
            } else if (randomOperation == 3 && !(student.isEmpty()) && !(expected.isEmpty())) {
                Integer studentItem = student.removeLast();
                Integer expectedItem = expected.removeLast();
                errorMsg.append(String.format("removeLast(): %s\n", expectedItem));
                assertEquals(errorMsg.toString(), expectedItem, studentItem);
            }
        }
    }
}
