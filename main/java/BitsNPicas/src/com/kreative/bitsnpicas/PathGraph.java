package com.kreative.bitsnpicas;

import java.awt.*;
import java.io.*;
import java.util.*;

public class PathGraph implements Cloneable, Serializable {
	private static final long serialVersionUID = -9041484459040713072L;
	
	private HashMap<ImmutablePoint, HashSet<ImmutablePoint>> graph;
	
	public PathGraph() {
		graph = new HashMap<ImmutablePoint, HashSet<ImmutablePoint>>();
	}

	public PathGraph(PathGraph pg) {
		graph = new HashMap<ImmutablePoint, HashSet<ImmutablePoint>>();
		for (Map.Entry<ImmutablePoint, HashSet<ImmutablePoint>> e : pg.graph.entrySet()) {
			HashSet<ImmutablePoint> s = new HashSet<ImmutablePoint>();
			s.addAll(e.getValue());
			graph.put(e.getKey(), s);
		}
	}
	
	public void plot(int size, int x, int y) {
		ImmutablePoint bl = new ImmutablePoint(x*size, y*size);
		ImmutablePoint tl = new ImmutablePoint(x*size, y*size+size);
		ImmutablePoint tr = new ImmutablePoint(x*size+size, y*size+size);
		ImmutablePoint br = new ImmutablePoint(x*size+size, y*size);
		add(bl, tl); add(tl, tr); add(tr, br); add(br, bl);
	}
	
	public void plot(int size, ImmutablePoint p) {
		plot(size, p.x, p.y);
	}
	
	public void plot(int size, Point p) {
		plot(size, p.x, p.y);
	}
	
	public void plot(int xsize, int ysize, int x, int y) {
		ImmutablePoint bl = new ImmutablePoint(x*xsize, y*ysize);
		ImmutablePoint tl = new ImmutablePoint(x*xsize, y*ysize+ysize);
		ImmutablePoint tr = new ImmutablePoint(x*xsize+xsize, y*ysize+ysize);
		ImmutablePoint br = new ImmutablePoint(x*xsize+xsize, y*ysize);
		add(bl, tl); add(tl, tr); add(tr, br); add(br, bl);
	}
	
	public void plot(int xsize, int ysize, ImmutablePoint p) {
		plot(xsize, ysize, p.x, p.y);
	}
	
	public void plot(int xsize, int ysize, Point p) {
		plot(xsize, ysize, p.x, p.y);
	}
	
	public void removeOverlap() {
		Iterator<ImmutablePoint> i = graph.keySet().iterator();
		while (i.hasNext()) {
			ImmutablePoint src = i.next();
			Iterator<ImmutablePoint> j = graph.get(src).iterator();
			while (j.hasNext()) {
				ImmutablePoint dst = j.next();
				if (contains(dst, src)) {
					remove(src, dst);
					remove(dst, src);
					i = graph.keySet().iterator();
					break;
				}
			}
		}
	}
	
	public void simplifyPaths() {
		Iterator<ImmutablePoint> i = graph.keySet().iterator();
		while (i.hasNext()) {
			ImmutablePoint src = i.next();
			Iterator<ImmutablePoint> j = graph.get(src).iterator();
			while (j.hasNext()) {
				ImmutablePoint dst = j.next();
				int yd = signum(dst.y - src.y);
				int xd = signum(dst.x - src.x);
				ImmutablePoint newdst = dst;
				boolean foundEnd = false;
				while (!foundEnd) {
					foundEnd = true;
					ImmutablePoint[] candidates = getAdjPoints(newdst);
					for (ImmutablePoint candidate : candidates) {
						int cyd = signum(candidate.y - newdst.y);
						int cxd = signum(candidate.x - newdst.x);
						if (cyd == yd && cxd == xd) {
							remove(newdst, candidate);
							newdst = candidate;
							foundEnd = false;
						}
					}
				}
				if (newdst != dst) {
					remove(src, dst);
					add(src, newdst);
					i = graph.keySet().iterator();
					break;
				}
			}
		}
	}
	
	public Rectangle getBoundingRect() {
		ImmutablePoint[] pts = getAllPoints();
		if (pts.length == 0) return new Rectangle(0,0,0,0);
		int minx = pts[0].x, miny = pts[0].y, maxx = pts[0].x, maxy = pts[0].y;
		for (ImmutablePoint p : pts) {
			if (p.x < minx) minx = p.x;
			if (p.y < miny) miny = p.y;
			if (p.x > maxx) maxx = p.x;
			if (p.y > maxy) maxy = p.y;
		}
		return new Rectangle(minx, miny, maxx-minx, maxy-miny);
	}
	
