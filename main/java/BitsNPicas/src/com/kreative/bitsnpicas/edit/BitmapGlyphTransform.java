package com.kreative.bitsnpicas.edit;

import java.awt.event.KeyEvent;
import javax.swing.KeyStroke;
import com.kreative.bitsnpicas.BitmapFontGlyph;
import com.kreative.bitsnpicas.Font;
import com.kreative.bitsnpicas.transformer.BoldBitmapFontGlyphTransformer;

public abstract class BitmapGlyphTransform {
	private static final int CTRL_SHIFT = CommonMenuItems.SHORTCUT_KEY | KeyEvent.SHIFT_MASK;
	public static final BitmapGlyphTransform[] TRANSFORMS = new BitmapGlyphTransform[] {
		new Bold          ("Bold",            CTRL_SHIFT, KeyEvent.VK_B),
		new Invert        ("Invert",          CTRL_SHIFT, KeyEvent.VK_N),
		new FlipHorizontal("Flip Horizontal", CTRL_SHIFT, KeyEvent.VK_H),
		new FlipVertical  ("Flip Vertical",   CTRL_SHIFT, KeyEvent.VK_J),
		null,
		new Nudge(-1, 0, "Nudge Left",  CTRL_SHIFT, KeyEvent.VK_LEFT ),
		new Nudge(+1, 0, "Nudge Right", CTRL_SHIFT, KeyEvent.VK_RIGHT),
		new Nudge(0, -1, "Nudge Up",    CTRL_SHIFT, KeyEvent.VK_UP   ),
		new Nudge(0, +1, "Nudge Down",  CTRL_SHIFT, KeyEvent.VK_DOWN ),
	};
	
	public final String name;
	public final KeyStroke keystroke;
	
	public BitmapGlyphTransform(String name, int modifiers, int keyCode) {
		this.name = name;
		this.keystroke = KeyStroke.getKeyStroke(keyCode, modifiers);
	}
	
	public abstract void transform(Font<BitmapFontGlyph> font, BitmapFontGlyph glyph);
	
	public static final class Bold extends BitmapGlyphTransform {
		private final BoldBitmapFontGlyphTransformer tx;
		public Bold(String name, int keyCode, int modifiers) {
			super(name, keyCode, modifiers);
			this.tx = new BoldBitmapFontGlyphTransformer();
		}
		public void transform(Font<BitmapFontGlyph> font, BitmapFontGlyph glyph) {
			new BitmapGlyphState(tx.transformGlyph(glyph)).apply(glyph);
		}
	}
	
	public static final class Invert extends BitmapGlyphTransform {
		public Invert(String name, int keyCode, int modifiers) {
			super(name, keyCode, modifiers);
		}
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
	
	public static final class FlipHorizontal extends BitmapGlyphTransform {
		public FlipHorizontal(String name, int keyCode, int modifiers) {
			super(name, keyCode, modifiers);
		}
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
	
	public static final class FlipVertical extends BitmapGlyphTransform {
		public FlipVertical(String name, int keyCode, int modifiers) {
			super(name, keyCode, modifiers);
		}
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
	
	public static final class Nudge extends BitmapGlyphTransform {
		private final int dx, dy;
		public Nudge(int dx, int dy, String name, int keyCode, int modifiers) {
			super(name, keyCode, modifiers);
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
