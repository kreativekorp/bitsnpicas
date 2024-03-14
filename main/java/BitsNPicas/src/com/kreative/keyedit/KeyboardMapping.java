package com.kreative.keyedit;

import java.awt.image.BufferedImage;
import java.text.Collator;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

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
	public String keymanIdentifier;
	public String keymanName;
	public String keymanCopyright;
	public String keymanMessage;
	public String keymanWebHelpText;
	public String keymanVersion;
	public String keymanComments;
	public String keymanAuthor;
	public String keymanEmailAddress;
	public String keymanWebSite;
	public boolean keymanRightToLeft = false;
	public boolean keymanKey102 = false;
	public boolean keymanDisplayUnderlying = false;
	public boolean keymanUseAltGr = false;
	public boolean keymanIgnoreCaps = false;
	public final EnumSet<KeyManTarget> keymanTargets;
	public final EnumSet<KeyManPlatform> keymanPlatforms;
	public final Map<String,String> keymanLanguages;
	public final Map<String,byte[]> keymanAttachments;
	public final Map<String,String> keymanFileIds;
	public final Map<Integer,String> keymanCpLabels;
	public String keymanFontFamily;
	public String keymanOSKFontFile;
	public String keymanDisplayFontFile;
	public String keymanDescription;
	public String keymanLicenseType;
	public String keymanLicenseText;
	public String keymanReadme;
	public String keymanHistory;
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
	public String htmlInstall;
	public BitSet htmlSquareChars;
	public BitSet htmlOutlineChars;
	public final Map<String,BitSet> htmlTdClasses;
	public final Map<String,BitSet> htmlSpanClasses;
	public final Map<Integer,String> htmlCpLabels;
	
	public KeyboardMapping() {
		TreeMap<Key,KeyMapping> map = new TreeMap<Key,KeyMapping>();
		for (Key key : Key.values()) map.put(key, new KeyMapping(key));
		this.map = Collections.unmodifiableMap(map);
		this.macActionIds = new TreeMap<Integer,String>();
		this.htmlTdClasses = new TreeMap<String,BitSet>();
		this.htmlSpanClasses = new TreeMap<String,BitSet>();
		this.htmlCpLabels = new TreeMap<Integer,String>();
		this.keymanTargets = EnumSet.noneOf(KeyManTarget.class);
		this.keymanPlatforms = EnumSet.noneOf(KeyManPlatform.class);
		this.keymanLanguages = new LinkedHashMap<String,String>();
		this.keymanAttachments = new TreeMap<String,byte[]>();
		this.keymanFileIds = new TreeMap<String,String>();
		this.keymanCpLabels = new TreeMap<Integer,String>();
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
	
	public String getKeymanIdentifierNotEmpty() {
		if (keymanIdentifier != null && keymanIdentifier.length() > 0) return keymanIdentifier;
		return getKeymanNameNotEmpty().toLowerCase().replaceAll("[^A-Za-z0-9]+", "_");
	}
	
	public String getKeymanNameNotEmpty() {
		if (keymanName != null && keymanName.length() > 0) return keymanName;
		return getNameNotEmpty();
	}
	
	public String getKeymanCopyrightNotEmpty() {
		if (keymanCopyright != null && keymanCopyright.length() > 0) return keymanCopyright;
		return getWinCopyrightNotEmpty().replaceAll("[(][Cc][)]", "©");
	}
	
	public String getKeymanVersionNotEmpty() {
		if (keymanVersion != null && keymanVersion.length() > 0) return keymanVersion;
		return "1.0";
	}
	
	public String getKeymanAuthorNotEmpty() {
		if (keymanAuthor != null && keymanAuthor.length() > 0) return keymanAuthor;
		return getWinCompanyNotEmpty();
	}
	
	public String getKeymanDescriptionNotEmpty() {
		if (keymanDescription != null && keymanDescription.length() > 0) return keymanDescription;
		return getKeymanNameNotEmpty() + " generated by Kreative KeyEdit";
	}
	
	public String getKeymanFileId(String filename) {
		String id = keymanFileIds.get(filename);
		if (id == null) {
			do { id = "id_" + UUID.randomUUID().toString().replace("-", ""); }
			while (keymanFileIds.values().contains(id));
			keymanFileIds.put(filename, id);
		}
		return id;
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
		keymanIdentifier = getKeymanIdentifierNotEmpty();
		keymanName = getKeymanNameNotEmpty();
		keymanCopyright = getKeymanCopyrightNotEmpty();
		keymanVersion = getKeymanVersionNotEmpty();
		keymanAuthor = getKeymanAuthorNotEmpty();
		keymanDescription = getKeymanDescriptionNotEmpty();
	}
	
	public boolean isWindowsNativeCompatible() {
		// Windows doesn't like non-BMP characters on altgr.
		// Trying to type them just makes it beep at you.
		// But MSKLC doesn't even issue a warning about this.
		// A third-party input method has to be used for these layouts.
		for (KeyMapping km : map.values()) {
			if (km.altUnshiftedOutput > 0xFFFF) return false;
			if (km.altShiftedOutput > 0xFFFF) return false;
			for (DeadKeyTable dead : new DeadKeyTable[] {
				km.unshiftedDeadKey, km.shiftedDeadKey, km.ctrlDeadKey,
				km.altUnshiftedDeadKey, km.altShiftedDeadKey
			}) {
				if (dead != null) {
					if (dead.winTerminator > 0xFFFF) return false;
					for (int output : dead.keyMap.values()) {
						if (output > 0xFFFF) return false;
					}
				}
			}
		}
		return true;
	}
	
	public void getAllOutputs(Collection<Integer> all, boolean includeDeadKeys, boolean includeLongPress) {
		for (KeyMapping km : map.values()) {
			for (int output : new int[] {
				km.unshiftedOutput, km.shiftedOutput,
				km.altUnshiftedOutput, km.altShiftedOutput
			}) {
				if (output > 0) all.add(output);
			}
			if (includeDeadKeys) {
				for (DeadKeyTable dead : new DeadKeyTable[] {
					km.unshiftedDeadKey, km.shiftedDeadKey,
					km.altUnshiftedDeadKey, km.altShiftedDeadKey
				}) {
					if (dead != null) {
						if (dead.winTerminator > 0) all.add(dead.winTerminator);
						if (dead.macTerminator > 0) all.add(dead.macTerminator);
						for (int o : dead.keyMap.values()) if (o > 0) all.add(o);
					}
				}
			}
			if (includeLongPress) {
				for (int[] lpo : new int[][] {
					km.unshiftedLongPressOutput, km.shiftedLongPressOutput,
					km.altUnshiftedLongPressOutput, km.altShiftedLongPressOutput
				}) {
					if (lpo != null) {
						for (int o : lpo) {
							if (o > 0) all.add(o);
						}
					}
				}
			}
		}
	}
	
	public List<Integer> getAutoLongPressOutput(List<Integer> list, int cp) {
		if (list == null) list = new ArrayList<Integer>();
		for (KeyMapping mm : map.values()) {
			Integer audk = (mm.altUnshiftedDeadKey != null) ? mm.altUnshiftedDeadKey.keyMap.get(cp) : null;
			Integer asdk = (mm.altShiftedDeadKey != null) ? mm.altShiftedDeadKey.keyMap.get(cp) : null;
			Integer udk = (mm.unshiftedDeadKey != null) ? mm.unshiftedDeadKey.keyMap.get(cp) : null;
			Integer sdk = (mm.shiftedDeadKey != null) ? mm.shiftedDeadKey.keyMap.get(cp) : null;
			if (audk != null) list.add(audk);
			if (asdk != null) list.add(asdk);
			if (udk != null) list.add(udk);
			if (sdk != null) list.add(sdk);
		}
		final Collator coll = Collator.getInstance();
		coll.setDecomposition(Collator.CANONICAL_DECOMPOSITION);
		coll.setStrength(Collator.IDENTICAL);
		Collections.sort(list, new Comparator<Integer>() {
			public int compare(Integer cp1, Integer cp2) {
				String s1 = String.valueOf(Character.toChars(cp1));
				String s2 = String.valueOf(Character.toChars(cp2));
				return coll.compare(s1, s2);
			}
		});
		return list;
	}
	
	public int[] getAutoLongPressOutput(int cp) {
		List<Integer> list = getAutoLongPressOutput(null, cp);
		int n = list.size();
		int[] a = new int[n];
		for (int i = 0; i < n; i++) a[i] = list.get(i);
		return a;
	}
}
