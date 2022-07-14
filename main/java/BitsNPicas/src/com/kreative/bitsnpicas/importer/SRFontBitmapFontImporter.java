package com.kreative.bitsnpicas.importer;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import com.kreative.bitsnpicas.BitmapFont;
import com.kreative.bitsnpicas.BitmapFontGlyph;
import com.kreative.bitsnpicas.BitmapFontImporter;
import com.kreative.bitsnpicas.Font;

public class SRFontBitmapFontImporter implements BitmapFontImporter {
	public BitmapFont[] importFont(byte[] data) throws IOException {
		ByteArrayInputStream b = new ByteArrayInputStream(data);
		BitmapFont[] f = importFont(b);
		b.close();
		return f;
	}
	
	public BitmapFont[] importFont(InputStream is) throws IOException {
		//BufferedImage bi = ImageIO.read(is);
		Iterator<ImageReader> iterator = ImageIO.getImageReadersBySuffix("png");
		ImageReader imagereader = iterator.next();
		imagereader.setInput(ImageIO.createImageInputStream(is));
		BufferedImage bi = imagereader.read(0);
		BitmapFont f = importFont(bi);
		if (f == null) return new BitmapFont[0];
		IIOImage iioImage = imagereader.readAll(0, null);
		IIOMetadata metadata = iioImage.getMetadata();
		String format = metadata.getNativeMetadataFormatName();
		Node root = metadata.getAsTree(format);
		for (Node n = root.getFirstChild(); n != null; n = n.getNextSibling()) {
			if ("tEXt".equals(n.getNodeName())) {
				for (Node m = n.getFirstChild(); m != null; m = m.getNextSibling()) {
					if ("tEXtEntry".equals(m.getNodeName())) {
						NamedNodeMap attr = m.getAttributes();
						String k = attr.getNamedItem("keyword").getNodeValue();
						String v = attr.getNamedItem("value").getNodeValue();
						if ("FontFamily".equals(k)) f.setName(Font.NAME_FAMILY, v);
						if ("FontStyle".equals(k)) f.setName(Font.NAME_STYLE, v);
						if ("Copyright".equals(k)) f.setName(Font.NAME_COPYRIGHT, v);
					}
				}
			}
		}
		return new BitmapFont[]{f};
	}
	
	public BitmapFont[] importFont(File file) throws IOException {
		FileInputStream in = new FileInputStream(file);
		BitmapFont[] f = importFont(in);
		in.close();
		return f;
	}
	
	public boolean canImportFont(BufferedImage bi) {
		return bi != null && decodeVM(bi, 0, 0, bi.getHeight()) != null;
	}
	
