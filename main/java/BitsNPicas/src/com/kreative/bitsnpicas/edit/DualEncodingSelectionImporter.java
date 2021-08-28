package com.kreative.bitsnpicas.edit;

import com.kreative.bitsnpicas.FontImporter;
import com.kreative.bitsnpicas.unicode.EncodingTable;

public interface DualEncodingSelectionImporter {
	public FontImporter<?> createImporter(EncodingTable sbenc, String dbenc);
}
