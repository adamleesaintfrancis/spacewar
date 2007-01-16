package edu.ou.mlfw.agents.AStar;

import edu.ou.mlfw.Agent;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Astar
 * Date: Feb 7, 2006
 * Time: 3:43:15 PM
 * A star agent using roadmap search.
 */
public class AStarAgent implements Agent 
{
	private LinkedList<AStarNode> path;
	
	public List<AStarNode> findPath(AStarNode start)
	{
		this.path.clear();
		Queue<AStarNode> fringe = 
			new PriorityQueue<AStarNode>(10, AStarNodeComparator.getInstance());
		
		fringe.add(start);
		while(!fringe.isEmpty()) {
			AStarNode node = fringe.remove();
			if(node.isGoal()) {
				do { //unwind from goal to start node, accumulating this.path 
					this.path.addFirst(node);
					node = node.getParent();
				} while(node != null); 
				break;
			}
			else {
				fringe.addAll(node.expand());
			}
		}
		
		return this.path;
	}


}
