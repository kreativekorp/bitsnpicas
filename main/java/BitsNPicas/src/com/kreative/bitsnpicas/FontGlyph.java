package com.kreative.bitsnpicas;

public abstract class FontGlyph {
	/*
	 * |            glyph            |
	 * |   glyph  <-width->          |
	 * |<-offset->+-------+ ^        |
	 * |        ^ |      *| |        |
	 * |        | | ***** | |        |
	 * |  glyph | |*    * | | glyph  |
	 * | ascent | |*    * | | height |
	 * |        v | ***** | |        |
	 * |==========|=====*=|=|========|
	 * |        ^ |     * | |        |
	 * |  glyph | | ****  | |        |
	 * |descent v +-------+ v        |
	 * |<------character width------>|
	 */
	
	public abstract int getGlyphOffset();
	public abstract int getGlyphWidth();
	public abstract int getGlyphHeight();
	public abstract int getGlyphAscent();
	public abstract int getGlyphDescent();
	public abstract int getCharacterWidth();
	
	public abstract double getGlyphOffset2D();
	public abstract double getGlyphWidth2D();
	public abstract double getGlyphHeight2D();
	public abstract double getGlyphAscent2D();
	public abstract double getGlyphDescent2D();
	public abstract double getCharacterWidth2D();
}
