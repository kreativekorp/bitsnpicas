package com.kreative.keyedit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class KeyManVisualWriter {
	public static void write(File file, KeyboardMapping km) throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(fos, "UTF-8"), true);
		write(pw, km);
		pw.flush();
		pw.close();
		fos.close();
	}
	
	public static void write(PrintWriter out, KeyboardMapping km) {
		out.print("<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n");
		out.print("<visualkeyboard>\r\n");
		out.print("  <header>\r\n");
		out.print("    <version>10.0</version>\r\n");
		out.print("    <kbdname>" + quote(km.getKeymanIdentifierNotEmpty()) + "</kbdname>\r\n");
		out.print("    <flags>\r\n");
		if (km.keymanKey102)            out.print("      <key102/>\r\n");
		if (km.keymanDisplayUnderlying) out.print("      <displayunderlying/>\r\n");
		if (km.keymanUseAltGr)          out.print("      <usealtgr/>\r\n");
		out.print("    </flags>\r\n");
		out.print("  </header>\r\n");
		out.print("  <encoding name=\"unicode\" fontname=\"Arial\" fontsize=\"-12\">\r\n");
		out.print("    <layer shift=\"\">\r\n");
		for (KeyManKey k : KeyManKey.KEYS) {
			KeyMapping m = km.map.get(k.key);
			writeKeyMapping(out, k.id, m.unshiftedOutput, m.unshiftedDeadKey);
		}
		out.print("    </layer>\r\n");
		out.print("    <layer shift=\"S\">\r\n");
		for (KeyManKey k : KeyManKey.KEYS) {
			KeyMapping m = km.map.get(k.key);
			writeKeyMapping(out, k.id, m.shiftedOutput, m.shiftedDeadKey);
		}
		out.print("    </layer>\r\n");
		out.print("    <layer shift=\"A\">\r\n");
		for (KeyManKey k : KeyManKey.KEYS) {
			KeyMapping m = km.map.get(k.key);
			writeKeyMapping(out, k.id, m.altUnshiftedOutput, m.altUnshiftedDeadKey);
		}
		out.print("    </layer>\r\n");
		out.print("    <layer shift=\"SA\">\r\n");
		for (KeyManKey k : KeyManKey.KEYS) {
			KeyMapping m = km.map.get(k.key);
			writeKeyMapping(out, k.id, m.altShiftedOutput, m.altShiftedDeadKey);
		}
		out.print("    </layer>\r\n");
		out.print("  </encoding>\r\n");
		out.print("</visualkeyboard>\r\n");
	}
	
	private static void writeKeyMapping(PrintWriter out, String keyCode, int output, DeadKeyTable dead) {
		int cp = getCP(output, dead);
		if (cp > 0) {
			String qs = quote(String.valueOf(Character.toChars(cp)));
			out.print("      <key vkey=\"" + keyCode + "\">" + qs + "</key>\r\n");
		}
	}
	
	private static int getCP(int output, DeadKeyTable dead) {
		if (dead != null) {
			if (dead.winTerminator > 0) {
				return dead.winTerminator;
			}
		}
		if (output > 0) {
			return output;
		}
		return -1;
	}
	
	private static String quote(String s) {
		if (s == null || s.length() == 0) return "";
		return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
	}
}
