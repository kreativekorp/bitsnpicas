package com.kreative.bitsnpicas.geos;

import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConvertFile implements CBMConstants {
	public CBMDirectoryBlock directoryBlock;
	public GEOSInfoBlock infoBlock;
	public VLIRRecordBlock recordBlock;
	public byte[] sequentialData;
	public List<byte[]> vlirData;
	
	public ConvertFile() {
		clear(CBM_FILE_TYPE_CLOSED, 0, 0);
	}
	
	public ConvertFile(int cbmFileType) {
		clear(cbmFileType, 0, 0);
	}
	
	public ConvertFile(int cbmFileType, int geosFileType) {
		clear(cbmFileType, geosFileType, 0);
	}
	
	public ConvertFile(int cbmFileType, int geosFileType, int fileStructure) {
		clear(cbmFileType, geosFileType, fileStructure);
	}
	
	public void clear() {
		clear(CBM_FILE_TYPE_CLOSED, 0, 0);
	}
	
	public void clear(int cbmFileType) {
		clear(cbmFileType, 0, 0);
	}
	
	public void clear(int cbmFileType, int geosFileType) {
		clear(cbmFileType, geosFileType, 0);
	}
	
	public void clear(int cbmFileType, int geosFileType, int fileStructure) {
		directoryBlock = new CBMDirectoryBlock();
		directoryBlock.cbmFileType = cbmFileType;
		directoryBlock.geosFileType = geosFileType;
		directoryBlock.fileStructure = fileStructure;
		if (geosFileType == 0) {
			infoBlock = null;
			recordBlock = null;
			sequentialData = new byte[0];
			vlirData = null;
		} else {
			directoryBlock.sectorSize++;
			infoBlock = new GEOSInfoBlock();
			infoBlock.cbmFileType = cbmFileType;
			infoBlock.geosFileType = geosFileType;
			infoBlock.fileStructure = fileStructure;
			if (fileStructure == FILE_STRUCTURE_VLIR) {
				directoryBlock.sectorSize++;
				directoryBlock.setCommentString(CBMDirectoryBlock.COMMENT_PRG);
				recordBlock = new VLIRRecordBlock();
				sequentialData = null;
				vlirData = new ArrayList<byte[]>();
			} else {
				directoryBlock.setCommentString(CBMDirectoryBlock.COMMENT_SEQ);
				recordBlock = null;
				sequentialData = new byte[0];
				vlirData = null;
			}
		}
	}
	
	public void recalculate() {
		directoryBlock.sectorSize = 0;
		if (directoryBlock.geosFileType == 0) {
			directoryBlock.sectorSize += (sequentialData.length + 253) / 254;
		} else {
			directoryBlock.sectorSize++; // info block
			if (infoBlock.fileStructure == FILE_STRUCTURE_VLIR) {
				directoryBlock.sectorSize++; // record block
				recordBlock.clear();
				for (byte[] data : vlirData) {
					VLIRRecordBlock.Entry e = new VLIRRecordBlock.Entry(data.length);
					directoryBlock.sectorSize += e.sectorCount;
					recordBlock.add(e);
				}
			} else {
				directoryBlock.sectorSize += (sequentialData.length + 253) / 254;
			}
		}
	}
	
	public void read(DataInput in) throws IOException {
		directoryBlock = new CBMDirectoryBlock();
		directoryBlock.read(in);
		if (directoryBlock.geosFileType == 0) {
			infoBlock = null;
			recordBlock = null;
			sequentialData = readRemainder(in);
			vlirData = null;
		} else {
			infoBlock = new GEOSInfoBlock();
			infoBlock.read(in);
			if (infoBlock.fileStructure == FILE_STRUCTURE_VLIR) {
				recordBlock = new VLIRRecordBlock();
				recordBlock.read(in);
				sequentialData = null;
				vlirData = new ArrayList<byte[]>();
				int lastFillLength = 0;
				for (VLIRRecordBlock.Entry e : recordBlock) {
					byte[] data = new byte[e.length];
					if (e.length > 0) {
						in.readFully(new byte[lastFillLength]);
						in.readFully(data);
						lastFillLength = e.sectorCount * 254 - e.length;
					}
					vlirData.add(data);
				}
			} else {
				recordBlock = null;
				sequentialData = readRemainder(in);
				vlirData = null;
			}
		}
	}
	
	public void write(DataOutput out) throws IOException {
		directoryBlock.write(out);
		if (directoryBlock.geosFileType == 0) {
			out.write(sequentialData);
		} else {
			infoBlock.write(out);
			if (infoBlock.fileStructure == FILE_STRUCTURE_VLIR) {
				recordBlock.write(out);
				int lastFillLength = 0;
				for (byte[] data : vlirData) {
					if (data.length > 0) {
						out.write(new byte[lastFillLength]);
						out.write(data);
						int sectorCount = (data.length + 253) / 254;
						lastFillLength = sectorCount * 254 - data.length;
					}
				}
			} else {
				out.write(sequentialData);
			}
		}
	}
	
	private static byte[] readRemainder(DataInput in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		while (true) {
			try { out.write(in.readByte()); }
			catch (EOFException e) { break; }
		}
		return out.toByteArray();
	}
}
