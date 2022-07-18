package com.kreative.bitsnpicas.importer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import com.kreative.bitsnpicas.GlyphPair;
import com.kreative.bitsnpicas.PathExtensions;
import com.kreative.bitsnpicas.VectorFont;
import com.kreative.bitsnpicas.VectorFontGlyph;
import com.kreative.bitsnpicas.VectorFontImporter;
import com.kreative.bitsnpicas.VectorInstruction;
import com.kreative.bitsnpicas.VectorPath;
import com.kreative.bitsnpicas.XMLUtility;

public class KpcaxVectorFontImporter implements VectorFontImporter {
	@Override
	public VectorFont[] importFont(byte[] data) throws IOException {
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		VectorFont f = parse("<byte[]>", in);
		in.close();
		return new VectorFont[]{f};
	}
	
	@Override
	public VectorFont[] importFont(InputStream is) throws IOException {
		VectorFont f = parse("<InputStream>", is);
		return new VectorFont[]{f};
	}
	
	@Override
	public VectorFont[] importFont(File file) throws IOException {
		FileInputStream in = new FileInputStream(file);
		VectorFont f = parse(file.getName(), in);
		in.close();
		return new VectorFont[]{f};
	}
	
	private static VectorFont parse(String name, InputStream in) throws IOException {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(true); // make sure the XML is valid
			factory.setExpandEntityReferences(false); // don't allow custom entities
			DocumentBuilder builder = factory.newDocumentBuilder();
			builder.setEntityResolver(XMLUtility.entityResolver("BitsNPicasVector", "kpcax.dtd", KpcaxVectorFontImporter.class));
			builder.setErrorHandler(XMLUtility.errorHandler(name));
			Document document = builder.parse(new InputSource(in));
			return parseDocument(document);
		} catch (ParserConfigurationException pce) {
			throw new IOException(pce);
		} catch (SAXException saxe) {
			throw new IOException(saxe);
		}
	}
	
	private static VectorFont parseDocument(Node node) throws IOException {
		String type = node.getNodeName();
		if (type.equalsIgnoreCase("#document")) {
			for (Node child : XMLUtility.getChildren(node)) {
				String ctype = child.getNodeName();
				if (ctype.equalsIgnoreCase("kpcas")) {
					if (child.hasAttributes() || child.hasChildNodes()) {
						return parseKpcas(child);
					}
				} else {
					throw new IOException("Unknown element: " + ctype);
				}
			}
			throw new IOException("Empty document.");
		} else {
			throw new IOException("Unknown element: " + type);
		}
	}
	
	private static VectorFont parseKpcas(Node node) throws IOException {
		VectorFont f = new VectorFont();
		for (Node child : XMLUtility.getChildren(node)) {
			String ctype = child.getNodeName();
			NamedNodeMap cattr = child.getAttributes();
			if (ctype.equalsIgnoreCase("prop")) {
				String id = XMLUtility.parseString(cattr, "id");
				Double dv = XMLUtility.parseDouble(cattr, "value");
				if ("emAscent".equalsIgnoreCase(id) && dv != null) f.setEmAscent2D(dv);
				if ("emDescent".equalsIgnoreCase(id) && dv != null) f.setEmDescent2D(dv);
				if ("lineAscent".equalsIgnoreCase(id) && dv != null) f.setLineAscent2D(dv);
				if ("lineDescent".equalsIgnoreCase(id) && dv != null) f.setLineDescent2D(dv);
				if ("lineGap".equalsIgnoreCase(id) && dv != null) f.setLineGap2D(dv);
				if ("xHeight".equalsIgnoreCase(id) && dv != null) f.setXHeight2D(dv);
				if ("capHeight".equalsIgnoreCase(id) && dv != null) f.setCapHeight2D(dv);
			} else if (ctype.equalsIgnoreCase("name")) {
				Integer id = XMLUtility.parseInt(cattr, "id");
				String name = XMLUtility.parseString(cattr, "value");
				if (id != null && name != null) f.setName(id, name);
			} else if (ctype.equalsIgnoreCase("g")) {
				Integer cp = XMLUtility.parseInt(cattr, "u");
				String gn = XMLUtility.parseString(cattr, "n");
				if (cp != null) f.putCharacter(cp, parseGlyph(cattr));
				if (gn != null) f.putNamedGlyph(gn, parseGlyph(cattr));
			} else if (ctype.equalsIgnoreCase("k")) {
				Integer o = XMLUtility.parseInt(cattr, "o");
				if (o != null) {
					Integer lu = XMLUtility.parseInt(cattr, "lu");
					String ln = XMLUtility.parseString(cattr, "ln");
					Integer ru = XMLUtility.parseInt(cattr, "ru");
					String rn = XMLUtility.parseString(cattr, "rn");
					if (lu != null) {
						if (ru != null) f.setKernPair(new GlyphPair(lu, ru), o);
						if (rn != null) f.setKernPair(new GlyphPair(lu, rn), o);
					}
					if (ln != null) {
						if (ru != null) f.setKernPair(new GlyphPair(ln, ru), o);
						if (rn != null) f.setKernPair(new GlyphPair(ln, rn), o);
					}
				}
			}
		}
		return f;
	}
	
	private static VectorFontGlyph parseGlyph(NamedNodeMap cattr) throws IOException {
		VectorFontGlyph g = new VectorFontGlyph();
		String d = XMLUtility.parseString(cattr, "d");
		if (d != null && d.length() > 0) parseVectorData(g.getContours(), d);
		Double w = XMLUtility.parseDouble(cattr, "w");
		g.setCharacterWidth2D((w == null) ? 0 : w);
		return g;
	}
	
	private static final Pattern PATH_TOKEN = Pattern.compile(
		"(ZZ|[A-Z])|([+-]?([0-9]+([.][0-9]*)?|[.][0-9]+)(E[+-]?[0-9]+)?)",
		Pattern.CASE_INSENSITIVE
	);
	
	public static void parseVectorData(Collection<VectorPath> paths, String d) {
		VectorPath path = null;
		char operation = 'M';
		List<Number> args = null;
		
		Matcher m = PATH_TOKEN.matcher(d);
		while (m.find()) {
			String os = m.group(1);
			if (os != null && os.length() > 0) {
				if (args != null) {
					if (path == null) path = new VectorPath();
					path.add(new VectorInstruction(operation, args));
					args = null;
				}
				if (os.equalsIgnoreCase("ZZ")) {
					if (path != null) {
						paths.add(path);
						path = null;
					}
				} else {
					operation = os.charAt(0);
					args = new ArrayList<Number>();
				}
			}
			String ns = m.group(2);
			if (ns != null && ns.length() > 0) {
				if (path == null) path = new VectorPath();
				if (args == null) args = new ArrayList<Number>();
				try { args.add(Double.parseDouble(ns)); }
				catch (NumberFormatException nfe) {}
				if (args.size() >= PathExtensions.getOperandCount(operation)) {
					path.add(new VectorInstruction(operation, args));
					if (operation == 'M') operation = 'L';
					if (operation == 'm') operation = 'l';
					args = null;
				}
			}
		}
		
		if (args != null) {
			if (path == null) path = new VectorPath();
			path.add(new VectorInstruction(operation, args));
			args = null;
		}
		if (path != null) {
			paths.add(path);
			path = null;
		}
	}
}
