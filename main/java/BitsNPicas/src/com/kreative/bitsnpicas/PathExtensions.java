package com.kreative.bitsnpicas;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.IllegalPathStateException;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.Arrays;
import java.util.List;

public class PathExtensions {
	public static final int CHORD = Arc2D.CHORD;
	public static final int OPEN = Arc2D.OPEN;
	public static final int PIE = Arc2D.PIE;
	public static final int WIND_NON_ZERO = GeneralPath.WIND_NON_ZERO;
	public static final int WIND_EVEN_ODD = GeneralPath.WIND_EVEN_ODD;
	
	private GeneralPath p;
	private double lcx, lx;
	private double lcy, ly;
	
	public PathExtensions() {
		this(new GeneralPath());
	}
	
	public PathExtensions(GeneralPath p) {
		this.p = p;
		Point2D p0 = p.getCurrentPoint();
		if (p0 == null) {
			p.moveTo(0, 0);
			this.lcx = this.lx = 0;
			this.lcy = this.ly = 0;
		} else {
			this.lcx = this.lx = p0.getX();
			this.lcy = this.ly = p0.getY();
		}
	}
	
	public GeneralPath getPath() {
		return p;
	}
	
	public Point2D getCurrentPoint() {
		return p.getCurrentPoint();
	}
	
	public void execute(char operation, Number... operands) {
		execute(operation, Arrays.asList(operands));
	}
	
	public void execute(char operation, List<? extends Number> operands) {
		double p0 = (operands.size() > 0) ? operands.get(0).doubleValue() : 0;
		double p1 = (operands.size() > 1) ? operands.get(1).doubleValue() : 0;
		double p2 = (operands.size() > 2) ? operands.get(2).doubleValue() : 0;
		double p3 = (operands.size() > 3) ? operands.get(3).doubleValue() : 0;
		double p4 = (operands.size() > 4) ? operands.get(4).doubleValue() : 0;
		double p5 = (operands.size() > 5) ? operands.get(5).doubleValue() : 0;
		double p6 = (operands.size() > 6) ? operands.get(6).doubleValue() : 0;
		switch (operation) {
			case 'M': moveTo(p0, p1); break;
			case 'm': moveTo(p0 + lx, p1 + ly); break;
			case 'H': horizTo(p0); break;
			case 'h': horizTo(p0 + lx); break;
			case 'V': vertTo(p0); break;
			case 'v': vertTo(p0 + ly); break;
			case 'L': lineTo(p0, p1); break;
			case 'l': lineTo(p0 + lx, p1 + ly); break;
			case 'Q': quadTo(p0, p1, p2, p3); break;
			case 'q': quadTo(p0 + lx, p1 + ly, p2 + lx, p3 + ly); break;
			case 'T': quadTo(p0, p1); break;
			case 't': quadTo(p0 + lx, p1 + ly); break;
			case 'C': curveTo(p0, p1, p2, p3, p4, p5); break;
			case 'c': curveTo(p0 + lx, p1 + ly, p2 + lx, p3 + ly, p4 + lx, p5 + ly); break;
			case 'S': curveTo(p0, p1, p2, p3); break;
			case 's': curveTo(p0 + lx, p1 + ly, p2 + lx, p3 + ly); break;
			case 'K': timmerTo(p0, p1, p2, p3, p4, p5); break;
			case 'k': timmerTo(p0 + lx, p1 + ly, p2 + lx, p3 + ly, p4 + lx, p5 + ly); break;
			case 'U': timmerTo(p0, p1, p2, p3); break;
			case 'u': timmerTo(p0 + lx, p1 + ly, p2 + lx, p3 + ly); break;
			case 'A': svgArcTo(p0, p1, p2, p3 != 0, p4 != 0, p5, p6); break;
			case 'a': svgArcTo(p0, p1, p2, p3 != 0, p4 != 0, p5 + lx, p6 + ly); break;
			case 'G': arcThroughTo(p0, p1, p2, p3); break;
			case 'g': arcThroughTo(p0 + lx, p1 + ly, p2 + lx, p3 + ly); break;
			case 'I': gerberArcTo(p0, p1, p2 != 0, p3 != 0, p4, p5); break;
			case 'i': gerberArcTo(p0, p1, p2 != 0, p3 != 0, p4 + lx, p5 + ly); break;
			case 'J': gerberArcTo(p0, p1, p2 != 0, p3 != 0, p4, p5); break;
			case 'j': gerberArcTo(p0, p1, p2 != 0, p3 != 0, p4 + lx, p5 + ly); break;
			case 'R': appendRectangle(p0, p1, p2, p3, p4, p5); break;
			case 'r': appendRectangle(p0 + lx, p1 + ly, p2, p3, p4, p5); break;
			case 'E': appendEllipse(p0, p1, p2, p3, p4, p5, (int)Math.round(p6)); break;
			case 'e': appendEllipse(p0 + lx, p1 + ly, p2, p3, p4, p5, (int)Math.round(p6)); break;
			case 'O': appendEllipse(p0, p1, p2, p3, p4, p5, (int)Math.round(p6)); break;
			case 'o': appendEllipse(p0 + lx, p1 + ly, p2, p3, p4, p5, (int)Math.round(p6)); break;
			case 'P': appendRegularPolygon(p0, p1, p2, p3, (int)Math.round(p4), (int)Math.round(p5)); break;
			case 'p': appendRegularPolygon(p0 + lx, p1 + ly, p2 + lx, p3 + ly, (int)Math.round(p4), (int)Math.round(p5)); break;
			case 'X': appendAsterisk(p0, p1, p2, p3, (int)Math.round(p4)); break;
			case 'x': appendAsterisk(p0 + lx, p1 + ly, p2 + lx, p3 + ly, (int)Math.round(p4)); break;
			case 'Z': closePath(); break;
			case 'z': closePath(); break;
			case 'W': setWindingRule((p0 == 0) ? WIND_NON_ZERO : WIND_EVEN_ODD); break;
			case 'w': setWindingRule((p0 == 0) ? WIND_NON_ZERO : WIND_EVEN_ODD); break;
		}
	}
	
