package tiffy;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
//import java.io.IOException;

import java.util.ArrayList;

import javax.swing.*;

public class MListener extends JFrame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3003254436464977008L;
	ArrayList<JMenuItem> items;
	JMenu men;

	public MListener(ArrayList<JMenuItem> i, JMenu m){
		items = i; men = m;
		for(int j = 0; j < items.size();++j){
			items.get(j).addActionListener(this);
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent ae) {
		for(int j = 0; j < items.size();++j){
			if(ae.getSource() == items.get(j)){
				men.setText(items.get(j).getText());
				men.setName(items.get(j).getName());
			}
		}
	}

}