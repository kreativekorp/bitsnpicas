package com.kreative.bitsnpicas.importer;

import java.io.*;
import java.util.*;
import com.kreative.bitsnpicas.BitmapFont;
import com.kreative.bitsnpicas.BitmapFontGlyph;
import com.kreative.bitsnpicas.BitmapFontImporter;

public class SFDBitmapFontImporter implements BitmapFontImporter {
	public BitmapFont[] importFont(byte[] data) throws IOException {
		return importFont(new Scanner(new ByteArrayInputStream(data), "UTF-8"));
	}

	public BitmapFont[] importFont(InputStream is) throws IOException {
		return importFont(new Scanner(is, "UTF-8"));
	}

	public BitmapFont[] importFont(File file) throws IOException {
		return importFont(new Scanner(new FileInputStream(file), "UTF-8"));
	}
	
	public BitmapFont[] importFont(Scanner scan) throws IOException {
		Map<Integer,String> names = new HashMap<Integer,String>();
		String currentGlyphName = null;
		Map<Integer,String> glyphNames = new HashMap<Integer,String>();
		Map<Integer,Integer> codePoints = new HashMap<Integer,Integer>();
		Vector<BitmapFont> fonts = new Vector<BitmapFont>();
		BitmapFont bm = null;
		String bitmapBuffer = null;
		int ch=0, cw=0, bbx=0, bbw=0, bby=0, bbh=0, bbs=0;
		while (scan.hasNextLine()) {
			String l = scan.nextLine();
			if (bm == null) {
				if (l.startsWith("Copyright: ")) {
					names.put(BitmapFont.NAME_COPYRIGHT, decodeEscapes(l.split(":\\s*", 2)[1]));
				} else if (l.startsWith("FamilyName: ")) {
					names.put(BitmapFont.NAME_FAMILY, decodeEscapes(l.split(":\\s*", 2)[1]));
				} else if (l.startsWith("Weight: ")) {
					names.put(BitmapFont.NAME_STYLE, decodeEscapes(l.split(":\\s*", 2)[1]));
				} else if (l.startsWith("StartChar: ")) {
					currentGlyphName = decodeEscapes(l.split(":\\s*", 2)[1]);
				} else if (l.startsWith("Encoding: ")) {
					if (currentGlyphName != null) {
						l = l.split(":\\s*", 2)[1];
						String[] nums = l.split("\\s");
						int local = Integer.parseInt(nums[0]);
						int unicode = Integer.parseInt(nums[1]);
						if (unicode >= 0) codePoints.put(local, unicode);
						else glyphNames.put(local, currentGlyphName);
					}
				} else if (l.equals("EndChar")) {
					currentGlyphName = null;
				} else if (l.startsWith("BitmapFont: ")) {
					l = l.split(":\\s*", 2)[1];
					String[] nums = l.split("\\s");
					int a = Integer.parseInt(nums[2]);
					int d = Integer.parseInt(nums[3]);
					bm = new BitmapFont(a, d, a, d, 0, 0, 0);
					for (Map.Entry<Integer,String> e : names.entrySet()) {
						bm.setName(e.getKey(), e.getValue());
					}
				}
			} else {
				if (l.equals("EndBitmapFont")) {
					if (bitmapBuffer != null) {
						byte[] data;
						try {
							data = decodeASCII85(bitmapBuffer.getBytes("US-ASCII"));
						} catch (UnsupportedEncodingException e) {
							data = decodeASCII85(bitmapBuffer.getBytes());
						}
						data = unpack(data);
						byte[][] glyph = new byte[bbh][bbw];
						for (int k = 0, j = 0; k < data.length && j < bbh; k += bbs, j++) {
							for (int i = 0; i < bbw && i < bbs && k+i < data.length; i++) {
								glyph[j][i] = ((data[k+i] != 0) ? (byte)0xFF : 0);
							}
						}
						BitmapFontGlyph bc = new BitmapFontGlyph(glyph, bbx, cw, bby+bbh);
						if (codePoints.containsKey(ch)) bm.putCharacter(codePoints.get(ch), bc);
						if (glyphNames.containsKey(ch)) bm.putNamedGlyph(glyphNames.get(ch), bc);
					}
					bm.setXHeight();
					bm.setCapHeight();
					fonts.add(bm);
					bm = null;
				} else if (l.startsWith("Resolution: ")) {
					// ignore
				} else if (l.startsWith("BDFChar: ")) {
					if (bitmapBuffer != null) {
						byte[] data;
						try {
							data = decodeASCII85(bitmapBuffer.getBytes("US-ASCII"));
						} catch (UnsupportedEncodingException e) {
							data = decodeASCII85(bitmapBuffer.getBytes());
						}
						data = unpack(data);
						byte[][] glyph = new byte[bbh][bbw];
						for (int k = 0, j = 0; k < data.length && j < bbh; k += bbs, j++) {
							for (int i = 0; i < bbw && i < bbs && k+i < data.length; i++) {
								glyph[j][i] = ((data[k+i] != 0) ? (byte)0xFF : 0);
							}
						}
						BitmapFontGlyph bc = new BitmapFontGlyph(glyph, bbx, cw, bby+bbh);
						if (codePoints.containsKey(ch)) bm.putCharacter(codePoints.get(ch), bc);
						if (glyphNames.containsKey(ch)) bm.putNamedGlyph(glyphNames.get(ch), bc);
					}
					l = l.split(":\\s*", 2)[1];
					String[] nums = l.split("\\s");
					ch = Integer.parseInt(nums[1]);
					cw = Integer.parseInt(nums[2]);
					bbx = Integer.parseInt(nums[3]);
					bbw = Integer.parseInt(nums[4])-bbx+1;
					bby = Integer.parseInt(nums[5]);
					bbh = Integer.parseInt(nums[6])-bby+1;
					bbs = ((bbw & 0x7) == 0) ? bbw : ((bbw | 7)+1);
					bitmapBuffer = null;
				} else {
					if (bitmapBuffer == null) bitmapBuffer = l;
					else bitmapBuffer += l;
				}
			}
		}
		if (bm != null) {
			if (bitmapBuffer != null) {
				byte[] data;
				try {
					data = decodeASCII85(bitmapBuffer.getBytes("US-ASCII"));
				} catch (UnsupportedEncodingException e) {
					data = decodeASCII85(bitmapBuffer.getBytes());
				}
				data = unpack(data);
				byte[][] glyph = new byte[bbh][bbw];
				for (int k = 0, j = 0; k < data.length && j < bbh; k += bbs, j++) {
					for (int i = 0; i < bbw && i < bbs && k+i < data.length; i++) {
						glyph[j][i] = ((data[k+i] != 0) ? (byte)0xFF : 0);
					}
				}
				BitmapFontGlyph bc = new BitmapFontGlyph(glyph, bbx, cw, bby+bbh);
				if (codePoints.containsKey(ch)) bm.putCharacter(codePoints.get(ch), bc);
				if (glyphNames.containsKey(ch)) bm.putNamedGlyph(glyphNames.get(ch), bc);
			}
			bm.setXHeight();
			bm.setCapHeight();
			fonts.add(bm);
			bm = null;
		}
		return fonts.toArray(new BitmapFont[0]);
	}
	
