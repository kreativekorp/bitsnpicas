package com.kreative.keyedit;

import java.util.ArrayList;
import java.util.List;

public class MacKeyboard {
	public final int group;
	public final int id;
	public final String name;
	public final int maxout;
	public final List<Layouts> layouts;
	public final List<ModifierMap> modifierMaps;
	public final List<KeyMapSet> keyMapSets;
	public final List<Actions> actions;
	public final List<Terminators> terminators;
	
	public MacKeyboard(int group, int id, String name, int maxout) {
		this.group = group;
		this.id = id;
		this.name = name;
		this.maxout = maxout;
		this.layouts = new ArrayList<Layouts>();
		this.modifierMaps = new ArrayList<ModifierMap>();
		this.keyMapSets = new ArrayList<KeyMapSet>();
		this.actions = new ArrayList<Actions>();
		this.terminators = new ArrayList<Terminators>();
	}
	
	public Layouts layouts() {
		if (this.layouts.isEmpty()) {
			Layouts layouts = new Layouts();
			this.layouts.add(layouts);
			return layouts;
		} else {
			int i = this.layouts.size() - 1;
			return this.layouts.get(i);
		}
	}
	
	public ModifierMap getModifierMap(String id) {
		for (ModifierMap modifierMap : this.modifierMaps) {
			if (modifierMap.id.equals(id)) {
				return modifierMap;
			}
		}
		return null;
	}
	
	public ModifierMap putModifierMap(ModifierMap modifierMap) {
		for (int i = 0; i < this.modifierMaps.size(); i++) {
			if (this.modifierMaps.get(i).id.equals(modifierMap.id)) {
				return this.modifierMaps.set(i, modifierMap);
			}
		}
		this.modifierMaps.add(modifierMap);
		return null;
	}
	
	public KeyMapSet getKeyMapSet(String id) {
		for (KeyMapSet keyMapSet : this.keyMapSets) {
			if (keyMapSet.id.equals(id)) {
				return keyMapSet;
			}
		}
		return null;
	}
	
	public KeyMapSet putKeyMapSet(KeyMapSet keyMapSet) {
		for (int i = 0; i < this.keyMapSets.size(); i++) {
			if (this.keyMapSets.get(i).id.equals(keyMapSet.id)) {
				return this.keyMapSets.set(i, keyMapSet);
			}
		}
		this.keyMapSets.add(keyMapSet);
		return null;
	}
	
	public Actions actions() {
		if (this.actions.isEmpty()) {
			Actions actions = new Actions();
			this.actions.add(actions);
			return actions;
		} else {
			int i = this.actions.size() - 1;
			return this.actions.get(i);
		}
	}
	
	public Terminators terminators() {
		if (this.terminators.isEmpty()) {
			Terminators terminators = new Terminators();
			this.terminators.add(terminators);
			return terminators;
		} else {
			int i = this.terminators.size() - 1;
			return this.terminators.get(i);
		}
	}
	
	public String startTag() {
		StringBuffer sb = new StringBuffer();
		sb.append("<keyboard");
		sb.append(" group=\"" + group + "\"");
		sb.append(" id=\"" + id + "\"");
		if (name != null) {
			String s = xmlEncode(name);
			sb.append(" name=\"" + s + "\"");
		}
		if (maxout >= 0) {
			sb.append(" maxout=\"" + maxout + "\"");
		}
		sb.append(">");
		return sb.toString();
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb.append("<!DOCTYPE keyboard SYSTEM \"file://localhost/System/Library/DTDs/KeyboardLayout.dtd\">\n");
		sb.append(startTag() + "\n");
		for (Layouts l : layouts) sb.append(l.toString());
		for (ModifierMap m : modifierMaps) sb.append(m.toString());
		for (KeyMapSet k : keyMapSets) sb.append(k.toString());
		for (Actions a : actions) sb.append(a.toString());
		for (Terminators t : terminators) sb.append(t.toString());
		sb.append("</keyboard>\n");
		return sb.toString();
	}
	
