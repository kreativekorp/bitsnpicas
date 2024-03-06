package com.kreative.keyedit;

import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Map;
import java.util.TreeMap;

public class KeyManWriter {
	public static void write(File file, KeyboardMapping km) throws IOException {
		String basename = stripSuffix(file.getName(), ".kmn");
		File icoFile = new File(file.getParentFile(), basename + ".ico");
		File kvksFile = new File(file.getParentFile(), basename + ".kvks");
		File ktlFile = new File(file.getParentFile(), basename + ".keyman-touch-layout");
		FileOutputStream fos = new FileOutputStream(file);
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(fos, "UTF-8"), true);
		write(
			pw, km,
			((km.icon != null) ? icoFile.getName() : null),
			(kvksFile.exists() ? kvksFile.getName() : null),
			(ktlFile.exists() ? ktlFile.getName() : null),
			false
		);
		pw.flush();
		pw.close();
		fos.close();
		if (km.icon != null) {
			FileOutputStream ifos = new FileOutputStream(icoFile);
			DataOutputStream idos = new DataOutputStream(ifos);
			WinIconDir ico = new WinIconDir();
			ico.add(newIcoSize(km.icon, 8, ColorTables.createWindowsBase()));
			ico.add(newIcoSize(km.icon, 4, ColorTables.createWindows4()));
			ico.add(newIcoSize(km.icon, 1, ColorTables.createBlackToWhite(1)));
			ico.write(idos);
			idos.flush();
			idos.close();
			ifos.close();
		}
	}
	
	public static void write(PrintWriter out, KeyboardMapping km, String icoFileName, String kvksFileName, String ktlFileName, boolean handleCaps) {
		out.print("\uFEFF");
		
		if (km.keymanComments != null && km.keymanComments.length() > 0) {
			String[] lines = km.keymanComments.replaceAll("\r\n","\n").replaceAll("\r","\n").split("\n");
			for (String line : lines) out.print("c " + line + "\r\n");
			out.print("\r\n");
		} else if (km.xkbComment != null && km.xkbComment.length() > 0) {
			String[] lines = km.xkbComment.replaceAll("\r\n","\n").replaceAll("\r","\n").split("\n");
			for (String line : lines) out.print("c " + line + "\r\n");
			out.print("\r\n");
		}
		
		out.print("store(&VERSION) '10.0'\r\n");
		out.print("store(&NAME) " + quote(km.getKeymanNameNotEmpty()) + "\r\n");
		out.print("store(&COPYRIGHT) " + quote(km.getKeymanCopyrightNotEmpty()) + "\r\n");
		out.print("store(&KEYBOARDVERSION) " + quote(km.getKeymanVersionNotEmpty()) + "\r\n");
		
		if (km.keymanTargets != null && km.keymanTargets.size() > 0) {
			StringBuffer sb = new StringBuffer();
			boolean first = true;
			for (KeyManTarget t : KeyManTarget.values()) {
				if (km.keymanTargets.contains(t)) {
					if (first) first = false;
					else sb.append(" ");
					sb.append(t);
				}
			}
			out.print("store(&TARGETS) " + quote(sb.toString()) + "\r\n");
		}
		
		if (icoFileName != null) out.print("store(&BITMAP) " + quote(icoFileName) + "\r\n");
		if (kvksFileName != null) out.print("store(&VISUALKEYBOARD) " + quote(kvksFileName) + "\r\n");
		if (ktlFileName != null) out.print("store(&LAYOUTFILE) " + quote(ktlFileName) + "\r\n");
		
		if (km.keymanMessage != null && km.keymanMessage.length() > 0) {
			out.print("store(&MESSAGE) " + quote(km.keymanMessage) + "\r\n");
		}
		if (km.keymanRightToLeft) {
			out.print("store(&KMW_RTL) '1'\r\n");
		}
		if (km.keymanWebHelpText != null && km.keymanWebHelpText.length() > 0) {
			out.print("store(&KMW_HELPTEXT) " + quote(km.keymanWebHelpText) + "\r\n");
		}
		
		out.print("\r\n");
		out.print("begin Unicode > use(main)\r\n");
		out.print("\r\n");
		
		Map<String,DeadKeyTable> deadKeys = new TreeMap<String,DeadKeyTable>();
		out.print("group(main) using keys\r\n");
		for (KeyManKey k : KeyManKey.KEYS) {
			KeyMapping m = km.map.get(k.key);
			writeKeyMapping(out, km, "SHIFT RALT " + k.id, m.altShiftedOutput, m.altShiftedDeadKey, deadKeys);
		}
		if (handleCaps) {
			for (KeyManKey k : KeyManKey.KEYS) {
				KeyMapping m = km.map.get(k.key);
				writeKeyMapping(out, km, "CAPS RALT " + k.id, m.altCapsLockMapping, m.altUnshiftedOutput, m.altUnshiftedDeadKey, m.altShiftedOutput, m.altShiftedDeadKey, deadKeys);
			}
			for (KeyManKey k : KeyManKey.KEYS) {
				KeyMapping m = km.map.get(k.key);
				writeKeyMapping(out, km, "NCAPS RALT " + k.id, m.altUnshiftedOutput, m.altUnshiftedDeadKey, deadKeys);
			}
		} else {
			for (KeyManKey k : KeyManKey.KEYS) {
				KeyMapping m = km.map.get(k.key);
				writeKeyMapping(out, km, "RALT " + k.id, m.altUnshiftedOutput, m.altUnshiftedDeadKey, deadKeys);
			}
		}
		for (KeyManKey k : KeyManKey.KEYS) {
			KeyMapping m = km.map.get(k.key);
			writeKeyMapping(out, km, "SHIFT " + k.id, m.shiftedOutput, m.shiftedDeadKey, deadKeys);
		}
		if (handleCaps) {
			for (KeyManKey k : KeyManKey.KEYS) {
				KeyMapping m = km.map.get(k.key);
				writeKeyMapping(out, km, "CAPS " + k.id, m.capsLockMapping, m.unshiftedOutput, m.unshiftedDeadKey, m.shiftedOutput, m.shiftedDeadKey, deadKeys);
			}
			for (KeyManKey k : KeyManKey.KEYS) {
				KeyMapping m = km.map.get(k.key);
				writeKeyMapping(out, km, "NCAPS " + k.id, m.unshiftedOutput, m.unshiftedDeadKey, deadKeys);
			}
		} else {
			for (KeyManKey k : KeyManKey.KEYS) {
				KeyMapping m = km.map.get(k.key);
				writeKeyMapping(out, km, k.id, m.unshiftedOutput, m.unshiftedDeadKey, deadKeys);
			}
		}
		
		for (Map.Entry<String,DeadKeyTable> dke : deadKeys.entrySet()) {
			out.print("\r\n");
			String name = dke.getKey();
			StringBuffer input = new StringBuffer();
			StringBuffer output = new StringBuffer();
			for (Map.Entry<Integer,Integer> kme : dke.getValue().keyMap.entrySet()) {
				input.append(" d"); input.append(kme.getKey());
				output.append(" d"); output.append(kme.getValue());
			}
			out.print("store(" + name + "in)" + input + "\r\n");
			out.print("store(" + name + "out)" + output + "\r\n");
			out.print("dk(" + name + ") + any(" + name + "in) > index(" + name + "out, 2)\r\n");
		}
	}
	
	private static void writeKeyMapping(PrintWriter out, KeyboardMapping km, String keyCode, CapsLockMapping caps, int uout, DeadKeyTable udead, int sout, DeadKeyTable sdead, Map<String,DeadKeyTable> deadKeys) {
		if (caps == CapsLockMapping.UNSHIFTED) {
			writeKeyMapping(out, km, keyCode, uout, udead, deadKeys);
		} else if (caps == CapsLockMapping.SHIFTED) {
			writeKeyMapping(out, km, keyCode, sout, sdead, deadKeys);
		} else if (udead != null && sdead != null) {
			caps = isCasePair(udead.winTerminator, sdead.winTerminator) ? CapsLockMapping.SHIFTED : CapsLockMapping.UNSHIFTED;
			writeKeyMapping(out, km, keyCode, caps, uout, udead, sout, sdead, deadKeys);
		} else if (udead != null) {
			caps = (udead.winTerminator == sout) ? CapsLockMapping.SHIFTED : CapsLockMapping.UNSHIFTED;
			writeKeyMapping(out, km, keyCode, caps, uout, udead, sout, sdead, deadKeys);
		} else if (sdead != null) {
			caps = (sdead.winTerminator == uout) ? CapsLockMapping.SHIFTED : CapsLockMapping.UNSHIFTED;
			writeKeyMapping(out, km, keyCode, caps, uout, udead, sout, sdead, deadKeys);
		} else {
			caps = isCasePair(uout, sout) ? CapsLockMapping.SHIFTED : CapsLockMapping.UNSHIFTED;
			writeKeyMapping(out, km, keyCode, caps, uout, udead, sout, sdead, deadKeys);
		}
	}
	
	private static boolean isCasePair(int u, int s) {
		return (u != s) && (Character.toUpperCase(u) == s || Character.toLowerCase(s) == u);
	}
	
	private static void writeKeyMapping(PrintWriter out, KeyboardMapping km, String keyCode, int output, DeadKeyTable dead, Map<String,DeadKeyTable> deadKeys) {
		if (dead != null) {
			String dk = getActionId(km, getCP(output, dead));
			deadKeys.put(dk, dead);
			out.print("+ [" + keyCode + "] > dk(" + dk + ")\r\n");
		} else if (output > 0) {
			out.print("+ [" + keyCode + "] > d" + output + "\r\n");
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
	
	private static String getActionId(KeyboardMapping km, int cp) {
		if (km.macActionIds.containsKey(cp)) {
			return km.macActionIds.get(cp);
		} else {
			return XkbKeySym.MAP.getKeySym(cp);
		}
	}
	
	private static String quote(String s) {
		if (s == null || s.length() == 0) return "''";
		s = "'" + s.replace("'", "' U+0027 '") + "' ";
		return s.replace("'' ", "").trim();
	}
	
	private static WinIconDirEntry newIcoSize(BufferedImage image, int bpp, int[] colorTable) {
		WinIconDirEntry e = new WinIconDirEntry();
		e.setBMPImage(image, bpp, colorTable);
		return e;
	}
	
	private static String stripSuffix(String s, String suffix) {
		if (s.toLowerCase().endsWith(suffix.toLowerCase())) {
			return s.substring(0, s.length() - suffix.length());
		} else {
			return s;
		}
	}
}
