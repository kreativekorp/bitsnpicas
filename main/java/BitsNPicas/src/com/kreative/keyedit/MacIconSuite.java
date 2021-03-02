package com.kreative.keyedit;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.imageio.ImageIO;

public class MacIconSuite extends LinkedHashMap<Integer,byte[]> {
	private static final long serialVersionUID = 1L;
	public static final int icns = 0x69636E73;
	public static final int ICON = 0x49434F4E;
	
	// 16x12 classic
	public static final int icm$ = 0x69636D23;
	public static final int icm4 = 0x69636D34;
	public static final int icm8 = 0x69636D38;
	public static final int im32 = 0x696D3332;
	public static final int m8mk = 0x6D386D6B;
	
	// 16x16 classic
	public static final int ics$ = 0x69637323;
	public static final int ics4 = 0x69637334;
	public static final int ics8 = 0x69637338;
	public static final int is32 = 0x69733332;
	public static final int s8mk = 0x73386D6B;
	
	// 32x32 classic
	public static final int ICN$ = 0x49434E23;
	public static final int icl4 = 0x69636C34;
	public static final int icl8 = 0x69636C38;
	public static final int il32 = 0x696C3332;
	public static final int l8mk = 0x6C386D6B;
	
	// 48x48 classic
	public static final int ich$ = 0x69636823;
	public static final int ich4 = 0x69636834;
	public static final int ich8 = 0x69636838;
	public static final int ih32 = 0x69683332;
	public static final int h8mk = 0x68386D6B;
	
	// 128x128 classic
	public static final int ict$ = 0x69637423;
	public static final int ict4 = 0x69637434;
	public static final int ict8 = 0x69637438;
	public static final int it32 = 0x69743332;
	public static final int t8mk = 0x74386D6B;
	
	// 16x16, 32x32, 64x64 ARGB PackBits
	public static final int ic04 = 0x69633034;
	public static final int ic05 = 0x69633035;
	public static final int ic06 = 0x69633036;
	
	// 16x16, 32x32, 64x64 JPEG2000 / PNG
	public static final int icp4 = 0x69637034;
	public static final int icp5 = 0x69637035;
	public static final int icp6 = 0x69637036;
	
	// 128x128, 256x256, 512x512 JPEG2000 / PNG
	public static final int ic07 = 0x69633037;
	public static final int ic08 = 0x69633038;
	public static final int ic09 = 0x69633039;
	
	// 1024x1024 / 512x512@2x JPEG2000 / PNG
	public static final int ic10 = 0x69633130;
	
	// 16x16@2x, 32x32@2x, 128x128@2x, 256x256@2x JPEG2000 / PNG
	public static final int ic11 = 0x69633131;
	public static final int ic12 = 0x69633132;
	public static final int ic13 = 0x69633133;
	public static final int ic14 = 0x69633134;
	
	// 18x18, 36x36 (what is this used for?)
	public static final int icsb = 0x69637362;
	public static final int icsB = 0x69637342;
	
	// Metadata
	public static final int TOC  = 0x544F4320;
	public static final int icnV = 0x69636E56;
	public static final int name = 0x6E616D65;
	public static final int info = 0x696E666F;
	
	public void read(DataInput in) throws IOException {
		if (in.readInt() != icns) throw new IOException("Bad magic number");
		int fptr = 8;
		int flen = in.readInt();
		if (flen < 8) throw new IOException("Bad file length");
		while (fptr < flen) {
			int type = in.readInt();
			int ilen = in.readInt();
			if (ilen < 8) throw new IOException("Bad data length");
			byte[] data = new byte[ilen - 8];
			in.readFully(data);
			this.put(type, data);
			fptr += ilen;
		}
	}
	
	public void write(DataOutput out) throws IOException {
		int flen = this.size() * 8 + 8;
		for (byte[] data : this.values()) {
			flen += data.length;
		}
		out.writeInt(icns);
		out.writeInt(flen);
		for (Map.Entry<Integer,byte[]> e : this.entrySet()) {
			out.writeInt(e.getKey());
			out.writeInt(e.getValue().length + 8);
			out.write(e.getValue());
		}
	}
	
