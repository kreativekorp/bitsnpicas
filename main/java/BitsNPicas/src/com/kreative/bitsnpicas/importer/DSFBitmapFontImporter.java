package com.kreative.bitsnpicas.importer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Scanner;

import com.kreative.bitsnpicas.BitmapFont;
import com.kreative.bitsnpicas.BitmapFontGlyph;
import com.kreative.bitsnpicas.BitmapFontImporter;
import com.kreative.bitsnpicas.Font;

public class DSFBitmapFontImporter implements BitmapFontImporter {
	public BitmapFont[] importFont(byte[] data) throws IOException {
		Scanner in = new Scanner(new ByteArrayInputStream(data), "UTF-8");
		BitmapFont f = importFont(in);
		in.close();
		if (f == null) return new BitmapFont[0];
		return new BitmapFont[]{f};
	}

	public BitmapFont[] importFont(InputStream is) throws IOException {
		Scanner in = new Scanner(is, "UTF-8");
		BitmapFont f = importFont(in);
		if (f == null) return new BitmapFont[0];
		return new BitmapFont[]{f};
	}

	public BitmapFont[] importFont(File file) throws IOException {
		Scanner in = new Scanner(file, "UTF-8");
		BitmapFont f = importFont(in);
		in.close();
		if (f == null) return new BitmapFont[0];
		return new BitmapFont[]{f};
	}
	
