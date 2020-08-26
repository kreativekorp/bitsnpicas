package com.kreative.keyedit.edit;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumn;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import com.kreative.keyedit.KeyboardMapping;
import com.kreative.keyedit.KkbReader;
import com.kreative.keyedit.KkbWriter;
import com.kreative.keyedit.WinLocale;
import com.kreative.keyedit.XkbAltGrKey;
import com.kreative.keyedit.XkbComposeKey;

public class LayoutInfoPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private final KeyboardMapping km;
	private final JTextField name;
	private final JTextField winIdentifier;
	private final JTextField winCopyright;
	private final JTextField winCompany;
	private final WinLocaleTableModel winLocaleModel;
	private final JTable winLocaleTable;
	private final JScrollPane winLocalePane;
	private final JCheckBox winAltGrEnable;
	private final JCheckBox winShiftLock;
	private final JCheckBox winLrmRlm;
	private final SpinnerNumberModel macGroupNumber;
	private final SpinnerNumberModel macIdNumber;
	private final JTextField xkbPath;
	private final JTextField xkbLabel;
	private final JTextArea xkbComment;
	private final JComboBox xkbAltGrKey;
	private final JComboBox xkbComposeKey;
	private final BufferedImageWell icon;
	private final JTextField macIconVersion;
	private final MacActionIdTableModel macActionIdsModel;
	private final JTable macActionIdsTable;
	private final JScrollPane macActionIdsPane;
	private final JButton macActionIdsAdd;
	private final JButton macActionIdsDelete;
	private final JTextField htmlTitle;
	private final JTextArea htmlStyle;
	private final JTextField htmlH1;
	private final JTextField htmlH2;
	private final JTextArea htmlBody1;
	private final JTextArea htmlBody2;
	private final JTextArea htmlBody3;
	private final JTextArea htmlBody4;
	private final JTextField htmlSquareChars;
	private final JTextField htmlOutlineChars;
	
	public LayoutInfoPanel(KeyboardMapping km) {
		this.km = km;
		this.name = new JTextField(km.name);
		
		this.winIdentifier = new JTextField(km.winIdentifier, 8);
		((AbstractDocument)this.winIdentifier.getDocument()).setDocumentFilter(new LimitingDocumentFilter(8));
		
		this.winCopyright = new JTextField(km.winCopyright);
		this.winCompany = new JTextField(km.winCompany);

		int wli = km.getWinLocaleNotNull().ordinal();
		this.winLocaleModel = new WinLocaleTableModel();
		this.winLocaleTable = new JTable(this.winLocaleModel);
		this.winLocaleTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.winLocaleTable.getSelectionModel().setSelectionInterval(wli, wli);
		this.winLocalePane = scrollWrap(this.winLocaleTable);
		setColumnWidth(this.winLocaleTable, 0, 80);
		setColumnWidth(this.winLocaleTable, 1, 120);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				int i = winLocaleTable.getSelectedRow();
				int n = winLocaleTable.getRowCount() - 1;
				JScrollBar vsb = winLocalePane.getVerticalScrollBar();
				vsb.setValue(vsb.getMaximum() * i / n);
			}
		});
		
		this.winAltGrEnable = new JCheckBox("Treat Right Alt as Ctrl+Alt (AltGr)");
		this.winAltGrEnable.setSelected(km.winAltGrEnable);
		this.winShiftLock = new JCheckBox("Disable Caps Lock when Shift is pressed (ShiftLock)");
		this.winShiftLock.setSelected(km.winShiftLock);
		this.winLrmRlm = new JCheckBox("Left Shift + Backspace = LRM, Right Shift + Backspace = RLM");
		this.winLrmRlm.setSelected(km.winLrmRlm);
		
		this.macGroupNumber = new SpinnerNumberModel(km.macGroupNumber, 0, 127, 1);
		this.macIdNumber = new SpinnerNumberModel(km.macIdNumber, -32768, 32767, 1);
		this.xkbPath = new JTextField(km.xkbPath);
		this.xkbLabel = new JTextField(km.xkbLabel, 4);
		this.xkbComment = mono(new JTextArea(km.xkbComment));
		
		this.xkbAltGrKey = new JComboBox(XkbAltGrKey.values());
		this.xkbAltGrKey.setEditable(false);
		this.xkbAltGrKey.setMaximumRowCount(32);
		this.xkbAltGrKey.setSelectedItem(km.xkbAltGrKey);
		
		this.xkbComposeKey = new JComboBox(XkbComposeKey.values());
		this.xkbComposeKey.setEditable(false);
		this.xkbComposeKey.setMaximumRowCount(32);
		this.xkbComposeKey.setSelectedItem(km.xkbComposeKey);
		
		this.icon = size(new BufferedImageWell(km.icon), 36, 36);
		
		String miv = null;
		if (km.macIconVersion != null) {
			miv = "00000000" + Integer.toHexString(km.macIconVersion);
			miv = miv.substring(miv.length() - 8).toUpperCase();
		}
		this.macIconVersion = new JTextField(miv, 8);
		
		this.macActionIdsModel = new MacActionIdTableModel(km.macActionIds);
		this.macActionIdsTable = new JTable(this.macActionIdsModel);
		this.macActionIdsPane = scrollWrap(this.macActionIdsTable);
		this.macActionIdsAdd = square(new JButton("+"));
		this.macActionIdsAdd.addActionListener(new AddActionIdActionListener());
		this.macActionIdsDelete = square(new JButton("\u2212"));
		this.macActionIdsDelete.addActionListener(new DeleteActionIdActionListener());
		setColumnWidth(macActionIdsTable, 0, 80);
		setColumnWidth(macActionIdsTable, 1, 80);
		
		this.htmlTitle = new JTextField(km.htmlTitle);
		this.htmlStyle = mono(new JTextArea(km.htmlStyle));
		this.htmlH1 = new JTextField(km.htmlH1);
		this.htmlH2 = new JTextField(km.htmlH2);
		this.htmlBody1 = mono(new JTextArea(km.htmlBody1));
		this.htmlBody2 = mono(new JTextArea(km.htmlBody2));
		this.htmlBody3 = mono(new JTextArea(km.htmlBody3));
		this.htmlBody4 = mono(new JTextArea(km.htmlBody4));
		this.htmlSquareChars = new JTextField(KkbWriter.formatRanges(km.htmlSquareChars));
		this.htmlOutlineChars = new JTextField(KkbWriter.formatRanges(km.htmlOutlineChars));
		
		JPanel iconPanel = leftSxS(new JLabel("Icon:"), icon, 8);
		JPanel namePanel = leftSxS(new JLabel("Name:"), name, 8);
		JPanel topPanel = leftSxS(iconPanel, verticalCenter(namePanel), 16);
		
		JPanel winLabels = verticalStack("Short Name:", "Copyright:", "Company:");
		JPanel winFields = verticalStack(leftAlign(winIdentifier), winCopyright, winCompany);
		JPanel winLocSel = topSxS(new JLabel("Locale:"), winLocalePane, 4);
		JPanel winChecks = verticalStack(winAltGrEnable, winShiftLock, winLrmRlm);
		JPanel winPanel = verticalSxS(leftSxS(winLabels, winFields, 8), winLocSel, winChecks, 8);
		
		JPanel macLabels = verticalStack("Group Number:", "ID Number:", "Icon Version:");
		JPanel macFields = verticalStack(leftAlign(macGroupNumber), leftAlign(macIdNumber), leftAlign(macIconVersion));
		JPanel macActIDs = topSxS(new JLabel("Action IDs:"), macActionIdsPane, 4);
		JPanel macButton = leftAlign(horizontalStack(macActionIdsAdd, macActionIdsDelete));
		JPanel macPanel = verticalSxS(leftSxS(macLabels, macFields, 8), macActIDs, macButton, 8);
		
		JPanel xkbLabel1 = verticalStack("Path:", "Label:");
		JPanel xkbField1 = verticalStack(xkbPath, leftAlign(xkbLabel));
		JPanel xkbCommnt = topSxS(new JLabel("Comment:"), scrollWrap(xkbComment), 4);
		JPanel xkbLabel2 = verticalStack("AltGr Key:", "Compose Key:");
		JPanel xkbField2 = leftAlign(verticalStack(xkbAltGrKey, xkbComposeKey));
		JPanel xkbPanel = verticalSxS(leftSxS(xkbLabel1, xkbField1, 8), xkbCommnt, leftSxS(xkbLabel2, xkbField2, 8), 8);
		
		JPanel htmLabels = verticalStack("Title:", "H1:", "H2:", "Square Chars:", "Outline Chars:");
		JPanel htmFields = verticalStack(htmlTitle, htmlH1, htmlH2, htmlSquareChars, htmlOutlineChars);
		JPanel htmStyles = topSxS(new JLabel("Stylesheet:"), scrollWrap(htmlStyle), 4);
		JPanel htmPanel0 = topSxS(leftSxS(htmLabels, htmFields, 8), htmStyles, 8);
		JPanel htmPanel1 = topSxS(new JLabel("Body HTML below header:"), scrollWrap(htmlBody1), 4);
		JPanel htmPanel2 = topSxS(new JLabel("Body HTML above layout:"), scrollWrap(htmlBody2), 4);
		JPanel htmPanel3 = topSxS(new JLabel("Body HTML below layout:"), scrollWrap(htmlBody3), 4);
		JPanel htmPanel4 = topSxS(new JLabel("Footer HTML:"), scrollWrap(htmlBody4), 4);
		
		JTabbedPane tabs = new JTabbedPane();
		tabs.add("Windows (MSKLC)", addBorder(winPanel, 20));
		tabs.add("Mac OS X", addBorder(macPanel, 20));
		tabs.add("Linux (XKB)", addBorder(xkbPanel, 20));
		tabs.add("HTML Header", addBorder(htmPanel0, 20));
		tabs.add("HTML Body 1", addBorder(htmPanel1, 20));
		tabs.add("HTML Body 2", addBorder(htmPanel2, 20));
		tabs.add("HTML Body 3", addBorder(htmPanel3, 20));
		tabs.add("HTML Footer", addBorder(htmPanel4, 20));
		
		JPanel mainPanel = topSxS(topPanel, tabs, 16);
		setLayout(new GridLayout(1,1,0,0));
		add(mainPanel);
	}
	
	private static JPanel verticalCenter(Component c) {
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));
		p.add(Box.createVerticalGlue());
		p.add(c);
		p.add(Box.createVerticalGlue());
		return p;
	}
	
	private static JPanel verticalStack(String... labels) {
		JPanel p = new JPanel(new GridLayout(0,1,4,4));
		for (String s : labels) p.add(new JLabel(s));
		return p;
	}
	
	private static JPanel verticalStack(Component... comps) {
		JPanel p = new JPanel(new GridLayout(0,1,4,4));
		for (Component c : comps) p.add(c);
		return p;
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
	
	private static JPanel leftAlign(SpinnerNumberModel snm) {
		JPanel p = new JPanel(new BorderLayout());
		p.add(new JSpinner(snm), BorderLayout.LINE_START);
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
	
	private static JPanel verticalSxS(Component t, Component c, Component b, int gap) {
		JPanel p = new JPanel(new BorderLayout(gap, gap));
		p.add(t, BorderLayout.PAGE_START);
		p.add(c, BorderLayout.CENTER);
		p.add(b, BorderLayout.PAGE_END);
		return p;
	}
	
	private static <C extends JComponent> C addBorder(C c, int b) {
		c.setBorder(BorderFactory.createEmptyBorder(b, b, b, b));
		return c;
	}
	
	private static <C extends JComponent> C mono(C c) {
		c.setFont(new Font("Monospaced", Font.PLAIN, 12));
		return c;
	}
	
	private static <C extends JComponent> C size(C c, int w, int h) {
		Dimension d = new Dimension(w, h);
		c.setMinimumSize(d);
		c.setPreferredSize(d);
		c.setMaximumSize(d);
		return c;
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
	
	private static void setColumnWidth(JTable t, int col, int w) {
		TableColumn column = t.getColumnModel().getColumn(col);
		column.setMinWidth(w);
		column.setPreferredWidth(w);
		column.setMaxWidth(w);
	}
	
	public void commit() {
		km.name = this.name.getText();
		km.winIdentifier = this.winIdentifier.getText();
		km.winCopyright = this.winCopyright.getText();
		km.winCompany = this.winCompany.getText();
		km.winLocale = WinLocale.values()[this.winLocaleTable.getSelectedRow()];
		km.winAltGrEnable = this.winAltGrEnable.isSelected();
		km.winShiftLock = this.winShiftLock.isSelected();
		km.winLrmRlm = this.winLrmRlm.isSelected();
		km.macGroupNumber = this.macGroupNumber.getNumber().intValue();
		km.macIdNumber = this.macIdNumber.getNumber().intValue();
		km.xkbPath = this.xkbPath.getText();
		km.xkbLabel = this.xkbLabel.getText();
		km.xkbComment = this.xkbComment.getText();
		km.xkbAltGrKey = (XkbAltGrKey)this.xkbAltGrKey.getSelectedItem();
		km.xkbComposeKey = (XkbComposeKey)this.xkbComposeKey.getSelectedItem();
		km.icon = this.icon.getImage();
		try { km.macIconVersion = Integer.parseInt(this.macIconVersion.getText(), 16); }
		catch (NumberFormatException nfe) { km.macIconVersion = null; }
		this.macActionIdsModel.toMap(km.macActionIds);
		km.htmlTitle = this.htmlTitle.getText();
		km.htmlStyle = this.htmlStyle.getText();
		km.htmlH1 = this.htmlH1.getText();
		km.htmlH2 = this.htmlH2.getText();
		km.htmlBody1 = this.htmlBody1.getText();
		km.htmlBody2 = this.htmlBody2.getText();
		km.htmlBody3 = this.htmlBody3.getText();
		km.htmlBody4 = this.htmlBody4.getText();
		km.htmlSquareChars = KkbReader.parseRanges(this.htmlSquareChars.getText());
		km.htmlOutlineChars = KkbReader.parseRanges(this.htmlOutlineChars.getText());
	}
	
	private class LimitingDocumentFilter extends DocumentFilter {
		private int limit;
		public LimitingDocumentFilter(int limit) {
			this.limit = limit;
		}
		public void insertString(FilterBypass fb, int offset, String text, AttributeSet a) throws BadLocationException {
			int currentLength = fb.getDocument().getLength();
            int overLimit = currentLength + text.length() - limit;
            if (overLimit > 0) text = text.substring(0, text.length() - overLimit);
            if (text.length() > 0) super.insertString(fb, offset, text, a); 
		}
		public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet a) throws BadLocationException {
            int currentLength = fb.getDocument().getLength();
            int overLimit = currentLength + text.length() - length - limit;
            if (overLimit > 0) text = text.substring(0, text.length() - overLimit);
            if (text.length() > 0) super.replace(fb, offset, length, text, a); 
        }
	}
	
	private class AddActionIdActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			final int i = macActionIdsModel.getRowCount();
			macActionIdsModel.addEntry(32, "space");
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					macActionIdsTable.getSelectionModel().setSelectionInterval(i, i);
					JScrollBar vsb = macActionIdsPane.getVerticalScrollBar();
					vsb.setValue(vsb.getMaximum());
				}
			});
		}
	}
	
	private class DeleteActionIdActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			int[] rows = macActionIdsTable.getSelectedRows();
			for (int i = rows.length - 1; i >= 0; i--) {
				macActionIdsModel.deleteEntry(rows[i]);
			}
		}
	}
}
