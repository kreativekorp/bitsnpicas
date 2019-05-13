package com.kreative.bitsnpicas.truetype;

public enum SbitTableType {
	TRUETYPE("bdat", "bloc", 0x00020000),
	OPENTYPE("EBDT", "EBLC", 0x00020000),
	COLOR("CBDT", "CBLC", 0x00030000);
	
	public final String dataTableName;
	public final String locTableName;
	public final int version;
	
	private SbitTableType(String dtn, String ltn, int vers) {
		this.dataTableName = dtn;
		this.locTableName = ltn;
		this.version = vers;
	}
}
