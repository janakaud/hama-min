package hama;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author janaka
 */
public interface Partitioner<K, V> {

    public int getPartition(K key, V value, int numTasks);
}
