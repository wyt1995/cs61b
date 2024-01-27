package deque;

import java.util.Iterator;

/**
 * ArrayDeque implements an array based list.
 * @param <T> supports generic reference types.
 */
public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    private final int initSize = 8;
    private T[] items;
    private int size;
    private int arraySize = initSize;
    private int firstIndex = 0;

    /**
     * The constructor of an empty array list.
     */
    public ArrayDeque() {
        this.items = (T[]) new Object[arraySize];
        this.size = 0;
    }

    /**
     * Resize the underlying array by copying the entire list.
     * @param capacity the size allocated to the new list.
     */
    private void resize(int capacity) {
        T[] temp = (T[]) new Object[capacity];
        int idx = firstIndex;
        for (int i = 0; i < size; i++) {
            temp[i] = items[idx];
            idx = plusIndex(idx);
        }
        this.items = temp;
        this.firstIndex = 0;
        this.arraySize = capacity;
    }

    /**
     * The index of the previous element.
     * @param i the current index in the array.
     * @return the previous index.
     */
    private int minusIndex(int i) {
        int newIndex = i - 1;
        if (newIndex < 0) {
            newIndex += arraySize;
        }
        return newIndex;
    }

    /**
     * The index of the next element.
     * @param i the current index in the array.
     * @return the next index.
     */
    private int plusIndex(int i) {
        int newIndex = i + 1;
        if (newIndex >= arraySize) {
            newIndex -= arraySize;
        }
        return newIndex;
    }

    /**
     * Given the index in the list, return the index in the underlying array.
     */
    private int getIndex(int listIndex) {
        int arrayIndex = firstIndex + listIndex;
        if (arrayIndex >= arraySize) {
            arrayIndex -= arraySize;
        }
        return arrayIndex;
    }

    /**
     * @return the array index of the last item of the list.
     */
    private int getLastIndex() {
        return getIndex(this.size - 1);
    }

    /**
     * Add an element to the front of the list.
     * Resize the array twofolds if no enough space.
     */
    @Override
    public void addFirst(T item) {
        if (size == arraySize) {
            this.resize(size * 2);
        }
        firstIndex = minusIndex(firstIndex);
        items[firstIndex] = item;
        size += 1;
    }

    /**
     * Add an element to the back of the list.
     * Resize the array twofolds if no enough space.
     */
    @Override
    public void addLast(T item) {
        if (size == arraySize) {
            this.resize(size * 2);
        }
        int lastIndex = plusIndex(getLastIndex());
        items[lastIndex] = item;
        size += 1;
    }

    /**
     * @return the size of the list.
     */
    @Override
    public int size() {
        return this.size;
    }

    /**
     * Remove and return the first item of the list.
     * Resize the array to a half if the usage factor is less than 25%.
     */
    @Override
    public T removeFirst() {
        if (arraySize > initSize && size < 0.25 * arraySize) {
            resize(arraySize / 2);
        }
        if (size == 0) {
            return null;
        }
        T firstItem = items[firstIndex];
        items[firstIndex] = null;
        this.size -= 1;
        this.firstIndex = plusIndex(firstIndex);
        return firstItem;
    }

    /**
     * Remove and return the last item of the list.
     * Resize the array to a half if the usage factor is less than 25%.
     */
    @Override
    public T removeLast() {
        if (arraySize > initSize && size < 0.25 * arraySize) {
            resize(arraySize / 2);
        }
        if (size == 0) {
            return null;
        }
        int lastIndex = getLastIndex();
        T lastItem = items[lastIndex];
        items[lastIndex] = null;
        this.size -= 1;
        return lastItem;
    }

    /**
     * Return the element in the given index.
     */
    @Override
    public T get(int index) {
        if (index > size) {
            return null;
        }
        int arrayIndex = getIndex(index);
        return items[arrayIndex];
    }

    /**
     * Prints the entire list by calling the toString method.
     */
    @Override
    public void printDeque() {
        StringBuilder printList = new StringBuilder();
        int arrayIndex = firstIndex;
        for (int i = 0; i < size; i++) {
            printList.append(items[arrayIndex]);
            printList.append(" ");
            arrayIndex = plusIndex(arrayIndex);
        }
        String str = printList.toString(); // can also override toString()
        System.out.println(str);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof Deque)) {
            return false;
        }
        Deque<T> other = (Deque<T>) obj;
        if (this.size() != other.size()) {
            return false;
        }
        for (int i = 0; i < size; i++) {
            if (!(this.get(i).equals(other.get(i)))) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return an iterator object.
     */
    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    /**
     * Implements the Iterator interface to support iteration.
     */
    private class ArrayDequeIterator implements Iterator<T> {
        private int position;

        ArrayDequeIterator() {
            position = 0;
        }

        @Override
        public boolean hasNext() {
            return position < size;
        }

        @Override
        public T next() {
            int arrayIndex = getIndex(position);
            position += 1;
            return items[arrayIndex];
        }
    }
}
