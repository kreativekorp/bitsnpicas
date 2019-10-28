package com.kreative.bitsnpicas.geos;

import java.awt.image.BufferedImage;

public class GEOSIcons {
	public static byte[] blankIcon() {
		return new byte[]{
			(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0x80,(byte)0x00,(byte)0x01,(byte)0x80,(byte)0x00,(byte)0x01,
			(byte)0x80,(byte)0x00,(byte)0x01,(byte)0x80,(byte)0x00,(byte)0x01,(byte)0x80,(byte)0x00,(byte)0x01,
			(byte)0x80,(byte)0x00,(byte)0x01,(byte)0x80,(byte)0x00,(byte)0x01,(byte)0x80,(byte)0x00,(byte)0x01,
			(byte)0x80,(byte)0x00,(byte)0x01,(byte)0x80,(byte)0x00,(byte)0x01,(byte)0x80,(byte)0x00,(byte)0x01,
			(byte)0x80,(byte)0x00,(byte)0x01,(byte)0x80,(byte)0x00,(byte)0x01,(byte)0x80,(byte)0x00,(byte)0x01,
			(byte)0x80,(byte)0x00,(byte)0x01,(byte)0x80,(byte)0x00,(byte)0x01,(byte)0x80,(byte)0x00,(byte)0x01,
			(byte)0x80,(byte)0x00,(byte)0x01,(byte)0x80,(byte)0x00,(byte)0x01,(byte)0xFF,(byte)0xFF,(byte)0xFF,
		};
	}
	
	public static byte[] fontIcon() {
		return new byte[]{
			(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0x80,(byte)0x00,(byte)0x01,(byte)0x80,(byte)0x00,(byte)0x01,
			(byte)0x80,(byte)0x00,(byte)0x01,(byte)0xBF,(byte)0x80,(byte)0x01,(byte)0x98,(byte)0x80,(byte)0x09,
			(byte)0x98,(byte)0x00,(byte)0x19,(byte)0x98,(byte)0x00,(byte)0x19,(byte)0x9E,(byte)0x00,(byte)0x3D,
			(byte)0x98,(byte)0xE7,(byte)0x99,(byte)0x99,(byte)0xB6,(byte)0xD9,(byte)0x99,(byte)0xB6,(byte)0xD9,
			(byte)0x99,(byte)0xB6,(byte)0xD9,(byte)0xBC,(byte)0xE6,(byte)0xCD,(byte)0x80,(byte)0x00,(byte)0x01,
			(byte)0x80,(byte)0x00,(byte)0x01,(byte)0x80,(byte)0x00,(byte)0x01,(byte)0x80,(byte)0x00,(byte)0x01,
			(byte)0x80,(byte)0x00,(byte)0x01,(byte)0x80,(byte)0x00,(byte)0x01,(byte)0xFF,(byte)0xFF,(byte)0xFF,
		};
	}
	
	public static byte[] printerIcon() {
		return new byte[]{
			(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0x80,(byte)0x00,(byte)0x01,(byte)0x80,(byte)0x3F,(byte)0xF1,
			(byte)0x80,(byte)0x40,(byte)0x61,(byte)0x80,(byte)0x57,(byte)0x61,(byte)0x80,(byte)0x40,(byte)0x61,
			(byte)0x80,(byte)0x42,(byte)0x61,(byte)0x80,(byte)0x80,(byte)0xC1,(byte)0x83,(byte)0x08,(byte)0xFD,
			(byte)0x85,(byte)0x00,(byte)0xCD,(byte)0x89,(byte)0xFF,(byte)0xDD,(byte)0x90,(byte)0x00,(byte)0x3D,
			(byte)0xBF,(byte)0xFF,(byte)0xFD,(byte)0xA0,(byte)0x00,(byte)0x79,(byte)0xA7,(byte)0xF3,(byte)0x71,
			(byte)0xA0,(byte)0x00,(byte)0x61,(byte)0xBF,(byte)0xFF,(byte)0xC1,(byte)0x80,(byte)0x00,(byte)0x01,
			(byte)0x80,(byte)0x00,(byte)0x01,(byte)0x80,(byte)0x00,(byte)0x01,(byte)0xFF,(byte)0xFF,(byte)0xFF,
		};
	}
	
	public static String toString(byte[] iconBitmap) {
		int[] cps = new int[91];
		for (int ci = 0, by = 0; by < 63; by += 9) {
			for (int bx = 0; bx < 3; bx++) {
				for (int m = 0x80; m != 0; m >>= 2, ci++) {
					if ((iconBitmap[by+bx+0] & (m >> 0)) != 0) cps[ci] |= 0x01;
					if ((iconBitmap[by+bx+0] & (m >> 1)) != 0) cps[ci] |= 0x02;
					if ((iconBitmap[by+bx+3] & (m >> 0)) != 0) cps[ci] |= 0x04;
					if ((iconBitmap[by+bx+3] & (m >> 1)) != 0) cps[ci] |= 0x08;
					if ((iconBitmap[by+bx+6] & (m >> 0)) != 0) cps[ci] |= 0x10;
					if ((iconBitmap[by+bx+6] & (m >> 1)) != 0) cps[ci] |= 0x20;
					switch (cps[ci]) {
						case 0x00: cps[ci] = 0x00A0; break;
						case 0x15: cps[ci] = 0x258C; break;
						case 0x2A: cps[ci] = 0x2590; break;
						case 0x3F: cps[ci] = 0x2588; break;
						default:
							if (cps[ci] > 0x2A) cps[ci]--;
							if (cps[ci] > 0x15) cps[ci]--;
							if (cps[ci] > 0x00) cps[ci]--;
							cps[ci] |= 0x1FB00;
							break;
					}
				}
			}
			cps[ci] = '\n';
			ci++;
		}
		StringBuffer sb = new StringBuffer();
		for (int cp : cps) sb.append(Character.toChars(cp));
		return sb.toString();
	}
	
	public static byte[][] toGlyph(byte[] iconBitmap) {
		byte[][] gd = new byte[21][24];
		for (int bi = 0, y = 0; y < 21; y++) {
			for (int x = 0, i = 0; i < 3; i++, bi++) {
				for (int m = 0x80; m != 0; m >>= 1, x++) {
					gd[y][x] = ((iconBitmap[bi] & m) == 0) ? (byte)0 : (byte)(-1);
				}
			}
		}
		return gd;
	}
	
	public static byte[] fromGlyph(byte[][] gd) {
		byte[] iconBitmap = new byte[63];
		for (int bi = 0, y = 0; y < 21; y++) {
			for (int x = 0, i = 0; i < 3; i++, bi++) {
				for (int m = 0x80; m != 0; m >>= 1, x++) {
					if (gd[y][x] < 0) iconBitmap[bi] |= m;
				}
			}
		}
		return iconBitmap;
	}
	
	public static int[] toRGB(byte[] iconBitmap) {
		int[] rgb = new int[504];
		for (int pi = 0, bi = 0, y = 0; y < 21; y++) {
			for (int x = 0; x < 3; x++, bi++) {
				for (int m = 0x80; m != 0; m >>= 1, pi++) {
					rgb[pi] = ((iconBitmap[bi] & m) == 0) ? 0 : 0xFF000000;
				}
			}
		}
		return rgb;
	}
	
	public static byte[] fromRGB(int[] rgb) {
		byte[] iconBitmap = new byte[63];
		for (int pi = 0, bi = 0, y = 0; y < 21; y++) {
			for (int x = 0; x < 3; x++, bi++) {
				for (int m = 0x80; m != 0; m >>= 1, pi++) {
					int a = (rgb[pi] >> 24) & 0xFF;
					int r = (rgb[pi] >> 16) & 0xFF;
					int g = (rgb[pi] >>  8) & 0xFF;
					int b = (rgb[pi] >>  0) & 0xFF;
					int k = (30 * r + 59 * g + 11 * b) / 100;
					if (a >= 0x80 && k < 0x80) iconBitmap[bi] |= m;
				}
			}
		}
		return iconBitmap;
	}
	
	public static BufferedImage toImage(byte[] iconBitmap) {
		BufferedImage image = new BufferedImage(24, 21, BufferedImage.TYPE_INT_ARGB);
		image.setRGB(0, 0, 24, 21, toRGB(iconBitmap), 0, 24);
		return image;
	}
	
	public static byte[] fromImage(BufferedImage image, int x, int y) {
		int[] rgb = new int[504];
		image.getRGB(x, y, 24, 21, rgb, 0, 24);
		return fromRGB(rgb);
	}
}