	public BufferedImage getImage() {
		for (int type : Arrays.asList(
			/* 1024x1024 */ ic10,
			/* 512x512 */ ic14, ic09,
			/* 256x256 */ ic13, ic08,
			/* 128x128 */ ic07, it32, ict8, ict4, ict$,
			/* 64x64 */ ic12, ic06, icp6,
			/* 48x48 */ ih32, ich8, ich4, ich$,
			/* 32x32 */ ic11, ic05, icp5, il32, icl8, icl4, ICN$, ICON,
			/* 16x16 */ ic04, icp4, is32, ics8, ics4, ics$,
			/* 16x12 */ im32, icm8, icm4, icm$
		)) {
			if (containsKey(type)) {
				BufferedImage image = getImage(type);
				if (image != null) return image;
			}
		}
		return null;
	}
	
	public BufferedImage getImage(int type) {
		switch (type) {
		case ICON: return decode1Bit(32, 32, get(ICON), 0, null, 0);
		case icm$: return decode1Bit(16, 12, get(icm$), 0, get(icm$), 24);
		case icm4: return decode4Bit(16, 12, get(icm4), 0, get(icm$), 24);
		case icm8: return decode8Bit(16, 12, get(icm8), 0, get(icm$), 24);
		case im32: return decode32Bit(16, 12, get(im32), 0, get(m8mk), 0);
		case m8mk: return decode32Bit(16, 12, null, 0, get(m8mk), 0);
		case ics$: return decode1Bit(16, 16, get(ics$), 0, get(ics$), 32);
		case ics4: return decode4Bit(16, 16, get(ics4), 0, get(ics$), 32);
		case ics8: return decode8Bit(16, 16, get(ics8), 0, get(ics$), 32);
		case is32: return decode32Bit(16, 16, get(is32), 0, get(s8mk), 0);
		case s8mk: return decode32Bit(16, 16, null, 0, get(s8mk), 0);
		case ICN$: return decode1Bit(32, 32, get(ICN$), 0, get(ICN$), 128);
		case icl4: return decode4Bit(32, 32, get(icl4), 0, get(ICN$), 128);
		case icl8: return decode8Bit(32, 32, get(icl8), 0, get(ICN$), 128);
		case il32: return decode32Bit(32, 32, get(il32), 0, get(l8mk), 0);
		case l8mk: return decode32Bit(32, 32, null, 0, get(l8mk), 0);
		case ich$: return decode1Bit(48, 48, get(ich$), 0, get(ich$), 288);
		case ich4: return decode4Bit(48, 48, get(ich4), 0, get(ich$), 288);
		case ich8: return decode8Bit(48, 48, get(ich8), 0, get(ich$), 288);
		case ih32: return decode32Bit(48, 48, get(ih32), 0, get(h8mk), 0);
		case h8mk: return decode32Bit(48, 48, null, 0, get(h8mk), 0);
		case ict$: return decode1Bit(128, 128, get(ict$), 0, get(ict$), 2048);
		case ict4: return decode4Bit(128, 128, get(ict4), 0, get(ict$), 2048);
		case ict8: return decode8Bit(128, 128, get(ict8), 0, get(ict$), 2048);
		case it32: return decode32Bit(128, 128, get(it32), 4, get(t8mk), 0);
		case t8mk: return decode32Bit(128, 128, null, 0, get(t8mk), 0);
		case ic04: case icp4: return decodeCompressed(16, 16, get(type), 0);
		case ic05: case icp5: return decodeCompressed(32, 32, get(type), 0);
		case ic06: case icp6: return decodeCompressed(64, 64, get(type), 0);
		case ic07: return decodeCompressed(128, 128, get(type), 0);
		case ic08: return decodeCompressed(256, 256, get(type), 0);
		case ic09: return decodeCompressed(512, 512, get(type), 0);
		case ic10: return decodeCompressed(1024, 1024, get(type), 0);
		case ic11: return decodeCompressed(32, 32, get(type), 0);
		case ic12: return decodeCompressed(64, 64, get(type), 0);
		case ic13: return decodeCompressed(256, 256, get(type), 0);
		case ic14: return decodeCompressed(512, 512, get(type), 0);
		default: return null;
		}
	}
	
