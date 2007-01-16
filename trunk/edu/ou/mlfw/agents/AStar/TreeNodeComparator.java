package edu.ou.mlfw.agents.AStar;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Comparator;

/**
 * Created by IntelliJ IDEA.
 * User: Astar
 * Date: Feb 10, 2006
 * Time: 4:11:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class TreeNodeComparator implements Comparator<DefaultMutableTreeNode> {
    public int compare(DefaultMutableTreeNode o1, DefaultMutableTreeNode o2) {
        Vertex v1 = (Vertex) ((DefaultMutableTreeNode) o1).getUserObject();
        Vertex v2 = (Vertex) ((DefaultMutableTreeNode) o2).getUserObject();

        if (v1.getF() < v2.getF()) {
            return -1;
        } else if (v1.getF() == v2.getF()) {
            return 0;
        } else {
            return 1;
        }
    }
}
