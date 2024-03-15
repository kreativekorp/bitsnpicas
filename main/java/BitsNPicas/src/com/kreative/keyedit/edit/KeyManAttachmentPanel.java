package com.kreative.keyedit.edit;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

public class KeyManAttachmentPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private final List<KeyManAttachmentPanelListener> listeners;
	private final KeyManAttachmentListModel model;
	private final JList list;
	private final JScrollPane pane;
	private final JButton add;
	private final JButton delete;
	private final JButton rename;
	
	public KeyManAttachmentPanel(Map<String,byte[]> attachments) {
		this.listeners = new ArrayList<KeyManAttachmentPanelListener>();
		this.model = new KeyManAttachmentListModel(attachments);
		this.list = new JList(this.model);
		this.list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.pane = scrollWrap(this.list);
		this.add = square(new JButton("+"));
		this.add.addActionListener(new AddActionListener());
		this.delete = square(new JButton("\u2212"));
		this.delete.addActionListener(new DeleteActionListener());
		this.rename = new JButton("Rename");
		this.rename.addActionListener(new RenameActionListener());
		
		setLayout(new BorderLayout(8, 8));
		add(topSxS(new JLabel("Attachments:"), pane, 4), BorderLayout.CENTER);
		JPanel buttons1 = horizontalStack(add, delete);
		JPanel buttons2 = horizontalStack(rename);
		add(leftAlign(leftSxS(buttons1, buttons2, 4)), BorderLayout.PAGE_END);
	}
	
	public void addListener(KeyManAttachmentPanelListener listener) {
		this.listeners.add(listener);
	}
	
	public void removeListener(KeyManAttachmentPanelListener listener) {
		this.listeners.remove(listener);
	}
	
	public void toMap(Map<String,byte[]> attachments) {
		this.model.toMap(attachments);
	}
	
	private static JPanel horizontalStack(Component... comps) {
		JPanel p = new JPanel(new GridLayout(1,0,4,4));
		for (Component c : comps) p.add(c);
		return p;
	}
	
	private static JPanel leftAlign(Component c) {
		JPanel p = new JPanel(new BorderLayout());
		p.add(c, BorderLayout.LINE_START);
		return p;
	}
	
	private static JPanel leftSxS(Component l, Component c, int gap) {
		JPanel p = new JPanel(new BorderLayout(gap, gap));
		p.add(l, BorderLayout.LINE_START);
		p.add(c, BorderLayout.CENTER);
		return p;
	}
	
	private static JPanel topSxS(Component t, Component c, int gap) {
		JPanel p = new JPanel(new BorderLayout(gap, gap));
		p.add(t, BorderLayout.PAGE_START);
		p.add(c, BorderLayout.CENTER);
		return p;
	}
	
	private static <C extends JComponent> C square(C c) {
		Dimension d = c.getPreferredSize();
		d.width = (d.height += 8);
		c.setMinimumSize(d);
		c.setPreferredSize(d);
		c.setMaximumSize(d);
		c.putClientProperty("JButton.buttonType", "bevel");
		return c;
	}
	
	private static JScrollPane scrollWrap(Component c) {
		return new JScrollPane(
			c,
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
		);
	}
	
	private class AddActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			File file = Main.getOpenFile();
			if (file == null) return;
			try {
				final int i = model.getSize();
				model.addEntry(file);
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						list.setSelectedIndex(i);
						JScrollBar vsb = pane.getVerticalScrollBar();
						vsb.setValue(vsb.getMaximum());
					}
				});
				for (KeyManAttachmentPanelListener l : listeners) {
					l.attachmentsChanged(model);
				}
			} catch (IOException ioe) {
				JOptionPane.showMessageDialog(
					null, "An error occurred while reading the selected file.",
					"Open", JOptionPane.ERROR_MESSAGE
				);
			}
		}
	}
	
	private class DeleteActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			int[] rows = list.getSelectedIndices();
			if (rows.length < 1) {
				Toolkit.getDefaultToolkit().beep();
				return;
			}
			for (int i = rows.length - 1; i >= 0; i--) {
				model.deleteEntry(rows[i]);
			}
			for (KeyManAttachmentPanelListener l : listeners) {
				l.attachmentsChanged(model);
			}
		}
	}
	
	private class RenameActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			int[] rows = list.getSelectedIndices();
			if (rows.length != 1) {
				Toolkit.getDefaultToolkit().beep();
				return;
			}
			String name = model.getElementAt(rows[0]).toString();
			Object input = JOptionPane.showInputDialog(
				null, "Enter a new name for " + name + ":", "Rename",
				JOptionPane.QUESTION_MESSAGE, null, null, name
			);
			if (input != null && !input.equals(name)) {
				if ((name = input.toString()).length() > 0) {
					model.renameEntry(rows[0], name);
					for (KeyManAttachmentPanelListener l : listeners) {
						l.attachmentsChanged(model);
					}
				}
			}
		}
	}
}
