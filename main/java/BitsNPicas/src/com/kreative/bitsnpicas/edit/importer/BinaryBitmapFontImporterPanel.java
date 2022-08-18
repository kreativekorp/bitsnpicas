package com.kreative.bitsnpicas.edit.importer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import com.kreative.bitsnpicas.edit.Main;
import com.kreative.bitsnpicas.importer.BinaryBitmapFontImporter;
import com.kreative.unicode.data.NameDatabase;
import com.kreative.unicode.data.NameResolver;

public class BinaryBitmapFontImporterPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private final File file;
	private final byte[] data;
	private final BinaryBitmapFontImporter importer;
	
	private List<BufferedImage> previewImages;
	private final RawImportEncodingList eList = new RawImportEncodingList();
	private final EncodingTableModel eTableModel = new EncodingTableModel();
	private final JTable eTable = new JTable(eTableModel);
	private final NameDatabase ndb = NameDatabase.instance();
	
	public BinaryBitmapFontImporterPanel(File file) throws IOException {
		FileInputStream in = new FileInputStream(file);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buf = new byte[1048576]; int read;
		while ((read = in.read(buf)) >= 0) out.write(buf, 0, read);
		out.close();
		in.close();
		this.file = file;
		this.data = out.toByteArray();
		this.importer = new BinaryBitmapFontImporter();
		makeUI();
		updatePreview();
	}
	
	private void makeUI() {
		final SpinnerNumberModel offset = new SpinnerNumberModel(importer.offset, 0, data.length, 1);
		final SpinnerNumberModel bytesPerChar = new SpinnerNumberModel(importer.bytesPerChar, 0, data.length, 1);
		final SpinnerNumberModel bytesPerRow = new SpinnerNumberModel(importer.bytesPerRow, 0, data.length, 1);
		final SpinnerNumberModel cellWidth = new SpinnerNumberModel(importer.cellWidth, 1, data.length, 1);
		final SpinnerNumberModel cellHeight = new SpinnerNumberModel(importer.cellHeight, 1, data.length, 1);
		final SpinnerNumberModel ascent = new SpinnerNumberModel(importer.ascent, -data.length, data.length, 1);
		final SpinnerNumberModel bitsPerPixel = new SpinnerNumberModel(importer.bitsPerPixel, 1, 8, 1);
		final JCheckBox invert = new JCheckBox("Invert"); invert.setSelected(importer.invert);
		final JCheckBox rightAlign = new JCheckBox("Right-Align"); rightAlign.setSelected(importer.rightAlign);
		final JCheckBox flipBits = new JCheckBox("Reverse Bits"); flipBits.setSelected(importer.flipBits);
		final JCheckBox flipBytes = new JCheckBox("Reverse Bytes"); flipBytes.setSelected(importer.flipBytes);
		final SpinnerNumberModel cellCount = new SpinnerNumberModel(importer.cellCount, 1, data.length, 1);
		
		final JPanel offsetPanel = borderLayout(0, 0, jSpinner(offset), BorderLayout.LINE_START);
		final JPanel bytesPerCharPanel = borderLayout(0, 0, jSpinner(bytesPerChar), BorderLayout.LINE_START);
		final JPanel bytesPerRowPanel = borderLayout(0, 0, jSpinner(bytesPerRow), BorderLayout.LINE_START);
		final JPanel cellWidthPanel = borderLayout(0, 0, jSpinner(cellWidth), BorderLayout.LINE_START);
		final JPanel cellHeightPanel = borderLayout(0, 0, jSpinner(cellHeight), BorderLayout.LINE_START);
		final JPanel ascentPanel = borderLayout(0, 0, jSpinner(ascent), BorderLayout.LINE_START);
		final JPanel bitsPerPixelPanel = borderLayout(0, 0, jSpinner(bitsPerPixel), BorderLayout.LINE_START);
		final JPanel cellCountPanel = borderLayout(0, 0, jSpinner(cellCount), BorderLayout.LINE_START);
		
		final JPanel labelPanel = gridLayout(0, 1, 4, 4,
			new JLabel("Offset:"), new JLabel("Bytes Per Char.:"), new JLabel("Bytes Per Row:"),
			new JLabel("Char. Width:"), new JLabel("Char. Height:"), new JLabel("Ascent:"),
			new JLabel("Bits Per Pixel:"), new JLabel("Bit Format:"),
			new JLabel(" "), new JLabel(" "), new JLabel(" "),
			new JLabel("Characters:")
		);
		final JPanel ctrlPanel = gridLayout(0, 1, 4, 4,
			offsetPanel, bytesPerCharPanel, bytesPerRowPanel,
			cellWidthPanel, cellHeightPanel, ascentPanel,
			bitsPerPixelPanel, rightAlign,
			flipBytes, flipBits, invert,
			cellCountPanel
		);
		final JPanel settingsPanel = borderLayout(8, 8, labelPanel, BorderLayout.LINE_START, ctrlPanel, BorderLayout.CENTER);
		
		eTable.setDefaultRenderer(BufferedImage.class, new GlyphCellRenderer());
		setColumnWidth(eTable.getColumnModel().getColumn(1), 80);
		setColumnWidth(eTable.getColumnModel().getColumn(2), 80);
		final JScrollPane eScrollPane = new JScrollPane(eTable);
		final JPanel eListPanel = borderLayout(8, 8, new JLabel("Encoding:"), BorderLayout.LINE_START, eList, BorderLayout.CENTER);
		final JPanel eTablePanel = borderLayout(4, 4, eListPanel, BorderLayout.PAGE_START, eScrollPane, BorderLayout.CENTER);
		
		final JButton openButton = new JButton("Create");
		final JPanel buttonPanel = flowLayout(openButton);
		
		final JPanel mainPanel = new JPanel(new BorderLayout(12, 12));
		mainPanel.add(settingsPanel, BorderLayout.LINE_START);
		mainPanel.add(eTablePanel, BorderLayout.CENTER);
		mainPanel.add(buttonPanel, BorderLayout.PAGE_END);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		setLayout(new GridLayout(1, 1, 0, 0));
		add(mainPanel);
		
		offset.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				importer.offset = offset.getNumber().intValue();
				updatePreview();
			}
		});
		bytesPerChar.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				importer.bytesPerChar = bytesPerChar.getNumber().intValue();
				updatePreview();
			}
		});
		bytesPerRow.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				importer.bytesPerRow = bytesPerRow.getNumber().intValue();
				updatePreview();
			}
		});
		cellWidth.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				importer.cellWidth = cellWidth.getNumber().intValue();
				updatePreview();
			}
		});
		cellHeight.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				importer.cellHeight = cellHeight.getNumber().intValue();
				updatePreview();
			}
		});
		ascent.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				importer.ascent = ascent.getNumber().intValue();
				updatePreview();
			}
		});
		bitsPerPixel.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				importer.bitsPerPixel = bitsPerPixel.getNumber().intValue();
				updatePreview();
			}
		});
		invert.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				importer.invert = invert.isSelected();
				updatePreview();
			}
		});
		rightAlign.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				importer.rightAlign = rightAlign.isSelected();
				updatePreview();
			}
		});
		flipBits.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				importer.flipBits = flipBits.isSelected();
				updatePreview();
			}
		});
		flipBytes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				importer.flipBytes = flipBytes.isSelected();
				updatePreview();
			}
		});
		cellCount.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				importer.cellCount = cellCount.getNumber().intValue();
				updatePreview();
			}
		});
		
		eList.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				eList.applySelectedEncoding(importer.encoding);
				eTableModel.fireTableRowsUpdated(0, importer.encoding.size());
			}
		});
		
		openButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Main.openFonts(file, null, importer.importFont(data));
			}
		});
	}
	
	private void updatePreview() {
		previewImages = importer.preview(data);
		List<Integer> selectedEncoding = eList.getSelectedEncoding();
		if (importer.encoding == null) importer.encoding = new ArrayList<Integer>();
		for (int es = importer.encoding.size(), ps = previewImages.size(); es < ps; es++) {
			importer.encoding.add(
				(selectedEncoding != null && es < selectedEncoding.size())
				? selectedEncoding.get(es) : -1
			);
		}
		for (int ps = previewImages.size(), es = importer.encoding.size(); ps < es; es--) {
			importer.encoding.remove(ps);
		}
		eTableModel.fireTableDataChanged();
		eTable.setRowHeight(Math.max(16, importer.cellHeight + 4));
		setColumnWidth(eTable.getColumnModel().getColumn(0), Math.max(20, importer.cellWidth + 8));
	}
	
	private class EncodingTableModel extends AbstractTableModel {
		private static final long serialVersionUID = 1L;
		public Class<?> getColumnClass(int col) {
			switch (col) {
				case 0: return BufferedImage.class;
				case 1: return String.class;
				case 2: return String.class;
				case 3: return String.class;
				default: return Object.class;
			}
		}
		public int getColumnCount() {
			return 4;
		}
		public String getColumnName(int col) {
			switch (col) {
				case 0: return "Glyph";
				case 1: return "Code Point";
				case 2: return "Character";
				case 3: return "Character Name";
				default: return null;
			}
		}
		public int getRowCount() {
			if (importer.encoding == null) return 0;
			return importer.encoding.size();
		}
		public Object getValueAt(int row, int col) {
			switch (col) {
				case 0:
					if (previewImages == null) return null;
					if (row < previewImages.size()) return previewImages.get(row);
					return null;
				case 1:
					if (importer.encoding == null) return null;
					if (row < importer.encoding.size()) {
						Integer e = importer.encoding.get(row);
						if (e == null || e.intValue() < 0) return null;
						String h = Integer.toHexString(e.intValue()).toUpperCase();
						while (h.length() < 4) h = "0" + h;
						return h;
					}
					return null;
				case 2:
					if (importer.encoding == null) return null;
					if (row < importer.encoding.size()) {
						Integer e = importer.encoding.get(row);
						if (e == null || e.intValue() < 0) return null;
						return String.valueOf(Character.toChars(e.intValue()));
					}
					return null;
				case 3:
					if (importer.encoding == null) return null;
					if (row < importer.encoding.size()) {
						Integer e = importer.encoding.get(row);
						if (e == null || e.intValue() < 0) return null;
						return NameResolver.instance(e).getName(e);
					}
					return null;
				default:
					return null;
			}
		}
		public boolean isCellEditable(int row, int col) {
			return (col == 1 || col == 2 || col == 3);
		}
		public void setValueAt(Object val, int row, int col) {
			switch (col) {
				case 1:
					if (importer.encoding == null) return;
					if (row < importer.encoding.size()) {
						if (val == null || val.toString().length() == 0) {
							importer.encoding.set(row, -1);
							eList.setSelectedIndex(0);
						} else try {
							int e = Integer.parseInt(val.toString(), 16);
							importer.encoding.set(row, e);
							eList.setSelectedIndex(0);
						} catch (NumberFormatException e) {
							// ignored
						}
					}
					return;
				case 2:
					if (importer.encoding == null) return;
					if (row < importer.encoding.size()) {
						if (val == null || val.toString().length() == 0) {
							importer.encoding.set(row, -1);
							eList.setSelectedIndex(0);
						} else {
							int e = val.toString().codePointAt(0);
							importer.encoding.set(row, e);
							eList.setSelectedIndex(0);
						}
					}
					return;
				case 3:
					if (importer.encoding == null) return;
					if (row < importer.encoding.size()) {
						if (val == null || val.toString().length() == 0) {
							importer.encoding.set(row, -1);
							eList.setSelectedIndex(0);
						} else {
							NameDatabase.NameEntry ne = ndb.find(val.toString());
							if (ne == null) importer.encoding.set(row, -1);
							else importer.encoding.set(row, ne.codePoint);
							eList.setSelectedIndex(0);
						}
					}
					return;
			}
			fireTableRowsUpdated(row, row);
		}
	}
	
	private class GlyphCellRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;
		public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean focus, int row, int col) {
			Component c = super.getTableCellRendererComponent(t, v, sel, focus, row, col);
			if (c instanceof JLabel && v instanceof BufferedImage) {
				final JLabel label = (JLabel)c;
				final BufferedImage bi = (BufferedImage)v;
				label.setText(null);
				label.setIcon(new ImageIcon(bi));
			}
			return c;
		}
	}
	
	private static JPanel borderLayout(int dx, int dy, Component comp, String cons) {
		JPanel p = new JPanel(new BorderLayout(dx, dy));
		p.add(comp, cons);
		return p;
	}
	
	private static JPanel borderLayout(int dx, int dy, Component comp1, String cons1, Component comp2, String cons2) {
		JPanel p = new JPanel(new BorderLayout(dx, dy));
		p.add(comp1, cons1);
		p.add(comp2, cons2);
		return p;
	}
	
	private static JPanel flowLayout(Component... comps) {
		JPanel p = new JPanel(new FlowLayout());
		for (Component c : comps) p.add(c);
		return p;
	}
	
	private static JPanel gridLayout(int rows, int cols, int dx, int dy, Component... comps) {
		JPanel p = new JPanel(new GridLayout(rows, cols, dx, dy));
		for (Component c : comps) p.add(c);
		return p;
	}
	
	private static JSpinner jSpinner(SpinnerNumberModel m) {
		JSpinner s = new JSpinner(m);
		Dimension d = s.getPreferredSize();
		d.width = 80;
		s.setMinimumSize(d);
		s.setPreferredSize(d);
		s.setMaximumSize(d);
		return s;
	}
	
	private static void setColumnWidth(TableColumn col, int width) {
		col.setMinWidth(width);
		col.setWidth(width);
		col.setMaxWidth(width);
	}
}
