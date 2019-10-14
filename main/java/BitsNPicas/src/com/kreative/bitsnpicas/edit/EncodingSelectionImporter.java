package com.kreative.bitsnpicas.edit;

import com.kreative.bitsnpicas.FontImporter;
import com.kreative.bitsnpicas.unicode.EncodingTable;

public interface EncodingSelectionImporter {
	public FontImporter<?> createImporter(EncodingTable encoding);
}
