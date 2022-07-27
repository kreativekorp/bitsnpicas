package com.kreative.bitsnpicas.edit;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class GlyphListURLPanel extends JPanel implements ListSelectionListener {
	private static final long serialVersionUID = 1L;
	
	private final JLabel label;
	
	public GlyphListURLPanel(GlyphListModelList modelList) {
		this.label = new JLabel(" ");
		this.label.setForeground(Color.blue);
		this.label.setFont(this.label.getFont().deriveFont(10f));
		this.label.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
		setLayout(new BorderLayout());
		add(this.label, BorderLayout.LINE_START);
		if (modelList != null) modelList.addListSelectionListener(this);
		
		this.label.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				String s = label.getText().trim();
				if (s.length() > 0) BareBonesBrowserLaunch.openURL(s);
			}
		});
	}
	
	public void valueChanged(ListSelectionEvent e) {
		label.setText(" ");
		label.setToolTipText(null);
		Object o = ((JList)e.getSource()).getSelectedValue();
		if (o instanceof GlyphListModel) {
			String url = ((GlyphListModel)o).getURL();
			if (url != null) {
				label.setText(url);
				label.setToolTipText(url);
			}
		}
	}
}
