package com.kreative.bitsnpicas.edit;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import com.kreative.bitsnpicas.Font;
import com.kreative.bitsnpicas.unicode.EncodingList;
import com.kreative.bitsnpicas.unicode.EncodingTable;

public class DualEncodingSelectionPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	public DualEncodingSelectionPanel(final String sben, final String dben, final File file, final DualEncodingSelectionImporter importer) {
		final JComboBox sbe = new JComboBox(EncodingList.instance().toArray());
		sbe.setEditable(false);
		sbe.setSelectedItem(EncodingList.instance().get(sben));
		
		final JComboBox dbe = new JComboBox(Charset.availableCharsets().keySet().toArray());
		dbe.setEditable(false);
		dbe.setSelectedItem(dben);
		
		final JPanel labelPanel = new JPanel(new GridLayout(0, 1, 4, 4));
		labelPanel.add(new JLabel("Single-Byte Encoding:"));
		labelPanel.add(new JLabel("Double-Byte Encoding:"));
		
		final JPanel controlPanel = new JPanel(new GridLayout(0, 1, 4, 4));
		controlPanel.add(sbe);
		controlPanel.add(dbe);
		
		final JPanel formPanel = new JPanel(new BorderLayout(8, 8));
		formPanel.add(labelPanel, BorderLayout.LINE_START);
		formPanel.add(controlPanel, BorderLayout.CENTER);
		
		final JPanel contentPanel = new JPanel(new BorderLayout(12, 12));
		contentPanel.add(new JLabel("Select an encoding for " + file.getName() + "."), BorderLayout.PAGE_START);
		contentPanel.add(formPanel, BorderLayout.CENTER);
		
		final JButton openButton = new JButton("Open");
		final JPanel buttonPanel = new JPanel(new FlowLayout());
		buttonPanel.add(openButton);
		
		final JPanel mainPanel = new JPanel(new BorderLayout(12, 12));
		mainPanel.add(contentPanel, BorderLayout.CENTER);
		mainPanel.add(buttonPanel, BorderLayout.PAGE_END);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		setLayout(new GridLayout(1, 1, 0, 0));
		add(mainPanel);
		
		openButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					EncodingTable sbenc = (EncodingTable)(sbe.getSelectedItem());
					String dbenc = (String)(dbe.getSelectedItem());
					Font<?>[] fonts = importer.createImporter(sbenc, dbenc).importFont(file);
					if (fonts != null && fonts.length > 0) {
						Main.openFonts(file, null, fonts);
					} else {
						JOptionPane.showMessageDialog(
							null, "The selected file did not contain any fonts.",
							"Open", JOptionPane.ERROR_MESSAGE
						);
					}
				} catch (IOException ioe) {
					JOptionPane.showMessageDialog(
						null, "An error occurred while reading the selected file.",
						"Open", JOptionPane.ERROR_MESSAGE
					);
				} catch (NoClassDefFoundError nce) {
					JOptionPane.showMessageDialog(
						null, "The selected file requires KSFL, but KSFL is not in the classpath.",
						"Open", JOptionPane.ERROR_MESSAGE
					);
				}
			}
		});
	}
}
