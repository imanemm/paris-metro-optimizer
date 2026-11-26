import javax.swing.text.Segment;
import java.util.*;
import java.util.Scanner;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Paris (P2- Part B)
 * Startup code given in the Fall 2025 for csi2110/csi2510
 *
 * @author Imane Moussa Marou
 */
public class ParisMetro {
    // === Instance variables ===

    static int nbOfStations;
    static int nbOfEdges;
    static int totalCost;
    static int segmentsUsed;
    static int totalHubVertices;

    // Map of all the metro stations in metroGraph (stationID, Station)
    static HashMap<Integer, Station> stations;
    // List of all the metro simple edges in metroGraph
    static ArrayList<Edge> edges;
    // Map of all the Hub Stations (stationID, hubID)
    static HashMap<Integer, Integer> stationToHub;
    // Number of Hub Stations
    static int nbOfHubStations;
    // List of all the expensiveParisSubway segments
    static ArrayList<Edge> segments;
    // List of all the Hub Stations Names
    static ArrayList<Object> hubNames;
    // List of all used Segments
    static ArrayList<Edge> usedSegments;

    public static void main (String[] args) {
        readMetro();
        findHubs();
        buildExpensiveSubway();
        runKruskal();
        printOutput();
    }

    // === Utility Methods ===

    /**
     * Reads and creates the metroGraph from the metro.txt input file.
     */
    public static void readMetro() {
        //System.out.println("\nStart Reading Metro");
        Scanner scan = new Scanner(System.in);

        nbOfStations = scan.nextInt();
        nbOfEdges = scan.nextInt();
        System.out.println("Paris Metro Graph has " + nbOfStations + " vertices and " + nbOfEdges + " edges.");

        stations = new HashMap<>();
        edges = new ArrayList<>();

        // Storing each station in the station map
        for (int i = 0; i < nbOfStations; i++) {
            // vertex number (unique, example: 0016)
            int vertexNumber = Integer.valueOf(scan.next());
            // vertex name (not unique, example: Bastille)
            String stationName = scan.nextLine();
            // System.out.println(vertexNumber+" "+stationName);

            Station station = new Station(vertexNumber, stationName);
            stations.put(vertexNumber, station);
        }

        scan.nextLine(); // read the $ sign

        //Storing each edge in the edge list
        for (int i = 0; i < nbOfEdges; i++) {
            // edge information
            int station1 = scan.nextInt();
            int station2 = scan.nextInt();
            int cost = scan.nextInt();
            // System.out.println("station1=" + station1 + " station2=" + station2+" cost=" + cost);

            Edge newEdge = new Edge(station1, station2, cost);
            edges.add(newEdge);
        }

        //System.out.println("End Reading Metro\n");
    }

    /**
     * Finds all Hub Stations by grouping stations connected with -1 edges.
     * A Hub Station is a cluster of size > 1. Each station in a hub gets a hubID, and stations
     * not in any hub get hubID = -1.
     */
    private static void findHubs() {
        // Partition of station IDs
        Partition<Integer> hubStations = new Partition<>();

        // Map of station ID and partition nodes
        HashMap<Integer, Node<Integer>> nodes = new HashMap<>();

        // Map of hub IDs based on their cluster rep
        HashMap<Node<Integer>, Integer> repToHubID = new HashMap<>();

        // Creating clusters for each station and adding them to a node list
        for (Integer stationID : stations.keySet()) {
            nodes.put(stationID, hubStations.makeCluster(stationID));
        }

        // Creating the connections of the hubs
        for (Edge edge : edges) {
            if (edge.getCost() == -1) {
                Node<Integer> nodeStation1 = nodes.get(edge.getStation1());
                Node<Integer> nodeStation2 = nodes.get(edge.getStation2());
                if (nodeStation1 != null && nodeStation2 != null) {
                    hubStations.union(nodeStation1, nodeStation2);
                }
            }
        }

        // Computing the size of each cluster (by rep)
        HashMap<Node<Integer>, Integer> repToSize = new HashMap<>();

        for (Integer stationID : stations.keySet()) {
            Node<Integer> nodeStation = nodes.get(stationID);
            Node<Integer> rep = hubStations.find(nodeStation);

            Integer size = repToSize.get(rep);
            if (size == null) size = 0;
            repToSize.put(rep, size + 1);
        }

        // Assigning HubIDs only to clusters with size > 1
        stationToHub = new HashMap<>();

        nbOfHubStations = 0;
        totalHubVertices = 0;
        hubNames = new ArrayList<>();

        for (Integer stationID : stations.keySet()) {
            Node<Integer> nodeStation = nodes.get(stationID);
            Node<Integer> rep = hubStations.find(nodeStation);

            int clusterSize = repToSize.get(rep);

            if (clusterSize > 1) {
                // Real Hub Station cluster
                Integer hubID = repToHubID.get(rep);
                if (hubID == null) {
                    hubID = nbOfHubStations;
                    repToHubID.put(rep, hubID);
                    nbOfHubStations++;

                    // Setting its name using this station
                    String hubName = stations.get(stationID).getStationName().trim();
                    hubNames.add(hubName);
                }

                stationToHub.put(stationID, hubID);
                totalHubVertices++;
            } else {
                // Not a hub so HubID = -1
                stationToHub.put(stationID, -1);
            }
        }
    }

