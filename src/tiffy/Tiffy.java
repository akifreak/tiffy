/**
 * Author: Lukas Riedersberger
 * License: CC
 * Free to use, just ask
 */

package tiffy;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.text.NumberFormatter;



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
		
		try {
			UIManager.setLookAndFeel("com.jtattoo.plaf.hifi.HiFiLookAndFeel");
		} catch (Exception ex) {
			return;
		}
				
		//initialize window
		final JFrame frame = new JFrame("tiffy");  
        frame.setSize(204,280);
        frame.getContentPane().setBackground(Color.BLUE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       
		//set path for runtime, create directory with options in homedirectory
        JFileChooser fr = new JFileChooser();
        String homedir = fr.getFileSystemView().getDefaultDirectory().getPath();

        File ffmpeg_settings_dir = new File(homedir+"\\tiffy");
        
        if(!ffmpeg_settings_dir.exists()){
        	ffmpeg_settings_dir.mkdir();
        }
        
        final String ffmpeg_settings_path = homedir+"\\tiffy\\settings.ini"; 
        File ffmpeg_settings = new File(ffmpeg_settings_path);
        
        if(!ffmpeg_settings.exists()) {
        	try {
				ffmpeg_settings.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}      	
        } 
        
        String binary_path = null;
        String last_dir = null;
        boolean last_dir_found = false;
        boolean binary_path_found = false;
        final ArrayList<String> codecs = new ArrayList<String>();
        try {        
        	String[] tmp = Settings.getSetting(ffmpeg_settings_path,"[binary]");
        	if(tmp.length >= 1){
        		binary_path = tmp[0];
        		binary_path_found = true;
        		File file = new File(binary_path);
            	//check if file is binary and named ffmpeg.exe
            	if(!file.exists() || file.isDirectory()) { 
            	    binary_path_found = false;
            	    binary_path = null;
            	}
        	}
        	
        	tmp = Settings.getSetting(ffmpeg_settings_path,"[lastdir]");
        	if(tmp.length >= 1){
        		last_dir = tmp[0];
        		last_dir_found = true;
        	}
        	
        	tmp = Settings.getSetting(ffmpeg_settings_path,"[codec]");
        	if(tmp.length == 0) codecs.add("copy");
        	else {
            	for(int f = 0; f < tmp.length; ++f)
            		codecs.add(tmp[f]);
        	}

        	
        } catch (IOException e) {
			e.printStackTrace();
		}
        
    	//ask for ffmpeg binary path
        while(!binary_path_found){
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
            
            if(!binary_path.contains("\\ffmpeg.exe")){
            	binary_path = null;
            	JOptionPane.showMessageDialog(frame, "Please select the ffmpeg.exe");
            	continue;
            }
            
            Settings.changeSetting(ffmpeg_settings_path,"[binary]",binary_path);
            binary_path_found = true;
        }
        Runtime rt = Runtime.getRuntime();
        Process proc = null;
        try {
			proc = rt.exec(binary_path+" -formats");
		} catch (IOException e) {
			e.printStackTrace();
		}

        BufferedReader stdInput = new BufferedReader(new 
             InputStreamReader(proc.getInputStream()));
       
        String l=null;
        
        ArrayList<String> encodable = new ArrayList<String>();
        final ArrayList<Pair<String,String>> extensions = new ArrayList<Pair<String,String>>();
        
        try {
			while((l=stdInput.readLine()) != null) {	
				String[] splitted = l.trim().split("\\s+");
				if(splitted[0].equals("DE") || splitted[0].equals("E")){
    				encodable.add(splitted[1]);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        proc.destroy();
        ArrayList<Pair<String,String>> containermappings = new ArrayList<Pair<String,String>>();
        ArrayList<String> ignored_containers = new ArrayList<String>();
        
		try {
			String[] containermappings_settingsfile = Settings.getSetting(ffmpeg_settings_path,"[container]");
			String[] ignored_containers_settingsfile = Settings.getSetting(ffmpeg_settings_path, "[container_ignore]");
			for (int i = 0; i < containermappings_settingsfile.length;++i){
				String[] splitted = containermappings_settingsfile[i].trim().split("\\s+");		
				containermappings.add(new Pair<String,String>(splitted[0],splitted[1]));
			}
			for (int i = 0; i < ignored_containers_settingsfile.length;++i)
			{
				ignored_containers.add(ignored_containers_settingsfile[i]);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}

        for(int i = 0; i < encodable.size(); ++i)
        {
        	if(ignored_containers.contains(encodable.get(i))){
        		continue;
        	}
        	int pos = -1;
        	for ( int j = 0; j < containermappings.size();++j)
        	{
        		if(containermappings.get(j).first().equals(encodable.get(i)))
        		{
        			pos = j; 
        			break;
        		}
        	}
        	if(pos == -1){
            	try {
        			proc = rt.exec(binary_path+" -h muxer="+encodable.get(i));
        		} catch (IOException e) {
        			e.printStackTrace();
        		}

                stdInput = new BufferedReader(new 
                     InputStreamReader(proc.getInputStream()));
                l=null;
                try {
                	boolean has_common_ext = false;
        			while((l=stdInput.readLine()) != null) {
        				String line = l.trim();
        				if(line.startsWith("Common extensions")){
        					has_common_ext = true;
        					line = line.replace("Common extensions:","");
        					line = line.replace(".","");
        					line = line.trim();
        					line = line.replace(","," ");
        					String[] splitted = line.trim().split("\\s+");
        					extensions.add(new Pair<String,String>(encodable.get(i),splitted[0]));
        					Settings.appendSetting(ffmpeg_settings_path, "[container]", encodable.get(i)+" "+splitted[0]);
        					break;
        				}
        			}
        			if(!has_common_ext)
        			{
        				Settings.appendSetting(ffmpeg_settings_path, "[container_ignore]", encodable.get(i));
        			}
        		} catch (IOException e) {
        			e.printStackTrace();
        		}
                
                proc.destroy();
        	} else {
        		extensions.add(new Pair<String,String>(containermappings.get(pos).first(),containermappings.get(pos).second()));
        	}
        }
        
        final ArrayList<Pair<String,String>> encoder = new ArrayList<Pair<String,String>>();
        
        try {
			proc = rt.exec(binary_path+" -encoders");
		} catch (IOException e) {
			e.printStackTrace();
		}

        stdInput = new BufferedReader(new 
             InputStreamReader(proc.getInputStream()));
        l=null;
        try {
			while((l=stdInput.readLine()) != null) {
				
				if(l.contains("=")) continue;
				
				String[] splitted = l.trim().split("\\s+");
				
				if(splitted[0].charAt(0) == 'V' && splitted[0].charAt(3) != 'X'){
					
					String description = ""; 
					for (int i = 2; i < splitted.length;++i)
						description+=splitted[i]+" ";
					description = description.trim();
					
					if (description.contains("264") || description.contains("265") || description.contains("hevc")) {
						encoder.add(new Pair<String,String>(splitted[1],description));
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        /*for(int i = 0; i < encoder.size();++i)
        	System.out.println(encoder.get(i).first()+" "+encoder.get(i).second());*/

        //make stuff visible for lambda
        final String final_binary_path = binary_path;
        final boolean final_last_dir_found = last_dir_found;
        ImageIcon icon = new ImageIcon("icon.png",
                "tiffy_icon");
        icon.setImage(icon.getImage().getScaledInstance(204, 247, Image.SCALE_DEFAULT));
        /*System.out.println("Working Directory = " +
                System.getProperty("user.dir"));*/
        JLabel dragndrop_label = new JLabel(icon);
        dragndrop_label.setDropTarget(new DropTarget() {
			private static final long serialVersionUID = 1L;

			public synchronized void drop(DropTargetDropEvent evt) {
                try {
                    evt.acceptDrop(DnDConstants.ACTION_COPY);
                    @SuppressWarnings("unchecked")
					List<File> droppedFiles = (List<File>)
                        evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    for (File file : droppedFiles) {
                        // process first dropped file
                    	String movie = file.getPath();
                    	
                    	//check ending
                    	
                    	//get settings into ini
                    	{
                    		StringBuilder tmp = new StringBuilder("");
                    		String[] parts = movie.split("\\\\");
                    		for(int i = 0; i < parts.length-1; ++i){
                    			tmp.append(parts[i]+"\\");
                    		} String last_dir = tmp.toString();
	                 	
		                 	if(final_last_dir_found){
		                 		Settings.changeSetting(ffmpeg_settings_path,"[lastdir]",last_dir);
		                 	}
		                 	else{
		                 		Settings.appendSetting(ffmpeg_settings_path,"[lastdir]",last_dir);
		                 	}
		                 	//System.out.println(ffmpeg_settings_path+" "+last_dir);
                    	}
                 		//setup frame for movie
                    	JFrame movie_frame = new JFrame(movie);  
                    	movie_frame.setSize(650,950);
                    	movie_frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    	Runtime rt = Runtime.getRuntime();
                        Process proc = null;
                		try {
                			proc = rt.exec(final_binary_path+" -i "+"\""+movie+"\"");
                		} catch (IOException e) {
                			e.printStackTrace();
                		}

                        BufferedReader stdError = new BufferedReader(new 
                             InputStreamReader(proc.getErrorStream()));

                        String l=null;
                       
                        ArrayList<String> audio_streams = new ArrayList<String>();
                        ArrayList<String> video_streams = new ArrayList<String>();
                        ArrayList<String> subtitle_streams = new ArrayList<String>();
                        
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
                					
                					if(l.contains("Subtitle")){
                						subtitle_streams.add(l);
                						streams.add(new SubtitleStream(l));
                					}
                						
                					
                				}
                			}
                		} catch (IOException e) {
                			e.printStackTrace();
                		}
                        
                        proc.destroy();
                        
                        

                        JPanel video_panel = new JPanel(new BorderLayout());
                        JPanel audio_panel = new JPanel(new BorderLayout());
                        
                        JLabel audio_label = new JLabel("Audiospuren");
                        JLabel video_label = new JLabel("Videospuren");
                        
                        video_panel.add(video_label,BorderLayout.NORTH);
                        audio_panel.add(audio_label,BorderLayout.NORTH);
                        
                        DefaultListModel<JCheckBox> audio_model = new DefaultListModel<JCheckBox>();
                        JCheckBoxList checkBoxList_audio = new JCheckBoxList(audio_model);
                        
                        DefaultListModel<JCheckBox> video_model = new DefaultListModel<JCheckBox>();
                        JCheckBoxList checkBoxList_video = new JCheckBoxList(video_model);

                        ArrayList<DefaultListModel<JCheckBox> > sub_model_list = new ArrayList<DefaultListModel<JCheckBox> >();
                        ArrayList<JCheckBoxList> checkBoxList_subtitle_list = new ArrayList<JCheckBoxList>();
                       
                        int desired_cols = 5;
                        int cols = subtitle_streams.size()/desired_cols;
                        if(subtitle_streams.size() % desired_cols != 0) cols++;
                        
                        for (int i = 0; i < cols; ++i){
                        	sub_model_list.add(new DefaultListModel<JCheckBox>());
                        	checkBoxList_subtitle_list.add(new JCheckBoxList(sub_model_list.get(i)));
                        }
                        int[] subcnts = new int[sub_model_list.size()];
                        for(int i = 0; i < subcnts.length;++i) subcnts[i] = 0;
                        
                        ArrayList<Pair<JCheckBox, DataStream> > jcb = new ArrayList<Pair<JCheckBox, DataStream> >();
                        
                        int cnt_audio = 0, cnt_video = 0, cnt_subtitle = 0;
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
                        	} else if (streams.get(i).getClass() == SubtitleStream.class){
                        		SubtitleStream s = (SubtitleStream) streams.get(i);
                        		JCheckBox tmp = new JCheckBox(s.representation());
                        		int col_id = cnt_subtitle/desired_cols;
                        		sub_model_list.get(col_id).add(subcnts[col_id], tmp);
                        		subcnts[col_id]++;
                        		cnt_subtitle++;
                        		jcb.add(new Pair<JCheckBox, DataStream>(tmp,s));
                        	}
                        }
                        
                        audio_panel.add(checkBoxList_audio);
                        video_panel.add(checkBoxList_video);

                        JMenuBar bar = new JMenuBar();
                       
                        //codec selection
                        JMenu mode_selection = null;
                        if(codecs.size() >= 1){
                        	mode_selection = new JMenu(codecs.get(0));
                        } else mode_selection = new JMenu("copy");
                        JSeparator sep = new JSeparator();   
                  
                        ArrayList<JMenuItem> mode_items = new ArrayList<JMenuItem>();
                        
                        if(codecs.size() == 0){
                        	JMenuItem tmp = new JMenuItem("copy");
                        	mode_items.add(tmp);
                			mode_selection.add(tmp); 
                        }
                        
                        for(int j = 0; j < encoder.size();++j)
                        {
                			JMenuItem tmp = new JMenuItem(encoder.get(j).first()+" ("+encoder.get(j).second()+")");
                			tmp.setName(encoder.get(j).first());
                			mode_items.add(tmp);
                			mode_selection.add(tmp); 
                			mode_selection.add(sep);
                        }
                                                
                        bar.add(mode_selection);
                        
                        //bitrate
                        NumberFormat format = DecimalFormat.getInstance();
                        format.setRoundingMode(RoundingMode.FLOOR);
                        format.setMinimumFractionDigits(0);
                        format.setMaximumFractionDigits(2);
                        format.setGroupingUsed(false);
                        NumberFormatter formatter = new NumberFormatter(format);
                        
                        formatter.setValueClass(Integer.class);
                        formatter.setMinimum(1);
                        formatter.setMaximum(Integer.MAX_VALUE);
                        formatter.setAllowsInvalid(false);
                        formatter.setCommitsOnValidEdit(true);
                        
                        
                        JFormattedTextField bitrate_selection = new JFormattedTextField(formatter);
                        bitrate_selection.setValue(new Integer(15000));
                        
                        
                        bar.add(bitrate_selection);
                        bar.add(new JLabel("kb/s"));
                        
                     
                        JMenu output_format_selection = new JMenu("mkv");
                        output_format_selection.setName("mkv");
                        ArrayList<JMenuItem> output_format_items = new ArrayList<JMenuItem>();

                        ArrayList<String> wanted_formats = new ArrayList<String>();
                        
                        wanted_formats.add("mkv");
                        wanted_formats.add("mp4");
                        
                        for(int i = 0; i < extensions.size();++i)
                        {
                        	JMenuItem tmp = new JMenuItem(extensions.get(i).first()+"(*."+extensions.get(i).second()+")"); 
                        	tmp.setName(extensions.get(i).second());
                        	
                        	boolean appendIt = false;
                        	for(int jin = 0; jin < wanted_formats.size(); ++jin) {
                        		if ( tmp.getText().contains(wanted_formats.get(jin)) ) {
                        			appendIt = true;
                        			break;
                        		}
                        	}
                        	
                        	if(!appendIt) continue;
                        	output_format_selection.add(tmp);
                        	output_format_items.add(tmp);
                        }
                        
                        
                        MenuScroller.setScrollerFor(mode_selection);    
                        MenuScroller.setScrollerFor(output_format_selection);
                       // MenuScroller.setScrollerFor(qualitiy_setting_selection);
                        bar.add(output_format_selection);
                        //JTextField infotext = new JTextField("crf wird nur bei ''libx264/libx265'' und ''auto'' beachtet");
                        //infotext.setEditable(false);
                        //bar.add(infotext);
                        movie_frame.setJMenuBar(bar);

                        MultiSplitPane menupane = new MultiSplitPane(JSplitPane.VERTICAL_SPLIT);
                        
                        JPanel menu_panel = new JPanel();
                        JButton button = new JButton(); button.setText("Konvertieren nach");
                        JButton stop_button = new JButton(); stop_button.setText("Stop");
                        menu_panel.add(button);
                        menu_panel.add(stop_button);
                      
                        
                        JTextArea textfeld = new JTextArea(40, 50);
                        JScrollPane scrollpane = new JScrollPane(textfeld);      
                        
                        menu_panel.add(scrollpane);
                        
                        
                        JPanel subtitle_panel = new JPanel(new BorderLayout());
                        JLabel subtitle_label = new JLabel("Untertitel");
                        subtitle_panel.add(subtitle_label,BorderLayout.NORTH);
                        
                        MultiSplitPane subitle_multi_pane = new MultiSplitPane(JSplitPane.VERTICAL_SPLIT);
                       
                        for(int i = 0; i <  checkBoxList_subtitle_list.size(); ++i){
                        	JPanel sp = new JPanel(new BorderLayout());
                        	sp.add( checkBoxList_subtitle_list.get(i));
                        	subitle_multi_pane.add(sp);
                        }
                        
                        subtitle_panel.add(subitle_multi_pane);
                 
                        MultiSplitPane toppane = new MultiSplitPane(JSplitPane.HORIZONTAL_SPLIT);
                        
                        toppane.addComp(video_panel);
                        toppane.addComp(audio_panel);
                        toppane.addComp(subtitle_panel);
                        
                        JPanel progress_panel = new JPanel(new BorderLayout());
                        progress_panel.add(new JLabel("Fortschritt"),BorderLayout.NORTH);
                        	 	
                	 	JProgressBar progress_bar = new JProgressBar(0,100); progress_bar.setValue(0);
                	 	progress_bar.setStringPainted(true);
                       
                        menupane.addComp(toppane);
                        menupane.addComp(progress_bar);
                        menupane.addComp(menu_panel);
                        
                        new MListener(mode_items,mode_selection);
                        //new MListener(bitrate_items,bitrate_selection);
                        new MListener(output_format_items,output_format_selection);
                        //new MListener(qualitiy_setting_items,qualitiy_setting_selection);
                        
                        //default codec
                        for(int j = 0; j < mode_items.size();++j)
                        {
                        	JMenuItem tmp = mode_items.get(j);
                        	if(tmp.getName().equals("libx264"))
                        		tmp.doClick();
                        }
                        
                        //default container
                        for(int j = 0; j < output_format_items.size();++j)
                        {
                        	JMenuItem tmp = output_format_items.get(j);
                        	if(tmp.getName().equals("mkv"))
                        		tmp.doClick();
                        }
                        
                        
                        
                        PrintStream printStream = new PrintStream(new CustomOutputStream(textfeld)); 
                        System.setOut(printStream);
                        System.setErr(printStream);

                        new Converter(frame,button,stop_button,progress_bar,mode_selection,
                        		bitrate_selection,output_format_selection,ffmpeg_settings_path,jcb,final_binary_path,movie);
                        
                        movie_frame.add(menupane);
                        movie_frame.setVisible(true);
                    	
                    	
                    	break;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });        
        
        JPanel main_panel = new JPanel();
        main_panel.add(dragndrop_label);
        main_panel.setBackground(Color.WHITE);
        frame.add(main_panel);
        frame.setVisible(true);
	}
	
}
