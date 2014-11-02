package hama;

import java.util.ArrayList;
import java.util.List;

/**
 * Vertex is a abstract definition of Google Pregel Vertex. For implementing a
 * graph application, one must implement a sub-class of Vertex and define, the
 * message passing and message processing for each vertex.
 * 
* Every vertex should be assigned an ID. This ID object should obey the
 * equals-hashcode contract and would be used for partitioning.
 * 
* The edges for a vertex could be accessed and modified using the
 * {@link Vertex#getEdges()} call. The self value of the vertex could be changed
 * by {@link Vertex#setValue(Writable)}.
 * 
 * @param <V> Vertex ID object type
 * @param <E> Edge cost object type
 * @param <M> Vertex value object type
 */
@SuppressWarnings("rawtypes")
public class Vertex<V extends WritableComparable, E extends Writable, M extends Writable>
        implements VertexInterface<V, E, M> {

    private V vertexID;
    private M value;
    private List<Edge<V, E>> edges;

    public V getVertexID() {
        return this.vertexID;
    }

    public Vertex(V vertexID, List<Edge<V, E>> edges, M value) {
        this.edges = edges;
        this.value = value;
        this.vertexID = vertexID;
    }

    public void setEdges(List<Edge<V, E>> list) {
        this.edges = list;
    }

    public void addEdge(Edge<V, E> edge) {
        if (edges == null) {
            this.edges = new ArrayList<Edge<V, E>>();
        }
        this.edges.add(edge);
    }

    public List<Edge<V, E>> getEdges() {
        return (edges == null) ? new ArrayList<Edge<V, E>>() : edges;
    }

    public M getValue() {
        return this.value;
    }

    public void setValue(M value) {
        this.value = value;
    }

    public void setVertexID(V vertexID) {
        this.vertexID = vertexID;
    }

    /**
     * Gives access to the BSP primitives and additional features by a peer.
     */
    /**
     * @return the configured partitioner instance to message vertices.
     */
    @Override
    public int hashCode() {
        return ((vertexID == null) ? 0 : vertexID.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Vertex<?, ?, ?> other = (Vertex<?, ?, ?>) obj;
        if (vertexID == null) {
            if (other.vertexID != null) {
                return false;
            }
        } else if (!vertexID.equals(other.vertexID)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Active: " + " -> ID: " + getVertexID()
                + (getValue() != null ? " = " + getValue() : "") + " // " + edges;
    }
    /*
     public void readFields(DataInput in) throws IOException {
     if (in.readBoolean()) {
     if (this.vertexID == null) {
     this.vertexID = GraphJobRunner.createVertexIDObject();
     }
     this.vertexID.readFields(in);
     }
     if (in.readBoolean()) {
     if (this.value == null) {
     this.value = GraphJobRunner.createVertexValue();
     }
     this.value.readFields(in);
     }
     this.edges = new ArrayList<Edge<V, E>>();
     if (in.readBoolean()) {
     int num = in.readInt();
     if (num > 0) {
     for (int i = 0; i < num; ++i) {
     V vertex = GraphJobRunner.createVertexIDObject();
     vertex.readFields(in);
     E edgeCost = null;
     if (in.readBoolean()) {
     edgeCost = GraphJobRunner.createEdgeCostObject();
     edgeCost.readFields(in);
     }
     Edge<V, E> edge = new Edge<V, E>(vertex, edgeCost);
     this.edges.add(edge);
     }
     }
     }
     votedToHalt = in.readBoolean();
     readState(in);
     }*/
    /*
     public void write(DataOutput out) throws IOException {
     if (vertexID == null) {
     out.writeBoolean(false);
     } else {
     out.writeBoolean(true);
     vertexID.write(out);
     }
     if (value == null) {
     out.writeBoolean(false);
     } else {
     out.writeBoolean(true);
     value.write(out);
     }
     if (this.edges == null) {
     out.writeBoolean(false);
     } else {
     out.writeBoolean(true);
     out.writeInt(this.edges.size());
     for (Edge<V, E> edge : this.edges) {
     edge.getDestinationVertexID().write(out);
     if (edge.getValue() == null) {
     out.writeBoolean(false);
     } else {
     out.writeBoolean(true);
     edge.getValue().write(out);
     }
     }
     }
     out.writeBoolean(votedToHalt);
     writeState(out);
     }*/
// compare across the vertex ID

//    @SuppressWarnings("unchecked")
    /**
     * Read the state of the vertex from the input stream. The framework would
     * have already constructed and loaded the vertex-id, edges and voteToHalt
     * state. This function is essential if there is any more properties of
     * vertex to be read from.
     */
    /**
     * Writes the state of vertex to the output stream. The framework writes the
     * vertex and edge information to the output stream. This function could be
     * used to save the state variable of the vertex added in the implementation
     * of object.
     */
    /**
     * Get the last aggregated value of the defined aggregator, null if nothing
     * was configured or not returned a result. You have to supply an index, the
     * index is defined by the order you set the aggregator classes in
     * {@link GraphJob#setAggregatorClass(Class...)}. Index is starting at zero,
     * so if you have a single aggregator you can retrieve it via
     * {@link GraphJobRunner#getLastAggregatedValue}(0).
     */
    /**
     * Get the number of aggregated vertices in the last superstep. Or null if
     * no aggregator is available.You have to supply an index, the index is
     * defined by the order you set the aggregator classes in
     * {@link GraphJob#setAggregatorClass(Class...)}. Index is starting at zero,
     * so if you have a single aggregator you can retrieve it via
     * {@link #getNumLastAggregatedVertices}(0).
     */
}
