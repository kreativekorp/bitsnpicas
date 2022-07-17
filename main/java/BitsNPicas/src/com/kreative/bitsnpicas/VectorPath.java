package com.kreative.bitsnpicas;

import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class VectorPath extends AbstractList<VectorInstruction> {
	private final List<VectorInstruction> ops = new ArrayList<VectorInstruction>();
	
	public void append(GeneralPath path) {
		append(path, false);
	}
	
	public void append(GeneralPath path, boolean connect) {
		double[] vals = new double[8];
		for (PathIterator pi = path.getPathIterator(null); !pi.isDone(); pi.next()) {
			switch (pi.currentSegment(vals)) {
				case PathIterator.SEG_MOVETO:
					ops.add(new VectorInstruction((connect ? 'L' : 'M'), vals[0], vals[1]));
					connect = false;
					break;
				case PathIterator.SEG_LINETO:
					ops.add(new VectorInstruction('L', vals[0], vals[1]));
					break;
				case PathIterator.SEG_QUADTO:
					ops.add(new VectorInstruction('Q', vals[0], vals[1], vals[2], vals[3]));
					break;
				case PathIterator.SEG_CUBICTO:
					ops.add(new VectorInstruction('C', vals[0], vals[1], vals[2], vals[3], vals[4], vals[5]));
					break;
				case PathIterator.SEG_CLOSE:
					ops.add(new VectorInstruction('Z'));
					break;
			}
		}
	}
	
	public GeneralPath toGeneralPath() {
		PathExtensions p = new PathExtensions();
		for (VectorInstruction op : ops) {
			p.execute(op.getOperation(), op.getOperands());
		}
		return p.getPath();
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		boolean first = true;
		for (VectorInstruction op : ops) {
			if (first) first = false;
			else sb.append(" ");
			sb.append(op.toString());
		}
		return sb.toString();
	}
	
	public boolean add(VectorInstruction e) { return ops.add(e); }
	public void add(int i, VectorInstruction e) { ops.add(i, e); }
	public boolean addAll(Collection<? extends VectorInstruction> c) { return ops.addAll(c); }
	public boolean addAll(int i, Collection<? extends VectorInstruction> c) { return ops.addAll(i, c); }
	public void clear() { ops.clear(); }
	public boolean contains(Object e) { return ops.contains(e); }
	public boolean containsAll(Collection<?> c) { return ops.containsAll(c); }
	public VectorInstruction get(int i) { return ops.get(i); }
	public int indexOf(Object e) { return ops.indexOf(e); }
	public boolean isEmpty() { return ops.isEmpty(); }
	public int lastIndexOf(Object e) { return ops.lastIndexOf(e); }
	public boolean remove(Object e) { return ops.remove(e); }
	public VectorInstruction remove(int i) { return ops.remove(i); }
	public boolean removeAll(Collection<?> c) { return ops.removeAll(c); }
	public boolean retainAll(Collection<?> c) { return ops.retainAll(c); }
	public VectorInstruction set(int i, VectorInstruction e) { return ops.set(i, e); }
	public int size() { return ops.size(); }
	public Object[] toArray() { return ops.toArray(); }
	public <T> T[] toArray(T[] a) { return ops.toArray(a); }
}
