/**
 * Author: Lukas Riedersberger
 * License: CC
 * Free to use, just ask
 */

package tiffy;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;

import javax.swing.*;



public class Tiffy {
	
	class PrimeThread extends Thread {
		BufferedReader stdInput;
	    PrimeThread(BufferedReader stin) {
	        stdInput = stin;
	    }

	    public void run() {
	    	
	    }
	}
	
	public static void main(String[] args) {
		
		//initialize window
		JFrame frame = new JFrame("tiffy");  
        frame.setSize(640,480);
       
		
		//set path for runtime, create directory with options in homedirectory
        JFileChooser fr = new JFileChooser();
        String homedir = fr.getFileSystemView().getDefaultDirectory().getPath();

        File ffmpeg_settings_dir = new File(homedir+"\\tiffy");
        
        if(!ffmpeg_settings_dir.exists()){
        	ffmpeg_settings_dir.mkdir();
        }
        
        File ffmpeg_settings = new File(homedir+"\\tiffy\\settings.ini");

        if(!ffmpeg_settings.exists()) {
        	try {
				ffmpeg_settings.createNewFile();
			} catch (IOException e) {
				//TODO
				e.printStackTrace();
			}      	
        } 
        
        String line;
        String binary_path = null;
        //exe defined by [binary]
        try {
            InputStream fis = new FileInputStream(homedir+"\\tiffy\\settings.ini");
            InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
            @SuppressWarnings("resource")
			BufferedReader br = new BufferedReader(isr);
            
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                if(line.length() >= 10 ){
                	String compare = line.substring(0, 8);
                	String tmp = line.substring(line.length()-10, line.length());
                	if(compare.equals("[binary]") && tmp.equals("ffmpeg.exe")){
                		binary_path = line.substring(8,line.length());
                		break;
                	}

                }
                
            }
        } catch (IOException e) {
			//TODO
			e.printStackTrace();
		}

    	//ask for ffmpeg binary path
        while(binary_path == null){
        	System.out.println("binary not found");
        	JFileChooser pc = new JFileChooser();   
            pc.setDialogTitle("Select ffmpeg.exe");
            
            if(pc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
	        {
				try {
					binary_path = pc.getSelectedFile().getCanonicalPath();
				} catch (IOException e) {
					e.printStackTrace();
				}
	        } 
            
            String tmp = binary_path.substring(binary_path.length()-10, binary_path.length());
            if(!tmp.equals("ffmpeg.exe")){
            	binary_path = null;
            	JOptionPane.showMessageDialog(frame, "Please select the ffmpeg.exe");
            	continue;
            }
            
            Writer output = null;
            try {
    			output = new BufferedWriter(new FileWriter(homedir+"\\tiffy\\settings.ini"));
    	        output.append("[binary]"+binary_path);
    	        output.close();
    		} catch (IOException e) {
    			//TODO
    			e.printStackTrace();
    		} 
        }

        System.out.println(binary_path);
        
        frame.setVisible(true);
        
        Runtime rt = Runtime.getRuntime();
        Process proc = null;
		try {
			proc = rt.exec(binary_path+" -i E:\\Filme\\INTERSTELLAR.mkv");
			//proc = rt.exec(binary_path+" -i E:\\Filme\\TOKYO_GODFATHERS.mkv");
			//proc = rt.exec(binary_path+" -i E:\\Filme\\THE_SILENCE_OF_THE_LAMBS.mkv");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        /*BufferedReader stdInput = new BufferedReader(new 
             InputStreamReader(proc.getInputStream()));*/

        BufferedReader stdError = new BufferedReader(new 
             InputStreamReader(proc.getErrorStream()));

        String l=null;
       
        ArrayList<String> audio_streams = new ArrayList<String>();
        ArrayList<String> video_streams = new ArrayList<String>();
        
        ArrayList<DataStream> streams = new ArrayList<DataStream>();
        
        try {
			while((l=stdError.readLine()) != null) {
				if(l.contains("Stream #")){
					if(l.contains("Audio")){
						audio_streams.add(l);
						streams.add(new AudioStream(l));
					}

					if(l.contains("Video")){
						video_streams.add(l);
						streams.add(new VideoStream(l));
					}
						
					
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

        JPanel video_panel = new JPanel(new BorderLayout());
        JPanel audio_panel = new JPanel(new BorderLayout());
        
        JLabel audio_label = new JLabel("Verfügbare Audiospuren");
        JLabel video_label = new JLabel("Verfügbare Videospuren");
        
        video_panel.add(video_label,BorderLayout.NORTH);
        audio_panel.add(audio_label,BorderLayout.NORTH);
        
        DefaultListModel<JCheckBox> audio_model = new DefaultListModel<JCheckBox>();
        JCheckBoxList checkBoxList_audio = new JCheckBoxList(audio_model);
        
        DefaultListModel<JCheckBox> video_model = new DefaultListModel<JCheckBox>();
        JCheckBoxList checkBoxList_video = new JCheckBoxList(video_model);
        
        int cnt_audio = 0, cnt_video = 0;
        for (int i = 0; i < streams.size(); ++i){
        	if(streams.get(i).getClass() == AudioStream.class){
        		AudioStream s = (AudioStream) streams.get(i);
        		JCheckBox tmp = new JCheckBox(s.representation());
        		audio_model.add(cnt_audio++, tmp);
        	} else if (streams.get(i).getClass() == VideoStream.class){
        		VideoStream s = (VideoStream) streams.get(i);
        		JCheckBox tmp = new JCheckBox(s.representation());
        		video_model.add(cnt_video++, tmp);
        	}
        	
        }
        
        audio_panel.add(checkBoxList_audio);
        video_panel.add(checkBoxList_video);
        
        JSplitPane splitpane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        splitpane.setLeftComponent(video_panel);
        splitpane.setRightComponent(audio_panel);
        
        JSplitPane menupane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        JPanel menu_panel = new JPanel();
  
        menupane.setTopComponent(menu_panel);
        menupane.setBottomComponent(splitpane);
        
        frame.add(menupane);
        frame.setVisible(true);
        
	}
	
}
