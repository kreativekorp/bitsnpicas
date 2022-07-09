package com.kreative.unicode.ttfbin;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.kreative.unicode.ttflib.TtcFile;
import com.kreative.unicode.ttflib.TtcFont;
import com.kreative.unicode.ttflib.TtfTable;

public class TtcUnpack {
	public static void main(String[] args) {
		for (String arg : args) {
			System.out.println(arg);
			try {
				File file = new File(arg);
				TtcFile ttc = new TtcFile(file);
				List<TtcFont> fonts = ttc.getFonts();
				for (int i = 0; i < fonts.size(); i++) {
					TtcFont font = fonts.get(i);
					List<TtfTable> tables = font.getTables();
					List<Integer> offsets = new ArrayList<Integer>();
					int offset = 12 + 16 * tables.size();
					for (TtfTable table : tables) {
						offsets.add(offset);
						offset += table.getLength() + 3;
						offset &= ~3;
					}
					String newName = file.getName() + "." + i + ".ttf";
					System.out.println("\t" + newName);
					File newFile = new File(file.getParentFile(), newName);
					FileOutputStream fos = new FileOutputStream(newFile);
					DataOutputStream out = new DataOutputStream(fos);
					out.writeInt(font.getScaler());
					out.writeShort(font.getCount());
					out.writeShort(font.getSearchRange());
					out.writeShort(font.getEntrySelector());
					out.writeShort(font.getRangeShift());
					for (int j = 0; j < tables.size(); j++) {
						out.writeInt(tables.get(j).getTag());
						out.writeInt(tables.get(j).getChecksum());
						out.writeInt(offsets.get(j));
						out.writeInt(tables.get(j).getLength());
					}
					for (TtfTable table : tables) {
						out.write(table.getData());
						int p = table.getLength() & 3;
						if (p > 0) while (p < 4) { out.write(0); p++; }
					}
					out.close();
					fos.close();
				}
			} catch (IOException e) {
				System.out.println("\tERROR: " + e);
			}
		}
	}
}
