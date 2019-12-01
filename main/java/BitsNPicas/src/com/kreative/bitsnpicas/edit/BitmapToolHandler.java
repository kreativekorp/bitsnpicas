package com.kreative.bitsnpicas.edit;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;
import java.util.Stack;
import com.kreative.bitsnpicas.BitmapFontGlyph;
import com.kreative.bitsnpicas.Font;
import com.kreative.bitsnpicas.FontGlyph;

public class BitmapToolHandler implements GlyphComponentListener {
	private final BitmapToolPanel toolPanel;
	private final GlyphComponent glyphComponent;
	private final Stack<BitmapGlyphState> undoStack;
	private final Stack<BitmapGlyphState> redoStack;
	private BitmapTool inProgressTool;
	private byte inProgressOpacity;
	private BitmapGlyphState inProgressState;
	private int startX;
	private int startY;
	private int startGx;
	private int startGy;
	private double startTx;
	private double startTy;
	
	public BitmapToolHandler(BitmapToolPanel toolPanel, GlyphComponent glyphComponent) {
		this.toolPanel = toolPanel;
		this.glyphComponent = glyphComponent;
		this.undoStack = new Stack<BitmapGlyphState>();
		this.redoStack = new Stack<BitmapGlyphState>();
		glyphComponent.addGlyphComponentListener(this);
	}
	
	public boolean canUndo() {
		return (!undoStack.isEmpty());
	}
	
	public boolean canRedo() {
		return (!redoStack.isEmpty());
	}
	
	public void pushUndoState(BitmapGlyphState state) {
		if (state == null) {
			BitmapFontGlyph g = (BitmapFontGlyph)glyphComponent.getGlyph();
			state = new BitmapGlyphState(g);
		}
		undoStack.push(state);
		redoStack.clear();
	}
	
	public void undo() {
		if (!undoStack.isEmpty()) {
			BitmapFontGlyph g = (BitmapFontGlyph)glyphComponent.getGlyph();
			redoStack.push(new BitmapGlyphState(g));
			undoStack.pop().apply(g);
			glyphComponent.glyphChanged();
		}
	}
	
	public void redo() {
		if (!redoStack.isEmpty()) {
			BitmapFontGlyph g = (BitmapFontGlyph)glyphComponent.getGlyph();
			undoStack.push(new BitmapGlyphState(g));
			redoStack.pop().apply(g);
			glyphComponent.glyphChanged();
		}
	}
	
	public Cursor getCursor(MouseEvent e, Point2D p, FontGlyph glyph, Font<?> font) {
		return toolPanel.getSelectedTool().cursor;
	}
	
	public boolean mouseMoved(MouseEvent e, Point2D p, FontGlyph glyph, Font<?> font) {
		return false;
	}
	
	public boolean mousePressed(MouseEvent e, Point2D p, FontGlyph glyph, Font<?> font) {
		BitmapFontGlyph g = (BitmapFontGlyph)glyph;
		this.inProgressTool = toolPanel.getSelectedTool();
		this.inProgressOpacity = (byte)toolPanel.getOpacity();
		this.inProgressState = new BitmapGlyphState(g);
		this.startX = (int)Math.floor(p.getX());
		this.startY = (int)Math.floor(p.getY());
		return doTool(e, startX, startY, g, font, true, false);
	}
	
	public boolean mouseDragged(MouseEvent e, Point2D p, FontGlyph glyph, Font<?> font) {
		BitmapFontGlyph g = (BitmapFontGlyph)glyph;
		int x = (int)Math.floor(p.getX());
		int y = (int)Math.floor(p.getY());
		return doTool(e, x, y, g, font, false, false);
	}
	
	public boolean mouseReleased(MouseEvent e, Point2D p, FontGlyph glyph, Font<?> font) {
		BitmapFontGlyph g = (BitmapFontGlyph)glyph;
		int x = (int)Math.floor(p.getX());
		int y = (int)Math.floor(p.getY());
		return doTool(e, x, y, g, font, false, true);
	}
	
