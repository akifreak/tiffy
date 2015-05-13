package tiffy;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JButton;
import javax.swing.JFrame;

public class StopButton extends JFrame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8784410943413490006L;
	JButton button;
	Process process;
	
	boolean killed;
	
	StopButton(JButton b, Process p){
		button = b; process = p;
		button.addActionListener(this);
		killed = false;
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == button && process != null) {
			//process.destroy();
			OutputStream ostream = process.getOutputStream();
			try {
				ostream.write("q\n".getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}     
			try {
				ostream.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}           
			
			killed = true;
		}
	}

}
