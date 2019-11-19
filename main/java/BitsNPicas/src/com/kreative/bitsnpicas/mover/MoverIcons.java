package com.kreative.bitsnpicas.mover;

import java.io.File;
import javax.swing.ImageIcon;
import com.kreative.bitsnpicas.MacUtility;

public class MoverIcons {
	public static final ImageIcon DA_16 = icon("DA.16.png", "desk accessory");
	public static final ImageIcon DA_32 = icon("DA.32.png", "desk accessory");
	public static final ImageIcon FILE_BLANK_16 = icon("File.Blank.16.png", "file");
	public static final ImageIcon FILE_BLANK_32 = icon("File.Blank.32.png", "file");
	public static final ImageIcon FILE_FKEY_16 = icon("File.FKEY.16.png", "function key");
	public static final ImageIcon FILE_FKEY_32 = icon("File.FKEY.32.png", "function key");
	public static final ImageIcon FILE_FONT_16 = icon("File.Font.16.png", "font");
	public static final ImageIcon FILE_FONT_32 = icon("File.Font.32.png", "font");
	public static final ImageIcon FILE_KEYBOARD_16 = icon("File.Keyboard.16.png", "keyboard layout");
	public static final ImageIcon FILE_KEYBOARD_32 = icon("File.Keyboard.32.png", "keyboard layout");
	public static final ImageIcon FILE_SCRIPT_16 = icon("File.Script.16.png", "script");
	public static final ImageIcon FILE_SCRIPT_32 = icon("File.Script.32.png", "script");
	public static final ImageIcon FILE_SOUND_16 = icon("File.Sound.16.png", "sound");
	public static final ImageIcon FILE_SOUND_32 = icon("File.Sound.32.png", "sound");
	public static final ImageIcon FILE_TRUETYPE_16 = icon("File.TrueType.16.png", "TrueType font");
	public static final ImageIcon FILE_TRUETYPE_32 = icon("File.TrueType.32.png", "TrueType font");
	public static final ImageIcon FILE_TTC_16 = icon("File.TTC.16.png", "TrueType collection");
	public static final ImageIcon FILE_TTC_32 = icon("File.TTC.32.png", "TrueType collection");
	public static final ImageIcon FILE_TTF_16 = icon("File.TTF.16.png", "TrueType flat font");
	public static final ImageIcon FILE_TTF_32 = icon("File.TTF.32.png", "TrueType flat font");
	public static final ImageIcon SUITCASE_BLANK_16 = icon("Suitcase.Blank.16.png", "suitcase");
	public static final ImageIcon SUITCASE_BLANK_32 = icon("Suitcase.Blank.32.png", "suitcase");
	public static final ImageIcon SUITCASE_DA_16 = icon("Suitcase.DA.16.png", "desk accessory suitcase");
	public static final ImageIcon SUITCASE_DA_32 = icon("Suitcase.DA.32.png", "desk accessory suitcase");
	public static final ImageIcon SUITCASE_FONT_16 = icon("Suitcase.Font.16.png", "font suitcase");
	public static final ImageIcon SUITCASE_FONT_32 = icon("Suitcase.Font.32.png", "font suitcase");
	public static final ImageIcon SUITCASE_SYSTEM_16 = icon("Suitcase.System.16.png", "system suitcase");
	public static final ImageIcon SUITCASE_SYSTEM_32 = icon("Suitcase.System.32.png", "system suitcase");
	
	private static ImageIcon icon(String name, String description) {
		return new ImageIcon(MoverIcons.class.getResource(name), description);
	}
	
	public static ImageIcon getFileIcon(File file) {
		String type = MacUtility.getType(file);
		String creator = MacUtility.getCreator(file);
		if (creator != null) {
			if (creator.equals("MACS") || creator.equals("macs")) {
				if (type != null) {
					if (type.equals("ZSYS") || type.equals("zsys")) {
						return SUITCASE_SYSTEM_32;
					}
				}
			}
			if (creator.equals("DMOV")) {
				if (type != null) {
					if (type.equals("DFIL")) return SUITCASE_DA_32;
					if (type.equals("FFIL")) return SUITCASE_FONT_32;
				}
				return SUITCASE_BLANK_32;
			}
			if (creator.equals("movr")) {
				if (type != null) {
					if (type.equals("dfil")) return DA_32;
					if (type.equals("ffil")) return FILE_FONT_32;
					if (type.equals("fkey")) return FILE_FKEY_32;
					if (type.equals("ifil")) return FILE_SCRIPT_32;
					if (type.equals("kfil")) return FILE_KEYBOARD_32;
					if (type.equals("sfil")) return FILE_SOUND_32;
					if (type.equals("tfil")) return FILE_TRUETYPE_32;
				}
				return FILE_BLANK_32;
			}
		}
		String name = file.getName();
		int o = name.lastIndexOf(".");
		if (o > 0) {
			String ext = name.substring(o);
			if (ext.equalsIgnoreCase(".dfont")) return SUITCASE_FONT_32;
			if (ext.equalsIgnoreCase(".suit")) return SUITCASE_FONT_32;
			if (ext.equalsIgnoreCase(".ttc")) return FILE_TTC_32;
			if (ext.equalsIgnoreCase(".ttf")) return FILE_TTF_32;
		}
		return FILE_BLANK_32;
	}
}
