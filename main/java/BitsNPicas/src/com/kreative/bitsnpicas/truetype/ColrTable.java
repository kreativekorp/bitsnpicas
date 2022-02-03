package com.kreative.bitsnpicas.truetype;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ColrTable extends TrueTypeTable {
	public int version = 0;
	public BaseGlyph[] baseGlyphRecords = new BaseGlyph[0];
	public Layer[] layerRecords = new Layer[0];
	
	@Override
	public String tableName() {
		return "COLR";
	}
	
	@Override
	public String[] dependencyNames() {
		return new String[]{};
	}
	
	@Override
	protected void compile(DataOutputStream out, TrueTypeTable[] dependencies) throws IOException {
		int offset = (version < 1) ? 14 : 34;
		out.writeShort(version);
		
		if (baseGlyphRecords != null) {
			out.writeShort(baseGlyphRecords.length);
			out.writeInt(offset);
			offset += baseGlyphRecords.length * 6;
		} else {
			out.writeShort(0);
			out.writeInt(0);
		}
		
		if (layerRecords != null) {
			out.writeInt(offset);
			offset += layerRecords.length * 4;
			out.writeShort(layerRecords.length);
		} else {
			out.writeInt(0);
			out.writeShort(0);
		}
		
		if (version >= 1) {
			out.writeInt(0); // TODO baseGlyphListOffset
			out.writeInt(0); // TODO layerListOffset
			out.writeInt(0); // TODO clipListOffset
			out.writeInt(0); // TODO varIndexMapOffset
			out.writeInt(0); // TODO itemVariationStoreOffset
		}
		
		if (baseGlyphRecords != null) {
			for (BaseGlyph record : baseGlyphRecords) {
				record.write(out);
			}
		}
		
		if (layerRecords != null) {
			for (Layer record : layerRecords) {
				record.write(out);
			}
		}
		
		if (version >= 1) {
			// TODO BaseGlyphList
			// TODO LayerList
			// TODO ClipList
			// TODO DeltaSetIndexMap
			// TODO ItemVariationStore
		}
	}
	
	@Override
	protected void decompile(DataInputStream in, int length, TrueTypeTable[] dependencies) throws IOException {
		version = in.readUnsignedShort();
		int numBaseGlyphRecords = in.readUnsignedShort();
		int baseGlyphRecordsOffset = in.readInt();
		int layerRecordsOffset = in.readInt();
		int numLayerRecords = in.readUnsignedShort();
		// TODO int baseGlyphListOffset = (version < 1) ? 0 : in.readInt();
		// TODO int layerListOffset = (version < 1) ? 0 : in.readInt();
		// TODO int clipListOffset = (version < 1) ? 0 : in.readInt();
		// TODO int varIndexMapOffset = (version < 1) ? 0 : in.readInt();
		// TODO int itemVariationStoreOffset = (version < 1) ? 0 : in.readInt();
		
		if (baseGlyphRecordsOffset > 0) {
			in.reset();
			in.skipBytes(baseGlyphRecordsOffset);
			baseGlyphRecords = new BaseGlyph[numBaseGlyphRecords];
			for (int i = 0; i < numBaseGlyphRecords; i++) {
				(baseGlyphRecords[i] = new BaseGlyph()).read(in);
			}
		} else {
			baseGlyphRecords = null;
		}
		
		if (layerRecordsOffset > 0) {
			in.reset();
			in.skipBytes(layerRecordsOffset);
			layerRecords = new Layer[numLayerRecords];
			for (int i = 0; i < numLayerRecords; i++) {
				(layerRecords[i] = new Layer()).read(in);
			}
		} else {
			layerRecords = null;
		}
		
		// TODO BaseGlyphList
		// TODO LayerList
		// TODO ClipList
		// TODO DeltaSetIndexMap
		// TODO ItemVariationStore
	}
	
	public static final class BaseGlyph {
		public int glyphID;
		public int firstLayerIndex;
		public int numLayers;
		public void read(DataInputStream in) throws IOException {
			glyphID = in.readUnsignedShort();
			firstLayerIndex = in.readUnsignedShort();
			numLayers = in.readUnsignedShort();
		}
		public void write(DataOutputStream out) throws IOException {
			out.writeShort(glyphID);
			out.writeShort(firstLayerIndex);
			out.writeShort(numLayers);
		}
	}
	
	public static final class Layer {
		public int glyphID;
		public int paletteIndex;
		public void read(DataInputStream in) throws IOException {
			glyphID = in.readUnsignedShort();
			paletteIndex = in.readUnsignedShort();
		}
		public void write(DataOutputStream out) throws IOException {
			out.writeShort(glyphID);
			out.writeShort(paletteIndex);
		}
	}
}
