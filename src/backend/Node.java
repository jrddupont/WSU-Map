package backend;

import java.awt.Point;
import java.util.LinkedList;

public class Node {
	private LinkedList<Node> edges = new LinkedList<Node>();
	public Point position;
	public String name = "default";
	
	// For pathfinding, does not need to be saved
	public double distance = Double.MAX_VALUE;
	public double priority = Double.MAX_VALUE;
	public Node parent = null;
	public Node(Point position){
		this.position = position;
	}
	public Node(){
		position = new Point();
	}
	public void addEdge(Node node){
		if(!edges.contains(node) && !(node == this)){
			edges.add(node);
		}
	}
	public String toString(){
		String connections = "";
		for(Node n : edges){
			connections += n.name + ", ";
		}
		return "Name: " + name + ", Parent: " + parent.name + ", " + position + ", Connected to: " + connections; 
	}
	public LinkedList<Node> getEdges(){
		return edges;
	}
	public double distanceToSQR(Point p){
		return p.distanceSq(position);
	}
}
