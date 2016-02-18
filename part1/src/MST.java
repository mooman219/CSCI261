
import java.util.ArrayList;
import java.util.Random;

/**
 * @author Joseph Cumbo (jwc6999)
 */
public class MST {

    public static void main(String[] args) {
        System.out.println(new Graph(7, 100000, 0.5).toString());
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

    public static class Graph {

        public final int n;
        public final int seed;
        public final double p;
        private final int[][] matrix;
        private final Node[] list;
        private long generationTime;

        public Graph(int n, int seed, double p) {
            this.n = n;
            this.seed = seed;
            this.p = p;
            this.matrix = new int[n][n];
            this.list = new Node[n];
            this.regenerate();
        }

        public final void regenerate() {
            Random randomA = new Random(seed);
            Random randomB = new Random(seed * 2);
            this.generationTime = System.currentTimeMillis();
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
            this.generationTime = System.currentTimeMillis() - this.generationTime;
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
