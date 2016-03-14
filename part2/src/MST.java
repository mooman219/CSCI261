
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;

/**
 * @author Joseph Cumbo (jwc6999)
 */
public class MST {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Input file not found");
            return;
        }
        try {
            Scanner reader = new Scanner(new File(args[0]));
            int n = reader.nextInt();
            int seed = reader.nextInt();
            double p = reader.nextDouble();
            if (n < 2) {
                System.out.println("n must be greater than 1");
                return;
            } else if (!Double.isFinite(p)) {
                System.out.println("p must be a real number");
                return;
            } else if (p < 0 || p > 1) {
                System.out.println("p must be between 0 and 1");
                return;
            }
            Graph graph = Graph.generate(n, seed, p);
            System.out.println(graph.toString());
        } catch (InputMismatchException ex) {
            System.out.println("n and seed must be integers");
        } catch (FileNotFoundException ex) {
            System.out.println("Input file not found");
        }
    }

    public static class Node {

        public final int id;
        public final ArrayList<Edge> edges = new ArrayList<Edge>();

        public Node(int id) {
            this.id = id;
        }

        public void addNeighbor(Node target, int weight) {
            edges.add(new Edge(this, target, weight));
        }

        @Override
        public String toString() {
            String result = id + "-> ";
            for (Edge vertex : edges) {
                result += vertex.target.id + "(" + vertex.weight + ") ";
            }
            return result;
        }
    }

    public static class Edge implements Comparable<Edge> {

        public final Node source;
        public final Node target;
        public final int weight;

        public Edge(Node source, Node target, int weight) {
            this.source = source;
            this.target = target;
            this.weight = weight;
        }

        @Override
        public int compareTo(Edge o) {
            int result = this.weight - o.weight;
            if (result == 0) {
                result = this.source.id - o.source.id;
                if (result == 0) {
                    result = this.target.id - o.target.id;
                }
            }
            return result;
        }

        @Override
        public String toString() {
            return source.id + " " + target.id + " weight = " + weight;
        }
    }

    public static class DFSResult {

        public final int nodesReached;
        public final Node[] predecessors;

        private DFSResult(int nodesReached, Node[] predecessors) {
            this.nodesReached = nodesReached;
            this.predecessors = predecessors;
        }

        public static final DFSResult search(Node[] list, int initial) {
            Node[] predcessors = new Node[list.length];
            int nodesReached = search(null, list[initial], predcessors, new HashSet<Node>());
            return new DFSResult(nodesReached, predcessors);
        }

        private static final int search(Node previous, Node current, Node[] predcessors, HashSet<Node> seen) {
            if (seen.contains(current)) {
                return 0;
            }
            seen.add(current);
            predcessors[current.id] = previous;
            int count = 1;
            for (Edge vertex : current.edges) {
                count += search(current, vertex.target, predcessors, seen);
            }
            return count;
        }

    }

    public static class EdgeSort {

        public final Edge[] sortedList;
        public final EdgeSort.Type sortType;
        public final int totalWeight;
        public final long searchTime;

        public EdgeSort(Edge[] sortedList, Type sortType, int totalWeight, long searchTime) {
            this.sortedList = sortedList;
            this.sortType = sortType;
            this.totalWeight = totalWeight;
            this.searchTime = searchTime;
        }

        public static enum Type {

            INSERTION("INSERTION SORT"),
            COUNT("COUNT SORT"),
            QUICK("QUICKSORT");

            public final String name;

            private Type(String name) {
                this.name = name;
            }
        }

        @Override
        public String toString() {
            String result = "===================================\n";
            result += "SORTED EDGES USING " + sortType.name + "\n";
            for (int i = 0; i < sortedList.length; i++) {
                result += sortedList[i].toString() + "\n";
            }
            result += "Total Weight = " + totalWeight + "\n";
            result += "Runtime: " + searchTime + "\n";
            return result;
        }

    }

    public static class Graph {

        public final int n;
        public final int seed;
        public final double p;
        public final int[][] adjacencyMatrix;
        public final Edge[] edges;
        public final Node[] adjacencyList;
        public final long generationTime;
        public final DFSResult searchResult;

        public Graph(int n, int seed, double p, int[][] adjacencyMatrix, Edge[] edges, Node[] adjacencyList, long generationTime, DFSResult searchResult) {
            this.n = n;
            this.seed = seed;
            this.p = p;
            this.adjacencyMatrix = adjacencyMatrix;
            this.edges = edges;
            this.adjacencyList = adjacencyList;
            this.generationTime = generationTime;
            this.searchResult = searchResult;
        }

        public static Graph generate(int n, int seed, double p) {
            Random randomA = new Random(seed);
            Random randomB = new Random(seed * 2);
            int[][] matrix = new int[n][n];
            Node[] list = new Node[n];
            DFSResult searchResult;

            int edgeCount;
            long time = System.currentTimeMillis();
            do {
                /**
                 * Generate the graph
                 */
                edgeCount = 0;
                for (int i = 0; i < n; i++) {
                    list[i] = new Node(i);
                }
                for (int x = 0; x < n; x++) {
                    for (int y = x + 1; y < n; y++) {
                        matrix[x][y] = 0;
                        matrix[y][x] = 0;
                        if (randomA.nextDouble() <= p) {
                            int weight = randomB.nextInt(n) + 1;
                            matrix[x][y] = weight;
                            matrix[y][x] = weight;
                            list[x].addNeighbor(list[y], weight);
                            list[y].addNeighbor(list[x], weight);
                            edgeCount++;
                        }
                    }
                }
                /**
                 * DFS to ensure it's connected, restarting the process if it
                 * isn't.
                 */
                searchResult = DFSResult.search(list, 0);
            } while (searchResult.nodesReached != n);
            time = System.currentTimeMillis() - time;
            /**
             * Generate the edges for the lower half of the matrix
             */
            Edge[] edges = new Edge[edgeCount];
            edgeCount = 0;
            for (int x = 0; x < n; x++) {
                for (int y = x + 1; y < n; y++) {
                    if (matrix[x][y] != 0) {
                        edges[edgeCount++] = new Edge(list[x], list[y], matrix[x][y]);
                    }
                }
            }
            return new Graph(n, seed, p, matrix, edges, list, time, searchResult);
        }

        @Override
        public String toString() {
            String result = "TEST: n=" + n + ", seed=" + seed + ", p=" + p + "\n";
            result += "Time to generate the graph: " + generationTime + " milliseconds\n\n";
            if (n <= 10) {
                result += "The graph as an adjacency matrix:\n\n";
                for (int x = 0; x < n; x++) {
                    for (int y = 0; y < n; y++) {
                        result += " " + adjacencyMatrix[x][y] + "  ";
                    }
                    result += "\n\n";
                }
                result += "The graph as an adjacency list:\n";
                for (Node node : adjacencyList) {
                    result += node.toString() + "\n";
                }
                result += "\nDepth-First Search:\n";
                result += "Vertices:\n";
                for (int i = 0; i < n; i++) {
                    result += " " + i;
                }
                result += "\nPredecessors:\n";
                for (int i = 0; i < n; i++) {
                    result += searchResult.predecessors[i] == null ? "-1" : " " + searchResult.predecessors[i].id;
                }
            }
            return result;
        }
    }
}