	private boolean doTool(
		MouseEvent e, int x, int y,
		BitmapFontGlyph glyph, Font<?> font,
		boolean pressed, boolean released
	) {
		int rx = Math.min(startX, x);
		int ry = Math.min(startY, y);
		int rw = Math.max(startX, x) - rx;
		int rh = Math.max(startY, y) - ry;
		switch (inProgressTool) {
			case PENCIL:
				if (pressed) {
					pushUndoState(inProgressState);
					byte existing = glyph.getPixel(x, y);
					if (inProgressOpacity == existing) inProgressOpacity = 0;
				}
				glyph.expand(rx, ry, rw + 1, rh + 1);
				glyph.drawLine(startX, startY, x, y, inProgressOpacity);
				startX = x;
				startY = y;
				if (released) glyph.contract();
				return true;
			case ERASER:
				if (pressed) pushUndoState(inProgressState);
				glyph.expand(rx, ry, rw + 1, rh + 1);
				glyph.drawLine(startX, startY, x, y, (byte)0);
				startX = x;
				startY = y;
				if (released) glyph.contract();
				return true;
			case EYEDROPPER:
				toolPanel.setOpacity(glyph.getPixel(x, y) & 0xFF);
				return false;
			case LINE:
				if (pressed) pushUndoState(inProgressState);
				inProgressState.apply(glyph);
				glyph.expand(rx, ry, rw + 1, rh + 1);
				glyph.drawLine(startX, startY, x, y, inProgressOpacity);
				if (released) glyph.contract();
				return true;
			case RECTANGLE:
				if (pressed) pushUndoState(inProgressState);
				inProgressState.apply(glyph);
				glyph.expand(rx, ry, rw + 1, rh + 1);
				glyph.drawRect(rx, ry, rw, rh, inProgressOpacity);
				if (released) glyph.contract();
				return true;
			case FILLED_RECT:
				if (pressed) pushUndoState(inProgressState);
				inProgressState.apply(glyph);
				glyph.expand(rx, ry, rw + 1, rh + 1);
				glyph.fillRect(rx, ry, rw + 1, rh + 1, inProgressOpacity);
				if (released) glyph.contract();
				return true;
			case INVERT:
				if (pressed) pushUndoState(inProgressState);
				inProgressState.apply(glyph);
				glyph.expand(rx, ry, rw + 1, rh + 1);
				glyph.invertRect(rx, ry, rw + 1, rh + 1);
				if (released) glyph.contract();
				return true;
			case MOVE:
				if (pressed) {
					pushUndoState(inProgressState);
					startGx = glyph.getX();
					startGy = glyph.getY();
				} else {
					glyph.setXY(
						startGx + (x - startX),
						startGy - (y - startY)
					);
				}
				return true;
			case GRABBER:
				if (pressed) {
					glyphComponent.setCursor(SwingUtils.CURSOR_HAND_CLOSED);
					startX = e.getX();
					startY = e.getY();
					startTx = glyphComponent.getTranslateX();
					startTy = glyphComponent.getTranslateY();
				} else {
					glyphComponent.setTranslate(
						Math.round(startTx + e.getX() - startX),
						Math.round(startTy + e.getY() - startY)
					);
				}
				if (released) {
					glyphComponent.setCursor(SwingUtils.CURSOR_HAND_OPEN);
				}
				return false;
		}
		return false;
	}
	
	public boolean mouseWheelMoved(MouseWheelEvent e, Point2D p, FontGlyph glyph, Font<?> font) {
		int opacity = toolPanel.getOpacity() - e.getWheelRotation();
		if (opacity < 0) opacity = 0;
		if (opacity > 255) opacity = 255;
		toolPanel.setOpacity(opacity);
		return false;
	}
	
	public void metricsChanged(FontGlyph glyph, Font<?> font) {}
	public void glyphChanged(FontGlyph glyph, Font<?> font) {}
}
