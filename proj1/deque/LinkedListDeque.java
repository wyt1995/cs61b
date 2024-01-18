package deque;

public class LinkedListDeque<Type> {
  /**
   * The node is a helper class for the doubly linked list
   * It has an internal recursive structure
   */
  private class Node {
    private Type item;
    private Node prev;
    private Node next;
    private Node(Type item, Node prev, Node next) {
      this.item = item;
      this.prev = prev;
      this.next = next;
    }
  }

  private Node sentinel;
  private int size;

  /**
   * The constructor for the linked list with the first item provided
   * @param first: the first element of the linked list
   */
  public LinkedListDeque(Type first) {
    this.sentinel = new Node(null, null, null);
    Node firstItem = new Node(first, this.sentinel, this.sentinel);
    this.sentinel.prev = firstItem;
    this.sentinel.next = firstItem;
    this.size = 1;
  }

  /**
   * The constructor for the empty linked list,
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
  public void addFirst(Type item) {
    Node newItem = new Node(item, sentinel, sentinel.next);
    this.sentinel.next.prev = newItem;
    this.sentinel.next = newItem;
    this.size += 1;
  }

  /**
   * add an item to the back of the list instance.
   * @param item: the element to be added to the list.
   */
  public void addLast(Type item) {
    Node newItem = new Node(item, sentinel.prev, sentinel);
    this.sentinel.prev.next = newItem;
    this.sentinel.prev = newItem;
    this.size += 1;
  }

  /**
   * @return true if deque is empty, false otherwise.
   */
  public boolean isEmpty() {
    return sentinel.next == sentinel;
  }

  /**
   * @return the size of this linked list instance.
   */
  public int size() {
    return this.size;
  }

  /**
   * Prints the items in the deque from first to last, separated by a space.
   */
  public void printDeque() {
    Node link = this.sentinel.next;
    while (link != sentinel) {
      System.out.print(link.item + " ");
      link = link.next;
    }
    System.out.println();
  }

  /**
   * Removes and returns the item at the front of the deque.
   * If no such item exists, returns null.
   */
  public Type removeFirst() {
    if (this.size == 0) {
      return null;
    }
    Type elem = this.sentinel.next.item;
    this.sentinel.next.next.prev = this.sentinel;
    this.sentinel.next = this.sentinel.next.next;
    return elem;
  }

  /**
   * Removes and returns the item at the back of the deque.
   * If no such item exists, returns null.
   */
  public Type removeLast() {
    if (this.size == 0) {
      return null;
    }
    Type elem = this.sentinel.prev.item;
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
  public Type get(int index) {
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
  public Type getRecursive(int index) {
    if (index >= this.size) {
      return null;
    }
    return getHelper(index, this.sentinel.next);
  }

  private Type getHelper(int idx, Node link) {
    if (idx == 0) {
      return link.item;
    }
    return getHelper(idx-1, link.next);
  }
}
