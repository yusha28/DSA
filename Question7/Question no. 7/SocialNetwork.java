
import java.util.ArrayList;

import java.util.HashMap;

import java.util.List;

import java.util.Map;

import javax.swing.*;

import java.awt.*;

import java.awt.event.*;

// import java.util.*;

import java.io.BufferedReader;

import java.io.FileReader;

import java.io.IOException;

 

public class SocialNetwork {

 

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            JFrame frame = new JFrame("Social Network Graph");

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

 

            GraphPanel graphPanel = new GraphPanel();

 

            JButton addNodeButton = new JButton("Add Node");

            addNodeButton.addActionListener(e -> graphPanel.addNode());

 

            JButton addEdgeButton = new JButton("Add Edge");

            addEdgeButton.addActionListener(e -> graphPanel.setEdgeMode(true));

 

            JButton deleteButton = new JButton("Delete");

            deleteButton.addActionListener(e -> graphPanel.setDeleteMode(true));

 

            JTextField searchField = new JTextField(15);

            searchField.addKeyListener(new KeyAdapter() {

                @Override

                public void keyReleased(KeyEvent e) {

                    graphPanel.searchUser(searchField.getText());

                }

            });

 

            JPanel toolbarPanel = new JPanel();

            toolbarPanel.add(addNodeButton);

            toolbarPanel.add(addEdgeButton);

            toolbarPanel.add(deleteButton);

            toolbarPanel.add(new JLabel("Search User:"));

            toolbarPanel.add(searchField);

 

            frame.getContentPane().add(toolbarPanel, BorderLayout.NORTH);

            frame.getContentPane().add(graphPanel, BorderLayout.CENTER);

 

            frame.setSize(800, 600);

