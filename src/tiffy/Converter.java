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
	
	Converter (JFrame f, JButton _b, JButton _stop , JMenu cs,String _settings, ArrayList<Pair<JCheckBox, DataStream> > _jcb, String bin, String in) {
		b = _b; jcb = _jcb; binary_path = bin; input = in; frame = f; output = null; stop = _stop; codec_selection = cs;
		settings = _settings;
		b.addActionListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == b) {
			
			String tmp_setting[] = null;
			try {
				tmp_setting = Settings.getSetting(settings,"[convert_to_dir]");
			} catch (IOException e1) {
				e1.printStackTrace();
				return;
			}
			JFileChooser pc = new JFileChooser();   
			pc.setDialogTitle("Select Movie");
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
			 }
			
			StringBuilder tmp_builder = new StringBuilder("");
         	String[] parts = output.split("\\\\");
         	for(int i = 0; i < parts.length-1; ++i){
         		tmp_builder.append(parts[i]+"\\");
         	} 
         	if(tmp_setting.length == 0){
         		Settings.appendSetting(settings, "[convert_to_dir]", tmp_builder.toString());
        		
         	} else {
         		Settings.changeSetting(settings, "[convert_to_dir]", tmp_builder.toString());
        		
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
					} else if(tmp.second().getClass() == SubtitleStream.class){
						//handle subtitles
						command.append("-map "+tmp.second().a+":"+tmp.second().b+" ");
					}
				}
			}
						
			String codec = codec_selection.getText();
			
			//copy everything
			command.append("-c copy ");
			
			//specify codecs for streams
			for (int i = 0; i < jcb.size();++i){
				Pair<JCheckBox, DataStream> tmp = jcb.get(i);
				if(tmp.first().isSelected()){		
					/*if(tmp.second().getClass() == AudioStream.class){
						//handle audio
						command.append("-c:a:"+tmp.second().b+" copy ");
					} else if(tmp.second().getClass() == VideoStream.class){
						//handle video
						command.append("-c:v:"+tmp.second().b+" "+codec+" ");
					} else if(tmp.second().getClass() == SubtitleStream.class){
						//handle video
						command.append("-c:s:"+tmp.second().b+" copy ");
					}*/
					//except the video streams
					if(tmp.second().getClass() == VideoStream.class){
						//handle video
						command.append("-c:v:"+tmp.second().b+" "+codec+" ");
					}
				}
			}
						
			//System.out.println(command.toString());
			
			Ffmpeg ffmpeg = new Ffmpeg(binary_path, input, command.toString(), output, frame, stop);
			ffmpeg.start();
		}
	}

}
