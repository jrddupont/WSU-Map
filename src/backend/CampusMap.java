package backend;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import ui.OptionsPanel;
import ui.PanAndZoomImagePanel;
import ui.SidePanel;

public class CampusMap {
	
	private static Color nodeColor =			new Color( 240, 200,   1);
	private static Color connectionColor =		new Color( 255, 110,   0);
	private static Color pathfindColor =		new Color(  90, 215,  90);
	private static Color makeConnectionColor =	new Color(   0, 180,   0);
	private static Color closeConnectionColor =	new Color( 180,   0,   0);
	
	private static final int IFW = JComponent.WHEN_IN_FOCUSED_WINDOW;
	static PanAndZoomImagePanel mapPanel;
	static OptionsPanel optionsPanel;
	static Graph mapGraph;
	
	static Node pathfindStartNode = null;
	static Node pathfindEndNode = null;
	static LinkedList<Node> path = null;
	
	static Point connectPointClicked = null;
	static Point connectPointDragPos = null;
	static boolean connectPoints = false;
	
	public static Node newStartNode;
	public static Node newEndNode;
	
	public static final int nodeSize = 30;
	
	public static void main(String[] args) throws IOException{
		JFrame mainFrame = new JFrame();
		Container contentPane = mainFrame.getContentPane();
		mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		mainFrame.setSize(1280, 720);
		mapPanel = new PanAndZoomImagePanel();
		optionsPanel = new OptionsPanel();
		SidePanel optionsContainer = new SidePanel(optionsPanel, mainFrame);
		
		
		contentPane.add(mapPanel, BorderLayout.CENTER);
		contentPane.add(optionsContainer, BorderLayout.LINE_END);
		
		
		try {
			mapPanel.setBackgroundImage( ImageIO.read(new File("maps/main.png")) );
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		mapGraph = new Graph();
		mapGraph.loadGraph("graph.txt");
		
		loadSettings();
		
		mapPanel.getInputMap(IFW).put(KeyStroke.getKeyStroke(' '), "pathfinding_start");
		mapPanel.getActionMap().put("pathfinding_start", new KeyAction("pathfinding_start"));
		
		MouseAdapter mouseAdapter = new MapMouseListener();
		mapPanel.addMouseMotionListener(mouseAdapter);
		mapPanel.addMouseListener(mouseAdapter);
		mapPanel.addMouseWheelListener(mouseAdapter);
		
		mapPanel.setPaintFunction( CampusMap::paint );
		
		mainFrame.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
            	mapGraph.saveGraph("graph.txt");
            	saveSettings();
            }
        });
		
		optionsPanel.nodeColorChooser.getSelectionModel().addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				nodeColor = optionsPanel.nodeColorChooser.getColor();
				mapPanel.repaint();
			}
		});
		optionsPanel.edgeColorChooser.getSelectionModel().addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				connectionColor = optionsPanel.edgeColorChooser.getColor();
				mapPanel.repaint();
			}
		});
		optionsPanel.pathColorChooser.getSelectionModel().addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				pathfindColor = optionsPanel.pathColorChooser.getColor();
				mapPanel.repaint();
			}
		});
		optionsPanel.newConColorChooser.getSelectionModel().addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				makeConnectionColor = optionsPanel.newConColorChooser.getColor();
				mapPanel.repaint();
			}
		});
		optionsPanel.delConColorChooser.getSelectionModel().addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				closeConnectionColor = optionsPanel.delConColorChooser.getColor();
				mapPanel.repaint();
			}
		});
		
		optionsPanel.nodeColorChooser.setColor(nodeColor);
		optionsPanel.edgeColorChooser.setColor(connectionColor);
		optionsPanel.pathColorChooser.setColor(pathfindColor);
		optionsPanel.newConColorChooser.setColor(makeConnectionColor);
		optionsPanel.delConColorChooser.setColor(closeConnectionColor);
		
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setVisible(true);
		mapPanel.repaint();
	}
	
	@SuppressWarnings("unchecked")
	private static void saveSettings(){
		JSONObject saveData = new JSONObject();
		saveData.put("scale", mapPanel.getScale());
		saveData.put("x", mapPanel.getViewPort().x);
		saveData.put("y", mapPanel.getViewPort().y);
		
		try{
			FileWriter file = new FileWriter("settings.txt");
			file.write(saveData.toJSONString());
			file.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	private static void loadSettings(){
		JSONParser parser = new JSONParser();
		try {
			JSONObject jsonArray = (JSONObject) parser.parse(new FileReader("settings.txt"));
			mapPanel.setScale((double) jsonArray.get("scale"));
			Point viewPortPosition = new Point((int)(long)jsonArray.get("x"), (int)(long)jsonArray.get("y"));
			mapPanel.setViewPortPosition(viewPortPosition);
			mapPanel.zoom(0);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	
	final static double minDistance = 30;
	public static void handleNodePlacement(Point clickLocation) {
		for(Node n : mapGraph.nodes){
			if(n.distanceToSQR(clickLocation) < minDistance * minDistance){
				return;
			}
		}
		mapGraph.nodes.add(new Node(clickLocation));
		mapPanel.repaint();
	}
	public static void handleNodeRemoval(Point clickLocation) {
		Node nodeToRemove = getNodeAtPos(clickLocation);
		if(nodeToRemove != null){
			mapGraph.removeNode(nodeToRemove);
		}
		mapPanel.repaint();
	}
	public static void handlePathfind(Point clickLocation){
		if(pathfindStartNode == null){
			pathfindStartNode = getNodeAtPos(clickLocation);
			return;
		}
		if(pathfindEndNode == null){
			pathfindEndNode = getNodeAtPos(clickLocation);
			if(pathfindEndNode == pathfindStartNode){
				pathfindEndNode = null;
			}
			return;
		}
		pathfindStartNode = null;
		pathfindEndNode = null;
		path = null;
	}
	
	public static void startPathfind(){
		if(pathfindStartNode != null && pathfindEndNode != null){
			path = Pathfinder.findShortestPath(pathfindStartNode, pathfindEndNode);
		}
		mapPanel.repaint();
	}
	
	public static Node getNodeAtPos(Point pos) {
		for(Node n : mapGraph.nodes){
			if(pos.distanceSq(n.position) < 900){
				return n; 
			}
		}
		return null;
	}
	
	public static void connectNodes(Node n1, Node n2) {
		n1.addEdge(n2);
		n2.addEdge(n1);
	}
	public static void disconnectNodes(Node n1, Node n2) {
		n1.getEdges().remove(n2);
		n2.getEdges().remove(n1);
	}
	
	
	public static Void paint(Graphics2D g2){
		int altNodeSize = (int)(nodeSize / mapPanel.getScale());
		Stroke dashed = new BasicStroke((int)(10 / mapPanel.getScale()), BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{10}, 0);
		Stroke solid = new BasicStroke((int)(10 / mapPanel.getScale()), BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, null, 0);
		Stroke pathLine = new BasicStroke((int)(20 / mapPanel.getScale()), BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL, 0, null, 0);
		Stroke dashedPathLine = new BasicStroke((int)(20 / mapPanel.getScale()), BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{10}, 0);
		
		if(pathfindStartNode != null){	// Dashed lines around start point
			g2.setColor(pathfindColor);
			g2.setStroke(dashedPathLine);
			Point localNodePos = mapPanel.getLocalPos(pathfindStartNode.position);
			g2.drawOval(localNodePos.x - altNodeSize, localNodePos.y - altNodeSize, altNodeSize * 2, altNodeSize * 2);
		}
		if(pathfindEndNode != null){	// Dashed lines around end point
			g2.setColor(pathfindColor);
			g2.setStroke(dashedPathLine);
			Point localNodePos = mapPanel.getLocalPos(pathfindEndNode.position);
			g2.drawOval(localNodePos.x - altNodeSize, localNodePos.y - altNodeSize, altNodeSize * 2, altNodeSize * 2);
		}
		
		g2.setStroke(solid);
		g2.setColor(connectionColor);
		for(Node n : mapGraph.nodes){	// Solid lines between every node
			if(mapPanel.getScale() < 3 && !mapPanel.getViewPort().contains(n.position)){
				continue;
			}
			Point localNodePos = mapPanel.getLocalPos(n.position);
			for(Node sn : n.getEdges()){
				Point localSubNodePos = mapPanel.getLocalPos(sn.position);
				g2.drawLine(localNodePos.x, localNodePos.y, localSubNodePos.x, localSubNodePos.y);
			}
		}
		
		if(connectPoints){	// Change color depending on removal state
			g2.setColor(makeConnectionColor);
		}else{
			g2.setColor(closeConnectionColor);
		}
		
		if(connectPointClicked != null){	// Dashed lines between new connection points
			newStartNode = getNodeAtPos(mapPanel.getGlobalPos(connectPointClicked));
			if(newStartNode != null){
				Point localStart = mapPanel.getLocalPos(newStartNode.position);
				newEndNode = getNodeAtPos(mapPanel.getGlobalPos(connectPointDragPos));
				g2.setStroke(dashed);
				g2.drawOval(localStart.x - altNodeSize, localStart.y - altNodeSize, altNodeSize * 2, altNodeSize * 2);
				if(newEndNode != null){
					Point localEnd = mapPanel.getLocalPos(newEndNode.position);
					g2.drawLine(localStart.x, localStart.y, localEnd.x, localEnd.y);
					g2.drawOval(localEnd.x - altNodeSize, localEnd.y - altNodeSize, altNodeSize * 2, altNodeSize * 2);
				}else{
					g2.drawLine(localStart.x, localStart.y, connectPointDragPos.x, connectPointDragPos.y);
				}
			}
		}
		
		for(Node n : mapGraph.nodes){	// Draw nodes
			if(!mapPanel.getViewPort().contains(n.position)){
				continue;
			}
			Point localNodePos = mapPanel.getLocalPos(n.position);
			g2.setColor(nodeColor);
			g2.fillOval(localNodePos.x - altNodeSize / 2, localNodePos.y - altNodeSize / 2, altNodeSize, altNodeSize);
			
			g2.setColor(new Color(100, 100, 100));
			//g2.drawString(n.name, localNodePos.x, localNodePos.y);
		}
		
		if(path != null){	// Draw path
			g2.setColor(pathfindColor);
			g2.setStroke(pathLine);
			for(int i = 0; i < path.size() - 1; i++){
				Node node1 = path.get(i);
				Node node2 = path.get(i + 1);
				Point thisNodePos = mapPanel.getLocalPos(node1.position);
				Point nextNodePos = mapPanel.getLocalPos(node2.position);
				
				g2.drawLine(thisNodePos.x, thisNodePos.y, nextNodePos.x, nextNodePos.y);
			}
		}
		return null;
	}
	
	static class MapMouseListener extends MouseAdapter {
		@Override 
		public void mouseDragged(MouseEvent e) {
			if(SwingUtilities.isLeftMouseButton(e)){
				CampusMap.connectPointDragPos = e.getPoint();
				CampusMap.connectPoints = true;
				CampusMap.mapPanel.repaint();
			}else if(SwingUtilities.isRightMouseButton(e)){
				CampusMap.connectPointDragPos = e.getPoint();
				CampusMap.connectPoints = false;
				CampusMap.mapPanel.repaint();
			}
			
		}
		@Override 
		public void mousePressed(MouseEvent e) {
			if(e.getButton() == MouseEvent.BUTTON1){
				if(e.isShiftDown()){
					CampusMap.connectPointClicked = e.getPoint();
					CampusMap.connectPointDragPos = e.getPoint();
					CampusMap.connectPoints = true;
					CampusMap.mapPanel.repaint();
				}
			}else if(e.getButton() == MouseEvent.BUTTON3){
				if(e.isShiftDown()){
					CampusMap.connectPointClicked = e.getPoint();
					CampusMap.connectPointDragPos = e.getPoint();
					CampusMap.connectPoints = false;
					CampusMap.mapPanel.repaint();
				}
			}
		}
		
		@Override 
		public void mouseClicked(MouseEvent e) {
			Point clickLocation = CampusMap.mapPanel.getGlobalPos(e.getPoint());
			if(e.getButton() == MouseEvent.BUTTON1){
				if(e.isControlDown()){
					CampusMap.handleNodePlacement(clickLocation);
				}else{
					CampusMap.handlePathfind(clickLocation);
				}
			}else if(e.getButton() == MouseEvent.BUTTON3){
				if(e.isControlDown()){
					CampusMap.handleNodeRemoval(clickLocation);
				}
			}
		}
		
		@Override 
		public void mouseReleased(MouseEvent e) {
			if(e.getButton() == MouseEvent.BUTTON1 || e.getButton() == MouseEvent.BUTTON3){
				if(e.isShiftDown()){
					if(CampusMap.newStartNode != null && CampusMap.newEndNode != null){
						if(CampusMap.connectPoints){
							CampusMap.connectNodes(CampusMap.newStartNode, CampusMap.newEndNode);
						}else{
							CampusMap.disconnectNodes(CampusMap.newStartNode, CampusMap.newEndNode);
						}
						
					}
				}
				CampusMap.connectPointClicked = null;
				CampusMap.connectPointDragPos = null;
				CampusMap.mapPanel.repaint();
			}
		}
	}
	
	@SuppressWarnings("serial")
	public static class KeyAction extends AbstractAction {
		String action;
		public KeyAction(String action){
			this.action = action;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			switch(action){
				case "pathfinding_start": startPathfind(); break;
			}
		}
	}
}
