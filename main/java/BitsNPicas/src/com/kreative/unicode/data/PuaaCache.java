package com.kreative.unicode.data;

import java.awt.Font;
import java.awt.font.OpenType;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import com.kreative.unicode.ttflib.FindOpenType;
import com.kreative.unicode.ttflib.PuaaTable;
import com.kreative.unicode.ttflib.TtfFile;

public class PuaaCache {
	private static final Map<String,PuaaTable> resCache = new HashMap<String,PuaaTable>();
	private static final Map<File,PuaaTable> fileCache = new HashMap<File,PuaaTable>();
	private static final Map<Font,PuaaTable> fontCache = new HashMap<Font,PuaaTable>();
	
	public static synchronized PuaaTable getPuaaTable(String res) {
		if (resCache.containsKey(res)) return resCache.get(res);
		
		try {
			TtfFile ttf = new TtfFile(PuaaCache.class.getResourceAsStream(res));
			PuaaTable puaa = ttf.getTableAs(PuaaTable.class, "PUAA");
			resCache.put(res, puaa);
			return puaa;
		} catch (Exception e) {}
		
		resCache.put(res, null);
		return null;
	}
	
	public static synchronized PuaaTable getPuaaTable(File file) {
		if (fileCache.containsKey(file)) return fileCache.get(file);
		
		try {
			TtfFile ttf = new TtfFile(file);
			PuaaTable puaa = ttf.getTableAs(PuaaTable.class, "PUAA");
			fileCache.put(file, puaa);
			return puaa;
		} catch (Exception e) {}
		
		fileCache.put(file, null);
		return null;
	}
	
	public static synchronized PuaaTable getPuaaTable(Font font) {
		if (fontCache.containsKey(font)) return fontCache.get(font);
		
		try {
			OpenType ot = FindOpenType.forFont(font);
			if (ot != null) {
				byte[] data = ot.getFontTable("PUAA");
				if (data != null && data.length > 0) {
					PuaaTable puaa = new PuaaTable(data);
					fontCache.put(font, puaa);
					return puaa;
				}
			}
		} catch (Exception e) {}
		
		fontCache.put(font, null);
		return null;
	}
}
