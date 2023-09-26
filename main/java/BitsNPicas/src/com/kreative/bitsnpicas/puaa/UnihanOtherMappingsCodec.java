package com.kreative.bitsnpicas.puaa;

import java.util.Arrays;

public class UnihanOtherMappingsCodec extends AbstractUnihanCodec {
	public UnihanOtherMappingsCodec() {
		super(
			"Unihan_OtherMappings.txt",
			Arrays.asList(
				"kBigFive",              //
				"kCCCII",                //
				"kCNS1986",              //
				"kCNS1992",              //
				"kEACC",                 //
				"kGB0",                  //
				"kGB1",                  //
				"kGB3",                  //
				"kGB5",                  //
				"kGB7",                  //
				"kGB8",                  //
				"kHKSCS",                // removed in Unicode 15.1
				"kIBMJapan",             //
				"kJa",                   // added in Unicode 8.0
				"kJinmeiyoKanji",        // added in Unicode 11.0
				"kJis0",                 //
				"kJis1",                 //
				"kJIS0213",              //
				"kJoyoKanji",            // added in Unicode 11.0
				"kKPS0",                 // removed in Unicode 15.1
				"kKPS1",                 // removed in Unicode 15.1
				"kKSC0",                 // removed in Unicode 15.1
				"kKSC1",                 // removed in Unicode 15.1
				"kKoreanEducationHanja", // added in Unicode 11.0
				"kKoreanName",           // added in Unicode 11.0
				"kMainlandTelegraph",    //
				"kPseudoGB1",            //
				"kTaiwanTelegraph",      //
				"kTGH",                  // added in Unicode 11.0
				"kXerox"                 //
			)
		);
	}
}
