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
import com.kreative.unicode.data.Block;

public class CodePointListListURLPanel extends JPanel implements ListSelectionListener {
	private static final long serialVersionUID = 1L;
	
	private final JLabel label;
	
	public CodePointListListURLPanel(JList list) {
		this.label = new JLabel(" ");
		this.label.setFont(this.label.getFont().deriveFont(10f));
		this.label.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
		setLayout(new BorderLayout());
		add(this.label, BorderLayout.LINE_START);
		if (list != null) list.addListSelectionListener(this);
		
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
		if (o instanceof Block) {
			Block b = (Block)o;
			int cp = b.firstCodePoint;
			if (cp >= 0xE000 && cp < 0xF900 || cp >= 0xF0000) {
				String h = Integer.toHexString(b.firstCodePoint).toUpperCase();
				while (h.length() < 4) h = "0" + h;
				String url = "http://www.kreativekorp.com/ucsur/charts/PDF/U" + h + ".pdf";
				label.setText(url);
				label.setToolTipText(url);
				label.setForeground(new Color(0xFF9900FF));
			} else {
				String h = Integer.toHexString(b.firstCodePoint).toUpperCase();
				while (h.length() < 4) h = "0" + h;
				String url = "http://www.unicode.org/charts/PDF/U" + h + ".pdf";
				label.setText(url);
				label.setToolTipText(url);
				label.setForeground(Color.blue);
			}
		}
	}
}
