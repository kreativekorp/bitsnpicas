package com.kreative.bitsnpicas.edit.importer;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import com.kreative.bitsnpicas.Font;
import com.kreative.bitsnpicas.edit.BitmapListFrame;
import com.kreative.bitsnpicas.edit.GlyphListFrame;
import com.kreative.bitsnpicas.edit.Main;
import com.kreative.unicode.data.EncodingList;
import com.kreative.unicode.data.GlyphList;

public class EncodingSelectionPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	public EncodingSelectionPanel(final String encodingName, final File file, final EncodingSelectionImporter importer) {
		final JComboBox encoding = new JComboBox(EncodingList.instance().glyphLists().toArray());
		encoding.setEditable(false);
		encoding.setSelectedItem(EncodingList.instance().getGlyphList(encodingName));
		
		final JPanel encodingPanel = new JPanel(new BorderLayout(12, 12));
		encodingPanel.add(new JLabel("Select an encoding for " + file.getName() + "."), BorderLayout.PAGE_START);
		encodingPanel.add(encoding, BorderLayout.CENTER);
		
		final JButton openButton = new JButton("Open");
		final JPanel buttonPanel = new JPanel(new FlowLayout());
		buttonPanel.add(openButton);
		
		final JPanel mainPanel = new JPanel(new BorderLayout(12, 12));
		mainPanel.add(encodingPanel, BorderLayout.CENTER);
		mainPanel.add(buttonPanel, BorderLayout.PAGE_END);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		setLayout(new GridLayout(1, 1, 0, 0));
		add(mainPanel);
		
		openButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					GlyphList enc = (GlyphList)(encoding.getSelectedItem());
					Font<?>[] fonts = importer.createImporter(enc).importFont(file);
					if (fonts != null && fonts.length > 0) {
						JFrame frame = Main.openFonts(file, null, fonts);
						if (frame instanceof GlyphListFrame) {
							((GlyphListFrame<?>)frame).getPanel().getModelList().setSelectedModelName(enc.getName(), true);
						} else if (frame instanceof BitmapListFrame) {
							((BitmapListFrame)frame).getPanel().getModelList().setSelectedModelName(enc.getName(), true);
						}
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