	public BitmapFont importFont(BufferedImage bi) {
		int[] mtx = decodeVM(bi, 0, 0, bi.getHeight());
		if (mtx == null) return null;
		int bh = 1 + mtx[MAX_ASCENT] + mtx[MAX_DESCENT] + mtx[LINE_GAP];
		int cp = decodeCC(bi, 1, 0, bh);
		if (cp < 0) {
			// SFont
			List<Integer> runs = new ArrayList<Integer>();
			List<Integer> widths = new ArrayList<Integer>();
			int x = 0;
			int width = bi.getWidth();
			while (x < width) {
				int cr = 0;
				int cw = 0;
				while (x < width && decodeControlPixel(bi.getRGB(x, 0)) == MAGENTA) {
					x++; cr++;
				}
				while (x < width && decodeControlPixel(bi.getRGB(x, 0)) != MAGENTA) {
					x++; cw++;
				}
				runs.add(cr);
				widths.add(cw);
			}
			int[] l = new int[runs.size()-1];
			int[] w = new int[runs.size()-1];
			int[] r = new int[runs.size()-1];
			byte[][][] g = new byte[runs.size()-1][][];
			x = 0;
			int tw = 0;
			for (int i = 0; i < l.length; i++) {
				l[i] = (i == 0) ? runs.get(i) : ((runs.get(i)+1)/2);
				w[i] = widths.get(i);
				r[i] = (i == l.length-1) ? runs.get(i+1) : (runs.get(i+1)/2);
				int gw = l[i] + w[i] + r[i];
				g[i] = new byte[bh-1][gw];
				for (int gy = 0; gy < g[i].length; gy++) {
					for (int gx = 0; gx < gw; gx++) {
						g[i][gy][gx] = (byte)((bi.getRGB(x+gx, 1+gy) >> 24) & 0xFF);
					}
				}
				x += gw;
				tw += w[i];
			}
			int Xi = 'X'-33;
			if (Xi < g.length) {
				byte[][] Xg = g[Xi];
				for (int i = 0; i < Xg.length; i++) {
					if (arrayEmpty(Xg[i])) {
						mtx[EM_ASCENT]--;
					} else {
						break;
					}
				}
			}
			int xi = 'x'-33;
			if (xi < g.length) {
				byte[][] xg = g[xi];
				for (int i = 0; i < xg.length; i++) {
					if (arrayEmpty(xg[i])) {
						mtx[X_HEIGHT]--;
					} else {
						break;
					}
				}
				for (int i = xg.length-1; i >= 0; i--) {
					if (arrayEmpty(xg[i])) {
						mtx[MAX_ASCENT]--;
						mtx[LINE_ASCENT]--;
						mtx[EM_ASCENT]--;
						mtx[X_HEIGHT]--;
						mtx[EM_DESCENT]++;
						mtx[LINE_DESCENT]++;
						mtx[MAX_DESCENT]++;
					} else {
						break;
					}
				}
			}
			int yi = 'y'-33;
			if (yi < g.length) {
				byte[][] yg = g[yi];
				for (int i = yg.length-1; i >= 0; i--) {
					if (arrayEmpty(yg[i])) {
						mtx[EM_DESCENT]--;
					} else {
						break;
					}
				}
			}
			BitmapFont bm = new BitmapFont(mtx[EM_ASCENT], mtx[EM_DESCENT], mtx[LINE_ASCENT], mtx[LINE_DESCENT], mtx[X_HEIGHT], mtx[LINE_GAP]);
			bm.putCharacter(32, new BitmapFontGlyph(new byte[bh-1][tw/g.length], 0, tw/g.length, mtx[MAX_ASCENT]));
			for (int i = 0; i < g.length; i++) {
				bm.putCharacter(i+33, new BitmapFontGlyph(g[i], -l[i], w[i], mtx[MAX_ASCENT]));
			}
			return bm;
		} else {
			// RFont
			Map<Integer,Integer> l = new HashMap<Integer,Integer>();
			Map<Integer,Integer> w = new HashMap<Integer,Integer>();
			Map<Integer,Integer> r = new HashMap<Integer,Integer>();
			Map<Integer,byte[][]> g = new HashMap<Integer,byte[][]>();
			int height = bi.getHeight();
			int width = bi.getWidth();
			for (int y = 0; y+bh <= height; y += bh) {
				int x = 1;
				while (x < width) {
					int ch = decodeCC(bi, x, y, bh);
					if (ch < 0 || ch >= 0x110000) {
						break;
					} else {
						x++;
						int ox = x;
						int ll = 0;
						int lw = 0;
						int lr = 0;
						while (x < width && decodeControlPixel(bi.getRGB(x, y)) == RED) {
							x++; ll++;
						}
						while (x < width && decodeControlPixel(bi.getRGB(x, y)) == TRANSPARENT) {
							x++; lw++;
						}
						while (x < width && decodeControlPixel(bi.getRGB(x, y)) == BLUE) {
							x++; lr++;
						}
						int gw = ll+lw+lr;
						byte[][] lg = new byte[bh-1][gw];
						for (int gy = 0; gy < lg.length; gy++) {
							for (int gx = 0; gx < gw; gx++) {
								lg[gy][gx] = (byte)((bi.getRGB(ox+gx, y+1+gy) >> 24) & 0xFF);
							}
						}
						l.put(ch, ll);
						w.put(ch, lw);
						r.put(ch, lr);
						g.put(ch, lg);
					}
				}
			}
			BitmapFont bm = new BitmapFont(mtx[EM_ASCENT], mtx[EM_DESCENT], mtx[LINE_ASCENT], mtx[LINE_DESCENT], mtx[X_HEIGHT], mtx[LINE_GAP]);
			for (int ch : g.keySet()) {
				bm.putCharacter(ch, new BitmapFontGlyph(g.get(ch), -l.get(ch), w.get(ch), mtx[MAX_ASCENT]));
			}
			return bm;
		}
	}
	