	public static class Layouts extends ArrayList<Layout> {
		private static final long serialVersionUID = 1L;
		public Layout getLayout(int i) {
			for (Layout layout : this) {
				if (i < 0 || (layout.first <= i && i <= layout.last)) {
					return layout;
				}
			}
			return null;
		}
		public void putLayout(Layout layout) {
			this.add(layout);
		}
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append("\t<layouts>\n");
			for (Layout layout : this) {
				sb.append("\t\t" + layout.toString() + "\n");
			}
			sb.append("\t</layouts>\n");
			return sb.toString();
		}
	}
	
	public static class Layout {
		public final int first;
		public final int last;
		public final String modifiers;
		public final String mapSet;
		public Layout(int first, int last, String modifiers, String mapSet) {
			this.first = first;
			this.last = last;
			this.modifiers = modifiers;
			this.mapSet = mapSet;
		}
		public boolean equals(Object o) {
			if (o instanceof Layout) {
				Layout that = (Layout)o;
				return this.first == that.first
				    && this.last  == that.last
				    && stringEquals(this.modifiers, that.modifiers)
				    && stringEquals(this.mapSet,    that.mapSet);
			}
			return false;
		}
		public int hashCode() {
			int hash = 0x6c61796f;
			hash *= 37; hash += first;
			hash *= 37; hash += last;
			hash *= 37; if (modifiers != null) hash += modifiers.hashCode();
			hash *= 37; if (mapSet    != null) hash += mapSet   .hashCode();
			return hash;
		}
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append("<layout");
			if (first >= 0) {
				sb.append(" first=\"" + first + "\"");
			}
			if (last >= 0) {
				sb.append(" last=\"" + last + "\"");
			}
			if (modifiers != null) {
				String s = xmlEncode(modifiers);
				sb.append(" modifiers=\"" + s + "\"");
			}
			if (mapSet != null) {
				String s = xmlEncode(mapSet);
				sb.append(" mapSet=\"" + s + "\"");
			}
			sb.append(" />");
			return sb.toString();
		}
	}
	
	public static class ModifierMap extends ArrayList<KeyMapSelect> {
		private static final long serialVersionUID = 1L;
		public final String id;
		public final int defaultIndex;
		public ModifierMap(String id, int defaultIndex) {
			this.id = id;
			this.defaultIndex = defaultIndex;
		}
		public KeyMapSelect getKeyMapSelect(int mapIndex) {
			for (KeyMapSelect kms : this) {
				if (kms.mapIndex == mapIndex) {
					return kms;
				}
			}
			return null;
		}
		public KeyMapSelect putKeyMapSelect(KeyMapSelect kms) {
			for (int i = 0; i < this.size(); i++) {
				if (this.get(i).mapIndex == kms.mapIndex) {
					return this.set(i, kms);
				}
			}
			this.add(kms);
			return null;
		}
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append("\t<modifierMap");
			if (id != null) {
				String s = xmlEncode(id);
				sb.append(" id=\"" + s + "\"");
			}
			if (defaultIndex >= 0) {
				sb.append(" defaultIndex=\"" + defaultIndex + "\"");
			}
			sb.append(">\n");
			for (KeyMapSelect kms : this) {
				sb.append(kms.toString());
			}
			sb.append("\t</modifierMap>\n");
			return sb.toString();
		}
		public int mapIndexForState(boolean ctrl, boolean cmd, boolean opt, boolean shift, boolean caps) {
			for (KeyMapSelect kms : this) {
				if (kms.matchesState(ctrl, cmd, opt, shift, caps)) {
					return kms.mapIndex;
				}
			}
			return defaultIndex;
		}
	}
	
	public static class KeyMapSelect extends ArrayList<Modifier> {
		private static final long serialVersionUID = 1L;
		public final int mapIndex;
		public KeyMapSelect(int mapIndex) {
			this.mapIndex = mapIndex;
		}
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append("\t\t<keyMapSelect");
			if (mapIndex >= 0) {
				sb.append(" mapIndex=\"" + mapIndex + "\"");
			}
			sb.append(">\n");
			for (Modifier mod : this) {
				sb.append("\t\t\t" + mod.toString() + "\n");
			}
			sb.append("\t\t</keyMapSelect>\n");
			return sb.toString();
		}
		public boolean matchesState(boolean ctrl, boolean cmd, boolean opt, boolean shift, boolean caps) {
			for (Modifier mod : this) {
				if (mod.matchesState(ctrl, cmd, opt, shift, caps)) {
					return true;
				}
			}
			return false;
		}
	}
	
	public static class Modifier {
		public static final Modifier UNSHIFTED = new Modifier("");
		public static final Modifier CAPSLOCK = new Modifier("caps");
		public static final Modifier SHIFTED = new Modifier("anyShift caps?");
		public static final Modifier ALT_UNSHIFTED = new Modifier("anyOption");
		public static final Modifier ALT_CAPSLOCK = new Modifier("anyOption caps");
		public static final Modifier ALT_SHIFTED = new Modifier("anyOption anyShift caps?");
		public static final Modifier COMMAND = new Modifier("command anyShift? caps?");
		public static final Modifier COMMAND_OPTION = new Modifier("command anyOption caps?");
		public static final Modifier COMMAND_OPTION_SHIFT = new Modifier("command anyOption anyShift caps?");
		public static final Modifier CTRL = new Modifier("control command? anyOption? anyShift? caps?");
		
		public static Modifier forState(Boolean ctrl, Boolean cmd, Boolean opt, Boolean shift, Boolean caps) {
			StringBuffer sb = new StringBuffer();
			if (ctrl  == null) sb.append(" control?"  ); else if (ctrl .booleanValue()) sb.append(" control"  );
			if (cmd   == null) sb.append(" command?"  ); else if (cmd  .booleanValue()) sb.append(" command"  );
			if (opt   == null) sb.append(" anyOption?"); else if (opt  .booleanValue()) sb.append(" anyOption");
			if (shift == null) sb.append(" anyShift?" ); else if (shift.booleanValue()) sb.append(" anyShift" );
			if (caps  == null) sb.append(" caps?"     ); else if (caps .booleanValue()) sb.append(" caps"     );
			return new Modifier(sb.toString().trim());
		}
		
		public final String keys;
		public Modifier(String keys) {
			this.keys = keys;
		}
		public boolean equals(Object o) {
			if (o instanceof Modifier) {
				Modifier that = (Modifier)o;
				return stringEquals(this.keys, that.keys);
			}
			return false;
		}
		public int hashCode() {
			int hash = 0x6d6f6469;
			hash *= 37; if (keys != null) hash += keys.hashCode();
			return hash;
		}
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append("<modifier");
			if (keys != null) {
				String s = xmlEncode(keys);
				sb.append(" keys=\"" + s + "\"");
			}
			sb.append(" />");
			return sb.toString();
		}
		
		public Boolean controlState() { return (keys == null || keys.contains("control?"  )) ? null : keys.contains("control"  ); }
		public Boolean commandState() { return (keys == null || keys.contains("command?"  )) ? null : keys.contains("command"  ); }
		public Boolean optionState()  { return (keys == null || keys.contains("anyOption?")) ? null : keys.contains("anyOption"); }
		public Boolean shiftState()   { return (keys == null || keys.contains("anyShift?" )) ? null : keys.contains("anyShift" ); }
		public Boolean capsState()    { return (keys == null || keys.contains("caps?"     )) ? null : keys.contains("caps"     ); }
		
		public boolean matchesState(boolean ctrl, boolean cmd, boolean opt, boolean shift, boolean caps) {
			return (
				(keys != null)
				&& (keys.contains("control?"  ) || (ctrl  == keys.contains("control"  )))
				&& (keys.contains("command?"  ) || (cmd   == keys.contains("command"  )))
				&& (keys.contains("anyOption?") || (opt   == keys.contains("anyOption")))
				&& (keys.contains("anyShift?" ) || (shift == keys.contains("anyShift" )))
				&& (keys.contains("caps?"     ) || (caps  == keys.contains("caps"     )))
			);
		}
	}
	
	public static class KeyMapSet extends ArrayList<KeyMap> {
		private static final long serialVersionUID = 1L;
		public final String id;
		public KeyMapSet(String id) {
			this.id = id;
		}
		public KeyMap getKeyMap(int index) {
			for (KeyMap keymap : this) {
				if (keymap.index == index) {
					return keymap;
				}
			}
			return null;
		}
		public KeyMap putKeyMap(KeyMap keymap) {
			for (int i = 0; i < this.size(); i++) {
				if (this.get(i).index == keymap.index) {
					return this.set(i, keymap);
				}
			}
			this.add(keymap);
			return null;
		}
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append("\t<keyMapSet");
			if (id != null) {
				String s = xmlEncode(id);
				sb.append(" id=\"" + s + "\"");
			}
			sb.append(">\n");
			for (KeyMap keymap : this) {
				sb.append(keymap.toString());
			}
			sb.append("\t</keyMapSet>\n");
			return sb.toString();
		}
	}
	
	public static class KeyMap extends ArrayList<Key> {
		private static final long serialVersionUID = 1L;
		public final int index;
		public KeyMap(int index) {
			this.index = index;
		}
		public Key getKey(int code) {
			for (Key key : this) {
				if (key.code == code) {
					return key;
				}
			}
			return null;
		}
		public Key putKey(Key key) {
			for (int i = 0; i < this.size(); i++) {
				if (this.get(i).code == key.code) {
					return this.set(i, key);
				}
			}
			this.add(key);
			return null;
		}
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append("\t\t<keyMap");
			if (index >= 0) {
				sb.append(" index=\"" + index + "\"");
			}
			sb.append(">\n");
			for (Key key : this) {
				sb.append("\t\t\t" + key.toString() + "\n");
			}
			sb.append("\t\t</keyMap>\n");
			return sb.toString();
		}
	}
	
	public static class Key {
		public final int code;
		public final String output;
		public final String action;
		public Key(int code, String output, String action) {
			this.code = code;
			this.output = output;
			this.action = action;
		}
		public boolean equals(Object o) {
			if (o instanceof Key) {
				Key that = (Key)o;
				return this.code == that.code
				    && stringEquals(this.output, that.output)
				    && stringEquals(this.action, that.action);
			}
			return false;
		}
		public int hashCode() {
			int hash = 0x6b657920;
			hash *= 37; hash += code;
			hash *= 37; if (output != null) hash += output.hashCode();
			hash *= 37; if (action != null) hash += action.hashCode();
			return hash;
		}
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append("<key");
			if (code >= 0) {
				String s = " code=\"" + code + "\"";
				while (s.length() < 11) s += " ";
				sb.append(s);
			}
			if (output != null) {
				String s = outputEncode(output);
				sb.append(" output=\"" + s + "\"");
			}
			if (action != null) {
				String s = xmlEncode(action);
				sb.append(" action=\"" + s + "\"");
			}
			sb.append(" />");
			return sb.toString();
		}
	}
	
	public static class Actions extends ArrayList<Action> {
		private static final long serialVersionUID = 1L;
		public Action getAction(String id) {
			for (Action action : this) {
				if (action.id.equals(id)) {
					return action;
				}
			}
			return null;
		}
		public Action putAction(Action action) {
			for (int i = 0; i < this.size(); i++) {
				if (this.get(i).id.equals(action.id)) {
					return this.set(i, action);
				}
			}
			this.add(action);
			return null;
		}
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append("\t<actions>\n");
			for (Action action : this) {
				sb.append(action.toString());
			}
			sb.append("\t</actions>\n");
			return sb.toString();
		}
	}
	
	public static class Action extends ArrayList<When> {
		private static final long serialVersionUID = 1L;
		public final String id;
		public Action(String id) {
			this.id = id;
		}
		public When getWhen(String state) {
			for (When when : this) {
				if (when.state.equals(state)) {
					return when;
				}
			}
			return null;
		}
		public When putWhen(When when) {
			for (int i = 0; i < this.size(); i++) {
				if (this.get(i).state.equals(when.state)) {
					return this.set(i, when);
				}
			}
			this.add(when);
			return null;
		}
		public int stateIndent() {
			int indent = 0;
			for (When when : this) {
				if (when.state != null) {
					String s = xmlEncode(when.state);
					if (s.length() > indent) indent = s.length();
				}
			}
			return indent;
		}
		public String toString() {
			int si = this.stateIndent();
			StringBuffer sb = new StringBuffer();
			sb.append("\t\t<action");
			if (id != null) {
				String s = xmlEncode(id);
				sb.append(" id=\"" + s + "\"");
			}
			sb.append(">\n");
			for (When when : this) {
				sb.append("\t\t\t" + when.toString(si) + "\n");
			}
			sb.append("\t\t</action>\n");
			return sb.toString();
		}
	}
	
	public static class When {
		public final String state;
		public final String through;
		public final String output;
		public final String multiplier;
		public final String next;
		public When(String state, String through, String output, String multiplier, String next) {
			this.state = state;
			this.through = through;
			this.output = output;
			this.multiplier = multiplier;
			this.next = next;
		}
		public boolean equals(Object o) {
			if (o instanceof When) {
				When that = (When)o;
				return stringEquals(this.state,      that.state)
				    && stringEquals(this.through,    that.through)
				    && stringEquals(this.output,     that.output)
				    && stringEquals(this.multiplier, that.multiplier)
				    && stringEquals(this.next,       that.next);
			}
			return false;
		}
		public int hashCode() {
			int hash = 0x7768656e;
			hash *= 37; if (state      != null) hash += state     .hashCode();
			hash *= 37; if (through    != null) hash += through   .hashCode();
			hash *= 37; if (output     != null) hash += output    .hashCode();
			hash *= 37; if (multiplier != null) hash += multiplier.hashCode();
			hash *= 37; if (next       != null) hash += next      .hashCode();
			return hash;
		}
		public String toString() {
			return toString(0);
		}
		public String toString(int stateIndent) {
			StringBuffer sb = new StringBuffer();
			sb.append("<when");
			if (state != null) {
				String s = xmlEncode(state);
				sb.append(" state=\"" + s + "\"");
				int ns = stateIndent - s.length();
				if (ns > 0) sb.append(spaces(ns));
			}
			if (through != null) {
				String s = xmlEncode(through);
				sb.append(" through=\"" + s + "\"");
			}
			if (output != null) {
				String s = outputEncode(output);
				sb.append(" output=\"" + s + "\"");
			}
			if (multiplier != null) {
				String s = xmlEncode(multiplier);
				sb.append(" multiplier=\"" + s + "\"");
			}
			if (next != null) {
				String s = xmlEncode(next);
				sb.append(" next=\"" + s + "\"");
			}
			sb.append(" />");
			return sb.toString();
		}
	}
	
	public static class Terminators extends ArrayList<When> {
		private static final long serialVersionUID = 1L;
		public When getWhen(String state) {
			for (When when : this) {
				if (when.state.equals(state)) {
					return when;
				}
			}
			return null;
		}
		public When putWhen(When when) {
			for (int i = 0; i < this.size(); i++) {
				if (this.get(i).state.equals(when.state)) {
					return this.set(i, when);
				}
			}
			this.add(when);
			return null;
		}
		public int stateIndent() {
			int indent = 0;
			for (When when : this) {
				if (when.state != null) {
					String s = xmlEncode(when.state);
					if (s.length() > indent) indent = s.length();
				}
			}
			return indent;
		}
		public String toString() {
			int si = this.stateIndent();
			StringBuffer sb = new StringBuffer();
			sb.append("\t<terminators>\n");
			for (When when : this) {
				sb.append("\t\t" + when.toString(si) + "\n");
			}
			sb.append("\t</terminators>\n");
			return sb.toString();
		}
	}
	
	private static boolean stringEquals(String a, String b) {
		if (a == null) return (b == null);
		if (b == null) return (a == null);
		return a.equals(b);
	}
	
	private static String outputEncode(String s) {
		if (s.equals(" ")) return "&#x0020;";
		return xmlEncode(s);
	}
	
	private static String xmlEncode(String s) {
		StringBuffer sb = new StringBuffer();
		int i = 0;
		while (i < s.length()) {
			int ch = s.codePointAt(i);
			if (
				ch < 0x20 || ch >= 0x7F ||
				ch == '"' || ch == '\'' ||
				ch == '&' || ch == '<' || ch == '>'
			) {
				String h = Integer.toHexString(ch).toUpperCase();
				while (h.length() < 4) h = "0" + h;
				sb.append("&#x" + h + ";");
			} else {
				sb.append((char)ch);
			}
			i += Character.charCount(ch);
		}
		return sb.toString();
	}
	
	private static String spaces(int n) {
		String s = " ";
		while (s.length() < n) s += s;
		return s.substring(0, n);
	}
}
