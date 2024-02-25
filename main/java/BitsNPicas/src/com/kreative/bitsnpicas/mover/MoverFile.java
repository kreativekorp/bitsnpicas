package com.kreative.bitsnpicas.mover;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.swing.ImageIcon;
import com.kreative.unicode.ttflib.DfontFile;
import com.kreative.unicode.ttflib.DfontResource;
import com.kreative.unicode.ttflib.DfontResourceType;

public class MoverFile {
	private static final Random random = new Random();
	
	private final DfontFile rsrc;
	private final List<ResourceBundle> items;
	
	public MoverFile(DfontFile rsrc) throws IOException {
		this.rsrc = rsrc;
		this.items = new ArrayList<ResourceBundle>();
		
		// Fonts
		
		DfontResourceType fonds = rsrc.getResourceType("FOND");
		if (fonds != null) for (DfontResource res : fonds.getResources()) {
			FONDResource fond = new FONDResource(res.getName(), res.getData());
			for (FONDEntry e : fond.entries) {
				ImageIcon icon;
				String moverType;
				DfontResource font;
				if (e.size == 0) {
					icon = MoverIcons.FILE_TRUETYPE_16;
					moverType = "tfil";
					font = rsrc.getResource("sfnt", e.id);
				} else {
					icon = MoverIcons.FILE_FONT_16;
					moverType = "ffil";
					font = rsrc.getResource("NFNT", e.id);
					if (font == null) font = rsrc.getResource("FONT", e.id);
				}
				if (font != null) {
					FONDResource fr = new FONDResource(fond, Arrays.asList(e));
					ResourceBundle rb = new ResourceBundle(icon, moverType, fr);
					rb.resources.add(font);
					items.add(rb);
				}
			}
		}
		if (items.isEmpty()) {
			DfontResourceType fonts = rsrc.getResourceType("FONT");
			if (fonts != null) for (DfontResource res : fonts.getResources()) {
				int fontSize = res.getId() & 0x7F;
				if (fontSize != 0) {
					int fontId = res.getId() >> 7;
					int fontNameId = res.getId() &~ 0x7F;
					DfontResource fontNameRes = fonts.getResource(fontNameId);
					String fontName = (fontNameRes != null) ? fontNameRes.getName() : null;
					FONDResource fr = new FONDResource(fontName, fontId);
					fr.entries.add(new FONDEntry(fontSize, 0, res.getId()));
					ResourceBundle rb = new ResourceBundle(MoverIcons.FILE_FONT_16, "ffil", fr);
					rb.resources.add(res);
					items.add(rb);
				}
			}
		}
		
		// Desk Accessories
		
		DfontResourceType drvrs = rsrc.getResourceType("DRVR");
		if (drvrs != null) for (DfontResource drvr : drvrs.getResources()) {
			String name = drvr.getName();
			if (name != null && name.length() > 0 && !name.startsWith(".")) {
				ResourceBundle rb = new ResourceBundle(
					MoverIcons.DA_16, "dfil", name.trim(), drvr.getId()
				);
				rb.resources.add(drvr);
				items.add(rb);
				for (DfontResourceType type : rsrc.getResourceTypes()) {
					for (DfontResource res : type.getResources()) {
						if (res.getOwnerType() == DfontResource.OWNER_TYPE_DRVR) {
							if (res.getOwnerId() == drvr.getId()) {
								rb.resources.add(res);
							}
						}
					}
				}
			}
		}
		
		// Script Systems
		
		DfontResourceType itlbs = rsrc.getResourceType("itlb");
		if (itlbs != null) for (DfontResource itlb : itlbs.getResources()) {
			ResourceBundle rb = new ResourceBundle(
				MoverIcons.FILE_SCRIPT_16, "ifil", itlb.getName(), itlb.getId()
			);
			rb.resources.add(itlb);
			items.add(rb);
			for (DfontResourceType type : rsrc.getResourceTypes()) {
				if (isScriptType(type.getType())) {
					for (DfontResource res : type.getResources()) {
						if (scriptCode(res.getId()) == itlb.getId()) {
							rb.resources.add(res);
						}
					}
				}
			}
		}
		
		// Keyboard Layouts
		
		for (int id : getIds("KCHR", "uchr")) {
			ResourceBundle rb = getAll(
				MoverIcons.FILE_KEYBOARD_16, "kfil", "" + id, id, "KCHR", "uchr", "itlk",
				"kcs#", "kcs4", "kcs8", "KCN#", "kcl4", "kcl8", "kscn", "ksc4", "ksc8", "kcns"
			);
			if (rb != null) items.add(rb);
		}
		
		// FKEYs
		
		for (int id : getIds("FKEY", "fkey")) {
			ResourceBundle rb = getAll(
				MoverIcons.FILE_FKEY_16, "fkey", "\u2318-Shift-" + id, id, "FKEY", "fkey"
			);
			if (rb != null) items.add(rb);
		}
		
		// Sounds
		
		DfontResourceType snds = rsrc.getResourceType("snd ");
		if (snds != null) for (DfontResource snd : snds.getResources()) {
			String name = snd.getName();
			if (name != null && name.length() > 0) {
				ResourceBundle rb = new ResourceBundle(
					MoverIcons.FILE_SOUND_16, "sfil", name, snd.getId()
				);
				rb.resources.add(snd);
				items.add(rb);
			}
		}
		
		Collections.sort(items);
	}
	
