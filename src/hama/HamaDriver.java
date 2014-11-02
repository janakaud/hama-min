package hama;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Nuran Arachchi
 */
public class HamaDriver {

    // data structures for reuse
    private ArrayList<Edge<WritableComparableInteger, WritableInteger>>[] graph;
    private Vertex<WritableComparableInteger, WritableInteger, WritableInteger> vertices[];
    private Map<WritableComparable, Integer> map;

    // partitioners
    private Partitioner<WritableComparable, Void> hash;
    private Partitioner<Vertex, Void> greedy;

    // test data
    private static final int[] vertexCounts = {
        0, 2, 5, 10, 20, 50, 100, 200, 500, 1000, 2000,
        5000, 5000, 5000, 5000, 5000, 5000
    };
    private static final int[] edgeCounts = {
        0, 1, 5, 30, 150, 1000, 4500, 18000, 120000, 450000, 1800000, 
        50000, 100000, 200000, 400000, 800000, 1600000
    };

    private static final int PARTITION_COUNT = 400;
    private static final int PARTITION_CAPACITY = 4000;

    public static void main(String args[]) {
        new HamaDriver().run();
    }

    public void run() {
        int hashCuts, greedyCuts;
        for (int i = 0; i < vertexCounts.length; i++) {
            makeGraph(vertexCounts[i], edgeCounts[i]);

            hashCuts = getHashCuts(PARTITION_COUNT, PARTITION_CAPACITY);
            greedyCuts = getGreedyCuts(PARTITION_COUNT, PARTITION_CAPACITY);

            System.out.format("%d\t%d\t%d\t%d\n",
                    vertexCounts[i], edgeCounts[i], hashCuts, greedyCuts);
        }
    }

    public void makeGraph(int vertexCount, int edgeCount) {
        // generate random graph for test
        graph = ConnectedGraphs.getRandomConnectedGraph(vertexCount, edgeCount);
        if (graph == null) {
            System.out.println("Invalid input data");
            System.exit(1);
        }

        // convert generated graph to Hama-compatible adjacency list form
        vertices = new Vertex[vertexCount];
        for (int i = 0; i < vertexCount; i++) {
            vertices[i] = new Vertex(new WritableComparableInteger(i),
                    graph[i], new WritableInteger(0));
        }

        /*        // display graph
         for (int i = 0; i < vertexCount; i++) {
         System.out.println("Vertex ID: " + vertices[i].getVertexID().getValue() + " Edges : ");
         int limit = vertices[i].getEdges().size();
         System.out.println(limit + " is the size");
         for (int j = 0; j < limit; j++) {
         System.out.println(vertices[i].getEdges().get(j).getDestinationVertexID().getValue() + "   " + vertices[i].getEdges().get(j).getValue().getValue());
         }
         }
         */
    }

    public int getHashCuts(int partitionCount, int partitionCapacity) {
        // try partitioning with default Hama hash partitioning algorithm
        hash = new HashPartitioner<>();
        map = new HashMap<>();

        for (Vertex v : vertices) {
            map.put(v.getVertexID(),
                    hash.getPartition(v.getVertexID(), null, partitionCount));
        }
        
        return countCuts();
    }

    public int getGreedyCuts(int partitionCount, int partitionCapacity) {
        // try partitioning with default Hama hash partitioning algorithm
        greedy = new GreedyHeuristicGraphPartitionerImpl<>(
                partitionCount, partitionCapacity);
        map = new HashMap<>();
        for (Vertex v : vertices) {
            map.put(v.getVertexID(),
                    greedy.getPartition(v, null, partitionCount));
        }
        
        return countCuts();
    }
    
    public int countCuts() {
        // count number of edge cuts in resulting partitioning
        int fromPartition, toPartition;
        Edge<WritableComparable, Writable> edge;

        int edgeCuts = 0;
        for (Vertex from : vertices) {
            fromPartition = map.get(from.getVertexID());
            for (Object e : from.getEdges()) {
                edge = (Edge) e;
                toPartition = map.get(edge.getDestinationVertexID());
//                System.out.format("%3d %3d\n", fromPartition, toPartition);
//                System.out.format("%3d %3d\n", 
//                        ((WritableComparableInteger)
//                                from.getVertexID()).getValue(), 
//                        ((WritableComparableInteger)
//                                edge.getDestinationVertexID()).getValue());
                if (fromPartition != toPartition) {  // edge cut detected!
                    edgeCuts++;
                }
            }
        }
        return edgeCuts;
    }
}
