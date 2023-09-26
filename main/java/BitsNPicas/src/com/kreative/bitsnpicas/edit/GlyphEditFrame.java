package com.kreative.bitsnpicas.edit;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import com.kreative.bitsnpicas.Font;
import com.kreative.bitsnpicas.FontGlyph;

public class GlyphEditFrame<G extends FontGlyph> extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private final GlyphEditPanel<G> panel;
	private final GlyphEditMenuBar<G> mb;
	
	public GlyphEditFrame(Class<G> glyphClass, Font<G> font, GlyphLocator<G> locator, GlyphList<G> gl, SaveManager sm) {
		this.panel = new GlyphEditPanel<G>(locator, gl);
		this.mb = new GlyphEditMenuBar<G>(this, sm, font, panel, glyphClass);
		setTitle(locator.toString());
		setJMenuBar(mb);
		setContentPane(panel);
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		GlyphEditFrame.addActions(this, panel, glyphClass);
		panel.getGlyphComponent().requestFocusInWindow();
	}
	
	public static <G extends FontGlyph> void addActions(final JFrame frame, final GlyphEditPanel<G> panel, final Class<G> glyphClass) {
		InputMap im = frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, 0), "previousGlyph");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, 0), "nextGlyph");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, KeyEvent.SHIFT_MASK), "previousDefinedGlyph");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, KeyEvent.SHIFT_MASK), "nextDefinedGlyph");
		
		ActionMap am = frame.getRootPane().getActionMap();
		am.put("previousGlyph", new AbstractAction() {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent e) {
				panel.setGlyph(panel.getGlyphLocator().getPrevious(), glyphClass);
				frame.setTitle(panel.getGlyphLocator().toString());
			}
		});
		am.put("nextGlyph", new AbstractAction() {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent e) {
				panel.setGlyph(panel.getGlyphLocator().getNext(), glyphClass);
				frame.setTitle(panel.getGlyphLocator().toString());
			}
		});
		am.put("previousDefinedGlyph", new AbstractAction() {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent e) {
				panel.setGlyph(panel.getGlyphLocator().getPreviousDefined(), null);
				frame.setTitle(panel.getGlyphLocator().toString());
			}
		});
		am.put("nextDefinedGlyph", new AbstractAction() {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent e) {
				panel.setGlyph(panel.getGlyphLocator().getNextDefined(), null);
				frame.setTitle(panel.getGlyphLocator().toString());
			}
		});
	}
}
