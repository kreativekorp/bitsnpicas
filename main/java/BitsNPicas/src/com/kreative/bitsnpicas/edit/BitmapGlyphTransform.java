package com.kreative.bitsnpicas.edit;

import java.awt.event.KeyEvent;
import javax.swing.KeyStroke;
import com.kreative.bitsnpicas.BitmapFontGlyph;
import com.kreative.bitsnpicas.Font;
import com.kreative.bitsnpicas.transformer.BoldBitmapFontGlyphTransformer;

public interface BitmapGlyphTransform {
	public static final class BitmapGlyphTransformInfo {
		public final BitmapGlyphTransform transform;
		public final String name;
		public final KeyStroke keystroke;
		public BitmapGlyphTransformInfo(BitmapGlyphTransform t, String n, KeyStroke k) {
			this.transform = t;
			this.name = n;
			this.keystroke = k;
		}
	}
	
	public static final BitmapGlyphTransformInfo[] TRANSFORMS = new BitmapGlyphTransformInfo[]{
		new BitmapGlyphTransformInfo(new Bold(), "Bold", KeyStroke.getKeyStroke(KeyEvent.VK_B, CommonMenuItems.SHORTCUT_KEY | KeyEvent.SHIFT_MASK)),
		new BitmapGlyphTransformInfo(new Invert(), "Invert", KeyStroke.getKeyStroke(KeyEvent.VK_N, CommonMenuItems.SHORTCUT_KEY | KeyEvent.SHIFT_MASK)),
		new BitmapGlyphTransformInfo(new FlipHorizontal(), "Flip Horizontal", KeyStroke.getKeyStroke(KeyEvent.VK_H, CommonMenuItems.SHORTCUT_KEY | KeyEvent.SHIFT_MASK)),
		new BitmapGlyphTransformInfo(new FlipVertical(), "Flip Vertical", KeyStroke.getKeyStroke(KeyEvent.VK_V, CommonMenuItems.SHORTCUT_KEY | KeyEvent.SHIFT_MASK)),
		null,
		new BitmapGlyphTransformInfo(new Nudge(-1, 0), "Nudge Left", KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, CommonMenuItems.SHORTCUT_KEY | KeyEvent.SHIFT_MASK)),
		new BitmapGlyphTransformInfo(new Nudge(+1, 0), "Nudge Right", KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, CommonMenuItems.SHORTCUT_KEY | KeyEvent.SHIFT_MASK)),
		new BitmapGlyphTransformInfo(new Nudge(0, -1), "Nudge Up", KeyStroke.getKeyStroke(KeyEvent.VK_UP, CommonMenuItems.SHORTCUT_KEY | KeyEvent.SHIFT_MASK)),
		new BitmapGlyphTransformInfo(new Nudge(0, +1), "Nudge Down", KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, CommonMenuItems.SHORTCUT_KEY | KeyEvent.SHIFT_MASK)),
	};
	
	public abstract void transform(Font<BitmapFontGlyph> font, BitmapFontGlyph glyph);
	
	public static final class Bold implements BitmapGlyphTransform {
		private static final BoldBitmapFontGlyphTransformer tx = new BoldBitmapFontGlyphTransformer();
		public void transform(Font<BitmapFontGlyph> font, BitmapFontGlyph glyph) {
			new BitmapGlyphState(tx.transformGlyph(glyph)).apply(glyph);
		}
	}
	
	public static final class Invert implements BitmapGlyphTransform {
		public void transform(Font<BitmapFontGlyph> font, BitmapFontGlyph glyph) {
			glyph.expand(
				0, -font.getLineAscent(), glyph.getCharacterWidth(),
				font.getLineAscent() + font.getLineDescent()
			);
			glyph.invertRect(
				0, -font.getLineAscent(), glyph.getCharacterWidth(),
				font.getLineAscent() + font.getLineDescent()
			);
			glyph.contract();
		}
	}
	
	public static final class FlipHorizontal implements BitmapGlyphTransform {
		public void transform(Font<BitmapFontGlyph> font, BitmapFontGlyph glyph) {
			for (byte[] row : glyph.getGlyph()) transform(row);
			int x = glyph.getCharacterWidth() - glyph.getGlyphWidth() - glyph.getGlyphOffset();
			glyph.setXY(x, glyph.getGlyphAscent());
		}
		private void transform(byte[] row) {
			for (int i = 0, j = row.length - 1; i < j; i++, j--) {
				byte tmp = row[i];
				row[i] = row[j];
				row[j] = tmp;
			}
		}
	}
	
	public static final class FlipVertical implements BitmapGlyphTransform {
		public void transform(Font<BitmapFontGlyph> font, BitmapFontGlyph glyph) {
			transform(glyph.getGlyph());
			int y = font.getLineAscent() - font.getLineDescent();
			y += glyph.getGlyphHeight() - glyph.getGlyphAscent();
			glyph.setXY(glyph.getGlyphOffset(), y);
		}
		private void transform(byte[][] rows) {
			for (int i = 0, j = rows.length - 1; i < j; i++, j--) {
				byte[] tmp = rows[i];
				rows[i] = rows[j];
				rows[j] = tmp;
			}
		}
	}
	
	public static final class Nudge implements BitmapGlyphTransform {
		private final int dx, dy;
		public Nudge(int dx, int dy) {
			this.dx = dx;
			this.dy = dy;
		}
		public void transform(Font<BitmapFontGlyph> font, BitmapFontGlyph glyph) {
			int x = glyph.getX() + dx;
			int y = glyph.getY() - dy;
			glyph.setXY(x, y);
		}
	}
}
