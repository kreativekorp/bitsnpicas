package com.kreative.keyedit;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class WinReader {
	public static KeyboardMapping read(File file) throws IOException {
		KeyboardMapping km = new KeyboardMapping();
		read(file, km);
		return km;
	}
	
	public static void read(File in, KeyboardMapping km) throws IOException {
		Scanner scan = new Scanner(new FileInputStream(in), "UTF-16LE");
		read(scan, km);
		scan.close();
	}
	
	public static void read(Scanner in, KeyboardMapping km) {
		Map<Integer,DeadKeyTable> deadKeys = new HashMap<Integer,DeadKeyTable>();
		boolean inLayout = false;
		DeadKeyTable inDeadKey = null;
		km.winAltGrEnable = false;
		km.winShiftLock = false;
		km.winLrmRlm = false;
		while (in.hasNextLine()) {
			String line = in.nextLine().trim();
			int o = line.indexOf("//");
			if (o >= 0) line = line.substring(0, o).trim();
			if (line.length() == 0) continue;
			String[] fields = line.split("\t+");
			String cmd = fields[0].trim();
			if (cmd.equals("\uFEFFKBD") || cmd.equals("KBD")) {
				if (fields.length > 1) km.winIdentifier = fields[1].trim();
				if (fields.length > 2) km.name = sq(fields[2].trim());
			} else if (cmd.equals("COPYRIGHT")) {
				if (fields.length > 1) km.winCopyright = sq(fields[1].trim());
			} else if (cmd.equals("COMPANY")) {
				if (fields.length > 1) km.winCompany = sq(fields[1].trim());
			} else if (cmd.equals("LOCALENAME")) {
				if (fields.length > 1) km.winLocale = WinLocale.forTag(sq(fields[1].trim()), km.winLocale);
			} else if (cmd.equals("ALTGR")) {
				km.winAltGrEnable = true;
			} else if (cmd.equals("SHIFTLOCK")) {
				km.winShiftLock = true;
			} else if (cmd.equals("LRM_RLM")) {
				km.winLrmRlm = true;
			} else if (cmd.equals("LAYOUT")) {
				inLayout = true;
				inDeadKey = null;
			} else if (cmd.equals("DEADKEY")) {
				inLayout = false;
				inDeadKey = (fields.length > 1) ? deadKeys.get(parseCP(fields[1].trim())) : null;
			} else if (cmd.equals("KEYNAME")) {
				inLayout = false;
				inDeadKey = null;
			} else if (inLayout) {
				WinKey k = WinKey.forScanCode(parseHex(cmd));
				if (k != null) {
					KeyMapping m = km.map.get(k.key);
					if (fields.length > 2) {
						int caps = parseHex(fields[2].trim());
						m.capsLockMapping    = ((caps & 1) == 0) ? CapsLockMapping.UNSHIFTED : CapsLockMapping.SHIFTED;
						m.altCapsLockMapping = ((caps & 4) == 0) ? CapsLockMapping.UNSHIFTED : CapsLockMapping.SHIFTED;
					}
					if (fields.length > 3) new ParseCPDK(fields[3].trim(), deadKeys).onUnshifted(m);
					if (fields.length > 4) new ParseCPDK(fields[4].trim(), deadKeys).onShifted(m);
					if (fields.length > 5) new ParseCPDK(fields[5].trim(), deadKeys).onCtrl(m);
					if (fields.length > 6) new ParseCPDK(fields[6].trim(), deadKeys).onAltUnshifted(m);
					if (fields.length > 7) new ParseCPDK(fields[7].trim(), deadKeys).onAltShifted(m);
				}
			} else if (inDeadKey != null) {
				int cp1 = (fields.length > 0) ? parseCP(fields[0].trim()) : -1;
				int cp2 = (fields.length > 1) ? parseCP(fields[1].trim()) : -1;
				if (cp1 > 0 && cp2 > 0) inDeadKey.keyMap.put(cp1, cp2);
			}
		}
	}
	
	private static String sq(String s) {
		if (s.length() >= 2 && s.startsWith("\"") && s.endsWith("\"")) {
			return s.substring(1, s.length() - 1);
		} else {
			return s;
		}
	}
	
	private static int parseCP(String s) {
		if (s == null) return -1;
		if (s.length() == 0) return -1;
		if (s.length() == 1) return s.charAt(0);
		return parseHex(s);
	}
	
	private static int parseHex(String s) {
		try { return Integer.parseInt(s, 16); }
		catch (NumberFormatException nfe) { return -1; }
	}
	
	private static class ParseCPDK {
		private int output = -1;
		private DeadKeyTable deadKey = null;
		public ParseCPDK(String s, Map<Integer,DeadKeyTable> dk) {
			if (s.endsWith("@")) {
				s = s.substring(0, s.length()-1).trim();
				output = parseCP(s);
				deadKey = new DeadKeyTable(output);
				dk.put(output, deadKey);
			} else {
				output = parseCP(s);
				deadKey = null;
			}
		}
		public void onUnshifted(KeyMapping m) {
			m.unshiftedOutput = output;
			m.unshiftedDeadKey = deadKey;
		}
		public void onShifted(KeyMapping m) {
			m.shiftedOutput = output;
			m.shiftedDeadKey = deadKey;
		}
		public void onCtrl(KeyMapping m) {
			m.ctrlOutput = output;
			m.ctrlDeadKey = deadKey;
		}
		public void onAltUnshifted(KeyMapping m) {
			m.altUnshiftedOutput = output;
			m.altUnshiftedDeadKey = deadKey;
		}
		public void onAltShifted(KeyMapping m) {
			m.altShiftedOutput = output;
			m.altShiftedDeadKey = deadKey;
		}
	}
}
