package com.kreative.bitsnpicas.exporter;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import com.kreative.bitsnpicas.BitmapFont;
import com.kreative.bitsnpicas.BitmapFontExporter;
import com.kreative.bitsnpicas.BitmapFontGlyph;
import com.kreative.bitsnpicas.Font;
import com.sun.imageio.plugins.png.PNGMetadata;

public class RFontBitmapFontExporter implements BitmapFontExporter {
	private int color;
	
	public RFontBitmapFontExporter() {
		this.color = 0;
	}
	
	public RFontBitmapFontExporter(int color) {
		this.color = color;
	}
	
	public byte[] exportFontToBytes(BitmapFont font) throws IOException {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		exportFontToStream(font, b);
		b.close();
		return b.toByteArray();
	}

	@SuppressWarnings("unchecked")
	public void exportFontToStream(BitmapFont font, OutputStream os) throws IOException {
		RFontBean r = exportFontToImage(font);
		//ImageIO.write(r.img, "png", os);
		Iterator<ImageWriter> iterator = ImageIO.getImageWritersBySuffix("png");
		ImageWriter imagewriter = iterator.next();
		imagewriter.setOutput(ImageIO.createImageOutputStream(os));
		PNGMetadata metadata = new PNGMetadata();
		if (r.fname != null) {
			metadata.tEXt_keyword.add("FontFamily");
			metadata.tEXt_text.add(r.fname);
		}
		if (r.sname != null) {
			metadata.tEXt_keyword.add("FontStyle");
			metadata.tEXt_text.add(r.sname);
		}
		if (r.copy != null) {
			metadata.tEXt_keyword.add("Copyright");
			metadata.tEXt_text.add(r.copy);
		}
		metadata.unknownChunkType.add("rfVM");
		metadata.unknownChunkData.add(r.vmtx);
		metadata.unknownChunkType.add("rfHM");
		metadata.unknownChunkData.add(r.hmtx);
		IIOImage iioImage = new IIOImage(r.img, null, null);
		iioImage.setMetadata(metadata);
		imagewriter.write(null, iioImage, null);
	}

	public void exportFontToFile(BitmapFont font, File file) throws IOException {
		FileOutputStream out = new FileOutputStream(file);
		exportFontToStream(font, out);
		out.close();
	}
	
	public class RFontBean {
		public String fname;
		public String sname;
		public String copy;
		public RenderedImage img;
		public byte[] vmtx;
		public byte[] hmtx;
	}
	
