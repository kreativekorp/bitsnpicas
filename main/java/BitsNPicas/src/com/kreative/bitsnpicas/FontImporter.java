package com.kreative.bitsnpicas;

import java.io.*;

public interface FontImporter<T extends Font<? extends FontGlyph>> {
	public T[] importFont(byte[] data) throws IOException;
	public T[] importFont(InputStream is) throws IOException;
	public T[] importFont(File file) throws IOException;
}