            frame.setVisible(true);

        });

    }

 

    static class GraphPanel extends JPanel {

        private List<Node> nodes;

        private List<Edge> edges;

        private Node selectedNode;

        private boolean deleteMode;

        private Edge selectedEdge;

        private boolean edgeMode;

        private Node startNodeForEdge;

        private Node endNodeForEdge;

        private Map<Node, Integer> followersMap;

        private Map<Node, Point> nodePositions;

        private Map<Edge, Point[]> edgePositions;

 

        public GraphPanel() {

            nodes = new ArrayList<>();

            edges = new ArrayList<>();

            followersMap = new HashMap<>();

            nodePositions = new HashMap<>();

            edgePositions = new HashMap<>();

 

            loadGraphData("yusha.txt"); // Load user data from file

            addMouseListener(new MouseAdapter() {

                @Override

                public void mousePressed(MouseEvent e) {

                    if (deleteMode) {

                        deleteNodeOrEdge(e.getX(), e.getY());

                    }

                    if (edgeMode) {

                        if (deleteMode) {

                        deleteMode = false;

                        }

                        if (startNodeForEdge == null) {

                            selectStartNodeForEdge(e.getX(), e.getY());

                        } else if (endNodeForEdge == null) {

                            selectEndNodeForEdge(e.getX(), e.getY());

                            establishConnection();

                        }      

                    } else {

                        selectNode(e.getX(), e.getY());

                    }

                }

            });

 

            addMouseMotionListener(new MouseAdapter() {

                @Override

                public void mouseDragged(MouseEvent e) {

                    moveSelected(e.getX(), e.getY());

                }

            });

        }

        private void deleteNodeOrEdge(int x, int y) {

            Node nodeToDelete = null;

            Edge edgeToDelete = null;

       

            // Check if a node is clicked

            for (Node node : nodes) {

                Point position = nodePositions.get(node);

                int nodeX = position.x;

                int nodeY = position.y;

       

                if (x >= nodeX - 20 && x <= nodeX + 20 && y >= nodeY - 20 && y <= nodeY + 20) {

                    nodeToDelete = node;

                    break;

                }

            }

       

            // Check if an edge is clicked

            if (nodeToDelete == null) {

                for (Edge edge : edges) {

                    Point[] points = edgePositions.get(edge);

                    if (points != null) {

                        int startX = points[0].x;

                        int startY = points[0].y;

                        int endX = points[1].x;

                        int endY = points[1].y;

       

                        if (x >= startX && x <= endX && y >= Math.min(startY, endY) && y <= Math.max(startY, endY)) {

                            edgeToDelete = edge;

                            break;

                        }

                    }

                }

            }

       

            if (nodeToDelete != null) {

                deleteNode(nodeToDelete);

            } else if (edgeToDelete != null) {

                deleteEdge(edgeToDelete);

            }

       

            repaint();

        }

        private void deleteNode(Node node) {

            nodes.remove(node);

            followersMap.remove(node);

            nodePositions.remove(node);

       

            // Remove connected edges

            List<Edge> edgesToRemove = new ArrayList<>();

            for (Edge edge : edges) {

                if (edge.getStartNode() == node || edge.getEndNode() == node) {

                    edgesToRemove.add(edge);

                }

            }

              // Update followers count for connected nodes and remove edges

            for (Edge edge : edgesToRemove) {

                Node otherNode = edge.getStartNode() == node ? edge.getEndNode() : edge.getStartNode();

                followersMap.put(otherNode, followersMap.getOrDefault(otherNode, 0) - 1);

                edgePositions.remove(edge);

            }

            edges.removeAll(edgesToRemove);

            for (Edge edge : edgesToRemove) {

                edgePositions.remove(edge);

            }

        }

       

        private void deleteEdge(Edge edge) {

            edges.remove(edge);

            edgePositions.remove(edge);

        }

               

 

        private void loadGraphData(String fileName) {

            try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {

                String line;

                Map<Integer, Node> idToNodeMap = new HashMap<>(); // Map to store nodes by ID

                while ((line = br.readLine()) != null) {

                    String[] parts = line.split(",");

                    if (parts.length == 2) {

                        Integer sourceId = Integer.parseInt(parts[0]);

                        Integer targetId = Integer.parseInt(parts[1]);

                        // Retrieve or create nodes using their IDs

                        Node sourceNode = null;

                        Node targetNode = null;

                        if(idToNodeMap.containsKey(sourceId)){

                            sourceNode = idToNodeMap.get(sourceId);

                        }

                        else{

                            sourceNode = createNewNode(sourceId);

                        }

                        if(idToNodeMap.containsKey(targetId)){

                            targetNode = idToNodeMap.get(sourceId);

                        }

                        else{

                            targetNode = createNewNode(targetId);

                        }

//                        Node sourceNode = idToNodeMap.getOrDefault(sourceId, createNewNode(sourceId));

//                        Node targetNode = idToNodeMap.getOrDefault(targetId, createNewNode(targetId));

                        idToNodeMap.put(sourceId, sourceNode);

                        idToNodeMap.put(targetId, targetNode);

                        // If both nodes are found, add an edge

                        if (sourceNode != null && targetNode != null) {

                            Edge newEdge = new Edge(sourceNode, targetNode);

                            edges.add(newEdge);

                            edgePositions.put(newEdge, new Point[]{nodePositions.get(sourceNode), nodePositions.get(targetNode)});

                           

                            // Increment followers count for connected nodes

                            followersMap.put(sourceNode, followersMap.getOrDefault(sourceNode, 0) + 1);

                            followersMap.put(targetNode, followersMap.getOrDefault(targetNode, 0) + 1);

                        }

                    }

                }

            } catch (IOException e) {

                e.printStackTrace();

            }

        }

       

        // Helper method to create a new node with a given ID and random position

        private Node createNewNode(int id) {

            int x = (int) (Math.random() * 800);

            int y = (int) (Math.random() * 600);

            Node newNode = new Node(id, x, y);

            nodes.add(newNode);

            nodePositions.put(newNode, new Point(x, y));

            return newNode;

        }

       

       

 

        public void setDeleteMode(boolean deleteMode) {

            this.deleteMode = deleteMode;

            selectedNode = null;

            selectedEdge = null;

            repaint();

        }

 

 

 

        public void setEdgeMode(boolean edgeMode) {

            this.edgeMode = edgeMode;

            if (!edgeMode) {

                resetEdgeMode();

            }

            repaint();

        }

 

        private void resetEdgeMode() {

            startNodeForEdge = null;

            endNodeForEdge = null;

            repaint();

        }

 

        @Override

        protected void paintComponent(Graphics g) {

            super.paintComponent(g);

 

            // Draw edges

            for (Edge edge : edges) {

                Point[] points = edgePositions.get(edge);

                if (points != null) {

                    g.setColor(Color.BLUE);

                    g.drawLine(points[0].x, points[0].y, points[1].x, points[1].y);

                }

            }

 

            // Draw nodes

            for (Node node : nodes) {

                Point position = nodePositions.get(node);

                int x = position.x;

                int y = position.y;

 

                g.setColor(Color.RED);

                g.fillOval(x - 20, y - 20, 40, 40);

                g.setColor(Color.BLACK);

                g.drawString("User " + node.getId(), x - 20, y + 30);

                g.drawString("Followers: " + followersMap.getOrDefault(node, 0), x - 20, y + 45);

 

                if (node == selectedNode) {

                    g.setColor(Color.GREEN);

                    g.drawRect(x - 25, y - 25, 50, 50);

                }

            }

 

            // Highlight the selected nodes for creating an edge

            if (edgeMode) {

                if (startNodeForEdge != null) {

                    Point position = nodePositions.get(startNodeForEdge);

                    int x = position.x;

                    int y = position.y;

                    g.setColor(Color.GREEN);

                    g.drawRect(x - 25, y - 25, 50, 50);

                }

                if (endNodeForEdge != null) {

                    Point position = nodePositions.get(endNodeForEdge);

                    int x = position.x;

                    int y = position.y;

                    g.setColor(Color.RED);

                    g.drawRect(x - 25, y - 25, 50, 50);

                }

            }

 

            // Highlight the selected edge

            if (selectedEdge != null) {

                Point[] points = edgePositions.get(selectedEdge);

                if (points != null) {

                    g.setColor(Color.YELLOW);

                    g.drawLine(points[0].x, points[0].y, points[1].x, points[1].y);

                }

            }

        }

 

        public void addNode() {

            // Generate random coordinates for the new node

            int x = (int) (Math.random() * getWidth());

            int y = (int) (Math.random() * getHeight());

 

            // Add the new node to the list

            int id = nodes.size() + 1;

            Node newNode = new Node(id, x, y);

            nodes.add(newNode);

            nodePositions.put(newNode, new Point(x, y));

 

            // Repaint the panel to update the graph

            repaint();

        }

 

 

        public void deleteSelected() {

            if (selectedNode != null) {

                // Remove the edges connected to the selected node

                List<Edge> edgesToRemove = new ArrayList<>();

                for (Edge edge : edges) {

                    if (edge.getStartNode() == selectedNode || edge.getEndNode() == selectedNode) {

                        edgesToRemove.add(edge);

                    }

                }

       

                // Update followers count for connected nodes and remove edges

                for (Edge edge : edgesToRemove) {

                    Node startNode = edge.getStartNode();

                    Node endNode = edge.getEndNode();

                    followersMap.put(startNode, followersMap.getOrDefault(startNode, 0) - 1);

                    followersMap.put(endNode, followersMap.getOrDefault(endNode, 0) - 1);

                    edgePositions.remove(edge); // Remove edge positions mapping

                }

       

                // Remove the selected node and its associated edges

                nodes.removeIf(node -> node.getId() == selectedNode.getId());

                followersMap.remove(selectedNode);

                nodePositions.remove(selectedNode);

                edges.removeAll(edgesToRemove);

       

                selectedNode = null;

                selectedEdge = null;

       

                // Repaint the panel to update the graph

                repaint();

            } else if (selectedEdge != null) {

                // Update followers count for connected nodes

                Node startNode = selectedEdge.getStartNode();

                Node endNode = selectedEdge.getEndNode();

                followersMap.put(startNode, followersMap.getOrDefault(startNode, 0) - 1);

                followersMap.put(endNode, followersMap.getOrDefault(endNode, 0) - 1);

       

                // Remove the selected edge and the unconnected node

                edges.remove(selectedEdge);

                edgePositions.remove(selectedEdge);

       

                Node unconnectedNode = (startNode.getX() == endNode.getX() && startNode.getY() == endNode.getY()) ?

                                        startNode : null;

                if (unconnectedNode != null) {

                    nodes.removeIf(node -> node.getId() == unconnectedNode.getId());

                    followersMap.remove(unconnectedNode);

                    nodePositions.remove(unconnectedNode);

                }

       

                selectedEdge = null;

       

                // Repaint the panel to update the graph

                repaint();

            }

        }

       

       

       

        public void searchUser(String query) {

            query.toLowerCase();

            repaint();

        }

 

        private void selectNode(int x, int y) {

            selectedNode = null;

            selectedEdge = null;

 

            // Check if a node is clicked

            for (Node node : nodes) {

                Point position = nodePositions.get(node);

                int nodeX = position.x;

                int nodeY = position.y;

 

                if (x >= nodeX - 20 && x <= nodeX + 20 && y >= nodeY - 20 && y <= nodeY + 20) {

                    selectedNode = node;

                    break;

                }

            }

 

            // Repaint the panel to update the graph

            repaint();

        }

 

        private void selectStartNodeForEdge(int x, int y) {

            selectedNode = null;

            selectedEdge = null;

 

            // Check if a node is clicked

            for (Node node : nodes) {

                Point position = nodePositions.get(node);

                int nodeX = position.x;

                int nodeY = position.y;

 

                if (x >= nodeX - 20 && x <= nodeX + 20 && y >= nodeY - 20 && y <= nodeY + 20) {

                    startNodeForEdge = node;

                    break;

                }

            }

 

            // Repaint the panel to update the graph

            repaint();

        }

 

        private void selectEndNodeForEdge(int x, int y) {

            selectedNode = null;

            selectedEdge = null;

 

            // Check if a node is clicked

            for (Node node : nodes) {

                Point position = nodePositions.get(node);

                int nodeX = position.x;

                int nodeY = position.y;

 

                if (x >= nodeX - 20 && x <= nodeX + 20 && y >= nodeY - 20 && y <= nodeY + 20) {

                    endNodeForEdge = node;

                    break;

                }

            }

 

            // Repaint the panel to update the graph

            repaint();

        }

 

        private void establishConnection() {

            if (startNodeForEdge != null && endNodeForEdge != null && startNodeForEdge != endNodeForEdge) {

                // Check if the connection already exists

                boolean connectionExists = false;

                for (Edge edge : edges) {

                    if ((edge.getStartNode() == startNodeForEdge && edge.getEndNode() == endNodeForEdge) ||

                            (edge.getStartNode() == endNodeForEdge && edge.getEndNode() == startNodeForEdge)) {

                        connectionExists = true;

                        break;

                    }

                }

 

                if (!connectionExists) {

                    // Add a new edge between the start node and the end node

                    Edge newEdge = new Edge(startNodeForEdge, endNodeForEdge);

                    edges.add(newEdge);

 

                    // Update followers count for connected nodes

                    followersMap.put(startNodeForEdge, followersMap.getOrDefault(startNodeForEdge, 0) + 1);

                    followersMap.put(endNodeForEdge, followersMap.getOrDefault(endNodeForEdge, 0) + 1);

 

                    // Update edge positions

                    Point[] points = new Point[2];

                    points[0] = nodePositions.get(startNodeForEdge);

                    points[1] = nodePositions.get(endNodeForEdge);

                    edgePositions.put(newEdge, points);

                }

 

                // Reset the start and end nodes for the next edge

                resetEdgeMode();

 

                // Repaint the panel to update the graph

                repaint();

            }

        }

 

        private void moveSelected(int x, int y) {

            if (selectedNode != null) {

                Point position = nodePositions.get(selectedNode);

                int dx = x - position.x;

                int dy = y - position.y;

 

                position.x += dx;

                position.y += dy;

 

                // Update edge positions

                for (Map.Entry<Edge, Point[]> entry : edgePositions.entrySet()) {

                    Edge edge = entry.getKey();

                    Point[] points = entry.getValue();

                    if (points[0] == nodePositions.get(selectedNode)) {

                        points[0] = new Point(points[0].x + dx, points[0].y + dy);

                    }

                    if (points[1] == nodePositions.get(selectedNode)) {

                        points[1] = new Point(points[1].x + dx, points[1].y + dy);

                    }

                    edgePositions.put(edge, points);

                }

 

                // Update positions of connected nodes

                for (Edge edge : edges) {

                    if (edge.getStartNode() == selectedNode) {

                        Point endPosition = nodePositions.get(edge.getEndNode());

                        endPosition.x += dx;

                        endPosition.y += dy;

                    }

                    if (edge.getEndNode() == selectedNode) {

                        Point startPosition = nodePositions.get(edge.getStartNode());

                        startPosition.x += dx;

                        startPosition.y += dy;

                    }

                }

 

                // Repaint the panel to update the graph

                repaint();

            }

        }

    }

 

    static class Node {

        private int id;

        private int x;

        private int y;

 

        public Node(int id, int x, int y) {

            this.id = id;

            this.x = x;

            this.y = y;

        }

 

        public int getId() {

            return id;

        }

 

        public int getX() {

            return x;

        }

 

        public int getY() {

            return y;

        }

    }

 

    static class Edge {

        private Node startNode;

        private Node endNode;

 

        public Edge(Node startNode, Node endNode) {

            this.startNode = startNode;

            this.endNode = endNode;

        }

 

        public Node getStartNode() {

            return startNode;

        }

 

        public Node getEndNode() {

            return endNode;

        }

    }

}

 

 