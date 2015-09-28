package com.kreative.bitsnpicas.truetype;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// FIXME THIS IS CURRENTLY BROKEN. SO, SO BROKEN.
public class GlyfTableEntry {
	public static final int FLAG_ON_CURVE = 0x01;
	public static final int FLAG_X_SHORT_VECTOR = 0x02;
	public static final int FLAG_Y_SHORT_VECTOR = 0x04;
	public static final int FLAG_REPEAT = 0x08;
	public static final int FLAG_THIS_X_IS_SAME = 0x10;
	public static final int FLAG_POSITIVE_X_SHORT_VECTOR = 0x10;
	public static final int FLAG_THIS_Y_IS_SAME = 0x20;
	public static final int FLAG_POSITIVE_Y_SHORT_VECTOR = 0x20;
	public static final int FLAG_RESERVED_1 = 0x40;
	public static final int FLAG_RESERVED_2 = 0x80;
	
	public static final int COMPONENT_FLAG_ARG_1_AND_2_ARE_WORDS = 0x0001;
	public static final int COMPONENT_FLAG_ARGS_ARE_XY_VALUES = 0x0002;
	public static final int COMPONENT_FLAG_ROUND_XY_TO_GRID = 0x0004;
	public static final int COMPONENT_FLAG_WE_HAVE_A_SCALE = 0x0008;
	public static final int COMPONENT_FLAG_OBSOLETE = 0x0010;
	public static final int COMPONENT_FLAG_MORE_COMPONENTS = 0x0020;
	public static final int COMPONENT_FLAG_WE_HAVE_AN_X_AND_Y_SCALE = 0x0040;
	public static final int COMPONENT_FLAG_WE_HAVE_A_TWO_BY_TWO = 0x0080;
	public static final int COMPONENT_FLAG_WE_HAVE_INSTRUCTIONS = 0x0100;
	public static final int COMPONENT_FLAG_USE_MY_METRICS = 0x0200;
	public static final int COMPONENT_FLAG_OVERLAP_COMPOUND = 0x0400;
	
	public int numberOfContours = 0;
	public int xMin = 0;
	public int yMin = 0;
	public int xMax = 0;
	public int yMax = 0;
	// For simple glyphs.
	public int[] endPointsOfContours = new int[0];
	public int[] instructions = new int[0];
	public int[] flags = new int[0];
	public int[] xCoordinates = new int[0];
	public int[] yCoordinates = new int[0];
	// For compound glyphs.
	public int numberOfComponents = 0;
	public int[] componentFlags = new int[0];
	public int[] componentGlyphIndex = new int[0];
	public int[] componentArgument1 = new int[0];
	public int[] componentArgument2 = new int[0];
	public double[] componentTransformA = new double[0];
	public double[] componentTransformB = new double[0];
	public double[] componentTransformC = new double[0];
	public double[] componentTransformD = new double[0];
	
