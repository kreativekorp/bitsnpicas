package com.kreative.bitsnpicas.edit.importer;

import com.kreative.bitsnpicas.FontImporter;
import com.kreative.unicode.data.GlyphList;

public interface DualEncodingSelectionImporter {
	public FontImporter<?> createImporter(GlyphList sbenc, String dbenc);
}
