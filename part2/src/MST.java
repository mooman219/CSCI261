
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
        public final ArrayList<Vertex> vertices = new ArrayList<Vertex>();

        public Node(int id) {
            this.id = id;
        }

        public void addNeighbor(Node target, int weight) {
            vertices.add(new Vertex(this, weight, target));
        }

        @Override
        public String toString() {
            String result = id + "-> ";
            for (Vertex vertex : vertices) {
                result += vertex.target.id + "(" + vertex.weight + ") ";
            }
            result += "\n";
            return result;
        }
    }

    public static class Vertex implements Comparable<Vertex> {

        public final Node source;
        public final Node target;
        public final int weight;

        public Vertex(Node source, int weight, Node target) {
            this.source = source;
            this.weight = weight;
            this.target = target;
        }

        @Override
        public int compareTo(Vertex o) {
            int result = this.weight - o.weight;
            if (result == 0) {
                result = this.source.id - o.source.id;
                if (result == 0) {
                    result = this.target.id - o.target.id;
                }
            }
            return result;
        }
    }

    public static class DFS {

        public final int nodesReached;
        public final Node[] predecessors;

        private DFS(int nodesReached, Node[] predecessors) {
            this.nodesReached = nodesReached;
            this.predecessors = predecessors;
        }

        public static final DFS search(Node[] list, int initial) {
            Node[] predcessors = new Node[list.length];
            int nodesReached = search(null, list[initial], predcessors, new HashSet<Node>());
            return new DFS(nodesReached, predcessors);
        }

        private static final int search(Node previous, Node current, Node[] predcessors, HashSet<Node> seen) {
            if (seen.contains(current)) {
                return 0;
            }
            seen.add(current);
            predcessors[current.id] = previous;
            int count = 1;
            for (Vertex vertex : current.vertices) {
                count += search(current, vertex.target, predcessors, seen);
            }
            return count;
        }

    }

    public static class Graph {

        public final int n;
        public final int seed;
        public final double p;
        public final int[][] matrix;
        public final Node[] list;
        public final long generationTime;
        public final DFS search;

        private Graph(int n, int seed, double p, int[][] matrix, Node[] list, long generationTime, DFS search) {
            this.n = n;
            this.seed = seed;
            this.p = p;
            this.matrix = matrix;
            this.list = list;
            this.generationTime = generationTime;
            this.search = search;
        }

        public static Graph generate(int n, int seed, double p) {
            Random randomA = new Random(seed);
            Random randomB = new Random(seed * 2);
            int[][] matrix = new int[n][n];
            Node[] list = new Node[n];
            DFS search;

            long time = System.currentTimeMillis();
            do {
                /**
                 * Generate the graph
                 */
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
                        }
                    }
                }
                /**
                 * DFS to ensure it's connected, restarting the process if it
                 * isn't.
                 */
                search = DFS.search(list, 0);
            } while (search.nodesReached != n);
            time = System.currentTimeMillis() - time;
            return new Graph(n, seed, p, matrix, list, time, search);
        }

        @Override
        public String toString() {
            String result = "TEST: n=" + n + ", seed=" + seed + ", p=" + p + "\n";
            result += "Time to generate the graph: " + generationTime + " milliseconds\n\n";
            if (n <= 10) {
                result += "The graph as an adjacency matrix:\n\n";
                for (int x = 0; x < n; x++) {
                    for (int y = 0; y < n; y++) {
                        result += " " + matrix[x][y] + "  ";
                    }
                    result += "\n\n";
                }
                result += "The graph as an adjacency list:\n";
                for (Node node : list) {
                    result += node.toString();
                }
                result += "\nDepth-First Search:\n";
                result += "Vertices:\n";
                for (int i = 0; i < n; i++) {
                    result += " " + i;
                }
                result += "\nPredecessors:\n";
                for (int i = 0; i < n; i++) {
                    result += search.predecessors[i] == null ? "-1" : " " + search.predecessors[i].id;
                }
            }
            return result;
        }
    }
}
