package com.kreative.mapedit;

public enum MappingTag {
	LR(0xE0004C52, "<LR>", "left to right"),
	RL(0xE000524C, "<RL>", "right to left"),
	RV(0xE0005256, "<RV>", "reverse video");
	
	public final int intValue;
	public final String stringValue;
	public final String description;
	
	private MappingTag(int i, String s, String d) {
		this.intValue = i;
		this.stringValue = s;
		this.description = d;
	}
	
	public static MappingTag forIntValue(int i) {
		for (MappingTag tag : values()) {
			if (tag.intValue == i) return tag;
		}
		return null;
	}
	
	public static MappingTag forStringValue(String s) {
		for (MappingTag tag : values()) {
			if (tag.stringValue.equalsIgnoreCase(s)) return tag;
		}
		return null;
	}
	
	public static MappingTag forDescription(String d) {
		for (MappingTag tag : values()) {
			if (tag.description.equalsIgnoreCase(d)) return tag;
		}
		return null;
	}
}
