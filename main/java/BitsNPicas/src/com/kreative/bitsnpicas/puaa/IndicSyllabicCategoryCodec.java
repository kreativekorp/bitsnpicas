package com.kreative.bitsnpicas.puaa;

import java.util.Arrays;

public class IndicSyllabicCategoryCodec extends AbstractCategoryCodec {
	public IndicSyllabicCategoryCodec() {
		super(
			"IndicSyllabicCategory.txt",
			"Indic_Syllabic_Category",
			Arrays.asList(
				"Bindu",
				"Visarga",
				"Avagraha",
				"Nukta",
				"Virama",
				"Pure_Killer",
				"Invisible_Stacker",
				"Vowel_Independent",
				"Vowel_Dependent",
				"Vowel",
				"Consonant_Placeholder",
				"Consonant",
				"Consonant_Dead",
				"Consonant_With_Stacker",
				"Consonant_Prefixed",
				"Consonant_Preceding_Repha",
				"Consonant_Initial_Postfixed",
				"Consonant_Succeeding_Repha",
				"Consonant_Subjoined",
				"Consonant_Medial",
				"Consonant_Final",
				"Consonant_Head_Letter",
				"Modifying_Letter",
				"Tone_Letter",
				"Tone_Mark",
				"Gemination_Mark",
				"Cantillation_Mark",
				"Register_Shifter",
				"Syllable_Modifier",
				"Consonant_Killer",
				"Non_Joiner",
				"Joiner",
				"Number_Joiner",
				"Number",
				"Brahmi_Joining_Number"
			)
		);
	}
}
