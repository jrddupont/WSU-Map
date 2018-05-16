package ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class AccordionPanel extends JPanel implements ActionListener{
	JButton toggle = new JButton("");
	String label;
	JComponent userComponent;
	boolean isAccordionOpen = false;
	
	int tabWidth = 20;
	public AccordionPanel(JComponent component, String label){
		userComponent = component;
		this.label = label;
		
		JPanel strutContainer = new JPanel();
		strutContainer.setLayout(new BoxLayout(strutContainer, BoxLayout.X_AXIS));
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		toggle.setHorizontalAlignment(SwingConstants.LEFT);
		toggle.setAlignmentX(LEFT_ALIGNMENT);
		userComponent.setAlignmentX(LEFT_ALIGNMENT);
		strutContainer.setAlignmentX(LEFT_ALIGNMENT);
		this.setAlignmentX(LEFT_ALIGNMENT);
		
		toggle.addActionListener(this);

		strutContainer.add(Box.createRigidArea(new Dimension(tabWidth, 0)));
		strutContainer.add(userComponent);
		
		this.add(toggle);
		this.add(strutContainer);
		
		userComponent.setMaximumSize(userComponent.getPreferredSize());
		toggle.setMaximumSize(new Dimension(strutContainer.getMinimumSize().width, 26));
		toggle.setPreferredSize(new Dimension(strutContainer.getMinimumSize().width, 26));
		toggle.getInputMap().put(KeyStroke.getKeyStroke("SPACE"), "none");

		openAccordion();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				closeAccordion();
			}
		});
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == toggle){
			if(isAccordionOpen){
				closeAccordion();
			}else{
				openAccordion();
			}
			validate();
		}
	}
	
	public void openAccordion(){
		userComponent.setVisible(true);
		toggle.setText( "\u2BC6	" + label);
		isAccordionOpen = true;
	}
	public void closeAccordion(){
		userComponent.setVisible(false);
		toggle.setText( "\u2BC8	" + label);
		isAccordionOpen = false;
	}
}