	public void putImage(BufferedImage image) {
		int w = image.getWidth(), h = image.getHeight();
		if (w >= 1024 && h >= 1024) putImage(ic10, image);
		if (w >= 512 && h >= 512) putImage(ic09, image);
		if (w >= 256 && h >= 256) putImage(ic08, image);
		if (w >= 128 && h >= 128) { putImage(it32, image); putImage(t8mk, image); }
		if (w >= 48 && h >= 48) { putImage(ih32, image); putImage(h8mk, image); }
		if (w >= 32 && h >= 32) { putImage(il32, image); putImage(l8mk, image); }
		if (w >= 16 && h >= 16) { putImage(is32, image); putImage(s8mk, image); }
	}
	
	public void putImage(int type, BufferedImage image) {
		switch (type) {
		case ICON: put(type, encode1Bit(image, 32, 32, true, false)); break;
		case icm$: put(type, encode1Bit(image, 16, 12, true, true)); break;
		case icm4: put(type, encode4Bit(image, 16, 12)); break;
		case icm8: put(type, encode8Bit(image, 16, 12)); break;
		case im32: put(type, encode32Bit(image, 16, 12, 0, true, false)); break;
		case m8mk: put(type, encode32Bit(image, 16, 12, 0, false, true)); break;
		case ics$: put(type, encode1Bit(image, 16, 16, true, true)); break;
		case ics4: put(type, encode4Bit(image, 16, 16)); break;
		case ics8: put(type, encode8Bit(image, 16, 16)); break;
		case is32: put(type, encode32Bit(image, 16, 16, 0, true, false)); break;
		case s8mk: put(type, encode32Bit(image, 16, 16, 0, false, true)); break;
		case ICN$: put(type, encode1Bit(image, 32, 32, true, true)); break;
		case icl4: put(type, encode4Bit(image, 32, 32)); break;
		case icl8: put(type, encode8Bit(image, 32, 32)); break;
		case il32: put(type, encode32Bit(image, 32, 32, 0, true, false)); break;
		case l8mk: put(type, encode32Bit(image, 32, 32, 0, false, true)); break;
		case ich$: put(type, encode1Bit(image, 48, 48, true, true)); break;
		case ich4: put(type, encode4Bit(image, 48, 48)); break;
		case ich8: put(type, encode8Bit(image, 48, 48)); break;
		case ih32: put(type, encode32Bit(image, 48, 48, 0, true, false)); break;
		case h8mk: put(type, encode32Bit(image, 48, 48, 0, false, true)); break;
		case ict$: put(type, encode1Bit(image, 128, 128, true, true)); break;
		case ict4: put(type, encode4Bit(image, 128, 128)); break;
		case ict8: put(type, encode8Bit(image, 128, 128)); break;
		case it32: put(type, encode32Bit(image, 128, 128, 4, true, false)); break;
		case t8mk: put(type, encode32Bit(image, 128, 128, 0, false, true)); break;
		case ic04: put(type, encodeCompressed(image, 16, 16, "ARGB")); break;
		case ic05: put(type, encodeCompressed(image, 32, 32, "ARGB")); break;
		case ic06: put(type, encodeCompressed(image, 64, 64, "ARGB")); break;
		case icp4: put(type, encodeCompressed(image, 16, 16, "jpeg2000", "png")); break;
		case icp5: put(type, encodeCompressed(image, 32, 32, "jpeg2000", "png")); break;
		case icp6: put(type, encodeCompressed(image, 64, 64, "jpeg2000", "png")); break;
		case ic07: put(type, encodeCompressed(image, 128, 128, "jpeg2000", "png")); break;
		case ic08: put(type, encodeCompressed(image, 256, 256, "jpeg2000", "png")); break;
		case ic09: put(type, encodeCompressed(image, 512, 512, "jpeg2000", "png")); break;
		case ic10: put(type, encodeCompressed(image, 1024, 1024, "jpeg2000", "png")); break;
		case ic11: put(type, encodeCompressed(image, 32, 32, "jpeg2000", "png")); break;
		case ic12: put(type, encodeCompressed(image, 64, 64, "jpeg2000", "png")); break;
		case ic13: put(type, encodeCompressed(image, 256, 256, "jpeg2000", "png")); break;
		case ic14: put(type, encodeCompressed(image, 512, 512, "jpeg2000", "png")); break;
		}
	}
	
