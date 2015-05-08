package tiffy;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

public class DynamicPane {

	JPanel m_pane;
	ArrayList<JPanel> panels;
	
	DynamicPane () {
		m_pane = new JPanel(new BorderLayout());
		panels = new ArrayList<JPanel>();
	}
	
	public void add(JPanel a){
		panels.add(a);
	}
	
	public JPanel fin(){
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
