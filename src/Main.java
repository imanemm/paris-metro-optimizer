import java.util.*;

/**
 * Expensive Subway problem (P2-part A)
 * Startup code given in the Fall 2025 for csi2110/csi2510
 *
 * Reads the input, converts station names to IDs, builds the edge list,
 * and runs my MST method to print the minimum cost or "Impossible".
 *
 * @author Imane Moussa Marou
 */
class Main {

    public static void main (String[] args) {


        Scanner scanner = new Scanner(System.in); // Note that for Online Judge the input must be given in the standard I/O
        // to read from input files use the command 'java Main < test1.txt', test1.txt should be in the same folder


        int nbOfStations, nbOfEdges;

        // This loop repeats for each problem that is included in the same input file (until 0 0 is read)
        while (true) {
            // read number of stations
            nbOfStations= scanner.nextInt();
            // read number of edges
            nbOfEdges= scanner.nextInt();

            //System.out.println(nbOfStations +" " + nbOfEdges);

            // if both are zero it is indication to stop
            if ((nbOfStations==0) & (nbOfEdges==0)) break;

            // Creating a map with the stations and creating the stations unique IDs
            HashMap<String, Integer> vertexNames = new HashMap<>();
            for (int s = 0; s < nbOfStations; s++) {
                // here a vertex name is read
                String stationName = scanner.next();

                vertexNames.put(stationName, s);
                //System.out.println(stationName + "'s ID: " + s);
            }

            ArrayList<Edge> edgeList = new ArrayList<>();

            for (int e=0; e<nbOfEdges; e++) {
                // a station name that is one end of the edge
                String s1Name = scanner.next();
                // a station name that is the other end of the edge
                String s2Name = scanner.next();
                // edge cost (weight) is given
                int cost = scanner.nextInt();

                //System.out.println(s1Name+" "+s2Name+" "+cost);

                // Creating an edge using the stations ID and the edge's cost
                Edge newEdge = new Edge(vertexNames.get(s1Name), vertexNames.get(s2Name), cost);

                // Adding the edge to the list of edges
                edgeList.add(newEdge);
            }

            // here is the name of the vertex of Peter's home
            String home=scanner.next();
            int homeID = vertexNames.get(home);
            // System.out.println(home);

            // Runs the Kruskal's algorithm and storing the total cost and the number of edges used
            // resultKruskal[0] represents the total cost
            // resultKruskal[1] represents the number of edges used
            int[] resultKruskal = runKruskal(edgeList, nbOfStations);
            //System.out.println("Total cost: " + resultKruskal[0]);
            //System.out.println("Number of edges used: " + resultKruskal[1]);

            // Decides the outcome of the Kruskal result
            if (resultKruskal[1] == nbOfStations - 1) {
                System.out.println(resultKruskal[0]);
            } else {
                System.out.println("Impossible");
            }

        }


    }


    // === Helper methods ===

    /**
     * Runs the Kruskal's algorithm to compute the MST.
     * Sorts the edges by cost and picks the cheapest ones that connect the network.
     * @param edgeList list of all the edges
     * @param nbOfStations total number of stations
     * @return a size two array containing the total cost and the number of edges used.
     */
    private static int[] runKruskal(ArrayList<Edge> edgeList, int nbOfStations) {
        int totalCost = 0;
        int edgesUsed = 0;
        ArrayList<Node<Integer>> nodeList = new ArrayList<>();
        int[] resultKruskal = new int[2];

        // Creating a Partition for all the stations IDs
        Partition<Integer> partition = new Partition<>();

        // Creating singleton cluster for each station's ID
        for (int s = 0; s < nbOfStations; s++) {
            // Storing the returned node in a list.
            nodeList.add(partition.makeCluster(s));
        }

        // Sorting the edge list from lowest to highest cost per edge.
        Collections.sort(edgeList);

        for (Edge edge : edgeList) {
            int station1 = edge.getStation1();
            int station2 = edge.getStation2();

            if (partition.find(nodeList.get(station1)) != partition.find(nodeList.get(station2))) {
                partition.union(nodeList.get(station1), nodeList.get(station2));
                totalCost += edge.getCost();
                edgesUsed += 1;
            }
        }

        // Storing the Kruskal's result in an array
        resultKruskal[0] = totalCost;
        resultKruskal[1] = edgesUsed;

        return resultKruskal;
    }

}

