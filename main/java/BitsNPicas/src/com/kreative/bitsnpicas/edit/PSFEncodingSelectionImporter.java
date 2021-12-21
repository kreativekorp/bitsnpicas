package com.kreative.bitsnpicas.edit;

import com.kreative.bitsnpicas.FontImporter;
import com.kreative.bitsnpicas.unicode.EncodingTable;

public interface PSFEncodingSelectionImporter {
	public FontImporter<?> createImporter(EncodingTable low, EncodingTable high, int puaBase);
}
