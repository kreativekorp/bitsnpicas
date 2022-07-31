package com.kreative.bitsnpicas.edit;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

public class GlyphListURLPanel extends JPanel implements TreeSelectionListener {
	private static final long serialVersionUID = 1L;
	
	private final JLabel label;
	
	public GlyphListURLPanel(GlyphListModelList modelList) {
		this.label = new JLabel(" ");
		this.label.setForeground(Color.blue);
		this.label.setFont(this.label.getFont().deriveFont(10f));
		this.label.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
		setLayout(new BorderLayout());
		add(this.label, BorderLayout.LINE_START);
		modelList.addTreeSelectionListener(this);
		
		this.label.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				String s = label.getText().trim();
				if (s.length() > 0) BareBonesBrowserLaunch.openURL(s);
			}
		});
	}
	
	public void valueChanged(TreeSelectionEvent e) {
		label.setText(" ");
		label.setToolTipText(null);
		GlyphListModel model = ((GlyphListModelList)e.getSource()).getSelectedModel();
		if (model != null) {
			String url = model.getURL();
			if (url != null) {
				label.setText(url);
				label.setToolTipText(url);
			}
		}
	}
}
