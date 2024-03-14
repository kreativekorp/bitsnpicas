package com.kreative.keyedit;

import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

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
			(ktlFile.exists() ? ktlFile.getName() : null)
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
	
	public static void write(PrintWriter out, KeyboardMapping km, String icoFileName, String kvksFileName, String ktlFileName) {
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
		out.print("group(main) using keys\r\n");
		
		Map<String,DeadKeyTable> deadKeys = new TreeMap<String,DeadKeyTable>();
		Map<Integer,Set<String>> deadKeyInputs = new TreeMap<Integer,Set<String>>();
		if (km.keymanIgnoreCaps || canIgnoreCaps(km)) {
			for (KeyManKey k : KeyManKey.KEYS) {
				KeyMapping m = km.map.get(k.key);
				writeKeyMapping(out, km, "SHIFT RALT " + k.id, m.altShiftedOutput, m.altShiftedDeadKey, deadKeys, deadKeyInputs);
			}
			for (KeyManKey k : KeyManKey.KEYS) {
				KeyMapping m = km.map.get(k.key);
				writeKeyMapping(out, km, "RALT " + k.id, m.altUnshiftedOutput, m.altUnshiftedDeadKey, deadKeys, deadKeyInputs);
			}
			for (KeyManKey k : KeyManKey.KEYS) {
				KeyMapping m = km.map.get(k.key);
				writeKeyMapping(out, km, "SHIFT " + k.id, m.shiftedOutput, m.shiftedDeadKey, deadKeys, deadKeyInputs);
			}
			for (KeyManKey k : KeyManKey.KEYS) {
				KeyMapping m = km.map.get(k.key);
				writeKeyMapping(out, km, k.id, m.unshiftedOutput, m.unshiftedDeadKey, deadKeys, deadKeyInputs);
			}
		} else {
			for (KeyManKey k : KeyManKey.KEYS) {
				KeyMapping m = km.map.get(k.key);
				writeKeyMapping(out, km, "CAPS SHIFT RALT " + k.id, m.altShiftedOutput, m.altShiftedDeadKey, deadKeys, deadKeyInputs);
			}
			for (KeyManKey k : KeyManKey.KEYS) {
				KeyMapping m = km.map.get(k.key);
				writeKeyMapping(out, km, "NCAPS SHIFT RALT " + k.id, m.altShiftedOutput, m.altShiftedDeadKey, deadKeys, deadKeyInputs);
			}
			for (KeyManKey k : KeyManKey.KEYS) {
				KeyMapping m = km.map.get(k.key);
				writeKeyMapping(out, km, "CAPS RALT " + k.id, m.altCapsLockMapping, m.altUnshiftedOutput, m.altUnshiftedDeadKey, m.altShiftedOutput, m.altShiftedDeadKey, deadKeys, deadKeyInputs);
			}
			for (KeyManKey k : KeyManKey.KEYS) {
				KeyMapping m = km.map.get(k.key);
				writeKeyMapping(out, km, "NCAPS RALT " + k.id, m.altUnshiftedOutput, m.altUnshiftedDeadKey, deadKeys, deadKeyInputs);
			}
			for (KeyManKey k : KeyManKey.KEYS) {
				KeyMapping m = km.map.get(k.key);
				writeKeyMapping(out, km, "CAPS SHIFT " + k.id, m.shiftedOutput, m.shiftedDeadKey, deadKeys, deadKeyInputs);
			}
			for (KeyManKey k : KeyManKey.KEYS) {
				KeyMapping m = km.map.get(k.key);
				writeKeyMapping(out, km, "NCAPS SHIFT " + k.id, m.shiftedOutput, m.shiftedDeadKey, deadKeys, deadKeyInputs);
			}
			for (KeyManKey k : KeyManKey.KEYS) {
				KeyMapping m = km.map.get(k.key);
				writeKeyMapping(out, km, "CAPS " + k.id, m.capsLockMapping, m.unshiftedOutput, m.unshiftedDeadKey, m.shiftedOutput, m.shiftedDeadKey, deadKeys, deadKeyInputs);
			}
			for (KeyManKey k : KeyManKey.KEYS) {
				KeyMapping m = km.map.get(k.key);
				writeKeyMapping(out, km, "NCAPS " + k.id, m.unshiftedOutput, m.unshiftedDeadKey, deadKeys, deadKeyInputs);
			}
		}
		
		for (Map.Entry<String,DeadKeyTable> dke : deadKeys.entrySet()) {
			out.print("\r\n");
			String name = dke.getKey();
			StringBuffer input = new StringBuffer();
			StringBuffer output = new StringBuffer();
			for (Map.Entry<Integer,Integer> kme : dke.getValue().keyMap.entrySet()) {
				Set<String> dki = deadKeyInputs.get(kme.getKey());
				if (dki == null || dki.isEmpty()) {
					System.err.println(
						"Hint: 109A The rule will never be matched for key " + quote(kme.getKey()) +
						" because its key code is never fired. (╯°□°)╯⁔ ┴─┴" // flip the DeadKeyTable
					);
				} else {
					String valueCode = quote(kme.getValue());
					for (String keyCode : dki) {
						input.append(" ["); input.append(keyCode); input.append("]");
						output.append(" "); output.append(valueCode);
					}
				}
			}
			out.print("store(" + name + "in)" + input + "\r\n");
			out.print("store(" + name + "out)" + output + "\r\n");
			out.print("dk(" + name + ") + any(" + name + "in) > index(" + name + "out, 2)\r\n");
		}
	}
	
	private static boolean canIgnoreCaps(KeyboardMapping km) {
		for (KeyManKey k : KeyManKey.KEYS) {
			KeyMapping m = km.map.get(k.key);
			int ut = outputToken(CapsLockMapping.UNSHIFTED, m.unshiftedOutput, m.unshiftedDeadKey, m.shiftedOutput, m.shiftedDeadKey);
			int ct = outputToken(m.capsLockMapping, m.unshiftedOutput, m.unshiftedDeadKey, m.shiftedOutput, m.shiftedDeadKey);
			if (ut != ct) return false;
			ut = outputToken(CapsLockMapping.UNSHIFTED, m.altUnshiftedOutput, m.altUnshiftedDeadKey, m.altShiftedOutput, m.altShiftedDeadKey);
			ct = outputToken(m.altCapsLockMapping, m.altUnshiftedOutput, m.altUnshiftedDeadKey, m.altShiftedOutput, m.altShiftedDeadKey);
			if (ut != ct) return false;
		}
		return true;
	}
	
	private static int outputToken(CapsLockMapping caps, int uout, DeadKeyTable udead, int sout, DeadKeyTable sdead) {
		if (caps == CapsLockMapping.UNSHIFTED) {
			return (udead == null) ? uout : (Integer.MIN_VALUE + getCP(uout, udead));
		} else if (caps == CapsLockMapping.SHIFTED) {
			return (sdead == null) ? sout : (Integer.MIN_VALUE + getCP(sout, sdead));
		} else if (udead != null && sdead != null) {
			caps = isCasePair(udead.winTerminator, sdead.winTerminator) ? CapsLockMapping.SHIFTED : CapsLockMapping.UNSHIFTED;
			return outputToken(caps, uout, udead, sout, sdead);
		} else if (udead != null) {
			caps = (udead.winTerminator == sout) ? CapsLockMapping.SHIFTED : CapsLockMapping.UNSHIFTED;
			return outputToken(caps, uout, udead, sout, sdead);
		} else if (sdead != null) {
			caps = (sdead.winTerminator == uout) ? CapsLockMapping.SHIFTED : CapsLockMapping.UNSHIFTED;
			return outputToken(caps, uout, udead, sout, sdead);
		} else {
			caps = isCasePair(uout, sout) ? CapsLockMapping.SHIFTED : CapsLockMapping.UNSHIFTED;
			return outputToken(caps, uout, udead, sout, sdead);
		}
	}
	
	private static void writeKeyMapping(PrintWriter out, KeyboardMapping km, String keyCode, CapsLockMapping caps, int uout, DeadKeyTable udead, int sout, DeadKeyTable sdead, Map<String,DeadKeyTable> deadKeys, Map<Integer,Set<String>> deadKeyInputs) {
		if (caps == CapsLockMapping.UNSHIFTED) {
			writeKeyMapping(out, km, keyCode, uout, udead, deadKeys, deadKeyInputs);
		} else if (caps == CapsLockMapping.SHIFTED) {
			writeKeyMapping(out, km, keyCode, sout, sdead, deadKeys, deadKeyInputs);
		} else if (udead != null && sdead != null) {
			caps = isCasePair(udead.winTerminator, sdead.winTerminator) ? CapsLockMapping.SHIFTED : CapsLockMapping.UNSHIFTED;
			writeKeyMapping(out, km, keyCode, caps, uout, udead, sout, sdead, deadKeys, deadKeyInputs);
		} else if (udead != null) {
			caps = (udead.winTerminator == sout) ? CapsLockMapping.SHIFTED : CapsLockMapping.UNSHIFTED;
			writeKeyMapping(out, km, keyCode, caps, uout, udead, sout, sdead, deadKeys, deadKeyInputs);
		} else if (sdead != null) {
			caps = (sdead.winTerminator == uout) ? CapsLockMapping.SHIFTED : CapsLockMapping.UNSHIFTED;
			writeKeyMapping(out, km, keyCode, caps, uout, udead, sout, sdead, deadKeys, deadKeyInputs);
		} else {
			caps = isCasePair(uout, sout) ? CapsLockMapping.SHIFTED : CapsLockMapping.UNSHIFTED;
			writeKeyMapping(out, km, keyCode, caps, uout, udead, sout, sdead, deadKeys, deadKeyInputs);
		}
	}
	
	private static boolean isCasePair(int u, int s) {
		return (u != s) && (Character.toUpperCase(u) == s || Character.toLowerCase(s) == u);
	}
	
	private static void writeKeyMapping(PrintWriter out, KeyboardMapping km, String keyCode, int output, DeadKeyTable dead, Map<String,DeadKeyTable> deadKeys, Map<Integer,Set<String>> deadKeyInputs) {
		if (dead != null) {
			String dk = getActionId(km, getCP(output, dead));
			deadKeys.put(dk, dead);
			out.print("+ [" + keyCode + "] > dk(" + dk + ")\r\n");
		} else if (output > 0) {
			Set<String> dki = deadKeyInputs.get(output);
			if (dki == null) deadKeyInputs.put(output, (dki = new TreeSet<String>()));
			dki.add(keyCode);
			out.print("+ [" + keyCode + "] > " + quote(output) + "\r\n");
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
	
	private static String quote(int cp) {
		switch (cp) {
			case '\"': return "\'\"\'";
			case '\'': return "\"\'\"";
		}
		
		switch (Character.getType(cp)) {
			case Character.UPPERCASE_LETTER:
			case Character.LOWERCASE_LETTER:
			case Character.TITLECASE_LETTER:
			case Character.MODIFIER_LETTER:
			case Character.OTHER_LETTER:
			case Character.DECIMAL_DIGIT_NUMBER:
			case Character.LETTER_NUMBER:
			case Character.OTHER_NUMBER:
			case Character.CONNECTOR_PUNCTUATION:
			case Character.DASH_PUNCTUATION:
			case Character.START_PUNCTUATION:
			case Character.END_PUNCTUATION:
			case Character.INITIAL_QUOTE_PUNCTUATION:
			case Character.FINAL_QUOTE_PUNCTUATION:
			case Character.OTHER_PUNCTUATION:
			case Character.MATH_SYMBOL:
			case Character.CURRENCY_SYMBOL:
			case Character.MODIFIER_SYMBOL:
			case Character.OTHER_SYMBOL:
				return "'" + String.valueOf(Character.toChars(cp)) + "'";
		}
		
		String h = Integer.toHexString(cp);
		while (h.length() < 4) h = "0" + h;
		return "U+" + h.toUpperCase();
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