	public RFontBean exportFontToImage(BitmapFont font) {
		// calculate font metrics
		BitmapFontGlyph[][] g = new BitmapFontGlyph[4352][];
		int[][] l = new int[4352][];
		int[][] w = new int[4352][];
		int[][] r = new int[4352][];
		int maxascent = font.getLineAscent();
		int maxdescent = font.getLineDescent();
		int linegap = font.getLineGap();
		int[] bw = new int[4352];
		for (int b = 0; b < 4352; b++) {
			for (int c = 0; c < 256; c++) {
				if (font.containsCharacter((b << 8) | c)) {
					if (g[b] == null) g[b] = new BitmapFontGlyph[256];
					g[b][c] = font.getCharacter((b << 8) | c);
					if (l[b] == null) l[b] = new int[256];
					l[b][c] = g[b][c].getGlyphOffset();
					l[b][c] = (l[b][c] < 0) ? (-l[b][c]) : 0;
					if (w[b] == null) w[b] = new int[256];
					w[b][c] = g[b][c].getCharacterWidth();
					if (r[b] == null) r[b] = new int[256];
					r[b][c] = g[b][c].getGlyphOffset() + g[b][c].getGlyphWidth();
					r[b][c] = (r[b][c] > w[b][c]) ? (r[b][c] - w[b][c]) : 0;
					if (g[b][c].getGlyphAscent() > maxascent) maxascent = g[b][c].getGlyphAscent();
					if (g[b][c].getGlyphDescent() > maxdescent) maxdescent = g[b][c].getGlyphDescent();
					bw[b] += 1 + l[b][c] + w[b][c] + r[b][c];
				}
			}
		}
		int bh = 1+maxascent+maxdescent+linegap;
		// calculate image metrics
		int width = 0;
		int height = 0;
		for (int b = 0; b < 4352; b++) {
			if (bw[b] > width) width = bw[b];
			if (bw[b] > 0) height += bh;
		}
		width += 2;
		// CONSTRUCT IMAGE
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		int y = 0;
		// for each row
		for (int b = 0; b < 4352; b++) {
			if (bw[b] > 0) {
				int x = 0;
				// control column - vertical metrics
				setVM(bi, x++, y, font, maxascent, maxdescent);
				// for each character
				for (int c = 0; c < 256; c++) {
					if (g[b][c] != null) {
						// control column - code point
						setCC(bi, x, y, bh, (b << 8) | c);
						// horizontal metrics - left overflow in red
						for (int lx = 0; lx < l[b][c]; lx++) {
							bi.setRGB(x+1+lx, y, 0xFFFF0000);
						}
						// horizontal metrics - right overflow in blue
						for (int rx = 0; rx < r[b][c]; rx++) {
							bi.setRGB(x+1+l[b][c]+w[b][c]+rx, y, 0xFF0000FF);
						}
						// glyph
						byte[][] gg = g[b][c].getGlyph();
						for (int gy = 0, iy = y+1+maxascent-g[b][c].getGlyphAscent(); gy < gg.length && iy < height; gy++, iy++) {
							for (int gx = 0, ix = x+1+l[b][c]+g[b][c].getGlyphOffset(); gx < gg[gy].length && ix < width; gx++, ix++) {
								bi.setRGB(ix, iy, ((gg[gy][gx] & 0xFF) << 24) | (color & 0xFFFFFF));
							}
						}
						// next character
						x += 1 + l[b][c] + w[b][c] + r[b][c];
					}
				}
				// control column - end marker
				setCC(bi, x, y, bh, -1);
				// next row
				y += bh;
			}
		}
		// vertical metrics table
		ByteArrayOutputStream vmb = new ByteArrayOutputStream();
		DataOutputStream vmd = new DataOutputStream(vmb);
		try {
			if (bh < 256) {
				vmd.writeShort(1);
				vmd.writeByte(maxascent);
				vmd.writeByte(font.getLineAscent());
				vmd.writeByte(font.getEmAscent());
				vmd.writeByte(font.getXHeight());
				vmd.writeByte(font.getEmDescent());
				vmd.writeByte(font.getLineDescent());
				vmd.writeByte(maxdescent);
				vmd.writeByte(linegap);
				vmd.writeByte(maxascent-1); // y offset
				vmd.writeByte(bh-1); // line advance, bitmap height
			} else {
				vmd.writeShort(2);
				vmd.writeShort(maxascent);
				vmd.writeShort(font.getLineAscent());
				vmd.writeShort(font.getEmAscent());
				vmd.writeShort(font.getXHeight());
				vmd.writeShort(font.getEmDescent());
				vmd.writeShort(font.getLineDescent());
				vmd.writeShort(maxdescent);
				vmd.writeShort(linegap);
				vmd.writeShort(maxascent-1); // y offset
				vmd.writeShort(bh-1); // line advance, bitmap height
			}
			vmd.close();
			vmb.close();
		} catch (IOException ignored) {
			// nothing
		}
		// horizontal metrics table
		ByteArrayOutputStream hmb = new ByteArrayOutputStream();
		DataOutputStream hmd = new DataOutputStream(hmb);
		try {
			int maxM3 = 0;
			int lastch = 0;
			List<Integer> start = new ArrayList<Integer>();
			List<List<int[]>> mtx = new ArrayList<List<int[]>>();
			y = 0;
			for (int b = 0; b < 4352; b++) {
				if (bw[b] > 0) {
					int x = 1;
					for (int c = 0; c < 256; c++) {
						if (g[b][c] != null) {
							int ch = (b << 8) | c;
							int[] m = new int[]{
									l[b][c], // x offset
									w[b][c], // character advance
									r[b][c],
									l[b][c] + w[b][c] + r[b][c], // bitmap width
									x+1,
									y+1
							};
							if (start.isEmpty() || ch != (lastch+1)) {
								start.add(ch);
								List<int[]> nmtx = new ArrayList<int[]>();
								nmtx.add(m);
								mtx.add(nmtx);
							} else {
								mtx.get(mtx.size()-1).add(m);
							}
							if (m[3] > maxM3) maxM3 = m[3];
							lastch = ch;
							x += 1 + l[b][c] + w[b][c] + r[b][c];
						}
					}
					y += bh;
				}
			}
			if (maxM3 < 256 && width < 65536 && height < 65536) {
				hmd.writeShort(1);
			} else {
				hmd.writeShort(2);
			}
			hmd.writeInt(start.size());
			for (int i = 0; i < start.size(); i++) {
				hmd.writeInt(start.get(i));
				hmd.writeInt(mtx.get(i).size());
				for (int[] m : mtx.get(i)) {
					if (maxM3 < 256 && width < 65536 && height < 65536) {
						hmd.writeByte(m[0]);
						hmd.writeByte(m[1]);
						hmd.writeByte(m[2]);
						hmd.writeByte(m[3]);
						hmd.writeShort(m[4]);
						hmd.writeShort(m[5]);
					} else {
						hmd.writeShort(m[0]);
						hmd.writeShort(m[1]);
						hmd.writeShort(m[2]);
						hmd.writeShort(m[3]);
						hmd.writeInt(m[4]);
						hmd.writeInt(m[5]);
					}
				}
			}
			hmd.close();
			hmb.close();
		} catch (IOException ignored) {
			// nothing
		}
		RFontBean rb = new RFontBean();
		rb.fname = font.getName(Font.NAME_FAMILY);
		rb.sname = font.getName(Font.NAME_STYLE);
		rb.copy = font.getName(Font.NAME_COPYRIGHT);
		rb.img = bi;
		rb.vmtx = vmb.toByteArray();
		rb.hmtx = hmb.toByteArray();
		return rb;
	}
	
