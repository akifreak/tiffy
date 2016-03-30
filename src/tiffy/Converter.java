package tiffy;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

public class Converter extends JFrame implements ActionListener {


	/**
	 * 
	 */
	private static final long serialVersionUID = -2949602210951433146L;
	JButton b;
	ArrayList<Pair<JCheckBox, DataStream> > jcb;
	String binary_path,input, output, settings;
	JFrame frame; JButton stop;
	JMenu codec_selection;
	JMenu bitrate_selection;
	JMenu output_selection;
	JProgressBar progress_bar;
	
	Converter (JFrame f, JButton _b, JButton _stop ,JProgressBar pb , 
			JMenu cs, JMenu bs,JMenu os,String _settings, 
			ArrayList<Pair<JCheckBox, DataStream> > _jcb, String bin, String in) {
		b = _b; 
		jcb = _jcb; 
		binary_path = bin; 
		input = in; 
		frame = f; 
		output = null; 
		stop = _stop; 
		codec_selection = cs; 
		bitrate_selection = bs;
		output_selection = os;
		settings = _settings; 
		progress_bar = pb;
		b.addActionListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == b) {
			
			String extension = output_selection.getName();
			String tmp_setting[] = null;
			try {
				//tmp_setting = Settings.getSetting(settings,"[convert_to_dir]");
				tmp_setting = Settings.getSetting(settings,"[lastdir]");
			} catch (IOException e1) {
				e1.printStackTrace();
				return;
			}
			JFileChooser pc = new JFileChooser();   
			pc.setDialogTitle("Select Target");
			if(tmp_setting.length >= 1){
				pc.setCurrentDirectory(new File(tmp_setting[0]));
			} 

			if(pc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
			{
			 try {
			 	output = pc.getSelectedFile().getCanonicalPath();
				} catch (IOException ex) {
					ex.printStackTrace();
					return;
				}
			 	File does_it_exist = new File(output);
			 	if(does_it_exist.exists()){
			 		int n = JOptionPane.showConfirmDialog(
			 			    frame,
			 			    "The file already exists. Do you want to overwrite it ?",
			 			    "Overwrite",
			 			    JOptionPane.YES_NO_OPTION);
			 		if(n != 0) {
			 			return;
			 		} 
			 		does_it_exist.delete();
			 	}
			 } else return;
			
			/*StringBuilder tmp_builder = new StringBuilder("");
         	String[] parts = output.split("\\\\");
         	for(int i = 0; i < parts.length-1; ++i){
         		tmp_builder.append(parts[i]+"\\");
         	} 
         	if(tmp_setting.length == 0){
         		Settings.appendSetting(settings, "[convert_to_dir]", tmp_builder.toString());
        		
         	} else {
         		Settings.changeSetting(settings, "[convert_to_dir]", tmp_builder.toString());
        		
         	}*/
         		
			
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
					} else if(tmp.second().getClass() == SubtitleStream.class){
						//handle subtitles
						command.append("-map "+tmp.second().a+":"+tmp.second().b+" ");
					}
				}
			}
						
			String codec = codec_selection.getText();
			String bitrate = bitrate_selection.getText();
			
			
			//System.out.println("extension = "+extension);
			
			//copy everything
			command.append("-c copy ");
			
			//specify codecs for streams
			for (int i = 0; i < jcb.size();++i){
				Pair<JCheckBox, DataStream> tmp = jcb.get(i);
				if(tmp.first().isSelected()){		
					//except the video streams
					if(tmp.second().getClass() == VideoStream.class){
						//handle video
						if(bitrate.equals("auto"))
						{
							command.append("-c:v:"+tmp.second().b+" "+codec+" ");	
						} else {
							command.append("-c:v:"+tmp.second().b+" "+codec+" -b:v "+bitrate+"k ");
							//System.out.println("p ==== "+command.toString());
						}
					}
				}
			}
			
			Ffmpeg ffmpeg = new Ffmpeg(binary_path, input , command.toString(), output, frame, stop,progress_bar);
			ffmpeg.start();
		}
	}

}
