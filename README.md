# Paris Metro Network Optimizer 🚇

A Java graph-optimization project that condenses the Paris Metro network (**376 stations, 933 connections**) into **58 hub stations**, then uses **Kruskal's minimum spanning tree algorithm** with a **union-find (Partition) data structure** to find the cheapest set of line segments that keeps every hub connected.

Built for CSI2110 (Data Structures and Algorithms) at the University of Ottawa, Fall 2025.

---

## 🧩 The Problem

The project has two parts:

**Part A: Expensive Subway** (UVa Online Judge problem)
Given a set of stations and the cost of each connection, find the minimum total cost to connect every station, or report `Impossible` if the network can't be fully connected.
✅ Accepted by the UVa Online Judge within time limits.

**Part B: Paris Metro**
Apply the same idea to the real Paris Metro network:
1. **Find hub stations.** Stations joined by walking transfers (edges with cost `-1`) are grouped into a single hub, for example all the platforms at Châtelet.
2. **Build a simplified graph.** Walk along each metro line from every hub, through non-hub stations, until reaching another hub. Keep only the cheapest segment between each pair of hubs.
3. **Compute the MST.** Run Kruskal's algorithm on the hub graph to choose the cheapest segments that connect all 58 hubs.

## 📊 Result

```
Paris Metro Graph has 376 vertices and 933 edges.
Number of Hub Stations = 58 (total Hub Vertices = 138)
Number of Possible Segments = 57
Total Cost = $4464
Segments to Buy:
1( Jaurès - Stalingrad ) - $27
2( Michel Ange Auteuil - Michel Ange Molitor ) - $28
3( Denfert Rochereau - Raspail ) - $37
...
57( Michel Ange Auteuil - Trocadéro ) - $204
```

## ⚙️ How It Works

| Step | Technique |
|---|---|
| Group transfer stations into hubs | Union-find (Partition ADT), keeping clusters of size > 1 |
| Find hub-to-hub segments | Adjacency list + line walking, keeping the minimum cost per hub pair |
| Connect all hubs at minimum cost | Kruskal's algorithm: sort edges by cost, union only across different clusters |
| Detect a disconnected network | MST is valid only if it uses exactly `hubs − 1` edges, otherwise `Impossible` |

## 📁 Project Structure

```
src/
├── Main.java          # Part A: Expensive Subway (Kruskal's MST, Online Judge format)
├── ParisMetro.java    # Part B: hub detection, segment building, MST, output
├── Edge.java          # Weighted edge, sortable by cost
├── Station.java       # Station ID + name
├── Neighbor.java      # Adjacency list entry (station + cost)
├── HubPair.java       # Unordered hub pair, used as a HashMap key
├── Partition.java     # Union-find ADT  ┐
├── Cluster.java       # Partition set   ├─ provided by the course
└── Node.java          # Partition node  ┘
input/
├── metro.txt          # Paris Metro network (376 stations, 933 edges)
└── test1-6.txt        # Part A test cases
```

## ▶️ Running It

Requires Java 8+.

```bash
# Compile
javac -d out src/*.java

# Part A: Expensive Subway
java -cp out Main < input/test1.txt

# Part B: Paris Metro
java -cp out ParisMetro < input/metro.txt
```

## 🛠️ Tech

**Java** • Graph algorithms • Kruskal's MST • Union-find • HashMaps & adjacency lists

## 🙏 Credits

Starter code and the Partition ADT (`Partition`, `Cluster`, `Node`) were provided by the CSI2110 teaching team at the University of Ottawa. All other classes and the algorithm implementations are my own.
