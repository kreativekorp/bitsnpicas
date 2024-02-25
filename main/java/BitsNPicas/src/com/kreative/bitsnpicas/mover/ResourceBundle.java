package com.kreative.bitsnpicas.mover;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.ImageIcon;
import com.kreative.unicode.ttflib.DfontResource;

public class ResourceBundle implements Cloneable, Comparable<ResourceBundle> {
	public final ImageIcon icon;
	public final String moverType;
	public final String name;
	public final int id;
	public final FONDResource fond;
	public final Set<DfontResource> resources;
	
	public ResourceBundle(ImageIcon icon, String moverType, String name, int id) {
		this.icon = icon;
		this.moverType = moverType;
		this.name = name;
		this.id = id;
		this.fond = null;
		this.resources = new HashSet<DfontResource>();
	}
	
	public ResourceBundle(ImageIcon icon, String moverType, FONDResource fond) {
		this.icon = icon;
		this.moverType = moverType;
		this.name = fond.toString();
		this.id = fond.id;
		this.fond = fond;
		this.resources = new HashSet<DfontResource>();
	}
	
	public int length() {
		int length = ((fond != null) ? fond.length() : 0);
		for (DfontResource res : resources) length += res.getData().length;
		return length;
	}
	
	@Override
	public ResourceBundle clone() {
		ResourceBundle copy;
		if (fond != null) {
			FONDResource newFOND = new FONDResource(fond, fond.entries);
			copy = new ResourceBundle(icon, moverType, newFOND);
		} else {
			copy = new ResourceBundle(icon, moverType, name, id);
		}
		for (DfontResource res : resources) {
			copy.resources.add(new DfontResource(
				res.getType(), res.getId(), res.getAttributes(),
				res.getName(), res.getData(), 0, res.getData().length
			));
		}
		return copy;
	}
	
	@Override
	public int compareTo(ResourceBundle that) {
		return compare(this, that);
	}
	
	public static int compare(ResourceBundle a, ResourceBundle b) {
		List<String> na = tokenize(a.name.trim());
		List<String> nb = tokenize(b.name.trim());
		for (int i = 0; i < na.size() && i < nb.size(); i++) {
			try {
				double va = Double.parseDouble(na.get(i));
				double vb = Double.parseDouble(nb.get(i));
				int cmp = Double.compare(va, vb);
				if (cmp != 0) return cmp;
			} catch (NumberFormatException e) {
				int cmp = na.get(i).compareToIgnoreCase(nb.get(i));
				if (cmp != 0) return cmp;
			}
		}
		return na.size() - nb.size();
	}
	
	private static List<String> tokenize(String s) {
		List<String> tokens = new ArrayList<String>();
		StringBuffer token = new StringBuffer();
		int tokenType = 0;
		CharacterIterator iter = new StringCharacterIterator(s);
		for (char ch = iter.first(); ch != CharacterIterator.DONE; ch = iter.next()) {
			int tt = Character.isDigit(ch) ? 1 : Character.isLetter(ch) ? 2 : 3;
			if (tt != tokenType) {
				if (token.length() > 0) {
					tokens.add(token.toString());
					token = new StringBuffer();
				}
				tokenType = tt;
			}
			token.append(ch);
		}
		if (token.length() > 0) tokens.add(token.toString());
		return tokens;
	}
}
