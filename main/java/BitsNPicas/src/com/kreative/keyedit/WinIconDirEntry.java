package com.kreative.keyedit;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

public class WinIconDirEntry {
	// IconDir Header
	private final boolean isCursor;
	// IconDirEntry Header
	private int icoWidth;
	private int icoHeight;
	private int icoColors;
	private int icoReserved;
	private int icoPlanes;
	private int icoBpp;
	private int dataLength;
	private int dataOffset;
	// IconDirEntry Data
	private byte[] data;
	private boolean isImageIO;
	private BufferedImage image;
	// BitmapInfoHeader
	private int bmpHeaderLength;
	private int bmpWidth;
	private int bmpHeight;
	private int bmpPlanes;
	private int bmpBpp;
	// private int bmpCompression;
	// private int bmpImageLength;
	// private int bmpXPPM;
	// private int bmpYPPM;
	private int bmpColors;
	// private int bmpImportantColors;
	
	public WinIconDirEntry() {
		this.isCursor = false;
	}
	
	public WinIconDirEntry(boolean isCursor) {
		this.isCursor = isCursor;
	}
	
	void readHeader(DataInputStream in) throws IOException {
		icoWidth = in.readUnsignedByte();
		icoHeight = in.readUnsignedByte();
		icoColors = in.readUnsignedByte();
		icoReserved = in.readUnsignedByte();
		icoPlanes = Short.reverseBytes(in.readShort());
		icoBpp = Short.reverseBytes(in.readShort());
		dataLength = Integer.reverseBytes(in.readInt());
		dataOffset = Integer.reverseBytes(in.readInt());
	}
	
	void readData(DataInputStream in) throws IOException {
		in.reset();
		in.skipBytes(dataOffset);
		in.readFully((data = new byte[dataLength]));
		isImageIO = dataStartsWith(data, 0, 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A);
		if (isImageIO) {
			try {
				image = ImageIO.read(new ByteArrayInputStream(data));
				bmpHeaderLength = 0;
				bmpWidth = image.getWidth();
				bmpHeight = image.getHeight() * 2;
				bmpPlanes = 0;
				bmpBpp = 0;
				// bmpCompression = 0;
				// bmpImageLength = 0;
				// bmpXPPM = 0;
				// bmpYPPM = 0;
				bmpColors = 0;
				// bmpImportantColors = 0;
			} catch (Exception e) {
				throw new IOException("error reading image: " + e, e);
			}
		} else {
			try {
				image = null;
				bmpHeaderLength = getInt32LE(data, 0);
				bmpWidth = getInt32LE(data, 4);
				bmpHeight = getInt32LE(data, 8);
				bmpPlanes = getInt16LE(data, 12);
				bmpBpp = getInt16LE(data, 14);
				// bmpCompression = getInt32LE(data, 16);
				// bmpImageLength = getInt32LE(data, 20);
				// bmpXPPM = getInt32LE(data, 24);
				// bmpYPPM = getInt32LE(data, 28);
				bmpColors = getInt32LE(data, 32);
				// bmpImportantColors = getInt32LE(data, 36);
			} catch (Exception e) {
				throw new IOException("error reading bitmap header: " + e, e);
			}
		}
	}
	
	void setDataOffset(int dataOffset) {
		this.dataOffset = dataOffset;
	}
	
	void writeHeader(DataOutputStream out) throws IOException {
		out.writeByte(icoWidth);
		out.writeByte(icoHeight);
		out.writeByte(icoColors);
		out.writeByte(icoReserved);
		out.writeShort(Short.reverseBytes((short)icoPlanes));
		out.writeShort(Short.reverseBytes((short)icoBpp));
		out.writeInt(Integer.reverseBytes(dataLength));
		out.writeInt(Integer.reverseBytes(dataOffset));
	}
	
	public boolean isCursor() {
		return isCursor;
	}
	
	public int getWidth() {
		if (icoWidth > 0) return icoWidth;
		if (bmpWidth > 0) return bmpWidth;
		return 0;
	}
	
	public int getHeight() {
		if (icoHeight > 0) return icoHeight;
		if (bmpHeight > 0) return bmpHeight / 2;
		return 0;
	}
	
	public int getPlanes() {
		if (icoPlanes > 0 && !isCursor) return icoPlanes;
		if (bmpPlanes > 0) return bmpPlanes;
		return 0;
	}
	
	public int getBitsPerPixel() {
		if (icoBpp > 0 && !isCursor) return icoBpp;
		if (bmpBpp > 0) return bmpBpp;
		return 0;
	}
	
	public int getColorCount() {
		if (icoColors > 0) return icoColors;
		if (icoBpp > 0 && !isCursor) {
			if (icoBpp < 1 || icoBpp > 8) return 0;
			return (1 << icoBpp);
		}
		if (bmpColors > 0) return bmpColors;
		if (bmpBpp < 1 || bmpBpp > 8) return 0;
		return (1 << bmpBpp);
	}
	