	// set Character Codepoint control column
	private static void setCC(BufferedImage bi, int x, int y, int bh, int ch) {
		int ccy = y;
		// control column header, in magenta
		bi.setRGB(x, ccy++, 0xFFFF00FF);
		// code point in 0xFFabcdef, 0xFFaabbcc, 0xFFddeeff, 0xFFaaabbb, 0xFFcccddd, 0xFFeeefff, ...
		for (int cpy = 0; cpy < bh-1; cpy++) {
			bi.setRGB(x, ccy++, codepixel(ch, cpy));
		}
	}
	
	// get pixel color for code point
	private static int codepixel(int ch, int x) {
		// n(x) = 1, 2, 2, 3, 3, 3, 4, 4, 4, 4, ...
		int n = (int)Math.floor((1+Math.sqrt(1+8*x))/2);
		// i(x) = 0, 0, 1, 0, 1, 2, 0, 1, 2, 3, ...
		int i = (x-((n*(n-1))/2));
		// p is our pixel
		int p = 0xFF000000;
		// for each nybble 0..5 in the pixel
		for (int j = 0, m = 20; j < 6 && m >= 0; j++, m -= 4) {
			// w = which nybble a..f in the code point to use for this nybble 0..5 in the pixel
			int w = 5 - ((i*6 + j) / n);
			// copy this nybble a..f in the code point to this nybble 0..5 in the pixel
			p |= ((ch >> (w << 2)) & 0xF) << m;
		}
		return p;
	}
	
	// set Vertical Metrics control column
	private static void setVM(BufferedImage bi, int x, int y, BitmapFont font, int maxascent, int maxdescent) {
		int vmy = y;
		// control column header, in magenta
		bi.setRGB(x, vmy++, 0xFFFF00FF);
		// maxAscent to lineAscent, in red
		for (int may = maxascent; may > font.getLineAscent(); may--) {
			bi.setRGB(x, vmy++, 0xFFFF0000);
		}
		// lineAscent to emAscent, in orange
		for (int lay = font.getLineAscent(); lay > font.getEmAscent(); lay--) {
			bi.setRGB(x, vmy++, 0xFFFF8000);
		}
		// emAscent to xHeight, in yellow
		for (int eay = font.getEmAscent(); eay > font.getXHeight(); eay--) {
			bi.setRGB(x, vmy++, 0xFFFFFF00);
		}
		// xHeight to baseline, no color
		for (int xhy = font.getXHeight(); xhy > 0; xhy--) {
			bi.setRGB(x, vmy++, 0);
		}
		// baseline to emDescent, in green
		for (int edy = font.getEmDescent(); edy > 0; edy--) {
			bi.setRGB(x, vmy++, 0xFF00FF00);
		}
		// emDescent to lineDescent, in cyan
		for (int ldy = font.getLineDescent(); ldy > font.getEmDescent(); ldy--) {
			bi.setRGB(x, vmy++, 0xFF00FFFF);
		}
		// lineDescent to maxDescent, in blue
		for (int mdy = maxdescent; mdy > font.getLineDescent(); mdy--) {
			bi.setRGB(x, vmy++, 0xFF0000FF);
		}
		// line gap, in purple
		for (int lgy = font.getLineGap(); lgy > 0; lgy--) {
			bi.setRGB(x, vmy++, 0xFF8000FF);
		}
	}
}
