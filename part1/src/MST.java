
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;

/**
 * @author Joseph Cumbo (jwc6999)
 */
public class MST {

    public static void main(String[] args) {
        try {
            Scanner reader = new Scanner(new File("input1"));
            Graph graph = Graph.generate(reader.nextInt(), reader.nextInt(), reader.nextDouble());
            System.out.println(graph.toString());
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

    public static class Vertex {

        public final Node source;
        public final int weight;
        public final Node target;

        public Vertex(Node source, int weight, Node target) {
            this.source = source;
            this.weight = weight;
            this.target = target;
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
            int nodesReached = search(null, list[0], predcessors, new HashSet<Node>());
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
                for (int i = 0; i < n; i++) {
                    list[i] = new Node(i);
                }
                for (int x = 0; x < n; x++) {
                    for (int y = x + 1; y < n; y++) {
                        if (randomA.nextDouble() <= p) {
                            int weight = randomB.nextInt(n) + 1;
                            matrix[x][y] = weight;
                            matrix[y][x] = weight;
                            list[x].addNeighbor(list[y], weight);
                            list[y].addNeighbor(list[x], weight);
                        }
                    }
                }
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


/* Example output
TEST: n=7, seed=100000, p=0.5
Time to generate the graph: 4 milliseconds

The graph as an adjacency matrix:

 0   0   0   2   5   0   0  

 0   0   0   0   2   4   5  

 0   0   0   1   0   0   0  

 2   0   1   0   4   7   0  

 5   2   0   4   0   0   0  

 0   4   0   7   0   0   5  

 0   5   0   0   0   5   0 

The graph as an adjacency list:
0-> 3(2) 4(5) 
1-> 4(2) 5(4) 6(5) 
2-> 3(1) 
3-> 0(2) 2(1) 4(4) 5(7) 
4-> 0(5) 1(2) 3(4) 
5-> 1(4) 3(7) 6(5) 
6-> 1(5) 5(5) 
            
Depth-First Search:
Vertices:  
 0 1 2 3 4 5 6
Predecessors: 
-1 4 3 0 3 1 5 
 */
