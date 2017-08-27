package com.kreative.bitsnpicas;

public class VectorFont extends Font<VectorFontGlyph> {
	protected double ascent, descent, typoascent, typodescent, xheight, linegap;
	
	public VectorFont() {
		this.ascent = 0;
		this.descent = 0;
		this.typoascent = 0;
		this.typodescent = 0;
		this.xheight = 0;
		this.linegap = 0;
	}
	
	public VectorFont(double ascent, double descent, double typoascent, double typodescent, double xheight, double linegap) {
		this.ascent = ascent;
		this.descent = descent;
		this.typoascent = typoascent;
		this.typodescent = typodescent;
		this.xheight = xheight;
		this.linegap = linegap;
	}
	
	public int getEmAscent() { return (int)Math.ceil(ascent); }
	public double getEmAscent2D() { return ascent; }
	public int getEmDescent() { return (int)Math.ceil(descent); }
	public double getEmDescent2D() { return descent; }
	public int getLineAscent() { return (int)Math.ceil(typoascent); }
	public double getLineAscent2D() { return typoascent; }
	public int getLineDescent() { return (int)Math.ceil(typodescent); }
	public double getLineDescent2D() { return typodescent; }
	public int getXHeight() { return (int)Math.ceil(xheight); }
	public double getXHeight2D() { return xheight; }
	public int getLineGap() { return (int)Math.ceil(linegap); }
	public double getLineGap2D() { return linegap; }
	
	public void setEmAscent(int v) { ascent = v; }
	public void setEmAscent2D(double v) { ascent = v; }
	public void setEmDescent(int v) { descent = v; }
	public void setEmDescent2D(double v) { descent = v; }
	public void setLineAscent(int v) { typoascent = v; }
	public void setLineAscent2D(double v) { typoascent = v; }
	public void setLineDescent(int v) { typodescent = v; }
	public void setLineDescent2D(double v) { typodescent = v; }
	public void setXHeight(int v) { xheight = v; }
	public void setXHeight2D(double v) { xheight = v; }
	public void setLineGap(int v) { linegap = v; }
	public void setLineGap2D(double v) { linegap = v; }
}
