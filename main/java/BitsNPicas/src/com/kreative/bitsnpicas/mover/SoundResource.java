package com.kreative.bitsnpicas.mover;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class SoundResource {
	public static final int SQUAREWAVESYNTH = 0x0001;
	public static final int WAVETABLESYNTH  = 0x0003;
	public static final int SAMPLEDSYNTH    = 0x0005;
	
	public static final int INITCHANLEFT    = 0x0002;
	public static final int INITCHANRIGHT   = 0x0003;
	public static final int WAVECHANNEL0    = 0x0004;
	public static final int WAVECHANNEL1    = 0x0005;
	public static final int WAVECHANNEL2    = 0x0006;
	public static final int WAVECHANNEL3    = 0x0007;
	public static final int INITMONO        = 0x0080;
	public static final int INITSTEREO      = 0x00C0;
	public static final int INITMACE3       = 0x0300;
	public static final int INITMACE6       = 0x0400;
	public static final int INITNOINTERP    = 0x0004;
	public static final int INITNODROP      = 0x0008;
	public static final int INITPANMASK     = 0x0003;
	public static final int INITSRATEMASK   = 0x0030;
	public static final int INITSTEREOMASK  = 0x00C0;
	public static final int INITCOMPMASK    = 0xFF00;
	
	public static final int NULLCMD         = 0x0000;
	public static final int QUIETCMD        = 0x0003;
	public static final int FLUSHCMD        = 0x0004;
	public static final int REINITCMD       = 0x0005;
	public static final int WAITCMD         = 0x000A;
	public static final int PAUSECMD        = 0x000B;
	public static final int RESUMECMD       = 0x000C;
	public static final int CALLBACKCMD     = 0x000D;
	public static final int SYNCCMD         = 0x000E;
	public static final int AVAILABLECMD    = 0x0018;
	public static final int VERSIONCMD      = 0x0019;
	public static final int TOTALLOADCMD    = 0x001A;
	public static final int LOADCMD         = 0x001B;
	public static final int FREQDURATIONCMD = 0x0028;
	public static final int RESTCMD         = 0x0029;
	public static final int FREQCMD         = 0x002A;
	public static final int AMPCMD          = 0x002B;
	public static final int TIMBRECMD       = 0x002C;
	public static final int GETAMPCMD       = 0x002D;
	public static final int VOLUMECMD       = 0x002E;
	public static final int GETVOLUMECMD    = 0x002F;
	public static final int WAVETABLECMD    = 0x003C;
	public static final int SOUNDCMD        = 0x0050;
	public static final int BUFFERCMD       = 0x0051;
	public static final int RATECMD         = 0x0052;
	public static final int GETRATECMD      = 0x0055;
	public static final int COMMANDMASK     = 0x7FFF;
	public static final int DATAOFFSETFLAG  = 0x8000;
	
	public static final int RATE_44KHZ = 0xAC440000;
	public static final int RATE_22KHZ = 0x56EE8BA3;
	public static final int RATE_22050 = 0x56220000;
	public static final int RATE_11KHZ = 0x2B7745D1;
	public static final int RATE_11025 = 0x2B110000;
	
	public static final int ENCODE_STANDARD   =  0;
	public static final int ENCODE_EXTENDED   = -1;
	public static final int ENCODE_COMPRESSED = -2;
	
	public static final short EXPONENT_44KHZ = 0x400E;
	public static final long  MANTISSA_44KHZ = 0xAC44000000000000L;
	public static final short EXPONENT_22KHZ = 0x400D;
	public static final long  MANTISSA_22KHZ = 0xADDD1745D145826BL;
	public static final short EXPONENT_22050 = 0x400D;
	public static final long  MANTISSA_22050 = 0xAC44000000000000L;
	public static final short EXPONENT_11KHZ = 0x400C;
	public static final long  MANTISSA_11KHZ = 0xADDD1745D145826BL;
	public static final short EXPONENT_11025 = 0x400C;
	public static final long  MANTISSA_11025 = 0xAC44000000000000L;
	
	public static final int FORMAT_NONE      = 0x4E4F4E45; // NONE
	public static final int FORMAT_ACE_2TO1  = 0x41434532; // ACE2
	public static final int FORMAT_ACE_8TO3  = 0x41434538; // ACE8
	public static final int FORMAT_MACE_3TO1 = 0x4D414333; // MAC3
	public static final int FORMAT_MACE_6TO1 = 0x4D414336; // MAC6
	
	public static final int COMPID_NONE       =  0;
	public static final int COMPID_ACE_2TO1   =  1;
	public static final int COMPID_ACE_8TO3   =  2;
	public static final int COMPID_MACE_3TO1  =  3;
	public static final int COMPID_MACE_6TO1  =  4;
	public static final int COMPID_USE_FORMAT = -1;
	
	private static final int RIFF = 0x52494646; // RIFF
	private static final int WAVE = 0x57415645; // WAVE
	private static final int FMT  = 0x666D7420; // fmt
	private static final int DATA = 0x64617461; // data
	private static final int CYNH = 0x43796E68; // Cynh
	
	private static final int FORM = 0x464F524D; // FORM
	private static final int AIFC = 0x41494643; // AIFC
	private static final int FVER = 0x46564552; // FVER
	private static final int COMM = 0x434F4D4D; // COMM
	private static final int SSND = 0x53534E44; // SSND
	
	private static final byte[] COMPNAME_NONE      = {0x0E,'n','o','t',' ','c','o','m','p','r','e','s','s','e','d',0};
	private static final byte[] COMPNAME_ACE_2TO1  = {0x0A,'A','C','E',' ','2','-','t','o','-','1', 0 , 0 , 0 , 0 ,0};
	private static final byte[] COMPNAME_ACE_8TO3  = {0x0A,'A','C','E',' ','8','-','t','o','-','3', 0 , 0 , 0 , 0 ,0};
	private static final byte[] COMPNAME_MACE_3TO1 = {0x0B,'M','A','C','E',' ','3','-','t','o','-','1', 0 , 0 , 0 ,0};
	private static final byte[] COMPNAME_MACE_6TO1 = {0x0B,'M','A','C','E',' ','6','-','t','o','-','1', 0 , 0 , 0 ,0};
	
	public static final class DataFormat {
		public final int synthId;
		public final int initOption;
		private DataFormat(DataInputStream in) throws IOException {
			synthId = in.readShort();
			initOption = in.readInt();
		}
	}
	
	public static final class SoundCommand {
		public final int cmd;
		public final int param1;
		public final int param2;
		private SoundCommand(DataInputStream in) throws IOException {
			cmd = in.readShort();
			param1 = in.readShort();
			param2 = in.readInt();
		}
	}
	
	public static final class SampledSoundHeader {
		public final int samplePtr;
		public final int length;
		public final int sampleRate;
		public final int loopStart;
		public final int loopEnd;
		public final int encode;
		public final int baseFrequency;
		private SampledSoundHeader(DataInputStream in) throws IOException {
			samplePtr = in.readInt();
			length = in.readInt();
			sampleRate = in.readInt();
			loopStart = in.readInt();
			loopEnd = in.readInt();
			encode = in.readByte();
			baseFrequency = in.readByte();
		}
	}
	
	public static final class ExtendedSoundHeader {
		public final int samplePtr;
		public final int numChannels;
		public final int sampleRate;
		public final int loopStart;
		public final int loopEnd;
		public final int encode;
		public final int baseFrequency;
		public final int numFrames;
		public final short sampleRateExponent;
		public final long sampleRateMantissa;
		public final int markerChunk;
		public final int instrumentChunks;
		public final int aesRecording;
		public final int sampleSize;
		public final int futureUse1;
		public final int futureUse2;
		public final int futureUse3;
		public final int futureUse4;
		private ExtendedSoundHeader(SampledSoundHeader sh, DataInputStream in) throws IOException {
			samplePtr = sh.samplePtr;
			numChannels = sh.length;
			sampleRate = sh.sampleRate;
			loopStart = sh.loopStart;
			loopEnd = sh.loopEnd;
			encode = sh.encode;
			baseFrequency = sh.baseFrequency;
			numFrames = in.readInt();
			sampleRateExponent = in.readShort();
			sampleRateMantissa = in.readLong();
			markerChunk = in.readInt();
			instrumentChunks = in.readInt();
			aesRecording = in.readInt();
			sampleSize = in.readShort();
			futureUse1 = in.readShort();
			futureUse2 = in.readInt();
			futureUse3 = in.readInt();
			futureUse4 = in.readInt();
		}
	}
	
	public static final class CompressedSoundHeader {
		public final int samplePtr;
		public final int numChannels;
		public final int sampleRate;
		public final int loopStart;
		public final int loopEnd;
		public final int encode;
		public final int baseFrequency;
		public final int numFrames;
		public final short sampleRateExponent;
		public final long sampleRateMantissa;
		public final int markerChunk;
		public final int format;
		public final int futureUse2;
		public final int stateVars;
		public final int leftOverSamples;
		public final int compressionId;
		public final int packetSize;
		public final int snthId;
		public final int sampleSize;
		private CompressedSoundHeader(SampledSoundHeader sh, DataInputStream in) throws IOException {
			samplePtr = sh.samplePtr;
			numChannels = sh.length;
			sampleRate = sh.sampleRate;
			loopStart = sh.loopStart;
			loopEnd = sh.loopEnd;
			encode = sh.encode;
			baseFrequency = sh.baseFrequency;
			numFrames = in.readInt();
			sampleRateExponent = in.readShort();
			sampleRateMantissa = in.readLong();
			markerChunk = in.readInt();
			format = in.readInt();
			futureUse2 = in.readInt();
			stateVars = in.readInt();
			leftOverSamples = in.readInt();
			compressionId = in.readShort();
			packetSize = in.readShort();
			snthId = in.readShort();
			sampleSize = in.readShort();
		}
		private byte[] decompress(byte[] data) {
			switch (compressionId) {
				case COMPID_USE_FORMAT:
					switch (format) {
						case FORMAT_NONE: return data;
						case FORMAT_MACE_3TO1: return MACEDecoder.decompressMACE3(data, numChannels);
						case FORMAT_MACE_6TO1: return MACEDecoder.decompressMACE6(data, numChannels);
						default: return null;
					}
				case COMPID_NONE: return data;
				case COMPID_MACE_3TO1: return MACEDecoder.decompressMACE3(data, numChannels);
				case COMPID_MACE_6TO1: return MACEDecoder.decompressMACE6(data, numChannels);
				default: return null;
			}
		}
	}
	
	public static final class SampledSound {
		public final SampledSoundHeader std;
		public final ExtendedSoundHeader ext;
		public final CompressedSoundHeader cmp;
		public final int dataLength;
		public final byte[] data;
		public SampledSound(byte[] data) throws IOException {
			this(new DataInputStream(new ByteArrayInputStream(data)));
		}
		private SampledSound(DataInputStream in) throws IOException {
			std = new SampledSoundHeader(in);
			switch (std.encode) {
				case ENCODE_STANDARD:
					ext = null; cmp = null;
					dataLength = std.length;
					data = new byte[dataLength];
					in.readFully(data);
					break;
				case ENCODE_EXTENDED:
					ext = new ExtendedSoundHeader(std, in); cmp = null;
					dataLength = ext.numChannels * ext.numFrames * ((ext.sampleSize + 7) / 8);
					data = new byte[dataLength];
					in.readFully(data);
					break;
				case ENCODE_COMPRESSED:
					ext = null; cmp = new CompressedSoundHeader(std, in);
					dataLength = cmp.numChannels * cmp.numFrames * ((cmp.packetSize + 7) / 8);
					data = new byte[dataLength];
					in.readFully(data);
					break;
				default:
					ext = null; cmp = null;
					dataLength = 0; data = null;
					break;
			}
		}
	}
	
	public final int format;
	public final int refCon;
	public final List<DataFormat> dataFormats;
	public final List<SoundCommand> soundCommands;
	public final Map<Integer,byte[]> soundData;
	
	public SoundResource(byte[] data) throws IOException {
		this(new DataInputStream(new ByteArrayInputStream(data)), data.length);
	}
	
	private SoundResource(DataInputStream in, int length) throws IOException {
		this.format = in.readShort();
		this.refCon = in.readShort();
		if (this.format == 1) {
			List<DataFormat> dataFormats = new ArrayList<DataFormat>(refCon);
			for (int i = 0; i < refCon; i++) dataFormats.add(new DataFormat(in));
			this.dataFormats = Collections.unmodifiableList(dataFormats);
		} else {
			this.dataFormats = null;
		}
		int numCommands = in.readShort();
		List<SoundCommand> soundCommands = new ArrayList<SoundCommand>(numCommands);
		for (int i = 0; i < numCommands; i++) soundCommands.add(new SoundCommand(in));
		this.soundCommands = Collections.unmodifiableList(soundCommands);
		
		TreeSet<Integer> dataOffsets = new TreeSet<Integer>();
		for (SoundCommand sc : soundCommands) {
			if ((sc.cmd & DATAOFFSETFLAG) != 0) {
				dataOffsets.add(sc.param2);
			}
		}
		dataOffsets.add(length);
		Map<Integer,byte[]> soundData = new HashMap<Integer,byte[]>();
		for (SoundCommand sc : soundCommands) {
			if ((sc.cmd & DATAOFFSETFLAG) != 0) {
				in.reset();
				in.skipBytes(sc.param2);
				int len = dataOffsets.higher(sc.param2) - sc.param2;
				byte[] data = new byte[len];
				in.readFully(data);
				soundData.put(sc.param2, data);
			}
		}
		this.soundData = Collections.unmodifiableMap(soundData);
	}
	
	public SampledSound toSampledSound() {
		if (format == 1 || format == 2) {
			for (SoundCommand sc : soundCommands) {
				if ((sc.cmd & DATAOFFSETFLAG) != 0) {
					int cmd = sc.cmd & COMMANDMASK;
					if (cmd == SOUNDCMD || cmd == BUFFERCMD) {
						try { return new SampledSound(soundData.get(sc.param2)); }
						catch (IOException e) { return null; }
					}
				}
			}
		}
		return null;
	}
	
	public byte[] toWav() {
		SampledSound snd = toSampledSound(); if (snd == null) return null;
		byte[] data = snd.data; if (data == null || data.length == 0) return null;
		
		int numChannels;
		int bitsPerSample;
		int bytesPerSample;
		switch (snd.std.encode) {
			case ENCODE_STANDARD:
				numChannels = 1;
				bitsPerSample = 8;
				bytesPerSample = 1;
				break;
			case ENCODE_EXTENDED:
				numChannels = snd.ext.numChannels;
				bitsPerSample = snd.ext.sampleSize;
				bytesPerSample = ((snd.ext.sampleSize + 7) / 8);
				break;
			case ENCODE_COMPRESSED:
				numChannels = snd.cmp.numChannels;
				bitsPerSample = snd.cmp.sampleSize;
				bytesPerSample = ((snd.cmp.sampleSize + 7) / 8);
				data = snd.cmp.decompress(data);
				if (data == null) return null;
				break;
			default:
				return null;
		}
		
		// If *compressed* 8-bit audio, flip signedness.
		if (data != snd.data && bytesPerSample == 1) {
			for (int i = 0; i < data.length; i++) data[i] ^= 0x80;
		}
		// If 16-bit audio, swap endianness (as WAV is a little-endian format).
		if (bytesPerSample > 1) {
			for (int i = 0; i < data.length; i += bytesPerSample) {
				for (int j = 0, k = bytesPerSample - 1; j < k; j++, k--) {
					byte t = data[i+j]; data[i+j] = data[i+k]; data[i+k] = t;
				}
			}
		}
		int padding = 0; while (((data.length + padding) & 3) != 0) padding++;
		
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			DataOutputStream out2 = new DataOutputStream(out);
			// RIFF header
			out2.writeInt(RIFF);
			out2.writeInt(Integer.reverseBytes(56 + data.length + padding));
			out2.writeInt(WAVE);
			// FMT chunk
			out2.writeInt(FMT);
			out2.writeInt(Integer.reverseBytes(16));
			out2.writeShort(Short.reverseBytes((short)1));
			out2.writeShort(Short.reverseBytes((short)numChannels));
			out2.writeInt(Integer.reverseBytes(snd.std.sampleRate >>> 16));
			out2.writeInt(Integer.reverseBytes((snd.std.sampleRate >>> 16) * bytesPerSample * numChannels));
			out2.writeShort(Short.reverseBytes((short)(bytesPerSample * numChannels)));
			out2.writeShort(Short.reverseBytes((short)bitsPerSample));
			// DATA chunk
			out2.writeInt(DATA);
			out2.writeInt(Integer.reverseBytes(data.length + padding));
			out2.write(data);
			out2.write(new byte[padding]);
			// CYNTH chunk
			out2.writeInt(CYNH);
			out2.writeInt(Integer.reverseBytes(12));
			out2.writeInt(Integer.reverseBytes(snd.std.loopStart));
			out2.writeInt(Integer.reverseBytes(snd.std.loopEnd));
			out2.writeInt(Integer.reverseBytes(snd.std.baseFrequency));
			// done
			out2.close();
			out.close();
			return out.toByteArray();
		} catch (IOException ioe) {
			return null;
		}
	}
	
	public byte[] toAiff() {
		SampledSound snd = toSampledSound(); if (snd == null) return null;
		byte[] data = snd.data; if (data == null || data.length == 0) return null;
		
		int numChannels;
		int numFrames;
		int bitsPerSample;
		short sampleRateExponent;
		long sampleRateMantissa;
		int format;
		byte[] compName;
		switch (snd.std.encode) {
			case ENCODE_STANDARD:
				numChannels = 1;
				numFrames = snd.std.length;
				bitsPerSample = 8;
				switch (snd.std.sampleRate) {
					case RATE_44KHZ: sampleRateExponent = EXPONENT_44KHZ; sampleRateMantissa = MANTISSA_44KHZ; break;
					case RATE_22KHZ: sampleRateExponent = EXPONENT_22KHZ; sampleRateMantissa = MANTISSA_22KHZ; break;
					case RATE_22050: sampleRateExponent = EXPONENT_22050; sampleRateMantissa = MANTISSA_22050; break;
					case RATE_11KHZ: sampleRateExponent = EXPONENT_11KHZ; sampleRateMantissa = MANTISSA_11KHZ; break;
					case RATE_11025: sampleRateExponent = EXPONENT_11025; sampleRateMantissa = MANTISSA_11025; break;
					default:
						double sampleRateFP = snd.std.sampleRate & 0xFFFFFFFFL;
						long sampleRateBits = Double.doubleToLongBits(sampleRateFP);
						sampleRateExponent = (short)((sampleRateBits >> 52) + 15344);
						sampleRateMantissa = (((1L << 52) | sampleRateBits) << 11);
						break;
				}
				format = FORMAT_NONE;
				compName = COMPNAME_NONE;
				break;
			case ENCODE_EXTENDED:
				numChannels = snd.ext.numChannels;
				numFrames = snd.ext.numFrames;
				bitsPerSample = snd.ext.sampleSize;
				sampleRateExponent = snd.ext.sampleRateExponent;
				sampleRateMantissa = snd.ext.sampleRateMantissa;
				format = FORMAT_NONE;
				compName = COMPNAME_NONE;
				break;
			case ENCODE_COMPRESSED:
				numChannels = snd.cmp.numChannels;
				numFrames = snd.cmp.numFrames;
				bitsPerSample = snd.cmp.sampleSize;
				sampleRateExponent = snd.cmp.sampleRateExponent;
				sampleRateMantissa = snd.cmp.sampleRateMantissa;
				switch (snd.cmp.compressionId) {
					case COMPID_USE_FORMAT:
						switch (snd.cmp.format) {
							case FORMAT_NONE: format = FORMAT_NONE; compName = COMPNAME_NONE; break;
							case FORMAT_ACE_2TO1: format = FORMAT_ACE_2TO1; compName = COMPNAME_ACE_2TO1; break;
							case FORMAT_ACE_8TO3: format = FORMAT_ACE_8TO3; compName = COMPNAME_ACE_8TO3; break;
							case FORMAT_MACE_3TO1: format = FORMAT_MACE_3TO1; compName = COMPNAME_MACE_3TO1; break;
							case FORMAT_MACE_6TO1: format = FORMAT_MACE_6TO1; compName = COMPNAME_MACE_6TO1; break;
							default: format = snd.cmp.format; compName = new byte[16]; break;
						}
						break;
					case COMPID_NONE: format = FORMAT_NONE; compName = COMPNAME_NONE; break;
					case COMPID_ACE_2TO1: format = FORMAT_ACE_2TO1; compName = COMPNAME_ACE_2TO1; break;
					case COMPID_ACE_8TO3: format = FORMAT_ACE_8TO3; compName = COMPNAME_ACE_8TO3; break;
					case COMPID_MACE_3TO1: format = FORMAT_MACE_3TO1; compName = COMPNAME_MACE_3TO1; break;
					case COMPID_MACE_6TO1: format = FORMAT_MACE_6TO1; compName = COMPNAME_MACE_6TO1; break;
					default: return null;
				}
				break;
			default:
				return null;
		}
		
		// If *uncompressed* 8-bit audio, flip signedness.
		if (format == FORMAT_NONE && bitsPerSample <= 8) {
			for (int i = 0; i < data.length; i++) data[i] ^= 0x80;
		}
		int padding = 0; while (((data.length + padding) & 3) != 0) padding++;
		
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			DataOutputStream out2 = new DataOutputStream(out);
			// form chunk
			out2.writeInt(FORM);
			out2.writeInt(98 + data.length + padding);
			out2.writeInt(AIFC);
			// format version chunk
			out2.writeInt(FVER);
			out2.writeInt(4);
			out2.writeInt(0xA2805140);
			// common chunk
			out2.writeInt(COMM);
			out2.writeInt(38);
			out2.writeShort(numChannels);
			out2.writeInt(numFrames);
			out2.writeShort(bitsPerSample);
			out2.writeShort(sampleRateExponent);
			out2.writeLong(sampleRateMantissa);
			out2.writeInt(format);
			out2.write(compName);
			// cynth chunk
			out2.writeInt(CYNH);
			out2.writeInt(12);
			out2.writeInt(snd.std.loopStart);
			out2.writeInt(snd.std.loopEnd);
			out2.writeInt(snd.std.baseFrequency);
			// sound data chunk
			out2.writeInt(SSND);
			out2.writeInt(8 + data.length);
			out2.writeInt(0);
			out2.writeInt(0);
			out2.write(data);
			out2.write(new byte[padding]);
			// done
			out2.close();
			out.close();
			return out.toByteArray();
		} catch (IOException ioe) {
			return null;
		}
	}
}
