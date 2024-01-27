package deque;

import java.util.Comparator;

public class MaxArrayDeque<Type> extends ArrayDeque<Type> {
    private final Comparator<Type> comparator;

    /**
     * A new constructor with a Comparator c
     */
    public MaxArrayDeque(Comparator<Type> c) {
        this.comparator = c;
    }

    /**
     * returns the maximum element in the deque as defined by the previous given Comparator.
     */
    public Type max() {
        return max(comparator);
    }

    /**
     * returns the maximum element in the deque as defined by the parameter Comparator c.
     */
    public Type max(Comparator<Type> c) {
        if (this.isEmpty()) {
            return null;
        }
        Type maxItem = this.get(0);
        for (int i = 1; i < size(); i++) {
            if (c.compare(maxItem, this.get(i)) < 0) {
              maxItem = this.get(i);
            }
        }
        return maxItem;
    }
  }