	public void compile(DataOutputStream out) throws IOException {
		out.writeShort(numberOfContours);
		out.writeShort(xMin);
		out.writeShort(yMin);
		out.writeShort(xMax);
		out.writeShort(yMax);
		if (numberOfContours >= 0) {
			// Simple glyph.
			int lastPointIndex = 0;
			for (int i = 0; i < numberOfContours; i++) {
				lastPointIndex = endPointsOfContours[i];
				out.writeShort(lastPointIndex);
			}
			out.writeShort(instructions.length);
			for (int i = 0; i < instructions.length; i++) {
				out.writeByte(instructions[i]);
			}
			for (int i = 0; i <= lastPointIndex; i++) {
				out.writeByte(flags[i]);
				if ((flags[i] & FLAG_REPEAT) != 0) {
					int repeat = 1;
					while (i + repeat <= lastPointIndex && flags[i + repeat] == (flags[i] & ~FLAG_REPEAT)) {
						repeat++;
					}
					out.writeByte(repeat - 1);
					i += repeat;
				}
			}
			int lastXCoordinate = 0;
			for (int i = 0; i <= lastPointIndex; i++) {
				if ((flags[i] & FLAG_X_SHORT_VECTOR) != 0) {
					out.writeByte(Math.abs(xCoordinates[i] - lastXCoordinate));
					lastXCoordinate = xCoordinates[i];
				} else if ((flags[i] & FLAG_THIS_X_IS_SAME) != 0) {
					lastXCoordinate = xCoordinates[i];
				} else {
					out.writeShort(xCoordinates[i] - lastXCoordinate);
					lastXCoordinate = xCoordinates[i];
				}
			}
			int lastYCoordinate = 0;
			for (int i = 0; i <= lastPointIndex; i++) {
				if ((flags[i] & FLAG_Y_SHORT_VECTOR) != 0) {
					out.writeByte(Math.abs(yCoordinates[i] - lastYCoordinate));
					lastYCoordinate = yCoordinates[i];
				} else if ((flags[i] & FLAG_THIS_Y_IS_SAME) != 0) {
					lastYCoordinate = yCoordinates[i];
				} else {
					out.writeShort(yCoordinates[i] - lastYCoordinate);
					lastYCoordinate = yCoordinates[i];
				}
			}
		} else if (numberOfContours == -1) {
			// Compound glyph.
			for (int i = 0; i < numberOfComponents; i++) {
				if (i < numberOfComponents - 1) {
					componentFlags[i] |= COMPONENT_FLAG_MORE_COMPONENTS;
				} else {
					componentFlags[i] &= ~COMPONENT_FLAG_MORE_COMPONENTS;
				}
				int flags = componentFlags[i];
				boolean words = ((flags & COMPONENT_FLAG_ARG_1_AND_2_ARE_WORDS) != 0);
				boolean scale = ((flags & COMPONENT_FLAG_WE_HAVE_A_SCALE) != 0);
				boolean xyScale = ((flags & COMPONENT_FLAG_WE_HAVE_AN_X_AND_Y_SCALE) != 0);
				boolean twoByTwo = ((flags & COMPONENT_FLAG_WE_HAVE_A_TWO_BY_TWO) != 0);
				out.writeShort(flags);
				out.writeShort(componentGlyphIndex[i]);
				if (words) {
					out.writeShort(componentArgument1[i]);
					out.writeShort(componentArgument2[i]);
				} else {
					out.writeByte(componentArgument1[i]);
					out.writeByte(componentArgument2[i]);
				}
				if (twoByTwo) {
					out.writeShort((int)Math.round(componentTransformA[i] * 16384.0) + ((componentTransformA[i] < 0) ? 0x8000 : 0));
					out.writeShort((int)Math.round(componentTransformB[i] * 16384.0) + ((componentTransformB[i] < 0) ? 0x8000 : 0));
					out.writeShort((int)Math.round(componentTransformC[i] * 16384.0) + ((componentTransformC[i] < 0) ? 0x8000 : 0));
					out.writeShort((int)Math.round(componentTransformD[i] * 16384.0) + ((componentTransformD[i] < 0) ? 0x8000 : 0));
				} else if (xyScale) {
					out.writeShort((int)Math.round(componentTransformA[i] * 16384.0) + ((componentTransformA[i] < 0) ? 0x8000 : 0));
					out.writeShort((int)Math.round(componentTransformD[i] * 16384.0) + ((componentTransformD[i] < 0) ? 0x8000 : 0));
				} else if (scale) {
					out.writeShort((int)Math.round(componentTransformA[i] * 16384.0) + ((componentTransformA[i] < 0) ? 0x8000 : 0));
				}
			}
		} else {
			throw new IllegalStateException("Unknown glyph format.");
		}
	}
	
