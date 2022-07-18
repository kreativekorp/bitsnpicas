package com.kreative.bitsnpicas;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XMLUtility {
	public static String wrap(String tag, boolean close, String... attrs) {
		StringBuffer sb = new StringBuffer();
		sb.append("<");
		sb.append(tag);
		int i = 0;
		while (i + 1 < attrs.length) {
			sb.append(" ");
			sb.append(attrs[i++]);
			sb.append("=\"");
			sb.append(xmlEncode(attrs[i++]));
			sb.append("\"");
		}
		if (i < attrs.length) {
			sb.append(">");
			sb.append(xmlEncode(attrs[i]));
			if (close) {
				sb.append("</");
				sb.append(tag);
				sb.append(">");
			}
		} else {
			sb.append(close ? "/>" : ">");
		}
		return sb.toString();
	}
	
	public static String xmlEncode(String s) {
		if (s == null) return "";
		StringBuffer sb = new StringBuffer();
		int i = 0;
		while (i < s.length()) {
			int ch = s.codePointAt(i);
			switch (ch) {
				case '&': sb.append("&amp;"); break;
				case '<': sb.append("&lt;"); break;
				case '>': sb.append("&gt;"); break;
				default:
					if (ch >= 0x20 && ch < 0x7F) {
						sb.append((char)ch);
						break;
					}
					// fallthrough;
				case '"':
				case '\'':
					sb.append("&#" + ch + ";");
					break;
			}
			i += Character.charCount(ch);
		}
		return sb.toString();
	}
	
	public static String parseString(NamedNodeMap attr, String key) {
		if (attr == null) return null;
		Node node = attr.getNamedItem(key);
		if (node == null) return null;
		String text = node.getTextContent();
		if (text == null) return null;
		return text.trim();
	}
	
	public static Integer parseInt(NamedNodeMap attr, String key) {
		if (attr == null) return null;
		Node node = attr.getNamedItem(key);
		if (node == null) return null;
		String text = node.getTextContent();
		if (text == null) return null;
		try { return Integer.parseInt(text.trim()); }
		catch (NumberFormatException nfe) { return null; }
	}
	
	public static Double parseDouble(NamedNodeMap attr, String key) {
		if (attr == null) return null;
		Node node = attr.getNamedItem(key);
		if (node == null) return null;
		String text = node.getTextContent();
		if (text == null) return null;
		try { return Double.parseDouble(text.trim()); }
		catch (NumberFormatException nfe) { return null; }
	}
	
	public static List<Node> getChildren(Node node) {
		List<Node> list = new ArrayList<Node>();
		if (node != null) {
			NodeList children = node.getChildNodes();
			if (children != null) {
				int count = children.getLength();
				for (int i = 0; i < count; i++) {
					Node child = children.item(i);
					if (child != null) {
						String type = child.getNodeName();
						if (type.equalsIgnoreCase("#text") || type.equalsIgnoreCase("#comment")) {
							continue;
						} else {
							list.add(child);
						}
					}
				}
			}
		}
		return list;
	}
	
	public static EntityResolver entityResolver(final String pubName, final String dtdName, final Class<?> resCls) {
		return new EntityResolver() {
			@Override
			public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
				if (publicId.contains(pubName) || systemId.contains(dtdName)) {
					return new InputSource(resCls.getResourceAsStream(dtdName));
				} else {
					return null;
				}
			}
		};
	}
	
	public static ErrorHandler errorHandler(final String name) {
		return new ErrorHandler() {
			@Override
			public void error(SAXParseException e) throws SAXException {
				System.err.print("Warning: Failed to parse " + name + ": ");
				System.err.println("ERROR on "+e.getLineNumber()+":"+e.getColumnNumber()+": "+e.getMessage());
			}
			@Override
			public void fatalError(SAXParseException e) throws SAXException {
				System.err.print("Warning: Failed to parse " + name + ": ");
				System.err.println("FATAL ERROR on "+e.getLineNumber()+":"+e.getColumnNumber()+": "+e.getMessage());
			}
			@Override
			public void warning(SAXParseException e) throws SAXException {
				System.err.print("Warning: Failed to parse " + name + ": ");
				System.err.println("WARNING on "+e.getLineNumber()+":"+e.getColumnNumber()+": "+e.getMessage());
			}
		};
	}
}
