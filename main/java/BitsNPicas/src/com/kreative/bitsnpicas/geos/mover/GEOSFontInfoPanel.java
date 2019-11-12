package com.kreative.bitsnpicas.geos.mover;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import com.kreative.bitsnpicas.geos.GEOSFontFile;

public class GEOSFontInfoPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private GEOSFontFile gff;
	private GEOSIconWell iconWell;
	private JTextField nameField;
	private SpinnerNumberModel idSpinner;
	private JTextField classField;
	private JLabel classLengthLabel;
	private JTextField descriptionField;
	
	public GEOSFontInfoPanel(GEOSFontFile gff) {
		this.gff = gff;
		this.iconWell = new GEOSIconWell(gff.infoBlock.iconBitmap);
		this.nameField = new JTextField(gff.getFontName());
		this.idSpinner = new SpinnerNumberModel(gff.getFontID(), 0, 1023, 1);
		String classText = gff.getClassTextString();
		String ctLen = Integer.toString(classText.length());
		this.classField = new JTextField(classText);
		this.classLengthLabel = new JLabel(ctLen);
		this.descriptionField = new JTextField(gff.getDescriptionString());
		
		JPanel rightLabelPanel = new JPanel(new GridLayout(0,1,4,4));
		rightLabelPanel.add(new JLabel("Font ID:"));
		rightLabelPanel.add(new JLabel("Length:"));
		rightLabelPanel.add(new JLabel(" "));
		
		JPanel rightControlPanel = new JPanel(new GridLayout(0,1,4,4));
		rightControlPanel.add(new JSpinner(idSpinner));
		rightControlPanel.add(classLengthLabel);
		rightControlPanel.add(new JLabel(" "));
		
		JPanel rightPanel = new JPanel(new BorderLayout(8,8));
		rightPanel.add(rightLabelPanel, BorderLayout.LINE_START);
		rightPanel.add(rightControlPanel, BorderLayout.CENTER);
		
		JPanel centerLabelPanel = new JPanel(new GridLayout(0,1,4,4));
		centerLabelPanel.add(new JLabel("Font Name:"));
		centerLabelPanel.add(new JLabel("Class Text:"));
		centerLabelPanel.add(new JLabel("Description:"));
		
		JPanel centerControlPanel = new JPanel(new GridLayout(0,1,4,4));
		centerControlPanel.add(nameField);
		centerControlPanel.add(classField);
		centerControlPanel.add(descriptionField);
		
		JPanel centerPanel = new JPanel(new BorderLayout(8,8));
		centerPanel.add(centerLabelPanel, BorderLayout.LINE_START);
		centerPanel.add(centerControlPanel, BorderLayout.CENTER);
		
		JPanel mainPanel = new JPanel(new BorderLayout(12,12));
		mainPanel.add(iconWell, BorderLayout.LINE_START);
		mainPanel.add(centerPanel, BorderLayout.CENTER);
		mainPanel.add(rightPanel, BorderLayout.LINE_END);
		
		setLayout(new GridLayout());
		add(mainPanel);
		init();
	}
	
	private void init() {
		iconWell.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				gff.infoBlock.iconBitmap = iconWell.getIconData();
				fireChangeEvent();
			}
		});
		nameField.getDocument().addDocumentListener(new DocumentAdapter() {
			public void documentUpdate(DocumentEvent e) {
				gff.setFontName(nameField.getText());
				fireChangeEvent();
			}
		});
		idSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				gff.setFontID(idSpinner.getNumber().intValue());
				fireChangeEvent();
			}
		});
		classField.getDocument().addDocumentListener(new DocumentAdapter() {
			public void documentUpdate(DocumentEvent e) {
				String classText = classField.getText();
				String ctLen = Integer.toString(classText.length());
				gff.setClassTextString(classText);
				classLengthLabel.setText(ctLen);
				fireChangeEvent();
			}
		});
		descriptionField.getDocument().addDocumentListener(new DocumentAdapter() {
			public void documentUpdate(DocumentEvent e) {
				gff.setDescriptionString(descriptionField.getText());
				fireChangeEvent();
			}
		});
	}
	
	public void addChangeListener(ChangeListener cl) {
		this.listenerList.add(ChangeListener.class, cl);
	}
	
	public void removeChangeListener(ChangeListener cl) {
		this.listenerList.remove(ChangeListener.class, cl);
	}
	
	public ChangeListener[] getChangeListeners() {
		return this.listenerList.getListeners(ChangeListener.class);
	}
	
	private void fireChangeEvent() {
		ChangeEvent e = new ChangeEvent(this);
		for (ChangeListener cl : getChangeListeners()) {
			cl.stateChanged(e);
		}
	}
	
	private static abstract class DocumentAdapter implements DocumentListener {
		public void changedUpdate(DocumentEvent e) { documentUpdate(e); }
		public void insertUpdate(DocumentEvent e) { documentUpdate(e); }
		public void removeUpdate(DocumentEvent e) { documentUpdate(e); }
		public abstract void documentUpdate(DocumentEvent e);
	}
}
