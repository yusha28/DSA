import java.util.ArrayList;

import java.util.Arrays;

import java.util.Collections;

import java.util.List;

import java.util.NoSuchElementException;

 

 class Edge { // used for storing information of edges

        int source, destination, weight;

   

        Edge(int source, int destination, int weight) {

            this.source = source;

            this.destination = destination;

            this.weight = weight;

        }

    }

    class BellmanFord {

    private int vertices;

    private List<Edge> edgeList;

 

    BellmanFord(int vertices, int edges) {

        this.vertices = vertices;

        this.edgeList = new ArrayList<>();

    }

 

    void addEdge(int source, int destination, int weight) {

        edgeList.add(new Edge(source, destination, weight));

    }

 

    void bellmanFord(int source) {

        int[] distance = new int[vertices];

        Arrays.fill(distance, Integer.MAX_VALUE);

        distance[source] = 0;

 

        // Relaxation step: Run (V-1) iterations to find shortest paths

        for (int i = 1; i < vertices; ++i) {

            for (Edge edge : edgeList) {

                int u = edge.source;

                int v = edge.destination;

                int w = edge.weight;

                if (distance[u] != Integer.MAX_VALUE && distance[u] + w < distance[v]) {

                    distance[v] = distance[u] + w;

                }

            }

        }

       

        // Check for negative cycles

        for (Edge edge : edgeList) {

            int u = edge.source;

            int v = edge.destination;

            int w = edge.weight;

            if (distance[u] != Integer.MAX_VALUE && distance[u] + w < distance[v]) {

                System.out.println("Negative cycle detected!");

                return;

            }

        }

       

        // Printing shortest distances

        System.out.println("Vertex\tDistance");

        for (int i = 0; i < vertices; ++i) {

            System.out.println(i + "\t\t" + distance[i]);

        }

    }

}

// MaxHeapPriorityQueue implementation

class MaxHeapPriorityQueue {

    private List<Integer> heap;

 

    MaxHeapPriorityQueue() {

        heap = new ArrayList<>();

    }

 

    void insert(int value) {

        heap.add(value);

        int currentIndex = heap.size() - 1;

        int parentIndex = (currentIndex - 1) / 2;

        // Maintain max-heap property by swapping with parent

        while (currentIndex > 0 && heap.get(currentIndex) > heap.get(parentIndex)) {

            Collections.swap(heap, currentIndex, parentIndex);

            currentIndex = parentIndex;

            parentIndex = (currentIndex - 1) / 2;

        }

    }

 

    int extractMax() {

        if (isEmpty()) {

            throw new NoSuchElementException("Priority queue is empty.");

        }

 

        int maxValue = heap.get(0);

        int lastIndex = heap.size() - 1;

        heap.set(0, heap.get(lastIndex));

        heap.remove(lastIndex);

 

        int currentIndex = 0;

        int leftChildIndex = 1;

        int rightChildIndex = 2;

        // Restore max-heap property after removing root

        while (leftChildIndex < heap.size()) {

            int largestIndex = currentIndex;

 

            if (heap.get(leftChildIndex) > heap.get(largestIndex)) {

                largestIndex = leftChildIndex;

            }

            if (rightChildIndex < heap.size() && heap.get(rightChildIndex) > heap.get(largestIndex)) {

                largestIndex = rightChildIndex;

            }

 

            if (largestIndex == currentIndex) {

                break;

            }

 

            Collections.swap(heap, currentIndex, largestIndex);

            currentIndex = largestIndex;

            leftChildIndex = 2 * currentIndex + 1;

            rightChildIndex = 2 * currentIndex + 2;

        }

 

        return maxValue;

    }

 

    boolean isEmpty() {

        return heap.isEmpty();

    }

}

public class BelmanandHeap {

   

 public static void main(String[] args) {

    int vertices = 5;

    int edges = 8;

 

    BellmanFord bellmanFord = new BellmanFord(vertices, edges);

    bellmanFord.addEdge(0, 1, -1);

    bellmanFord.addEdge(0, 2, 4);

    bellmanFord.addEdge(1, 2, 3);

    bellmanFord.addEdge(1, 3, 2);

    bellmanFord.addEdge(1, 4, 2);

    bellmanFord.addEdge(3, 2, 5);

    bellmanFord.addEdge(3, 1, 1);

    bellmanFord.addEdge(4, 3, -3);

 

    int sourceVertex = 0;

    bellmanFord.bellmanFord(sourceVertex);

 

    MaxHeapPriorityQueue maxHeap = new MaxHeapPriorityQueue();

    maxHeap.insert(5);

    maxHeap.insert(10);

    maxHeap.insert(2);

    maxHeap.insert(8);

    maxHeap.insert(1);

 

    System.out.println("Max heap elements:");

    while (!maxHeap.isEmpty()) {

        System.out.println(maxHeap.extractMax());

    }

 }

 

}