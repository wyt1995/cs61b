package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private final Comparator<T> comparator;

    /**
     * A new constructor with a Comparator c
     */
    public MaxArrayDeque(Comparator<T> c) {
        this.comparator = c;
    }

    /**
     * returns the maximum element in the deque as defined by the previous given Comparator.
     */
    public T max() {
        return max(comparator);
    }

    /**
     * returns the maximum element in the deque as defined by the parameter Comparator c.
     */
    public T max(Comparator<T> c) {
        if (this.isEmpty()) {
            return null;
        }
        T maxItem = this.get(0);
        for (int i = 1; i < size(); i++) {
            if (c.compare(maxItem, this.get(i)) < 0) {
                maxItem = this.get(i);
            }
        }
        return maxItem;
    }
}
