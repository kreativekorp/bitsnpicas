package com.kreative.bitsnpicas.edit;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import com.kreative.bitsnpicas.BitmapFont;
import com.kreative.bitsnpicas.BitmapFontGlyph;

public class BitmapEditPanel extends GlyphEditPanel<BitmapFontGlyph> {
	private static final long serialVersionUID = 1L;
	
	public final BitmapToolPanel toolPanel;
	public final BitmapToolHandler toolHandler;
	
	public BitmapEditPanel(BitmapFont font, BitmapFontGlyph glyph, GlyphList gl) {
		super(font, glyph, gl);
		this.toolPanel = new BitmapToolPanel();
		this.toolHandler = new BitmapToolHandler(toolPanel, glyphComponent);
		add(toolPanel, BorderLayout.LINE_START);
		glyphComponent.setFocusable(true);
		glyphComponent.addMouseListener(new MyMouseListener());
		glyphComponent.addKeyListener(new MyKeyListener() {});
	}
	
	private class MyMouseListener extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			glyphComponent.requestFocusInWindow();
		}
	}
	
	private class MyKeyListener extends KeyAdapter {
		private boolean first = true;
		public void keyPressed(KeyEvent e) {
			if (!(e.isControlDown() || e.isMetaDown())) {
				switch (e.getKeyCode()) {
					case KeyEvent.VK_UP: translateGlyph(first, 0, -1); break;
					case KeyEvent.VK_DOWN: translateGlyph(first, 0, +1); break;
					case KeyEvent.VK_LEFT: translateGlyph(first, -1, 0); break;
					case KeyEvent.VK_RIGHT: translateGlyph(first, +1, 0); break;
					case KeyEvent.VK_OPEN_BRACKET: translateGlyphWidth(-1); break;
					case KeyEvent.VK_CLOSE_BRACKET: translateGlyphWidth(+1); break;
					case KeyEvent.VK_B: toolPanel.setSelectedTool(BitmapTool.PENCIL); break;
					case KeyEvent.VK_P: toolPanel.setSelectedTool(BitmapTool.PENCIL); break;
					case KeyEvent.VK_E: toolPanel.setSelectedTool(BitmapTool.ERASER); break;
					case KeyEvent.VK_I: toolPanel.setSelectedTool(BitmapTool.EYEDROPPER); break;
					case KeyEvent.VK_L: toolPanel.setSelectedTool(BitmapTool.LINE); break;
					case KeyEvent.VK_R: toolPanel.setSelectedTool(BitmapTool.RECTANGLE); break;
					case KeyEvent.VK_F: toolPanel.setSelectedTool(BitmapTool.FILLED_RECT); break;
					case KeyEvent.VK_N: toolPanel.setSelectedTool(BitmapTool.INVERT); break;
					case KeyEvent.VK_M: toolPanel.setSelectedTool(BitmapTool.MOVE); break;
					case KeyEvent.VK_V: toolPanel.setSelectedTool(BitmapTool.MOVE); break;
					case KeyEvent.VK_H: toolPanel.setSelectedTool(BitmapTool.GRABBER); break;
					case KeyEvent.VK_SPACE: toolPanel.setSelectedTool(BitmapTool.GRABBER); break;
					case KeyEvent.VK_D: toolPanel.setOpacity(255); break;
					case KeyEvent.VK_X: toolPanel.setOpacity(255 - toolPanel.getOpacity()); break;
					case KeyEvent.VK_BACK_QUOTE: toolPanel.setOpacity(0); break;
					case KeyEvent.VK_1: toolPanel.setOpacity(255 * 1 / 10); break;
					case KeyEvent.VK_2: toolPanel.setOpacity(255 * 2 / 10); break;
					case KeyEvent.VK_3: toolPanel.setOpacity(255 * 3 / 10); break;
					case KeyEvent.VK_4: toolPanel.setOpacity(255 * 4 / 10); break;
					case KeyEvent.VK_5: toolPanel.setOpacity(255 * 5 / 10); break;
					case KeyEvent.VK_6: toolPanel.setOpacity(255 * 6 / 10); break;
					case KeyEvent.VK_7: toolPanel.setOpacity(255 * 7 / 10); break;
					case KeyEvent.VK_8: toolPanel.setOpacity(255 * 8 / 10); break;
					case KeyEvent.VK_9: toolPanel.setOpacity(255 * 9 / 10); break;
					case KeyEvent.VK_0: toolPanel.setOpacity(255); break;
					case KeyEvent.VK_MINUS: translateOpacity(-1); break;
					case KeyEvent.VK_EQUALS: translateOpacity(+1); break;
				}
			}
			first = false;
		}
		public void keyReleased(KeyEvent e) {
			first = true;
		}
	}
	
	private void translateGlyph(boolean first, int tx, int ty) {
		if (first) toolHandler.pushUndoState(null);
		BitmapFontGlyph glyph = (BitmapFontGlyph)glyphComponent.getGlyph();
		glyph.setXY(glyph.getX() + tx, glyph.getY() - ty);
		glyphComponent.glyphChanged();
	}
	
	private void translateGlyphWidth(int t) {
		BitmapFontGlyph glyph = (BitmapFontGlyph)glyphComponent.getGlyph();
		t += glyph.getCharacterWidth();
		if (t < 0) t = 0;
		glyph.setCharacterWidth(t);
		glyphComponent.metricsChanged();
	}
	
	private void translateOpacity(int t) {
		t += toolPanel.getOpacity();
		if (t < 0) t = 0;
		if (t > 255) t = 255;
		toolPanel.setOpacity(t);
	}
}
