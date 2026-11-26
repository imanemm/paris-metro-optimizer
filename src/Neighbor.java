/**
 * Small helper class used in the adjacency list to store a neighbor station
 * and the travel cost of that connection.
 *
 * @author Imane Moussa Marou
 */
public class Neighbor {
    private final int station;
    private final int cost;

    public Neighbor(int station, int cost) {
        this.station = station;
        this.cost = cost;
    }

    public int getStation() {
        return station;
    }

    public int getCost() {
        return cost;
    }
}