	public Integer getVersion() {
		byte[] d = get(icnV);
		if (d == null || d.length < 4) {
			return null;
		} else {
			int v = ((d[0] & 0xFF) << 24);
			v    |= ((d[1] & 0xFF) << 16);
			v    |= ((d[2] & 0xFF) <<  8);
			v    |= ((d[3] & 0xFF) <<  0);
			return v;
		}
	}
	
	public void putVersion(Integer v) {
		if (v == null) {
			remove(icnV);
		} else {
			byte[] d = new byte[4];
			d[0] = (byte)(v >> 24);
			d[1] = (byte)(v >> 16);
			d[2] = (byte)(v >>  8);
			d[3] = (byte)(v >>  0);
			put(icnV, d);
		}
	}
	
	private static final int[] COLORS_8BIT = {
		0xFFFFFFFF, 0xFFFFFFCC, 0xFFFFFF99, 0xFFFFFF66, 0xFFFFFF33, 0xFFFFFF00, 0xFFFFCCFF, 0xFFFFCCCC,
		0xFFFFCC99, 0xFFFFCC66, 0xFFFFCC33, 0xFFFFCC00, 0xFFFF99FF, 0xFFFF99CC, 0xFFFF9999, 0xFFFF9966,
		0xFFFF9933, 0xFFFF9900, 0xFFFF66FF, 0xFFFF66CC, 0xFFFF6699, 0xFFFF6666, 0xFFFF6633, 0xFFFF6600,
		0xFFFF33FF, 0xFFFF33CC, 0xFFFF3399, 0xFFFF3366, 0xFFFF3333, 0xFFFF3300, 0xFFFF00FF, 0xFFFF00CC,
		0xFFFF0099, 0xFFFF0066, 0xFFFF0033, 0xFFFF0000, 0xFFCCFFFF, 0xFFCCFFCC, 0xFFCCFF99, 0xFFCCFF66,
		0xFFCCFF33, 0xFFCCFF00, 0xFFCCCCFF, 0xFFCCCCCC, 0xFFCCCC99, 0xFFCCCC66, 0xFFCCCC33, 0xFFCCCC00,
		0xFFCC99FF, 0xFFCC99CC, 0xFFCC9999, 0xFFCC9966, 0xFFCC9933, 0xFFCC9900, 0xFFCC66FF, 0xFFCC66CC,
		0xFFCC6699, 0xFFCC6666, 0xFFCC6633, 0xFFCC6600, 0xFFCC33FF, 0xFFCC33CC, 0xFFCC3399, 0xFFCC3366,
		0xFFCC3333, 0xFFCC3300, 0xFFCC00FF, 0xFFCC00CC, 0xFFCC0099, 0xFFCC0066, 0xFFCC0033, 0xFFCC0000,
		0xFF99FFFF, 0xFF99FFCC, 0xFF99FF99, 0xFF99FF66, 0xFF99FF33, 0xFF99FF00, 0xFF99CCFF, 0xFF99CCCC,
		0xFF99CC99, 0xFF99CC66, 0xFF99CC33, 0xFF99CC00, 0xFF9999FF, 0xFF9999CC, 0xFF999999, 0xFF999966,
		0xFF999933, 0xFF999900, 0xFF9966FF, 0xFF9966CC, 0xFF996699, 0xFF996666, 0xFF996633, 0xFF996600,
		0xFF9933FF, 0xFF9933CC, 0xFF993399, 0xFF993366, 0xFF993333, 0xFF993300, 0xFF9900FF, 0xFF9900CC,
		0xFF990099, 0xFF990066, 0xFF990033, 0xFF990000, 0xFF66FFFF, 0xFF66FFCC, 0xFF66FF99, 0xFF66FF66,
		0xFF66FF33, 0xFF66FF00, 0xFF66CCFF, 0xFF66CCCC, 0xFF66CC99, 0xFF66CC66, 0xFF66CC33, 0xFF66CC00,
		0xFF6699FF, 0xFF6699CC, 0xFF669999, 0xFF669966, 0xFF669933, 0xFF669900, 0xFF6666FF, 0xFF6666CC,
		0xFF666699, 0xFF666666, 0xFF666633, 0xFF666600, 0xFF6633FF, 0xFF6633CC, 0xFF663399, 0xFF663366,
		0xFF663333, 0xFF663300, 0xFF6600FF, 0xFF6600CC, 0xFF660099, 0xFF660066, 0xFF660033, 0xFF660000,
		0xFF33FFFF, 0xFF33FFCC, 0xFF33FF99, 0xFF33FF66, 0xFF33FF33, 0xFF33FF00, 0xFF33CCFF, 0xFF33CCCC,
		0xFF33CC99, 0xFF33CC66, 0xFF33CC33, 0xFF33CC00, 0xFF3399FF, 0xFF3399CC, 0xFF339999, 0xFF339966,
		0xFF339933, 0xFF339900, 0xFF3366FF, 0xFF3366CC, 0xFF336699, 0xFF336666, 0xFF336633, 0xFF336600,
		0xFF3333FF, 0xFF3333CC, 0xFF333399, 0xFF333366, 0xFF333333, 0xFF333300, 0xFF3300FF, 0xFF3300CC,
		0xFF330099, 0xFF330066, 0xFF330033, 0xFF330000, 0xFF00FFFF, 0xFF00FFCC, 0xFF00FF99, 0xFF00FF66,
		0xFF00FF33, 0xFF00FF00, 0xFF00CCFF, 0xFF00CCCC, 0xFF00CC99, 0xFF00CC66, 0xFF00CC33, 0xFF00CC00,
		0xFF0099FF, 0xFF0099CC, 0xFF009999, 0xFF009966, 0xFF009933, 0xFF009900, 0xFF0066FF, 0xFF0066CC,
		0xFF006699, 0xFF006666, 0xFF006633, 0xFF006600, 0xFF0033FF, 0xFF0033CC, 0xFF003399, 0xFF003366,
		0xFF003333, 0xFF003300, 0xFF0000FF, 0xFF0000CC, 0xFF000099, 0xFF000066, 0xFF000033, 0xFFEE0000,
		0xFFDD0000, 0xFFBB0000, 0xFFAA0000, 0xFF880000, 0xFF770000, 0xFF550000, 0xFF440000, 0xFF220000,
		0xFF110000, 0xFF00EE00, 0xFF00DD00, 0xFF00BB00, 0xFF00AA00, 0xFF008800, 0xFF007700, 0xFF005500,
		0xFF004400, 0xFF002200, 0xFF001100, 0xFF0000EE, 0xFF0000DD, 0xFF0000BB, 0xFF0000AA, 0xFF000088,
		0xFF000077, 0xFF000055, 0xFF000044, 0xFF000022, 0xFF000011, 0xFFEEEEEE, 0xFFDDDDDD, 0xFFBBBBBB,
		0xFFAAAAAA, 0xFF888888, 0xFF777777, 0xFF555555, 0xFF444444, 0xFF222222, 0xFF111111, 0xFF000000
	};
	
