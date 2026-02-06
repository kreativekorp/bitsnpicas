package com.kreative.bitsnpicas.edit;

import java.util.List;
import java.util.Map;
import com.kreative.bitsnpicas.BitmapFontGlyph;
import com.kreative.bitsnpicas.Font;
import com.kreative.unicode.data.PuaaCache;
import com.kreative.unicode.ttflib.PuaaTable;

public class SitelenPonaCartoucheGlyphGenerator extends GlyphGenerator<BitmapFontGlyph> {
	private static final PuaaTable extras = PuaaCache.getPuaaTable("extras.ucd");
	private static final Map<Integer,String> psNames = extras.getPropertyMap("PostScript_Name");
	private static final String getGlyphName(String name, int cp) {
		if (name != null) return name;
		name = psNames.get(cp);
		if (name != null) return name;
		name = Integer.toHexString(cp).toUpperCase();
		if (cp >= 0x10000) return "u" + name;
		if (cp >= 0x1000) return "uni" + name;
		if (cp >= 0x100) return "uni0" + name;
		if (cp >= 0x10) return "uni00" + name;
		if (cp >= 0x0) return "uni000" + name;
		throw new IllegalArgumentException(name);
	}
	
	public static void addCartouchedGlyph(Font<BitmapFontGlyph> font, BitmapFontGlyph g, String gn, int cp) {
		if (g == null) return;
		int a = -font.getLineAscent();
		int d = font.getLineDescent()-1;
		int w = g.getCharacterWidth();
		BitmapFontGlyph extn = BitmapFontGlyph.compose(g);
		extn.expand(0, d, w, 1);
		extn.fillRect(0, d, w, 1, (byte)-1);
		BitmapFontGlyph cart = BitmapFontGlyph.compose(extn);
		cart.expand(0, a, w, 1);
		cart.fillRect(0, a, w, 1, (byte)-1);
		String name = getGlyphName(gn, cp);
		font.putNamedGlyph(name + ".cartouche", cart);
		font.putNamedGlyph(name + ".extension", extn);
	}
	
	public String getName() { return "Sitelen Pona Cartouche Glyph"; }
	public Class<BitmapFontGlyph> getGlyphClass() { return BitmapFontGlyph.class; }
	public Result generate(Font<BitmapFontGlyph> font, List<GlyphLocator<BitmapFontGlyph>> locators) {
		if (locators.isEmpty()) return Result.NO_CHANGE;
		for (GlyphLocator<BitmapFontGlyph> loc : locators) {
			addCartouchedGlyph(font, loc.getGlyph(), loc.getGlyphName(), loc.getCodePoint());
		}
		return Result.REPERTOIRE_CHANGED;
	}
}
