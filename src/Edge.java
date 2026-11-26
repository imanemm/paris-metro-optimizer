/**
 * Represents one subway connection.
 * Stores the two station IDs and the cost of the monthly pass between them.
 *
 * @author Imane Moussa Marou
 */
class Edge implements Comparable<Edge> {
    private final int station1;
    private final int station2;
    private int cost;

    /**
     * Creates an edge using the two connected stations IDs and its cost.
     *
     * @param station1 First station's ID connected to the edge
     * @param station2 Second station's ID connected to the edge
     * @param cost     Weight of the edge
     */
    public Edge(int station1, int station2, int cost) {
        this.station1 = station1;
        this.station2 = station2;
        this.cost = cost;
    }

    /**
     * Returns the cost of this edge.
     *
     * @return the edge's weight.
     */
    public int getCost() {
        return cost;
    }

    /**
     * Returns the ID of this edge's first station.
     *
     * @return the edge's first station's ID
     */
    public int getStation1() {
        return station1;
    }

    /**
     * Returns the ID of this edge's second station.
     *
     * @return the edge's second station's ID
     */
    public int getStation2() {
        return station2;
    }

    /**
     * Compares this edge to another edge based on their cost.
     *
     * @param otherEdge the edge to compare this edge with
     * @return a negative if this edge has a lower cost, zero if equal cost,
     * a positive number if this edge has higher cost
     */
    @Override
    public int compareTo(Edge otherEdge) {
        return Integer.compare(this.getCost(), otherEdge.getCost());
    }
}
