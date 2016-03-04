package tiffy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;


public class Ffmpeg extends Thread {
	
	class InputHandler extends Thread {

        InputStream input_;
        
        InputHandler(InputStream input, String name) {
            super(name);
            input_ = input;
        }

        @SuppressWarnings("resource")
		public void run() {
            try {
            	
            	if(getName().equals("Output Stream")){
	            	BufferedReader in = new BufferedReader(new InputStreamReader(input_));
	            	String line = null;
	            	while((line = in.readLine()) != null) {
	            		System.out.println(getName()+" "+line);
	            	}
            	} else {
                	Scanner sc = new Scanner(input_);

                    // Find duration
                    Pattern durPattern = Pattern.compile("(?<=Duration: )[^,]*");
                    String dur = sc.findWithinHorizon(durPattern, 0);
                    if (dur == null){
                    		sc.close();
                    		throw new RuntimeException("Could not parse duration.");
                    }
                     
                    String[] hms = dur.split(":");
                    double totalSecs = Integer.parseInt(hms[0]) * 3600
                                     + Integer.parseInt(hms[1]) *   60
                                     + Double.parseDouble(hms[2]);
                    System.out.println("Total duration: " + totalSecs + " seconds.");

                    // Find time as long as possible.
                    Pattern timePattern = Pattern.compile("(?<=time=)[\\d:.]*");
                    Pattern framePattern = Pattern.compile("(?<=fps=)[\\d.]*");
                    Pattern framePattern_withspace = Pattern.compile("(?<=fps= )[\\d.]*");
                    String match = "";
                    String framematch = "";
                    double framerate = 0.0;
                    while(sc.hasNextLine()){
                    	String line = sc.nextLine();
                    	if(line.contains("fps=") || line.contains("time=")){
	                    	Scanner frame_scanner = new Scanner(line);
	                    	
	                    	Pattern fp = framePattern;
	                    	if(line.contains("fps= ")){
	                    		fp = framePattern_withspace;
	                    	}
	                    	
	                    	if(null != (framematch = frame_scanner.findWithinHorizon(fp, 0))){
	                    		framerate = Double.parseDouble(framematch);
	                    	}
	                    	
	                    	Scanner time_scanner = new Scanner(line);
	                    	
	                    	if(null != (match = time_scanner.findWithinHorizon(timePattern, 0))){
	                    		hms = match.split(":");
	                            double secs = Integer.parseInt(hms[0]) * 3600
	                                             + Integer.parseInt(hms[1]) *   60
	                                             + Double.parseDouble(hms[2]);
	                            int progress = (int) ((secs/totalSecs)*100.f);
	                            progress_bar.setValue(progress);
	                            if(line.contains("fps=")){
	                            	progress_bar.setString(progress+" %"+" / "+framerate+" fps");
	                            } else {
	                            	progress_bar.setString(progress+" %");
	                            }
	                    	}
                    	} else {
                    		System.out.println(line);
                    	}
                    }
                    sc.close();
                    progress_bar.setValue(100);
                    progress_bar.setString(100+" %");
            	}
	
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }
	
	String binary_path, input, command, output;
	JFrame frame; JButton button;
	JProgressBar progress_bar;
	
	Ffmpeg (String b, String i, String c, String o, JFrame f, JButton bu, JProgressBar pb) {
		binary_path = b; input = i; command = c; output = o; frame = f; button = bu; progress_bar = pb;
	}

	 public void run() {
		 
		 
		 	System.out.println(command);
		
		 	
			try {
				Runtime rt = Runtime.getRuntime();
		        Process process = null;
				try {
					process = rt.exec(binary_path+" -i "+"\""+input+"\""+" "+command+" "+"\""+output+"\"");
				} catch (IOException ex) {
					ex.printStackTrace();
				}

				InputHandler errorHandler = new	InputHandler(process.getErrorStream(), "Error Stream");  
	            errorHandler.start();
				InputHandler inputHandler = new InputHandler(process.getInputStream(), "Output Stream");
				inputHandler.start();
				
				StopButton actioner = new StopButton(button,process);				
				
	            try {
	                process.waitFor();
	            } catch (InterruptedException ex) {
	            	button.removeActionListener(actioner);
	            	JOptionPane.showMessageDialog(frame, "Process interrupted, throwing exception");
	                throw new IOException("process interrupted");
	            }
	            System.out.println("exit code: " + process.exitValue());

				process.destroy();
				
				button.removeActionListener(actioner);
				
				if(actioner.killed){
					JOptionPane.showMessageDialog(frame, "Aborted by user");
				} else JOptionPane.showMessageDialog(frame, "Done");
				
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			
     }
	
}
