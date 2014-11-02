package hama;

/**
 *
 * @author janaka
 */
public interface Partitioner<K, V> {

    public int getPartition(K key, V value, int numTasks);
}
