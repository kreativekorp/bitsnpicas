package com.kreative.bitsnpicas.importer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.imageio.ImageIO;
import com.kreative.bitsnpicas.BitmapFont;
import com.kreative.bitsnpicas.BitmapFontGlyph;
import com.kreative.bitsnpicas.BitmapFontImporter;
import com.kreative.bitsnpicas.WindingOrder;

public class ImageBitmapFontImporter implements BitmapFontImporter {
	public int matte = -1;
	public int startX = 0, startY = 0;
	public int cellWidth = 8, cellHeight = 8, ascent = 7;
	public int deltaX = 0, deltaY = 0;
	public int columnCount = 0, rowCount = 0;
	public WindingOrder order = WindingOrder.LTR_TTB;
	public boolean invert = false;
	public int threshold = 128;
	public List<Integer> encoding = null;
	
	public static final class PreviewResult {
		public final BufferedImage preview;
		public final Point[] points;
		private PreviewResult(BufferedImage preview, int count) {
			this.preview = preview;
			this.points = new Point[count];
		}
	}
	
	public PreviewResult preview(BufferedImage in) {
		int w = in.getWidth(), h = in.getHeight();
		int rows = 0; for (int y = startY; (y + cellHeight <= h) && (rowCount <= 0 || rows < rowCount); y += cellHeight + deltaY, rows++);
		int columns = 0; for (int x = startX; (x + cellWidth <= w) && (columnCount <= 0 || columns < columnCount); x += cellWidth + deltaX, columns++);
		BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D gr = out.createGraphics();
		gr.setColor(new Color(matte));
		gr.fillRect(0, 0, w, h);
		gr.drawImage(in, null, 0, 0);
		gr.setColor(new Color(0x80808080, true));
		gr.fillRect(0, 0, w, h);
		gr.dispose();
		PreviewResult result = new PreviewResult(out, rows * columns);
		int[] rgb = new int[cellHeight * cellWidth];
		for (int y = startY, j = 0; j < rows; y += cellHeight + deltaY, j++) {
			for (int x = startX, i = 0; i < columns; x += cellWidth + deltaX, i++) {
				in.getRGB(x, y, cellWidth, cellHeight, rgb, 0, cellWidth);
				for (int z = 0, gy = 0; gy < cellHeight; gy++) {
					for (int gx = 0; gx < cellWidth; gx++, z++) {
						int a = (rgb[z] >> 24) & 0xFF;
						int r = (((rgb[z] >> 16) & 0xFF) * a / 255);
						int g = (((rgb[z] >>  8) & 0xFF) * a / 255);
						int b = (((rgb[z] >>  0) & 0xFF) * a / 255);
						r += (((matte >> 16) & 0xFF) * (255 - a) / 255);
						g += (((matte >>  8) & 0xFF) * (255 - a) / 255);
						b += (((matte >>  0) & 0xFF) * (255 - a) / 255);
						int k = (30 * r + 59 * g + 11 * b) / 100;
						if (invert) k = 255 - k;
						if (threshold > 0) k = (k < threshold) ? 0 : 255;
						rgb[z] = (0xFF << 24) | (k << 16) | (k << 8) | (k << 0);
						if (deltaX <= 0 && (gx == 0 || gx == cellWidth - 1)) rgb[z] ^= 0xFF0000;
						else if (deltaY <= 0 && (gy == 0 || gy == cellHeight - 1)) rgb[z] ^= 0xFF0000;
						else if (gy == ascent) rgb[z] ^= 0x00FFFF;
					}
				}
				int ci = order.getIndex(rows, columns, j, i);
				out.setRGB(x, y, cellWidth, cellHeight, rgb, 0, cellWidth);
				result.points[ci] = new Point(x, y);
			}
		}
		return result;
	}
	
	public BitmapFont importFont(BufferedImage in) {
		BitmapFont bm = new BitmapFont(ascent, cellHeight - ascent, ascent, cellHeight - ascent, 0, 0, 0);
		int w = in.getWidth(), h = in.getHeight();
		int rows = 0; for (int y = startY; (y + cellHeight <= h) && (rowCount <= 0 || rows < rowCount); y += cellHeight + deltaY, rows++);
		int columns = 0; for (int x = startX; (x + cellWidth <= w) && (columnCount <= 0 || columns < columnCount); x += cellWidth + deltaX, columns++);
		int[] rgb = new int[cellHeight * cellWidth];
		for (int y = startY, j = 0; j < rows; y += cellHeight + deltaY, j++) {
			for (int x = startX, i = 0; i < columns; x += cellWidth + deltaX, i++) {
				byte[][] gd = new byte[cellHeight][cellWidth];
				in.getRGB(x, y, cellWidth, cellHeight, rgb, 0, cellWidth);
				for (int z = 0, gy = 0; gy < cellHeight; gy++) {
					for (int gx = 0; gx < cellWidth; gx++, z++) {
						int a = (rgb[z] >> 24) & 0xFF;
						int r = (((rgb[z] >> 16) & 0xFF) * a / 255);
						int g = (((rgb[z] >>  8) & 0xFF) * a / 255);
						int b = (((rgb[z] >>  0) & 0xFF) * a / 255);
						r += (((matte >> 16) & 0xFF) * (255 - a) / 255);
						g += (((matte >>  8) & 0xFF) * (255 - a) / 255);
						b += (((matte >>  0) & 0xFF) * (255 - a) / 255);
						int k = (30 * r + 59 * g + 11 * b) / 100;
						if (invert) k = 255 - k;
						if (threshold > 0) k = (k < threshold) ? 0 : 255;
						gd[gy][gx] = (byte)(255 - k);
					}
				}
				int ci = order.getIndex(rows, columns, j, i);
				BitmapFontGlyph glyph = new BitmapFontGlyph(gd, 0, cellWidth, ascent);
				if (encoding == null || encoding.isEmpty()) {
					bm.putCharacter(0xF0000 + ci, glyph);
				} else if (ci < encoding.size()) {
					Integer e = encoding.get(ci);
					if (e != null && e.intValue() >= 0) {
						bm.putCharacter(e.intValue(), glyph);
					}
				} else {
					bm.putCharacter(0xF0000 + ci - encoding.size(), glyph);
				}
			}
		}
		bm.setXHeight();
		bm.setCapHeight();
		return bm;
	}
	
	public BitmapFont[] importFont(byte[] data) throws IOException {
		ByteArrayInputStream b = new ByteArrayInputStream(data);
		BitmapFont[] f = importFont(b);
		b.close();
		return f;
	}
	
	public BitmapFont[] importFont(InputStream is) throws IOException {
		BufferedImage bi = ImageIO.read(is);
		BitmapFont f = importFont(bi);
		return new BitmapFont[]{f};
	}
	
	public BitmapFont[] importFont(File file) throws IOException {
		FileInputStream in = new FileInputStream(file);
		BitmapFont[] f = importFont(in);
		in.close();
		return f;
	}
}