	public void decompile(DataInputStream in) throws IOException {
		numberOfContours = in.readShort();
		xMin = in.readShort();
		yMin = in.readShort();
		xMax = in.readShort();
		yMax = in.readShort();
		if (numberOfContours >= 0) {
			// Simple glyph.
			endPointsOfContours = new int[numberOfContours];
			int lastPointIndex = 0;
			for (int i = 0; i < numberOfContours; i++) {
				lastPointIndex = in.readUnsignedShort();
				endPointsOfContours[i] = lastPointIndex;
			}
			instructions = new int[in.readUnsignedShort()];
			for (int i = 0; i < instructions.length; i++) {
				instructions[i] = in.readUnsignedByte();
			}
			flags = new int[lastPointIndex + 1];
			for (int i = 0; i <= lastPointIndex; i++) {
				flags[i] = in.readUnsignedByte();
				if ((flags[i] & FLAG_REPEAT) != 0) {
					int repeat = in.readUnsignedByte();
					for (int j = 1; j <= repeat; j++) {
						flags[i+j] = flags[i] & ~FLAG_REPEAT;
					}
					i += repeat;
				}
			}
			xCoordinates = new int[lastPointIndex + 1];
			int lastXCoordinate = 0;
			for (int i = 0; i <= lastPointIndex; i++) {
				if ((flags[i] & FLAG_X_SHORT_VECTOR) != 0) {
					int sign = ((flags[i] & FLAG_POSITIVE_X_SHORT_VECTOR) != 0) ? 1 : -1;
					xCoordinates[i] = lastXCoordinate + sign * in.readUnsignedByte();
					lastXCoordinate = xCoordinates[i];
				} else if ((flags[i] & FLAG_THIS_X_IS_SAME) != 0) {
					xCoordinates[i] = lastXCoordinate;
				} else {
					xCoordinates[i] = lastXCoordinate + in.readShort();
					lastXCoordinate = xCoordinates[i];
				}
			}
			yCoordinates = new int[lastPointIndex + 1];
			int lastYCoordinate = 0;
			for (int i = 0; i <= lastPointIndex; i++) {
				if ((flags[i] & FLAG_Y_SHORT_VECTOR) != 0) {
					int sign = ((flags[i] & FLAG_POSITIVE_Y_SHORT_VECTOR) != 0) ? 1 : -1;
					yCoordinates[i] = lastYCoordinate + sign * in.readUnsignedByte();
					lastYCoordinate = yCoordinates[i];
				} else if ((flags[i] & FLAG_THIS_Y_IS_SAME) != 0) {
					yCoordinates[i] = lastYCoordinate;
				} else {
					yCoordinates[i] = lastYCoordinate + in.readShort();
					lastYCoordinate = yCoordinates[i];
				}
			}
		} else if (numberOfContours == -1) {
			// Compound glyph.
			numberOfComponents = 0;
			List<Integer> componentFlags = new ArrayList<Integer>();
			List<Integer> componentGlyphIndex = new ArrayList<Integer>();
			List<Integer> componentArgument1 = new ArrayList<Integer>();
			List<Integer> componentArgument2 = new ArrayList<Integer>();
			List<Double> componentTransformA = new ArrayList<Double>();
			List<Double> componentTransformB = new ArrayList<Double>();
			List<Double> componentTransformC = new ArrayList<Double>();
			List<Double> componentTransformD = new ArrayList<Double>();
			while (true) {
				int flags = in.readUnsignedShort();
				boolean words = ((flags & COMPONENT_FLAG_ARG_1_AND_2_ARE_WORDS) != 0);
				boolean signed = ((flags & COMPONENT_FLAG_ARGS_ARE_XY_VALUES) != 0);
				boolean scale = ((flags & COMPONENT_FLAG_WE_HAVE_A_SCALE) != 0);
				boolean xyScale = ((flags & COMPONENT_FLAG_WE_HAVE_AN_X_AND_Y_SCALE) != 0);
				boolean twoByTwo = ((flags & COMPONENT_FLAG_WE_HAVE_A_TWO_BY_TWO) != 0);
				boolean more = ((flags & COMPONENT_FLAG_MORE_COMPONENTS) != 0);
				numberOfComponents++;
				componentFlags.add(flags);
				componentGlyphIndex.add(in.readUnsignedShort());
				if (words) {
					if (signed) {
						componentArgument1.add((int)in.readShort());
						componentArgument2.add((int)in.readShort());
					} else {
						componentArgument1.add(in.readUnsignedShort());
						componentArgument2.add(in.readUnsignedShort());
					}
				} else {
					if (signed) {
						componentArgument1.add((int)in.readByte());
						componentArgument2.add((int)in.readByte());
					} else {
						componentArgument1.add(in.readUnsignedByte());
						componentArgument2.add(in.readUnsignedByte());
					}
				}
				double transformA;
				double transformB;
				double transformC;
				double transformD;
				if (twoByTwo) {
					int a = in.readShort(); transformA = Math.signum(a) * (a & 0x7FFF) / 16384.0;
					int b = in.readShort(); transformB = Math.signum(b) * (b & 0x7FFF) / 16384.0;
					int c = in.readShort(); transformC = Math.signum(c) * (c & 0x7FFF) / 16384.0;
					int d = in.readShort(); transformD = Math.signum(d) * (d & 0x7FFF) / 16384.0;
				} else if (xyScale) {
					int a = in.readShort(); transformA = Math.signum(a) * (a & 0x7FFF) / 16384.0;
					transformB = 0.0;
					transformC = 0.0;
					int d = in.readShort(); transformD = Math.signum(d) * (d & 0x7FFF) / 16384.0;
				} else if (scale) {
					int a = in.readShort(); transformA = Math.signum(a) * (a & 0x7FFF) / 16384.0;
					transformB = 0.0;
					transformC = 0.0;
					transformD = transformA;
				} else {
					transformA = 1.0;
					transformB = 0.0;
					transformC = 0.0;
					transformD = 1.0;
				}
				componentTransformA.add(transformA);
				componentTransformB.add(transformB);
				componentTransformC.add(transformC);
				componentTransformD.add(transformD);
				if (!more) break;
			}
			this.componentFlags = new int[numberOfComponents];
			this.componentGlyphIndex = new int[numberOfComponents];
			this.componentArgument1 = new int[numberOfComponents];
			this.componentArgument2 = new int[numberOfComponents];
			this.componentTransformA = new double[numberOfComponents];
			this.componentTransformB = new double[numberOfComponents];
			this.componentTransformC = new double[numberOfComponents];
			this.componentTransformD = new double[numberOfComponents];
			for (int i = 0; i < numberOfComponents; i++) {
				this.componentFlags[i] = componentFlags.get(i);
				this.componentGlyphIndex[i] = componentGlyphIndex.get(i);
				this.componentArgument1[i] = componentArgument1.get(i);
				this.componentArgument2[i] = componentArgument2.get(i);
				this.componentTransformA[i] = componentTransformA.get(i);
				this.componentTransformB[i] = componentTransformB.get(i);
				this.componentTransformC[i] = componentTransformC.get(i);
				this.componentTransformD[i] = componentTransformD.get(i);
			}
		} else {
			throw new IllegalStateException("Unknown glyph format.");
		}
	}
}