	public int[] getColorTable() {
		return getColorTable(data, bmpHeaderLength, getColorCount());
	}
	
	public boolean hasAlphaChannel() {
		if (isImageIO) return true;
		int bpp = getBitsPerPixel();
		return (bpp == 16 || bpp == 32);
	}
	
	public int getDataLength() {
		return dataLength;
	}
	
	public byte[] getData() {
		return data;
	}
	
	public boolean isPNG() {
		return isImageIO;
	}
	
	public BufferedImage getImage() {
		if (image != null) return image;
		
		int width = getWidth();
		int height = getHeight();
		int bpp = getBitsPerPixel();
		int colors = getColorCount();
		boolean mask = !hasAlphaChannel();
		
		int[] pixels = new int[width * height];
		decodeImage(data, bmpHeaderLength, pixels, width, height, bpp, colors, mask);
		
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		image.setRGB(0, 0, width, height, pixels, 0, width);
		return image;
	}
	
	public void setBMPImage(BufferedImage image, int bpp, int[] colorTable) {
		int width = image.getWidth();
		int height = image.getHeight();
		
		int[] pixels = new int[width * height];
		image.getRGB(0, 0, width, height, pixels, 0, width);
		
		byte[] data = new byte[dataLength(40, width, height, bpp, colorTable.length, true)];
		encodeImage(data, 40, pixels, width, height, bpp, colorTable, true);
		
		// IconDirEntry Header
		icoWidth = (width >= 256) ? 0 : width;
		icoHeight = (height >= 256) ? 0 : height;
		icoColors = (colorTable.length >= 256) ? 0 : colorTable.length;
		icoReserved = 0;
		if (!isCursor) {
			icoPlanes = 1;
			icoBpp = bpp;
		}
		dataLength = data.length;
		dataOffset = 0;
		
		// IconDirEntry Data
		this.data = data;
		this.isImageIO = false;
		this.image = null;
		
		// BitmapInfoHeader
		putInt32LE(data, 0, (bmpHeaderLength = 40));
		putInt32LE(data, 4, (bmpWidth = width));
		putInt32LE(data, 8, (bmpHeight = height * 2));
		putInt16LE(data, 12, (bmpPlanes = 1));
		putInt16LE(data, 14, (bmpBpp = bpp));
		// putInt32LE(data, 16, (bmpCompression = 0));
		// putInt32LE(data, 20, (bmpImageLength = 0));
		// putInt32LE(data, 20, data.length - colorTable.length * 4 - 40);
		// putInt32LE(data, 24, (bmpXPPM = 0));
		// putInt32LE(data, 28, (bmpYPPM = 0));
		putInt32LE(data, 32, (bmpColors = colorTable.length));
		// putInt32LE(data, 36, (bmpImportantColors = 0));
	}
	