	private static final int TRANSPARENT = 0;
	private static final int RED         = 02200;
	private static final int ORANGE      = 02210;
	private static final int YELLOW      = 02220;
	private static final int GREEN       = 02020;
	private static final int CYAN        = 02022;
	private static final int BLUE        = 02002;
	private static final int VIOLET      = 02102;
	private static final int MAGENTA     = 02202;
	private int decodeControlPixel(int rgb) {
		int a = ((rgb >> 24) & 0xFF) / 86;
		if (a == 0) return 0;
		int r = ((rgb >> 16) & 0xFF) / 86;
		int g = ((rgb >>  8) & 0xFF) / 86;
		int b = ((rgb >>  0) & 0xFF) / 86;
		return (a<<9) | (r<<6) | (g<<3) | b;
	}
	private static final int MAX_ASCENT = 0;
	private static final int LINE_ASCENT = 1;
	private static final int EM_ASCENT = 2;
	private static final int X_HEIGHT = 3;
	private static final int EM_DESCENT = 4;
	private static final int LINE_DESCENT = 5;
	private static final int MAX_DESCENT = 6;
	private static final int LINE_GAP = 7;
	private int[] decodeVM(BufferedImage bi, int x, int y, int height) {
		if (decodeControlPixel(bi.getRGB(x, y)) != MAGENTA) {
			return null;
		} else {
			int[] mtx = new int[8];
			y++;
			while (y < height) {
				switch (decodeControlPixel(bi.getRGB(x, y))) {
				case TRANSPARENT: mtx[X_HEIGHT]++; // continue;
				case YELLOW: mtx[EM_ASCENT]++; // continue;
				case ORANGE: mtx[LINE_ASCENT]++; // continue;
				case RED: mtx[MAX_ASCENT]++; break;
				case GREEN: mtx[EM_DESCENT]++; // continue;
				case CYAN: mtx[LINE_DESCENT]++; // continue;
				case BLUE: mtx[MAX_DESCENT]++; break;
				case VIOLET: mtx[LINE_GAP]++; break;
				case MAGENTA: return mtx;
				default: return null;
				}
				y++;
			}
			return mtx;
		}
	}
	
