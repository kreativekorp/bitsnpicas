package com.kreative.bitsnpicas.main;

import java.io.File;
import java.io.FileOutputStream;
import com.kreative.bitsnpicas.mover.SoundResource;
import com.kreative.bitsnpicas.mover.SoundResource.SampledSound;
import com.kreative.unicode.ttflib.DfontFile;
import com.kreative.unicode.ttflib.DfontResource;
import com.kreative.unicode.ttflib.DfontResourceType;

public class DebugSound {
	public static void main(String[] args) {
		for (String arg : args) {
			File file = new File(arg);
			try {
				DfontFile rsrc = new DfontFile(file);
				DfontResourceType rt = rsrc.getResourceType("snd ");
				if (rt != null) {
					for (DfontResource res : rt.getResources()) {
						String ns = file.getName() + " " + res.getId();
						if (res.getName() != null) ns += " " + res.getName();
						String fns = ns.replaceAll("[\\\\/:*?\"<>|]+", "_");
						try {
							SoundResource sr = new SoundResource(res.getData());
							SampledSound ss = sr.toSampledSound();
							if (ss == null) continue;
							
							System.out.print(ns + ":");
							System.out.print("\tsndFmt " + sr.format);
							System.out.print("\trate " + rateString(ss.std.sampleRate));
							System.out.print("\tenc " + ss.std.encode);
							if (ss.ext != null) {
								System.out.print("\tch " + ss.ext.numChannels);
								System.out.print("\tsampSize " + ss.ext.sampleSize);
							}
							if (ss.cmp != null) {
								System.out.print("\tch " + ss.cmp.numChannels);
								System.out.print("\tfmt " + formatString(ss.cmp.format));
								System.out.print("\tcompId " + ss.cmp.compressionId);
								System.out.print("\tpktSize " + ss.cmp.packetSize);
								System.out.print("\tsampSize " + ss.cmp.sampleSize);
							}
							System.out.println();
							
							byte[] wav = sr.toWav();
							if (wav != null) {
								FileOutputStream out = new FileOutputStream(new File(fns + ".wav"));
								out.write(wav);
								out.close();
							}
							byte[] aiff = sr.toAiff();
							if (aiff != null) {
								FileOutputStream out = new FileOutputStream(new File(fns + ".aiff"));
								out.write(aiff);
								out.close();
							}
						} catch (Exception e) {
							System.err.println("Skipping " + ns + ": " + e);
						}
					}
				}
			} catch (Exception e) {
				System.err.println("Skipping " + file.getName() + ": " + e);
			}
		}
	}
	
	public static String rateString(int rate) {
		return Double.toString((rate & 0xFFFFFFFFL) / 65536.0);
	}
	
	public static String formatString(int format) {
		if (format == 0) return "0";
		return DfontResourceType.toString(format);
	}
}
