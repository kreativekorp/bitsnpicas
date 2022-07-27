package com.kreative.bitsnpicas.edit.importer;

import com.kreative.bitsnpicas.FontImporter;
import com.kreative.unicode.data.GlyphList;

public interface PSFEncodingSelectionImporter {
	public FontImporter<?> createImporter(GlyphList low, GlyphList high, int puaBase);
}
