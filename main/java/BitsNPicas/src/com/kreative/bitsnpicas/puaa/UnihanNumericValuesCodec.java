package com.kreative.bitsnpicas.puaa;

import java.util.Arrays;

public class UnihanNumericValuesCodec extends AbstractUnihanCodec {
	public UnihanNumericValuesCodec() {
		super(
			"Unihan_NumericValues.txt",
			Arrays.asList(
				"kAccountingNumeric",
				"kOtherNumeric",
				"kPrimaryNumeric"
			)
		);
	}
}
