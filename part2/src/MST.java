
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
            System.out.println(EdgeSortResult.doInsertionSort(graph).toString());
            System.out.println(EdgeSortResult.doCountSort(graph).toString());
            System.out.println(EdgeSortResult.doQuickSort(graph).toString());
        } catch (InputMismatchException ex) {
            System.out.println("n and seed must be integers");
        } catch (FileNotFoundException ex) {
            System.out.println("Input file not found");
        }
    }

    public static class Node {

        /**
         * The id of the node.
         */
        public final int id;
        /**
         * The list of edges that can be followed from this node.
         */
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

        /**
         * The source node of the edge.
         */
        public final Node source;
        /**
         * The target node of the edge.
         */
        public final Node target;
        /**
         * The weight of the edge.
         */
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

        /**
         * The number of nodes reached by the DFS during the search.
         */
        public final int nodesReached;
        /**
         * The predecessors list of nodes. If the element is null, then that
         * node does not have a predecessor.
         */
        public final Node[] predecessors;

        // Marked as private to encourage use of static construction method.
        private DFSResult(int nodesReached, Node[] predecessors) {
            this.nodesReached = nodesReached;
            this.predecessors = predecessors;
        }

        /**
         * Generates a DFS result based on the given adjacency list and starting
         * node.
         *
         * @param list the adjacency list of nodes in the graph.
         * @param initial the node index to start the search at.
         * @return the result of the search.
         */
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

    public static class EdgeSortResult {

        /**
         * The sorted list of edges.
         */
        public final Edge[] sortedList;
        /**
         * The sorting method used to sort the list.
         */
        public final EdgeSortResult.Type sortType;
        /**
         * The time taken to sort the list.
         */
        public final long searchTime;
        /**
         * The total weight of the sorted edges.
         */
        public final int totalWeight;

        // Marked as private to encourage use of static construction method.
        private EdgeSortResult(Edge[] sortedList, Type sortType, long searchTime) {
            // Given variables
            this.sortedList = sortedList;
            this.sortType = sortType;
            this.searchTime = searchTime;
            // Computed variables 
            int totalWeight = 0;
            for (int i = 0; i < sortedList.length; i++) {
                totalWeight += sortedList[i].weight;
            }
            this.totalWeight = totalWeight;
        }

        /**
         * Does an insertion sort on the given graph.
         *
         * @param input the given graph.
         * @return the result of the sort.
         */
        public static EdgeSortResult doInsertionSort(Graph input) {
            Edge[] sortedList = new Edge[input.edges.length];
            System.arraycopy(input.edges, 0, sortedList, 0, sortedList.length);
            long searchTime = System.currentTimeMillis();
            doInsertionSortInternal(sortedList);
            searchTime = System.currentTimeMillis() - searchTime;
            return new EdgeSortResult(sortedList, Type.INSERTION, searchTime);
        }

        private static <T extends Comparable<T>> void doInsertionSortInternal(T[] list) {
            T temp;
            for (int i = 1; i < list.length; i++) {
                for (int j = i; j > 0; j--) {
                    if (list[j].compareTo(list[j - 1]) < 0) {
                        temp = list[j];
                        list[j] = list[j - 1];
                        list[j - 1] = temp;
                    }
                }
            }
        }

        public static EdgeSortResult doCountSort(Graph input) {
            long searchTime = System.currentTimeMillis();
            Edge[] sortedList = doCountSortInternal(input.edges);
            searchTime = System.currentTimeMillis() - searchTime;
            return new EdgeSortResult(sortedList, Type.COUNT, searchTime);
        }

        private static Edge[] doCountSortInternal(Edge[] list) {
            int maxWeight = 0;
            for (int i = 0; i < list.length; i++) {
                maxWeight = maxWeight < list[i].weight ? list[i].weight : maxWeight;
            }
            maxWeight += 1;
            int c[] = new int[maxWeight];
            for (int i = 0; i < list.length; i++) {
                c[list[i].weight]++;
            }
            for (int i = 1; i < maxWeight; i++) {
                c[i] += c[i - 1];
            }
            Edge b[] = new Edge[list.length];
            for (int i = list.length - 1; i >= 0; i--) {
                b[--c[list[i].weight]] = list[i];
            }
            return b;
        }

        public static EdgeSortResult doQuickSort(Graph input) {
            Edge[] sortedList = new Edge[input.edges.length];
            System.arraycopy(input.edges, 0, sortedList, 0, sortedList.length);
            long searchTime = System.currentTimeMillis();
            doQuickSortInternal(sortedList, 0, sortedList.length - 1);
            searchTime = System.currentTimeMillis() - searchTime;
            return new EdgeSortResult(sortedList, Type.QUICK, searchTime);
        }

        private static <T extends Comparable<T>> void doQuickSortInternal(T[] list, int lowerIndex, int higherIndex) {
            int i = lowerIndex;
            int j = higherIndex;
            T temp;
            T pivot = list[lowerIndex + (higherIndex - lowerIndex) / 2];
            while (i <= j) {
                while (list[i].compareTo(pivot) < 0) {
                    i++;
                }
                while (list[j].compareTo(pivot) > 0) {
                    j--;
                }
                if (i <= j) {
                    temp = list[i];
                    list[i] = list[j];
                    list[j] = temp;
                    i++;
                    j--;
                }
            }
            if (lowerIndex < j) {
                doQuickSortInternal(list, lowerIndex, j);
            }
            if (i < higherIndex) {
                doQuickSortInternal(list, i, higherIndex);
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
            result += "Runtime: " + searchTime + " milliseconds\n";
            return result;
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

    }

    public static class Graph {

        public final int n;
        public final int seed;
        public final double p;
        public final int totalWeight;
        public final long generationTime;
        public final Edge[] edges;
        public final int[][] adjacencyMatrix;
        public final Node[] adjacencyList;
        public final DFSResult searchResult;

        // Marked as private to encourage use of static construction method.
        private Graph(int n, int seed, double p, int totalWeight, long generationTime, Edge[] edges, int[][] adjacencyMatrix, Node[] adjacencyList, DFSResult searchResult) {
            this.n = n;
            this.seed = seed;
            this.p = p;
            this.totalWeight = totalWeight;
            this.generationTime = generationTime;
            this.edges = edges;
            this.adjacencyMatrix = adjacencyMatrix;
            this.adjacencyList = adjacencyList;
            this.searchResult = searchResult;
        }

        /**
         * Generates a graph based on the given parameters.
         *
         * @param n the length and width of the graph.
         * @param seed the seed for the random.
         * @param p the probability for an edge to be made between two nodes.
         * @return the generated graph.
         */
        public static Graph generate(int n, int seed, double p) {
            Random randomA = new Random(seed);
            Random randomB = new Random(seed * 2);
            int[][] matrix = new int[n][n];
            Node[] list = new Node[n];
            DFSResult searchResult;
            int edgeCount;
            int totalWeight;

            long generationTime = System.currentTimeMillis();
            do {
                /**
                 * Generate the graph
                 */
                edgeCount = 0;
                totalWeight = 0;
                for (int i = 0; i < n; i++) {
                    list[i] = new Node(i);
                }
                for (int x = 0; x < n; x++) {
                    for (int y = x + 1; y < n; y++) {
                        matrix[x][y] = 0;
                        matrix[y][x] = 0;
                        if (randomA.nextDouble() <= p) {
                            int weight = randomB.nextInt(n) + 1;
                            totalWeight += weight;
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
            generationTime = System.currentTimeMillis() - generationTime;
            /**
             * Get the edges and total weight of the created graph
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
            return new Graph(n, seed, p, totalWeight, generationTime, edges, matrix, list, searchResult);
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
