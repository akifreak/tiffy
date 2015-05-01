package tiffy;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

public class Converter extends JFrame implements ActionListener {


	/**
	 * 
	 */
	private static final long serialVersionUID = -2949602210951433146L;
	JButton b;
	ArrayList<Pair<JCheckBox, DataStream> > jcb;
	String binary_path,input, output;
	JFrame frame;
	
	Converter (JFrame f, JButton _b, ArrayList<Pair<JCheckBox, DataStream> > _jcb, String bin, String in) {
		b = _b; jcb = _jcb; binary_path = bin; input = in; frame = f; output = null;
		b.addActionListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == b) {
				System.out.println("geklickt");
			
				JFileChooser pc = new JFileChooser();   
			    pc.setDialogTitle("Select Movie");
			
				if(pc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
			    {
		         try {
		         	output = pc.getSelectedFile().getCanonicalPath();
					} catch (IOException ex) {
						ex.printStackTrace();
					}
			     }
			
			StringBuilder command = new StringBuilder();
			
			//generate default mapping
			for (int i = 0; i < jcb.size();++i){
				Pair<JCheckBox, DataStream> tmp = jcb.get(i);
				if(tmp.first().isSelected()){		
					if(tmp.second().getClass() == AudioStream.class){
						//handle audio
						command.append("-map "+tmp.second().a+":"+tmp.second().b+" ");
					} else if(tmp.second().getClass() == VideoStream.class){
						//handle video
						command.append("-map "+tmp.second().a+":"+tmp.second().b+" ");
					}
				}
			}
						
			//specify codecs for streams
			for (int i = 0; i < jcb.size();++i){
				Pair<JCheckBox, DataStream> tmp = jcb.get(i);
				if(tmp.first().isSelected()){		
					if(tmp.second().getClass() == AudioStream.class){
						//handle audio
						command.append("-c:a:"+tmp.second().b+" copy"+" ");
					} else if(tmp.second().getClass() == VideoStream.class){
						//handle video
						command.append("-c:v:"+tmp.second().b+" copy"+" ");
					}
				}
			}
			
			Ffmpeg ffmpeg = new Ffmpeg(binary_path, input, command.toString(), output, frame);
			ffmpeg.start();	        
		}
	}

}
