package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {
    private Node sentinel;
    private int size;

    /**
     * The Node is a helper class for the doubly linked list.
     * It has an internal recursive structure.
     */
    private class Node {
        private T item;
        private Node prev;
        private Node next;

        private Node(T item, Node prev, Node next) {
            this.item = item;
            this.prev = prev;
            this.next = next;
        }
    }

    /**
     * The constructor for an empty linked list,
     * in which sentinel.prev and sentinel.next both point to the sentinel node itself.
     */
    public LinkedListDeque() {
        this.sentinel = new Node(null, null, null);
        this.sentinel.prev = this.sentinel;
        this.sentinel.next = this.sentinel;
        this.size = 0;
    }

    /**
     * add an item to the front of the list instance.
     * @param item: the element to be added to the list.
     */
    @Override
    public void addFirst(T item) {
        Node newItem = new Node(item, sentinel, sentinel.next);
        this.sentinel.next.prev = newItem;
        this.sentinel.next = newItem;
        this.size += 1;
    }

    /**
     * add an item to the back of the list instance.
     * @param item: the element to be added to the list.
     */
    @Override
    public void addLast(T item) {
        Node newItem = new Node(item, sentinel.prev, sentinel);
        this.sentinel.prev.next = newItem;
        this.sentinel.prev = newItem;
        this.size += 1;
    }

    /**
     * @return the size of this linked list instance.
     */
    @Override
    public int size() {
        return this.size;
    }

    /**
     * Removes and returns the item at the front of the deque.
     * If no such item exists, returns null.
     */
    @Override
    public T removeFirst() {
        if (this.size == 0) {
            return null;
        }
        T elem = this.sentinel.next.item;
        this.sentinel.next.next.prev = this.sentinel;
        this.sentinel.next = this.sentinel.next.next;
        this.size -= 1;
        return elem;
    }

    /**
     * Removes and returns the item at the back of the deque.
     * If no such item exists, returns null.
     */
    @Override
    public T removeLast() {
        if (this.size == 0) {
            return null;
        }
        T elem = this.sentinel.prev.item;
        this.sentinel.prev.prev.next = sentinel;
        this.sentinel.prev = this.sentinel.prev.prev;
        this.size -= 1;
        return elem;
    }

    /**
     * Gets the item at the given index.
     * If no such item exists, returns null.
     * @param index: an index integer
     */
    @Override
    public T get(int index) {
        if (index >= this.size) {
            return null;
        }
        Node link = this.sentinel;
        while (index >= 0) {
            link = link.next;
            index--;
        }
        return link.item;
    }

    /**
     * The recursive version of the get method.
     */
    public T getRecursive(int index) {
        if (index >= this.size) {
            return null;
        }
        return getHelper(index, this.sentinel.next);
    }

    private T getHelper(int idx, Node link) {
        if (idx == 0) {
            return link.item;
        }
        return getHelper(idx - 1, link.next);
    }

    /**
     * Prints the items in the deque from first to last, separated by a space.
     */
    @Override
    public void printDeque() {
        StringBuilder printList = new StringBuilder();
        Node link = this.sentinel.next;
        while (link != sentinel) {
            printList.append(link.item);
            printList.append(" ");
            link = link.next;
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
        return new LinkedListIterator();
    }

    /**
     * Implements the Iterator interface to support iteration.
     */
    private class LinkedListIterator implements Iterator<T> {
        private int position;

        LinkedListIterator() {
            position = 0;
        }

        @Override
        public boolean hasNext() {
            return position < size;
        }

        @Override
        public T next() {
            T nextItem = get(position);
            position += 1;
            return nextItem;
        }
    }
}
