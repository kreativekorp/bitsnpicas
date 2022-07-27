package com.kreative.bitsnpicas.edit.importer;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import com.kreative.bitsnpicas.Font;
import com.kreative.bitsnpicas.edit.Main;
import com.kreative.unicode.data.EncodingList;
import com.kreative.unicode.data.GlyphList;

public class PSFEncodingSelectionPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	public PSFEncodingSelectionPanel(final File file, final PSFEncodingSelectionImporter importer) {
		final ArrayList<Object> lea = new ArrayList<Object>();
		lea.add("None"); lea.addAll(EncodingList.instance().glyphLists());
		final JComboBox le = new JComboBox(lea.toArray());
		le.setEditable(false);
		
		final ArrayList<Object> hea = new ArrayList<Object>();
		hea.add("None"); hea.addAll(EncodingList.instance().glyphLists());
		final JComboBox he = new JComboBox(hea.toArray());
		he.setEditable(false);
		
		final ArrayList<Object> sea = new ArrayList<Object>();
		sea.add(new PuaBaseOption(    "None",       -1));
		sea.add(new PuaBaseOption(  "U+0000",   0x0000));
		sea.add(new PuaBaseOption(  "U+E000",   0xE000));
		sea.add(new PuaBaseOption(  "U+F000",   0xF000));
		sea.add(new PuaBaseOption( "U+F0000",  0xF0000));
		sea.add(new PuaBaseOption( "U+FE000",  0xFE000));
		sea.add(new PuaBaseOption( "U+FF000",  0xFF000));
		sea.add(new PuaBaseOption("U+100000", 0x100000));
		sea.add(new PuaBaseOption("U+10E000", 0x10E000));
		sea.add(new PuaBaseOption("U+10F000", 0x10F000));
		final JComboBox se = new JComboBox(sea.toArray());
		se.setEditable(false);
		
		final JPanel labelPanel = new JPanel(new GridLayout(0, 1, 4, 4));
		labelPanel.add(new JLabel("Primary Character Set ($000-$0FF):"));
		labelPanel.add(new JLabel("Alternate Character Set ($100-$1FF):"));
		labelPanel.add(new JLabel("Passthrough Starting Code Point:"));
		
		final JPanel controlPanel = new JPanel(new GridLayout(0, 1, 4, 4));
		controlPanel.add(le);
		controlPanel.add(he);
		controlPanel.add(se);
		
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
					GlyphList lenc = (le.getSelectedIndex() > 0) ? (GlyphList)le.getSelectedItem() : null;
					GlyphList henc = (he.getSelectedIndex() > 0) ? (GlyphList)he.getSelectedItem() : null;
					int puaBase = (se.getSelectedIndex() > 0) ? ((PuaBaseOption)se.getSelectedItem()).value : -1;
					Font<?>[] fonts = importer.createImporter(lenc, henc, puaBase).importFont(file);
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
	
	private static final class PuaBaseOption {
		public final String label;
		public final int value;
		public PuaBaseOption(String label, int value) {
			this.label = label;
			this.value = value;
		}
		@Override
		public String toString() {
			return label;
		}
	}
}