	public void moveTo(double x, double y) {
		p.moveTo(lcx = lx = x, lcy = ly = y);
	}
	
	public void horizTo(double x) {
		p.lineTo(lcx = lx = x, lcy = ly);
	}
	
	public void vertTo(double y) {
		p.lineTo(lcx = lx, lcy = ly = y);
	}
	
	public void lineTo(double x, double y) {
		p.lineTo(lcx = lx = x, lcy = ly = y);
	}
	
	public void quadTo(double cx, double cy, double x, double y) {
		p.quadTo(lcx = cx, lcy = cy, lx = x, ly = y);
	}
	
	public void quadTo(double x, double y) {
		this.quadTo(lx + lx - lcx, ly + ly - lcy, x, y);
	}
	
	public void curveTo(double ccx, double ccy, double cx, double cy, double x, double y) {
		p.curveTo(ccx, ccy, lcx = cx, lcy = cy, lx = x, ly = y);
	}
	
	public void curveTo(double cx, double cy, double x, double y) {
		this.curveTo(lx + lx - lcx, ly + ly - lcy, cx, cy, x, y);
	}
	
	public void timmerTo(double ccx, double ccy, double cx, double cy, double x, double y) {
		timmerTo(p, ccx, ccy, lcx = cx, lcy = cy, lx = x, ly = y);
	}
	
	public void timmerTo(double cx, double cy, double x, double y) {
		this.timmerTo(lx + lx - lcx, ly + ly - lcy, cx, cy, x, y);
	}
	
	public void svgArcTo(double rx, double ry, double a, boolean large, boolean sweep, double x, double y) {
		svgArcTo(p, rx, ry, a, large, sweep, lcx = lx = x, lcy = ly = y);
	}
	
	public void arcThroughTo(double cx, double cy, double x, double y) {
		arcThroughTo(p, cx, cy, lcx = lx = x, lcy = ly = y);
	}
	
	public void gerberArcTo(double i, double j, boolean large, boolean sweep, double x, double y) {
		gerberArcTo(p, i, j, large, sweep, lcx = lx = x, lcy = ly = y);
	}
	
	public void appendRectangle(double x, double y, double w, double h, double rw, double rh) {
		Point2D p0 = appendRectangle(p, x, y, w, h, rw, rh);
		lcx = lx = p0.getX(); lcy = ly = p0.getY();
	}
	
