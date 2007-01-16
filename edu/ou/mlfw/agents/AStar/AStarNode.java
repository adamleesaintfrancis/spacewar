package edu.ou.mlfw.agents.AStar;

import java.util.List;

public interface AStarNode {
	public List<AStarNode> expand();
	public boolean isGoal();
	public double getCostFromStart();
	public double getEstimateToGoal();
	public double getAStarEstimate(); 
	public AStarNode getParent();
}
