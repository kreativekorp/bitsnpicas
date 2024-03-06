package com.kreative.keyedit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class KeyManTouchWriter {
	public static void write(File file, KeyboardMapping km) throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(fos, "UTF-8"), true);
		write(pw, km);
		pw.flush();
		pw.close();
		fos.close();
	}
	
	public static void write(PrintWriter out, KeyboardMapping km) {
		StringBuffer sb = new StringBuffer();
		write(sb, km);
		String s = sb.toString();
		s = s.replaceAll(",(\n *[\\}\\]])", "$1");
		out.print(s);
	}
	
	private static void write(StringBuffer out, KeyboardMapping km) {
		out.append("{\n");
		if (km.keymanPlatforms == null || km.keymanPlatforms.isEmpty()) {
			writeLayout(out, "  ", "tablet", km);
			writeLayout(out, "  ", "phone", km);
		} else {
			for (KeyManPlatform p : KeyManPlatform.values()) {
				if (km.keymanPlatforms.contains(p)) {
					writeLayout(out, "  ", p.toString(), km);
				}
			}
		}
		out.append("}");
	}
	
	private static void writeLayout(StringBuffer out, String prefix, String key, KeyboardMapping km) {
		out.append(prefix + quote(key) + ": {\n");
		out.append(prefix + "  \"displayUnderlying\": false,\n");
		out.append(prefix + "  \"layer\": [\n");
		String layerPrefix = prefix + "    ";
		writeLayer(out, layerPrefix, "default", false, false, km);
		writeLayer(out, layerPrefix, "shift", true, false, km);
		writeLayer(out, layerPrefix, "alt", false, true, km);
		writeLayer(out, layerPrefix, "shift-alt", true, true, km);
		out.append(prefix + "  ],\n");
		out.append(prefix + "},\n");
	}
	
	private static void writeLayer(StringBuffer out, String prefix, String id, boolean shift, boolean alt, KeyboardMapping km) {
		out.append(prefix + "{\n");
		out.append(prefix + "  \"id\": " + quote(id) + ",\n");
		out.append(prefix + "  \"row\": [\n");
		String rowPrefix = prefix + "    ";
		writeNumberRow(out, rowPrefix, shift, alt, km);
		writeTopRow(out, rowPrefix, shift, alt, km);
		writeHomeRow(out, rowPrefix, shift, alt, km);
		writeBottomRow(out, rowPrefix, shift, alt, km);
		writeFunctionRow(out, rowPrefix, shift, alt, km);
		out.append(prefix + "  ],\n");
		out.append(prefix + "},\n");
	}
	
	private static void writeNumberRow(StringBuffer out, String prefix, boolean shift, boolean alt, KeyboardMapping km) {
		out.append(prefix + "{\n");
		out.append(prefix + "  \"id\": 1,\n");
		out.append(prefix + "  \"key\": [\n");
		String keyPrefix = prefix + "    ";
		writeKeyMapping(out, keyPrefix, "K_1", 0, 0, shift, alt, km);
		writeKeyMapping(out, keyPrefix, "K_2", 0, 0, shift, alt, km);
		writeKeyMapping(out, keyPrefix, "K_3", 0, 0, shift, alt, km);
		writeKeyMapping(out, keyPrefix, "K_4", 0, 0, shift, alt, km);
		writeKeyMapping(out, keyPrefix, "K_5", 0, 0, shift, alt, km);
		writeKeyMapping(out, keyPrefix, "K_6", 0, 0, shift, alt, km);
		writeKeyMapping(out, keyPrefix, "K_7", 0, 0, shift, alt, km);
		writeKeyMapping(out, keyPrefix, "K_8", 0, 0, shift, alt, km);
		writeKeyMapping(out, keyPrefix, "K_9", 0, 0, shift, alt, km);
		writeKeyMapping(out, keyPrefix, "K_0", 0, 0, shift, alt, km);
		writeKeyMapping(out, keyPrefix, "K_HYPHEN", 0, 0, shift, alt, km);
		writeKeyMapping(out, keyPrefix, "K_EQUAL", 0, 0, shift, alt, km);
		writeKeyMapping(out, keyPrefix, "K_BKSP", "*BkSp*", null, 0, 100, true, null);
		out.append(prefix + "  ],\n");
		out.append(prefix + "},\n");
	}
	
	private static void writeTopRow(StringBuffer out, String prefix, boolean shift, boolean alt, KeyboardMapping km) {
		out.append(prefix + "{\n");
		out.append(prefix + "  \"id\": 2,\n");
		out.append(prefix + "  \"key\": [\n");
		String keyPrefix = prefix + "    ";
		writeKeyMapping(out, keyPrefix, "K_Q", 75, 0, shift, alt, km);
		writeKeyMapping(out, keyPrefix, "K_W", 0, 0, shift, alt, km);
		writeKeyMapping(out, keyPrefix, "K_E", 0, 0, shift, alt, km);
		writeKeyMapping(out, keyPrefix, "K_R", 0, 0, shift, alt, km);
		writeKeyMapping(out, keyPrefix, "K_T", 0, 0, shift, alt, km);
		writeKeyMapping(out, keyPrefix, "K_Y", 0, 0, shift, alt, km);
		writeKeyMapping(out, keyPrefix, "K_U", 0, 0, shift, alt, km);
		writeKeyMapping(out, keyPrefix, "K_I", 0, 0, shift, alt, km);
		writeKeyMapping(out, keyPrefix, "K_O", 0, 0, shift, alt, km);
		writeKeyMapping(out, keyPrefix, "K_P", 0, 0, shift, alt, km);
		writeKeyMapping(out, keyPrefix, "K_LBRKT", 0, 0, shift, alt, km);
		writeKeyMapping(out, keyPrefix, "K_RBRKT", 0, 0, shift, alt, km);
		out.append(prefix + "  ],\n");
		out.append(prefix + "},\n");
	}
	
	private static void writeHomeRow(StringBuffer out, String prefix, boolean shift, boolean alt, KeyboardMapping km) {
		out.append(prefix + "{\n");
		out.append(prefix + "  \"id\": 3,\n");
		out.append(prefix + "  \"key\": [\n");
		String keyPrefix = prefix + "    ";
		writeKeyMapping(out, keyPrefix, "K_BKQUOTE", 0, 0, shift, alt, km);
		writeKeyMapping(out, keyPrefix, "K_A", 0, 0, shift, alt, km);
		writeKeyMapping(out, keyPrefix, "K_S", 0, 0, shift, alt, km);
		writeKeyMapping(out, keyPrefix, "K_D", 0, 0, shift, alt, km);
		writeKeyMapping(out, keyPrefix, "K_F", 0, 0, shift, alt, km);
		writeKeyMapping(out, keyPrefix, "K_G", 0, 0, shift, alt, km);
		writeKeyMapping(out, keyPrefix, "K_H", 0, 0, shift, alt, km);
		writeKeyMapping(out, keyPrefix, "K_J", 0, 0, shift, alt, km);
		writeKeyMapping(out, keyPrefix, "K_K", 0, 0, shift, alt, km);
		writeKeyMapping(out, keyPrefix, "K_L", 0, 0, shift, alt, km);
		writeKeyMapping(out, keyPrefix, "K_COLON", 0, 0, shift, alt, km);
		writeKeyMapping(out, keyPrefix, "K_QUOTE", 0, 0, shift, alt, km);
		writeKeyMapping(out, keyPrefix, "K_BKSLASH", 0, 0, shift, alt, km);
		out.append(prefix + "  ],\n");
		out.append(prefix + "},\n");
	}
	
	private static void writeBottomRow(StringBuffer out, String prefix, boolean shift, boolean alt, KeyboardMapping km) {
		out.append(prefix + "{\n");
		out.append(prefix + "  \"id\": 4,\n");
		out.append(prefix + "  \"key\": [\n");
		String keyPrefix = prefix + "    ";
		if (km.keymanKey102) {
			writeKeyMapping(out, keyPrefix, "K_oE2", 0, 160, shift, alt, km);
		} else {
			writeKeyMapping(out, keyPrefix, "K_SHIFT", "*Shift*", null, 0, 160, true, null);
		}
		writeKeyMapping(out, keyPrefix, "K_Z", 0, 0, shift, alt, km);
		writeKeyMapping(out, keyPrefix, "K_X", 0, 0, shift, alt, km);
		writeKeyMapping(out, keyPrefix, "K_C", 0, 0, shift, alt, km);
		writeKeyMapping(out, keyPrefix, "K_V", 0, 0, shift, alt, km);
		writeKeyMapping(out, keyPrefix, "K_B", 0, 0, shift, alt, km);
		writeKeyMapping(out, keyPrefix, "K_N", 0, 0, shift, alt, km);
		writeKeyMapping(out, keyPrefix, "K_M", 0, 0, shift, alt, km);
		writeKeyMapping(out, keyPrefix, "K_COMMA", 0, 0, shift, alt, km);
		writeKeyMapping(out, keyPrefix, "K_PERIOD", 0, 0, shift, alt, km);
		writeKeyMapping(out, keyPrefix, "K_SLASH", 0, 0, shift, alt, km);
		writeKeyMapping(out, keyPrefix, "K_SHIFT", "*Shift*", null, 0, 155, true, null);
		out.append(prefix + "  ],\n");
		out.append(prefix + "},\n");
	}
	
	private static void writeFunctionRow(StringBuffer out, String prefix, boolean shift, boolean alt, KeyboardMapping km) {
		out.append(prefix + "{\n");
		out.append(prefix + "  \"id\": 5,\n");
		out.append(prefix + "  \"key\": [\n");
		String keyPrefix = prefix + "    ";
		String nextLayer = alt ? (shift ? "shift" : "default") : (shift ? "shift-alt" : "alt");
		writeKeyMapping(out, keyPrefix, "K_LCONTROL", "alt", null, 0, 130, true, nextLayer);
		writeKeyMapping(out, keyPrefix, "K_LOPT", "*Menu*", null, 0, 140, true, null);
		writeKeyMapping(out, keyPrefix, "K_SPACE", 0, 930, shift, alt, km);
		writeKeyMapping(out, keyPrefix, "K_ENTER", "*Enter*", null, 0, 235, true, null);
		out.append(prefix + "  ],\n");
		out.append(prefix + "},\n");
	}
	
	private static void writeKeyMapping(StringBuffer out, String prefix, String id, int pad, int width, boolean shift, boolean alt, KeyboardMapping km) {
		KeyMapping m = km.map.get(KeyManKey.forId(id).key);
		int[] lpo = alt ? (shift ? m.altShiftedLongPressOutput : m.altUnshiftedLongPressOutput) : (shift ? m.shiftedLongPressOutput : m.unshiftedLongPressOutput);
		DeadKeyTable dead = alt ? (shift ? m.altShiftedDeadKey : m.altUnshiftedDeadKey) : (shift ? m.shiftedDeadKey : m.unshiftedDeadKey);
		int output = alt ? (shift ? m.altShiftedOutput : m.altUnshiftedOutput) : (shift ? m.shiftedOutput : m.unshiftedOutput);
		int cp = (dead != null && dead.winTerminator > 0) ? dead.winTerminator : output;
		String text = (cp > 0) ? String.valueOf(Character.toChars(cp)) : null;
		List<Integer> sk = new ArrayList<Integer>();
		if (lpo == null) km.getAutoLongPressOutput(sk, cp);
		else for (int o : lpo) sk.add(o);
		writeKeyMapping(out, prefix, id, text, sk, pad, width, false, null);
	}
	
	private static void writeKeyMapping(StringBuffer out, String prefix, String id, String text, List<Integer> sk, int pad, int width, boolean sp, String nextlayer) {
		out.append(prefix + "{\n");
		if (id != null) out.append(prefix + "  \"id\": " + quote(id) + ",\n");
		if (text != null) out.append(prefix + "  \"text\": " + quote(text) + ",\n");
		if (sk != null && sk.size() > 0) {
			out.append(prefix + "  \"sk\": [\n");
			for (int cp : sk) {
				String skText = quote(String.valueOf(Character.toChars(cp)));
				String skId = Integer.toHexString(cp);
				while (skId.length() < 4) skId = "0" + skId;
				skId = quote("U_" + skId.toUpperCase());
				out.append(prefix + "    {\n");
				out.append(prefix + "      \"text\": " + skText + ",\n");
				out.append(prefix + "      \"id\": " + skId + ",\n");
				out.append(prefix + "    },\n");
			}
			out.append(prefix + "  ],\n");
		}
		if (pad > 0) out.append(prefix + "  \"pad\": " + pad + ",\n");
		if (width > 0) out.append(prefix + "  \"width\": " + width + ",\n");
		if (sp) out.append(prefix + "  \"sp\": 1,\n");
		if (nextlayer != null) out.append(prefix + "  \"nextlayer\": " + quote(nextlayer) + ",\n");
		out.append(prefix + "},\n");
	}
	
	private static String quote(String s) {
		if (s == null || s.length() == 0) return "\"\"";
		return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
	}
}