	private static final int itl0 = 0x69746C30;
	private static final int itl9 = 0x69746C39;
	private static final int trsl = 0x7472736C;
	private boolean isScriptType(int type) {
		return (type >= itl0 && type <= itl9) || type == trsl;
	}
	
	private int scriptCode(int id) {
		id &= 0xFFFF;
		if (id < 16384) return 0;
		return ((id - 16384) / 512) + 1;
	}
	
	private Set<Integer> getIds(String... types) {
		Set<Integer> ids = new HashSet<Integer>();
		for (String type : types) {
			DfontResourceType t = rsrc.getResourceType(type);
			if (t != null) ids.addAll(t.getResourceIds());
		}
		return ids;
	}
	
	private ResourceBundle getAll(ImageIcon icon, String moverType, String name, int id, String... types) {
		ResourceBundle rb = null;
		for (String type : types) {
			DfontResource res = rsrc.getResource(type, id);
			if (res != null) {
				if (rb == null) {
					String n = res.getName();
					if (n == null || n.length() == 0) n = name;
					rb = new ResourceBundle(icon, moverType, n, id);
				}
				rb.resources.add(res);
			}
		}
		return rb;
	}
	
	public void add(ResourceBundle e) {
		if (!items.contains(e)) {
			if (e.fond != null) {
				if ((e = addFont(e)) == null) return;
			} else if (e.moverType.equals("dfil")) {
				if ((e = addDeskAccessory(e)) == null) return;
			} else if (e.moverType.equals("kfil")) {
				if ((e = addKeyboardLayout(e)) == null) return;
			} else if (e.moverType.equals("sfil")) {
				if ((e = addSound(e)) == null) return;
			} else {
				if ((e = addGeneric(e)) == null) return;
			}
			items.add(e);
			Collections.sort(items);
		}
	}
	
	private ResourceBundle addFont(ResourceBundle e) {
		// Remove existing resource if it exists.
		for (ResourceBundle ee : items) {
			if (ee.moverType.equals(e.moverType) && ee.name.equals(e.name)) {
				remove(ee);
				break;
			}
		}
		// Build list of font resources to add, renumbering if necessary.
		Map<String,DfontResource> resmap = new HashMap<String,DfontResource>();
		for (DfontResource res : e.resources) {
			String type = res.getTypeString();
			if (type.equals("FONT")) type = "NFNT";
			String key = type + res.getId();
			int id = res.getId();
			while (rsrc.getResource(type, id) != null) {
				id = random.nextInt(32768 - 256) + 256;
			}
			resmap.put(key, new DfontResource(
				type, id, res.getAttributes(), res.getName(),
				res.getData(), 0, res.getData().length
			));
		}
		// Build list of FOND entries to add, renumbered if necessary.
		List<FONDEntry> entries = new ArrayList<FONDEntry>();
		for (FONDEntry entry : e.fond.entries) {
			String type = ((entry.size == 0) ? "sfnt" : "NFNT");
			String key = type + entry.id;
			int id = resmap.get(key).getId();
			entries.add(new FONDEntry(entry.size, entry.style, id));
		}
		// Update or create the FOND resource.
		FONDResource fond;
		DfontResource fondRes = rsrc.getResource("FOND", e.fond.name);
		if (fondRes == null) {
			fond = new FONDResource(e.fond, entries);
			while (rsrc.getResource("FOND", fond.id) != null) {
				fond.id = randomScriptId(fond.id);
			}
			try {
				byte[] data = fond.toByteArray();
				fondRes = new DfontResource(
					"FOND", fond.id, 0x60, fond.name,
					data, 0, data.length
				);
				if (!rsrc.addResource(fondRes)) return null;
				if (!addAllResources(resmap.values())) return null;
			} catch (IOException ioe) {
				ioe.printStackTrace();
				return null;
			}
		} else {
			try {
				fond = new FONDResource(fondRes.getName(), fondRes.getData());
				fond.entries.addAll(entries);
				byte[] data = fond.toByteArray();
				fond.entries.retainAll(entries);
				fondRes.setData(data, 0, data.length);
				if (!addAllResources(resmap.values())) return null;
			} catch (IOException ioe) {
				ioe.printStackTrace();
				return null;
			}
		}
		e = new ResourceBundle(e.icon, e.moverType, fond);
		e.resources.addAll(resmap.values());
		return e;
	}
	