	public void appendEllipse(double x, double y, double w, double h, double as, double ae, int at) {
		Point2D p0 = appendEllipse(p, x, y, w, h, as, ae, at);
		lcx = lx = p0.getX(); lcy = ly = p0.getY();
	}
	
	public void appendRegularPolygon(double cx, double cy, double ex, double ey, int sides, int skips) {
		Point2D p0 = appendRegularPolygon(p, cx, cy, ex, ey, sides, skips);
		lcx = lx = p0.getX(); lcy = ly = p0.getY();
	}
	
	public void appendAsterisk(double cx, double cy, double ex, double ey, int sides) {
		Point2D p0 = appendAsterisk(p, cx, cy, ex, ey, sides);
		lcx = lx = p0.getX(); lcy = ly = p0.getY();
	}
	
	public void closePath() {
		p.closePath();
		lcx = lx = p.getCurrentPoint().getX();
		lcy = ly = p.getCurrentPoint().getY();
	}
	
	public int getWindingRule() {
		return p.getWindingRule();
	}
	
	public void setWindingRule(int rule) {
		p.setWindingRule(rule);
	}
	
	public void append(PathIterator pi, boolean connect) {
		p.append(pi, connect);
		lcx = lx = p.getCurrentPoint().getX();
		lcy = ly = p.getCurrentPoint().getY();
	}
	
	public void append(Shape s, boolean connect) {
		p.append(s, connect);
		lcx = lx = p.getCurrentPoint().getX();
		lcy = ly = p.getCurrentPoint().getY();
	}
	
	public static int getOperandCount(char operation) {
		switch (operation) {
			case 'Z': case 'z':
				return 0;
			case 'H': case 'h':
			case 'V': case 'v':
			case 'W': case 'w':
				return 1;
			case 'M': case 'm':
			case 'L': case 'l':
			case 'T': case 't':
				return 2;
			case 'Q': case 'q':
			case 'S': case 's':
			case 'U': case 'u':
			case 'G': case 'g':
				return 4;
			case 'X': case 'x':
				return 5;
			case 'C': case 'c':
			case 'K': case 'k':
			case 'I': case 'i':
			case 'J': case 'j':
			case 'R': case 'r':
			case 'P': case 'p':
				return 6;
			case 'A': case 'a':
			case 'E': case 'e':
			case 'O': case 'o':
				return 7;
			default:
				return -1;
		}
	}
	
	public static void timmerTo(
		GeneralPath p, double rx, double ry,
		double sx, double sy, double x, double y
	) {
		Point2D p0 = p.getCurrentPoint();
		if (p0 == null) throw new IllegalPathStateException("missing initial moveto in path definition");
		double ax = p0.getX() + (rx - p0.getX()) * 4 / 3;
		double ay = p0.getY() + (ry - p0.getY()) * 4 / 3;
		double bx = x - (x - sx) * 4 / 3;
		double by = y - (y - sy) * 4 / 3;
		p.curveTo(ax, ay, bx, by, x, y);
	}
	
	public static CubicCurve2D createTimmer(
		double x0, double y0, double rx, double ry,
		double sx, double sy, double x, double y
	) {
		double ax = x0 + (rx - x0) * 4 / 3;
		double ay = y0 + (ry - y0) * 4 / 3;
		double bx = x - (x - sx) * 4 / 3;
		double by = y - (y - sy) * 4 / 3;
		return new CubicCurve2D.Double(x0, y0, ax, ay, bx, by, x, y);
	}
	
	public static void svgArcTo(
		GeneralPath p, double rx, double ry, double a,
		boolean large, boolean sweep, double x, double y
	) {
		Point2D p0 = p.getCurrentPoint();
		if (p0 == null) throw new IllegalPathStateException("missing initial moveto in path definition");
		Shape arc = createSvgArc(p0.getX(), p0.getY(), rx, ry, a, large, sweep, x, y);
		if (arc != null) p.append(arc, true);
		p.lineTo(x, y);
	}
	
