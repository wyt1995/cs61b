package deque;

public interface Deque<Type> {
  void addFirst(Type item);

  void addLast(Type item);

  default boolean isEmpty() {
    return this.size() == 0;
  }

  int size();

  Type removeFirst();

  Type removeLast();

  Type get(int index);

  void printDeque();
}
