package com.kreative.keyedit;

import java.io.File;
import java.io.IOException;

public enum KeyboardFormat {
	KKB {
		@Override
		public boolean recognizesInputFormatName(String s) {
			return s.equalsIgnoreCase(".kkb")
			    || s.equalsIgnoreCase(".kkbx")
			    || s.equalsIgnoreCase("kkb")
			    || s.equalsIgnoreCase("kkbx")
			    || s.equalsIgnoreCase("keyedit");
		}
		@Override
		public boolean recognizesOutputFormatName(String s) {
			return s.equalsIgnoreCase(".kkb")
			    || s.equalsIgnoreCase(".kkbx")
			    || s.equalsIgnoreCase("kkb")
			    || s.equalsIgnoreCase("kkbx")
			    || s.equalsIgnoreCase("keyedit");
		}
		@Override
		public boolean recognizesInputFile(File f) {
			return f.getName().toLowerCase().endsWith(".kkbx");
		}
		@Override
		public boolean recognizesOutputFile(File f) {
			return f.getName().toLowerCase().endsWith(".kkbx");
		}
		@Override
		public KeyboardMapping read(File f) throws IOException {
			return KkbReader.read(f);
		}
		@Override
		public void write(File f, KeyboardMapping km) throws IOException {
			KkbWriter.write(f, km);
		}
	},
	MAC {
		@Override
		public boolean recognizesInputFormatName(String s) {
			return s.equalsIgnoreCase(".keylayout")
			    || s.equalsIgnoreCase("keylayout")
			    || s.equalsIgnoreCase("mac")
			    || s.equalsIgnoreCase("macos")
			    || s.equalsIgnoreCase("macintosh");
		}
		@Override
		public boolean recognizesOutputFormatName(String s) {
			return s.equalsIgnoreCase(".keylayout")
			    || s.equalsIgnoreCase("keylayout")
			    || s.equalsIgnoreCase("mac")
			    || s.equalsIgnoreCase("macos")
			    || s.equalsIgnoreCase("macintosh");
		}
		@Override
		public boolean recognizesInputFile(File f) {
			return f.getName().toLowerCase().endsWith(".keylayout");
		}
		@Override
		public boolean recognizesOutputFile(File f) {
			return f.getName().toLowerCase().endsWith(".keylayout");
		}
		@Override
		public KeyboardMapping read(File f) throws IOException {
			return MacReader.read(f);
		}
		@Override
		public void write(File f, KeyboardMapping km) throws IOException {
			MacWriter.write(f, km);
		}
	},
	WIN {
		@Override
		public boolean recognizesInputFormatName(String s) {
			return s.equalsIgnoreCase(".klc")
			    || s.equalsIgnoreCase("klc")
			    || s.equalsIgnoreCase("msklc")
			    || s.equalsIgnoreCase("win")
			    || s.equalsIgnoreCase("windows");
		}
		@Override
		public boolean recognizesOutputFormatName(String s) {
			return s.equalsIgnoreCase(".klc")
			    || s.equalsIgnoreCase("klc")
			    || s.equalsIgnoreCase("msklc")
			    || s.equalsIgnoreCase("win")
			    || s.equalsIgnoreCase("windows");
		}
		@Override
		public boolean recognizesInputFile(File f) {
			return f.getName().toLowerCase().endsWith(".klc");
		}
		@Override
		public boolean recognizesOutputFile(File f) {
			return f.getName().toLowerCase().endsWith(".klc");
		}
		@Override
		public KeyboardMapping read(File f) throws IOException {
			return WinReader.read(f);
		}
		@Override
		public void write(File f, KeyboardMapping km) throws IOException {
			WinWriter.write(f, km);
		}
	},
	XKB {
		@Override
		public boolean recognizesInputFormatName(String s) {
			return s.equalsIgnoreCase("xkb")
			    || s.equalsIgnoreCase("x11")
			    || s.equalsIgnoreCase("linux");
		}
		@Override
		public boolean recognizesOutputFormatName(String s) {
			return s.equalsIgnoreCase("xkb")
			    || s.equalsIgnoreCase("x11")
			    || s.equalsIgnoreCase("linux");
		}
		@Override
		public boolean recognizesInputFile(File f) {
			return !f.getName().contains(".");
		}
		@Override
		public boolean recognizesOutputFile(File f) {
			return !f.getName().contains(".");
		}
		@Override
		public KeyboardMapping read(File f) throws IOException {
			return XkbReader.read(f);
		}
		@Override
		public void write(File f, KeyboardMapping km) throws IOException {
			XkbWriter.write(f, km);
		}
	},
	HTML {
		@Override
		public boolean recognizesInputFormatName(String s) {
			return false;
		}
		@Override
		public boolean recognizesOutputFormatName(String s) {
			return s.equalsIgnoreCase(".html")
			    || s.equalsIgnoreCase(".htm")
			    || s.equalsIgnoreCase("html")
			    || s.equalsIgnoreCase("htm");
		}
		@Override
		public boolean recognizesInputFile(File f) {
			return false;
		}
		@Override
		public boolean recognizesOutputFile(File f) {
			return f.getName().toLowerCase().endsWith(".html");
		}
		@Override
		public KeyboardMapping read(File f) throws IOException {
			throw new IOException("HTML import unsupported.");
		}
		@Override
		public void write(File f, KeyboardMapping km) throws IOException {
			HTMLWriter.write(f, km);
		}
	};
	
	public abstract boolean recognizesInputFormatName(String s);
	public abstract boolean recognizesOutputFormatName(String s);
	public abstract boolean recognizesInputFile(File f);
	public abstract boolean recognizesOutputFile(File f);
	public abstract KeyboardMapping read(File f) throws IOException;
	public abstract void write(File f, KeyboardMapping km) throws IOException;
	
	public static KeyboardFormat forInputFormatName(String s) {
		for (KeyboardFormat format : values()) {
			if (format.recognizesInputFormatName(s)) {
				return format;
			}
		}
		return null;
	}
	
	public static KeyboardFormat forOutputFormatName(String s) {
		for (KeyboardFormat format : values()) {
			if (format.recognizesOutputFormatName(s)) {
				return format;
			}
		}
		return null;
	}
	
	public static KeyboardFormat forInputFile(File f) {
		for (KeyboardFormat format : values()) {
			if (format.recognizesInputFile(f)) {
				return format;
			}
		}
		return null;
	}
	
	public static KeyboardFormat forOutputFile(File f) {
		for (KeyboardFormat format : values()) {
			if (format.recognizesOutputFile(f)) {
				return format;
			}
		}
		return null;
	}
}
