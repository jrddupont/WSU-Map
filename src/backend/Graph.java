package backend;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Graph {
	public ArrayList<Node> nodes = new ArrayList<Node>();
	
	@SuppressWarnings("unchecked")
	public void saveGraph(String filename){
		JSONArray jsonNodeArray = new JSONArray();
		
		for(Node n : nodes){
			JSONObject nodeData = new JSONObject();
			nodeData.put("label", n.name);
			nodeData.put("x", n.position.x);
			nodeData.put("y", n.position.y);
			JSONArray jsonConnections = new JSONArray();
			
			LinkedList<Node> connections = n.getEdges();
			for(Node connection : connections){
				jsonConnections.add(nodes.indexOf(connection));
			}
			nodeData.put("connections", jsonConnections);
			jsonNodeArray.add(nodeData);
		}

		try{
			FileWriter file = new FileWriter(filename);
			FileWriter backupfile = new FileWriter("backups/graph_" + System.nanoTime() + ".txt");
			file.write(jsonNodeArray.toJSONString());
			file.close();
			backupfile.write(jsonNodeArray.toJSONString());
			backupfile.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void loadGraph(String filename){
		JSONParser parser = new JSONParser();
		nodes.clear();
		try{

			JSONArray jsonArray = (JSONArray) parser.parse(new FileReader(filename));
			for(int i = 0; i < jsonArray.size(); i++){
				nodes.add(new Node());
			}
			for(int i = 0; i < jsonArray.size(); i++){
				Node n = nodes.get(i);
				JSONObject jsonNode = (JSONObject) jsonArray.get(i);
				n.position.x = (int)(long)jsonNode.get("x");
				n.position.y = (int)(long)jsonNode.get("y");
				n.name = i + "";//(String)jsonNode.get("label");
				
				JSONArray connectionsArray = (JSONArray) parser.parse(jsonNode.get("connections").toString());
				for(Object o : connectionsArray){ // I really do not like this json library. Oh well
					int connection = (int)(long) o;
					Node otherNode = nodes.get(connection);
					n.addEdge(otherNode);
					otherNode.addEdge(n);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public void printGraph(){
		for (Node n : nodes) {
			System.out.println(n);
		}
	}

	public void removeNode(Node node) {
		for(Node n : node.getEdges()){
			n.getEdges().remove(node);
		}
		nodes.remove(node);
	}
}
