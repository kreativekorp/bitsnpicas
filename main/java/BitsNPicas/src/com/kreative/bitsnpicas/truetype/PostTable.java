package com.kreative.bitsnpicas.truetype;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class PostTable extends ListBasedTable<PostTableEntry> {
	public static final int FORMAT_1 = 0x00010000;
	public static final int FORMAT_2 = 0x00020000;
	public static final int FORMAT_2_5 = 0x00028000;
	public static final int FORMAT_3 = 0x00030000;
	public static final int FORMAT_4 = 0x00040000;
	public static final int ITALIC_ANGLE_ISOMETRIC = 0xFFE56F59;
	public static final int ITALIC_ANGLE_UPRIGHT = 0;
	public static final int FIXED_PITCH_FALSE = 0;
	public static final int FIXED_PITCH_TRUE = 1;
	public static final int MEM_UNKNOWN = 0;
	
	public int format = FORMAT_2;
	public int italicAngle = ITALIC_ANGLE_UPRIGHT;
	public int underlinePosition = 0;
	public int underlineThickness = 0;
	public int fixedPitch = FIXED_PITCH_FALSE;
	public int minMemType42 = MEM_UNKNOWN;
	public int maxMemType42 = MEM_UNKNOWN;
	public int minMemType1 = MEM_UNKNOWN;
	public int maxMemType1 = MEM_UNKNOWN;
	
	@Override
	public String tableName() {
		return "post";
	}
	
	@Override
	public String[] dependencyNames() {
		return new String[]{};
	}
	
	@Override
	protected void compile(DataOutputStream out, TrueTypeTable[] dependencies) throws IOException {
		out.writeInt(format);
		out.writeInt(italicAngle);
		out.writeShort(underlinePosition);
		out.writeShort(underlineThickness);
		out.writeInt(fixedPitch);
		out.writeInt(minMemType42);
		out.writeInt(maxMemType42);
		out.writeInt(minMemType1);
		out.writeInt(maxMemType1);
		
		switch (format) {
		case FORMAT_1:
			// No additional content.
			break;
		case FORMAT_2:
			out.writeShort(this.size());
			int stringIndex = 258;
			for (PostTableEntry e : this) {
				if (e.isInteger()) {
					out.writeShort(e.intValue());
				} else if (e.isString()) {
					out.writeShort(stringIndex++);
				}
			}
			for (PostTableEntry e : this) {
				if (e.isString()) {
					byte[] data;
					try {
						data = e.stringValue().getBytes("US-ASCII");
					} catch (UnsupportedEncodingException uee) {
						throw new IllegalStateException("Java is stupid.");
					}
					out.writeByte(data.length);
					out.write(data);
					stringIndex--;
				}
			}
			if (stringIndex != 258) {
				throw new IllegalStateException("Assertion failed: number of names written <> number of names assigned.");
			}
			break;
		case FORMAT_2_5:
			out.writeShort(this.size());
			for (int i = 0; i < this.size(); i++) {
				PostTableEntry e = this.get(i);
				if (e.isInteger()) {
					out.writeByte(e.intValue() - i);
				} else {
					throw new IllegalStateException("Invalid entry in Format 2.5 'post' table.");
				}
			}
			break;
		case FORMAT_3:
			// No additional content.
			break;
		case FORMAT_4:
			for (int i = 0; i < this.size(); i++) {
				PostTableEntry e = this.get(i);
				if (e.isInteger()) {
					out.writeShort(e.intValue());
				} else {
					throw new IllegalStateException("Invalid entry in Format 4 'post' table.");
				}
			}
			break;
		default:
			throw new IllegalStateException("Invalid format for 'post' table.");
		}
	}
	
	@Override
	protected void decompile(DataInputStream in, int length, TrueTypeTable[] dependencies) throws IOException {
		format = in.readInt();
		italicAngle = in.readInt();
		underlinePosition = in.readShort();
		underlineThickness = in.readShort();
		fixedPitch = in.readInt();
		minMemType42 = in.readInt();
		maxMemType42 = in.readInt();
		minMemType1 = in.readInt();
		maxMemType1 = in.readInt();
		
		switch (format) {
		case FORMAT_1:
			this.clear();
			// No additional content.
			break;
		case FORMAT_2:
			int count = in.readUnsignedShort();
			int[] intValues = new int[count];
			int maxValue = 0;
			for (int i = 0; i < count; i++) {
				intValues[i] = in.readUnsignedShort();
				if (intValues[i] > maxValue) {
					maxValue = intValues[i];
				}
			}
			String[] stringValues = new String[maxValue + 1];
			for (int i = 258; i < stringValues.length; i++) {
				byte[] d = new byte[in.readUnsignedByte()];
				in.readFully(d);
				try {
					stringValues[i] = new String(d, "US-ASCII");
				} catch (UnsupportedEncodingException uee) {
					throw new IllegalStateException("Java is stupid.");
				}
			}
			this.clear();
			for (int i = 0; i < count; i++) {
				int intValue = intValues[i];
				String stringValue = stringValues[intValue];
				if (stringValue != null) {
					this.add(new PostTableEntry(stringValue));
				} else {
					this.add(new PostTableEntry(intValue));
				}
			}
			break;
		case FORMAT_2_5:
			int cnt = in.readUnsignedShort();
			this.clear();
			for (int i = 0; i < cnt; i++) {
				int intValue = i + in.readByte();
				this.add(new PostTableEntry(intValue));
			}
			break;
		case FORMAT_3:
			this.clear();
			// No additional content.
			break;
		case FORMAT_4:
			int n = (length - 32) / 2;
			this.clear();
			for (int i = 0; i < n; i++) {
				int intValue = in.readUnsignedShort();
				if (intValue == 0xFFFF) intValue = -1;
				this.add(new PostTableEntry(intValue));
			}
			break;
		default:
			throw new IllegalStateException("Invalid format for 'post' table.");
		}
	}
}
