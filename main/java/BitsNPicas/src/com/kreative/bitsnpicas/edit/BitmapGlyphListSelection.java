package com.kreative.bitsnpicas.edit;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import com.kreative.bitsnpicas.BitmapFontGlyph;
import com.kreative.bitsnpicas.Font;

public class BitmapGlyphListSelection implements ClipboardOwner, Transferable {
	public static final DataFlavor flavor = new DataFlavor(BitmapGlyphState[].class, "Bitmap Glyph List");
	
	private final BitmapGlyphState[] states;
	private final BufferedImage image;
	private final String string;
	
	public BitmapGlyphListSelection(Collection<GlyphLocator<BitmapFontGlyph>> locators) {
		StringBuffer textString = new StringBuffer();
		List<BitmapFontGlyph> glyphs = new ArrayList<BitmapFontGlyph>();
		List<BitmapGlyphState> states = new ArrayList<BitmapGlyphState>();
		int totalGlyphWidth = 0, ascent = 0, descent = 0;
		for (GlyphLocator<BitmapFontGlyph> loc : locators) {
			if (loc.isCodePoint()) textString.append(Character.toChars(loc.getCodePoint()));
			else if (loc.isGlyphName()) textString.append(loc.getGlyphName());
			BitmapFontGlyph glyph = loc.getGlyph();
			if (glyph != null) {
				glyphs.add(glyph);
				states.add(new BitmapGlyphState(glyph));
				totalGlyphWidth += glyph.getCharacterWidth();
			}
			Font<BitmapFontGlyph> font = loc.getGlyphFont();
			if (font != null) {
				if (font.getLineAscent() > ascent) ascent = font.getLineAscent();
				if (font.getLineDescent() > descent) descent = font.getLineDescent();
			}
		}
		this.states = states.toArray(new BitmapGlyphState[states.size()]);
		int w = totalGlyphWidth; if (w < 1) w = 1;
		int h = ascent + descent; if (h < 1) h = 1;
		this.image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		g.setColor(Color.black);
		double x = 0;
		for (BitmapFontGlyph glyph : glyphs) x += glyph.paint(g, x, ascent, 1);
		g.dispose();
		this.string = textString.toString();
	}
	
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		if (BitmapGlyphListSelection.flavor.equals(flavor)) return states;
		else if (DataFlavor.imageFlavor.equals(flavor)) return image;
		else if (DataFlavor.stringFlavor.equals(flavor)) return string;
		else throw new UnsupportedFlavorException(flavor);
	}
	
	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[]{
			BitmapGlyphListSelection.flavor,
			DataFlavor.imageFlavor,
			DataFlavor.stringFlavor
		};
	}
	
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return (
			BitmapGlyphListSelection.flavor.equals(flavor) ||
			DataFlavor.imageFlavor.equals(flavor) ||
			DataFlavor.stringFlavor.equals(flavor)
		);
	}
	
	public void lostOwnership(Clipboard clipboard, Transferable contents) {}
}
