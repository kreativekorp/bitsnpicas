package com.kreative.bitsnpicas.importer;

import java.util.List;

// An importer that has things to tell about the font it imported, which are not errors.
public interface ImportWarnings {
	public List<String> getImportWarnings();
}