	public ImmutablePoint[][] getContours() {
		Vector<Vector<ImmutablePoint>> v = new Vector<Vector<ImmutablePoint>>();
		PathGraph tmp = new PathGraph(this);
		while (!tmp.isEmpty()) {
			ImmutablePoint[] firsts = tmp.getAllPoints();
			for (ImmutablePoint first : firsts) {
				if (tmp.graph.containsKey(first) && !tmp.graph.get(first).isEmpty()) {
					Vector<ImmutablePoint> w = new Vector<ImmutablePoint>();
					int lastdx = 0, lastdy = 0;
					ImmutablePoint p = first;
					w.add(p);
					while (true) {
						ImmutablePoint[] candidates = tmp.getAdjPoints(p);
						if (candidates == null || candidates.length < 1) break;
						ImmutablePoint q = null;
						if (lastdx == 0 && lastdy == 0) {
							q = candidates[0];
						} else {
							// looking for point going counterclockwise
							for (ImmutablePoint candidate : candidates) {
								int dy = signum(candidate.y - p.y);
								int dx = signum(candidate.x - p.x);
								if (dy == -lastdx && dx == lastdy && !candidate.equals(p)) {
									q = candidate; break;
								}
							}
							// looking for point going straight
							if (q == null) {
								for (ImmutablePoint candidate : candidates) {
									int dy = signum(candidate.y - p.y);
									int dx = signum(candidate.x - p.x);
									if (dy == lastdy && dx == lastdx && !candidate.equals(p)) {
										q = candidate; break;
									}
								}
							}
							// looking for point going clockwise
							if (q == null) {
								for (ImmutablePoint candidate : candidates) {
									int dy = signum(candidate.y - p.y);
									int dx = signum(candidate.x - p.x);
									if (dy == lastdx && dx == -lastdy && !candidate.equals(p)) {
										q = candidate; break;
									}
								}
							}
							if (q == null) {
								for (ImmutablePoint candidate : candidates) {
									if (!candidate.equals(p)) {
										q = candidate; break;
									}
								}
							}
							if (q == null) break;
						}
						w.add(q);
						tmp.remove(p, q);
						if (first.equals(q)) break;
						else {
							lastdy = signum(q.y - p.y);
							lastdx = signum(q.x - p.x);
							p = q;
						}
					}
					if (!w.isEmpty()) v.add(w);
				}
			}
		}
		ImmutablePoint[][] a = new ImmutablePoint[v.size()][];
		for (int i = 0; i < v.size(); i++) {
			a[i] = v.get(i).toArray(new ImmutablePoint[0]);
		}
		return a;
	}
	
	public byte[] getGlyfData() {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		
		Rectangle rect = getBoundingRect();
		ImmutablePoint[][] contours = getContours();
		int[] endpoints = new int[contours.length];
		Vector<ImmutablePoint> coords = new Vector<ImmutablePoint>();
		for (int i = 0; i < contours.length; i++) {
			ImmutablePoint first = contours[i][0];
			coords.add(first);
			for (int j = 1; j < contours[i].length; j++) {
				ImmutablePoint next = contours[i][j];
				if (next.equals(first)) break;
				else coords.add(next);
			}
			endpoints[i] = coords.size()-1;
		}
		
		try {
			dos.writeShort(contours.length); // number of contours
			dos.writeShort(rect.x); // xMin
			dos.writeShort(rect.y); // yMin
			dos.writeShort(rect.x+rect.width); // xMax
			dos.writeShort(rect.y+rect.height); // yMax
			for (int ep : endpoints) dos.writeShort(ep); // endpoints
			dos.writeShort(0); // instruction length
			// instructions (there are none)
			
			// flags:
			int run = coords.size();
			while (run >= 256) {
				dos.writeByte(9);
				dos.writeByte(255);
				run -= 256;
			}
			if (run > 1) {
				dos.writeByte(9);
				dos.writeByte(run-1);
			} else if (run == 1) {
				dos.writeByte(1);
			}
			
			int last;
			last = 0; for (ImmutablePoint p : coords) { dos.writeShort(p.x-last); last = p.x; } // x coords
			last = 0; for (ImmutablePoint p : coords) { dos.writeShort(p.y-last); last = p.y; } // y coords
			while ((bos.size() & 0x3) != 0) dos.writeByte(0);
		} catch (IOException ioe) {}
		
		return bos.toByteArray();
	}
	
	public void add(int sx, int sy, int dx, int dy) {
		this.add(new ImmutablePoint(sx, sy), new ImmutablePoint(dx, dy));
	}
	
	public void add(ImmutablePoint src, ImmutablePoint dst) {
		if (graph.containsKey(src)) {
			graph.get(src).add(dst);
		} else {
			HashSet<ImmutablePoint> s = new HashSet<ImmutablePoint>();
			s.add(dst);
			graph.put(src, s);
		}
	}
	
	public void add(PathEdge e) {
		this.add(e.src, e.dst);
	}
	
	public void clear() {
		graph.clear();
	}
	
	public boolean contains(int sx, int sy, int dx, int dy) {
		return this.contains(new ImmutablePoint(sx, sy), new ImmutablePoint(dx, dy));
	}
	
	public boolean contains(ImmutablePoint src, ImmutablePoint dst) {
		return (graph.containsKey(src) && graph.get(src).contains(dst));
	}
	
