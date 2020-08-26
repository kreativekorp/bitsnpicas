package com.kreative.keyedit.edit;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import com.kreative.keyedit.KeyboardMapping;

public class LayoutInfoFrame extends JDialog {
	private static final long serialVersionUID = 1L;
	
	private final LayoutInfoPanel panel;
	private final List<KeyEditListener> listeners;
	
	public LayoutInfoFrame(Frame parent, KeyboardMapping km) {
		super(parent, "Layout Info");
		this.panel = new LayoutInfoPanel(km);
		this.listeners = new ArrayList<KeyEditListener>();
		
		JButton cancelButton = new JButton("Cancel");
		JButton okButton = new JButton("OK");
		JPanel buttonPanel = new JPanel(new FlowLayout());
		buttonPanel.add(cancelButton);
		buttonPanel.add(okButton);
		
		JPanel mainPanel = new JPanel(new BorderLayout(12, 12));
		mainPanel.add(this.panel, BorderLayout.CENTER);
		mainPanel.add(buttonPanel, BorderLayout.PAGE_END);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		setContentPane(mainPanel);
		SwingUtils.setDefaultButton(getRootPane(), okButton);
		SwingUtils.setCancelButton(getRootPane(), cancelButton);
		
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panel.commit();
				dispose();
				for (KeyEditListener l : listeners) l.metadataChanged();
			}
		});
		
		setSize(900, 600);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
	
	public void addListener(KeyEditListener listener) {
		if (listener != null) listeners.add(listener);
	}
	
	public void removeListener(KeyEditListener listener) {
		if (listener != null) listeners.remove(listener);
	}
	
	public KeyEditListener[] getListeners() {
		return listeners.toArray(new KeyEditListener[listeners.size()]);
	}
}
