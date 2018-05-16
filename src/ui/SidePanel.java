package ui;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class SidePanel extends JPanel implements ActionListener{
	JButton toggle = new JButton("");
	JComponent userComponent;
	boolean isSideOpen = false;
	
	public SidePanel(JComponent component, JFrame container){
		userComponent = component;
		toggle.setMargin(new Insets(2, 2, 2, 2));
		toggle.setMaximumSize(new Dimension(30, container.getSize().height));
		toggle.setPreferredSize(new Dimension(30, container.getSize().height));
		toggle.setFocusable(false);
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		//toggle.setMaximumSize(new Dimension(26, userComponent.getMinimumSize().width));
		this.add(toggle);
		this.add(userComponent);
		toggle.addActionListener(this);
		openSide();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				closeSide();
			}
		});
		
		this.addComponentListener(new ComponentListener() {
			@Override
			public void componentResized(ComponentEvent e) {
				toggle.setMaximumSize(new Dimension(30, container.getSize().height));
				toggle.setPreferredSize(new Dimension(30, container.getSize().height));	
				validate();
			}
			@Override
			public void componentShown(ComponentEvent e) {}
			@Override
			public void componentMoved(ComponentEvent e) {}
			@Override
			public void componentHidden(ComponentEvent e) {}
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == toggle){
			if(isSideOpen){
				closeSide();
			}else{
				openSide();
			}
		}
	}
	
	public void openSide(){
		userComponent.setVisible(true);
		toggle.setText( "\u2BC8");
		isSideOpen = true;
	}
	public void closeSide(){
		userComponent.setVisible(false);
		toggle.setText( "\u2630");
		isSideOpen = false;
	}
}
