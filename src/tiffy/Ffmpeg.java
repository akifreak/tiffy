package tiffy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Ffmpeg extends Thread {
	
	class InputHandler extends Thread {

        InputStream input_;
        
        InputHandler(InputStream input, String name) {
            super(name);
            input_ = input;
        }

        public void run() {
            try {
            	
            	BufferedReader in = new BufferedReader(new InputStreamReader(input_));
            	String line = null;
            	while((line = in.readLine()) != null) {
            		System.out.println(line);
            	}
            	
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }
	
	String binary_path, input, command, output;
	JFrame frame; 
	
	Ffmpeg (String b, String i, String c, String o, JFrame f) {
		binary_path = b; input = i; command = c; output = o; frame = f;
	}

	 public void run() {
			try {
				Runtime rt = Runtime.getRuntime();
		        Process process = null;
				try {
					process = rt.exec(binary_path+" -i "+input+" "+command+" "+output);
				} catch (IOException ex) {
					ex.printStackTrace();
				}

				InputHandler errorHandler = new	InputHandler(process.getErrorStream(), "Error Stream");  
	            errorHandler.start();
				InputHandler inputHandler = new InputHandler(process.getInputStream(), "Output Stream");
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
