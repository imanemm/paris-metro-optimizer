/**
 * Represents a simple station (vertex) in metroGraph.
 * Stores the station's unique ID and station name.
 *
 * @author Imane Moussa Marou
 */
public class Station {
    private final int ID;
    private final String stationName;

    public Station(int ID, String stationName) {
        this.ID = ID;
        this.stationName = stationName;
    }

    /**
     * Returns the unique ID of this station.
     *
     * @return the station's unique ID
     */
    public int getID() {
        return ID;
    }

    /**
     * Returns the name of this station.
     *
     * @return the station's name
     */
    public String getStationName() {
        return stationName;
    }

}