	private int decodeCC(BufferedImage bi, int x, int y, int bh) {
		int cp = bi.getRGB(x, y);
		if (
				(((cp >> 24) & 0xFF) < 0xAA) ||
				(((cp >> 16) & 0xFF) < 0xAA) ||
				(((cp >>  8) & 0xFF) > 0x55) ||
				(((cp >>  0) & 0xFF) < 0xAA)
		) {
			return -1;
		}
		for (int i = 1; i < bh; i++) {
			int rgb = bi.getRGB(x, y+i);
			if (((rgb >> 24) & 0xFF) < 0xAA) {
				return -1;
			}
		}
		if (bh >= 22) {
			int p1 = bi.getRGB(x, y+16) & 0xFFFFFF;
			int p2 = bi.getRGB(x, y+17) & 0xFFFFFF;
			int p3 = bi.getRGB(x, y+18) & 0xFFFFFF;
			int p4 = bi.getRGB(x, y+19) & 0xFFFFFF;
			int p5 = bi.getRGB(x, y+20) & 0xFFFFFF;
			int p6 = bi.getRGB(x, y+21) & 0xFFFFFF;
			int a = (((p1 >> 20) & 0xF) + ((p1 >> 12) & 0xF) + ((p1 >> 4) & 0xF)) / 3;
			int b = (((p2 >> 20) & 0xF) + ((p2 >> 12) & 0xF) + ((p2 >> 4) & 0xF)) / 3;
			int c = (((p3 >> 20) & 0xF) + ((p3 >> 12) & 0xF) + ((p3 >> 4) & 0xF)) / 3;
			int d = (((p4 >> 20) & 0xF) + ((p4 >> 12) & 0xF) + ((p4 >> 4) & 0xF)) / 3;
			int e = (((p5 >> 20) & 0xF) + ((p5 >> 12) & 0xF) + ((p5 >> 4) & 0xF)) / 3;
			int f = (((p6 >> 20) & 0xF) + ((p6 >> 12) & 0xF) + ((p6 >> 4) & 0xF)) / 3;
			return (a<<20) | (b<<16) | (c<<12) | (d<<8) | (e<<4) | f;
		} else if (bh >= 11) {
			int p1 = bi.getRGB(x, y+7) & 0xFFFFFF;
			int p2 = bi.getRGB(x, y+8) & 0xFFFFFF;
			int p3 = bi.getRGB(x, y+9) & 0xFFFFFF;
			int p4 = bi.getRGB(x, y+10) & 0xFFFFFF;
			int a = (((p1 >> 20) & 0xF) + ((p1 >> 12) & 0xF)) / 2;
			int b = (((p1 >>  4) & 0xF) + ((p2 >> 20) & 0xF)) / 2;
			int c = (((p2 >> 12) & 0xF) + ((p2 >>  4) & 0xF)) / 2;
			int d = (((p3 >> 20) & 0xF) + ((p3 >> 12) & 0xF)) / 2;
			int e = (((p3 >>  4) & 0xF) + ((p4 >> 20) & 0xF)) / 2;
			int f = (((p4 >> 12) & 0xF) + ((p4 >>  4) & 0xF)) / 2;
			return (a<<20) | (b<<16) | (c<<12) | (d<<8) | (e<<4) | f;
		} else if (bh >= 7) {
			int p1 = bi.getRGB(x, y+4) & 0xFFFFFF;
			int p2 = bi.getRGB(x, y+5) & 0xFFFFFF;
			int p3 = bi.getRGB(x, y+6) & 0xFFFFFF;
			int a = (p1 >> 20) & 0xF;
			int b = (p1 >>  4) & 0xF;
			int c = (p2 >> 20) & 0xF;
			int d = (p2 >>  4) & 0xF;
			int e = (p3 >> 20) & 0xF;
			int f = (p3 >>  4) & 0xF;
			return (a<<20) | (b<<16) | (c<<12) | (d<<8) | (e<<4) | f;
		} else if (bh >= 4) {
			int p1 = bi.getRGB(x, y+2) & 0xFFFFFF;
			int p2 = bi.getRGB(x, y+3) & 0xFFFFFF;
			int a = (p1 >> 20) & 0xF;
			int b = (p1 >> 12) & 0xF;
			int c = (p1 >>  4) & 0xF;
			int d = (p2 >> 20) & 0xF;
			int e = (p2 >> 12) & 0xF;
			int f = (p2 >>  4) & 0xF;
			return (a<<20) | (b<<16) | (c<<12) | (d<<8) | (e<<4) | f;
		} else if (bh >= 2) {
			return bi.getRGB(x, y+1) & 0xFFFFFF;
		} else {
			return -1;
		}
	}
	
	private boolean arrayEmpty(byte[] a) {
		for (byte b : a) if (b != 0) return false;
		return true;
	}
}
