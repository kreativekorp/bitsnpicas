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
import com.kreative.ksfl.KSFLConstants;
import com.kreative.rsrc.MacResource;
import com.kreative.rsrc.MacResourceAlreadyExistsException;
import com.kreative.rsrc.MacResourceProvider;

public class MoverFile {
	private static final Random random = new Random();
	
	private final MacResourceProvider rp;
	private final List<ResourceBundle> items;
	
	public MoverFile(MacResourceProvider rp) throws IOException {
		this.rp = rp;
		this.items = new ArrayList<ResourceBundle>();
		
		// Fonts
		
		for (short id : rp.getIDs(KSFLConstants.FOND)) {
			MacResource res = rp.get(KSFLConstants.FOND, id);
			FONDResource fond = new FONDResource(res.name, res.data);
			for (FONDEntry e : fond.entries) {
				ImageIcon icon;
				String moverType;
				MacResource font;
				if (e.size == 0) {
					icon = MoverIcons.FILE_TRUETYPE_16;
					moverType = "tfil";
					font = rp.get(KSFLConstants.sfnt, (short)e.id);
				} else {
					icon = MoverIcons.FILE_FONT_16;
					moverType = "ffil";
					font = rp.get(KSFLConstants.NFNT, (short)e.id);
					if (font == null) font = rp.get(KSFLConstants.FONT, (short)e.id);
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
			for (short id : rp.getIDs(KSFLConstants.FONT)) {
				int fontSize = id & 0x7F;
				if (fontSize != 0) {
					int fontId = (id & 0xFFFF) >> 7;
					short fontNameId = (short)(id &~ 0x7F);
					String fontName = rp.getNameFromID(KSFLConstants.FONT, fontNameId);
					FONDResource fr = new FONDResource(fontName, fontId);
					fr.entries.add(new FONDEntry(fontSize, 0, id));
					ResourceBundle rb = new ResourceBundle(MoverIcons.FILE_FONT_16, "ffil", fr);
					rb.resources.add(rp.get(KSFLConstants.FONT, id));
					items.add(rb);
				}
			}
		}
		
		// Desk Accessories
		
		for (short drvrId : rp.getIDs(KSFLConstants.DRVR)) {
			MacResource drvr = rp.get(KSFLConstants.DRVR, drvrId);
			if (drvr.name.length() > 0 && !drvr.name.startsWith(".")) {
				ResourceBundle rb = new ResourceBundle(
					MoverIcons.DA_16, "dfil", drvr.name.trim(), drvr.id
				);
				rb.resources.add(drvr);
				items.add(rb);
				for (int type : rp.getTypes()) {
					for (short id : rp.getIDs(type)) {
						if (((id >> 11) & 0x1F) == MacResource.OWNER_TYPE_DRVR) {
							if (((id >> 5) & 0x3F) == drvr.id) {
								rb.resources.add(rp.get(type, id));
							}
						}
					}
				}
			}
		}
		
		// Script Systems
		
		for (short scriptId : rp.getIDs(KSFLConstants.itlb)) {
			MacResource itlb = rp.get(KSFLConstants.itlb, scriptId);
			ResourceBundle rb = new ResourceBundle(
				MoverIcons.FILE_SCRIPT_16, "ifil", itlb.name, itlb.id
			);
			rb.resources.add(itlb);
			items.add(rb);
			for (int type : rp.getTypes()) {
				if (
					(  type >= 0x69746C30 /*'itl0'*/
					&& type <= 0x69746C39 /*'itl9'*/ )
					|| type == 0x7472736C /*'trsl'*/
				) {
					for (short id : rp.getIDs(type)) {
						if (scriptCode(id) == itlb.id) {
							rb.resources.add(rp.get(type, id));
						}
					}
				}
			}
		}
		
		// Keyboard Layouts
		
		for (short id : getIDs(KSFLConstants.KCHR, KSFLConstants.uchr)) {
			ResourceBundle rb = getAll(
				MoverIcons.FILE_KEYBOARD_16, "kfil", "", id,
				KSFLConstants.KCHR, KSFLConstants.uchr, KSFLConstants.itlk,
				KSFLConstants.kcs$, KSFLConstants.kcs4, KSFLConstants.kcs8,
				KSFLConstants.KCN$, KSFLConstants.kcl4, KSFLConstants.kcl8,
				KSFLConstants.kscn, KSFLConstants.ksc4, KSFLConstants.ksc8,
				KSFLConstants.kcns
			);
			if (rb != null) items.add(rb);
		}
		
		// FKEYs
		
		for (short id : getIDs(KSFLConstants.FKEY, KSFLConstants.fkey)) {
			ResourceBundle rb = getAll(
				MoverIcons.FILE_FKEY_16, "fkey", "\u2318-Shift-" + id, id,
				KSFLConstants.FKEY, KSFLConstants.fkey
			);
			if (rb != null) items.add(rb);
		}
		
		// Sounds
		
		for (short id : rp.getIDs(KSFLConstants.snd)) {
			MacResource snd = rp.get(KSFLConstants.snd, id);
			if (snd.name.length() > 0) {
				ResourceBundle rb = new ResourceBundle(
					MoverIcons.FILE_SOUND_16, "sfil", snd.name, snd.id
				);
				rb.resources.add(snd);
				items.add(rb);
			}
		}
		
		Collections.sort(items);
	}
	
	private int scriptCode(int id) {
		id &= 0xFFFF;
		if (id < 16384) return 0;
		return ((id - 16384) / 512) + 1;
	}
	
	private Set<Short> getIDs(int... types) {
		Set<Short> ids = new HashSet<Short>();
		for (int type : types) {
			for (short id : rp.getIDs(type)) {
				ids.add(id);
			}
		}
		return ids;
	}
	
	private ResourceBundle getAll(ImageIcon icon, String moverType, String name, short id, int... types) {
		ResourceBundle rb = null;
		for (int type : types) {
			MacResource res = rp.get(type, id);
			if (res != null) {
				if (rb == null) {
					String n = ((res.name.length() > 0) ? res.name : name);
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
		Map<Long,MacResource> resmap = new HashMap<Long,MacResource>();
		for (MacResource res : e.resources) {
			int type = ((res.type == KSFLConstants.FONT) ? KSFLConstants.NFNT : res.type);
			long key = ((long)type << 32L) ^ (long)res.id;
			short id = res.id;
			while (rp.contains(type, id)) {
				id = (short)(random.nextInt(32768 - 256) + 256);
			}
			resmap.put(key, new MacResource(
				type, id, res.getAttributes(), res.name, res.data
			));
		}
		// Build list of FOND entries to add, renumbered if necessary.
		List<FONDEntry> entries = new ArrayList<FONDEntry>();
		for (FONDEntry entry : e.fond.entries) {
			int type = ((entry.size == 0) ? KSFLConstants.sfnt : KSFLConstants.NFNT);
			long key = ((long)type << 32L) ^ (long)entry.id;
			short id = resmap.get(key).id;
			entries.add(new FONDEntry(entry.size, entry.style, id));
		}
		// Update or create the FOND resource.
		FONDResource fond;
		MacResource fondRes = rp.get(KSFLConstants.FOND, e.fond.name);
		if (fondRes == null) {
			fond = new FONDResource(e.fond, entries);
			while (rp.contains(KSFLConstants.FOND, (short)fond.id)) {
				fond.id = randomScId(fond.id);
			}
			try {
				fondRes = new MacResource(
					KSFLConstants.FOND, (short)fond.id, (byte)0x60,
					fond.name, fond.toByteArray()
				);
			} catch (IOException ioe) {
				ioe.printStackTrace();
				return null;
			}
			try {
				rp.add(fondRes);
				for (MacResource res : resmap.values()) rp.add(res);
			} catch (MacResourceAlreadyExistsException aee) {
				aee.printStackTrace();
				return null;
			}
		} else {
			try {
				fond = new FONDResource(fondRes.name, fondRes.data);
				fond.entries.addAll(entries);
				fondRes.data = fond.toByteArray();
				fond.entries.retainAll(entries);
			} catch (IOException ioe) {
				ioe.printStackTrace();
				return null;
			}
			try {
				rp.setData(fondRes.type, fondRes.id, fondRes.data);
				for (MacResource res : resmap.values()) rp.add(res);
			} catch (MacResourceAlreadyExistsException aee) {
				aee.printStackTrace();
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
			int newID = random.nextInt(64);
			List<MacResource> newRes = new ArrayList<MacResource>();
			for (MacResource res : e.resources) {
				if (res.id == e.id) {
					res.id = (short)newID;
				} else {
					if (res.getOwnerType() == MacResource.OWNER_TYPE_DRVR) {
						if (res.getOwnerID() == e.id) {
							res.setOwnerID(newID);
						}
					}
				}
				newRes.add(res);
			}
			e = new ResourceBundle(e.icon, e.moverType, e.name, newID);
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
			int newID = randomScId(e.id);
			List<MacResource> newRes = new ArrayList<MacResource>();
			for (MacResource res : e.resources) {
				res.id = (short)newID;
				newRes.add(res);
			}
			e = new ResourceBundle(e.icon, e.moverType, e.name, newID);
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
			int newID = random.nextInt(32768 - 256) + 256;
			List<MacResource> newRes = new ArrayList<MacResource>();
			for (MacResource res : e.resources) {
				res.id = (short)newID;
				newRes.add(res);
			}
			e = new ResourceBundle(e.icon, e.moverType, e.name, newID);
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
		for (MacResource res : e.resources) rp.remove(res.type, res.id);
		return addAllResources(e.resources) ? e : null;
	}
	
	private short randomScId(int id) {
		id &= 0xFFFF;
		if (id < 16384) return (short)(random.nextInt(16383 - 256) + 256);
		return (short)((id & 0xFE00) + random.nextInt(511 - 256) + 256);
	}
	
	private boolean containsAny(Iterable<MacResource> resources) {
		for (MacResource res : resources) {
			if (rp.contains(res.type, res.id)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean addAllResources(Iterable<MacResource> resources) {
		for (MacResource res : resources) {
			try {
				rp.add(res);
			} catch (MacResourceAlreadyExistsException aee) {
				aee.printStackTrace();
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
			for (MacResource res : e.resources) {
				rp.remove(res.type, res.id);
			}
			if (e.fond != null) {
				// Update or remove FOND resource.
				MacResource res = rp.get(KSFLConstants.FOND, (short)e.fond.id);
				if (res != null) {
					try {
						FONDResource fond = new FONDResource(res.name, res.data);
						fond.entries.removeAll(e.fond.entries);
						if (fond.entries.isEmpty()) rp.remove(res.type, res.id);
						else rp.setData(res.type, res.id, fond.toByteArray());
					} catch (IOException ioe) {
						ioe.printStackTrace();
					}
				}
				// Remove base FONT resource if no other FONT resources are left.
				int base = e.fond.id << 7;
				short sbase = (short)base;
				if (base == sbase && rp.contains(KSFLConstants.FONT, sbase)) {
					boolean found = false;
					for (int i = 1; i < 128; i++) {
						if (rp.contains(KSFLConstants.FONT, (short)(base + i))) {
							found = true;
							break;
						}
					}
					if (!found) {
						rp.remove(KSFLConstants.FONT, sbase);
					}
				}
			}
		}
	}
	
	public int size() {
		return items.size();
	}
}
