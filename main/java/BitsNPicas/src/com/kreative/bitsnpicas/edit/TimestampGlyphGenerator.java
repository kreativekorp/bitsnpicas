package com.kreative.bitsnpicas.edit;

import java.util.GregorianCalendar;
import java.util.List;
import com.kreative.bitsnpicas.BitmapFontGlyph;
import com.kreative.bitsnpicas.Font;

public class TimestampGlyphGenerator extends GlyphGenerator<BitmapFontGlyph> {
	public String getName() { return "Timestamp Glyph"; }
	public Class<BitmapFontGlyph> getGlyphClass() { return BitmapFontGlyph.class; }
	public Result generate(Font<BitmapFontGlyph> font, List<GlyphLocator<BitmapFontGlyph>> locators) {
		if (locators.isEmpty()) return Result.NO_CHANGE;
		
		GregorianCalendar now = new GregorianCalendar();
		// Support reproducible builds by using SOURCE_DATE_EPOCH as the current UNIX time.
		String sourceDateEpochEnv = System.getenv("SOURCE_DATE_EPOCH");
		if (sourceDateEpochEnv != null) {
			long sourceDateEpoch = Long.parseLong(sourceDateEpochEnv);
			now.setTimeInMillis(sourceDateEpoch * 1000L);
		}
		
		BitmapFontGlyph[] glyphs = getTimestampGlyphs(font, now);
		if (glyphs == null) return Result.NO_CHANGE;
		for (GlyphLocator<BitmapFontGlyph> loc : locators) {
			loc.setGlyph(BitmapFontGlyph.compose(glyphs));
		}
		return Result.CONTENT_CHANGED;
	}
	
	public static BitmapFontGlyph[] getTimestampGlyphs(Font<BitmapFontGlyph> font, GregorianCalendar cal) {
		int y = cal.get(GregorianCalendar.YEAR);
		int m = cal.get(GregorianCalendar.MONTH) + 1;
		int d = cal.get(GregorianCalendar.DAY_OF_MONTH);
		BitmapFontGlyph[] namedGlyphs = {
			font.getNamedGlyph("timestamp.ch" + ((y / 1000) % 10)),
			font.getNamedGlyph("timestamp.cl" + ((y /  100) % 10)),
			font.getNamedGlyph("timestamp.yh" + ((y /   10) % 10)),
			font.getNamedGlyph("timestamp.yl" + ((y /    1) % 10)),
			font.getNamedGlyph("timestamp.mh" + ((m /   10) % 10)),
			font.getNamedGlyph("timestamp.ml" + ((m /    1) % 10)),
			font.getNamedGlyph("timestamp.dh" + ((d /   10) % 10)),
			font.getNamedGlyph("timestamp.dl" + ((d /    1) % 10)),
			font.getNamedGlyph("timestamp"),
		};
		for (BitmapFontGlyph g : namedGlyphs) {
			if (g != null) return namedGlyphs;
		}
		BitmapFontGlyph[] mappedGlyphs = {
			font.getCharacter(0x10FF40 + ((y / 1000) % 10)),
			font.getCharacter(0x10FF50 + ((y /  100) % 10)),
			font.getCharacter(0x10FF60 + ((y /   10) % 10)),
			font.getCharacter(0x10FF70 + ((y /    1) % 10)),
			font.getCharacter(0x10FF80 + ((m /   10) % 10)),
			font.getCharacter(0x10FF90 + ((m /    1) % 10)),
			font.getCharacter(0x10FFA0 + ((d /   10) % 10)),
			font.getCharacter(0x10FFB0 + ((d /    1) % 10)),
			font.getCharacter(0x10FFC0)
		};
		for (BitmapFontGlyph g : mappedGlyphs) {
			if (g != null) return mappedGlyphs;
		}
		return null;
	}
}
