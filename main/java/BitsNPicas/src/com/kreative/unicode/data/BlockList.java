package com.kreative.unicode.data;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;
import java.util.TreeSet;
import com.kreative.unicode.ttflib.PuaaEntry;
import com.kreative.unicode.ttflib.PuaaTable;

public class BlockList extends AbstractList<Block> {
	private static BlockList instance = null;
	
	public static BlockList instance() {
		if (instance == null) instance = new BlockList();
		return instance;
	}
	
	private final List<Block> blocks;
	
	private BlockList() {
		TreeSet<Block> blocks = new TreeSet<Block>();
		readTextStream(BlockList.class.getResourceAsStream("roadmap.txt"), blocks);
		readPuaaTable(PuaaCache.getPuaaTable("unidata.ucd"), blocks);
		
		String[] fontNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		for (String fontName : fontNames) {
			Font font = new Font(fontName, 0, 12);
			PuaaTable puaa = PuaaCache.getPuaaTable(font);
			if (puaa != null) readPuaaTable(puaa, blocks);
		}
		
		readDirectory(UnicodeUtils.getTableDirectory("Blocks"), blocks);
		this.blocks = Arrays.asList(blocks.toArray(new Block[blocks.size()]));
	}
	
	private static void readDirectory(File d, Collection<Block> blocks) {
		for (File f : d.listFiles()) {
			if (f.getName().startsWith(".") || f.getName().endsWith("\r")) {
				continue;
			} else if (f.isDirectory()) {
				readDirectory(f, blocks);
			} else {
				String n = f.getName().toLowerCase();
				if (n.endsWith(".ucd") || n.endsWith(".ttf") || n.endsWith(".otf")) {
					readPuaaTable(PuaaCache.getPuaaTable(f), blocks);
				} else if (n.endsWith(".txt")) {
					try { readTextStream(new FileInputStream(f), blocks); }
					catch (IOException e) { e.printStackTrace(); }
				}
			}
		}
	}
	
	private static void readTextStream(InputStream in, Collection<Block> blocks) {
		Scanner scan = new Scanner(in);
		while (scan.hasNextLine()) {
			String line = scan.nextLine().trim();
			if (line.length() > 0 && line.charAt(0) != '#') {
				String[] f1 = line.split(";");
				if (f1.length == 2) {
					String blockName = f1[1].trim();
					String[] f2 = f1[0].split("[.]+");
					if (f2.length == 2) {
						try {
							int first = Integer.parseInt(f2[0], 16);
							int last = Integer.parseInt(f2[1], 16);
							blocks.add(new Block(first, last, blockName));
						} catch (NumberFormatException e) {
							continue;
						}
					}
				}
			}
		}
		scan.close();
	}
	
	private static void readPuaaTable(PuaaTable puaaTable, Collection<Block> blocks) {
		if (puaaTable != null) {
			List<PuaaEntry> puaaEntries = puaaTable.getPropertyEntries("Block");
			if (puaaEntries != null && puaaEntries.size() > 0) {
				for (PuaaEntry puaaEntry : puaaEntries) {
					int fcp = puaaEntry.getFirstCodePoint();
					int lcp = puaaEntry.getLastCodePoint();
					String name = puaaEntry.getPropertyString(fcp);
					blocks.add(new Block(fcp, lcp, name));
				}
			}
		}
	}
	
	public boolean contains(Object o) {
		return blocks.contains(o);
	}
	
	public boolean containsAll(Collection<?> c) {
		return blocks.containsAll(c);
	}
	
	public Block get(int index) {
		return blocks.get(index);
	}
	
	public int indexOf(Object o) {
		return blocks.indexOf(o);
	}
	
	public boolean isEmpty() {
		return blocks.isEmpty();
	}
	
	public Iterator<Block> iterator() {
		return blocks.iterator();
	}
	
	public int lastIndexOf(Object o) {
		return blocks.lastIndexOf(o);
	}
	
	public ListIterator<Block> listIterator() {
		return blocks.listIterator();
	}
	
	public ListIterator<Block> listIterator(int index) {
		return blocks.listIterator(index);
	}
	
	public int size() {
		return blocks.size();
	}
	
	public List<Block> subList(int fromIndex, int toIndex) {
		return blocks.subList(fromIndex, toIndex);
	}
	
	public Object[] toArray() {
		return blocks.toArray();
	}
	
	public <T> T[] toArray(T[] a) {
		return blocks.toArray(a);
	}
}
