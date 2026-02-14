package com.kreative.bitsnpicas.edit;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SetWidthDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	
	private JRadioButton setButton;
	private JRadioButton incrementButton;
	private JRadioButton scaleButton;
	private SpinnerNumberModel setSpinner;
	private SpinnerNumberModel incrementSpinner;
	private SpinnerNumberModel scaleSpinner;
	private JSpinner setJSpinner;
	private JSpinner incrementJSpinner;
	private JSpinner scaleJSpinner;
	private JButton okButton;
	private JButton cancelButton;
	private Number[] values;
	
	public SetWidthDialog(Dialog parent, String title) {
		super(parent, title);
		setModal(true);
		make();
	}
	
	public SetWidthDialog(Frame parent, String title) {
		super(parent, title);
		setModal(true);
		make();
	}
	
	public SetWidthDialog(Window parent, String title) {
		super(parent, title);
		setModal(true);
		make();
	}
	
	private void make() {
		this.setButton = new JRadioButton("Set To:");
		this.incrementButton = new JRadioButton("Increment By:");
		this.scaleButton = new JRadioButton("Scale By:");
		this.setSpinner = new SpinnerNumberModel();
		this.incrementSpinner = new SpinnerNumberModel();
		this.scaleSpinner = new SpinnerNumberModel();
		this.setJSpinner = new JSpinner(setSpinner);
		this.incrementJSpinner = new JSpinner(incrementSpinner);
		this.scaleJSpinner = new JSpinner(scaleSpinner);
		this.okButton = new JButton("OK");
		this.cancelButton = new JButton("Cancel");
		
		ButtonGroup bg = new ButtonGroup();
		bg.add(this.setButton);
		bg.add(this.incrementButton);
		bg.add(this.scaleButton);
		JPanel buttonPanel = new JPanel(new GridLayout(0, 1, 4, 4));
		buttonPanel.add(this.setButton);
		buttonPanel.add(this.incrementButton);
		buttonPanel.add(this.scaleButton);
		JPanel spinnerPanel = new JPanel(new GridLayout(0, 1, 4, 4));
		spinnerPanel.add(this.setJSpinner);
		spinnerPanel.add(this.incrementJSpinner);
		spinnerPanel.add(this.scaleJSpinner);
		Dimension d = new Dimension(80, spinnerPanel.getPreferredSize().height);
		spinnerPanel.setMinimumSize(d);
		spinnerPanel.setPreferredSize(d);
		JPanel unitPanel = new JPanel(new GridLayout(0, 1, 4, 4));
		unitPanel.add(new JLabel());
		unitPanel.add(new JLabel());
		unitPanel.add(new JLabel("%"));
		JPanel setPanel = new JPanel(new BorderLayout(4, 4));
		setPanel.add(buttonPanel, BorderLayout.LINE_START);
		setPanel.add(spinnerPanel, BorderLayout.CENTER);
		setPanel.add(unitPanel, BorderLayout.LINE_END);
		
		JPanel buttonPanel1 = new JPanel(new GridLayout(1, 0, 8, 8));
		buttonPanel1.add(okButton);
		buttonPanel1.add(cancelButton);
		JPanel buttonPanel2 = new JPanel(new FlowLayout());
		buttonPanel2.add(buttonPanel1);
		
		JPanel mainPanel = new JPanel(new BorderLayout(8, 8));
		mainPanel.add(setPanel, BorderLayout.CENTER);
		mainPanel.add(buttonPanel2, BorderLayout.PAGE_END);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		
		setContentPane(mainPanel);
		SwingUtils.setDefaultButton(getRootPane(), okButton);
		SwingUtils.setCancelButton(getRootPane(), cancelButton);
		setResizable(false);
		pack();
		setLocationRelativeTo(null);
		
		setSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				setButton.setSelected(true);
			}
		});
		incrementSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				incrementButton.setSelected(true);
			}
		});
		scaleSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				scaleButton.setSelected(true);
			}
		});
		
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (scaleButton.isSelected()) {
					double scale = scaleSpinner.getNumber().doubleValue();
					for (int i = 0; i < values.length; i++) {
						values[i] = values[i].doubleValue() * scale / 100;
					}
				} else if (incrementButton.isSelected()) {
					double increment = incrementSpinner.getNumber().doubleValue();
					for (int i = 0; i < values.length; i++) {
						values[i] = values[i].doubleValue() + increment;
					}
				} else {
					double set = setSpinner.getNumber().doubleValue();
					for (int i = 0; i < values.length; i++) {
						values[i] = set;
					}
				}
				dispose();
			}
		});
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				values = null;
				dispose();
			}
		});
	}
	
	public Number[] showDialog(Number[] values) {
		this.values = values;
		this.setSpinner.setValue(values[0]);
		this.incrementSpinner.setValue(0);
		this.scaleSpinner.setValue(100.0);
		this.setButton.setSelected(true);
		
		JComponent ed = this.setJSpinner.getEditor();
		if (ed instanceof JSpinner.DefaultEditor) {
			JSpinner.DefaultEditor de = (JSpinner.DefaultEditor)ed;
			de.getTextField().requestFocusInWindow();
		}
		
		setVisible(true);
		return this.values;
	}
	
	public Number showDialog(Number value) {
		Number[] values = new Number[]{value};
		values = showDialog(values);
		if (values == null) return null;
		return values[0];
	}
}
