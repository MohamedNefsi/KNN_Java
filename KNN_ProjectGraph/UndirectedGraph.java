/* Mohamed Nefsi
student number :300305042
*/


/* ---------------------------------------------------------------------------------
The UndirectedGraph class that uses a HashMap to build an undirected graph
representation based on an adjacency list. 
A vertex in the graph is of type T.
There is no weight associated with the edges.

(c) Robert Laganiere, CSI2510 2023
------------------------------------------------------------------------------------*/

import java.util.*;

class UndirectedGraph<T> {
    private Map<T, LinkedList<T>> adjacencyList;
    // Constructs an empty graph
    public UndirectedGraph() {
        adjacencyList = new HashMap<>();
    }
    // adds a vertex to the graph with an empty adjacency list
    public void addVertex(T vertex) {
        if (!adjacencyList.containsKey(vertex)) {
            adjacencyList.put(vertex, new LinkedList<>());
        }
    }
    // adds an edge between vertex1 and vertex 2.
// the two adjacency lists are updated
    public void addEdge(T vertex1, T vertex2) {
        addVertex(vertex1);
        addVertex(vertex2);
        adjacencyList.get(vertex1).add(vertex2);
        adjacencyList.get(vertex2).add(vertex1);
    }
    // gets the list of all adjacency vertices of a vertex
    public List<T> getNeighbors(T vertex) {
        return adjacencyList.
                getOrDefault(vertex, new LinkedList<>());
    }
    // calculates the size (number of vertices) of the graph
    public int size() {
        return adjacencyList.size();
    }

    public T getRandomVertex() {
        int graphSize = adjacencyList.size();
        List<T> vertices = new ArrayList<>(adjacencyList.keySet());
        if (graphSize == 0) {
            // graph is empty
            return null;
        }

        int randomIndex = new Random().nextInt(graphSize);
        return vertices.get(randomIndex);
        //return getVertexAtIndex(randomIndex);
    }

    public Set<T> getVertices() {
        return adjacencyList.keySet();
    }

}
