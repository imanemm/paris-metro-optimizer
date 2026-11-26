/**
 * Represents an unordered pair of hub IDs used as a key in a HashMap.
 * Two HubPairs are equal if they contain the same two hub IDs.
 *
 * @author Imane Moussa Marou
 */
public class HubPair {
    // smaller hub ID
    private final int hubA;
    // larger hub ID
    private final int hubB;

    public HubPair(int hubA, int hubB) {
        this.hubA = hubA;
        this.hubB = hubB;
    }

    public int getHubA() {
        return hubA;
    }

    public int getHubB() {
        return hubB;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof HubPair)) return false;

        HubPair other = (HubPair) obj;
        return this.hubA == other.hubA && this.hubB == other.hubB;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + hubA;
        result = 31 * result + hubB;
        return result;
    }

    @Override
    public String toString() {
        return "(" + hubA + ", " + hubB + ")";
    }
}