	private static BitmapFont importFont(Scanner in) {
		if (in.nextLine().equals("DosStartFont")) {
			String[] tmp;
			String fontName = in.nextLine();
			tmp = in.nextLine().split("\\s*,\\s*");
			int format = Integer.parseInt(tmp[0].trim());
			if (format == 0 || format == 1) {
				int[] widths = new int[95];
				int[][] bboxes = new int[widths.length][];
				boolean[][][] bitmaps = new boolean[widths.length][][];
				int ascent = 0;
				int descent = 0;
				if (format == 1) {
					ascent = Integer.parseInt(tmp[1].trim());
				}
				for (int i=0; i<widths.length; i++) {
					tmp = in.nextLine().split("\\s*,\\s*");
					widths[i] = Integer.parseInt(tmp[1].trim());
					if (format == 1) {
						int cols = Integer.parseInt(tmp[0].substring(0, 3));
						int rows = Integer.parseInt(tmp[0].substring(3, 6));
						bboxes[i] = new int[4];
						bboxes[i][0] = 0;
						bboxes[i][1] = ascent;
						bboxes[i][2] = cols;
						bboxes[i][3] = ascent - rows;
						bitmaps[i] = new boolean[rows][cols];
						int chnum = 6;
						for (int row=0; row<rows && chnum<tmp[0].length(); row++) {
							for (int col=0; col<cols && chnum<tmp[0].length(); col++) {
								char ch = tmp[0].charAt(chnum++);
								bitmaps[i][row][col] = (ch == 'H' || ch == 'h');
							}
						}
						descent = Math.max(descent, rows - ascent);
					}
					else if (format == 0) {
						bboxes[i] = dsfBBox(tmp[0]);
						bitmaps[i] = dsfBitmap(tmp[0], bboxes[i]);
						ascent = Math.max(ascent, bboxes[i][1]);
						descent = Math.max(descent, -bboxes[i][3]);
					}
				}
				int minx = 0, miny = 0, maxx = 0, maxy = 0;
				for (int[] bbox : bboxes) {
					if (bbox[0] < minx) minx = bbox[0];
					if (bbox[1] > maxy) maxy = bbox[1];
					if (bbox[2] > maxx) maxx = bbox[2];
					if (bbox[3] < miny) miny = bbox[3];
				}
				BitmapFont bf = new BitmapFont(ascent, descent, ascent, descent, 0, 0);
				bf.setName(Font.NAME_FAMILY, fontName);
				for (int i=0; i<widths.length; i++) {
					boolean[][] boolmp = bitmaps[i];
					byte[][] bmp = new byte[boolmp.length][];
					for (int j = 0; j < boolmp.length; j++) {
						bmp[j] = new byte[boolmp[j].length];
						for (int k = 0; k < boolmp[j].length; k++) {
							bmp[j][k] = (byte)(boolmp[j][k] ? -1 : 0);
						}
					}
					BitmapFontGlyph g = new BitmapFontGlyph(bmp, bboxes[i][0], widths[i], bboxes[i][1]);
					bf.putCharacter(i+32, g);
				}
				return bf;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
	
	private static int sgn(int a) {
		if (a < 0) return -1;
		if (a > 0) return 1;
		return 0;
	}
	
	private static int[] dsfBBox(String str) {
		int minx = 0, miny = 0, maxx = 0, maxy = 0, curx = 0, cury = 0;
		Iterator<Integer> ti = new DSFTokenIterator(str);
		while (ti.hasNext()) {
			int t = ti.next();
			int attr = t & 0x3F;
			int val = t >>> 6;
			int nextx = ((attr & 0x10) != 0) ? (curx - val) : ((attr & 0x20) != 0) ? (curx + val) : curx;
			int nexty = ((attr & 0x04) != 0) ? (cury + val) : ((attr & 0x08) != 0) ? (cury - val) : cury;
			if (nextx < minx) minx = nextx;
			if (nextx > maxx) maxx = nextx;
			if (nexty < miny) miny = nexty;
			if (nexty > maxy) maxy = nexty;
			if ((attr & 0x01) != 0) {
				curx = nextx;
				cury = nexty;
			}
		}
		return new int[]{minx, maxy+1, maxx+1, miny};
	}
	
	private static boolean[][] dsfBitmap(String str, int[] bbox) {
		int rows = bbox[1] - bbox[3];
		int cols = bbox[2] - bbox[0];
		boolean[][] bmap = new boolean[rows][cols];
		int curx = -bbox[0];
		int cury = bbox[1]-1;
		Iterator<Integer> ti = new DSFTokenIterator(str);
		while (ti.hasNext()) {
			int t = ti.next();
			int attr = t & 0x3F;
			int val = t >>> 6;
			int nextx = ((attr & 0x10) != 0) ? (curx - val) : ((attr & 0x20) != 0) ? (curx + val) : curx;
			int nexty = ((attr & 0x04) != 0) ? (cury - val) : ((attr & 0x08) != 0) ? (cury + val) : cury;
			if ((attr & 0x02) != 0) {
				int xstep = sgn(nextx - curx);
				int ystep = sgn(nexty - cury);
				for (int x = curx, y = cury; val >= 0; x += xstep, y += ystep) {
					if (x >= 0 && y >= 0 && x < cols && y < rows) bmap[y][x] = true;
					val--;
				}
			}
			if ((attr & 0x01) != 0) {
				curx = nextx;
				cury = nexty;
			}
		}
		return bmap;
	}
	
	private static class DSFTokenIterator implements Iterator<Integer> {
		private String str;
		private int strl;
		private int pos;
		public DSFTokenIterator(String str) {
			this.str = str+"xxx";
			this.strl = str.length();
			this.pos = 0;
		}
		public boolean hasNext() {
			return (pos < strl);
		}
		public Integer next() {
			int attr = 0x01 | 0x02;
			char ch = str.charAt(pos++);
			switch (ch) {
				case 'n': case 'N': attr = 0x02; ch = str.charAt(pos++); break;
				case 'b': case 'B': attr = 0x01; ch = str.charAt(pos++); break;
			}
			switch (ch) {
				case 'u': case 'U': attr |= 0x04; break;
				case 'd': case 'D': attr |= 0x08; break;
				case 'l': case 'L': attr |= 0x10; break;
				case 'r': case 'R': attr |= 0x20; break;
				case 'e': case 'E': attr |= 0x24; break;
				case 'f': case 'F': attr |= 0x28; break;
				case 'g': case 'G': attr |= 0x18; break;
				case 'h': case 'H': attr |= 0x14; break;
			}
			int val = 0;
			while (Character.isDigit(str.charAt(pos))) {
				val = val * 10 + Character.getNumericValue(str.charAt(pos++));
			}
			if (val == 0) val = 1;
			return (val << 6) | attr;
		}
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
