package com.kreative.keyedit;

import java.awt.image.BufferedImage;
import java.util.BitSet;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.TreeMap;

public class KeyboardMapping {
	public String name;
	public String winIdentifier;
	public String winCopyright;
	public String winCompany;
	public WinLocale winLocale = WinLocale.EN_US;
	public boolean winAltGrEnable = true;
	public boolean winShiftLock = false;
	public boolean winLrmRlm = false;
	public int macGroupNumber = 126;
	public int macIdNumber = -1;
	public String xkbPath;
	public String xkbLabel;
	public String xkbComment;
	public boolean xkbUseKeySym = false;
	public XkbAltGrKey xkbAltGrKey = XkbAltGrKey.ralt_switch;
	public XkbComposeKey xkbComposeKey = XkbComposeKey.none;
	public BufferedImage icon;
	public Integer macIconVersion;
	public final Map<Key,KeyMapping> map;
	public final Map<Integer,String> macActionIds;
	public String htmlTitle;
	public String htmlStyle;
	public String htmlH1;
	public String htmlH2;
	public String htmlBody1;
	public String htmlBody2;
	public String htmlBody3;
	public String htmlBody4;
	public BitSet htmlSquareChars;
	public BitSet htmlOutlineChars;
	
	public KeyboardMapping() {
		TreeMap<Key,KeyMapping> map = new TreeMap<Key,KeyMapping>();
		for (Key key : Key.values()) map.put(key, new KeyMapping(key));
		this.map = Collections.unmodifiableMap(map);
		this.macActionIds = new TreeMap<Integer,String>();
	}
	
	public void autoFill() {
		if (name == null || name.length() == 0) {
			name = "Untitled Layout";
		}
		if (winIdentifier == null || winIdentifier.length() == 0) {
			winIdentifier = name.replaceAll("[^A-Za-z0-9]", "");
			if (winIdentifier.length() > 8) winIdentifier = winIdentifier.substring(0, 8);
		}
		if (winCompany == null || winCompany.length() == 0) {
			winCompany = "Anonymous";
		}
		if (winCopyright == null || winCopyright.length() == 0) {
			int year = new GregorianCalendar().get(GregorianCalendar.YEAR);
			winCopyright = "(c) " + year + " " + winCompany;
		}
		if (winLocale == null) {
			winLocale = WinLocale.EN_US;
		}
		if (xkbPath == null) {
			xkbPath = name.replaceAll("[^A-Za-z0-9_-]", "").toLowerCase();
		}
		if (xkbLabel == null) {
			xkbLabel = (name.length() > 2) ? name.substring(0, 2) : name;
		}
		if (xkbAltGrKey == null) {
			xkbAltGrKey = XkbAltGrKey.none;
		}
		if (xkbComposeKey == null) {
			xkbComposeKey = XkbComposeKey.none;
		}
	}
}
