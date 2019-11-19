package com.kreative.bitsnpicas.mover;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class FONDEntry implements Comparable<FONDEntry> {
	public final int size;
	public final int style;
	public final int id;
	
	public FONDEntry(int size, int style, int id) {
		this.size = size;
		this.style = style;
		this.id = id;
	}
	
	public FONDEntry(DataInput in) throws IOException {
		this.size = in.readShort();
		this.style = in.readShort();
		this.id = in.readShort();
	}
	
	public void write(DataOutput out) throws IOException {
		out.writeShort(size);
		out.writeShort(style);
		out.writeShort(id);
	}
	
	@Override
	public int compareTo(FONDEntry that) {
		if (this.size != that.size) return this.size - that.size;
		if (this.style != that.style) return this.style - that.style;
		return this.id - that.id;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof FONDEntry) {
			FONDEntry that = (FONDEntry)o;
			return (
				this.size == that.size &&
				this.style == that.style &&
				this.id == that.id
			);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return (id << 16) ^ (style << 8) ^ size;
	}
	
	@Override
	public String toString() {
		return "[" + toString("FONDEntry") + " -> " + id + "]";
	}
	
	public String toString(String fontName) {
		if (style != 0) fontName += " (" + styleString(style) + ")";
		if (size != 0) fontName += " " + size;
		return fontName;
	}
	
	private static String styleString(int style) {
		StringBuffer sb = new StringBuffer();
		if ((style & 0x01) != 0) { if (sb.length() > 0) sb.append(", "); sb.append("bold"     ); }
		if ((style & 0x02) != 0) { if (sb.length() > 0) sb.append(", "); sb.append("italic"   ); }
		if ((style & 0x04) != 0) { if (sb.length() > 0) sb.append(", "); sb.append("underline"); }
		if ((style & 0x08) != 0) { if (sb.length() > 0) sb.append(", "); sb.append("outline"  ); }
		if ((style & 0x10) != 0) { if (sb.length() > 0) sb.append(", "); sb.append("shadow"   ); }
		if ((style & 0x20) != 0) { if (sb.length() > 0) sb.append(", "); sb.append("condensed"); }
		if ((style & 0x40) != 0) { if (sb.length() > 0) sb.append(", "); sb.append("extended" ); }
		if ((style & 0x80) != 0) { if (sb.length() > 0) sb.append(", "); sb.append("group"    ); }
		return sb.toString();
	}
}
