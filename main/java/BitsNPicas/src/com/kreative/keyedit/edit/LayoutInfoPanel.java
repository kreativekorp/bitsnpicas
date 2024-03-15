package com.kreative.keyedit.edit;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.LinkedHashMap;
import java.util.Scanner;
import java.util.TreeSet;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import com.kreative.keyedit.HTMLWriterUtility;
import com.kreative.keyedit.KeyManPlatform;
import com.kreative.keyedit.KeyManProjectWriter;
import com.kreative.keyedit.KeyManTarget;
import com.kreative.keyedit.KeyManWriterUtility;
import com.kreative.keyedit.KeyboardMapping;
import com.kreative.keyedit.KkbReader;
import com.kreative.keyedit.KkbWriter;
import com.kreative.keyedit.WinLocale;
import com.kreative.keyedit.XkbAltGrKey;
import com.kreative.keyedit.XkbComposeKey;
import com.kreative.unicode.data.NameResolver;

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
	private final CodePointLabelTableModel macActionIdsModel;
	private final JTable macActionIdsTable;
	private final JScrollPane macActionIdsPane;
	private final JButton macActionIdsAdd;
	private final JButton macActionIdsDelete;
	private final JTextField keymanIdentifier;
	private final JTextField keymanName;
	private final JTextField keymanCopyright;
	private final JTextField keymanMessage;
	private final JTextField keymanWebHelpText;
	private final JTextField keymanVersion;
	private final JTextArea keymanComments;
	private final JTextField keymanAuthor;
	private final JTextField keymanEmailAddress;
	private final JTextField keymanWebSite;
	private final JCheckBox keymanRightToLeft;
	private final JCheckBox keymanKey102;
	private final JCheckBox keymanDisplayUnderlying;
	private final JCheckBox keymanUseAltGr;
	private final JCheckBox keymanIgnoreCaps;
	private final JCheckBox[] keymanTargets;
	private final JCheckBox[] keymanPlatforms;
	private final KeyManLanguageTableModel keymanLanguagesModel;
	private final JTable keymanLanguagesTable;
	private final JScrollPane keymanLanguagesPane;
	private final JButton keymanLanguagesAdd;
	private final JButton keymanLanguagesDelete;
	private final JButton keymanLanguagesClear;
	private final JButton keymanLanguagesCopy;
	private final JButton keymanLanguagesPaste;
	private final JButton keymanLanguagesSort;
	private final KeyManAttachmentPanel keymanAttachments;
	private final CodePointLabelTableModel keymanCpLabelsModel;
	private final JTable keymanCpLabelsTable;
	private final JScrollPane keymanCpLabelsPane;
	private final JButton keymanCpLabelsAdd;
	private final JButton keymanCpLabelsDelete;
	private final JTextField keymanFontFamily;
	private final JComboBox keymanOSKFontFile;
	private final JComboBox keymanDisplayFontFile;
	private final JTextField keymanDescription;
	private final JTextField keymanLicenseType;
	private final JTextArea keymanLicenseText;
	private final JButton keymanLicenseDefault;
	private final JTextArea keymanReadme;
	private final JButton keymanReadmeDefault;
	private final JTextArea keymanHistory;
	private final JButton keymanHistoryDefault;
	private final JTextField htmlTitle;
	private final JTextArea htmlStyle;
	private final JTextField htmlH1;
	private final JTextField htmlH2;
	private final JTextArea htmlBody1;
	private final JTextArea htmlBody2;
	private final JTextArea htmlBody3;
	private final JTextArea htmlBody4;
	private final JTextArea htmlInstall;
	private final JButton htmlInstallDefault;
	private final JTextField htmlSquareChars;
	private final JTextField htmlOutlineChars;
	private final CodePointClassTableModel htmlTdClassesModel;
	private final JTable htmlTdClassesTable;
	private final JScrollPane htmlTdClassesPane;
	private final JButton htmlTdClassesAdd;
	private final JButton htmlTdClassesDelete;
	private final CodePointClassTableModel htmlSpanClassesModel;
	private final JTable htmlSpanClassesTable;
	private final JScrollPane htmlSpanClassesPane;
	private final JButton htmlSpanClassesAdd;
	private final JButton htmlSpanClassesDelete;
	private final CodePointLabelTableModel htmlCpLabelsModel;
	private final JTable htmlCpLabelsTable;
	private final JScrollPane htmlCpLabelsPane;
	private final JButton htmlCpLabelsAdd;
	private final JButton htmlCpLabelsDelete;
	private final KeyManAttachmentPanel winAttachments;
	private final KeyManAttachmentPanel macAttachments;
	private final KeyManAttachmentPanel xkbAttachments;
	private final JCheckBox charsIncludeDeadKeys;
	private final JCheckBox charsIncludeLongPress;
	private final JCheckBox charsVerbose;
	private final JTextArea chars;
	
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
				Rectangle r = winLocaleTable.getCellRect(i, 0, true);
				winLocaleTable.scrollRectToVisible(r);
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
		
		this.keymanIdentifier = new JTextField(km.keymanIdentifier);
		this.keymanName = new JTextField(km.keymanName);
		this.keymanCopyright = new JTextField(km.keymanCopyright);
		this.keymanMessage = new JTextField(km.keymanMessage);
		this.keymanWebHelpText = new JTextField(km.keymanWebHelpText);
		this.keymanVersion = new JTextField(km.keymanVersion);
		this.keymanComments = new JTextArea(km.keymanComments);
		this.keymanAuthor = new JTextField(km.keymanAuthor);
		this.keymanEmailAddress = new JTextField(km.keymanEmailAddress);
		this.keymanWebSite = new JTextField(km.keymanWebSite);
		this.keymanRightToLeft = new JCheckBox("Keyboard is right-to-left");
		this.keymanRightToLeft.setSelected(km.keymanRightToLeft);
		this.keymanKey102 = new JCheckBox("Display 102nd Key (as on European keyboards)");
		this.keymanKey102.setSelected(km.keymanKey102);
		this.keymanDisplayUnderlying = new JCheckBox("Display underlying layout characters");
		this.keymanDisplayUnderlying.setSelected(km.keymanDisplayUnderlying);
		this.keymanUseAltGr = new JCheckBox("Distinguish between left and right Ctrl/Alt");
		this.keymanUseAltGr.setSelected(km.keymanUseAltGr);
		this.keymanIgnoreCaps = new JCheckBox("Disable Caps Lock");
		this.keymanIgnoreCaps.setSelected(km.keymanIgnoreCaps);
		
		KeyManTarget[] targets = KeyManTarget.values();
		this.keymanTargets = new JCheckBox[targets.length];
		for (int i = 0; i < targets.length; i++) {
			this.keymanTargets[i] = new JCheckBox(targets[i].toString());
			this.keymanTargets[i].setSelected(km.keymanTargets.contains(targets[i]));
		}
		
		KeyManPlatform[] platforms = KeyManPlatform.values();
		this.keymanPlatforms = new JCheckBox[platforms.length];
		for (int i = 0; i < platforms.length; i++) {
			this.keymanPlatforms[i] = new JCheckBox(platforms[i].toString());
			this.keymanPlatforms[i].setSelected(km.keymanPlatforms.contains(platforms[i]));
		}
		
		this.keymanLanguagesModel = new KeyManLanguageTableModel(km.keymanLanguages);
		this.keymanLanguagesTable = new JTable(this.keymanLanguagesModel);
		this.keymanLanguagesTable.setDefaultRenderer(String.class, new KeyManLanguageTableCellRenderer(keymanLanguagesModel));
		this.keymanLanguagesPane = scrollWrap(this.keymanLanguagesTable);
		this.keymanLanguagesAdd = square(new JButton("+"));
		this.keymanLanguagesAdd.addActionListener(new AddKeyManLanguageActionListener(keymanLanguagesModel, keymanLanguagesTable, keymanLanguagesPane));
		this.keymanLanguagesDelete = square(new JButton("\u2212"));
		this.keymanLanguagesDelete.addActionListener(new DeleteKeyManLanguageActionListener(keymanLanguagesModel, keymanLanguagesTable));
		this.keymanLanguagesClear = new JButton("Clear All");
		this.keymanLanguagesClear.addActionListener(new ClearKeyManLanguageActionListener(keymanLanguagesModel));
		this.keymanLanguagesCopy = new JButton("Copy All");
		this.keymanLanguagesCopy.addActionListener(new CopyKeyManLanguageActionListener(keymanLanguagesModel));
		this.keymanLanguagesPaste = new JButton("Paste All");
		this.keymanLanguagesPaste.addActionListener(new PasteKeyManLanguageActionListener(keymanLanguagesModel));
		this.keymanLanguagesSort = new JButton("Sort All");
		this.keymanLanguagesSort.addActionListener(new SortKeyManLanguageActionListener(keymanLanguagesModel));
		setColumnWidth(keymanLanguagesTable, 0, 80);
		
		this.keymanAttachments = new KeyManAttachmentPanel(km.keymanAttachments);
		this.keymanAttachments.addListener(new KeyManAttachmentPanelListener() {
			public void attachmentsChanged(KeyManAttachmentListModel model) {
				updateKeymanFonts(model);
			}
		});
		
		this.keymanCpLabelsModel = new CodePointLabelTableModel(km.keymanCpLabels, "Label");
		this.keymanCpLabelsTable = new JTable(this.keymanCpLabelsModel);
		this.keymanCpLabelsPane = scrollWrap(this.keymanCpLabelsTable);
		this.keymanCpLabelsAdd = square(new JButton("+"));
		this.keymanCpLabelsAdd.addActionListener(new AddCodePointLabelActionListener(keymanCpLabelsModel, keymanCpLabelsTable, keymanCpLabelsPane));
		this.keymanCpLabelsDelete = square(new JButton("\u2212"));
		this.keymanCpLabelsDelete.addActionListener(new DeleteCodePointLabelActionListener(keymanCpLabelsModel, keymanCpLabelsTable));
		setColumnWidth(keymanCpLabelsTable, 0, 80);
		setColumnWidth(keymanCpLabelsTable, 1, 80);
		
		this.keymanFontFamily = new JTextField(km.keymanFontFamily);
		this.keymanOSKFontFile = new JComboBox(new String[0]);
		this.keymanOSKFontFile.setEditable(false);
		this.keymanOSKFontFile.setMaximumRowCount(32);
		this.keymanDisplayFontFile = new JComboBox(new String[0]);
		this.keymanDisplayFontFile.setEditable(false);
		this.keymanDisplayFontFile.setMaximumRowCount(32);
		updateKeymanFonts(null);
		
		this.keymanDescription = new JTextField(km.keymanDescription);
		this.keymanLicenseType = mono(new JTextField(km.keymanLicenseType, 8));
		this.keymanLicenseText = mono(new JTextArea(km.keymanLicenseText));
		this.keymanLicenseDefault = new JButton("Generate Default License");
		this.keymanLicenseDefault.addActionListener(new KeymanDefaultActionListener("keyman-license.md", keymanLicenseText));
		this.keymanReadme = mono(new JTextArea(km.keymanReadme));
		this.keymanReadmeDefault = new JButton("Generate Default Readme");
		this.keymanReadmeDefault.addActionListener(new KeymanDefaultActionListener("keyman-readme.md", keymanReadme));
		this.keymanHistory = mono(new JTextArea(km.keymanHistory));
		this.keymanHistoryDefault = new JButton("Generate Default History");
		this.keymanHistoryDefault.addActionListener(new KeymanDefaultActionListener("keyman-history.md", keymanHistory));
		
		this.icon = size(new BufferedImageWell(km.icon), 36, 36);
		
		String miv = null;
		if (km.macIconVersion != null) {
			miv = "00000000" + Integer.toHexString(km.macIconVersion);
			miv = miv.substring(miv.length() - 8).toUpperCase();
		}
		this.macIconVersion = new JTextField(miv, 8);
		
		this.macActionIdsModel = new CodePointLabelTableModel(km.macActionIds, "Action ID");
		this.macActionIdsTable = new JTable(this.macActionIdsModel);
		this.macActionIdsPane = scrollWrap(this.macActionIdsTable);
		this.macActionIdsAdd = square(new JButton("+"));
		this.macActionIdsAdd.addActionListener(new AddCodePointLabelActionListener(macActionIdsModel, macActionIdsTable, macActionIdsPane));
		this.macActionIdsDelete = square(new JButton("\u2212"));
		this.macActionIdsDelete.addActionListener(new DeleteCodePointLabelActionListener(macActionIdsModel, macActionIdsTable));
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
		this.htmlInstall = mono(new JTextArea(km.htmlInstall));
		this.htmlInstallDefault = new JButton("Generate Default Instructions");
		this.htmlInstallDefault.addActionListener(new HTMLInstallDefaultActionListener());
		this.htmlSquareChars = new JTextField(KkbWriter.formatRanges(km.htmlSquareChars));
		this.htmlOutlineChars = new JTextField(KkbWriter.formatRanges(km.htmlOutlineChars));
		
		this.htmlTdClassesModel = new CodePointClassTableModel(km.htmlTdClasses);
		this.htmlTdClassesTable = new JTable(this.htmlTdClassesModel);
		this.htmlTdClassesPane = scrollWrap(this.htmlTdClassesTable);
		this.htmlTdClassesAdd = square(new JButton("+"));
		this.htmlTdClassesAdd.addActionListener(new AddCodePointClassActionListener(htmlTdClassesModel, htmlTdClassesTable, htmlTdClassesPane));
		this.htmlTdClassesDelete = square(new JButton("\u2212"));
		this.htmlTdClassesDelete.addActionListener(new DeleteCodePointClassActionListener(htmlTdClassesModel, htmlTdClassesTable));
		setColumnWidth(htmlTdClassesTable, 0, 80);
		
		this.htmlSpanClassesModel = new CodePointClassTableModel(km.htmlSpanClasses);
		this.htmlSpanClassesTable = new JTable(this.htmlSpanClassesModel);
		this.htmlSpanClassesPane = scrollWrap(this.htmlSpanClassesTable);
		this.htmlSpanClassesAdd = square(new JButton("+"));
		this.htmlSpanClassesAdd.addActionListener(new AddCodePointClassActionListener(htmlSpanClassesModel, htmlSpanClassesTable, htmlSpanClassesPane));
		this.htmlSpanClassesDelete = square(new JButton("\u2212"));
		this.htmlSpanClassesDelete.addActionListener(new DeleteCodePointClassActionListener(htmlSpanClassesModel, htmlSpanClassesTable));
		setColumnWidth(htmlSpanClassesTable, 0, 80);
		
		this.htmlCpLabelsModel = new CodePointLabelTableModel(km.htmlCpLabels, "Label");
		this.htmlCpLabelsTable = new JTable(this.htmlCpLabelsModel);
		this.htmlCpLabelsPane = scrollWrap(this.htmlCpLabelsTable);
		this.htmlCpLabelsAdd = square(new JButton("+"));
		this.htmlCpLabelsAdd.addActionListener(new AddCodePointLabelActionListener(htmlCpLabelsModel, htmlCpLabelsTable, htmlCpLabelsPane));
		this.htmlCpLabelsDelete = square(new JButton("\u2212"));
		this.htmlCpLabelsDelete.addActionListener(new DeleteCodePointLabelActionListener(htmlCpLabelsModel, htmlCpLabelsTable));
		setColumnWidth(htmlCpLabelsTable, 0, 80);
		setColumnWidth(htmlCpLabelsTable, 1, 80);
		
		this.winAttachments = new KeyManAttachmentPanel(km.winAttachments);
		this.macAttachments = new KeyManAttachmentPanel(km.macAttachments);
		this.xkbAttachments = new KeyManAttachmentPanel(km.xkbAttachments);
		
		this.charsIncludeDeadKeys = new JCheckBox("Include Dead Keys");
		this.charsIncludeDeadKeys.setSelected(true);
		this.charsIncludeDeadKeys.addActionListener(new UpdateCharsActionListener());
		this.charsIncludeLongPress = new JCheckBox("Include Long Press");
		this.charsIncludeLongPress.addActionListener(new UpdateCharsActionListener());
		this.charsVerbose = new JCheckBox("Show Details");
		this.charsVerbose.addActionListener(new UpdateCharsActionListener());
		this.chars = mono(new JTextArea());
		this.chars.setEditable(false);
		this.chars.setLineWrap(true);
		new UpdateCharsActionListener().actionPerformed(null);
		
		JPanel iconPanel = leftSxS(new JLabel("Icon:"), icon, 8);
		JPanel namePanel = leftSxS(new JLabel("Name:"), name, 8);
		JPanel topPanel = leftSxS(iconPanel, verticalCenter(namePanel), 16);
		
		JPanel winLabels = verticalStack("Short Name:", "Copyright:", "Company:");
		JPanel winFields = verticalStack(leftAlign(winIdentifier), winCopyright, winCompany);
		JPanel winLocSel = topSxS(new JLabel("Locale:"), winLocalePane, 4);
		JPanel winChecks = verticalStack(winAltGrEnable, winShiftLock, winLrmRlm);
		JPanel winPanel1 = verticalSxS(leftSxS(winLabels, winFields, 8), winLocSel, winChecks, 8);
		JPanel winPanel = left2Right1(winPanel1, winAttachments, 20);
		
		JPanel macLabels = verticalStack("Group Number:", "ID Number:", "Icon Version:");
		JPanel macFields = verticalStack(leftAlign(macGroupNumber), leftAlign(macIdNumber), leftAlign(macIconVersion));
		JPanel macActIDs = topSxS(new JLabel("Action IDs:"), macActionIdsPane, 4);
		JPanel macButton = leftAlign(horizontalStack(macActionIdsAdd, macActionIdsDelete));
		JPanel macPanel1 = verticalSxS(leftSxS(macLabels, macFields, 8), macActIDs, macButton, 8);
		JPanel macPanel = left2Right1(macPanel1, macAttachments, 20);
		
		JPanel xkbLabel1 = verticalStack("Path:", "Label:");
		JPanel xkbField1 = verticalStack(xkbPath, leftAlign(xkbLabel));
		JPanel xkbCommnt = topSxS(new JLabel("Comment:"), scrollWrap(xkbComment), 4);
		JPanel xkbLabel2 = verticalStack("AltGr Key:", "Compose Key:");
		JPanel xkbField2 = leftAlign(verticalStack(xkbAltGrKey, xkbComposeKey));
		JPanel xkbPanel1 = verticalSxS(leftSxS(xkbLabel1, xkbField1, 8), xkbCommnt, leftSxS(xkbLabel2, xkbField2, 8), 8);
		JPanel xkbPanel = left2Right1(xkbPanel1, xkbAttachments, 20);
		
		JPanel kmnLabels = verticalStack("ID:", "Name:", "Copyright:", "Message:", "Web Help Text:", "Keyboard Version:", "Author:", "Email Address:", "Web Site:");
		JPanel kmnFields = verticalStack(keymanIdentifier, keymanName, keymanCopyright, keymanMessage, keymanWebHelpText, keymanVersion, keymanAuthor, keymanEmailAddress, keymanWebSite);
		JPanel kmnCommts = topSxS(new JLabel("Comments:"), scrollWrap(keymanComments), 4);
		JPanel kmnPanelL = topSxS(leftSxS(kmnLabels, kmnFields, 8), kmnCommts, 8);
		JPanel kmnChecks = verticalStack(keymanRightToLeft, keymanKey102, keymanDisplayUnderlying, keymanUseAltGr, keymanIgnoreCaps);
		JPanel kmnTarget = topSxS(new JLabel("Targets:"), scrollWrap(topAlign(verticalStack(0, keymanTargets))), 4);
		JPanel kmnPlatfm = topSxS(new JLabel("Platforms:"), scrollWrap(topAlign(verticalStack(0, keymanPlatforms))), 4);
		JPanel kmnTgtPfm = horizontalStack(12, kmnTarget, kmnPlatfm);
		JPanel kmnLangsB = leftAlign(leftSxS(horizontalStack(keymanLanguagesAdd, keymanLanguagesDelete), horizontalStack(keymanLanguagesClear, keymanLanguagesCopy, keymanLanguagesPaste, keymanLanguagesSort), 4));
		JPanel kmnLangsP = verticalSxS(new JLabel("Languages:"), keymanLanguagesPane, kmnLangsB, 4);
		JPanel kmnPanelR = topSxS(kmnChecks, verticalStack(8, kmnTgtPfm, kmnLangsP), 8);
		JPanel kmnPanel = horizontalStack(20, kmnPanelL, kmnPanelR);
		
		JPanel kmnFontLb = verticalStack("Editor Font:", "OSK Font:", "Display Font:");
		JPanel kmnFontFd = verticalStack(keymanFontFamily, keymanOSKFontFile, keymanDisplayFontFile);
		JPanel kmnFontFm = leftSxS(kmnFontLb, kmnFontFd, 8);
		JPanel kmnCpLabs = topSxS(new JLabel("Code Point Labels:"), keymanCpLabelsPane, 4);
		JPanel kmnCpBtns = leftAlign(horizontalStack(keymanCpLabelsAdd, keymanCpLabelsDelete));
		JPanel kmnFontP1 = verticalSxS(kmnFontFm, kmnCpLabs, kmnCpBtns, 8);
		JPanel kmnFontP = left2Right1(kmnFontP1, keymanAttachments, 20);
		
		JPanel kmnRMDesc = leftSxS(new JLabel("Description:"), keymanDescription, 8);
		JPanel kmnRMText = verticalSxS(new JLabel("Readme Markdown:"), scrollWrap(keymanReadme), leftAlign(keymanReadmeDefault), 4);
		JPanel kmnRMPane = topSxS(kmnRMDesc, kmnRMText, 8);
		
		JPanel kmnLicTyp = leftSxS(new JLabel("License Type:"), leftAlign(keymanLicenseType), 8);
		JPanel kmnLicTxt = verticalSxS(new JLabel("License Markdown:"), scrollWrap(keymanLicenseText), leftAlign(keymanLicenseDefault), 4);
		JPanel kmnLicPnl = topSxS(kmnLicTyp, kmnLicTxt, 8);
		
		JPanel kmnHistP = verticalSxS(new JLabel("History Markdown:"), scrollWrap(keymanHistory), leftAlign(keymanHistoryDefault), 4);
		
		JPanel htmHLabel = verticalStack("Title:", "H1:", "H2:");
		JPanel htmHField = verticalStack(htmlTitle, htmlH1, htmlH2);
		JPanel htmStyles = topSxS(new JLabel("Stylesheet:"), scrollWrap(htmlStyle), 4);
		JPanel htmLLabel = verticalStack("Square Chars:", "Outline Chars:");
		JPanel htmLField = verticalStack(htmlSquareChars, htmlOutlineChars);
		JPanel htmTcLabs = topSxS(new JLabel("Cell Classes:"), htmlTdClassesPane, 4);
		JPanel htmTcBtns = leftAlign(horizontalStack(htmlTdClassesAdd, htmlTdClassesDelete));
		JPanel htmScLabs = topSxS(new JLabel("Span Classes:"), htmlSpanClassesPane, 4);
		JPanel htmScBtns = leftAlign(horizontalStack(htmlSpanClassesAdd, htmlSpanClassesDelete));
		JPanel htmCpLabs = topSxS(new JLabel("Code Point Labels:"), htmlCpLabelsPane, 4);
		JPanel htmCpBtns = leftAlign(horizontalStack(htmlCpLabelsAdd, htmlCpLabelsDelete));
		JPanel htmXxLabs = horizontalStack(htmTcLabs, htmScLabs, htmCpLabs);
		JPanel htmXxBtns = horizontalStack(htmTcBtns, htmScBtns, htmCpBtns);
		JPanel htmPanelH = topSxS(leftSxS(htmHLabel, htmHField, 8), htmStyles, 8);
		JPanel htmPanelL = verticalSxS(leftSxS(htmLLabel, htmLField, 8), htmXxLabs, htmXxBtns, 8);
		JPanel htmPanel1 = topSxS(new JLabel("Body HTML below header:"), scrollWrap(htmlBody1), 4);
		JPanel htmPanel2 = topSxS(new JLabel("Body HTML above layout:"), scrollWrap(htmlBody2), 4);
		JPanel htmPanel3 = topSxS(new JLabel("Body HTML below layout:"), scrollWrap(htmlBody3), 4);
		JPanel htmPanelI = verticalSxS(new JLabel("Installation instructions HTML:"), scrollWrap(htmlInstall), leftAlign(htmlInstallDefault), 4);
		JPanel htmPanel4 = topSxS(new JLabel("Footer HTML:"), scrollWrap(htmlBody4), 4);
		
		JPanel charsBtns = horizontalStack(12, charsIncludeDeadKeys, charsIncludeLongPress, charsVerbose);
		JPanel charsHead = rightSxS(new JLabel("Characters generated by layout:"), charsBtns, 12);
		JPanel charsPnl = topSxS(charsHead, scrollWrap(chars), 4);
		
		JTabbedPane tabs = new JTabbedPane(JTabbedPane.LEFT);
		tabs.add("Windows (MSKLC)", addBorder(winPanel, 20));
		tabs.add("Mac OS X", addBorder(macPanel, 20));
		tabs.add("Linux (XKB)", addBorder(xkbPanel, 20));
		tabs.add("Keyman", addBorder(kmnPanel, 20));
		tabs.add("Keyman Fonts", addBorder(kmnFontP, 20));
		tabs.add("Keyman Readme", addBorder(kmnRMPane, 20));
		tabs.add("Keyman License", addBorder(kmnLicPnl, 20));
		tabs.add("Keyman History", addBorder(kmnHistP, 20));
		tabs.add("HTML Header", addBorder(htmPanelH, 20));
		tabs.add("HTML Layout", addBorder(htmPanelL, 20));
		tabs.add("HTML Body 1", addBorder(htmPanel1, 20));
		tabs.add("HTML Body 2", addBorder(htmPanel2, 20));
		tabs.add("HTML Body 3", addBorder(htmPanel3, 20));
		tabs.add("HTML Install", addBorder(htmPanelI, 20));
		tabs.add("HTML Footer", addBorder(htmPanel4, 20));
		tabs.add("Characters", addBorder(charsPnl, 20));
		
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
	
	private static JPanel verticalStack(int gap, Component... comps) {
		JPanel p = new JPanel(new GridLayout(0,1,gap,gap));
		for (Component c : comps) p.add(c);
		return p;
	}
	
	private static JPanel horizontalStack(Component... comps) {
		JPanel p = new JPanel(new GridLayout(1,0,4,4));
		for (Component c : comps) p.add(c);
		return p;
	}
	
	private static JPanel horizontalStack(int gap, Component... comps) {
		JPanel p = new JPanel(new GridLayout(1,0,gap,gap));
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
	
	private static JPanel rightSxS(Component c, Component r, int gap) {
		JPanel p = new JPanel(new BorderLayout(gap, gap));
		p.add(c, BorderLayout.CENTER);
		p.add(r, BorderLayout.LINE_END);
		return p;
	}
	
	private static JPanel left2Right1(JComponent l, JComponent r, int gap) {
		JPanel p = new JPanel(new FixedGridBagLayout());
		FixedGridBagConstraints gbc = new FixedGridBagConstraints();
		gbc.gridwidth = 2;
		p.add(addBorder(l, 0, 0, 0, gap/2), gbc);
		gbc.gridx = 2;
		gbc.gridwidth = 1;
		p.add(addBorder(r, 0, gap/2, 0, 0), gbc);
		return p;
	}
	
	private static JPanel topAlign(Component c) {
		JPanel p = new JPanel(new BorderLayout());
		p.add(c, BorderLayout.PAGE_START);
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
	
	private static <C extends JComponent> C addBorder(C c, int t, int l, int b, int r) {
		c.setBorder(BorderFactory.createEmptyBorder(t, l, b, r));
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
	
	private static boolean hasExt(String name, String... exts) {
		name = name.toLowerCase();
		for (String ext : exts) if (name.endsWith(ext)) return true;
		return false;
	}
	
	private void updateKeymanFonts(KeyManAttachmentListModel alm) {
		String currentOSKFont;
		String currentDisplayFont;
		if (alm == null) {
			currentOSKFont = (km.keymanOSKFontFile != null && km.keymanOSKFontFile.length() > 0) ? km.keymanOSKFontFile : null;
			currentDisplayFont = (km.keymanDisplayFontFile != null && km.keymanDisplayFontFile.length() > 0) ? km.keymanDisplayFontFile : null;
		} else {
			currentOSKFont = (this.keymanOSKFontFile.getSelectedIndex() > 0) ? this.keymanOSKFontFile.getSelectedItem().toString() : null;
			currentDisplayFont = (this.keymanDisplayFontFile.getSelectedIndex() > 0) ? this.keymanDisplayFontFile.getSelectedItem().toString() : null;
		}
		
		ArrayList<String> options = new ArrayList<String>();
		options.add("None");
		if (alm == null) {
			for (String name : km.keymanAttachments.keySet()) {
				if (hasExt(name, ".ttf", ".otf", ".eot", ".woff", ".woff2")) {
					options.add(name);
				}
			}
		} else {
			for (int i = 0, n = alm.getSize(); i < n; i++) {
				String name = alm.getElementAt(i).toString();
				if (hasExt(name, ".ttf", ".otf", ".eot", ".woff", ".woff2")) {
					options.add(name);
				}
			}
		}
		
		this.keymanOSKFontFile.setModel(new DefaultComboBoxModel(options.toArray()));
		this.keymanDisplayFontFile.setModel(new DefaultComboBoxModel(options.toArray()));
		this.keymanOSKFontFile.setSelectedIndex(0);
		this.keymanDisplayFontFile.setSelectedIndex(0);
		if (currentOSKFont != null) this.keymanOSKFontFile.setSelectedItem(currentOSKFont);
		if (currentDisplayFont != null) this.keymanDisplayFontFile.setSelectedItem(currentDisplayFont);
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
		km.keymanIdentifier = this.keymanIdentifier.getText();
		km.keymanName = this.keymanName.getText();
		km.keymanCopyright = this.keymanCopyright.getText();
		km.keymanMessage = this.keymanMessage.getText();
		km.keymanWebHelpText = this.keymanWebHelpText.getText();
		km.keymanVersion = this.keymanVersion.getText();
		km.keymanComments = this.keymanComments.getText();
		km.keymanAuthor = this.keymanAuthor.getText();
		km.keymanEmailAddress = this.keymanEmailAddress.getText();
		km.keymanWebSite = this.keymanWebSite.getText();
		km.keymanRightToLeft = this.keymanRightToLeft.isSelected();
		km.keymanKey102 = this.keymanKey102.isSelected();
		km.keymanDisplayUnderlying = this.keymanDisplayUnderlying.isSelected();
		km.keymanUseAltGr = this.keymanUseAltGr.isSelected();
		km.keymanIgnoreCaps = this.keymanIgnoreCaps.isSelected();
		KeyManTarget[] targets = KeyManTarget.values();
		for (int i = 0; i < targets.length; i++) {
			if (this.keymanTargets[i].isSelected()) km.keymanTargets.add(targets[i]);
			else km.keymanTargets.remove(targets[i]);
		}
		KeyManPlatform[] platforms = KeyManPlatform.values();
		for (int i = 0; i < platforms.length; i++) {
			if (this.keymanPlatforms[i].isSelected()) km.keymanPlatforms.add(platforms[i]);
			else km.keymanPlatforms.remove(platforms[i]);
		}
		this.keymanLanguagesModel.toMap(km.keymanLanguages);
		this.keymanAttachments.toMap(km.keymanAttachments);
		KeyManProjectWriter.updateFileIds(km);
		this.keymanCpLabelsModel.toMap(km.keymanCpLabels);
		km.keymanFontFamily = this.keymanFontFamily.getText();
		km.keymanOSKFontFile = (
			(this.keymanOSKFontFile.getSelectedIndex() > 0) ?
			this.keymanOSKFontFile.getSelectedItem().toString() : null
		);
		km.keymanDisplayFontFile = (
			(this.keymanDisplayFontFile.getSelectedIndex() > 0) ?
			this.keymanDisplayFontFile.getSelectedItem().toString() : null
		);
		km.keymanDescription = this.keymanDescription.getText();
		km.keymanLicenseType = this.keymanLicenseType.getText();
		km.keymanLicenseText = this.keymanLicenseText.getText();
		km.keymanReadme = this.keymanReadme.getText();
		km.keymanHistory = this.keymanHistory.getText();
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
		km.htmlInstall = this.htmlInstall.getText();
		km.htmlSquareChars = KkbReader.parseRanges(this.htmlSquareChars.getText());
		km.htmlOutlineChars = KkbReader.parseRanges(this.htmlOutlineChars.getText());
		this.htmlTdClassesModel.toMap(km.htmlTdClasses);
		this.htmlSpanClassesModel.toMap(km.htmlSpanClasses);
		this.htmlCpLabelsModel.toMap(km.htmlCpLabels);
		this.winAttachments.toMap(km.winAttachments);
		this.macAttachments.toMap(km.macAttachments);
		this.xkbAttachments.toMap(km.xkbAttachments);
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
	
	private class KeymanDefaultActionListener implements ActionListener {
		private final String template;
		private final JTextArea textArea;
		private KeymanDefaultActionListener(String template, JTextArea textArea) {
			this.template = template;
			this.textArea = textArea;
		}
		public void actionPerformed(ActionEvent e) {
			StringBuffer sb = new StringBuffer();
			Scanner scan = KeyManWriterUtility.getTemplate(template);
			while (scan.hasNextLine()) {
				if (sb.length() > 0) sb.append("\n");
				sb.append(KeyManWriterUtility.replaceFields(scan.nextLine(), km));
			}
			scan.close();
			textArea.setText(sb.toString());
			if (textArea == keymanLicenseText) {
				keymanLicenseType.setText("mit");
			}
		}
	}
	
	private class HTMLInstallDefaultActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			StringBuffer sb = new StringBuffer();
			String rn = km.isWindowsNativeCompatible() ? "install.html" : "install-nonbmp.html";
			Scanner scan = HTMLWriterUtility.getTemplate(rn);
			while (scan.hasNextLine()) {
				if (sb.length() > 0) sb.append("\n");
				sb.append(HTMLWriterUtility.replaceFields(scan.nextLine(), km));
			}
			scan.close();
			htmlInstall.setText(sb.toString());
		}
	}
	
	private static class AddCodePointLabelActionListener implements ActionListener {
		private final CodePointLabelTableModel model;
		private final JTable table;
		private final JScrollPane pane;
		private AddCodePointLabelActionListener(CodePointLabelTableModel model, JTable table, JScrollPane pane) {
			this.model = model;
			this.table = table;
			this.pane = pane;
		}
		public void actionPerformed(ActionEvent e) {
			final int i = model.getRowCount();
			model.addEntry(32, "space");
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					table.getSelectionModel().setSelectionInterval(i, i);
					JScrollBar vsb = pane.getVerticalScrollBar();
					vsb.setValue(vsb.getMaximum());
				}
			});
		}
	}
	
	private static class DeleteCodePointLabelActionListener implements ActionListener {
		private final CodePointLabelTableModel model;
		private final JTable table;
		private DeleteCodePointLabelActionListener(CodePointLabelTableModel model, JTable table) {
			this.model = model;
			this.table = table;
		}
		public void actionPerformed(ActionEvent e) {
			int[] rows = table.getSelectedRows();
			for (int i = rows.length - 1; i >= 0; i--) {
				model.deleteEntry(rows[i]);
			}
		}
	}
	
	private static class AddCodePointClassActionListener implements ActionListener {
		private final CodePointClassTableModel model;
		private final JTable table;
		private final JScrollPane pane;
		private AddCodePointClassActionListener(CodePointClassTableModel model, JTable table, JScrollPane pane) {
			this.model = model;
			this.table = table;
			this.pane = pane;
		}
		public void actionPerformed(ActionEvent e) {
			final int i = model.getRowCount();
			model.addEntry("", new BitSet());
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					table.getSelectionModel().setSelectionInterval(i, i);
					JScrollBar vsb = pane.getVerticalScrollBar();
					vsb.setValue(vsb.getMaximum());
				}
			});
		}
	}
	
	private static class DeleteCodePointClassActionListener implements ActionListener {
		private final CodePointClassTableModel model;
		private final JTable table;
		private DeleteCodePointClassActionListener(CodePointClassTableModel model, JTable table) {
			this.model = model;
			this.table = table;
		}
		public void actionPerformed(ActionEvent e) {
			int[] rows = table.getSelectedRows();
			for (int i = rows.length - 1; i >= 0; i--) {
				model.deleteEntry(rows[i]);
			}
		}
	}
	
	private static class KeyManLanguageTableCellRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;
		private final KeyManLanguageTableModel model;
		private KeyManLanguageTableCellRenderer(KeyManLanguageTableModel model) {
			this.model = model;
		}
		public Component getTableCellRendererComponent(JTable table, Object value, boolean sel, boolean focus, int row, int col) {
			Component c = super.getTableCellRendererComponent(table, value, sel, focus, row, col);
			if (!sel) {
				Object v0 = model.getValueAt(row, 0);
				Object v1 = model.getValueAt(row, 1);
				if (v0 == null || v1 == null) {
					c.setBackground(Color.red);
					c.setForeground(Color.white);
				} else {
					String s0 = v0.toString();
					String s1 = v1.toString();
					if (s0 == null || s1 == null || s0.trim().length() == 0 || s1.trim().length() == 0) {
						c.setBackground(Color.red);
						c.setForeground(Color.white);
					} else {
						WinLocale loc0 = WinLocale.forTag(s0, null);
						WinLocale loc1 = WinLocale.forName(s1, null);
						if (loc0 != loc1) {
							c.setBackground(Color.orange);
							c.setForeground(Color.black);
						} else if (loc0 == null) {
							c.setBackground(Color.yellow);
							c.setForeground(Color.black);
						} else {
							c.setBackground(SystemColor.text);
							c.setForeground(SystemColor.textText);
						}
					}
				}
			}
			return c;
		}
	}
	
	private static class AddKeyManLanguageActionListener implements ActionListener {
		private final KeyManLanguageTableModel model;
		private final JTable table;
		private final JScrollPane pane;
		private AddKeyManLanguageActionListener(KeyManLanguageTableModel model, JTable table, JScrollPane pane) {
			this.model = model;
			this.table = table;
			this.pane = pane;
		}
		public void actionPerformed(ActionEvent e) {
			final int i = model.getRowCount();
			model.addEntry("en", "English");
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					table.getSelectionModel().setSelectionInterval(i, i);
					JScrollBar vsb = pane.getVerticalScrollBar();
					vsb.setValue(vsb.getMaximum());
				}
			});
		}
	}
	
	private static class DeleteKeyManLanguageActionListener implements ActionListener {
		private final KeyManLanguageTableModel model;
		private final JTable table;
		private DeleteKeyManLanguageActionListener(KeyManLanguageTableModel model, JTable table) {
			this.model = model;
			this.table = table;
		}
		public void actionPerformed(ActionEvent e) {
			int[] rows = table.getSelectedRows();
			for (int i = rows.length - 1; i >= 0; i--) {
				model.deleteEntry(rows[i]);
			}
		}
	}
	
	private static class ClearKeyManLanguageActionListener implements ActionListener {
		private final KeyManLanguageTableModel model;
		private ClearKeyManLanguageActionListener(KeyManLanguageTableModel model) {
			this.model = model;
		}
		public void actionPerformed(ActionEvent e) {
			model.clearEntries();
		}
	}
	
	private static class CopyKeyManLanguageActionListener implements ActionListener {
		private final KeyManLanguageTableModel model;
		private CopyKeyManLanguageActionListener(KeyManLanguageTableModel model) {
			this.model = model;
		}
		public void actionPerformed(ActionEvent e) {
			StringBuffer sb = new StringBuffer();
			for (int i = 0, n = model.getRowCount(); i < n; i++) {
				sb.append(model.getValueAt(i, 0));
				sb.append('\t');
				sb.append(model.getValueAt(i, 1));
				sb.append('\n');
			}
			Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
			StringSelection sel = new StringSelection(sb.toString());
			cb.setContents(sel, sel);
		}
	}
	
	private static class PasteKeyManLanguageActionListener implements ActionListener {
		private final KeyManLanguageTableModel model;
		private PasteKeyManLanguageActionListener(KeyManLanguageTableModel model) {
			this.model = model;
		}
		public void actionPerformed(ActionEvent e) {
			Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
			if (cb.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
				try {
					String s = cb.getData(DataFlavor.stringFlavor).toString();
					s = s.replace("\r\n|\r|\n", "\n");
					
					// Parse copied data from hyperglot
					s = s.replaceAll("ISO code: (\\w+)(\nautonym: .+(\n.+)*)?\nname: (.+)(\n.+)*", "$1\t$4");
					
					LinkedHashMap<String,String> langs = new LinkedHashMap<String,String>();
					lines: for (String line : s.trim().split("\n")) {
						if ((line = line.trim()).length() > 0) {
							WinLocale loc = WinLocale.forTag(line, null);
							if (loc != null) { langs.put(loc.tag, loc.name); continue lines; }
							loc = WinLocale.forName(line, null);
							if (loc != null) { langs.put(loc.tag, loc.name); continue lines; }
							
							String[] tabbed = line.split("\t");
							String[] space2 = line.split("\\s+", 2);
							String[] spaced = line.split("\\s+");
							
							for (String[] fields : Arrays.asList(tabbed, space2, spaced)) {
								for (String field : fields) {
									if ((field = field.trim()).length() > 0) {
										loc = WinLocale.forTag(field, null);
										if (loc != null) { langs.put(loc.tag, loc.name); continue lines; }
										loc = WinLocale.forName(field, null);
										if (loc != null) { langs.put(loc.tag, loc.name); continue lines; }
									}
								}
							}
							
							for (String[] fields : Arrays.asList(tabbed, space2, spaced)) {
								if (fields.length == 2) {
									langs.put(fields[0].trim(), fields[1].trim());
									continue lines;
								}
							}
							
							langs.put(line, line);
						}
					}
					
					if (!langs.isEmpty()) {
						model.clearEntries();
						model.addEntries(langs);
						return;
					}
				}
				catch (UnsupportedFlavorException e1) {}
				catch (IOException e1) {}
			}
			Toolkit.getDefaultToolkit().beep();
		}
	}
	
	private static class SortKeyManLanguageActionListener implements ActionListener {
		private final KeyManLanguageTableModel model;
		private SortKeyManLanguageActionListener(KeyManLanguageTableModel model) {
			this.model = model;
		}
		public void actionPerformed(ActionEvent e) {
			model.sortEntries();
		}
	}
	
	private class UpdateCharsActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			boolean includeDeadKeys = charsIncludeDeadKeys.isSelected();
			boolean includeLongPress = charsIncludeLongPress.isSelected();
			boolean verbose = charsVerbose.isSelected();
			TreeSet<Integer> all = new TreeSet<Integer>();
			km.getAllOutputs(all, includeDeadKeys, includeLongPress);
			StringBuffer sb = new StringBuffer();
			for (int output : all) {
				if (output >= 32) {
					if (verbose) {
						String h = Integer.toHexString(output);
						while (h.length() < 4) h = "0" + h;
						sb.append("U+" + h.toUpperCase() + "\t");
					}
					sb.append(Character.toChars(output));
					if (verbose) {
						String n = NameResolver.instance(output).getName(output);
						sb.append("\t" + n + "\n");
					}
				}
			}
			chars.setText(sb.toString());
		}
	}
}