	public void setPNGImage(BufferedImage image) throws IOException {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ImageIO.write(image, "png", out);
			out.flush(); out.close();
			byte[] data = out.toByteArray();
			image = ImageIO.read(new ByteArrayInputStream(data));
			int width = image.getWidth();
			int height = image.getHeight();
			
			// IconDirEntry Header
			icoWidth = (width >= 256) ? 0 : width;
			icoHeight = (height >= 256) ? 0 : height;
			icoColors = 0;
			icoReserved = 0;
			if (!isCursor) {
				icoPlanes = 1;
				icoBpp = 32;
			}
			dataLength = data.length;
			dataOffset = 0;
			
			// IconDirEntry Data
			this.data = data;
			this.isImageIO = true;
			this.image = image;
			
			// BitmapInfoHeader
			bmpHeaderLength = 0;
			bmpWidth = width;
			bmpHeight = height * 2;
			bmpPlanes = 0;
			bmpBpp = 0;
			// bmpCompression = 0;
			// bmpImageLength = 0;
			// bmpXPPM = 0;
			// bmpYPPM = 0;
			bmpColors = 0;
			// bmpImportantColors = 0;
		} catch (Exception e) {
			throw new IOException("error writing image: " + e, e);
		}
	}
	
	public Point getHotspot() {
		return isCursor ? new Point(icoPlanes, icoBpp) : null;
	}
	
	public void setHotspot(Point p) {
		if (isCursor) {
			icoPlanes = p.x;
			icoBpp = p.y;
		}
	}
	
	public void setHotspot(int x, int y) {
		if (isCursor) {
			icoPlanes = x;
			icoBpp = y;
		}
	}
	
	private static boolean dataStartsWith(byte[] data, int offset, int... prefix) {
		for (int b : prefix) {
			if (offset >= data.length) return false;
			if (data[offset] != (byte)b) return false;
			offset++;
		}
		return true;
	}
	
	private static int getInt32LE(byte[] data, int offset) {
		int v = (data[offset++] & 0xFF);
		v |= ((data[offset++] & 0xFF) <<  8);
		v |= ((data[offset++] & 0xFF) << 16);
		v |= ((data[offset++] & 0xFF) << 24);
		return v;
	}
	
	private static int getInt24LE(byte[] data, int offset) {
		int v = (data[offset++] & 0xFF);
		v |= ((data[offset++] & 0xFF) <<  8);
		v |= ((data[offset++] & 0xFF) << 16);
		return v;
	}
	
	private static int getInt16LE(byte[] data, int offset) {
		int v = (data[offset++] & 0xFF);
		v |= ((data[offset++] & 0xFF) <<  8);
		return v;
	}
	
	private static void putInt32LE(byte[] data, int offset, int v) {
		data[offset++] = (byte)(v >>  0);
		data[offset++] = (byte)(v >>  8);
		data[offset++] = (byte)(v >> 16);
		data[offset++] = (byte)(v >> 24);
	}
	
	private static void putInt24LE(byte[] data, int offset, int v) {
		data[offset++] = (byte)(v >>  0);
		data[offset++] = (byte)(v >>  8);
		data[offset++] = (byte)(v >> 16);
	}
	
	private static void putInt16LE(byte[] data, int offset, int v) {
		data[offset++] = (byte)(v >>  0);
		data[offset++] = (byte)(v >>  8);
	}
	
	private static int[] getColorTable(byte[] data, int dp, int colors) {
		int[] colorTable = new int[colors];
		for (int i = 0; i < colors; i++) {
			colorTable[i] = getInt32LE(data, dp) | 0xFF000000;
			dp += 4;
		}
		return colorTable;
	}
	
	private static void decodeImage(byte[] data, int dp, int[] pixels, int width, int height, int bpp, int colors, boolean mask) {
		int[] colorTable = new int[colors];
		for (int i = 0; i < colors; i++) {
			colorTable[i] = getInt32LE(data, dp) | 0xFF000000;
			dp += 4;
		}
		
		int rowWidth = (((width * bpp) + 31) / 32) * 4;
		for (int pp = pixels.length, y = 0; y < height; y++) {
			pp -= width;
			decodeRow(data, dp, pixels, pp, width, bpp, colorTable);
			dp += rowWidth;
		}
		
		if (mask) {
			int maskRowWidth = ((width + 31) / 32) * 4;
			for (int pp = pixels.length, y = 0; y < height; y++) {
				pp -= width;
				decodeMaskRow(data, dp, pixels, pp, width);
				dp += maskRowWidth;
			}
		}
	}
	
	private static void encodeImage(byte[] data, int dp, int[] pixels, int width, int height, int bpp, int[] colorTable, boolean mask) {
		int adaptiveCount = 0;
		for (int color : colorTable) if (color == 0) adaptiveCount++;
		if (adaptiveCount > 0) {
			int[] adaptivePixels = new int[pixels.length];
			for (int i = 0; i < pixels.length; i++) adaptivePixels[i] = pixels[i];
			int[] adaptiveColors = ColorReducer.reduce(adaptivePixels, adaptiveCount);
			int adaptiveIndex = 0;
			int[] newColorTable = new int[colorTable.length];
			for (int i = 0; i < colorTable.length; i++) {
				if (colorTable[i] != 0) {
					newColorTable[i] = colorTable[i];
				} else if (adaptiveIndex < adaptiveColors.length) {
					newColorTable[i] = adaptiveColors[adaptiveIndex++];
				} else {
					newColorTable[i] = -1;
				}
			}
			colorTable = newColorTable;
		}
		
		for (int color : colorTable) {
			putInt32LE(data, dp, (color & 0xFFFFFF));
			dp += 4;
		}
		
		int rowWidth = (((width * bpp) + 31) / 32) * 4;
		for (int pp = pixels.length, y = 0; y < height; y++) {
			pp -= width;
			encodeRow(data, dp, pixels, pp, width, bpp, colorTable);
			dp += rowWidth;
		}
		
		if (mask) {
			int maskRowWidth = ((width + 31) / 32) * 4;
			for (int pp = pixels.length, y = 0; y < height; y++) {
				pp -= width;
				encodeMaskRow(data, dp, pixels, pp, width);
				dp += maskRowWidth;
			}
		}
	}
	
	private static int dataLength(int dp, int width, int height, int bpp, int colors, boolean mask) {
		dp += colors * 4;
		
		int rowWidth = (((width * bpp) + 31) / 32) * 4;
		dp += rowWidth * height;
		
		if (mask) {
			int maskRowWidth = ((width + 31) / 32) * 4;
			dp += maskRowWidth * height;
		}
		
		return dp;
	}
	
	private static void decodeRow(byte[] data, int dp, int[] pixels, int pp, int width, int bpp, int[] colorTable) {
		switch (bpp) {
		case 1:
			for (int x = 0; x < width;) {
				int b = data[dp++];
				for (int s = (8-1); x < width && s >= 0; x++, s -= 1) {
					pixels[pp++] = colorTable[(b >> s) & 1];
				}
			}
			break;
		case 2:
			for (int x = 0; x < width;) {
				int b = data[dp++];
				for (int s = (8-2); x < width && s >= 0; x++, s -= 2) {
					pixels[pp++] = colorTable[(b >> s) & 3];
				}
			}
			break;
		case 4:
			for (int x = 0; x < width;) {
				int b = data[dp++];
				for (int s = (8-4); x < width && s >= 0; x++, s -= 4) {
					pixels[pp++] = colorTable[(b >> s) & 15];
				}
			}
			break;
		case 8:
			for (int x = 0; x < width; x++, dp += 1) {
				int i = data[dp] & 0xFF;
				pixels[pp++] = colorTable[i];
			}
			break;
		case 16:
			for (int x = 0; x < width; x++, dp += 2) {
				int i = getInt16LE(data, dp);
				int b = ((i >>  0) & 0x1F); b = (((b << 3) | (b >> 2)) <<  0);
				int g = ((i >>  5) & 0x1F); g = (((g << 3) | (g >> 2)) <<  8);
				int r = ((i >> 10) & 0x1F); r = (((r << 3) | (r >> 2)) << 16);
				int a = ((i >> 15) & 0x01); a = ((      a * 0xFF     ) << 24);
				pixels[pp++] = b | g | r | a;
			}
			break;
		case 24:
			for (int x = 0; x < width; x++, dp += 3) {
				int i = getInt24LE(data, dp);
				pixels[pp++] = i | 0xFF000000;
			}
			break;
		case 32:
			for (int x = 0; x < width; x++, dp += 4) {
				int i = getInt32LE(data, dp);
				pixels[pp++] = i;
			}
			break;
		}
	}
	
	private static void encodeRow(byte[] data, int dp, int[] pixels, int pp, int width, int bpp, int[] colorTable) {
		switch (bpp) {
		case 1:
			for (int x = 0; x < width;) {
				byte b = 0;
				for (int s = (8-1); x < width && s >= 0; x++, s -= 1) {
					b |= (closestColorIndex(colorTable, pixels[pp++]) << s);
				}
				data[dp++] = b;
			}
			break;
		case 2:
			for (int x = 0; x < width;) {
				byte b = 0;
				for (int s = (8-2); x < width && s >= 0; x++, s -= 2) {
					b |= (closestColorIndex(colorTable, pixels[pp++]) << s);
				}
				data[dp++] = b;
			}
			break;
		case 4:
			for (int x = 0; x < width;) {
				byte b = 0;
				for (int s = (8-4); x < width && s >= 0; x++, s -= 4) {
					b |= (closestColorIndex(colorTable, pixels[pp++]) << s);
				}
				data[dp++] = b;
			}
			break;
		case 8:
			for (int x = 0; x < width; x++, dp += 1) {
				int i = closestColorIndex(colorTable, pixels[pp++]);
				data[dp] = (byte)i;
			}
			break;
		case 16:
			for (int x = 0; x < width; x++, dp += 2) {
				int i = pixels[pp++];
				int b = (((i >>  3) & 0x1F) <<  0);
				int g = (((i >> 11) & 0x1F) <<  5);
				int r = (((i >> 19) & 0x1F) << 10);
				int a = (((i >> 31) & 0x01) << 15);
				putInt16LE(data, dp, (b | g | r | a));
			}
			break;
		case 24:
			for (int x = 0; x < width; x++, dp += 3) {
				int i = pixels[pp++];
				putInt24LE(data, dp, i);
			}
			break;
		case 32:
			for (int x = 0; x < width; x++, dp += 4) {
				int i = pixels[pp++];
				putInt32LE(data, dp, i);
			}
			break;
		}
	}
	
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
	
	private static void decodeMaskRow(byte[] data, int dp, int[] pixels, int pp, int width) {
		for (int x = 0; x < width;) {
			int b = data[dp++];
			for (int m = 0x80; x < width && m != 0; x++, m >>= 1) {
				if ((b & m) == 0) {
					pixels[pp++] |= 0xFF000000;
				} else {
					pixels[pp++] &= 0xFFFFFF;
				}
			}
		}
	}
	
	private static void encodeMaskRow(byte[] data, int dp, int[] pixels, int pp, int width) {
		for (int x = 0; x < width;) {
			byte b = 0;
			for (int m = 0x80; x < width && m != 0; x++, m >>= 1) {
				if (pixels[pp++] >= 0) {
					b |= m;
				}
			}
			data[dp++] = b;
		}
	}
}