	public boolean contains(PathEdge e) {
		return this.contains(e.src, e.dst);
	}
	
	public ImmutablePoint[] getSrcPoints() {
		return graph.keySet().toArray(new ImmutablePoint[0]);
	}
	
	public ImmutablePoint[] getAllPoints() {
		HashSet<ImmutablePoint> s = new HashSet<ImmutablePoint>();
		s.addAll(graph.keySet());
		for (HashSet<ImmutablePoint> t : graph.values()) {
			s.addAll(t);
		}
		return s.toArray(new ImmutablePoint[0]);
	}
	
	public ImmutablePoint[] getAdjPoints(ImmutablePoint src) {
		if (graph.containsKey(src)) {
			return graph.get(src).toArray(new ImmutablePoint[0]);
		} else {
			return new ImmutablePoint[0];
		}
	}
	
	public PathEdge[] getAllEdges() {
		HashSet<PathEdge> s = new HashSet<PathEdge>();
		for (Map.Entry<ImmutablePoint, HashSet<ImmutablePoint>> e : graph.entrySet()) {
			ImmutablePoint src = e.getKey();
			for (ImmutablePoint dst : e.getValue()) {
				s.add(new PathEdge(src, dst));
			}
		}
		return s.toArray(new PathEdge[0]);
	}
	
	public PathEdge[] getAdjEdges(ImmutablePoint src) {
		HashSet<PathEdge> s = new HashSet<PathEdge>();
		if (graph.containsKey(src)) {
			for (ImmutablePoint dst : graph.get(src)) {
				s.add(new PathEdge(src, dst));
			}
		}
		return s.toArray(new PathEdge[0]);
	}
	
	public boolean isEmpty() {
		if (!graph.isEmpty()) {
			for (HashSet<ImmutablePoint> s : graph.values()) {
				if (!s.isEmpty()) return false;
			}
		}
		return true;
	}
	
	public void remove(int sx, int sy, int dx, int dy) {
		this.remove(new ImmutablePoint(sx, sy), new ImmutablePoint(dx, dy));
	}
	
	public void remove(ImmutablePoint src, ImmutablePoint dst) {
		if (graph.containsKey(src)) {
			graph.get(src).remove(dst);
			if (graph.get(src).size() < 1) {
				graph.remove(src);
			}
		}
	}
	
	public void remove(PathEdge e) {
		this.remove(e.src, e.dst);
	}
	
	public boolean equals(Object o) {
		if (o instanceof PathGraph) {
			return this.graph.equals(((PathGraph)o).graph);
		}
		else {
			return this.graph.equals(o);
		}
	}
	
	public int hashCode() {
		return graph.hashCode();
	}
	
	public String toString() {
		return graph.toString();
	}
	
	private static int signum(int i) {
		return (i < 0) ? -1 : (i > 0) ? 1 : 0;
	}
	
	public static class ImmutablePoint implements Cloneable, Serializable {
		private static final long serialVersionUID = -274454806715511788L;
		
		private int x;
		private int y;
		
		public ImmutablePoint(int x, int y) {
			this.x = x; this.y = y;
		}
		
		public ImmutablePoint(ImmutablePoint p) {
			this.x = p.x; this.y = p.y;
		}
		
		public ImmutablePoint(Point p) {
			this.x = p.x; this.y = p.y;
		}
		
		public int getX() {
			return x;
		}
		
		public int getY() {
			return y;
		}
		
		public Point getPoint() {
			return new Point(x, y);
		}
		
		public boolean equals(Object o) {
			if (o instanceof ImmutablePoint) {
				ImmutablePoint p = (ImmutablePoint)o;
				return this.x == p.x && this.y == p.y;
			}
			else if (o instanceof Point) {
				Point p = (Point)o;
				return this.x == p.x && this.y == p.y;
			}
			else {
				return false;
			}
		}
		
		public int hashCode() {
			return x ^ Integer.reverseBytes(y);
		}
		
		public String toString() {
			return x+","+y;
		}
	}
	
	public static class PathEdge implements Cloneable, Serializable {
		private static final long serialVersionUID = -7710603668134471572L;
		
		private ImmutablePoint src;
		private ImmutablePoint dst;
		
		public PathEdge(ImmutablePoint src, ImmutablePoint dst) {
			this.src = src; this.dst = dst;
		}
		
		public ImmutablePoint getSrc() {
			return src;
		}
		
		public ImmutablePoint getDst() {
			return dst;
		}
		
		public PathEdge getReflection() {
			return new PathEdge(dst, src);
		}
		
		public boolean equals(Object o) {
			if (o instanceof PathEdge) {
				PathEdge e = (PathEdge)o;
				return this.src.equals(e.src) && this.dst.equals(e.dst);
			}
			else {
				return false;
			}
		}
		
		public int hashCode() {
			return src.hashCode() ^ Integer.reverse(dst.hashCode());
		}
		
		public String toString() {
			return src.toString() + " -> " + dst.toString();
		}
	}
}