	private static final int[] COLORS_4BIT = {
		0xFFFFFFFF, 0xFFFCF305, 0xFFFF6503, 0xFFDD0907, 0xFFF30885, 0xFF4700A5, 0xFF0000D4, 0xFF02ABEB,
		0xFF1FB814, 0xFF006512, 0xFF562D05, 0xFF91713A, 0xFFC0C0C0, 0xFF808080, 0xFF404040, 0xFF000000
	};
	
	private static int closestColorIndex(int[] colors, int color) {
		int r = (color >> 16) & 0xFF;
		int g = (color >>  8) & 0xFF;
		int b = (color >>  0) & 0xFF;
		int index = -1, delta = Integer.MAX_VALUE;
		for (int i = 0; i < colors.length; i++) {
			int dr = r - ((colors[i] >> 16) & 0xFF);
			int dg = g - ((colors[i] >>  8) & 0xFF);
			int db = b - ((colors[i] >>  0) & 0xFF);
			int d = dr * dr + dg * dg + db * db;
			if (d < delta) { index = i; delta = d; }
		}
		return index;
	}
	
	private static void unpack(byte[] packed, int offset, int length, OutputStream out) throws IOException {
		while (length > 0) {
			int n = packed[offset++]; length--;
			if (n >= 0) {
				for (int i = n + 1; i > 0; i--) {
					int b = packed[offset++]; length--;
					out.write(b);
				}
			} else {
				int b = packed[offset++]; length--;
				for (int i = n + 131; i > 0; i--) {
					out.write(b);
				}
			}
		}
	}
	
