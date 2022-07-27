package com.kreative.bitsnpicas.edit.importer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
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
import com.kreative.bitsnpicas.WindingOrder;
import com.kreative.bitsnpicas.edit.Main;
import com.kreative.bitsnpicas.importer.ImageBitmapFontImporter;
import com.kreative.bitsnpicas.importer.ImageBitmapFontImporter.PreviewResult;
import com.kreative.unicode.data.NameDatabase;
import com.kreative.unicode.data.NameResolver;

public class ImageBitmapFontImporterPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private final File file;
	private final BufferedImage image;
	private final ImageBitmapFontImporter importer;
	
	private BufferedImage previewImage = null;
	private Point[] previewPoints = null;
	private final JLabel previewLabel = new JLabel();
	private final EncodingTableModel eTableModel = new EncodingTableModel();
	private final JTable eTable = new JTable(eTableModel);
	private final NameDatabase ndb = NameDatabase.instance();
	
	public ImageBitmapFontImporterPanel(File file) throws IOException {
		this.file = file;
		this.image = ImageIO.read(file);
		this.importer = new ImageBitmapFontImporter();
		makeUI();
		updatePreview();
	}
	
	private void makeUI() {
		final JComboBox matte = new JComboBox(new String[]{"White", "Black"}); matte.setEditable(false);
		final SpinnerNumberModel startX = new SpinnerNumberModel(importer.startX, 0, image.getWidth(), 1);
		final SpinnerNumberModel startY = new SpinnerNumberModel(importer.startY, 0, image.getHeight(), 1);
		final SpinnerNumberModel cellWidth = new SpinnerNumberModel(importer.cellWidth, 1, image.getWidth(), 1);
		final SpinnerNumberModel cellHeight = new SpinnerNumberModel(importer.cellHeight, 1, image.getHeight(), 1);
		final SpinnerNumberModel ascent = new SpinnerNumberModel(importer.ascent, -image.getHeight(), image.getHeight(), 1);
		final SpinnerNumberModel deltaX = new SpinnerNumberModel(importer.deltaX, 0, image.getWidth(), 1);
		final SpinnerNumberModel deltaY = new SpinnerNumberModel(importer.deltaY, 0, image.getHeight(), 1);
		final SpinnerNumberModel columnCount = new SpinnerNumberModel(importer.columnCount, 0, image.getWidth(), 1);
		final SpinnerNumberModel rowCount = new SpinnerNumberModel(importer.rowCount, 0, image.getHeight(), 1);
		final JComboBox order = new JComboBox(WindingOrder.values()); order.setEditable(false);
		final JCheckBox invert = new JCheckBox("Invert"); invert.setSelected(importer.invert);
		final SpinnerNumberModel threshold = new SpinnerNumberModel(importer.threshold, 0, 255, 1);
		
		final JPanel startPanel = borderLayout(0, 0, boxLayout(BoxLayout.LINE_AXIS, jSpinner(startX), new JLabel(" , "), jSpinner(startY)), BorderLayout.LINE_START);
		final JPanel cellWidthPanel = borderLayout(0, 0, jSpinner(cellWidth), BorderLayout.LINE_START);
		final JPanel cellHeightPanel = borderLayout(0, 0, jSpinner(cellHeight), BorderLayout.LINE_START);
		final JPanel ascentPanel = borderLayout(0, 0, jSpinner(ascent), BorderLayout.LINE_START);
		final JPanel deltaPanel = borderLayout(0, 0, boxLayout(BoxLayout.LINE_AXIS, jSpinner(deltaX), new JLabel(" , "), jSpinner(deltaY)), BorderLayout.LINE_START);
		final JPanel columnCountPanel = borderLayout(0, 0, jSpinner(columnCount), BorderLayout.LINE_START);
		final JPanel rowCountPanel = borderLayout(0, 0, jSpinner(rowCount), BorderLayout.LINE_START);
		final JPanel thresholdPanel = borderLayout(0, 0, boxLayout(BoxLayout.LINE_AXIS, jSpinner(threshold), new JLabel(" "), invert), BorderLayout.LINE_START);
		
		final JPanel labelPanel = gridLayout(0, 1, 4, 4,
			new JLabel("Matte:"), new JLabel("Offset:"),
			new JLabel("Char. Width:"), new JLabel("Char. Height:"), new JLabel("Ascent:"),
			new JLabel("Spacing:"), new JLabel("Columns:"), new JLabel("Rows:"),
			new JLabel("Order:"), new JLabel("Threshold:")
		);
		final JPanel ctrlPanel = gridLayout(0, 1, 4, 4,
			matte, startPanel,
			cellWidthPanel, cellHeightPanel, ascentPanel,
			deltaPanel, columnCountPanel, rowCountPanel,
			order, thresholdPanel
		);
		final JPanel settingsPanel = borderLayout(8, 8, labelPanel, BorderLayout.LINE_START, ctrlPanel, BorderLayout.CENTER);
		
		final JScrollPane previewPane = new JScrollPane(previewLabel);
		previewPane.setPreferredSize(new Dimension(300, 200));
		
		final JPanel leftPanel = borderLayout(12, 12, settingsPanel, BorderLayout.PAGE_START, previewPane, BorderLayout.CENTER);
		
		eTable.setDefaultRenderer(Point.class, new GlyphCellRenderer());
		setColumnWidth(eTable.getColumnModel().getColumn(1), 80);
		setColumnWidth(eTable.getColumnModel().getColumn(2), 80);
		final JScrollPane eScrollPane = new JScrollPane(eTable);
		
		final JButton openButton = new JButton("Create");
		final JPanel buttonPanel = flowLayout(openButton);
		
		final JPanel mainPanel = new JPanel(new BorderLayout(12, 12));
		mainPanel.add(leftPanel, BorderLayout.LINE_START);
		mainPanel.add(eScrollPane, BorderLayout.CENTER);
		mainPanel.add(buttonPanel, BorderLayout.PAGE_END);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		setLayout(new GridLayout(1, 1, 0, 0));
		add(mainPanel);
		
		matte.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				importer.matte = matte.getSelectedIndex() - 1;
				updatePreview();
			}
		});
		startX.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				importer.startX = startX.getNumber().intValue();
				updatePreview();
			}
		});
		startY.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				importer.startY = startY.getNumber().intValue();
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
		deltaX.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				importer.deltaX = deltaX.getNumber().intValue();
				updatePreview();
			}
		});
		deltaY.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				importer.deltaY = deltaY.getNumber().intValue();
				updatePreview();
			}
		});
		columnCount.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				importer.columnCount = columnCount.getNumber().intValue();
				updatePreview();
			}
		});
		rowCount.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				importer.rowCount = rowCount.getNumber().intValue();
				updatePreview();
			}
		});
		order.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				importer.order = (WindingOrder)order.getSelectedItem();
				updatePreview();
			}
		});
		invert.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				importer.invert = invert.isSelected();
				updatePreview();
			}
		});
		threshold.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				importer.threshold = threshold.getNumber().intValue();
				updatePreview();
			}
		});
		
		openButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Main.openFont(file, null, importer.importFont(image));
			}
		});
	}
	
	private void updatePreview() {
		PreviewResult pr = importer.preview(image);
		previewImage = pr.preview;
		previewPoints = pr.points;
		previewLabel.setIcon(new ImageIcon(pr.preview));
		if (importer.encoding == null) importer.encoding = new ArrayList<Integer>();
		for (int es = importer.encoding.size(), ps = pr.points.length; es < ps; importer.encoding.add(0xF0000 + es), es++);
		for (int ps = pr.points.length, es = importer.encoding.size(); ps < es; importer.encoding.remove(ps), es--);
		eTableModel.fireTableDataChanged();
		eTable.setRowHeight(Math.max(16, importer.cellHeight + 4));
		setColumnWidth(eTable.getColumnModel().getColumn(0), Math.max(20, importer.cellWidth + 8));
	}
	
	private class EncodingTableModel extends AbstractTableModel {
		private static final long serialVersionUID = 1L;
		public Class<?> getColumnClass(int col) {
			switch (col) {
				case 0: return Point.class;
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
					if (previewPoints == null) return null;
					if (row < previewPoints.length) return previewPoints[row];
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
						} else try {
							int e = Integer.parseInt(val.toString(), 16);
							importer.encoding.set(row, e);
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
						} else {
							int e = val.toString().codePointAt(0);
							importer.encoding.set(row, e);
						}
					}
					return;
				case 3:
					if (importer.encoding == null) return;
					if (row < importer.encoding.size()) {
						if (val == null || val.toString().length() == 0) {
							importer.encoding.set(row, -1);
						} else {
							NameDatabase.NameEntry ne = ndb.find(val.toString());
							if (ne == null) importer.encoding.set(row, -1);
							else importer.encoding.set(row, ne.codePoint);
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
			if (c instanceof JLabel && v instanceof Point) {
				final JLabel label = (JLabel)c;
				final Point p = (Point)v;
				label.setText(null);
				label.setIcon(new Icon() {
					public int getIconHeight() { return importer.cellHeight; }
					public int getIconWidth() { return importer.cellWidth; }
					public void paintIcon(Component c, Graphics g, int x, int y) {
						g.drawImage(
							previewImage, x, y, x + importer.cellWidth, y + importer.cellHeight,
							p.x, p.y, p.x + importer.cellWidth, p.y + importer.cellHeight, null
						);
					}
				});
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
	
	private static JPanel boxLayout(int axis, Component... comps) {
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, axis));
		for (Component c : comps) p.add(c);
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
