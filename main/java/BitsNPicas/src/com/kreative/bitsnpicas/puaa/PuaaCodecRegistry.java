package com.kreative.bitsnpicas.puaa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class PuaaCodecRegistry {
	public static final PuaaCodecRegistry instance = new PuaaCodecRegistry();
	
	private final SortedMap<String,PuaaCodec> codecs = new TreeMap<String,PuaaCodec>();
	
	private PuaaCodecRegistry() {
		addCodec(new ArabicShapingCodec());
		addCodec(new BidiBracketsCodec());
		addCodec(new BidiMirroringCodec());
		addCodec(new BlocksCodec());
		addCodec(new CompositionExclusionsCodec());
		addCodec(new DerivedAgeCodec());
		addCodec(new EastAsianWidthCodec());
		addCodec(new EmojiDataCodec());
		addCodec(new EquivalentUnifiedIdeographCodec());
		addCodec(new GraphemeBreakPropertyCodec());
		addCodec(new HangulSyllableTypeCodec());
		addCodec(new IndicPositionalCategoryCodec());
		addCodec(new IndicSyllabicCategoryCodec());
		addCodec(new JamoCodec());
		addCodec(new LineBreakCodec());
		addCodec(new NameAliasesCodec());
		addCodec(new NushuSourcesCodec());
		addCodec(new PropListCodec());
		addCodec(new ScriptsCodec());
		addCodec(new ScriptExtensionsCodec());
		addCodec(new SentenceBreakPropertyCodec());
		addCodec(new SpecialCasingCodec());
		addCodec(new TangutSourcesCodec());
		addCodec(new UnicodeDataCodec());
		addCodec(new UnihanDictionaryIndicesCodec());
		addCodec(new UnihanDictionaryLikeDataCodec());
		addCodec(new UnihanIRGSourcesCodec());
		addCodec(new UnihanNumericValuesCodec());
		addCodec(new UnihanOtherMappingsCodec());
		addCodec(new UnihanRadicalStrokeCountsCodec());
		addCodec(new UnihanReadingsCodec());
		addCodec(new UnihanVariantsCodec());
		addCodec(new VerticalOrientationCodec());
		addCodec(new WordBreakPropertyCodec());
	}
	
	public void addCodec(PuaaCodec codec) {
		this.codecs.put(codec.getFileName().toLowerCase(), codec);
	}
	
	public PuaaCodec getCodec(String fileName) {
		return this.codecs.get(fileName.toLowerCase());
	}
	
	public Collection<PuaaCodec> getCodecs() {
		return this.codecs.values();
	}
	
	public void printFileNames() {
		List<String> fileNames = new ArrayList<String>();
		int longestFileName = 0;
		for (PuaaCodec codec : this.codecs.values()) {
			String fileName = codec.getFileName();
			fileNames.add(fileName);
			if (fileName.length() > longestFileName) {
				longestFileName = fileName.length();
			}
		}
		
		int columns = 80 / (longestFileName + 2);
		if (columns < 1) { columns = 1; longestFileName = 0; }
		int rows = (fileNames.size() + columns - 1) / columns;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				int k = j * rows + i;
				if (k < fileNames.size()) {
					String fileName = fileNames.get(k);
					while (fileName.length() < longestFileName) fileName += " ";
					System.out.print("  " + fileName);
				}
			}
			System.out.println();
		}
	}
}
