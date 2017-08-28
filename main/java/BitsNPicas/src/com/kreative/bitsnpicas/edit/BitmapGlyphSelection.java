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
import com.kreative.bitsnpicas.BitmapFont;
import com.kreative.bitsnpicas.BitmapFontGlyph;

public class BitmapGlyphSelection implements ClipboardOwner, Transferable {
	public static final DataFlavor flavor = new DataFlavor(BitmapGlyphState.class, "Bitmap Glyph");
	
	private final BitmapGlyphState state;
	private final BufferedImage image;
	private final String string;
	
	public BitmapGlyphSelection(BitmapFont font, BitmapFontGlyph glyph, int codePoint) {
		this.state = new BitmapGlyphState(glyph);
		int w = glyph.getCharacterWidth(); if (w < 1) w = 1;
		int h = font.getEmAscent() + font.getEmDescent(); if (h < 1) h = 1;
		this.image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		g.setColor(Color.black);
		glyph.paint(g, 0, font.getEmAscent(), 1);
		g.dispose();
		this.string = String.valueOf(Character.toChars(codePoint));
	}
	
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		if (BitmapGlyphSelection.flavor.equals(flavor)) return state;
		else if (DataFlavor.imageFlavor.equals(flavor)) return image;
		else if (DataFlavor.stringFlavor.equals(flavor)) return string;
		else throw new UnsupportedFlavorException(flavor);
	}
	
	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[]{
			BitmapGlyphSelection.flavor,
			DataFlavor.imageFlavor,
			DataFlavor.stringFlavor
		};
	}
	
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return (
			BitmapGlyphSelection.flavor.equals(flavor) ||
			DataFlavor.imageFlavor.equals(flavor) ||
			DataFlavor.stringFlavor.equals(flavor)
		);
	}
	
	public void lostOwnership(Clipboard clipboard, Transferable contents) {}
}
