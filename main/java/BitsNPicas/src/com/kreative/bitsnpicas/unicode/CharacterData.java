package com.kreative.bitsnpicas.unicode;

public class CharacterData implements Comparable<CharacterData> {
	public final int codePoint;
	public final String name;
	public final String category;
	public final int combiningClass;
	public final String bidiClass;
	public final String decomposition;
	public final Integer decimalValue;
	public final Integer digitValue;
	public final String numericValue;
	public final boolean bidiMirrored;
	public final String v1Name;
	public final String isoComment;
	public final Integer uppercaseMapping;
	public final Integer lowercaseMapping;
	public final Integer titlecaseMapping;
	
	public CharacterData(String... f) {
		this.codePoint = Integer.parseInt(f[0].trim(), 16);
		this.name = f[1].trim();
		this.category = f[2].trim();
		this.combiningClass = Integer.parseInt(f[3].trim(), 10);
		this.bidiClass = f[4].trim();
		this.decomposition = (f[5].trim().length() > 0) ? f[5].trim() : null;
		this.decimalValue = (f[6].trim().length() > 0) ? Integer.parseInt(f[6].trim(), 10) : null;
		this.digitValue = (f[7].trim().length() > 0) ? Integer.parseInt(f[7].trim(), 10) : null;
		this.numericValue = (f[8].trim().length() > 0) ? f[8].trim() : null;
		this.bidiMirrored = f[9].trim().equals("Y");
		this.v1Name = (f[10].trim().length() > 0) ? f[10].trim() : null;
		this.isoComment = (f[11].trim().length() > 0) ? f[11].trim() : null;
		this.uppercaseMapping = (f[12].trim().length() > 0) ? Integer.parseInt(f[12].trim(), 16) : null;
		this.lowercaseMapping = (f[13].trim().length() > 0) ? Integer.parseInt(f[13].trim(), 16) : null;
		this.titlecaseMapping = (f[14].trim().length() > 0) ? Integer.parseInt(f[14].trim(), 16) : null;
	}
	
	public int compareTo(CharacterData that) {
		if (this.codePoint != that.codePoint) {
			return this.codePoint - that.codePoint;
		} else {
			return this.name.compareToIgnoreCase(that.name);
		}
	}
	
	public boolean equals(Object o) {
		if (o == this) return true;
		if (o == null) return false;
		if (o instanceof CharacterData) {
			CharacterData that = (CharacterData)o;
			return (
				this.codePoint == that.codePoint &&
				equals(this.name, that.name) &&
				equals(this.category, that.category) &&
				this.combiningClass == that.combiningClass &&
				equals(this.bidiClass, that.bidiClass) &&
				equals(this.decomposition, that.decomposition) &&
				equals(this.decimalValue, that.decimalValue) &&
				equals(this.digitValue, that.digitValue) &&
				equals(this.numericValue, that.numericValue) &&
				this.bidiMirrored == that.bidiMirrored &&
				equals(this.v1Name, that.v1Name) &&
				equals(this.isoComment, that.isoComment) &&
				equals(this.uppercaseMapping, that.uppercaseMapping) &&
				equals(this.lowercaseMapping, that.lowercaseMapping) &&
				equals(this.titlecaseMapping, that.titlecaseMapping)
			);
		} else {
			return false;
		}
	}
	
	public int hashCode() {
		return (
			this.codePoint + this.name.hashCode() + this.category.hashCode() +
			this.combiningClass + this.bidiClass.hashCode()
		);
	}
	
	public String toString() {
		if (name.equals("<control>")) {
			if (v1Name != null) {
				return v1Name;
			}
		}
		return name;
	}
	
	private static boolean equals(Object a, Object b) {
		if (a == b) return true;
		if (a == null || b == null) return false;
		return a.equals(b);
	}
}