	private static String decodeEscapes(String s) {
		if (!s.contains("\\")) return s;
		char[] och = s.toCharArray();
		char[] nch = new char[och.length];
		int oi = 0;
		int ni = 0;
		while (oi < och.length) {
			if (och[oi] == '\\') {
				oi++;
				if (oi < och.length) switch(och[oi]) {
				case 'a': nch[ni++] = '\u0007'; break;
				case 'b': nch[ni++] = '\u0008'; break;
				case 'd': nch[ni++] = '\u007F'; break;
				case 'e': nch[ni++] = '\u001B'; break;
				case 'f': nch[ni++] = '\u000C'; break;
				case 'i': nch[ni++] = '\u000F'; break;
				case 'n': nch[ni++] = '\n'; break;
				case 'o': nch[ni++] = '\u000E'; break;
				case 'r': nch[ni++] = '\r'; break;
				case 't': nch[ni++] = '\u0009'; break;
				case 'u': {
					int h1 = och[++oi]; h1 = ((h1>='a')?(10+h1-'a'):(h1>='A')?(10+h1-'A'):(h1>='0')?(h1-'0'):0);
					int h2 = och[++oi]; h2 = ((h2>='a')?(10+h2-'a'):(h2>='A')?(10+h2-'A'):(h2>='0')?(h2-'0'):0);
					int h3 = och[++oi]; h3 = ((h3>='a')?(10+h3-'a'):(h3>='A')?(10+h3-'A'):(h3>='0')?(h3-'0'):0);
					int h4 = och[++oi]; h4 = ((h4>='a')?(10+h4-'a'):(h4>='A')?(10+h4-'A'):(h4>='0')?(h4-'0'):0);
					nch[ni++] = (char)((h1 << 12) | (h2 << 8) | (h3 << 4) | h4);
				} break;
				case 'v': nch[ni++] = '\u000B'; break;
				case 'x': {
					int h1 = och[++oi]; h1 = ((h1>='a')?(10+h1-'a'):(h1>='A')?(10+h1-'A'):(h1>='0')?(h1-'0'):0);
					int h2 = och[++oi]; h2 = ((h2>='a')?(10+h2-'a'):(h2>='A')?(10+h2-'A'):(h2>='0')?(h2-'0'):0);
					nch[ni++] = (char)((h1 << 4) | h2);
				} break;
				case ' ': nch[ni++] = ' '; break;
				case '\"': nch[ni++] = '\"'; break;
				case '\'': nch[ni++] = '\''; break;
				case '\\': nch[ni++] = '\\'; break;
				default: nch[ni++] = och[oi]; break;
				}
				oi++;
			} else {
				nch[ni++] = och[oi++];
			}
		}
		return new String(nch, 0, ni);
	}
	
