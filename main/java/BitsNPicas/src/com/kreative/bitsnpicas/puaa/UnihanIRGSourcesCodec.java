package com.kreative.bitsnpicas.puaa;

import java.util.Arrays;

public class UnihanIRGSourcesCodec extends AbstractUnihanCodec {
	public UnihanIRGSourcesCodec() {
		super(
			"Unihan_IRGSources.txt",
			Arrays.asList(
				"kCompatibilityVariant", //
				"kIICore",               //
				"kIRG_GSource",          //
				"kIRG_HSource",          //
				"kIRG_JSource",          //
				"kIRG_KPSource",         //
				"kIRG_KSource",          //
				"kIRG_MSource",          // added in Unicode 5.2
				"kIRG_SSource",          // added in Unicode 13.0
				"kIRG_TSource",          //
				"kIRG_UKSource",         // added in Unicode 13.0
				"kIRG_USource",          //
				"kIRG_VSource",          //
				"kRSUnicode",            //
				"kTotalStrokes"          //
			)
		);
	}
}
