package com.kreative.bitsnpicas.geos;

import java.util.HashMap;
import java.util.Map;

public class GEOSFontPointSize {
	public int pointSize;
	public GEOSFontStrike strike;
	public GEOSFontStrike[] megaStrikes;
	public GEOSFontStrike megaStrikeIndex;
	public UTF8StrikeMap utf8Map;
	public Map<UTF8StrikeEntry,GEOSFontStrike> utf8Strikes;
	
	public GEOSFontPointSize(int pointSize) {
		this.pointSize = pointSize;
		this.strike = null;
		this.megaStrikes = null;
		this.megaStrikeIndex = null;
		this.utf8Map = null;
		this.utf8Strikes = null;
	}
	
	public boolean isMega() {
		return (megaStrikes != null && megaStrikeIndex != null);
	}
	
	public boolean isUTF8() {
		return (utf8Map != null && utf8Strikes != null);
	}
	
	public void remap(Map<UTF8StrikeEntry,UTF8StrikeEntry> remap) {
		if (utf8Map != null) {
			utf8Map.remap(remap);
		}
		if (utf8Strikes != null) {
			Map<UTF8StrikeEntry,GEOSFontStrike> newStrikes = new HashMap<UTF8StrikeEntry,GEOSFontStrike>();
			for (Map.Entry<UTF8StrikeEntry,GEOSFontStrike> e : utf8Strikes.entrySet()) {
				if (remap.containsKey(e.getKey())) {
					newStrikes.put(remap.get(e.getKey()), e.getValue());
				} else {
					newStrikes.put(e.getKey(), e.getValue());
				}
			}
			utf8Strikes = newStrikes;
		}
	}
}
