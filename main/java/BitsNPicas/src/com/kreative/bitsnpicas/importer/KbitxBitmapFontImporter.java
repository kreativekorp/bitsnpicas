package com.kreative.bitsnpicas.importer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import com.kreative.bitsnpicas.Base64InputStream;
import com.kreative.bitsnpicas.BitmapFont;
import com.kreative.bitsnpicas.BitmapFontGlyph;
import com.kreative.bitsnpicas.BitmapFontImporter;
import com.kreative.bitsnpicas.GlyphPair;
import com.kreative.bitsnpicas.WIBInputStream;
import com.kreative.bitsnpicas.XMLUtility;

public class KbitxBitmapFontImporter implements BitmapFontImporter {
	@Override
	public BitmapFont[] importFont(byte[] data) throws IOException {
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		BitmapFont f = parse("<byte[]>", in);
		in.close();
		return new BitmapFont[]{f};
	}
	
	@Override
	public BitmapFont[] importFont(InputStream is) throws IOException {
		BitmapFont f = parse("<InputStream>", is);
		return new BitmapFont[]{f};
	}
	
	@Override
	public BitmapFont[] importFont(File file) throws IOException {
		FileInputStream in = new FileInputStream(file);
		BitmapFont f = parse(file.getName(), in);
		in.close();
		return new BitmapFont[]{f};
	}
	
	private static BitmapFont parse(String name, InputStream in) throws IOException {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(true); // make sure the XML is valid
			factory.setExpandEntityReferences(false); // don't allow custom entities
			DocumentBuilder builder = factory.newDocumentBuilder();
			builder.setEntityResolver(XMLUtility.entityResolver("BitsNPicasBitmap", "kbitx.dtd", KbitxBitmapFontImporter.class));
			builder.setErrorHandler(XMLUtility.errorHandler(name));
			Document document = builder.parse(new InputSource(in));
			return parseDocument(document);
		} catch (ParserConfigurationException pce) {
			throw new IOException(pce);
		} catch (SAXException saxe) {
			throw new IOException(saxe);
		}
	}
	
	private static BitmapFont parseDocument(Node node) throws IOException {
		String type = node.getNodeName();
		if (type.equalsIgnoreCase("#document")) {
			for (Node child : XMLUtility.getChildren(node)) {
				String ctype = child.getNodeName();
				if (ctype.equalsIgnoreCase("kbits")) {
					if (child.hasAttributes() || child.hasChildNodes()) {
						return parseKbits(child);
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
	
	private static BitmapFont parseKbits(Node node) throws IOException {
		BitmapFont f = new BitmapFont();
		for (Node child : XMLUtility.getChildren(node)) {
			String ctype = child.getNodeName();
			NamedNodeMap cattr = child.getAttributes();
			if (ctype.equalsIgnoreCase("prop")) {
				String id = XMLUtility.parseString(cattr, "id");
				Integer iv = XMLUtility.parseInt(cattr, "value");
				if ("emAscent".equalsIgnoreCase(id) && iv != null) f.setEmAscent(iv);
				if ("emDescent".equalsIgnoreCase(id) && iv != null) f.setEmDescent(iv);
				if ("lineAscent".equalsIgnoreCase(id) && iv != null) f.setLineAscent(iv);
				if ("lineDescent".equalsIgnoreCase(id) && iv != null) f.setLineDescent(iv);
				if ("lineGap".equalsIgnoreCase(id) && iv != null) f.setLineGap(iv);
				if ("xHeight".equalsIgnoreCase(id) && iv != null) f.setXHeight(iv);
				if ("capHeight".equalsIgnoreCase(id) && iv != null) f.setCapHeight(iv);
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
	
	private static BitmapFontGlyph parseGlyph(NamedNodeMap cattr) throws IOException {
		byte[][] data;
		String d = XMLUtility.parseString(cattr, "d");
		if (d == null || d.length() == 0) {
			data = new byte[0][0];
		} else {
			Base64InputStream bs = new Base64InputStream(d);
			int dh = readLEB128(bs);
			int dw = readLEB128(bs);
			data = new byte[dh][dw];
			WIBInputStream ws = new WIBInputStream(bs);
			for (byte[] row : data) ws.read(row);
			ws.close();
			bs.close();
		}
		Integer x = XMLUtility.parseInt(cattr, "x");
		Integer y = XMLUtility.parseInt(cattr, "y");
		Integer w = XMLUtility.parseInt(cattr, "w");
		return new BitmapFontGlyph(
			data,
			((x == null) ? 0 : x),
			((w == null) ? 0 : w),
			((y == null) ? 0 : y)
		);
	}
	
	private static int readLEB128(InputStream in) throws IOException {
		int v = 0, s = 0;
		for (int b = in.read(); b >= 0; b = in.read()) {
			v |= (b & 0x7F) << s;
			if ((b & 0x80) == 0) break;
			s += 7;
		}
		return v;
	}
}
