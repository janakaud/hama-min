package hama;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

//import org.apache.commons.collections.CollectionUtils;
/**
 * GreedyHeuristicGraphPartitioner is partitioning by the (Weighted)
 * Deterministic Greedy algorithm.
 *
 * @param <K>
 * @param <V>
 * 
*/
public class GreedyHeuristicGraphPartitionerImpl<W extends WritableComparable, E extends Writable, M extends Writable, K, V>
        implements Partitioner<K, V> {

    private final int totalPartitions;
    // Map beetween partionID to VertextID
    // Edge Count of each partion
    private Map<K, V> vertexPartitionMap;
    private List<Partition<W, E, M>> partitions;// may be priority queue??

    public GreedyHeuristicGraphPartitionerImpl(int totalPartitions) {
        this.totalPartitions = totalPartitions;
        this.partitions = new ArrayList<Partition<W, E, M>>();
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
        double peakValue = 0;

        Iterator<Partition<W, E, M>> it = partitions.iterator();

        // Iterate through all the partitions and slect the best possible
        while (it.hasNext()) {
            Partition<W, E, M> p = it.next();

            // Intersections between partition vertextIds and given Vertex
            double function = CollectionUtils.intersection(p.getVertextIds(),
                    getNeighborVetrices(vert)).size()
                    * p.calculateWeightPenalty();

            // Find minimum
            if (function > peakValue) {
                peakValue = function;
                partitionID = p.getPartitionID();
                partitionVertexCount = p.getVertices().size();
            }

            // Balanced
            if (function == peakValue) {
                if (partitionVertexCount > p.getVertices().size()) {
                    partitionID = p.getPartitionID();
                    partitionVertexCount = p.getVertices().size();
                }
            }
        }
        return partitionID;
    }

    private Set<W> getNeighborVetrices(Vertex<W, E, M> vert) {
        Set<W> verticesId = new HashSet<W>();
        for (Edge<W, E> outEdge : vert.getEdges()) {
            verticesId.add(outEdge.getDestinationVertexID());
        }
        return verticesId;

    }

    @Override
    public int getPartition(K key, V value, int numTasks) {
        Partition result = (Partition) vertexPartitionMap.get(key);
        if (result == null) {
            result = partitions.get(
                    (int) deterministicGreedyPartitioning((Vertex) key));
            vertexPartitionMap.put(key, (V) result);
        }
        return (int) result.getPartitionID();
    }
}
