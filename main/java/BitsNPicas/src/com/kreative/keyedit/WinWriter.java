package com.kreative.keyedit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;
import com.kreative.bitsnpicas.unicode.CharacterData;
import com.kreative.bitsnpicas.unicode.CharacterDatabase;

public class WinWriter {
	private static final String NONBMP_WARNING = "WARNING: Supplemental characters are assigned to altgr or dead keys.\nThese won't work natively on Windows. A third-party input method must be used.";
	
	public static void write(File file, KeyboardMapping km) throws IOException {
		if (!km.isWindowsNativeCompatible()) System.err.println(NONBMP_WARNING);
		FileOutputStream fos = new FileOutputStream(file);
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(fos, "UTF-16LE"), true);
		write(pw, km);
		pw.flush();
		pw.close();
		fos.close();
	}
	
	public static void write(PrintWriter out, KeyboardMapping km) {
		out.print("\uFEFF");
		out.print("KBD\t" + km.getWinIdentifierNotEmpty() + "\t\"" + km.getNameNotEmpty() + "\"\r\n\r\n");
		out.print("COPYRIGHT\t\"" + km.getWinCopyrightNotEmpty() + "\"\r\n\r\n");
		out.print("COMPANY\t\"" + km.getWinCompanyNotEmpty() + "\"\r\n\r\n");
		out.print("LOCALENAME\t\"" + km.getWinLocaleNotNull().tag + "\"\r\n\r\n");
		out.print("LOCALEID\t\"" + hex(km.getWinLocaleNotNull().lcid,8) + "\"\r\n\r\n");
		out.print("VERSION\t1.0\r\n\r\n");
		
		if (km.winAltGrEnable || km.winShiftLock || km.winLrmRlm) {
			out.print("ATTRIBUTES\r\n");
			if (km.winAltGrEnable) out.print("ALTGR\r\n");
			if (km.winShiftLock) out.print("SHIFTLOCK\r\n");
			if (km.winLrmRlm) out.print("LRM_RLM\r\n");
			out.print("\r\n");
		}
		
		out.print("SHIFTSTATE\r\n");
		out.print("\r\n");
		out.print("0\t//Column 4\r\n");
		out.print("1\t//Column 5 : Shft\r\n");
		out.print("2\t//Column 6 :       Ctrl\r\n");
		out.print("6\t//Column 7 :       Ctrl Alt\r\n");
		out.print("7\t//Column 8 : Shft  Ctrl Alt\r\n");
		out.print("\r\n");
		
		out.print("LAYOUT\t\t;an extra '@' at the end is a dead key\r\n");
		out.print("\r\n");
		out.print("//SC\tVK_\t\tCap\t0\t1\t2\t6\t7\r\n");
		out.print("//--\t----\t\t----\t----\t----\t----\t----\t----\r\n");
		out.print("\r\n");
		
		Map<Integer,DeadKeyTable> deadKeys = new LinkedHashMap<Integer,DeadKeyTable>();
		for (WinKey k : WinKey.KEYS) {
			KeyMapping m = km.map.get(k.key);
			int u = getCP(m.unshiftedOutput, m.unshiftedDeadKey);
			int s = getCP(m.shiftedOutput, m.shiftedDeadKey);
			int c = getCP(m.ctrlOutput, m.ctrlDeadKey);
			int au = getCP(m.altUnshiftedOutput, m.altUnshiftedDeadKey);
			int as = getCP(m.altShiftedOutput, m.altShiftedDeadKey);
			int caps = 0;
			if (capsLockShifted(m.capsLockMapping,    u,  s )) caps |= 1;
			if (capsLockShifted(m.altCapsLockMapping, au, as)) caps |= 4;
			if (!(c == 0x1B || c == 0x1C || c == 0x1D || c == 0x20)) c = -1;
			
			out.print(hex(k.scanCode,2) + "\t");
			out.print(k.vkConstant + ((k.vkConstant.length() >= 6) ? "\t" : "\t\t"));
			out.print(caps + "\t");
			out.print(cpString(u, m.unshiftedDeadKey) + "\t");
			out.print(cpString(s, m.shiftedDeadKey) + "\t");
			out.print(cpString(c, m.ctrlDeadKey) + "\t");
			out.print(cpString(au, m.altUnshiftedDeadKey) + "\t");
			out.print(cpString(as, m.altShiftedDeadKey) + "\t");
			out.print("\t// ");
			out.print(uniString(u) + ", ");
			out.print(uniString(s) + ", ");
			out.print(uniString(c) + ", ");
			out.print(uniString(au) + ", ");
			out.print(uniString(as) + "\r\n");
			
			if (u > 0 && m.unshiftedDeadKey != null) deadKeys.put(u, m.unshiftedDeadKey);
			if (s > 0 && m.shiftedDeadKey != null) deadKeys.put(s, m.shiftedDeadKey);
			if (c > 0 && m.ctrlDeadKey != null) deadKeys.put(c, m.ctrlDeadKey);
			if (au > 0 && m.altUnshiftedDeadKey != null) deadKeys.put(au, m.altUnshiftedDeadKey);
			if (as > 0 && m.altShiftedDeadKey != null) deadKeys.put(as, m.altShiftedDeadKey);
		}
		
		if (!deadKeys.isEmpty()) {
			out.print("\r\n");
			for (Map.Entry<Integer,DeadKeyTable> e : deadKeys.entrySet()) {
				out.print("\r\n");
				out.print("DEADKEY\t" + cpString(e.getKey(), null) + "\r\n");
				out.print("\r\n");
				for (Map.Entry<Integer,Integer> ee : e.getValue().keyMap.entrySet()) {
					out.print(hex(ee.getKey(),4) + "\t");
					out.print(hex(ee.getValue(),4) + "\t");
					out.print("// ");
					out.print(Character.toChars(ee.getKey()));
					out.print(" -> ");
					out.print(Character.toChars(ee.getValue()));
					out.print("\r\n");
				}
			}
		}
		
		out.print("\r\n");
		out.print("\r\n");
		out.print("KEYNAME\r\n");
		out.print("\r\n");
		for (String s : KEYNAME) out.print(s);
		out.print("\r\n");
		out.print("KEYNAME_EXT\r\n");
		out.print("\r\n");
		for (String s : KEYNAME_EXT) out.print(s);
		
		if (!deadKeys.isEmpty()) {
			out.print("\r\n");
			out.print("KEYNAME_DEAD\r\n");
			out.print("\r\n");
			for (Map.Entry<Integer,DeadKeyTable> e : deadKeys.entrySet()) {
				out.print(cpString(e.getKey(), null) + "\t");
				out.print("\"" + uniString(e.getKey()) + "\"");
				out.print("\r\n");
			}
			out.print("\r\n");
		}
		
		out.print("\r\n");
		out.print("DESCRIPTIONS\r\n");
		out.print("\r\n");
		out.print(hex(km.getWinLocaleNotNull().lcid,4) + "\t" + km.getNameNotEmpty() + "\r\n");
		out.print("\r\n");
		out.print("LANGUAGENAMES\r\n");
		out.print("\r\n");
		out.print(hex(km.getWinLocaleNotNull().lcid,4) + "\t" + km.getWinLocaleNotNull().name + "\r\n");
		out.print("\r\n");
		out.print("ENDKBD\r\n");
	}
	
	private static String hex(int v, int n) {
		String h = Integer.toHexString(v);
		while (h.length() < n) h = "0" + h;
		return h;
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
	
	private static boolean isCasePair(int u, int s) {
		return (u != s) && (Character.toUpperCase(u) == s || Character.toLowerCase(s) == u);
	}
	
	private static boolean capsLockShifted(CapsLockMapping m, int u, int s) {
		if (m == CapsLockMapping.UNSHIFTED) return false;
		if (m == CapsLockMapping.SHIFTED) return true;
		return isCasePair(u, s);
	}
	
	private static String cpString(int cp, DeadKeyTable dead) {
		if (cp <= 0) return "-1";
		String h;
		if (cp >= '0' && cp <= '9') h = Character.toString((char)cp);
		else if (cp >= 'A' && cp <= 'Z') h = Character.toString((char)cp);
		else if (cp >= 'a' && cp <= 'z') h = Character.toString((char)cp);
		else h = hex(cp,4);
		if (dead != null) h += "@";
		return h;
	}
	
	private static String uniString(int cp) {
		if (cp <= 0) return "<none>";
		if (cp >= 0xE000 && cp < 0xF900) return "Private Use";
		if (cp >= 0xF0000) return "Private Use";
		CharacterData cd = CharacterDatabase.instance().get(cp);
		if (cd == null) return "<null>";
		return cd.toString();
	}
	
	private static final String[] KEYNAME = {
		"01\tEsc\r\n",
		"0e\tBackspace\r\n",
		"0f\tTab\r\n",
		"1c\tEnter\r\n",
		"1d\tCtrl\r\n",
		"2a\tShift\r\n",
		"36\t\"Right Shift\"\r\n",
		"37\t\"Num *\"\r\n",
		"38\tAlt\r\n",
		"39\tSpace\r\n",
		"3a\t\"Caps Lock\"\r\n",
		"3b\tF1\r\n",
		"3c\tF2\r\n",
		"3d\tF3\r\n",
		"3e\tF4\r\n",
		"3f\tF5\r\n",
		"40\tF6\r\n",
		"41\tF7\r\n",
		"42\tF8\r\n",
		"43\tF9\r\n",
		"44\tF10\r\n",
		"45\tPause\r\n",
		"46\t\"Scroll Lock\"\r\n",
		"47\t\"Num 7\"\r\n",
		"48\t\"Num 8\"\r\n",
		"49\t\"Num 9\"\r\n",
		"4a\t\"Num -\"\r\n",
		"4b\t\"Num 4\"\r\n",
		"4c\t\"Num 5\"\r\n",
		"4d\t\"Num 6\"\r\n",
		"4e\t\"Num +\"\r\n",
		"4f\t\"Num 1\"\r\n",
		"50\t\"Num 2\"\r\n",
		"51\t\"Num 3\"\r\n",
		"52\t\"Num 0\"\r\n",
		"53\t\"Num Del\"\r\n",
		"54\t\"Sys Req\"\r\n",
		"57\tF11\r\n",
		"58\tF12\r\n",
		"7c\tF13\r\n",
		"7d\tF14\r\n",
		"7e\tF15\r\n",
		"7f\tF16\r\n",
		"80\tF17\r\n",
		"81\tF18\r\n",
		"82\tF19\r\n",
		"83\tF20\r\n",
		"84\tF21\r\n",
		"85\tF22\r\n",
		"86\tF23\r\n",
		"87\tF24\r\n",
	};
	
	private static final String[] KEYNAME_EXT = {
		"1c\t\"Num Enter\"\r\n",
		"1d\t\"Right Ctrl\"\r\n",
		"35\t\"Num /\"\r\n",
		"37\t\"Prnt Scrn\"\r\n",
		"38\t\"Right Alt\"\r\n",
		"45\t\"Num Lock\"\r\n",
		"46\tBreak\r\n",
		"47\tHome\r\n",
		"48\tUp\r\n",
		"49\t\"Page Up\"\r\n",
		"4b\tLeft\r\n",
		"4d\tRight\r\n",
		"4f\tEnd\r\n",
		"50\tDown\r\n",
		"51\t\"Page Down\"\r\n",
		"52\tInsert\r\n",
		"53\tDelete\r\n",
		"54\t<00>\r\n",
		"56\tHelp\r\n",
		"5b\t\"Left Windows\"\r\n",
		"5c\t\"Right Windows\"\r\n",
		"5d\tApplication\r\n",
	};
}