	private static void packUncompressed(byte[] unpacked, OutputStream out) throws IOException {
		packUncompressed(unpacked, 0, unpacked.length, out);
	}
	
	private static void packUncompressed(byte[] unpacked, int offset, int length, OutputStream out) throws IOException {
		while (length >= 128) {
			out.write(127);
			out.write(unpacked, offset, 128);
			offset += 128; length -= 128;
		}
		if (length > 0) {
			out.write(length - 1);
			out.write(unpacked, offset, length);
		}
	}
	
	private static void pack(byte[] unpacked, OutputStream out) throws IOException {
		pack(unpacked, 0, unpacked.length, out);
	}
	
	private static void pack(byte[] unpacked, int offset, int length, OutputStream out) throws IOException {
		ByteArrayOutputStream run = new ByteArrayOutputStream();
		while (length > 0) {
			int b = unpacked[offset++]; length--;
			int c = 1;
			while (length > 0 && unpacked[offset] == b) {
				offset++; length--; c++;
				if (c >= 130) break;
			}
			if (c < 3) {
				while (c > 0) {
					run.write(b);
					c--;
				}
			} else {
				packUncompressed(run.toByteArray(), out);
				out.write(c + 125);
				out.write(b);
				run = new ByteArrayOutputStream();
			}
		}
		packUncompressed(run.toByteArray(), out);
	}
	
	private static boolean dataStartsWith(byte[] data, int offset, int... prefix) {
		for (int b : prefix) {
			if (offset >= data.length) return false;
			if (data[offset] != (byte)b) return false;
			offset++;
		}
		return true;
	}
	
	private static BufferedImage resizeImage(BufferedImage image, int width, int height) {
		if (image.getWidth() == width && image.getHeight() == height) return image;
		BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = resized.createGraphics();
		g.drawImage(image, 0, 0, width, height, null);
		g.dispose();
		return resized;
	}
	
	private static BufferedImage decode1Bit(int w, int h, byte[] data, int di, byte[] mask, int mi) {
		int[] pixels = new int[w * h];
		if (data != null) {
			for (int pi = 0, y = 0; y < h; y++) {
				for (int x = 0; x < w; x += 8) {
					int b = data[di++];
					for (int m = 0x80; m != 0; m >>= 1, pi++) {
						if ((b & m) == 0) pixels[pi] = 0x00FFFFFF;
						else              pixels[pi] = 0xFF000000;
					}
				}
			}
		}
		if (mask != null) {
			for (int pi = 0, y = 0; y < h; y++) {
				for (int x = 0; x < w; x += 8) {
					int b = mask[mi++];
					for (int m = 0x80; m != 0; m >>= 1, pi++) {
						if ((b & m) == 0) pixels[pi] &= 0x00FFFFFF;
						else              pixels[pi] |= 0xFF000000;
					}
				}
			}
		}
		BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		img.setRGB(0, 0, w, h, pixels, 0, w);
		return img;
	}
	
	private static BufferedImage decode4Bit(int w, int h, byte[] data, int di, byte[] mask, int mi) {
		int[] pixels = new int[w * h];
		if (data != null) {
			for (int pi = 0, y = 0; y < h; y++) {
				for (int x = 0; x < w; x += 2) {
					int b = data[di++] & 0xFF;
					pixels[pi++] = COLORS_4BIT[b >> 4];
					pixels[pi++] = COLORS_4BIT[b & 15];
				}
			}
		}
		if (mask != null) {
			for (int pi = 0, y = 0; y < h; y++) {
				for (int x = 0; x < w; x += 8) {
					int b = mask[mi++];
					for (int m = 0x80; m != 0; m >>= 1, pi++) {
						if ((b & m) == 0) pixels[pi] &= 0x00FFFFFF;
						else              pixels[pi] |= 0xFF000000;
					}
				}
			}
		}
		BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		img.setRGB(0, 0, w, h, pixels, 0, w);
		return img;
	}
	
