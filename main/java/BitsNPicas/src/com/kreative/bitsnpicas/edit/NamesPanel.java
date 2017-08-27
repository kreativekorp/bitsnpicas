package com.kreative.bitsnpicas.edit;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Scrollable;
import com.kreative.bitsnpicas.Font;
import com.kreative.bitsnpicas.FontGlyph;

public class NamesPanel extends JPanel implements Scrollable {
	private static final long serialVersionUID = 1L;
	
	public static final Map<Integer,String> NAME_TYPE_NAMES;
	static {
		Map<Integer,String> ntn = new LinkedHashMap<Integer,String>();
		ntn.put(Font.NAME_FAMILY_AND_STYLE, "Font Family & Style Name");
		ntn.put(Font.NAME_FAMILY, "Font Family Name");
		ntn.put(Font.NAME_STYLE, "Font Style Name");
		ntn.put(Font.NAME_POSTSCRIPT, "PostScript Name");
		ntn.put(Font.NAME_WINDOWS_FAMILY, "Windows Family Name");
		ntn.put(Font.NAME_WINDOWS_STYLE, "Windows Style Name");
		ntn.put(Font.NAME_MACOS_FAMILY_AND_STYLE, "Mac OS Family & Style Name");
		ntn.put(Font.NAME_WWS_FAMILY, "WWS Family Name");
		ntn.put(Font.NAME_WWS_STYLE, "WWS Style Name");
		ntn.put(Font.NAME_VERSION, "Version Number");
		ntn.put(Font.NAME_UNIQUE_ID, "Unique ID");
		ntn.put(Font.NAME_POSTSCRIPT_CID, "PostScript CID");
		ntn.put(Font.NAME_COPYRIGHT, "Copyright Notice");
		ntn.put(Font.NAME_TRADEMARK, "Trademark Notice");
		ntn.put(Font.NAME_DESCRIPTION, "Description");
		ntn.put(Font.NAME_MANUFACTURER, "Manufacturer");
		ntn.put(Font.NAME_VENDOR_URL, "Vendor URL");
		ntn.put(Font.NAME_DESIGNER, "Designer");
		ntn.put(Font.NAME_DESIGNER_URL, "Designer URL");
		ntn.put(Font.NAME_LICENSE_DESCRIPTION, "License Description");
		ntn.put(Font.NAME_LICENSE_URL, "License URL");
		ntn.put(Font.NAME_SAMPLE_TEXT, "Sample Text");
		NAME_TYPE_NAMES = Collections.unmodifiableMap(ntn);
	}
	
	private final Map<Integer,JTextField> fields;
	
	public NamesPanel() {
		JPanel labelPanel = new JPanel(new GridLayout(0, 1, 4, 4));
		JPanel fieldPanel = new JPanel(new GridLayout(0, 1, 4, 4));
		fields = new HashMap<Integer,JTextField>();
		for (Map.Entry<Integer,String> e : NAME_TYPE_NAMES.entrySet()) {
			JLabel label = new JLabel(e.getValue());
			JTextField field = new JTextField();
			labelPanel.add(label);
			fieldPanel.add(field);
			fields.put(e.getKey(), field);
		}
		
		JButton autoFillButton = new JButton("Auto Fill");
		JPanel buttonPanel = new JPanel(new FlowLayout());
		buttonPanel.add(autoFillButton);
		
		JPanel mainPanel = new JPanel(new BorderLayout(8, 8));
		mainPanel.add(labelPanel, BorderLayout.LINE_START);
		mainPanel.add(fieldPanel, BorderLayout.CENTER);
		mainPanel.add(buttonPanel, BorderLayout.PAGE_END);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
		setLayout(new BorderLayout());
		add(mainPanel, BorderLayout.PAGE_START);
		
		autoFillButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				autoFillNames();
			}
		});
	}
	
	public void readFrom(Font<?> font) {
		for (Map.Entry<Integer,JTextField> e : fields.entrySet()) {
			e.getValue().setText(font.getName(e.getKey()));
		}
	}
	
	public void writeTo(Font<?> font) {
		for (Map.Entry<Integer,JTextField> e : fields.entrySet()) {
			String name = e.getValue().getText().trim();
			if (name.length() == 0) font.removeName(e.getKey());
			else font.setName(e.getKey(), name);
		}
	}
	
	public void autoFillNames() {
		BogusFont bogus = new BogusFont();
		writeTo(bogus);
		bogus.autoFillNames();
		readFrom(bogus);
	}
	
	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}
	
	public boolean getScrollableTracksViewportWidth() {
		return true;
	}
	
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}
	
	public int getScrollableUnitIncrement(Rectangle vr, int or, int dir) {
		return fields.get(0).getPreferredSize().height + 4;
	}
	
	public int getScrollableBlockIncrement(Rectangle vr, int or, int dir) {
		int h = fields.get(0).getPreferredSize().height + 4;
		return (vr.height / h) * h;
	}
	
	private static class BogusFont extends Font<BogusGlyph> {
		public int getEmAscent() { return 0; }
		public int getEmDescent() { return 0; }
		public int getLineAscent() { return 0; }
		public int getLineDescent() { return 0; }
		public int getXHeight() { return 0; }
		public int getLineGap() { return 0; }
		public double getEmAscent2D() { return 0; }
		public double getEmDescent2D() { return 0; }
		public double getLineAscent2D() { return 0; }
		public double getLineDescent2D() { return 0; }
		public double getXHeight2D() { return 0; }
		public double getLineGap2D() { return 0; }
		public void setEmAscent(int v) {}
		public void setEmDescent(int v) {}
		public void setLineAscent(int v) {}
		public void setLineDescent(int v) {}
		public void setXHeight(int v) {}
		public void setLineGap(int v) {}
		public void setEmAscent2D(double v) {}
		public void setEmDescent2D(double v) {}
		public void setLineAscent2D(double v) {}
		public void setLineDescent2D(double v) {}
		public void setXHeight2D(double v) {}
		public void setLineGap2D(double v) {}
	}
	
	private static class BogusGlyph extends FontGlyph {
		public int getGlyphOffset() { return 0; }
		public int getGlyphWidth() { return 0; }
		public int getGlyphHeight() { return 0; }
		public int getGlyphAscent() { return 0; }
		public int getGlyphDescent() { return 0; }
		public int getCharacterWidth() { return 0; }
		public double getGlyphOffset2D() { return 0; }
		public double getGlyphWidth2D() { return 0; }
		public double getGlyphHeight2D() { return 0; }
		public double getGlyphAscent2D() { return 0; }
		public double getGlyphDescent2D() { return 0; }
		public double getCharacterWidth2D() { return 0; }
		public void setCharacterWidth(int v) {}
		public void setCharacterWidth2D(double v) {}
		public double paint(Graphics g, double x, double y, double scale) { return 0; }
	}
}