	public static Shape createSvgArc(
		double x0, double y0, double rx, double ry, double a,
		boolean large, boolean sweep, double x, double y
	) {
		if (x0 == x && y0 == y) return null;
		if (rx == 0 || ry == 0) return null;
		double dx2 = (x0 - x) / 2;
		double dy2 = (y0 - y) / 2;
		a = Math.toRadians(a % 360);
		double ca = Math.cos(a);
		double sa = Math.sin(a);
		double x1 = sa * dy2 + ca * dx2;
		double y1 = ca * dy2 - sa * dx2;
		rx = Math.abs(rx);
		ry = Math.abs(ry);
		double Prx = rx * rx;
		double Pry = ry * ry;
		double Px1 = x1 * x1;
		double Py1 = y1 * y1;
		double rc = Px1/Prx + Py1/Pry;
		if (rc > 1) {
			rx = Math.sqrt(rc) * rx;
			ry = Math.sqrt(rc) * ry;
			Prx = rx * rx;
			Pry = ry * ry;
		}
		double s = (large == sweep) ? -1 : 1;
		double sq = ((Prx*Pry)-(Prx*Py1)-(Pry*Px1)) / ((Prx*Py1)+(Pry*Px1));
		if (sq < 0) sq = 0;
		double m = s * Math.sqrt(sq);
		double cx1 = m *  ((rx * y1) / ry);
		double cy1 = m * -((ry * x1) / rx);
		double sx2 = (x0 + x) / 2;
		double sy2 = (y0 + y) / 2;
		double cx = sx2 + ca * cx1 - sa * cy1;
		double cy = sy2 + sa * cx1 + ca * cy1;
		double ux = (x1 - cx1) / rx;
		double uy = (y1 - cy1) / ry;
		double vx = (-x1 -cx1) / rx;
		double vy = (-y1 -cy1) / ry;
		double sn = Math.sqrt(ux*ux + uy*uy);
		double sp = ux;
		double ss = (uy < 0) ? -1 : 1;
		double as = Math.toDegrees(ss * Math.acos(sp / sn));
		double en = Math.sqrt((ux*ux + uy*uy) * (vx*vx + vy*vy));
		double ep = ux * vx + uy * vy;
		double es = (ux * vy - uy * vx < 0) ? -1 : 1;
		double ae = Math.toDegrees(es * Math.acos(ep / en));
		if (!sweep && ae > 0) ae -= 360;
		if (sweep && ae < 0) ae += 360;
		ae %= 360;
		as %= 360;
		Arc2D.Double arc = new Arc2D.Double();
		arc.x = cx - rx;
		arc.y = cy - ry;
		arc.width = rx * 2;
		arc.height = ry * 2;
		arc.start = -as;
		arc.extent = -ae;
		double acx = arc.getCenterX();
		double acy = arc.getCenterY();
		AffineTransform t = AffineTransform.getRotateInstance(a, acx, acy);
		return t.createTransformedShape(arc);
	}
	
	public static void arcThroughTo(
		GeneralPath p, double x2, double y2, double x3, double y3
	) {
		Point2D p1 = p.getCurrentPoint();
		if (p1 == null) throw new IllegalPathStateException("missing initial moveto in path definition");
		Arc2D arc = createArcThrough(p1.getX(), p1.getY(), x2, y2, x3, y3);
		if (arc != null) p.append(arc, true);
		p.lineTo(x3, y3);
	}
	
	public static Arc2D createArcThrough(
		double x1, double y1, double x2, double y2, double x3, double y3
	) {
		if (x1 == x2 && x2 == x3) return null;
		if (y1 == y2 && y2 == y3) return null;
		double d = arcHK(x1, y1, x2, y2, x3, y3);
		double h = arcH(x1, y1, x2, y2, x3, y3) / d;
		double k = arcK(x1, y1, x2, y2, x3, y3) / d;
		if (Double.isNaN(h) || Double.isInfinite(h)) return null;
		if (Double.isNaN(k) || Double.isInfinite(k)) return null;
		double r = Math.hypot(k - y1, x1 - h);
		double a1 = Math.toDegrees(Math.atan2(k - y1, x1 - h));
		double a2 = Math.toDegrees(Math.atan2(k - y2, x2 - h));
		double a3 = Math.toDegrees(Math.atan2(k - y3, x3 - h));
		Arc2D.Double arc = new Arc2D.Double();
		arc.x = h - r;
		arc.y = k - r;
		arc.width = r + r;
		arc.height = r + r;
		arc.start = a1;
		if ((a1 <= a2 && a2 <= a3) || (a3 <= a2 && a2 <= a1)) {
			arc.extent = a3 - a1;
		} else if (a3 <= a1) {
			arc.extent = a3 - a1 + 360;
		} else {
			arc.extent = a3 - a1 - 360;
		}
		return arc;
	}
	
