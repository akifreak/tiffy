package tiffy;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

public class StopButton extends JFrame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8784410943413490006L;
	JButton button;
	Process process;
	
	StopButton(JButton b, Process p){
		button = b; process = p;
		button.addActionListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == button && process != null) {
			process.destroy();
		}
	}

}
