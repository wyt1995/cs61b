package deque;

public class ArrayDeque<Type> {
  private Type[] items;
  private int size;
  private final int initSize = 8;
  private int arraySize = initSize;
  private int firstIndex = 0;

  public ArrayDeque() {
    this.items = (Type[]) new Object[arraySize];
    this.size = 0;
  }

  private void resize(int capacity) {
    Type[] temp = (Type[]) new Object[capacity];
    int idx = firstIndex;
    for (int i = 0; i < size; i++) {
      temp[i] = items[idx];
      idx = plusIndex(idx);
    }
    this.items = temp;
    this.firstIndex = 0;
    this.arraySize = capacity;
  }

  private int minusIndex(int i) {
    int newIndex = i - 1;
    if (newIndex < 0) {
      newIndex += arraySize;
    }
    return newIndex;
  }

  private int plusIndex(int i) {
    int newIndex = i + 1;
    if (newIndex >= arraySize) {
      newIndex -= arraySize;
    }
    return newIndex;
  }

  private int getIndex(int listIndex) {
    int arrayIndex = firstIndex + listIndex;
    if (arrayIndex >= arraySize) {
      arrayIndex -= arraySize;
    }
    return arrayIndex;
  }

  private int getLastIndex() {
    return getIndex(this.size - 1);
  }

  public void addFirst(Type item) {
    if (size == arraySize) {
      this.resize(size * 2);
    }
    firstIndex = minusIndex(firstIndex);
    items[firstIndex] = item;
    size += 1;
  }

  public void addLast(Type item) {
    if (size == arraySize) {
      this.resize(size * 2);
    }
    int lastIndex = plusIndex(getLastIndex());
    items[lastIndex] = item;
    size += 1;
  }

  public boolean isEmpty() {
    return items[firstIndex] == null;
  }

  public int size() {
    return this.size;
  }

  public void printDeque() {
    int arrayIndex = firstIndex;
    for (int i = 0; i < size; i++) {
      System.out.print(items[arrayIndex] + " ");
      arrayIndex = plusIndex(arrayIndex);
    }
    System.out.println();
  }

  public Type removeFirst() {
    if (arraySize > initSize && size < 0.25 * arraySize) {
      resize(arraySize / 2);
    }
    Type firstItem = items[firstIndex];
    items[firstIndex] = null;
    this.size -= 1;
    this.firstIndex = plusIndex(firstIndex);
    return firstItem;
  }

  public Type removeLast() {
    if (arraySize > initSize && size < 0.25 * arraySize) {
      resize(arraySize / 2);
    }
    int lastIndex = getLastIndex();
    Type lastItem = items[lastIndex];
    items[lastIndex] = null;
    this.size -= 1;
    return lastItem;
  }

  public Type get(int index) {
    if (index > size) {
      return null;
    }
    int arrayIndex = getIndex(index);
    return items[arrayIndex];
  }


  public static void main(String[] args) {
    ArrayDeque<Integer> alist = new ArrayDeque<>();
    for (int i = 0; i < 21; i++) {
      alist.addFirst(i);
      alist.addLast(i);
    }
    alist.printDeque();
    System.out.println("Array size after adding: " + alist.arraySize);

    for (int i = 0; i < 18; i++) {
      alist.removeLast();
      alist.removeFirst();
    }
    alist.printDeque();
    System.out.println("Array size after removal: " + alist.arraySize);
  }
}
