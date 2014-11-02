/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hama;

import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author Nuran Arachchi
 */
public class ConnectedGraphs {

    private static ArrayList<Edge<WritableComparableInteger, WritableInteger>>[] graph;

    private static int MIN_WEIGHT = 1;
    private static int MAX_WEIGHT = 100;

    public static ArrayList<Edge<WritableComparableInteger, WritableInteger>>[] 
        getRandomConnectedGraph(int vertices, int edges) {
            
        graph = new ArrayList[vertices];
        for (int i = 0; i < vertices; i++) {
            graph[i] = new ArrayList();
        }

        int maxEdges, from, to, edgeCount, temp;
        int list[] = new int[vertices];
        boolean adj[][] = new boolean[vertices][vertices];//2d array of graphs
        Random random = new Random();
        
        // initialize the adjacency matrix
        for (from = 0; from < vertices; from++) {
            for (to = 0; to < vertices; to++) {
                adj[from][to] = false;
            }
        }
        edgeCount = 0;
        
        // check for valid input data
        if (edges < vertices) {
            return null;
        }
        maxEdges = (vertices * (vertices + 1)) / 2;
        if (edges > maxEdges) {
            return null;
        }
        
        // generate a random spanning tree by the greedy method
        permuteRandomly(list, random);
        
        for (from = 0; from < vertices; from++) {
            to = random.nextInt(vertices);
            adj[list[from]][list[to]] = true;
//            adj[permute[nodeb]][permute[nodea]] = true;

            Edge c = new Edge(new WritableComparableInteger(list[to]), 
                    new WritableInteger((int) (MIN_WEIGHT + 
                            random.nextDouble() * (MAX_WEIGHT + 1 - MIN_WEIGHT))));
            graph[list[from]].add(c);
            edgeCount++;
        }
        
        // add the remaining edges randomly
        while (edgeCount < edges) {
            from = random.nextInt(vertices);
            to = random.nextInt(vertices);
            if (from == to) {
                continue;
            }
            if (from > to) {
                temp = from;
                from = to;
                to = temp;
            }
            if (!adj[from][to]) {
                adj[from][to] = true;
                Edge c = new Edge(new WritableComparableInteger(to), 
                        new WritableInteger((int) (MIN_WEIGHT + 
                                random.nextDouble() * (MAX_WEIGHT + 1 - MIN_WEIGHT))));
                graph[from].add(c);
                edgeCount++;
            }
        }

        return graph;
    }

    public static ArrayList<Edge<WritableComparableInteger, 
        WritableInteger>>[] getGraph() {
        return graph;
    }

    public static void permuteRandomly(int[] list, Random random) {
        int i, j, temp;
        int n = list.length;

        // init with natural order of values
        for (i = 0; i < n; i++) {
            list[i] = i;
        }

        // now, randomly swap array elements
        for (i = 0; i < n; i++) {
            j = (int) (i + random.nextDouble() * (n - i));
            temp = list[i];
            list[i] = list[j];
            list[j] = temp;
        }
    }
}
