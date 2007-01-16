package edu.ou.mlfw.agents.AStar;

import edu.ou.spacewar.Vector2D;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Astar
 * Date: Feb 8, 2006
 * Time: 2:51:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class Vertex  {
    boolean start, goal;
    Vector2D location;
    ArrayList edges, connectedVertices;
    static public int drawingRadius = 3;
    double estCostToGoal, costFromStart;
    boolean visited, solution, currentGoal;

    public Vertex(Vector2D location) {
        start = false;
        goal = false;
        edges = new ArrayList();
        connectedVertices = new ArrayList();
        this.location = location;
        estCostToGoal = 0;
        costFromStart = 0;
        visited = false;
        solution = false;
        currentGoal = false;
    }

    public void setStart() {
        start = true;
    }

    public void setGoal() {
        goal = true;
    }

    public Vector2D getLocation() {
        return location;
    }

    public int getDrawingRadius() {
        return drawingRadius;
    }


    public void addEdge(Vertex vertex) {
        Edge edge = new Edge(this, vertex);
        edges.add(edge);
        addConnectedVertex(vertex);
        vertex.addConnectedVertex(this);
    }

    public void addConnectedVertex(Vertex vertex) {
        connectedVertices.add(vertex);
    }

    public Vertex[] getConnectedVertices() {
        return (Vertex[]) connectedVertices.toArray(new Vertex[connectedVertices.size()]);
    }

    public void addEdge(Edge edge) {
        edges.add(edge);
    }

    public Edge[] getEdges() {
        return (Edge[]) edges.toArray(new Edge[edges.size()]);
    }

    public double getEstCostToGoal() {
        return estCostToGoal;
    }

    public void setEstCostToGoal(double estCostToGoal) {
        this.estCostToGoal = estCostToGoal;
    }

    public double getCostFromStart() {
        return costFromStart;
    }

    public void setCostFromStart(double costFromStart) {
        this.costFromStart = costFromStart;
    }

    public double getF() {
        return estCostToGoal + costFromStart;
    }

    public boolean isGoal() {
        return goal;
    }

    public boolean isStart() {
        return start;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public boolean isSolution() {
        return solution;
    }

    public void setSolution(boolean solution) {
        this.solution = solution;
    }

    public boolean isCurrentGoal() {
        return currentGoal;
    }

    public void setCurrentGoal(boolean currentGoal) {
        this.currentGoal = currentGoal;
    }

}
