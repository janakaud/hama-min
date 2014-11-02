/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package hama;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@SuppressWarnings("rawtypes")
public class Partition<W extends WritableComparable, E extends Writable, M extends Writable> {

    private final long partitionId;
    private final long capacity;
    // private List<Vertex<V, E, M>> vertices;
//	private ConcurrentMap<V, Vertex<V, E, M>> vertices;
    private ConcurrentMap<W, Vertex<W, E, M>> vertices;

    public Partition(long partitionId, long capacity) {
        this.partitionId = partitionId;
        this.capacity = capacity;
        // this.vertices = new ArrayList<Vertex<V, E, M>>();
//		this.vertices = Maps.newConcurrentMap();
        this.vertices = new ConcurrentHashMap<>();
    }

    public long getPartitionID() {
        return partitionId;
    }

    public long getCapacity() {
        return capacity;
    }

    public Vertex<W, E, M> getVertex() {
        return null;
    }

    public void putVertex(Vertex<W, E, M> v) {
        vertices.put(v.getVertexID(), v);
    }

    public void removeVertex(Vertex<W, E, M> v) {
        vertices.remove(v.getVertexID());
    }

    public long numberOfEdges() {
        return capacity;
    }

    public double calculateWeightPenalty() {
        return 1 - ((double) getEdgeCount() / getCapacity());
    }

    public Map<W, Vertex<W, E, M>> getVertices() {
        return vertices;
    }

    public Set<W> getVertextIds() {
        return vertices.keySet();
    }

    public long getEdgeCount() {
        long total = 0;
        for (Map.Entry<W, Vertex<W, E, M>> entry : vertices.entrySet()) {
            total += entry.getValue().getEdges().size();
        }
        return total;
    }

    public Set<Edge<W, E>> getAllEdges() {
        Set<Edge<W, E>> partitionEdgeSet = new HashSet<Edge<W, E>>();

        for (Map.Entry<W, Vertex<W, E, M>> entry : vertices.entrySet()) {
            partitionEdgeSet.addAll(entry.getValue().getEdges());
        }
        return partitionEdgeSet;

    }
}
