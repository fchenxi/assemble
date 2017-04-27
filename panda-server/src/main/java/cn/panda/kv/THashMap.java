package cn.panda.kv;

import java.io.Serializable;
import java.util.*;

public class THashMap<K, V> extends AbstractMap<K,V>
        implements Map<K,V>, Cloneable, Serializable {

    int threshold;
    /**
     * An empty table instance to share when the table is not inflated.
     */
    static final Entry<?,?>[] EMPTY_TABLE = {};

    /**
     * The table, resized as necessary. Length MUST Always be a power of two.
     */
    transient Entry<K,V>[] table = (Entry<K,V>[]) EMPTY_TABLE;

    // TODO what is means?
    /**
     * The number of key-value mappings contained in this map.
     */
    transient int size;
    // TODO what is means?
    /**
     * The number of times this HashMap has been structurally modified
     * Structural modifications are those that change the number of mappings in
     * the HashMap or otherwise modify its internal structure (e.g.,
     * rehash).  This field is used to make iterators on Collection-views of
     * the HashMap fail-fast.  (See ConcurrentModificationException).
     */
    transient int modCount;
    // TODO why need hash seed?
    /**
     * A randomizing value associated with this instance that is applied to
     * hash code of keys to make hash collisions harder to find. If 0 then
     * alternative hashing is disabled.
     */
    transient int hashSeed = 0;

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return null;
    }
    final int hash(Object k) {
        int h = hashSeed;
        if (0 != h && k instanceof String) {
            return sun.misc.Hashing.stringHash32((String) k);
        }

        h ^= k.hashCode();

        // This function ensures that hashCodes that differ only by
        // constant multiples at each bit position have a bounded
        // number of collisions (approximately 8 at default load factor).
        h ^= (h >>> 20) ^ (h >>> 12);
        return h ^ (h >>> 7) ^ (h >>> 4);
    }
    /**
     * Returns index for hash code h.
     */
    static int indexFor(int h, int length) {
        // assert Integer.bitCount(length) == 1 : "length must be a non-zero power of 2";
        return h & (length-1);
    }
    final Entry<K, V> removeEntryForKey(Object key) {
        if (size == 0) {
            return null;
        }
        int hash = (key == null) ? 0 : hash(key);
        int i = indexFor(hash, table.length);
        Entry<K, V> prev = table[i];
        Entry<K ,V> e = prev;
        while (e != null) {
            Entry<K ,V> next = e.next;
            Object k;
            if (e.hash == hash &&
                    ((k = e.key) == key || (key != null && key.equals(k)))) {
                modCount++;
                size--;
                if (prev == e) {
                    table[i] = next;
                } else {
                    prev.next = next;
                }
                e.recordAccess(this);
                return e;
            }
            prev = e;
            e = next;
        }
        return e;
    }
    static class Entry<K, V> implements Map.Entry<K, V> {
        final K key;
        V value;
        Entry<K, V> next;
        int hash;
        // Create new entry
        Entry(int h, K k, V v, Entry<K,V> n) {
            value = v;
            next = n;
            key = k;
            hash = h;
        }
        public final K getKey() {
            return key;
        }
        public final V getValue() {
            return value;
        }
        public final V setValue(V newValue) {
            V oldValue = value;
            value = newValue;
            return oldValue;
        }
        public final boolean equals(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            Object sourceKey = getKey();
            Object targetKey = e.getKey();
            if (sourceKey == targetKey ||
                    (sourceKey != null && sourceKey.equals(targetKey))) {
                Object sourceValue = getValue();
                Object targetValue = e.getValue();
                if (sourceValue == targetValue ||
                        (sourceValue != null && sourceValue.equals(targetValue))) {
                    return true;
                }
            }
            return false;
        }
        public final int hashCode() {
            return Objects.hashCode(getKey()) ^ Objects.hashCode(getValue());
        }
        public final String toString() {
            return getKey() + "=" + getValue();
        }
        // This method is invoked when the value in an entry (exists) is overwrite
        void recordAccess(THashMap<K, V> m) {
        }
        // This method is invoked when the entry is removed from the table.
        // Be careful, is the entry key (head index) removed from table.
        void recordRemoval(THashMap<K, V> m) {
        }
    }
    void addEntry(int hash, K key, V value, int bucketIndex) {
        if ((size >= threshold) && (null != table[bucketIndex])) {
            // TODO resize
        }
        createEntry(hash, key, value, bucketIndex);
    }
    void createEntry(int hash, K key, V value, int bucketIndex) {
        Entry<K, V> e = table[bucketIndex];
        table[bucketIndex] = new Entry<>(hash, key, value, e);
        size++;
    }
    private abstract class HashIterator<E> implements Iterator<E> {
        Entry<K, V> next;           // next entry to return
        int expectedModCount;       // For fast fail
        int index;                  // current slot
        Entry<K, V> current;        // current entry
        HashIterator() {
            expectedModCount = modCount;
            if (size > 0) {
                Entry[] t = table;
                while (index < t.length && (next = t[index++]) == null)
                    ;
            }
        }
        public final boolean hasNext() {
            return next != null;
        }
        final Entry<K, V> nextEntry() {
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
            Entry<K, V> e = next;
            if (e == null) {
                throw new NoSuchElementException();
            }
            if ((next = e.next)  == null) {
                Entry[] t = table;
                while (index < t.length && (next = t[index++]) == null)
                    ;
            }
            current = e;
            return e;
        }
        public void remove() {
            if (current == null)
                throw new IllegalArgumentException();
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
            Object k = current.key;
            current = null;
            // do remove entry
            THashMap.this.remove(k);
            expectedModCount = modCount;
        }
    }
    private final class ValueIterator extends HashIterator<V> {
        public V next() {
            return nextEntry().value;
        }
    }
    private final class KeyIterator extends HashIterator<K> {
        public K next() {
            return nextEntry().getKey();
        }
    }

    private final class EntryIterator extends HashIterator<Map.Entry<K,V>> {
        public Map.Entry<K,V> next() {
            return nextEntry();
        }
    }

    // what do themes do?
    Iterator<K> newKeyIterator()   {
        return new KeyIterator();
    }
    Iterator<V> newValueIterator()   {
        return new ValueIterator();
    }
    Iterator<Map.Entry<K,V>> newEntryIterator()   {
        return new EntryIterator();
    }


    // Does the entrySet is the view?
    private transient Set<Map.Entry<K,V>> entrySet = null;

    // fuck, the keySet is package in AbstractMap.
//    public Set<K> keySet() {
//        Set<K> ks = keySet;
//        return (ks != null ? ks : (keySet = new KeySet()));
//    }
    private final class KeySet extends AbstractSet<K> {
        @Override
        public Iterator<K> iterator() {
            return newKeyIterator();
        }
        @Override
        public int size() {
            return size;
        }
        public boolean contains(Object o) {
            return contains(o);
        }
        public boolean remove(Object o) {
            return THashMap.this.removeEntryForKey(o) != null;
        }
        public void clear() {
            THashMap.this.clear();
        }
    }
}