	private static double arcdet(double a, double b, double c, double d, double e, double f, double g, double h, double i) {
		return a*e*i + b*f*g + c*d*h - a*f*h - b*d*i - c*e*g;
	}
	private static double arcHK(double x1, double y1, double x2, double y2, double x3, double y3) {
		return arcdet(x1, y1, 1, x2, y2, 1, x3, y3, 1) * 2;
	}
	private static double arcH(double x1, double y1, double x2, double y2, double x3, double y3) {
		return arcdet(x1*x1 + y1*y1, y1, 1, x2*x2 + y2*y2, y2, 1, x3*x3 + y3*y3, y3, 1);
	}
	private static double arcK(double x1, double y1, double x2, double y2, double x3, double y3) {
		return arcdet(x1, x1*x1 + y1*y1, 1, x2, x2*x2 + y2*y2, 1, x3, x3*x3 + y3*y3, 1);
	}
	
	public static void gerberArcTo(
		GeneralPath p, double i, double j,
		boolean large, boolean sweep, double x, double y
	) {
		Point2D p0 = p.getCurrentPoint();
		if (p0 == null) throw new IllegalPathStateException("missing initial moveto in path definition");
		Arc2D arc = createGerberArc(p0.getX(), p0.getY(), i, j, large, sweep, x, y);
		if (arc != null) p.append(arc, true);
		p.lineTo(x, y);
	}
	
	public static Arc2D createGerberArc(
		double x0, double y0, double i, double j,
		boolean large, boolean sweep, double x, double y
	) {
		// Handle the 0° / 360° case explicitly.
		// Elsewhere we can assume 0° < extent < 360°. 
		if (Math.abs(x0-x) < 1e-6 && Math.abs(y0-y) < 1e-6) {
			if (large) {
				double r = Math.hypot(i, j);
				if (r < 1e-6) return null;
				double t = Math.toDegrees(Math.atan2(j, -i));
				double e = sweep ? (-360) : (+360);
				return new Arc2D.Double(x0+i-r, y0+j-r, r+r, r+r, t, e, Arc2D.OPEN);
			} else {
				return null;
			}
		}
		double r1 = Math.hypot(i, j);
		GerberArcCandidate a1 = new GerberArcCandidate(x0, y0, r1, x0 + i, y0 + j, x, y, sweep);
		if (large) return a1.arc;
		GerberArcCandidate a2 = new GerberArcCandidate(x0, y0, r1, x0 + i, y0 - j, x, y, sweep);
		GerberArcCandidate a3 = new GerberArcCandidate(x0, y0, r1, x0 - i, y0 + j, x, y, sweep);
		GerberArcCandidate a4 = new GerberArcCandidate(x0, y0, r1, x0 - i, y0 - j, x, y, sweep);
		GerberArcCandidate a = null;
		if (Math.abs(a1.extent) <= 90 && (a == null || a1.deviation < a.deviation)) a = a1;
		if (Math.abs(a2.extent) <= 90 && (a == null || a2.deviation < a.deviation)) a = a2;
		if (Math.abs(a3.extent) <= 90 && (a == null || a3.deviation < a.deviation)) a = a3;
		if (Math.abs(a4.extent) <= 90 && (a == null || a4.deviation < a.deviation)) a = a4;
		return (a != null) ? a.arc : null;
	}
	
	private static class GerberArcCandidate {
		public final double deviation;
		public final double extent;
		public final Arc2D arc;
		public GerberArcCandidate(double x0, double y0, double r1, double cx, double cy, double x, double y, boolean sweep) {
			double r2 = Math.hypot(x - cx, y - cy);
			this.deviation = Math.abs(r1 - r2);
			double r = (r1 + r2) / 2;
			double start = Math.toDegrees(Math.atan2(cy - y0, x0 - cx));
			double end = Math.toDegrees(Math.atan2(cy - y, x - cx));
			if (sweep) this.extent = ((end < start) ? (end - start) : ((end - start) - 360));
			else       this.extent = ((end > start) ? (end - start) : ((end - start) + 360));
			this.arc = new Arc2D.Double(cx-r, cy-r, r+r, r+r, start, extent, Arc2D.OPEN);
		}
	}
	
