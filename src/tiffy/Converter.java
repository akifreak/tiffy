package tiffy;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Converter extends JFrame implements ActionListener {

	class InputHandler extends Thread {

        InputStream input_;

        InputHandler(InputStream input, String name) {
            super(name);
            input_ = input;
        }

        public void run() {
            try {
                int c;
                while ((c = input_.read()) != -1) {
                    System.out.write(c);
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }

    }
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2949602210951433146L;
	JButton b;
	ArrayList<Pair<JCheckBox, DataStream> > jcb;
	String binary_path,input,output;
	JFrame frame;
	
	Converter (JFrame f, JButton _b,  ArrayList<Pair<JCheckBox, DataStream> > _jcb, String bin, String in, String out) {
		b = _b; jcb = _jcb; binary_path = bin; input = in; output = out; frame = f;
		b.addActionListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == b) {
			System.out.println("geklickt");
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
			System.out.println(binary_path+" -i "+input+" "+command.toString()+" "+output);
			
			try {
				Runtime rt = Runtime.getRuntime();
		        Process process = null;
				try {
					process = rt.exec(binary_path+" -i "+input+" "+command.toString()+" "+output);
				} catch (IOException ex) {
					ex.printStackTrace();
				}

				InputHandler errorHandler = new
						InputHandler(process.getErrorStream(), "Error Stream");
						            errorHandler.start();
						            InputHandler inputHandler = new
						InputHandler(process.getInputStream(), "Output Stream");
						            inputHandler.start();
						            try {
						                process.waitFor();
						            } catch (InterruptedException ex) {
						                throw new IOException("process interrupted");
						            }
						            System.out.println("exit code: " + process.exitValue());

				process.destroy();
				
				JOptionPane.showMessageDialog(frame, "Done");
				
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	        
		}
	}

}
