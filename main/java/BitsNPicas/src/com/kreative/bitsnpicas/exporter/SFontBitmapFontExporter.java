package com.kreative.bitsnpicas.exporter;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import javax.imageio.ImageIO;
import com.kreative.bitsnpicas.BitmapFont;
import com.kreative.bitsnpicas.BitmapFontExporter;
import com.kreative.bitsnpicas.BitmapFontGlyph;

public class SFontBitmapFontExporter implements BitmapFontExporter {
	private int color;
	
	public SFontBitmapFontExporter() {
		this.color = 0;
	}
	
	public SFontBitmapFontExporter(int color) {
		this.color = color;
	}
	
	public byte[] exportFontToBytes(BitmapFont font) throws IOException {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		RenderedImage r = exportFontToImage(font);
		ImageIO.write(r, "png", b);
		return b.toByteArray();
	}

	public void exportFontToStream(BitmapFont font, OutputStream os) throws IOException {
		RenderedImage r = exportFontToImage(font);
		ImageIO.write(r, "png", os);
	}

	public void exportFontToFile(BitmapFont font, File file) throws IOException {
		RenderedImage r = exportFontToImage(font);
		ImageIO.write(r, "png", file);
	}
	
	public RenderedImage exportFontToImage(BitmapFont font) {
		BitmapFontGlyph[] g = new BitmapFontGlyph[94];
		int[] l = new int[94];
		int[] w = new int[94];
		int[] r = new int[94];
		for (int i = 0; i < 94; i++) {
			g[i] = font.getCharacter(i+33);
			if (g[i] == null) g[i] = new BitmapFontGlyph(new byte[1][1], 0, 1, 1);
			l[i] = g[i].getGlyphOffset();
			l[i] = (l[i] < 0) ? (-l[i]) : 1;
			w[i] = g[i].getCharacterWidth();
			if (w[i] < 1) w[i] = 1;
			r[i] = g[i].getGlyphOffset()+g[i].getGlyphWidth();
			r[i] = (r[i] > w[i]) ? (r[i] - w[i]) : 1;
		}
		for (int i = 0; i+1 < 94; i++) {
			r[i] = l[i+1] = Math.max(r[i], l[i+1]);
		}
		int width = 0;
		for (int i = 0; i < 94; i++) {
			width += l[i] + w[i] + r[i];
		}
		int height = 1+font.getLineAscent()+font.getLineDescent()+font.getLineGap();
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		for (int x = 0, i = 0; i < 94; x += l[i]+w[i]+r[i], i++) {
			for (int lx = 0; lx < l[i]; lx++) {
				bi.setRGB(x+lx, 0, 0xFFFF00FF);
			}
			for (int rx = 0; rx < r[i]; rx++) {
				bi.setRGB(x+l[i]+w[i]+rx, 0, 0xFFFF00FF);
			}
			byte[][] gg = g[i].getGlyph();
			for (int gy = 0, iy = 1+font.getLineAscent()-g[i].getGlyphAscent(); gy < gg.length; gy++, iy++) {
				if (iy > 0 && iy < height) {
					for (int gx = 0, ix = x+l[i]+g[i].getGlyphOffset(); gx < gg[gy].length; gx++, ix++) {
						bi.setRGB(ix, iy, ((gg[gy][gx] & 0xFF) << 24) | (color & 0xFFFFFF));
					}
				}
			}
		}
		return bi;
	}
}
