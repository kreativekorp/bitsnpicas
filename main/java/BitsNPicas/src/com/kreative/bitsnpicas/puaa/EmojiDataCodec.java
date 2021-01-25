package com.kreative.bitsnpicas.puaa;

import java.util.Arrays;

public class EmojiDataCodec extends AbstractPropListCodec {
	public EmojiDataCodec() {
		super(
			"emoji-data.txt",
			Arrays.asList(
				"Emoji",
				"Emoji_Presentation",
				"Emoji_Modifier",
				"Emoji_Modifier_Base",
				"Emoji_Component",
				"Extended_Pictographic"
			)
		);
	}
}
