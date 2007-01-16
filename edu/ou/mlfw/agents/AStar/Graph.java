package edu.ou.mlfw.agents.AStar;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * Created by IntelliJ IDEA.
 * User: Astar
 * Date: Feb 8, 2006
 * Time: 2:51:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class Graph {
    ArrayList<Vertex> vertices;
    PriorityQueue<DefaultMutableTreeNode> queue;
    Vertex start;
    Vertex goal;
    private int maxSteps = 20;

    public Graph(int width, int height) {
        vertices = new ArrayList<Vertex>();
        queue = new PriorityQueue<DefaultMutableTreeNode>(maxSteps + 1, new TreeNodeComparator());
    }


    public void add(Vertex vertex) {
        vertices.add(vertex);
    }

    public Vertex[] getVertices() {
        return vertices.toArray(new Vertex[vertices.size()]);
    }

    public void reset() {
        vertices.clear();
    }

    /**
     * Returns the best path found using Astar
     */
    public Graph findAstarPath(ImmutableSpacewarState state) {
        goal = findGoal();
        start = findStart();
        this.state = state;

        //clear the priority queue
	    queue.clear();

        // reset all the visited links
        resetVisited();

        // start at the start vertex
        Vertex currentVertex = start;
        start.setVisited(true);
        // add the root node to the search tree
        DefaultMutableTreeNode currentNode = new DefaultMutableTreeNode(start);
        addSuccessorsToQueue(currentNode, start, 0);

        // loop until you get to the goal but stop if you get too many steps (as goal may not be connected)
        int step = 0;
        while (!currentVertex.isGoal() && step < maxSteps) {
            // if the queue is empty, return failure
            if (queue.isEmpty()) {
                return null;
            }

            // take the next vertex off the queue
            currentNode = queue.peek();
            queue.remove(currentNode);
            currentVertex = (Vertex) currentNode.getUserObject();

            // visit it
            currentVertex.setVisited(true);

            //System.out.println("G is: " + currentVertex.getCostFromStart());
            //System.out.println("H is: " + currentVertex.getEstCostToGoal());
            //System.out.println("F is: " + currentVertex.getF() + "\n");

            // add its successors to the queue
            addSuccessorsToQueue(currentNode, currentVertex, currentVertex.getCostFromStart());
            step++;
        }

        // now traverse the search tree for a solution by searching back from the goal
        TreeNode[] path = currentNode.getPath();
        Graph solutionPath = new Graph(getWidth(), getHeight());
        Vertex previousVertex = null;

        for (int p = 0; p < path.length; p++) {
            TreeNode node = path[p];
            Vertex vertex = (Vertex) ((DefaultMutableTreeNode) node).getUserObject();

            Vertex newVertex = new Vertex(vertex.getLocation());
            newVertex.setSolution(true);
            solutionPath.add(newVertex);

            if (p > 0) {
                Edge edge = new Edge(previousVertex, newVertex);
                edge.setSolution(true);
                previousVertex.addEdge(edge);
            }
            previousVertex = newVertex;
        }

        return solutionPath;
    }

    /**
     * Marks all vertices as unvisited
     */
    private void resetVisited() {
        for (Vertex vertex : vertices) {
            vertex.setVisited(false);
        }
    }

    /**
     * Adds the non-visited edges to the queue
     *
     * @param currentVertex
     * @param costToCurrent
     */
    private void addSuccessorsToQueue(DefaultMutableTreeNode treeNode,
                                      Vertex currentVertex, 
                                      double costToCurrent) {
        double eval, cost;

        Vertex[] connectedVertices = currentVertex.getConnectedVertices();
        for (Vertex destVertex : connectedVertices) {
            cost = state.findShortestDistance(currentVertex.getLocation(),
                    destVertex.getLocation()).getMagnitude();
            destVertex.setCostFromStart(costToCurrent + cost);

            eval = calculateHeuristic(destVertex);
            destVertex.setEstCostToGoal(eval);

            DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(destVertex);
            treeNode.add(newNode);
            queue.add(newNode);
        }
    }

    /**
     * For now, my heuristic is SLD to goal
     *
     * @param vertex
     */
    private double calculateHeuristic(Vertex vertex) {
        Vector2D dist = state.findShortestDistance(goal.getLocation(), vertex.getLocation());
        return dist.getMagnitude();
    }

    /**
     * Returns the starting vertex or null if it doesn't exist
     */
    private Vertex findStart() {
        for (Vertex vertex : vertices) {
            if (vertex.isStart()) {
                return vertex;
            }
        }
        return null;
    }

    /**
     * Returns the goal vertex or null if none exists
     */
    public Vertex findGoal() {
        for (Vertex vertex : vertices) {
            if (vertex.isGoal()) {
                return vertex;
            }
        }
        return null;
    }
}