    /**
     * Builds the expensiveParisSubway Graph.
     * Vertices = Hub Stations
     * Edges (segments) = paths along subway lines between hubs with total cost
     */
    private static void buildExpensiveSubway() {
        // Building the adjacency map of metroGraph using only positive edges
        HashMap<Integer, ArrayList<Neighbor>> adjacencyMap = new HashMap<>();

        for (Integer stationID : stations.keySet()) {
            adjacencyMap.put(stationID, new ArrayList<>());
        }

        for (Edge edge : edges) {
            if (edge.getCost() > 0) {
                int station1 = edge.getStation1();
                int station2 = edge.getStation2();
                int cost = edge.getCost();

                adjacencyMap.get(station1).add(new Neighbor(station2, cost));
                adjacencyMap.get(station2).add(new Neighbor(station1, cost));
            }
        }

        // Walking along lines to find segments to other hubs
        HashMap<HubPair, Integer> bestCostMap = new HashMap<>();
        segments = new ArrayList<>();

        // Map of list of stations IDs for each HubID
        HashMap<Integer, ArrayList<Integer>> hubToStations = new HashMap<>();
        for (int h = 0; h < nbOfHubStations; h++) {
            hubToStations.put(h, new ArrayList<>());
        }

        for (Integer stationID : stations.keySet()) {
            int hubID = stationToHub.get(stationID);
            if (hubID >= 0) {
                hubToStations.get(hubID).add(stationID);
            }
        }

        // Each Hub Station as a starting point
        for (int hubStart = 0; hubStart < nbOfHubStations; hubStart++) {
            // Each station belonging to this Hub Station
            for (Integer startStation : hubToStations.get(hubStart)) {
                for (Neighbor neighbor : adjacencyMap.get(startStation)) {
                    int nextStation = neighbor.getStation();
                    int cost = neighbor.getCost();

                    int hubStationNext = stationToHub.get(nextStation);

                    // Skipping if still in the same hub
                    if (hubStationNext == hubStart) continue;

                    if (hubStationNext >= 0 && hubStationNext != hubStart) {
                        // Direct connection to another Hub Station from HubStart
                        int hubMin = min(hubStart, hubStationNext);
                        int hubMax = max(hubStart, hubStationNext);
                        HubPair pair = new HubPair(hubMin, hubMax);
                        updateBestCost(bestCostMap, pair, cost);
                    } else if (hubStationNext == -1) {
                        // Going through non-hub stations until reaching another Hub Station
                        int segmentCost = cost;
                        int currentStation = nextStation;

                        HashSet<Integer> visitedStations = new HashSet<>();
                        visitedStations.add(startStation);
                        visitedStations.add(currentStation);

                        boolean reachedHubStation = false;
                        int hubEnd = -1;

                        // Walking forward along the line
                        while (true) {
                            int neighborCount = 0;
                            int nextOnLine = -1;
                            int edgeCost = 0;

                            // Looking at neighbors of current station
                            for (Neighbor neighbor2 : adjacencyMap.get(currentStation)) {
                                int station = neighbor2.getStation();
                                int costOfNeighbor = neighbor2.getCost();

                                if (visitedStations.contains(station)) continue;

                                int stationHub = stationToHub.get(station);
                                if (stationHub == -1) {
                                    // Another non-hub station
                                    neighborCount ++;
                                    nextOnLine = station;
                                    edgeCost = costOfNeighbor;
                                } else {
                                    // Reached a Hub Station
                                    neighborCount++;
                                    hubEnd = stationHub;
                                    edgeCost = costOfNeighbor;
                                    reachedHubStation = true;
                                    nextOnLine = station;
                                }
                            }

                            // No segment
                            if (neighborCount == 0) break;

                            // Moving forward
                            segmentCost += edgeCost;
                            visitedStations.add(nextOnLine);
                            currentStation = nextOnLine;

                            if (reachedHubStation) {
                                if (hubEnd != hubStart) {
                                    int hubMin = min(hubStart, hubEnd);
                                    int hubMax = max(hubStart, hubEnd);
                                    HubPair pair = new HubPair(hubMin, hubMax);
                                    updateBestCost(bestCostMap, pair, segmentCost);
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }

        // Converting bestCostMap into final segment list
        for (HubPair pair : bestCostMap.keySet()) {
            int hubA = pair.getHubA();
            int hubB = pair.getHubB();
            int bestCost = bestCostMap.get(pair);

            Edge segment = new Edge(hubA, hubB, bestCost);
            segments.add(segment);
        }
    }

    /**
     * Runs the Kruskal's algorithm to compute the MST inspired by Part A.
     * Sorts the segments by cost and picks the cheapest ones that connect the network.
     */
    private static void runKruskal() {
        int potentialTotalCost = 0;
        segmentsUsed = 0;
        usedSegments = new ArrayList<>();

        // List of Partition nodes for each hub station ID
        ArrayList<Node<Integer>> nodes = new ArrayList<>();

        // Creating a Partition for all the Hub Stations IDs
        Partition<Integer> partition = new Partition<>();

        // Creating singleton cluster for each hub station's ID
        for (int h = 0; h < nbOfHubStations; h++) {
            // Storing the returned node in a list.
            nodes.add(partition.makeCluster(h));
        }

        // Sorting the segment list from lowest to highest cost per edge.
        Collections.sort(segments);

        for (Edge segment : segments) {
            int hub1 = segment.getStation1();
            int hub2 = segment.getStation2();

            if (partition.find(nodes.get(hub1)) != partition.find(nodes.get(hub2))) {
                partition.union(nodes.get(hub1), nodes.get(hub2));
                potentialTotalCost += segment.getCost();
                segmentsUsed += 1;
                usedSegments.add(segment);
            }
        }

        if (segmentsUsed == nbOfHubStations - 1) {
            totalCost = potentialTotalCost;
        } else {
            totalCost = -1;
        }
    }

    /**
     * Displays the final result of Part B's MST problem.
     * Prints either "Impossible" or the complete output.
     */
    private static void printOutput() {

        // Displays Impossible if the output of the Kruskal's algorithm is -1
        if (totalCost == -1 ) {
            System.out.println("\nImpossible");
            return;
        }

        // Display a list of Hub Stations names in a proper format
        System.out.println("\nHub Stations = " + hubNames);

        // Displays the number of Hub Stations and the total of Hub Vertices
        System.out.println("\nNumber of Hub Stations = " + nbOfHubStations + " (total Hub Vertices = " + totalHubVertices + ")");

        // Displays the number of Possible Segments
        System.out.println("\nNumber of Possible Segments = " + segmentsUsed);

        // Displays the total cost and each segment to buy for the month
        System.out.println("\nTotal Cost = $" + totalCost);
        System.out.println("Segments to Buy:");
        for (int s = 0; s < segmentsUsed; s++) {
            int index = s + 1;
            Edge segment = usedSegments.get(s);

            // Segments store hub IDs, so look up the hub names (not station names)
            String hubName1 = (String) hubNames.get(segment.getStation1());
            String hubName2 = (String) hubNames.get(segment.getStation2());

            System.out.println(index + "( " + hubName1 + " - " + hubName2 + " ) - $" + segment.getCost());
        }
    }

    // === Helper methods ===

    /**
     * Updates bestCostMap with the minimum cost for a hub pair
     * @param bestCostMap Map storing the minimum cost per hub pair
     * @param pair pair of Hub IDs
     * @param cost cost of the current segment
     */
    private static void updateBestCost(HashMap<HubPair, Integer> bestCostMap, HubPair pair, int cost) {
        if (!bestCostMap.containsKey(pair)) {
            bestCostMap.put(pair, cost);
        } else {
            int existingCost = bestCostMap.get(pair);
            if (cost < existingCost) {
                bestCostMap.put(pair, cost);
            }
        }
    }
}

