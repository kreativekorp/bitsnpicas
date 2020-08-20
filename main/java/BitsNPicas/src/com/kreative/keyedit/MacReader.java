package com.kreative.keyedit;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class MacReader {
	public static KeyboardMapping read(File file) throws IOException {
		KeyboardMapping km = new KeyboardMapping();
		read(file, km);
		String iname = stripSuffix(file.getName(), ".keylayout");
		File ifile = new File(file.getParentFile(), iname + ".icns");
		if (ifile.isFile()) {
			FileInputStream ifis = new FileInputStream(ifile);
			DataInputStream idis = new DataInputStream(ifis);
			MacIconSuite icns = new MacIconSuite();
			icns.read(idis);
			km.icon = icns.getImage();
			km.macIconVersion = icns.getVersion();
			idis.close();
			ifis.close();
		}
		return km;
	}
	
	public static void read(File in, KeyboardMapping km) throws IOException {
		FileInputStream fis = new FileInputStream(in);
		read(in.getName(), fis, km);
		fis.close();
	}
	
	public static void read(String name, InputStream in, KeyboardMapping km) throws IOException {
		transform(MacKeyboardParser.parse(name, in), km);
	}
	
	public static void transform(MacKeyboard mk, KeyboardMapping km) {
		km.macGroupNumber = mk.group;
		km.macIdNumber = mk.id;
		km.name = mk.name;
		km.macActionIds.clear();
		
		MacKeyboard.Layout layout = getLayout(mk);
		if (layout == null) return;
		MacKeyboard.ModifierMap mm = mk.getModifierMap(layout.modifiers);
		if (mm == null) return;
		MacKeyboard.KeyMapSet kms = mk.getKeyMapSet(layout.mapSet);
		if (kms == null) return;
		
		// Get keymaps for the 6.2 states supported by KeyMapping.          ctrl   cmd    opt    shift  caps
		MacKeyboard.KeyMap unshifted    = kms.getKeyMap(mm.mapIndexForState(false, false, false, false, false));
		MacKeyboard.KeyMap capslock     = kms.getKeyMap(mm.mapIndexForState(false, false, false, false, true ));
		MacKeyboard.KeyMap shifted      = kms.getKeyMap(mm.mapIndexForState(false, false, false, true,  false));
		MacKeyboard.KeyMap altUnshifted = kms.getKeyMap(mm.mapIndexForState(false, false, true,  false, false));
		MacKeyboard.KeyMap altCapslock  = kms.getKeyMap(mm.mapIndexForState(false, false, true,  false, true ));
		MacKeyboard.KeyMap altShifted   = kms.getKeyMap(mm.mapIndexForState(false, false, true,  true,  false));
		MacKeyboard.KeyMap command      = kms.getKeyMap(mm.mapIndexForState(false, true,  false, false, false));
		MacKeyboard.KeyMap ctrl         = kms.getKeyMap(mm.mapIndexForState(true,  false, false, false, false));
		
		// Set KeyMappings according to each keymap.
		for (MacKeyboard.Key key : unshifted   ) new GetKey(km, mk, key).onUnshifted   (getKeyMapping(key, km));
		for (MacKeyboard.Key key : shifted     ) new GetKey(km, mk, key).onShifted     (getKeyMapping(key, km));
		for (MacKeyboard.Key key : altUnshifted) new GetKey(km, mk, key).onAltUnshifted(getKeyMapping(key, km));
		for (MacKeyboard.Key key : altShifted  ) new GetKey(km, mk, key).onAltShifted  (getKeyMapping(key, km));
		for (MacKeyboard.Key key : command     ) new GetKey(km, mk, key).onCommand     (getKeyMapping(key, km));
		for (MacKeyboard.Key key : ctrl        ) new GetKey(km, mk, key).onCtrl        (getKeyMapping(key, km));
		for (MacKeyboard.Key key : capslock    ) new GetCapsLockMapping(key, unshifted,    shifted   ).onCapsLock   (getKeyMapping(key, km));
		for (MacKeyboard.Key key : altCapslock ) new GetCapsLockMapping(key, altUnshifted, altShifted).onAltCapsLock(getKeyMapping(key, km));
	}
	
	private static KeyMapping getKeyMapping(MacKeyboard.Key key, KeyboardMapping km) {
		MacKey k = MacKey.forKeyCode(key.code);
		if (k == null || k.key == null) return null;
		return km.map.get(k.key);
	}
	
	private static class GetCapsLockMapping {
		private final CapsLockMapping mapping;
		public GetCapsLockMapping(MacKeyboard.Key key, MacKeyboard.KeyMap unshifted, MacKeyboard.KeyMap shifted) {
			MacKeyboard.Key unshiftedKey = unshifted.getKey(key.code);
			MacKeyboard.Key shiftedKey = shifted.getKey(key.code);
			if (unshiftedKey.equals(shiftedKey)) mapping = CapsLockMapping.AUTO;
			else if (key.equals(unshiftedKey)) mapping = CapsLockMapping.UNSHIFTED;
			else if (key.equals(shiftedKey)) mapping = CapsLockMapping.SHIFTED;
			else mapping = CapsLockMapping.AUTO;
		}
		public void onCapsLock(KeyMapping m) {
			if (m == null) return;
			m.capsLockMapping = mapping;
		}
		public void onAltCapsLock(KeyMapping m) {
			if (m == null) return;
			m.altCapsLockMapping = mapping;
		}
	}
	
	private static class GetKey {
		private int output = -1;
		private DeadKeyTable deadKey = null;
		public GetKey(KeyboardMapping km, MacKeyboard mk, MacKeyboard.Key key) {
			if (key.output != null && key.output.length() > 0) {
				output = key.output.codePointAt(0);
			}
			if (key.action != null && key.action.length() > 0) {
				MacKeyboard.Action action = getAction(mk, key.action);
				if (action != null) {
					for (MacKeyboard.When when : action) {
						if (when.state.equals("none")) {
							if (when.output != null && when.output.length() > 0) {
								output = when.output.codePointAt(0);
							}
							if (when.next != null && when.next.length() > 0) {
								deadKey = getDeadKeyTable(km, mk, when.next);
							}
						}
					}
				}
			}
		}
		public void onCommand(KeyMapping m) {
			if (m == null) return;
			m.commandOutput = output;
			m.commandDeadKey = deadKey;
		}
		public void onUnshifted(KeyMapping m) {
			if (m == null) return;
			m.unshiftedOutput = output;
			m.unshiftedDeadKey = deadKey;
		}
		public void onShifted(KeyMapping m) {
			if (m == null) return;
			m.shiftedOutput = output;
			m.shiftedDeadKey = deadKey;
		}
		public void onCtrl(KeyMapping m) {
			if (m == null) return;
			m.ctrlOutput = output;
			m.ctrlDeadKey = deadKey;
		}
		public void onAltUnshifted(KeyMapping m) {
			if (m == null) return;
			m.altUnshiftedOutput = output;
			m.altUnshiftedDeadKey = deadKey;
		}
		public void onAltShifted(KeyMapping m) {
			if (m == null) return;
			m.altShiftedOutput = output;
			m.altShiftedDeadKey = deadKey;
		}
	}
	
	private static MacKeyboard.Action getAction(MacKeyboard mk, String actionId) {
		for (MacKeyboard.Actions actions : mk.actions) {
			for (MacKeyboard.Action action : actions) {
				if (action.id.equals(actionId)) {
					return action;
				}
			}
		}
		return null;
	}
	
	private static DeadKeyTable getDeadKeyTable(KeyboardMapping km, MacKeyboard mk, String state) {
		String t = getTerminatorOutput(mk, state);
		if (t == null || t.length() == 0) return null;
		DeadKeyTable dkt = new DeadKeyTable(t.codePointAt(0));
		dkt.macStateId = state;
		for (MacKeyboard.Actions actions : mk.actions) {
			for (MacKeyboard.Action action : actions) {
				int noneOutput = -1;
				int deadOutput = -1;
				for (MacKeyboard.When when : action) {
					String o = when.output;
					if (o == null || o.length() == 0) continue;
					if (when.state.equals("none")) noneOutput = o.codePointAt(0);
					if (when.state.equals(state)) deadOutput = o.codePointAt(0);
				}
				if (noneOutput > 0 && deadOutput > 0) {
					dkt.keyMap.put(noneOutput, deadOutput);
					km.macActionIds.put(noneOutput, action.id);
				}
			}
		}
		return dkt;
	}
	
	private static String getTerminatorOutput(MacKeyboard mk, String state) {
		for (MacKeyboard.Terminators terms : mk.terminators) {
			for (MacKeyboard.When when : terms) {
				if (when.state.equals(state)) {
					return when.output;
				}
			}
		}
		return null;
	}
	
	private static MacKeyboard.Layout getLayout(MacKeyboard mk) {
		for (MacKeyboard.Layouts layouts : mk.layouts) {
			for (MacKeyboard.Layout layout : layouts) {
				return layout;
			}
		}
		return null;
	}
	
	private static String stripSuffix(String s, String suffix) {
		if (s.toLowerCase().endsWith(suffix.toLowerCase())) {
			return s.substring(0, s.length() - suffix.length());
		} else {
			return s;
		}
	}
}
