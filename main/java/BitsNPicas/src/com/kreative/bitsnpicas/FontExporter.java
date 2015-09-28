package com.kreative.bitsnpicas;

import java.io.*;

public interface FontExporter<T extends Font<? extends FontGlyph>> {
	public byte[] exportFontToBytes(T font) throws IOException;
	public void exportFontToStream(T font, OutputStream os) throws IOException;
	public void exportFontToFile(T font, File file) throws IOException;
}
