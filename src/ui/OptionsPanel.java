package ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class OptionsPanel extends JScrollPane{
	
	private static Color defaultNodeColor =				new Color( 240, 200,   1);
	private static Color defaultConnectionColor =		new Color( 255, 110,   0);
	private static Color defaultPathfindColor =			new Color(  90, 215,  90);
	private static Color defaultMakeConnectionColor =	new Color(   0, 180,   0);
	private static Color defaultCloseConnectionColor =	new Color( 180,   0,   0);
	
	public SmallColorChooser nodeColorChooser	= new SmallColorChooser(defaultNodeColor);
	public SmallColorChooser edgeColorChooser	= new SmallColorChooser(defaultConnectionColor);
	public SmallColorChooser pathColorChooser	= new SmallColorChooser(defaultPathfindColor);
	public SmallColorChooser newConColorChooser	= new SmallColorChooser(defaultMakeConnectionColor);
	public SmallColorChooser delConColorChooser	= new SmallColorChooser(defaultCloseConnectionColor);
	
	
	public OptionsPanel(){

		this.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
		this.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_ALWAYS);
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		this.getViewport().add(mainPanel);
		this.getVerticalScrollBar().setUnitIncrement(16);
		
		ArrayList<AccordionPanel> accordians = new ArrayList<AccordionPanel>();
		
		accordians.add(new AccordionPanel(nodeColorChooser, "Change node color"));
		accordians.add(new AccordionPanel(edgeColorChooser, "Change edge color"));
		accordians.add(new AccordionPanel(pathColorChooser, "Change path color"));
		accordians.add(new AccordionPanel(newConColorChooser, "Change node addition color"));
		accordians.add(new AccordionPanel(delConColorChooser, "Change node deletion color"));
		
		for(AccordionPanel ap : accordians){
			mainPanel.add(ap);
		}
	}
	public void printStuff(Component component, int level){
		String lv = "";
		for(int i = 0; i < level; i++){
			lv += "|	";
		}
		System.out.println(lv + component.getClass());
		for(Component c : ((JComponent) component).getComponents()){
			printStuff(c, level + 1);
		}
	}
}
