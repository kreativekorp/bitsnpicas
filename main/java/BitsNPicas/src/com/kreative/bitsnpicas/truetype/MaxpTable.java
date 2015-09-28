package com.kreative.bitsnpicas.truetype;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class MaxpTable extends TrueTypeTable {
	public static final int VERSION_DEFAULT = 0x00010000;
	
	public int version = VERSION_DEFAULT;
	
	/** Number of glyphs in the font. */
	public int numGlyphs = 0;
	
	/** Maximum number of points in any non-compound glyph. */
	public int maxPoints = 0;
	
	/** Maximum number of contours in any non-compound glyph. */
	public int maxContours = 0;
	
	/** Maximum number of points in any compound glyph. */
	public int maxComponentPoints = 0;
	
	/** Maximum number of contours in any compound glyph. */
	public int maxComponentContours = 0;
	
	public int maxZones = 2;
	
	/** Maximum number of points in Twilight Zone (Z0). */
	public int maxTwilightPoints = 0;
	
	/** Maximum number of variables. */
	public int maxStorage = 0;
	
	/** Maximum number of function definitions. */
	public int maxFunctionDefs = 0;
	
	/** Maximum number of instruction definitions. */
	public int maxInstructionDefs = 0;
	
	/** Maximum stack depth. */
	public int maxStackElements = 0;
	
	/** Maximum byte count of glyph instructions. */
	public int maxSizeOfInstructions = 0;
	
	/** Maximum number of glyphs in any compound glyph. */
	public int maxComponentElements = 0;
	
	/** Maximum levels of recursion in any compound glyph. */
	public int maxComponentDepth = 0;
	
	@Override
	public String tableName() {
		return "maxp";
	}
	
	@Override
	public String[] dependencyNames() {
		return new String[]{};
	}
	
	@Override
	protected void compile(DataOutputStream out, TrueTypeTable[] dependencies) throws IOException {
		out.writeInt(version);
		out.writeShort(numGlyphs);
		out.writeShort(maxPoints);
		out.writeShort(maxContours);
		out.writeShort(maxComponentPoints);
		out.writeShort(maxComponentContours);
		out.writeShort(maxZones);
		out.writeShort(maxTwilightPoints);
		out.writeShort(maxStorage);
		out.writeShort(maxFunctionDefs);
		out.writeShort(maxInstructionDefs);
		out.writeShort(maxStackElements);
		out.writeShort(maxSizeOfInstructions);
		out.writeShort(maxComponentElements);
		out.writeShort(maxComponentDepth);
	}
	
	@Override
	protected void decompile(DataInputStream in, int length, TrueTypeTable[] dependencies) throws IOException {
		version = in.readInt();
		numGlyphs = in.readUnsignedShort();
		maxPoints = in.readUnsignedShort();
		maxContours = in.readUnsignedShort();
		maxComponentPoints = in.readUnsignedShort();
		maxComponentContours = in.readUnsignedShort();
		maxZones = in.readUnsignedShort();
		maxTwilightPoints = in.readUnsignedShort();
		maxStorage = in.readUnsignedShort();
		maxFunctionDefs = in.readUnsignedShort();
		maxInstructionDefs = in.readUnsignedShort();
		maxStackElements = in.readUnsignedShort();
		maxSizeOfInstructions = in.readUnsignedShort();
		maxComponentElements = in.readUnsignedShort();
		maxComponentDepth = in.readUnsignedShort();
	}
}
