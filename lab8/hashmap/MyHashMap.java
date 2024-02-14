package hashmap;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author Yutong Wang
 */
public class MyHashMap<K, V> implements Map61B<K, V> {
    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /** Instance Variables */
    private Collection<Node>[] buckets;  // array of collection objects
    private int nodeSize;
    private int bucketSize;
    private double loadFactor;
    /** Default values */
    private static final int defaultInitSize = 16;
    private static final double defaultMaxLoad = 0.75;

    /** Constructors */
    public MyHashMap() {
        this(defaultInitSize, defaultMaxLoad);
    }

    public MyHashMap(int initialSize) {
        this(initialSize, defaultMaxLoad);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        this.buckets = createTable(initialSize);
        this.nodeSize = 0;
        this.bucketSize = initialSize;
        this.loadFactor = maxLoad;
        for (int i = 0; i < bucketSize; i++) {
            this.buckets[i] = createBucket();
        }
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("Map key cannot be null.");
        }
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        return new Collection[tableSize];
    }

    /**
     * Returns the index of a given key by reducing its hash code.
     */
    private int hashIndex(K key) {
        if (key == null) {
            return 0;
        }
        int hashCode = key.hashCode();
        return Math.floorMod(hashCode, bucketSize);
    }

    // Implement the methods of the Map61B Interface below

    /**
     * Clear the HaspMap by setting its buckets to null.
     */
    @Override
    public void clear() {
        for (int i = 0; i < bucketSize; i++) {
            buckets[i] = null;
        }
        this.buckets = null;
        this.nodeSize = 0;
    }

    /**
     * Check if the HashMap contains a given key.
     * @return a boolean value.
     */
    @Override
    public boolean containsKey(K key) {
        return get(key) != null;
    }

    /**
     * Get the value associated with a given key.
     * @return null if the key doesn't exist.
     */
    @Override
    public V get(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Map key cannot be null.");
        }
        int index = hashIndex(key);
        if (buckets != null && buckets[index] != null) {
            for (Node n : buckets[index]) {
                if (n.key.equals(key)) {
                    return n.value;
                }
            }
        }
        return null;
    }

    /**
     * Returns the number of nodes in the HashMap.
     */
    @Override
    public int size() {
        return this.nodeSize;
    }

    private int bucketSize() {
        return this.bucketSize;
    }

    private double currentLoad() {
        return (double) nodeSize / (double) bucketSize;
    }

    /**
     * Check if the number of nodes exceeds the max load factor.
     */
    private boolean exceedLoad() {
        return currentLoad() > loadFactor;
    }

    /**
     * Insert a key-value pair into the HashMap.
     * If the key already exists, update its value.
     */
    @Override
    public void put(K key, V value) {
        if (exceedLoad()) {
            this.resize(bucketSize * 2);
        }
        int index = hashIndex(key);
        for (Node n : buckets[index]) {
            if (n.key.equals(key)) {
                n.value = value;
                return;
            }
        }
        Node pair = createNode(key, value);
        buckets[index].add(pair);
        this.nodeSize += 1;
    }

    /**
     * Resize the HashMap when load factor is exceeded.
     * Copies all nodes from the original map.
     * @param size the bucket size of the new HashMap.
     */
    private void resize(int size) {
        MyHashMap<K, V> newMap = new MyHashMap<>(size, loadFactor);
        int prevSize = this.bucketSize;
        for (int i = 0; i < prevSize; i++) {
            for (Node n : buckets[i]) {
                int index = newMap.hashIndex(n.key);
                newMap.buckets[index].add(n);
            }
        }
        this.buckets = newMap.buckets;
        this.bucketSize = size;
    }

    /**
     * Returns a HashSet of keys in the HashMap.
     */
    @Override
    public Set<K> keySet() {
        Set<K> setOfKeys = new HashSet<>();
        for (int i = 0; i < bucketSize; i++) {
            if (buckets[i] == null) {
                continue;
            }
            for (Node n : buckets[i]) {
                setOfKeys.add(n.key);
            }
        }
        return setOfKeys;
    }

    @Override
    public V remove(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Map key cannot be null.");
        }
        if (bucketSize > defaultInitSize && currentLoad() < loadFactor / 4.0) {
            resize(bucketSize / 2);
        }
        int index = hashIndex(key);
        if (buckets != null && buckets[index] != null) {
            for (Node n : buckets[index]) {
                if (n.key.equals(key)) {
                    V value = n.value;
                    buckets[index].remove(n);
                    nodeSize -= 1;
                    return value;
                }
            }
        }
        return null;
    }

    @Override
    public V remove(K key, V value) {
        V valueInMap = get(key);
        if (value.equals(valueInMap)) {
            return remove(key);
        }
        return null;
    }

    /**
     * Supports iteration over the HashMap using a protected Iterator class.
     */
    public Iterator<K> iterator() {
        return new MyHashMapIterator();
    }

    private class MyHashMapIterator implements Iterator<K> {
        K[] keys;
        int position;

        MyHashMapIterator() {
            keys = keySet().toArray((K[]) new Object[nodeSize]);
            position = 0;
        }

        @Override
        public boolean hasNext() {
            return position < nodeSize;
        }

        @Override
        public K next() {
            K key = keys[position];
            position += 1;
            return key;
        }
    }
}
