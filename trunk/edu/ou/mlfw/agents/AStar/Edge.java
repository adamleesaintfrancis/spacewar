package edu.ou.mlfw.agents.AStar;

/**
 * Created by IntelliJ IDEA.
 * User: Astar
 * Date: Feb 10, 2006
 * Time: 4:50:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class Edge {
    Vertex srcVertex;
    Vertex destVertex;
    boolean solution;

    public Edge(Vertex srcVertex, Vertex destVertex) {
        this.srcVertex = srcVertex;
        this.destVertex = destVertex;
        solution = false;
    }

    public Vertex getSrcVertex() {
        return srcVertex;
    }

    public Vertex getDestVertex() {
        return destVertex;
    }

    public boolean isSolution() {
        return solution;
    }

    public void setSolution(boolean solution) {
        this.solution = solution;
    }
}
