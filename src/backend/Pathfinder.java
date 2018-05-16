package backend;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class Pathfinder {
	public static LinkedList<Node> findShortestPath(Node start, Node end){
		Comparator<Node> comparator = new Comparator<Node>() {	// Comparator to find the closest node to the finish 
			@Override
			public int compare(Node n1, Node n2) {
				if (n1.priority < n2.priority){
					return -1;
				}
				if (n1.priority > n2.priority){
					return 1;
				}
				return 0;
			}
		};
		
		PriorityQueue<Node> openList = new PriorityQueue<Node>(comparator);	// Open list or "frontier" list, its the nodes that still need to be investigated
		ArrayList<Node> closeList = new ArrayList<Node>();	// Closed list, the nodes that are done being investigated.
		start.parent = null;	// The first node or where the pathfinding starts, it does not have a parent
		start.distance = 0;	// "distance" is how far away from the start that node is, in start's case, 0 
		closeList.add(start);
		openList.add(start);	// A* keeps pulling nodes from openlist until it is empty, so adding start to the list is required
		
		while(!openList.isEmpty()){	// loop until openlist is empty
			Node current = openList.poll();	// Get the node that is closest to the end (sorta)
			if(current == end){	// If the current node is the end node, we have reached the end. 
				break;
			}
			for(Node next : current.getEdges()){	// Loop through each edge on the current node
				double newCost = current.distance + current.position.distance(next.position);	// The cost of getting to a node is how far along the path it is
				if(!closeList.contains(next) || newCost < next.distance){	// If the current node has not already been investigated or if the new cost is smaller than that node's cost 
					// If either of those checks pass, readjust the current node and other node to be connected
					next.distance = newCost;	
					closeList.add(next);
					next.priority = newCost + end.position.distance(next.position);
					openList.add(next);
					next.parent = current;
				}
			}
		}	// At this point the pathfinding is done, now we trace the path backwards from the end node to the start node
		
		
		LinkedList<Node> finalPath = new LinkedList<Node>();	// The actual path we are searching for
		finalPath.add(end);
		start.parent = start;	// Make the start node's parant itself
		Node curNode = end;	// the node we are currently looking at
		while(true){	// This loop looks at curNode's parent and adds that node to the final path, then makes curNode the parent
			curNode = curNode.parent;
			finalPath.add(curNode);
			if(curNode == start){
				break;
			}
			if(finalPath.contains(curNode.parent)){
				System.out.println("Detected loop");
				return null;
			}
		}
		return finalPath;
	}
}
