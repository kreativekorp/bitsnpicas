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

public class LongPressTableFrame extends JDialog {
	private static final long serialVersionUID = 1L;
	
	private final LongPressTablePanel panel;
	private final List<LongPressTableListener> listeners;
	
	public LongPressTableFrame(Frame parent, Promise<int[]> autoLPO, int[] lpo) {
		super(parent, "Edit Long Press Output");
		this.panel = new LongPressTablePanel(autoLPO, lpo);
		this.listeners = new ArrayList<LongPressTableListener>();
		
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
				int[] lpo = panel.getLongPressOutput();
				dispose();
				for (LongPressTableListener l : listeners) l.longPressOutputChanged(lpo);
			}
		});
		
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
	
	public void addListener(LongPressTableListener listener) {
		if (listener != null) listeners.add(listener);
	}
	
	public void removeListener(LongPressTableListener listener) {
		if (listener != null) listeners.remove(listener);
	}
	
	public LongPressTableListener[] getListeners() {
		return listeners.toArray(new LongPressTableListener[listeners.size()]);
	}
}
