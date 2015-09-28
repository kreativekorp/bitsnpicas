package com.kreative.bitsnpicas.truetype;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SbixEntry {
	public static final int VERSION_DEFAULT = 0;
	public static final int IMAGE_TYPE_PNG = 0x706E6720;
	
	public int version = VERSION_DEFAULT;
	public int imageType = 0;
	public byte[] imageData = new byte[0];
	
	public String getImageTypeString() {
		return new String(new char[]{
			(char)((imageType >> 24) & 0xFF),
			(char)((imageType >> 16) & 0xFF),
			(char)((imageType >>  8) & 0xFF),
			(char)((imageType >>  0) & 0xFF),
		});
	}
	
	public void setImageTypeString(String imageTypeString) {
		char[] a = imageTypeString.toCharArray();
		imageType = (((a.length > 0 && a[0] >= 0x20 && a[0] < 0x7F) ? a[0] : 0x20) << 24)
		          | (((a.length > 1 && a[1] >= 0x20 && a[1] < 0x7F) ? a[1] : 0x20) << 16)
		          | (((a.length > 2 && a[2] >= 0x20 && a[2] < 0x7F) ? a[2] : 0x20) <<  8)
		          | (((a.length > 3 && a[3] >= 0x20 && a[3] < 0x7F) ? a[3] : 0x20) <<  0);
	}
	
	private void compile(DataOutputStream out) throws IOException {
		if (imageData != null && imageData.length > 0) {
			out.writeInt(version);
			out.writeInt(imageType);
			out.write(imageData);
		}
	}
	
	public byte[] compile() throws IOException {
		ByteArrayOutputStream byteout = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(byteout);
		compile(out);
		out.flush();
		byteout.flush();
		out.close();
		byteout.close();
		return byteout.toByteArray();
	}
	
	private void decompile(DataInputStream in, int length) throws IOException {
		if (length == 0) {
			version = VERSION_DEFAULT;
			imageType = 0;
			imageData = new byte[0];
		} else {
			version = in.readInt();
			imageType = in.readInt();
			imageData = new byte[length - 8];
			in.readFully(imageData);
		}
	}
	
	public void decompile(byte[] data) throws IOException {
		ByteArrayInputStream bytein = new ByteArrayInputStream(data);
		DataInputStream in = new DataInputStream(bytein);
		decompile(in, data.length);
		in.close();
		bytein.close();
	}
}
