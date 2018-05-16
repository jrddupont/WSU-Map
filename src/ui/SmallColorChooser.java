package ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.colorchooser.ColorSelectionModel;

@SuppressWarnings("serial")
public class SmallColorChooser extends JPanel{
	JColorChooser colorChooser = new JColorChooser();
	JButton defaultButton = new JButton("Reset to default");
	Color defaultColor;
	public SmallColorChooser(Color defaultColor){
		this.defaultColor = defaultColor;
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		this.setAlignmentX(LEFT_ALIGNMENT);
		colorChooser.setAlignmentX(LEFT_ALIGNMENT);
		defaultButton.setAlignmentX(LEFT_ALIGNMENT);
		
		
		colorChooser.setPreviewPanel(new JPanel());
		AbstractColorChooserPanel[] panels = colorChooser.getChooserPanels();
		for (AbstractColorChooserPanel accp : panels) {
			if(!accp.getDisplayName().equals("HSV")) {
				colorChooser.removeChooserPanel(accp);
			} 
		}
		JComponent current = (JComponent) colorChooser.getComponents()[0];
		while( !current.getClass().toString().equals( "class javax.swing.colorchooser.ColorPanel" ) ){
			
			current = (JComponent) current.getComponents()[0]; 
		}
		current.removeAll();
		this.add(colorChooser);
		this.add(defaultButton);
		this.add(Box.createVerticalStrut(10));
		
		defaultButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				colorChooser.setColor(defaultColor);
			}
		});
	}
	public ColorSelectionModel getSelectionModel() {
		return colorChooser.getSelectionModel();
	}
	public Color getColor(){
		return colorChooser.getColor();
	}
	public void setColor(Color newColor){
		colorChooser.setColor(newColor);
	}
}