	private ResourceBundle addDeskAccessory(ResourceBundle e) {
		// Remove existing resource if it exists.
		for (ResourceBundle ee : items) {
			if (ee.moverType.equals(e.moverType) && ee.name.equals(e.name)) {
				remove(ee);
				break;
			}
		}
		// Renumber if necessary.
		e = e.clone();
		while (containsAny(e.resources)) {
			int newOwnerId = random.nextInt(64);
			List<DfontResource> newRes = new ArrayList<DfontResource>();
			for (DfontResource res : e.resources) {
				int newId;
				if (res.getId() == e.id) {
					newId = newOwnerId;
				} else if (res.getOwnerType() == DfontResource.OWNER_TYPE_DRVR && res.getOwnerId() == e.id) {
					newId = DfontResource.ownedId(DfontResource.OWNER_TYPE_DRVR, newOwnerId, res.getSubId());
				} else {
					newId = res.getId();
				}
				newRes.add(new DfontResource(
					res.getType(), newId, res.getAttributes(),
					res.getName(), res.getData(), 0, res.getData().length
				));
			}
			e = new ResourceBundle(e.icon, e.moverType, e.name, newOwnerId);
			e.resources.addAll(newRes);
		}
		return addAllResources(e.resources) ? e : null;
	}
	
	private ResourceBundle addKeyboardLayout(ResourceBundle e) {
		// Remove existing resource if it exists.
		for (ResourceBundle ee : items) {
			if (ee.moverType.equals(e.moverType) && ee.name.equals(e.name)) {
				remove(ee);
				break;
			}
		}
		// Renumber if necessary.
		e = e.clone();
		while (containsAny(e.resources)) {
			int newId = randomScriptId(e.id);
			List<DfontResource> newRes = new ArrayList<DfontResource>();
			for (DfontResource res : e.resources) {
				newRes.add(new DfontResource(
					res.getType(), newId, res.getAttributes(),
					res.getName(), res.getData(), 0, res.getData().length
				));
			}
			e = new ResourceBundle(e.icon, e.moverType, e.name, newId);
			e.resources.addAll(newRes);
		}
		return addAllResources(e.resources) ? e : null;
	}
	
	private ResourceBundle addSound(ResourceBundle e) {
		// Remove existing resource if it exists.
		for (ResourceBundle ee : items) {
			if (ee.moverType.equals(e.moverType) && ee.name.equals(e.name)) {
				remove(ee);
				break;
			}
		}
		// Renumber if necessary.
		e = e.clone();
		while (containsAny(e.resources)) {
			int newId = random.nextInt(32768 - 256) + 256;
			List<DfontResource> newRes = new ArrayList<DfontResource>();
			for (DfontResource res : e.resources) {
				newRes.add(new DfontResource(
					res.getType(), newId, res.getAttributes(),
					res.getName(), res.getData(), 0, res.getData().length
				));
			}
			e = new ResourceBundle(e.icon, e.moverType, e.name, newId);
			e.resources.addAll(newRes);
		}
		return addAllResources(e.resources) ? e : null;
	}
	
	private ResourceBundle addGeneric(ResourceBundle e) {
		// Remove existing resource if it exists.
		for (ResourceBundle ee : items) {
			if (ee.moverType.equals(e.moverType) && ee.id == e.id) {
				remove(ee);
				break;
			}
		}
		// Don't renumber, just clobber anything with the same id.
		// (Should be done by the above but just in case.)
		e = e.clone();
		for (DfontResource res : e.resources) rsrc.removeResource(res.getType(), res.getId());
		return addAllResources(e.resources) ? e : null;
	}
	
	private int randomScriptId(int id) {
		if (id >= 0 && id < 16384) return random.nextInt(16383 - 256) + 256;
		return (id &~ 0x1FF) + random.nextInt(511 - 256) + 256;
	}
	
	private boolean containsAny(Iterable<DfontResource> resources) {
		for (DfontResource res : resources) {
			if (rsrc.getResource(res.getType(), res.getId()) != null) {
				return true;
			}
		}
		return false;
	}
	
	private boolean addAllResources(Iterable<DfontResource> resources) {
		for (DfontResource res : resources) {
			if (!rsrc.addResource(res)) {
				return false;
			}
		}
		return true;
	}
	
	public boolean contains(ResourceBundle e) {
		return items.contains(e);
	}
	
	public ResourceBundle get(int i) {
		return items.get(i);
	}
	
	public boolean isEmpty() {
		return items.isEmpty();
	}
	
	public void remove(ResourceBundle e) {
		if (items.remove(e)) {
			for (DfontResource res : e.resources) {
				rsrc.removeResource(res);
			}
			if (e.fond != null) {
				// Update or remove FOND resource.
				DfontResource res = rsrc.getResource("FOND", e.fond.id);
				if (res != null) {
					try {
						FONDResource fond = new FONDResource(res.getName(), res.getData());
						fond.entries.removeAll(e.fond.entries);
						if (fond.entries.isEmpty()) {
							rsrc.removeResource(res);
						} else {
							byte[] data = fond.toByteArray();
							res.setData(data, 0, data.length);
						}
					} catch (IOException ioe) {
						ioe.printStackTrace();
					}
				}
				// Remove base FONT resource if no other FONT resources are left.
				int base = e.fond.id << 7;
				res = rsrc.getResource("FONT", base);
				if (res != null) {
					for (int i = 1; i < 128; i++) {
						if (rsrc.getResource("FONT", base + i) != null) {
							return;
						}
					}
					rsrc.removeResource(res);
				}
			}
		}
	}
	
	public int size() {
		return items.size();
	}
}
