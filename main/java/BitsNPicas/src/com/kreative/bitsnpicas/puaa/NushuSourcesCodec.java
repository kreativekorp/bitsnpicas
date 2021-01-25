package com.kreative.bitsnpicas.puaa;

import java.util.Arrays;

public class NushuSourcesCodec extends AbstractUnihanCodec {
	public NushuSourcesCodec() {
		super(
			"NushuSources.txt",
			Arrays.asList(
				"kSrc_NushuDuben",
				"kReading"
			)
		);
	}
}
