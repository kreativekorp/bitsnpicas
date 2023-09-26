package com.kreative.bitsnpicas.puaa;

import java.util.Arrays;

public class UnihanDictionaryIndicesCodec extends AbstractUnihanCodec {
	public UnihanDictionaryIndicesCodec() {
		super(
			"Unihan_DictionaryIndices.txt",
			Arrays.asList(
				"kCheungBauerIndex", // added in Unicode 5.0
				"kCihaiT",           // moved in Unicode 15.0 (from DictionaryLikeData)
				"kCowles",           //
				"kDaeJaweon",        //
				"kFennIndex",        //
				"kGSR",              //
				"kHanYu",            //
				"kIRGDaeJaweon",     //
				"kIRGDaiKanwaZiten", // removed in Unicode 15.1
				"kIRGHanyuDaZidian", //
				"kIRGKangXi",        //
				"kKangXi",           //
				"kKarlgren",         //
				"kLau",              //
				"kMatthews",         //
				"kMeyerWempe",       //
				"kMorohashi",        //
				"kNelson",           //
				"kSBGY",             //
				"kSMSZD2003Index"    // added in Unicode 15.1
			)
		);
	}
}
