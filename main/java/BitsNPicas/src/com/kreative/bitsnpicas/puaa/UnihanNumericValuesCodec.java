package com.kreative.bitsnpicas.puaa;

import java.util.Arrays;

public class UnihanNumericValuesCodec extends AbstractUnihanCodec {
	public UnihanNumericValuesCodec() {
		super(
			"Unihan_NumericValues.txt",
			Arrays.asList(
				"kAccountingNumeric", //
				"kOtherNumeric",      //
				"kPrimaryNumeric",    //
				"kVietnameseNumeric", // added in Unicode 15.1
				"kZhuangNumeric"      // added in Unicode 15.1
			)
		);
	}
}
