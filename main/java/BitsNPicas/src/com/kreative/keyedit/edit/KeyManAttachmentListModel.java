package com.kreative.keyedit.edit;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractListModel;

public class KeyManAttachmentListModel extends AbstractListModel {
	private static final long serialVersionUID = 1L;
	private final List<Entry> entries;
	
	public KeyManAttachmentListModel(Map<String,byte[]> map) {
		this.entries = new ArrayList<Entry>();
		for (Map.Entry<String,byte[]> e : map.entrySet()) {
			Entry entry = new Entry();
			entry.name = e.getKey();
			entry.data = e.getValue();
			this.entries.add(entry);
		}
	}
	
	public void addEntry(File file) throws IOException {
		ByteArrayOutputStream ba = new ByteArrayOutputStream();
		FileInputStream in = new FileInputStream(file);
		byte[] buf = new byte[65536]; int n;
		while ((n = in.read(buf)) > 0) ba.write(buf, 0, n);
		in.close();
		ba.close();
		addEntry(file.getName(), ba.toByteArray());
	}
	
	public void addEntry(String name, byte[] data) {
		int i = entries.size();
		Entry entry = new Entry();
		entry.name = name;
		entry.data = data;
		entries.add(entry);
		fireIntervalAdded(this, i, i);
	}
	
	public void deleteEntry(int i) {
		entries.remove(i);
		fireIntervalRemoved(this, i, i);
	}
	
	public void moveEntry(int i, int dir) {
		if (dir < 0 && i > 0) {
			Entry e = entries.remove(i);
			i -= 1;
			entries.add(i, e);
			fireContentsChanged(this, i, i + 1);
		}
		if (dir > 0 && i < (entries.size() - 1)) {
			Entry e = entries.remove(i);
			i += 1;
			entries.add(i, e);
			fireContentsChanged(this, i - 1, i);
		}
	}
	
	public void toMap(Map<String,byte[]> map) {
		map.clear();
		for (Entry e : entries) {
			map.put(e.name, e.data);
		}
	}
	
	@Override
	public Object getElementAt(int row) {
		return entries.get(row).name;
	}

	@Override
	public int getSize() {
		return entries.size();
	}
	
	private static class Entry {
		private String name;
		private byte[] data;
	}
}