	private static BufferedImage decode8Bit(int w, int h, byte[] data, int di, byte[] mask, int mi) {
		int[] pixels = new int[w * h];
		if (data != null) {
			for (int pi = 0, y = 0; y < h; y++) {
				for (int x = 0; x < w; x++) {
					int b = data[di++] & 0xFF;
					pixels[pi++] = COLORS_8BIT[b];
				}
			}
		}
		if (mask != null) {
			for (int pi = 0, y = 0; y < h; y++) {
				for (int x = 0; x < w; x += 8) {
					int b = mask[mi++];
					for (int m = 0x80; m != 0; m >>= 1, pi++) {
						if ((b & m) == 0) pixels[pi] &= 0x00FFFFFF;
						else              pixels[pi] |= 0xFF000000;
					}
				}
			}
		}
		BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		img.setRGB(0, 0, w, h, pixels, 0, w);
		return img;
	}
	
	private static BufferedImage decode32Bit(int w, int h, byte[] data, int di, byte[] mask, int mi) {
		int[] pixels = new int[w * h];
		if (data != null) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			try { unpack(data, di, data.length - di, out); }
			catch (IOException e) { e.printStackTrace(); }
			data = out.toByteArray(); di = 0;
			for (int pi = 0, y = 0; y < h; y++) {
				for (int x = 0; x < w; x++, pi++) {
					int b = data[di++] & 0xFF;
					pixels[pi] = 0xFF000000 | (b << 16);
				}
			}
			for (int pi = 0, y = 0; y < h; y++) {
				for (int x = 0; x < w; x++, pi++) {
					int b = data[di++] & 0xFF;
					pixels[pi] |= (b << 8);
				}
			}
			for (int pi = 0, y = 0; y < h; y++) {
				for (int x = 0; x < w; x++, pi++) {
					int b = data[di++] & 0xFF;
					pixels[pi] |= b;
				}
			}
		}
		if (mask != null) {
			for (int pi = 0, y = 0; y < h; y++) {
				for (int x = 0; x < w; x++, pi++) {
					int b = mask[mi++] & 0xFF;
					pixels[pi] &= 0x00FFFFFF;
					pixels[pi] |= (b << 24);
				}
			}
		}
		BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		img.setRGB(0, 0, w, h, pixels, 0, w);
		return img;
	}
	
	private static BufferedImage decodeCompressed(int w, int h, byte[] data, int di) {
		if (dataStartsWith(data, di, 'A', 'R', 'G', 'B')) {
			int[] pixels = new int[w * h];
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			try { unpack(data, di + 4, data.length - (di + 4), out); }
			catch (IOException e) { e.printStackTrace(); }
			data = out.toByteArray(); di = 0;
			for (int pi = 0, y = 0; y < h; y++) {
				for (int x = 0; x < w; x++, pi++) {
					int b = data[di++] & 0xFF;
					pixels[pi] = (b << 24);
				}
			}
			for (int pi = 0, y = 0; y < h; y++) {
				for (int x = 0; x < w; x++, pi++) {
					int b = data[di++] & 0xFF;
					pixels[pi] |= (b << 16);
				}
			}
			for (int pi = 0, y = 0; y < h; y++) {
				for (int x = 0; x < w; x++, pi++) {
					int b = data[di++] & 0xFF;
					pixels[pi] |= (b << 8);
				}
			}
			for (int pi = 0, y = 0; y < h; y++) {
				for (int x = 0; x < w; x++, pi++) {
					int b = data[di++] & 0xFF;
					pixels[pi] |= b;
				}
			}
			BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			img.setRGB(0, 0, w, h, pixels, 0, w);
			return img;
		}
		try {
			return ImageIO.read(new ByteArrayInputStream(data, di, data.length - di));
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return null;
	}
	
	private static byte[] encode1Bit(BufferedImage img, int w, int h, boolean data, boolean mask) {
		img = resizeImage(img, w, h);
		int[] pixels = new int[w * h];
		img.getRGB(0, 0, w, h, pixels, 0, w);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		if (data) {
			for (int pi = 0, y = 0; y < h; y++) {
				for (int x = 0; x < w; x += 8) {
					int b = 0;
					for (int m = 0x80; m != 0; m >>= 1, pi++) {
						int pa = (pixels[pi] >> 24) & 0xFF;
						int pr = (pixels[pi] >> 16) & 0xFF;
						int pg = (pixels[pi] >>  8) & 0xFF;
						int pb = (pixels[pi] >>  0) & 0xFF;
						int pk = pr * 30 + pg * 59 + pb * 11;
						if (pa >= 128 && pk < 12750) b |= m;
					}
					out.write(b);
				}
			}
		}
		if (mask) {
			for (int pi = 0, y = 0; y < h; y++) {
				for (int x = 0; x < w; x += 8) {
					int b = 0;
					for (int m = 0x80; m != 0; m >>= 1, pi++) {
						int pa = (pixels[pi] >> 24) & 0xFF;
						if (pa >= 128) b |= m;
					}
					out.write(b);
				}
			}
		}
		return out.toByteArray();
	}
	
	private static byte[] encode4Bit(BufferedImage img, int w, int h) {
		img = resizeImage(img, w, h);
		int[] pixels = new int[w * h];
		img.getRGB(0, 0, w, h, pixels, 0, w);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		for (int pi = 0, y = 0; y < h; y++) {
			for (int x = 0; x < w; x += 2) {
				int b = 0;
				if (pixels[pi] < 0) b |= closestColorIndex(COLORS_4BIT, pixels[pi]) << 4; pi++;
				if (pixels[pi] < 0) b |= closestColorIndex(COLORS_4BIT, pixels[pi]) << 0; pi++;
				out.write(b);
			}
		}
		return out.toByteArray();
	}
	
	private static byte[] encode8Bit(BufferedImage img, int w, int h) {
		img = resizeImage(img, w, h);
		int[] pixels = new int[w * h];
		img.getRGB(0, 0, w, h, pixels, 0, w);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		for (int pi = 0, y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				int b = 0;
				if (pixels[pi] < 0) b = closestColorIndex(COLORS_8BIT, pixels[pi]); pi++;
				out.write(b);
			}
		}
		return out.toByteArray();
	}
	
	private static byte[] encode32Bit(BufferedImage img, int w, int h, int z, boolean data, boolean mask) {
		img = resizeImage(img, w, h);
		int[] pixels = new int[w * h];
		img.getRGB(0, 0, w, h, pixels, 0, w);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		while (z > 0) {
			out.write(0);
			z--;
		}
		if (data) {
			byte[] unpacked = new byte[w * h];
			for (int s = 16; s >= 0; s -= 8) {
				for (int pi = 0, y = 0; y < h; y++) {
					for (int x = 0; x < w; x++, pi++) {
						unpacked[pi] = (byte)(pixels[pi] >> s);
					}
				}
				try { pack(unpacked, out); }
				catch (IOException e) { e.printStackTrace(); }
			}
		}
		if (mask) {
			for (int pi = 0, y = 0; y < h; y++) {
				for (int x = 0; x < w; x++, pi++) {
					out.write(pixels[pi] >> 24);
				}
			}
		}
		return out.toByteArray();
	}
	
	private static byte[] encodeCompressed(BufferedImage img, int w, int h, String... formats) {
		img = resizeImage(img, w, h);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		for (String format : formats) {
			if (format.equalsIgnoreCase("ARGB")) {
				int[] pixels = new int[w * h];
				img.getRGB(0, 0, w, h, pixels, 0, w);
				out.write('A');
				out.write('R');
				out.write('G');
				out.write('B');
				byte[] unpacked = new byte[w * h];
				for (int s = 24; s >= 0; s -= 8) {
					for (int pi = 0, y = 0; y < h; y++) {
						for (int x = 0; x < w; x++, pi++) {
							unpacked[pi] = (byte)(pixels[pi] >> s);
						}
					}
					try { pack(unpacked, out); }
					catch (IOException e) { e.printStackTrace(); }
				}
				break;
			} else {
				try { if (ImageIO.write(img, format, out)) break; }
				catch (IOException e) { e.printStackTrace(); }
			}
		}
		return out.toByteArray();
	}
}
