package com.kreative.bitsnpicas;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

public class FileProxy {
	private final File file;
	private final String lcname;
	private boolean checkedMacInfo;
	private String macType;
	private String macCreator;
	private byte[] contents;
	private boolean checkedImage;
	private BufferedImage image;
	
	public FileProxy(String pathname) {
		this(new File(pathname));
	}
	
	public FileProxy(String parent, String child) {
		this(new File(parent, child));
	}
	
	public FileProxy(File parent, String child) {
		this(new File(parent, child));
	}
	
	public FileProxy(File file) {
		this.file = file;
		this.lcname = file.getName().toLowerCase();
		this.checkedMacInfo = false;
		this.macType = null;
		this.macCreator = null;
		this.contents = null;
		this.checkedImage = false;
		this.image = null;
	}
	
	public boolean hasExtension(String... exts) {
		for (String ext : exts) {
			ext = ext.toLowerCase();
			if (!ext.startsWith(".")) ext = "." + ext;
			if (lcname.endsWith(ext)) return true;
		}
		return false;
	}
	
	private void checkMacInfo() {
		if (!checkedMacInfo) {
			macType = MacUtility.getType(file);
			macCreator = MacUtility.getCreator(file);
			checkedMacInfo = true;
		}
	}
	
	public boolean hasMacType(String type) {
		checkMacInfo();
		return type.equals(macType);
	}
	
	public boolean hasMacCreator(String creator) {
		checkMacInfo();
		return creator.equals(macCreator);
	}
	
	private void checkContents(int length) {
		if (contents == null || contents.length < length) {
			try {
				byte[] buf = new byte[length];
				FileInputStream in = new FileInputStream(file);
				for (int i = 0; i < length; i++) buf[i] = (byte)in.read();
				in.close();
				contents = buf;
			} catch (IOException e) {
				return;
			}
		}
	}
	
	public boolean startsWith(byte... data) {
		checkContents(data.length);
		if (contents == null || contents.length < data.length) {
			return false;
		} else {
			for (int i = 0; i < data.length; i++) {
				if (contents[i] != (byte)data[i]) {
					return false;
				}
			}
			return true;
		}
	}
	
	public boolean startsWith(int... data) {
		checkContents(data.length);
		if (contents == null || contents.length < data.length) {
			return false;
		} else {
			for (int i = 0; i < data.length; i++) {
				if (contents[i] != (byte)data[i]) {
					return false;
				}
			}
			return true;
		}
	}
	
	public boolean isImage() {
		if (checkedImage) return image != null;
		try {
			image = ImageIO.read(file);
			checkedImage = true;
			return image != null;
		} catch (IOException e) {
			return false;
		}
	}
	
	public BufferedImage getImage() {
		if (checkedImage) return image;
		try {
			image = ImageIO.read(file);
			checkedImage = true;
			return image;
		} catch (IOException e) {
			return null;
		}
	}
}
