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
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;

import javax.swing.*;



public class Tiffy {
	
	private static String[] getSetting(String file, String code) throws IOException{
		
		ArrayList<String> r = new ArrayList<String>();
		InputStream fis = null;

		fis = new FileInputStream(file);

		InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
		@SuppressWarnings("resource")
		BufferedReader br = new BufferedReader(isr);
		String line;
		String regex = "\\s*\\"+code+"\\b\\s*";
		while ((line = br.readLine()) != null) {
			if (line.startsWith(code)) {
				line = line.replaceFirst(regex,"");
				r.add(line);
			} 
		}

		String[] res = new String[r.size()];
		for (int i = 0; i < r.size(); ++i){
			res[i] = r.get(i);
		}
		
		return res;
	}
	
	private static void changeSetting(String file, String code, String option){
		String line;
		ArrayList<String> file_list = new ArrayList<String>();
		InputStream fis = null;
		try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
			return;
		}
		InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
		@SuppressWarnings("resource")
		BufferedReader br = new BufferedReader(isr);
		boolean code_found = false;
		try {
			while ((line = br.readLine()) != null) {
				if (!code_found && line.startsWith(code)) {
					file_list.add(code+option);
					code_found = true;
				} else {
					file_list.add(line);
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}
		Writer output = null;
		try {
			output = new BufferedWriter(new FileWriter(file));
			
			for(int i = 0; i < file_list.size();++i){
				output.append(file_list.get(i)+"\n");
			}
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		} 
	}
	
	private static void appendSetting(String file, String code, String option){
		String line;
		ArrayList<String> file_list = new ArrayList<String>();
		InputStream fis = null;
		try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
			return;
		}
		InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
		@SuppressWarnings("resource")
		BufferedReader br = new BufferedReader(isr);
		try {
			while ((line = br.readLine()) != null) {
				file_list.add(line);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}
		Writer output = null;
		try {
			output = new BufferedWriter(new FileWriter(file));
			
			for(int i = 0; i < file_list.size();++i){
				output.append(file_list.get(i)+"\n");
			}
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		} 
	}
	
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
        frame.setSize(650,850);
       
		
		//set path for runtime, create directory with options in homedirectory
        JFileChooser fr = new JFileChooser();
        String homedir = fr.getFileSystemView().getDefaultDirectory().getPath();

        File ffmpeg_settings_dir = new File(homedir+"\\tiffy");
        
        if(!ffmpeg_settings_dir.exists()){
        	ffmpeg_settings_dir.mkdir();
        }
        
        String ffmpeg_settings_path = homedir+"\\tiffy\\settings.ini"; 
        File ffmpeg_settings = new File(ffmpeg_settings_path);
        
        if(!ffmpeg_settings.exists()) {
        	try {
				ffmpeg_settings.createNewFile();
			} catch (IOException e) {
				//TODO
				e.printStackTrace();
			}      	
        } 
        
        String binary_path = null;
        String last_dir = null;
        ArrayList<String> codecs = new ArrayList<String>();
        try {        	
        	String[] tmp = getSetting(ffmpeg_settings_path,"[binary]");
        	if(tmp.length >= 1){
        		binary_path = tmp[0];
        	}
        	
        	tmp = getSetting(ffmpeg_settings_path,"[lastdir]");
        	if(tmp.length >= 1){
        		last_dir = tmp[0];
        	}
        	
        	tmp = getSetting(ffmpeg_settings_path,"[codec]");
        	for(int f = 0; f < tmp.length; ++f)
        		codecs.add(tmp[f]);
        	
        } catch (IOException e) {
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
            
            changeSetting(ffmpeg_settings_path,"[binary]",binary_path);

        }

        //System.out.println(binary_path);
        
        frame.setVisible(true);
        
        JFileChooser pc = new JFileChooser();   
        pc.setDialogTitle("Select Movie");
        if (last_dir != null) pc.setCurrentDirectory(new File(last_dir));        
        String movie = "";
        while(!new File(movie).isFile()){
        	 if(pc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
             {
                 try {
                 	movie = pc.getSelectedFile().getCanonicalPath();
                 	StringBuilder tmp = new StringBuilder("");
                 	String[] parts = movie.split("\\\\");
                 	for(int i = 0; i < parts.length-1; ++i){
                 		tmp.append(parts[i]+"\\");
                 	} last_dir = tmp.toString();
                 	
                 	changeSetting(ffmpeg_settings_path,"[lastdir]",last_dir);

                 	
     			} catch (IOException e) {
     				e.printStackTrace();
     			}
             } else {
            	 System.exit(0);
             }
        }
        
        Runtime rt = Runtime.getRuntime();
        Process proc = null;
		try {
			proc = rt.exec(binary_path+" -i "+"\""+movie+"\"");
		} catch (IOException e) {
			e.printStackTrace();
		}

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
        
        proc.destroy();

        JPanel video_panel = new JPanel(new BorderLayout());
        JPanel audio_panel = new JPanel(new BorderLayout());
        
        JLabel audio_label = new JLabel("Verf�gbare Audiospuren");
        JLabel video_label = new JLabel("Verf�gbare Videospuren");
        
        video_panel.add(video_label,BorderLayout.NORTH);
        audio_panel.add(audio_label,BorderLayout.NORTH);
        
        DefaultListModel<JCheckBox> audio_model = new DefaultListModel<JCheckBox>();
        JCheckBoxList checkBoxList_audio = new JCheckBoxList(audio_model);
        
        DefaultListModel<JCheckBox> video_model = new DefaultListModel<JCheckBox>();
        JCheckBoxList checkBoxList_video = new JCheckBoxList(video_model);
        
        ArrayList<Pair<JCheckBox, DataStream> > jcb = new ArrayList<Pair<JCheckBox, DataStream> >();
        
        int cnt_audio = 0, cnt_video = 0;
        for (int i = 0; i < streams.size(); ++i){
        	if(streams.get(i).getClass() == AudioStream.class){
        		AudioStream s = (AudioStream) streams.get(i);
        		JCheckBox tmp = new JCheckBox(s.representation());
        		audio_model.add(cnt_audio++, tmp);
        		jcb.add(new Pair<JCheckBox, DataStream>(tmp,s));
        	} else if (streams.get(i).getClass() == VideoStream.class){
        		VideoStream s = (VideoStream) streams.get(i);
        		JCheckBox tmp = new JCheckBox(s.representation());
        		video_model.add(cnt_video++, tmp);
        		jcb.add(new Pair<JCheckBox, DataStream>(tmp,s));
        	}
        	
        }
        
        audio_panel.add(checkBoxList_audio);
        video_panel.add(checkBoxList_video);
        
        JMenuBar bar = new JMenuBar();
        JMenu mode_selection = new JMenu("copy");
        JSeparator sep = new JSeparator();   
  
        ArrayList<JMenuItem> items = new ArrayList<JMenuItem>();
        for (int j = 0; j < codecs.size();++j){
			JMenuItem tmp = new JMenuItem(codecs.get(j));
			items.add(tmp);
			mode_selection.add(tmp); 
			mode_selection.add(sep);
        }
        
        bar.add(mode_selection);
        frame.setJMenuBar(bar);
        
        JSplitPane splitpane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        splitpane.setLeftComponent(video_panel);
        splitpane.setRightComponent(audio_panel);
        
        JSplitPane menupane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        JPanel menu_panel = new JPanel();
        JButton button = new JButton(); button.setText("Konvertieren nach");
        JButton stop_button = new JButton(); stop_button.setText("Stop");
        menu_panel.add(button);
        menu_panel.add(stop_button);
        JTextArea textfeld = new JTextArea(40, 50);
        JScrollPane scrollpane = new JScrollPane(textfeld);      
        menu_panel.add(scrollpane);
        
        menupane.setTopComponent(splitpane);
        menupane.setBottomComponent(menu_panel);
        
        new MListener(items,mode_selection);
        
        PrintStream printStream = new PrintStream(new CustomOutputStream(textfeld)); 
        System.setOut(printStream);
        System.setErr(printStream);

        new Converter(frame,button,stop_button,mode_selection,jcb,binary_path,movie);
        
        frame.add(menupane);
        frame.setVisible(true);
        
	}
	
}
