package com.kreative.bitsnpicas.truetype;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class GsubTable extends TrueTypeTable {
	
	public Map<Object, Integer> glyphsToId = null;
	
	private Map<String, Map<Object, String>> singlesubs;
	
	public GsubTable(Map<Object, Integer> gToId) {
		glyphsToId = gToId;
		singlesubs = new HashMap<String, Map<Object, String>>();
	}
	
	
	public void addSingleSub(String name, Map<Object, String> map) {
		singlesubs.put(name, map);
	}
	
	@Override
	protected void compile(DataOutputStream out, TrueTypeTable[] dependencies) throws IOException {

	    
		List<ByteArrayOutputStream> features = new ArrayList<>();
		List<String> featureTags = new ArrayList<>();
		for (Map.Entry<String, Map<Object, String>> singlesub : singlesubs.entrySet()) {
		    ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    DataOutputStream d = new DataOutputStream(baos);
		    makeSingleSubTable(d, singlesub.getKey(), singlesub.getValue());
		    d.flush();
		    if (baos.size() > 0) {
		        features.add(baos);
		        featureTags.add(singlesub.getKey());
		    }
		}
		if (features.isEmpty()) return;
	    

		List<String> scripts = new ArrayList<String>(){{
			add("DFLT");
			add("latn");
			add("arab");
		}};
		

	    ByteArrayOutputStream scriptBaos = new ByteArrayOutputStream();
	    DataOutputStream script = new DataOutputStream(scriptBaos);
	    //script list
	    script.writeShort(scripts.size());
	    int scriptTableSize = 10 + 2 * features.size();
	    int scriptBase = 2 + scripts.size() * 6;
	    for (String s : scripts) {
	        for (int i = 0; i < 4; i++) {
	        	script.writeByte(s.codePointAt(i));
	        }
	        script.writeShort(scriptBase);
	        scriptBase += scriptTableSize;
	    }
	    
	    
	    for (int i = 0; i < scripts.size(); i++) {
	    	script.writeShort(4); //default lang sys offset
	    	script.writeShort(0); //lang sys
	        // LangSys
	    	script.writeShort(0); //lookup order offset
	    	script.writeShort(0xffff); //required feature index
	    	script.writeShort(features.size()); //feature index count
	        for (int j = 0; j < features.size(); j++) {
	        	script.writeShort(j);
	        }
	    }

	    ByteArrayOutputStream featureBaos = new ByteArrayOutputStream();
	    DataOutputStream feature = new DataOutputStream(featureBaos);
	    //feature list
	    feature.writeShort(features.size()); //feature count
	    int featureBase = 2 + features.size() * 6; //feature records
	    for (String tag : featureTags) {
	        for (int i = 0; i < 4; i++) {
	        	feature.writeByte(tag.charAt(i));
	        }
	        feature.writeShort(featureBase);
	        featureBase += 6;
	    }
	    for (int i = 0; i < features.size(); i++) {
	    	feature.writeShort(0); //param offset
	    	feature.writeShort(1); //lookup count
	    	feature.writeShort(i); //lookup index
	    }
	    

	    out.writeShort(1); //major version
	    out.writeShort(0); //minor version
	    out.writeShort(12); //script list offset
	    out.writeShort(12 + scriptBaos.size()); //feature list offset
	    out.writeShort(12 + scriptBaos.size() + featureBaos.size()); //lookup list offset
	    out.writeShort(0); //feature variations offset

	    out.write(scriptBaos.toByteArray());
	    out.write(featureBaos.toByteArray());
	    
	    //lookup list
	    out.writeShort(features.size());
	    int lookupBase = 2 + 2 * features.size();
	    for (ByteArrayOutputStream f : features) {
	        out.writeShort(lookupBase);
	        lookupBase += f.size();
	    }
	    for (ByteArrayOutputStream f : features) {
	        out.write(f.toByteArray());
	    }
	}
	
	protected void makeSingleSubTable(DataOutputStream out, String name, Map<Object, String> map) throws IOException {
		List<Integer> orig = new ArrayList<>();
	    List<Integer> subs = new ArrayList<>();
	    for (Object key : new TreeSet<>(map.keySet())) {
	        Integer id = glyphsToId.get(key);
	        if (id != null) {
	            orig.add(id);
	            subs.add(glyphsToId.get(map.get(key)));
	        }
	    }
	    if (orig.isEmpty()) return;
	    int size = orig.size();
	    
	    out.writeShort(1); //lookup type (Single Substitution)
	    out.writeShort(0); //flags
	    out.writeShort(1); //subtable count
	    out.writeShort(8);
	    
	    out.writeShort(2); //format
	    out.writeShort(6 + 2 * size);
	    out.writeShort(size); //glyph count
	    for (Integer v : subs) {
	    	out.writeShort(v); //substitute glyph IDs
	    }
	    out.writeShort(1); //coverage format
	    out.writeShort(size); //coverage glyph count
	    for (Integer v : orig) {
	    	out.writeShort(v); //original glyph IDs
	    }
	}
	
	
	
	@Override
	protected void decompile(DataInputStream in, int length, TrueTypeTable[] dependencies) throws IOException {
	}

	@Override
	public String tableName() {
		return "GSUB";
	}

	@Override
	public String[] dependencyNames() {
		return new String[0];
	}
}