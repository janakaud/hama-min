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
        0, 2, 5, 10, 20, 50, 100, 200, 
        500, 500,
        1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000
    };
    private static final int[] edgeCounts = {
        0, 1, 5, 30, 150, 1000, 4500, 18000, 
        10000, 120000, 
        1000, 2000, 5000, 8000, 10000, 20000, 50000, 100000, 200000, 450000
    };

    private static final int PARTITION_COUNT = 400;
    private static final int PARTITION_CAPACITY = 4000;

    public static void main(String args[]) {
        new HamaDriver().run();
    }

    private void run() {
        int hashCuts, greedyCuts;
        
        // prompt
        System.out.println("This program performs a side-by-side performance "
                + "comparison of existing \nHashing partitioning algorithm "
                + "with the new Greedy Heuristic partitioning \n"
                + "algorithm.\n");
        System.out.println("For each of the next " + vertexCounts.length + 
                " lines, the program will generate random graphs of the\n"
                + "specified sizes, partition them using the two algorithms "
                + "separately, count\n"
                + "the edge cuts in the resulting arrangements and report the "
                + "percentage\n"
                + "efficiency of the Greedy Heuristic based algorithm over the "
                + "Hashing based\nalgorithm.\n");

        // header
        System.out.printf("%10s%10s%15s%12s%12s%18s\n", "Vertices", "Edges", 
                "Density (%)", "Hashing", "Greedy", "Efficiency (%)");
        
        // run each test case
        for (int i = 0; i < vertexCounts.length; i++) {
            makeGraph(vertexCounts[i], edgeCounts[i]);

            hashCuts = getHashCuts(PARTITION_COUNT, PARTITION_CAPACITY);
            greedyCuts = getGreedyCuts(PARTITION_COUNT, PARTITION_CAPACITY);

            // display metrics
            System.out.format("%10d%10d%15.3f%12d%12d%18.3f\n",
                    vertexCounts[i], edgeCounts[i], 
                    density(vertexCounts[i], edgeCounts[i]), 
                    hashCuts, greedyCuts, 
                    ((float)hashCuts - greedyCuts)*100/hashCuts);
        }
    }
    
    private float density(int vertices, int edges) {
        // density = % of edges present, out of entire possible edge count
        int possibleEdges = (vertices*(vertices - 1)/2 - (vertices - 1));
        return (float)edges*100/possibleEdges;
    }

    private void makeGraph(int vertexCount, int edgeCount) {
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
    }

    private int getHashCuts(int partitionCount, int partitionCapacity) {
        // partition with default Hama hash partitioning algorithm
        hash = new HashPartitioner<>();
        map = new HashMap<>();

        for (Vertex v : vertices) {
            map.put(v.getVertexID(),
                    hash.getPartition(v.getVertexID(), null, partitionCount));
        }
        
        return countCuts();
    }

    private int getGreedyCuts(int partitionCount, int partitionCapacity) {
        // partition with Greedy heuristic partitioning algorithm
        greedy = new GreedyHeuristicGraphPartitionerImpl<>(
                partitionCount, partitionCapacity);
        map = new HashMap<>();
        for (Vertex v : vertices) {
            map.put(v.getVertexID(),
                    greedy.getPartition(v, null, partitionCount));
        }
        
        return countCuts();
    }
    
    private int countCuts() {
        // count number of edge cuts in resulting partitioning
        int fromPartition, toPartition;
        Edge<WritableComparable, Writable> edge;

        int edgeCuts = 0;
        for (Vertex from : vertices) {
            fromPartition = map.get(from.getVertexID());
            for (Object e : from.getEdges()) {
                edge = (Edge) e;
                toPartition = map.get(edge.getDestinationVertexID());
                if (fromPartition != toPartition) {  // edge cut detected!
                    edgeCuts++;
                }
            }
        }
        return edgeCuts;
    }
}
