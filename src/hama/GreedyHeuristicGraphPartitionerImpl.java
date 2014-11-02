package hama;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

//import org.apache.commons.collections.CollectionUtils;
/**
 * GreedyHeuristicGraphPartitioner is partitioning by the (Weighted)
 * Deterministic Greedy algorithm.
 *
 * @param <W> Vertex ID object type
 * @param <E> Edge cost object type
 * @param <M> Vertex value object type
 * @param <K> Generally W (Map key type for vertex to partition mapping)
 * @param <V> Generally Integer (Map value type for vertex to partition mapping)
 * 
*/
public class GreedyHeuristicGraphPartitionerImpl<W extends WritableComparable, 
        E extends Writable, M extends Writable, K, V>
        implements Partitioner<K, V> {

    private final int totalPartitions;
    // Map beetween partionID to VertextID
    // Edge Count of each partion
    private Map<K, V> vertexPartitionMap;
    private final Partition<W, E, M>[] partitions;  // may be priority queue??

    public GreedyHeuristicGraphPartitionerImpl(int totalPartitions, 
            int partitionCapacity) {
        this.totalPartitions = totalPartitions;
        this.partitions = new Partition[totalPartitions];
        for(int i = 0; i < totalPartitions; i++)
            this.partitions[i] = new Partition(i, partitionCapacity);
        this.vertexPartitionMap = new HashMap<>();
    }

    public Map<K, V> getVertexPartitionMap() {
        return vertexPartitionMap;
    }

    public void setVertexPartitionMap(Map<K, V> vertexPartitionMap) {
        this.vertexPartitionMap = vertexPartitionMap;
    }

    public int getTotalPartitions() {
        return totalPartitions;
    }

    // Linear weighted greedy . Only consider OutEdges
    synchronized private long deterministicGreedyPartitioning(
            Vertex<W, E, M> vert) {
        long partitionID = -1;
        long partitionVertexCount = 0;
        double peakValue = Double.NEGATIVE_INFINITY;

//        int i = 0;
        // Iterate through all the partitions and select the best possible
        for(Partition<W, E, M> p: partitions) {

            // Intersections between partition vertextIDs and given Vertex
            double function = CollectionUtils.intersection(p.getVertexIDs(),
                    getNeighborVertices(vert)).size()
                    * p.calculateWeightPenalty();

            if (function > peakValue) {         // Find minimum
                peakValue = function;
                partitionID = p.getPartitionID();
                partitionVertexCount = p.getVertices().size();
            }
            else if (function == peakValue) {   // Balanced
                if (partitionVertexCount > p.getVertices().size()) {
                    partitionID = p.getPartitionID();
                    partitionVertexCount = p.getVertices().size();
                }
            }
//                
//            if(i == totalPartitions - 1 && partitionID == -1) {                    
//                System.out.printf("%f %d\n", function, partitionVertexCount);
//            }
//            i++;
        }
        return partitionID;
    }

    private Set<W> getNeighborVertices(Vertex<W, E, M> vert) {
        Set<W> verticesID = new HashSet<>();
        for (Edge<W, E> outEdge : vert.getEdges()) {
            verticesID.add(outEdge.getDestinationVertexID());
        }
        return verticesID;
    }

    @Override
    public int getPartition(K key, V value, int numTasks) {
        Partition result = (Partition) vertexPartitionMap.get(key);
        if (result == null) {
            result = partitions[
                    (int) deterministicGreedyPartitioning((Vertex) key)];
            vertexPartitionMap.put(key, (V) result);
        }
        result.putVertex((Vertex) key);
        return (int) result.getPartitionID();
    }
}