	private static byte[] decodeASCII85(byte[] b) {
		try {
			byte[] r = new byte[b.length*4];
			int rp = 0;
			int bp = 0;
			while (b[bp]<33 || b[bp]>126) bp++;
			if (b[bp]=='<' && b[bp+1]=='~') bp+=2;
			int d=0; int n=0;
			for (; bp<b.length; bp++) {
				if (b[bp] == '~') break;
				else if (b[bp] == 'z') {
					r[rp++]=0; r[rp++]=0; r[rp++]=0; r[rp++]=0;
				}
				else if (b[bp]>32 && b[bp]<127){
					d *= 85;
					d += b[bp]-'!';
					n++;
					if (n==5) {
						r[rp++] = (byte)(d >>> 24);
						r[rp++] = (byte)(d >>> 16);
						r[rp++] = (byte)(d >>>  8);
						r[rp++] = (byte)(d >>>  0);
						d=0; n=0;
					}
				}
			}
			if (n>0) {
				d*=85; d+=85;
				for (int m=n+1; m<5; m++) d*=85;
				if (n>1) r[rp++] = (byte)(d >>> 24);
				if (n>2) r[rp++] = (byte)(d >>> 16);
				if (n>3) r[rp++] = (byte)(d >>>  8);
				if (n>4) r[rp++] = (byte)(d >>>  0);
			}
			byte[] rr = new byte[rp];
			while((rp--)>0) rr[rp]=r[rp];
			return rr;
		} catch (ArrayIndexOutOfBoundsException e) {
			return b;
		}
	}
	
	private static byte[] unpack(byte[] data) {
		byte[] nd = new byte[data.length*8];
		for (int s=0, d=0; s<data.length && d < nd.length; s++, d+=8) {
			nd[d+0] = (byte)((data[s] >> 7) & 1);
			nd[d+1] = (byte)((data[s] >> 6) & 1);
			nd[d+2] = (byte)((data[s] >> 5) & 1);
			nd[d+3] = (byte)((data[s] >> 4) & 1);
			nd[d+4] = (byte)((data[s] >> 3) & 1);
			nd[d+5] = (byte)((data[s] >> 2) & 1);
			nd[d+6] = (byte)((data[s] >> 1) & 1);
			nd[d+7] = (byte)((data[s] >> 0) & 1);
		}
		return nd;
	}
}