	public static Point2D appendRectangle(GeneralPath p, double x, double y, double w, double h, double rw, double rh) {
		if (rw == 0 || rh == 0) {
			Point2D p0 = p.getCurrentPoint();
			if (p0 == null) p0 = new Point2D.Double(x+w/2, y+h/2);
			p.append(new Rectangle2D.Double(x, y, w, h), false);
			p.moveTo(p0.getX(), p0.getY());
			return p0;
		} else {
			Point2D p0 = p.getCurrentPoint();
			if (p0 == null) p0 = new Point2D.Double(x+w/2, y+h/2);
			p.append(new RoundRectangle2D.Double(x, y, w, h, rw, rh), false);
			p.moveTo(p0.getX(), p0.getY());
			return p0;
		}
	}
	
	public static Point2D appendEllipse(GeneralPath p, double x, double y, double w, double h, double as, double ae, int at) {
		at = Math.abs(at) % 5;
		if (ae <= -360 || ae >= 360) {
			Point2D p0 = p.getCurrentPoint();
			if (p0 == null) p0 = new Point2D.Double(x+w/2, y+h/2);
			p.append(new Ellipse2D.Double(x, y, w, h), false);
			p.moveTo(p0.getX(), p0.getY());
			return p0;
		} else if (at < 3) {
			Point2D p0 = p.getCurrentPoint();
			if (p0 == null) p0 = new Point2D.Double(x+w/2, y+h/2);
			p.append(new Arc2D.Double(x, y, w, h, as, ae, at), false);
			p.moveTo(p0.getX(), p0.getY());
			return p0;
		} else {
			p.append(new Arc2D.Double(x, y, w, h, as, ae, Arc2D.OPEN), at > 3);
			return p.getCurrentPoint();
		}
	}
	
	public static Point2D appendRegularPolygon(GeneralPath p, double cx, double cy, double ex, double ey, int sides, int skips) {
		Point2D p0 = p.getCurrentPoint();
		if (p0 == null) p0 = new Point2D.Double(cx, cy);
		p.append(createRegularPolygon(cx, cy, ex, ey, sides, skips), false);
		p.moveTo(p0.getX(), p0.getY());
		return p0;
	}
	
	public static GeneralPath createRegularPolygon(double cx, double cy, double ex, double ey, int sides, int skips) {
		if (sides < 3) sides = 3;
		if (skips < 1) skips = 1;
		GeneralPath p = new GeneralPath();
		double r = Math.hypot(ey - cy, ex - cx);
		double t = Math.atan2(ey - cy, ex - cx);
		for (int n = 0, j = 0; n < sides; j++) {
			for (int i = 0; n < sides && ((i == 0) || ((i % sides) != 0)); n++, i += skips) {
				double a = t + (Math.PI * (double)((i+j)*2) / (double)(sides));
				double x = cx + r * Math.cos(a);
				double y = cy + r * Math.sin(a);
				if (i == 0) p.moveTo((float)x, (float)y);
				else p.lineTo((float)x, (float)y);
			}
			p.closePath();
		}
		return p;
	}
	
	public static Point2D appendAsterisk(GeneralPath p, double cx, double cy, double ex, double ey, int sides) {
		Point2D p0 = p.getCurrentPoint();
		if (p0 == null) p0 = new Point2D.Double(cx, cy);
		p.append(createAsterisk(cx, cy, ex, ey, sides), false);
		p.moveTo(p0.getX(), p0.getY());
		return p0;
	}
	
	public static GeneralPath createAsterisk(double cx, double cy, double ex, double ey, int sides) {
		if (sides < 1) sides = 1;
		GeneralPath p = new GeneralPath();
		double r = Math.hypot(ey - cy, ex - cx);
		double t = Math.atan2(ey - cy, ex - cx);
		for (int i = 0; i < sides; i++) {
			double a = t + (Math.PI * (double)(i*2) / (double)(sides));
			double x = cx + r * Math.cos(a);
			double y = cy + r * Math.sin(a);
			p.moveTo((float)cx, (float)cy);
			p.lineTo((float)x, (float)y);
		}
		return p;
	}
}
