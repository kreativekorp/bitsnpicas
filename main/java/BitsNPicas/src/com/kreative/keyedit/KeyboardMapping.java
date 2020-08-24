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
	
	public String getNameNotEmpty() {
		if (name != null && name.length() > 0) return name;
		return "Untitled Layout";
	}
	
	public String getWinIdentifierNotEmpty() {
		if (winIdentifier != null && winIdentifier.length() > 0) return winIdentifier;
		String winId = getNameNotEmpty().replaceAll("[^A-Za-z0-9]", "");
		if (winId.length() > 8) winId = winId.substring(0, 8);
		return winId;
	}
	
	public String getWinCompanyNotEmpty() {
		if (winCompany != null && winCompany.length() > 0) return winCompany;
		return "Anonymous";
	}
	
	public String getWinCopyrightNotEmpty() {
		if (winCopyright != null && winCopyright.length() > 0) return winCopyright;
		int year = new GregorianCalendar().get(GregorianCalendar.YEAR);
		return "(c) " + year + " " + getWinCompanyNotEmpty();
	}
	
	public WinLocale getWinLocaleNotNull() {
		if (winLocale != null) return winLocale;
		return WinLocale.EN_US;
	}
	
	public String getXkbPathNotEmpty() {
		if (xkbPath != null && xkbPath.length() > 0) return xkbPath;
		return getNameNotEmpty().replaceAll("[^A-Za-z0-9_-]", "").toLowerCase();
	}
	
	public String getXkbLabelNotEmpty() {
		if (xkbLabel != null && xkbLabel.length() > 0) return xkbLabel;
		String name = getNameNotEmpty();
		return (name.length() > 2) ? name.substring(0, 2) : name;
	}
	
	public XkbAltGrKey getXkbAltGrKeyNotNull() {
		if (xkbAltGrKey != null) return xkbAltGrKey;
		return XkbAltGrKey.none;
	}
	
	public XkbComposeKey getXkbComposeKeyNotNull() {
		if (xkbComposeKey != null) return xkbComposeKey;
		return XkbComposeKey.none;
	}
	
	public void autoFill() {
		name = getNameNotEmpty();
		winIdentifier = getWinIdentifierNotEmpty();
		winCompany = getWinCompanyNotEmpty();
		winCopyright = getWinCopyrightNotEmpty();
		winLocale = getWinLocaleNotNull();
		xkbPath = getXkbPathNotEmpty();
		xkbLabel = getXkbLabelNotEmpty();
		xkbAltGrKey = getXkbAltGrKeyNotNull();
		xkbComposeKey = getXkbComposeKeyNotNull();
	}
}
