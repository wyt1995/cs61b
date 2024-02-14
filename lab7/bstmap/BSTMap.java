package bstmap;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {
    private BSTNode node;
    private int size;

    /**
     * A BSTNode represents a vertex in a binary search tree.
     * Each node contains a key/value pair.
     */
    private class BSTNode {
        private K key;
        private V value;
        private BSTNode left;
        private BSTNode right;

        public BSTNode(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    /**
     * A constructor for BSTMap with no node.
     */
    public BSTMap() {
        this.node = null;
        this.size = 0;
    }

    /**
     * Clear the BSTMap by setting its node pointer to null.
     */
    @Override
    public void clear() {
        this.node = null;
        this.size = 0;
    }

    /**
     * Check if the BSTMap contains a given key.
     */
    @Override
    public boolean containsKey(K key) {
        return containsHelper(key, this.node);
    }

    /**
     * A recursive helper function for the contains method.
     */
    private boolean containsHelper(K key, BSTNode root) {
        if (root == null) {
            return false;
        }
        int cmp = key.compareTo(root.key);
        if (cmp == 0) {
            return true;
        } else if (cmp < 0) {
            return containsHelper(key, root.left);
        } else {
            return containsHelper(key, root.right);
        }
    }

    @Override
    public V get(K key) {
        if (key == null) {
            return null;
        }
        return getHelper(key, this.node);
    }

    /**
     * A recursive helper function for the get method.
     */
    private V getHelper(K key, BSTNode root) {
        if (root == null) {
            return null;
        }
        int cmp = key.compareTo(root.key);
        if (cmp == 0) {
            return root.value;
        } else if (cmp < 0) {
            return getHelper(key, root.left);
        } else {
            return getHelper(key, root.right);
        }
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public void put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("Null pointer exception.");
        }
        this.node = insert(key, value, this.node);
        this.size += 1;
    }

    /**
     * A recursive helper function for the put method.
     */
    private BSTNode insert(K key, V value, BSTNode root) {
        if (root == null) {
            return new BSTNode(key, value);
        }
        int cmp = key.compareTo(root.key);
        if (cmp < 0) {
            root.left = insert(key, value, root.left);
        } else if (cmp > 0) {
            root.right = insert(key, value, root.right);
        } else {
            root.value = value;
        }
        return root;
    }

    @Override
    public Set<K> keySet() {
        Set<K> setOfKeys = new TreeSet<>();
        addToSet(this.node, setOfKeys);
        return setOfKeys;
    }

    private void addToSet(BSTNode root, Set<K> keySet) {
        if (root == null) {
            return;
        }
        keySet.add(root.key);
        addToSet(root.left, keySet);
        addToSet(root.right, keySet);
    }

    /**
     * Remove a key-value pair from the BSTMap.
     * @param key the key of the pair.
     * @return the value of the given key.
     */
    @Override
    public V remove(K key) {
        V actualValue = get(key);
        if (actualValue == null) {
            return null;
        }
        this.node = removeHelper(key, this.node);
        this.size -= 1;
        return actualValue;
    }

    /**
     * Remove a key-value pair from the BSTMap.
     * @return null if key doesn't exist or key-value doesn't match.
     */
    @Override
    public V remove(K key, V value) {
        V actualValue = get(key);
        if (actualValue == null || actualValue != value) {
            return null;
        }
        return remove(key);
    }

    /**
     * Remove key from the BSTMap.
     * Assume the key is in the tree.
     */
    private BSTNode removeHelper(K key, BSTNode root) {
        int cmp = key.compareTo(root.key);
        if (cmp < 0) {
            root.left = removeHelper(key, root.left);
        } else if (cmp > 0) {
            root.right = removeHelper(key, root.right);
        } else {
            if (root.left == null) {
                return root.right;
            } else if (root.right == null) {
                return root.left;
            } else {
                BSTNode maxKey = maxKey(root.left);
                maxKey.left = removeHelper(maxKey.key, root.left);
                maxKey.right = root.right;
                root = maxKey;
            }
        }
        return root;
    }

    /**
     * Return the maximum (right-most) key in a BSTMap.
     * This right-most key must have no child on the right.
     */
    private BSTNode maxKey(BSTNode root) {
        if (root.right == null) {
            return root;
        }
        return maxKey(root.right);
    }

    /**
     * Return the minimum (left-most) key in a BSTMap.
     */
    private BSTNode minKey(BSTNode root) {
        if (root.left == null) {
            return root;
        }
        return maxKey(root.left);
    }

    /**
     * Supports iteration over the keys of the BSTMap.
     */
    @Override
    public Iterator<K> iterator() {
        return keySet().iterator();
    }

    public void printInOrder() {
        printHelper(this.node);
    }

    private void printHelper(BSTNode root) {
        if (root == null) {
            return;
        }
        printHelper(root.left);
        System.out.print(minKey(root).key + " ");
        printHelper(root.right);
        System.out.println();
    }
}
