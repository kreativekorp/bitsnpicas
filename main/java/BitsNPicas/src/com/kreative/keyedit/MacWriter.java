package com.kreative.keyedit;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MacWriter {
	public static void write(File file, KeyboardMapping km) throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(fos, "UTF-8"), true);
		write(pw, km);
		pw.flush();
		pw.close();
		fos.close();
		if (km.icon != null) {
			String iname = stripSuffix(file.getName(), ".keylayout");
			File ifile = new File(file.getParentFile(), iname + ".icns");
			FileOutputStream ifos = new FileOutputStream(ifile);
			DataOutputStream idos = new DataOutputStream(ifos);
			MacIconSuite icns = new MacIconSuite();
			icns.putImage(km.icon);
			icns.putVersion(km.macIconVersion);
			icns.write(idos);
			idos.flush();
			idos.close();
			ifos.close();
		}
	}
	
	public static void write(PrintWriter out, KeyboardMapping km) {
		out.print(transform(km).toString());
	}
	
	public static MacKeyboard transform(KeyboardMapping km) {
		MacKeyboard mk = new MacKeyboard(km.macGroupNumber, km.macIdNumber, km.getNameNotEmpty(), 1);
		
		// Create actions, terminators, and output map for dead keys.
		Map<Integer,Map<String,Integer>> dko = new TreeMap<Integer,Map<String,Integer>>();
		for (KeyMapping k : km.map.values()) txDeadKey(k.unshiftedDeadKey, mk, dko);
		for (KeyMapping k : km.map.values()) txDeadKey(k.shiftedDeadKey, mk, dko);
		for (KeyMapping k : km.map.values()) txDeadKey(k.altUnshiftedDeadKey, mk, dko);
		for (KeyMapping k : km.map.values()) txDeadKey(k.altShiftedDeadKey, mk, dko);
		for (KeyMapping k : km.map.values()) txDeadKey(k.commandDeadKey, mk, dko);
		for (KeyMapping k : km.map.values()) txDeadKey(k.ctrlDeadKey, mk, dko);
		
		// Create actions for dead key output map.
		for (Map.Entry<Integer,Map<String,Integer>> e : dko.entrySet()) {
			String actionId = getActionId(km, e.getKey());
			MacKeyboard.Action action = new MacKeyboard.Action(actionId);
			for (Map.Entry<String,Integer> f : e.getValue().entrySet()) {
				String state = f.getKey();
				String output = outputString(f.getValue());
				action.putWhen(new MacKeyboard.When(state, null, output, null, null));
			}
			mk.actions().putAction(action);
		}
		
		// Create keymaps.
		List<MacKeyboard.Key> unshifted = new ArrayList<MacKeyboard.Key>();
		List<MacKeyboard.Key> capslock = new ArrayList<MacKeyboard.Key>();
		List<MacKeyboard.Key> shifted = new ArrayList<MacKeyboard.Key>();
		List<MacKeyboard.Key> altUnshifted = new ArrayList<MacKeyboard.Key>();
		List<MacKeyboard.Key> altCapslock = new ArrayList<MacKeyboard.Key>();
		List<MacKeyboard.Key> altShifted = new ArrayList<MacKeyboard.Key>();
		List<MacKeyboard.Key> command = new ArrayList<MacKeyboard.Key>();
		List<MacKeyboard.Key> commandOption = new ArrayList<MacKeyboard.Key>();
		List<MacKeyboard.Key> commandOptionShift = new ArrayList<MacKeyboard.Key>();
		List<MacKeyboard.Key> ctrl = new ArrayList<MacKeyboard.Key>();
		for (MacKey key : MacKey.KEYS) {
			if (key.key == null) {
				String output = outputString(key.output);
				MacKeyboard.Key k = new MacKeyboard.Key(key.keyCode, output, null);
				unshifted.add(k);
				capslock.add(k);
				shifted.add(k);
				altUnshifted.add(k);
				altCapslock.add(k);
				altShifted.add(k);
				command.add(k);
				commandOption.add(k);
				commandOptionShift.add(k);
				ctrl.add(k);
			} else {
				KeyMapping m = km.map.get(key.key);
				TxKeyMemo defUnshiftedKM = txKey(key, key.key.defaultUnshifted, null, null, km, dko);
				TxKeyMemo defShiftedKM = txKey(key, key.key.defaultShifted, null, null, km, dko);
				TxKeyMemo defCtrlKM = txKey(key, key.key.defaultCtrl, null, defUnshiftedKM, km, dko);
				TxKeyMemo unshiftedKM = txKey(key, m.unshiftedOutput, m.unshiftedDeadKey, defUnshiftedKM, km, dko);
				TxKeyMemo shiftedKM = txKey(key, m.shiftedOutput, m.shiftedDeadKey, defShiftedKM, km, dko);
				TxKeyMemo capslockKM = capsLockKeyMemo(m.capsLockMapping, unshiftedKM, shiftedKM);
				TxKeyMemo altUnshiftedKM = txKey(key, m.altUnshiftedOutput, m.altUnshiftedDeadKey, unshiftedKM, km, dko);
				TxKeyMemo altShiftedKM = txKey(key, m.altShiftedOutput, m.altShiftedDeadKey, shiftedKM, km, dko);
				TxKeyMemo altCapslockKM = capsLockKeyMemo(m.altCapsLockMapping, altUnshiftedKM, altShiftedKM);
				TxKeyMemo commandKM = txKey(key, m.commandOutput, m.commandDeadKey, defUnshiftedKM, km, dko);
				TxKeyMemo commandOptionKM = undeadKeyMemo(altUnshiftedKM, km, dko);
				TxKeyMemo commandOptionShiftKM = undeadKeyMemo(altShiftedKM, km, dko);
				TxKeyMemo ctrlKM = txKey(key, m.ctrlOutput, m.ctrlDeadKey, defCtrlKM, km, dko);
				unshifted.add(unshiftedKM.key);
				capslock.add(capslockKM.key);
				shifted.add(shiftedKM.key);
				altUnshifted.add(altUnshiftedKM.key);
				altCapslock.add(altCapslockKM.key);
				altShifted.add(altShiftedKM.key);
				command.add(commandKM.key);
				commandOption.add(commandOptionKM.key);
				commandOptionShift.add(commandOptionShiftKM.key);
				ctrl.add(ctrlKM.key);
			}
		}
		
		// Dedupe keymaps.
		List<List<MacKeyboard.Key>> keymaps = new ArrayList<List<MacKeyboard.Key>>();
		int unshiftedIndex = addKeyMap(keymaps, unshifted);
		int capslockIndex = addKeyMap(keymaps, capslock);
		int shiftedIndex = addKeyMap(keymaps, shifted);
		int altUnshiftedIndex = addKeyMap(keymaps, altUnshifted);
		int altCapslockIndex = addKeyMap(keymaps, altCapslock);
		int altShiftedIndex = addKeyMap(keymaps, altShifted);
		int commandIndex = addKeyMap(keymaps, command);
		int commandOptionIndex = addKeyMap(keymaps, commandOption);
		int commandOptionShiftIndex = addKeyMap(keymaps, commandOptionShift);
		int ctrlIndex = addKeyMap(keymaps, ctrl);
		
		// Create modifier map and key map set.
		MacKeyboard.ModifierMap mm = new MacKeyboard.ModifierMap("modifiers", unshiftedIndex);
		MacKeyboard.KeyMapSet kms = new MacKeyboard.KeyMapSet("ANSI");
		for (int i = 0, n = keymaps.size(); i < n; i++) {
			MacKeyboard.KeyMapSelect kmsel = new MacKeyboard.KeyMapSelect(i);
			if (unshiftedIndex == i) kmsel.add(MacKeyboard.Modifier.UNSHIFTED);
			if (capslockIndex == i) kmsel.add(MacKeyboard.Modifier.CAPSLOCK);
			if (shiftedIndex == i) kmsel.add(MacKeyboard.Modifier.SHIFTED);
			if (altUnshiftedIndex == i) kmsel.add(MacKeyboard.Modifier.ALT_UNSHIFTED);
			if (altCapslockIndex == i) kmsel.add(MacKeyboard.Modifier.ALT_CAPSLOCK);
			if (altShiftedIndex == i) kmsel.add(MacKeyboard.Modifier.ALT_SHIFTED);
			if (commandIndex == i) kmsel.add(MacKeyboard.Modifier.COMMAND);
			if (commandOptionIndex == i) kmsel.add(MacKeyboard.Modifier.COMMAND_OPTION);
			if (commandOptionShiftIndex == i) kmsel.add(MacKeyboard.Modifier.COMMAND_OPTION_SHIFT);
			if (ctrlIndex == i) kmsel.add(MacKeyboard.Modifier.CTRL);
			mm.putKeyMapSelect(kmsel);
			MacKeyboard.KeyMap map = new MacKeyboard.KeyMap(i);
			map.addAll(keymaps.get(i));
			kms.putKeyMap(map);
		}
		mk.putModifierMap(mm);
		mk.putKeyMapSet(kms);
		
		mk.layouts().putLayout(new MacKeyboard.Layout(0, 255, mm.id, kms.id));
		return mk;
	}
	
	private static int addKeyMap(List<List<MacKeyboard.Key>> keymaps, List<MacKeyboard.Key> keymap) {
		int i = keymaps.indexOf(keymap);
		if (i >= 0) return i;
		i = keymaps.size();
		keymaps.add(keymap);
		return i;
	}
	
	private static TxKeyMemo undeadKeyMemo(TxKeyMemo possiblyDead, KeyboardMapping km, Map<Integer,Map<String,Integer>> dko) {
		if (possiblyDead.deadKey == null) return possiblyDead;
		int code = possiblyDead.key.code;
		int out = possiblyDead.deadKey.macTerminator;
		if (dko.containsKey(out)) {
			String actionId = getActionId(km, out);
			MacKeyboard.Key k = new MacKeyboard.Key(code, null, actionId);
			return new TxKeyMemo(out, null, k);
		} else {
			String output = outputString(out);
			MacKeyboard.Key k = new MacKeyboard.Key(code, output, null);
			return new TxKeyMemo(out, null, k);
		}
	}
	
	private static TxKeyMemo capsLockKeyMemo(CapsLockMapping caps, TxKeyMemo unshifted, TxKeyMemo shifted) {
		if (caps == CapsLockMapping.UNSHIFTED) {
			return unshifted;
		} else if (caps == CapsLockMapping.SHIFTED) {
			return shifted;
		} else if (unshifted.deadKey != null && shifted.deadKey != null) {
			return isCasePair(unshifted.deadKey.macTerminator, shifted.deadKey.macTerminator) ? shifted : unshifted;
		} else if (unshifted.deadKey != null) {
			return (unshifted.deadKey.macTerminator == shifted.output) ? shifted : unshifted;
		} else if (shifted.deadKey != null) {
			return (shifted.deadKey.macTerminator == unshifted.output) ? shifted : unshifted;
		} else {
			return isCasePair(unshifted.output, shifted.output) ? shifted : unshifted;
		}
	}
	
	private static boolean isCasePair(int u, int s) {
		return (u != s) && (Character.toUpperCase(u) == s || Character.toLowerCase(s) == u);
	}
	
	private static TxKeyMemo txKey(
		MacKey key, int out, DeadKeyTable dead, TxKeyMemo def,
		KeyboardMapping km, Map<Integer,Map<String,Integer>> dko
	) {
		if (dead != null) {
			MacKeyboard.Key k = new MacKeyboard.Key(key.keyCode, null, dead.macStateId);
			return new TxKeyMemo(out, dead, k);
		}
		if (out > 0) {
			if (dko.containsKey(out)) {
				String actionId = getActionId(km, out);
				MacKeyboard.Key k = new MacKeyboard.Key(key.keyCode, null, actionId);
				return new TxKeyMemo(out, dead, k);
			} else {
				String output = outputString(out);
				MacKeyboard.Key k = new MacKeyboard.Key(key.keyCode, output, null);
				return new TxKeyMemo(out, dead, k);
			}
		}
		return def;
	}
	
	private static class TxKeyMemo {
		public final int output;
		public final DeadKeyTable deadKey;
		public final MacKeyboard.Key key;
		public TxKeyMemo(int output, DeadKeyTable deadKey, MacKeyboard.Key key) {
			this.output = output;
			this.deadKey = deadKey;
			this.key = key;
		}
	}
	
	private static String getActionId(KeyboardMapping km, int cp) {
		if (km.macActionIds.containsKey(cp)) {
			return km.macActionIds.get(cp);
		} else {
			return XkbKeySym.MAP.getKeySym(cp);
		}
	}
	
	private static void txDeadKey(DeadKeyTable dkt, MacKeyboard mk, Map<Integer,Map<String,Integer>> dko) {
		if (dkt != null) {
			// If there is no state name invent one.
			String state = dkt.macStateId;
			if (state == null || state.length() == 0) {
				state = XkbKeySym.MAP.getKeySym(dkt.macTerminator);
				dkt.macStateId = state;
			}
			
			// Add action for dead key.
			MacKeyboard.Action action = new MacKeyboard.Action(state);
			action.putWhen(new MacKeyboard.When("none", null, null, null, state));
			mk.actions().putAction(action);
			
			// Add terminator for dead key.
			String output = outputString(dkt.macTerminator);
			mk.terminators().putWhen(new MacKeyboard.When(state, null, output, null, null));
			
			// Add dead key outputs to dead key output map.
			for (Map.Entry<Integer,Integer> e : dkt.keyMap.entrySet()) {
				Map<String,Integer> dka = dko.get(e.getKey());
				if (dka == null) {
					dka = new LinkedHashMap<String,Integer>();
					dka.put("none", e.getKey());
					dko.put(e.getKey(), dka);
				}
				dka.put(state, e.getValue());
			}
		}
	}
	
	private static String outputString(int cp) {
		return (cp > 0) ? String.valueOf(Character.toChars(cp)) : null;
	}
	
	private static String stripSuffix(String s, String suffix) {
		if (s.toLowerCase().endsWith(suffix.toLowerCase())) {
			return s.substring(0, s.length() - suffix.length());
		} else {
			return s;
		}
	}
}
