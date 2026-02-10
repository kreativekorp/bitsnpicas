package com.kreative.bitsnpicas;

public class VectorFont extends Font<VectorFontGlyph> {
	protected double emAscent, emDescent;
	protected double lineAscent, lineDescent;
	protected double xHeight, capHeight, lineGap;
	
	public VectorFont() {
		this.emAscent = 0;
		this.emDescent = 0;
		this.lineAscent = 0;
		this.lineDescent = 0;
		this.xHeight = 0;
		this.capHeight = 0;
		this.lineGap = 0;
	}
	
	public VectorFont(
		double emAscent, double emDescent,
		double lineAscent, double lineDescent,
		double xHeight, double capHeight, double lineGap
	) {
		this.emAscent = emAscent;
		this.emDescent = emDescent;
		this.lineAscent = lineAscent;
		this.lineDescent = lineDescent;
		this.xHeight = xHeight;
		this.capHeight = capHeight;
		this.lineGap = lineGap;
	}
	
	public int getEmAscent() { return (int)Math.ceil(emAscent); }
	public double getEmAscent2D() { return emAscent; }
	public int getEmDescent() { return (int)Math.ceil(emDescent); }
	public double getEmDescent2D() { return emDescent; }
	public int getLineAscent() { return (int)Math.ceil(lineAscent); }
	public double getLineAscent2D() { return lineAscent; }
	public int getLineDescent() { return (int)Math.ceil(lineDescent); }
	public double getLineDescent2D() { return lineDescent; }
	public int getXHeight() { return (int)Math.ceil(xHeight); }
	public double getXHeight2D() { return xHeight; }
	public int getCapHeight() { return (int)Math.ceil(capHeight); }
	public double getCapHeight2D() { return capHeight; }
	public int getLineGap() { return (int)Math.ceil(lineGap); }
	public double getLineGap2D() { return lineGap; }
	
	public void setEmAscent(int v) { emAscent = v; }
	public void setEmAscent2D(double v) { emAscent = v; }
	public void setEmDescent(int v) { emDescent = v; }
	public void setEmDescent2D(double v) { emDescent = v; }
	public void setLineAscent(int v) { lineAscent = v; }
	public void setLineAscent2D(double v) { lineAscent = v; }
	public void setLineDescent(int v) { lineDescent = v; }
	public void setLineDescent2D(double v) { lineDescent = v; }
	public void setXHeight(int v) { xHeight = v; }
	public void setXHeight2D(double v) { xHeight = v; }
	public void setCapHeight(int v) { capHeight = v; }
	public void setCapHeight2D(double v) { capHeight = v; }
	public void setLineGap(int v) { lineGap = v; }
	public void setLineGap2D(double v) { lineGap = v; }
	
	public void setXHeight2D() {
		double xh = guessXHeight2D();
		if (xh != 0) xHeight = xh;
	}
	
	public void setCapHeight2D() {
		double ch = guessCapHeight2D();
		if (ch != 0) capHeight = ch;
	}
}
