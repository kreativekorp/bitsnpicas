package com.kreative.keyedit;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MacKeyboardDiff {
	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println("Usage: MacKeyboardDiff <left-file> <right-file>");
		} else {
			MacKeyboard left;
			MacKeyboard right;
			try {
				File leftFile = new File(args[0]);
				FileInputStream leftIn = new FileInputStream(leftFile);
				left = MacKeyboardParser.parse(leftFile.getName(), leftIn);
				leftIn.close();
			} catch (IOException e) {
				System.err.println("Error: Failed to parse " + args[0] + ": " + e.getMessage());
				return;
			}
			try {
				File rightFile = new File(args[1]);
				FileInputStream rightIn = new FileInputStream(rightFile);
				right = MacKeyboardParser.parse(rightFile.getName(), rightIn);
				rightIn.close();
			} catch (IOException e) {
				System.err.println("Error: Failed to parse " + args[1] + ": " + e.getMessage());
				return;
			}
			
			System.out.println("@@ Header @@");
			printDiff(args[0], args[1], left.startTag(), right.startTag());
			
			System.out.println("@@ Layouts @@");
			List<MacKeyboard.Layout> leftLayouts = collapse(left.layouts);
			List<MacKeyboard.Layout> rightLayouts = collapse(right.layouts);
			printDiff(args[0], args[1], leftLayouts, rightLayouts);
			
			System.out.println("@@ Keymaps @@");
			for (String layoutId : intersect(layoutIds(leftLayouts), layoutIds(rightLayouts))) {
				printKeymapDiff(args[0], args[1], left, right, layoutId);
			}
			
			System.out.println("@@ Actions @@");
			printActionsDiff(args[0], args[1], collapse(left.actions), collapse(right.actions));
			
			System.out.println("@@ Terminators @@");
			printDiff(args[0], args[1], collapse(left.terminators), collapse(right.terminators));
		}
	}
	
	private static boolean[] booleans = {false, true};
	
	private static boolean stringEquals(String a, String b) {
		if (a == null) return (b == null);
		if (b == null) return (a == null);
		return a.equals(b);
	}
	
	private static <T> List<T> collapse(List<? extends List<T>> listList) {
		List<T> collapsed = new ArrayList<T>();
		for (List<T> list : listList) collapsed.addAll(list);
		return collapsed;
	}
	
	private static <T> List<T> subtract(Collection<T> left, Collection<T> right) {
		List<T> diff = new ArrayList<T>();
		diff.addAll(left);
		diff.removeAll(right);
		return diff;
	}
	
	private static <T> List<T> intersect(Collection<T> left, Collection<T> right) {
		List<T> inter = new ArrayList<T>();
		inter.addAll(left);
		inter.retainAll(right);
		return inter;
	}
	
	private static List<String> layoutIds(List<MacKeyboard.Layout> layouts) {
		List<String> ids = new ArrayList<String>();
		for (MacKeyboard.Layout layout : layouts) {
			ids.add(layout.modifiers + "," + layout.mapSet);
		}
		return ids;
	}
	
	private static List<String> actionIds(List<MacKeyboard.Action> actions) {
		List<String> ids = new ArrayList<String>();
		for (MacKeyboard.Action action : actions) ids.add(action.id);
		return ids;
	}
	
	private static MacKeyboard.ModifierMap getModifierMap(MacKeyboard mk, String id) {
		for (MacKeyboard.ModifierMap mm : mk.modifierMaps) {
			if (stringEquals(mm.id, id)) {
				return mm;
			}
		}
		return null;
	}
	
	private static MacKeyboard.KeyMapSet getKeyMapSet(MacKeyboard mk, String id) {
		for (MacKeyboard.KeyMapSet kms : mk.keyMapSets) {
			if (stringEquals(kms.id, id)) {
				return kms;
			}
		}
		return null;
	}
	
	private static MacKeyboard.Action getAction(List<MacKeyboard.Action> actions, String id) {
		for (MacKeyboard.Action action : actions) {
			if (stringEquals(action.id, id)) {
				return action;
			}
		}
		return null;
	}
	
	private static String modifierString(boolean ctrl, boolean cmd, boolean opt, boolean shift, boolean caps) {
		StringBuffer sb = new StringBuffer();
		if (ctrl) sb.append(" control");
		if (cmd) sb.append(" command");
		if (opt) sb.append(" option");
		if (shift) sb.append(" shift");
		if (caps) sb.append(" caps");
		return "{" + sb.toString().trim().replaceAll(" ", ",") + "}";
	}
	
	private static void printDiff(String l, String r, String left, String right) {
		if (stringEquals(left, right)) return;
		System.out.println("--- " + l + ":");
		System.out.println("+++ " + r + ":");
		System.out.println("- " + left);
		System.out.println("+ " + right);
	}
	
	private static <T> void printDiff(String l, String r, List<T> left, List<T> right) {
		List<T> leftDiff = subtract(left, right);
		List<T> rightDiff = subtract(right, left);
		if (leftDiff.isEmpty() && rightDiff.isEmpty()) return;
		System.out.println("--- " + l + ":");
		System.out.println("+++ " + r + ":");
		for (T t : leftDiff) System.out.println("- " + t);
		for (T t : rightDiff) System.out.println("+ " + t);
	}
	
	private static void printKeymapDiff(String l, String r, MacKeyboard left, MacKeyboard right, String layoutId) {
		String[] ids = layoutId.split(",");
		MacKeyboard.ModifierMap lmm = getModifierMap(left, ids[0]);
		MacKeyboard.ModifierMap rmm = getModifierMap(right, ids[0]);
		MacKeyboard.KeyMapSet lkms = getKeyMapSet(left, ids[1]);
		MacKeyboard.KeyMapSet rkms = getKeyMapSet(right, ids[1]);
		for (boolean ctrl : booleans) {
			for (boolean cmd : booleans) {
				for (boolean opt : booleans) {
					for (boolean shift : booleans) {
						for (boolean caps : booleans) {
							String ms = modifierString(ctrl, cmd, opt, shift, caps);
							String ls = l + ":keymaps[" + layoutId + "," + ms + "]";
							String rs = r + ":keymaps[" + layoutId + "," + ms + "]";
							MacKeyboard.KeyMap lm = lkms.getKeyMap(lmm.mapIndexForState(ctrl, cmd, opt, shift, caps));
							MacKeyboard.KeyMap rm = rkms.getKeyMap(rmm.mapIndexForState(ctrl, cmd, opt, shift, caps));
							printDiff(ls, rs, lm, rm);
						}
					}
				}
			}
		}
	}
	
	private static void printActionsDiff(String l, String r, List<MacKeyboard.Action> left, List<MacKeyboard.Action> right) {
		List<String> leftIds = actionIds(left);
		List<String> rightIds = actionIds(right);
		printDiff(l, r, leftIds, rightIds);
		for (String actionId : intersect(leftIds, rightIds)) {
			String la = l + ":actions[" + actionId + "]";
			String ra = r + ":actions[" + actionId + "]";
			printDiff(la, ra, getAction(left, actionId), getAction(right, actionId));
		}
	}
}
