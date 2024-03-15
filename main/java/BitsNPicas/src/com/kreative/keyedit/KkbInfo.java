package com.kreative.keyedit;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class KkbInfo {
	public static void main(String[] args) {
		ArrayList<Selector> selectors = new ArrayList<Selector>();
		ArrayList<File> files = new ArrayList<File>();
		boolean parseOptions = true;
		
		for (String arg : args) {
			if (parseOptions && arg.startsWith("-")) {
				if (arg.equals("--")) {
					parseOptions = false;
				} else {
					Selector selector = Selector.forOption(arg);
					if (selector == null) { help(); return; }
					selectors.add(selector);
				}
			} else {
				files.add(new File(arg));
			}
		}
		
		if (selectors.isEmpty() || files.isEmpty()) { help(); return; }
		
		for (File file : files) {
			try {
				KeyboardMapping km = KkbReader.read(file);
				boolean first = true;
				for (Selector selector : selectors) {
					if (first) first = false;
					else System.out.print("\t");
					System.out.print(selector.get(file, km));
				}
				System.out.println();
			} catch (IOException e) {
				System.err.println("Error reading " + file + ": " + e);
			}
		}
	}
	
	public static void help() {
		System.err.println("Usage: KkbInfo <options> [--] <files>");
		System.err.println();
		System.err.println("Options:");
		Selector[] selectors = Selector.values();
		Arrays.sort(selectors, new Comparator<Selector>() {
			public int compare(Selector a, Selector b) {
				return a.option.compareTo(b.option);
			}
		});
		for (Selector selector : selectors) {
			System.err.println("  " + selector.option + "  " + selector.description);
		}
	}
	
	private static enum Selector {
		FILE_NAME("-f", "file name") {
			public String get(File file, KeyboardMapping km) {
				return file.getName();
			}
		},
		FILE_PATH("-F", "file path") {
			public String get(File file, KeyboardMapping km) {
				return file.getPath();
			}
		},
		PARENT_PATH("-P", "parent path") {
			public String get(File file, KeyboardMapping km) {
				return file.getParent();
			}
		},
		NAME("-n", "name") {
			public String get(File file, KeyboardMapping km) {
				return km.getNameNotEmpty();
			}
		},
		WIN_IDENTIFIER("-i", "(MSKLC) short name") {
			public String get(File file, KeyboardMapping km) {
				return km.getWinIdentifierNotEmpty();
			}
		},
		WIN_COPYRIGHT("-c", "(MSKLC) copyright") {
			public String get(File file, KeyboardMapping km) {
				return km.getWinCopyrightNotEmpty();
			}
		},
		WIN_COMPANY("-a", "(MSKLC) company") {
			public String get(File file, KeyboardMapping km) {
				return km.getWinCompanyNotEmpty();
			}
		},
		WIN_LOCALE("-l", "(MSKLC) locale") {
			public String get(File file, KeyboardMapping km) {
				return km.getWinLocaleNotNull().tag;
			}
		},
		WIN_ALTGR_ENABLE("-g", "(MSKLC) treat right alt as ctrl+alt (altgr)") {
			public String get(File file, KeyboardMapping km) {
				return km.winAltGrEnable ? "Y" : "N";
			}
		},
		WIN_SHIFT_LOCK("-s", "(MSKLC) disable caps lock when shift is pressed (shiftlock)") {
			public String get(File file, KeyboardMapping km) {
				return km.winShiftLock ? "Y" : "N";
			}
		},
		WIN_LRM_RLM("-r", "(MSKLC) left shift + bksp = LRM, right shift + bksp = RLM") {
			public String get(File file, KeyboardMapping km) {
				return km.winLrmRlm ? "Y" : "N";
			}
		},
		MAC_GROUP_NUMBER("-J", "(Mac OS X) group number") {
			public String get(File file, KeyboardMapping km) {
				return Integer.toString(km.macGroupNumber);
			}
		},
		MAC_ID_NUMBER("-j", "(Mac OS X) ID number") {
			public String get(File file, KeyboardMapping km) {
				return Integer.toString(km.macIdNumber);
			}
		},
		XKB_PATH("-p", "(XKB) path / name") {
			public String get(File file, KeyboardMapping km) {
				return km.getXkbPathNotEmpty();
			}
		},
		XKB_LABEL("-d", "(XKB) label / short description") {
			public String get(File file, KeyboardMapping km) {
				return km.getXkbLabelNotEmpty();
			}
		},
		XKB_USE_KEYSYM("-u", "(XKB) use keysym constants") {
			public String get(File file, KeyboardMapping km) {
				return km.xkbUseKeySym ? "Y" : "N";
			}
		},
		XKB_ALTGR_KEY("-v", "(XKB) altgr key / level3 key") {
			public String get(File file, KeyboardMapping km) {
				return km.getXkbAltGrKeyNotNull().name();
			}
		},
		XKB_COMPOSE_KEY("-k", "(XKB) compose key") {
			public String get(File file, KeyboardMapping km) {
				return km.getXkbComposeKeyNotNull().name();
			}
		},
		KEYMAN_IDENTIFIER("-I", "(Keyman) ID") {
			public String get(File file, KeyboardMapping km) {
				return km.getKeymanIdentifierNotEmpty();
			}
		},
		KEYMAN_NAME("-N", "(Keyman) name") {
			public String get(File file, KeyboardMapping km) {
				return km.getKeymanNameNotEmpty();
			}
		},
		KEYMAN_COPYRIGHT("-C", "(Keyman) copyright") {
			public String get(File file, KeyboardMapping km) {
				return km.getKeymanCopyrightNotEmpty();
			}
		},
		KEYMAN_MESSAGE("-M", "(Keyman) message") {
			public String get(File file, KeyboardMapping km) {
				return (km.keymanMessage != null) ? km.keymanMessage : "";
			}
		},
		KEYMAN_WEB_HELP_TEXT("-H", "(Keyman) web help text") {
			public String get(File file, KeyboardMapping km) {
				return (km.keymanWebHelpText != null) ? km.keymanWebHelpText : "";
			}
		},
		KEYMAN_VERSION("-V", "(Keyman) keyboard version") {
			public String get(File file, KeyboardMapping km) {
				return km.getKeymanVersionNotEmpty();
			}
		},
		KEYMAN_AUTHOR("-A", "(Keyman) author") {
			public String get(File file, KeyboardMapping km) {
				return km.getKeymanAuthorNotEmpty();
			}
		},
		KEYMAN_EMAIL_ADDRESS("-E", "(Keyman) email address") {
			public String get(File file, KeyboardMapping km) {
				return (km.keymanEmailAddress != null) ? km.keymanEmailAddress : "";
			}
		},
		KEYMAN_WEB_SITE("-W", "(Keyman) web site") {
			public String get(File file, KeyboardMapping km) {
				return (km.keymanWebSite != null) ? km.keymanWebSite : "";
			}
		},
		KEYMAN_RIGHT_TO_LEFT("-R", "(Keyman) keyboard is right-to-left") {
			public String get(File file, KeyboardMapping km) {
				return km.keymanRightToLeft ? "Y" : "N";
			}
		},
		KEYMAN_KEY_102("-K", "(Keyman) display 102nd key") {
			public String get(File file, KeyboardMapping km) {
				return km.keymanKey102 ? "Y" : "N";
			}
		},
		KEYMAN_DISPLAY_UNDERLYING("-U", "(Keyman) display underlying layout characters") {
			public String get(File file, KeyboardMapping km) {
				return km.keymanDisplayUnderlying ? "Y" : "N";
			}
		},
		KEYMAN_USE_ALTGR("-G", "(Keyman) distinguish between left and right ctrl/alt") {
			public String get(File file, KeyboardMapping km) {
				return km.keymanUseAltGr ? "Y" : "N";
			}
		},
		KEYMAN_IGNORE_CAPS("-S", "(Keyman) disable caps lock") {
			public String get(File file, KeyboardMapping km) {
				return km.keymanIgnoreCaps ? "Y" : "N";
			}
		},
		KEYMAN_DESCRIPTION("-D", "(Keyman) description") {
			public String get(File file, KeyboardMapping km) {
				return km.getKeymanDescriptionNotEmpty();
			}
		},
		KEYMAN_LICENSE_TYPE("-L", "(Keyman) license type") {
			public String get(File file, KeyboardMapping km) {
				return km.getKeymanLicenseTypeNotEmpty();
			}
		};
		
		private final String option;
		private final String description;
		private Selector(String option, String description) {
			this.option = option;
			this.description = description;
		}
		
		public abstract String get(File file, KeyboardMapping km);
		
		public static Selector forOption(String s) {
			for (Selector selector : values()) {
				if (selector.option.equals(s)) {
					return selector;
				}
			}
			return null;
		}
	}
}
