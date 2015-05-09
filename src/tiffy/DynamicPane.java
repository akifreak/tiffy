package tiffy;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

public class DynamicPane {

	JPanel m_pane;
	ArrayList<JPanel> panels;
	String name;
	
	DynamicPane () {
		m_pane = new JPanel(new BorderLayout());
		panels = new ArrayList<JPanel>();
		name = "";
	}
	
	DynamicPane (String _name) {
		this(); 
		name = _name;
	    JLabel label = new JLabel(name);
		m_pane.add(label,BorderLayout.NORTH);	
	}
	
	public void add(JPanel a){
		panels.add(a);
	}
	
	public JPanel fin(){
		
		if(panels.size() == 1) {
			JLabel label = new JLabel(name);
			panels.get(0).add(label,BorderLayout.NORTH);
			return panels.get(0);
		}
		else if(panels.size() == 0) return m_pane;
		
		JSplitPane splitpane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		m_pane.add(splitpane);
		for (int i = 0; i < panels.size()-1; ++i){
			splitpane.setLeftComponent(panels.get(i));
			JSplitPane tmp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
			splitpane.setRightComponent(tmp);
			splitpane = tmp;
		}
		splitpane.setRightComponent(panels.get(panels.size()-1));
		return m_pane;
	}

}
