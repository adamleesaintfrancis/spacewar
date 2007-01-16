package edu.ou.mlfw.agents.AStar;

import java.util.Comparator;

public class AStarNodeComparator implements Comparator<AStarNode>
{
	private static AStarNodeComparator instance = new AStarNodeComparator();
	private AStarNodeComparator(){}
		
	public static AStarNodeComparator getInstance() 
	{
		return instance;
	}
		
	public int compare(AStarNode node1, AStarNode node2) 
	{
		return Double.compare( node1.getAStarEstimate(), 
				               node2.getAStarEstimate() );
	}
}
