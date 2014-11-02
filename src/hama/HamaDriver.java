/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hama;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Nuran Arachchi
 */
public class HamaDriver  {

    public static void main(String args[]) {
        int vertexCount = 10;
        int edgeCount = 25;
        int partitionCount = 4;
        
        ArrayList<Edge<WritableComparableInteger, WritableInteger>>[] graph;

        // generate random graph for test
        graph = ConnectedGraphs.getRandomConnectedGraph(vertexCount, edgeCount);
        if (graph == null) {
            System.out.println("Invalid input data");
            System.exit(1);
        }
        
        // convert generated graph to Hama-compatible adjacency list form
        Vertex<WritableComparableInteger, WritableInteger, WritableInteger> 
                vertices[] = new Vertex[vertexCount];
        for (int i = 0; i < vertexCount; i++) {
            vertices[i] = new Vertex(new WritableComparableInteger(i), 
                    graph[i], new WritableInteger(0));
        }
        
/*        // display graph
        for (int i = 0; i < vertexCount; i++) {
            System.out.println("vertex Id: " + vertices[i].getVertexID().getValue() + " Edges : ");
            int limit = vertices[i].getEdges().size();
            System.out.println(limit + " is the size");
            for (int j = 0; j < limit; j++) {
                System.out.println(vertices[i].getEdges().get(j).getDestinationVertexID().getValue() + "   " + vertices[i].getEdges().get(j).getValue().getValue());
            }
        }
*/
        
        // try partitioning with default Hama hash partitioning algorithm
        Partitioner<WritableComparable, Void> hash = new HashPartitioner<>();
        Map<WritableComparable, Integer> map = new HashMap<>();
        for(Vertex v: vertices) {
            map.put(v.getVertexID(), 
                    hash.getPartition(v.getVertexID(), null, partitionCount));
        }
        
        // count number of edge cuts in resulting partitioning
        int fromPartition, toPartition;
        Edge<WritableComparable, Writable> edge;
        
        int edgeCuts = 0;
        for(Vertex from: vertices) {
            fromPartition = map.get(from.getVertexID());
            for(Object e: from.getEdges()) {
                edge = (Edge)e;
                toPartition = map.get(edge.getDestinationVertexID());
                if(fromPartition != toPartition) {  // edge cut detected!
                    edgeCuts++;
                }
            }
        }
        
        System.out.format("%d\t%d\t%20s\t%d\n", vertexCount, partitionCount, 
                "Hashing", edgeCuts);
    }
}
