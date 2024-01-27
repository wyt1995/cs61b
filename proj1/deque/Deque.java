package deque;

public interface Deque<T> {
    void addFirst(T item);

    void addLast(T item);

    default boolean isEmpty() {
        return this.size() == 0;
    }

    int size();

    T removeFirst();

    T removeLast();

    T get(int index);

    void printDeque();
}
