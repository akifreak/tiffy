package tiffy;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;

public class Converter extends JFrame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2949602210951433146L;
	JButton b;
	ArrayList<JCheckBox> jcb;
	
	Converter (JButton _b, ArrayList<JCheckBox> _jcb) {
		b = _b; jcb = _jcb;
		b.addActionListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == b) {
			System.out.println("geklickt");
			for (int i = 0; i < jcb.size();++i){
				System.out.println(jcb.get(i).isSelected());
			}
		}
	}

}
