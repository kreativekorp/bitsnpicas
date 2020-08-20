package com.kreative.keyedit;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class MacKeyboardParser {
	public static MacKeyboard parse(String name, InputStream in) throws IOException {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(true); // make sure the XML is valid
			factory.setExpandEntityReferences(false); // don't allow custom entities
			DocumentBuilder builder = factory.newDocumentBuilder();
			builder.setEntityResolver(new KeylayoutEntityResolver());
			builder.setErrorHandler(new KeylayoutErrorHandler(name));
			Document document = builder.parse(new InputSource(fixStream(in)));
			return parseDocument(document);
		} catch (ParserConfigurationException pce) {
			throw new IOException(pce);
		} catch (SAXException saxe) {
			throw new IOException(saxe);
		}
	}
	
	private static MacKeyboard parseDocument(Node node) throws IOException {
		String type = node.getNodeName();
		if (type.equalsIgnoreCase("#document")) {
			for (Node child : getChildren(node)) {
				String ctype = child.getNodeName();
				if (ctype.equalsIgnoreCase("keyboard")) {
					if (child.hasAttributes() || child.hasChildNodes()) {
						return parseKeyboard(child);
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
	
	private static MacKeyboard parseKeyboard(Node node) throws IOException {
		String type = node.getNodeName();
		if (type.equalsIgnoreCase("keyboard")) {
			NamedNodeMap attr = node.getAttributes();
			int group = parseInt(attr, "group", 126);
			int id = parseInt(attr, "id", -1);
			String name = parseString(attr, "name", true);
			int maxout = parseInt(attr, "maxout", 1);
			MacKeyboard kbd = new MacKeyboard(group, id, name, maxout);
			for (Node child : getChildren(node)) {
				String ctype = child.getNodeName();
				if (ctype.equalsIgnoreCase("layouts")) {
					kbd.layouts.add(parseLayouts(child));
				} else if (ctype.equalsIgnoreCase("modifierMap")) {
					kbd.modifierMaps.add(parseModifierMap(child));
				} else if (ctype.equalsIgnoreCase("keyMapSet")) {
					kbd.keyMapSets.add(parseKeyMapSet(child));
				} else if (ctype.equalsIgnoreCase("actions")) {
					kbd.actions.add(parseActions(child));
				} else if (ctype.equalsIgnoreCase("terminators")) {
					kbd.terminators.add(parseTerminators(child));
				} else {
					throw new IOException("Unknown element: " + ctype);
				}
			}
			return kbd;
		} else {
			throw new IOException("Unknown element: " + type);
		}
	}
	
	private static MacKeyboard.Layouts parseLayouts(Node node) throws IOException {
		String type = node.getNodeName();
		if (type.equalsIgnoreCase("layouts")) {
			MacKeyboard.Layouts layouts = new MacKeyboard.Layouts();
			for (Node child : getChildren(node)) {
				layouts.add(parseLayout(child));
			}
			return layouts;
		} else {
			throw new IOException("Unknown element: " + type);
		}
	}
	
	private static MacKeyboard.Layout parseLayout(Node node) throws IOException {
		String type = node.getNodeName();
		if (type.equalsIgnoreCase("layout")) {
			NamedNodeMap attr = node.getAttributes();
			int first = parseInt(attr, "first", 0);
			int last = parseInt(attr, "last", 255);
			String modifiers = parseString(attr, "modifiers", true);
			String mapSet = parseString(attr, "mapSet", true);
			return new MacKeyboard.Layout(first, last, modifiers, mapSet);
		} else {
			throw new IOException("Unknown element: " + type);
		}
	}
	
	private static MacKeyboard.ModifierMap parseModifierMap(Node node) throws IOException {
		String type = node.getNodeName();
		if (type.equalsIgnoreCase("modifierMap")) {
			NamedNodeMap attr = node.getAttributes();
			String id = parseString(attr, "id", true);
			int defaultIndex = parseInt(attr, "defaultIndex", 0);
			MacKeyboard.ModifierMap mm = new MacKeyboard.ModifierMap(id, defaultIndex);
			for (Node child : getChildren(node)) {
				mm.add(parseKeyMapSelect(child));
			}
			return mm;
		} else {
			throw new IOException("Unknown element: " + type);
		}
	}
	
	private static MacKeyboard.KeyMapSelect parseKeyMapSelect(Node node) throws IOException {
		String type = node.getNodeName();
		if (type.equalsIgnoreCase("keyMapSelect")) {
			NamedNodeMap attr = node.getAttributes();
			int mapIndex = parseInt(attr, "mapIndex", -1);
			MacKeyboard.KeyMapSelect kms = new MacKeyboard.KeyMapSelect(mapIndex);
			for (Node child : getChildren(node)) {
				kms.add(parseModifier(child));
			}
			return kms;
		} else {
			throw new IOException("Unknown element: " + type);
		}
	}
	
	private static MacKeyboard.Modifier parseModifier(Node node) throws IOException {
		String type = node.getNodeName();
		if (type.equalsIgnoreCase("modifier")) {
			NamedNodeMap attr = node.getAttributes();
			String keys = parseString(attr, "keys", true);
			return new MacKeyboard.Modifier(keys);
		} else {
			throw new IOException("Unknown element: " + type);
		}
	}
	
	private static MacKeyboard.KeyMapSet parseKeyMapSet(Node node) throws IOException {
		String type = node.getNodeName();
		if (type.equalsIgnoreCase("keyMapSet")) {
			NamedNodeMap attr = node.getAttributes();
			String id = parseString(attr, "id", true);
			MacKeyboard.KeyMapSet kms = new MacKeyboard.KeyMapSet(id);
			for (Node child : getChildren(node)) {
				kms.add(parseKeyMap(child));
			}
			return kms;
		} else {
			throw new IOException("Unknown element: " + type);
		}
	}
	
	private static MacKeyboard.KeyMap parseKeyMap(Node node) throws IOException {
		String type = node.getNodeName();
		if (type.equalsIgnoreCase("keyMap")) {
			NamedNodeMap attr = node.getAttributes();
			int index = parseInt(attr, "index", -1);
			MacKeyboard.KeyMap keymap = new MacKeyboard.KeyMap(index);
			for (Node child : getChildren(node)) {
				keymap.add(parseKey(child));
			}
			return keymap;
		} else {
			throw new IOException("Unknown element: " + type);
		}
	}
	
	private static MacKeyboard.Key parseKey(Node node) throws IOException {
		String type = node.getNodeName();
		if (type.equalsIgnoreCase("key")) {
			NamedNodeMap attr = node.getAttributes();
			int code = parseInt(attr, "code", -1);
			String output = parseString(attr, "output", false);
			String action = parseString(attr, "action", true);
			return new MacKeyboard.Key(code, output, action);
		} else {
			throw new IOException("Unknown element: " + type);
		}
	}
	
	private static MacKeyboard.Actions parseActions(Node node) throws IOException {
		String type = node.getNodeName();
		if (type.equalsIgnoreCase("actions")) {
			MacKeyboard.Actions actions = new MacKeyboard.Actions();
			for (Node child : getChildren(node)) {
				actions.add(parseAction(child));
			}
			return actions;
		} else {
			throw new IOException("Unknown element: " + type);
		}
	}
	
	private static MacKeyboard.Action parseAction(Node node) throws IOException {
		String type = node.getNodeName();
		if (type.equalsIgnoreCase("action")) {
			NamedNodeMap attr = node.getAttributes();
			String id = parseString(attr, "id", true);
			MacKeyboard.Action action = new MacKeyboard.Action(id);
			for (Node child : getChildren(node)) {
				action.add(parseWhen(child));
			}
			return action;
		} else {
			throw new IOException("Unknown element: " + type);
		}
	}
	
	private static MacKeyboard.When parseWhen(Node node) throws IOException {
		String type = node.getNodeName();
		if (type.equalsIgnoreCase("when")) {
			NamedNodeMap attr = node.getAttributes();
			String state = parseString(attr, "state", true);
			String through = parseString(attr, "through", true);
			String output = parseString(attr, "output", false);
			String multiplier = parseString(attr, "multiplier", true);
			String next = parseString(attr, "next", true);
			return new MacKeyboard.When(state, through, output, multiplier, next);
		} else {
			throw new IOException("Unknown element: " + type);
		}
	}
	
	private static MacKeyboard.Terminators parseTerminators(Node node) throws IOException {
		String type = node.getNodeName();
		if (type.equalsIgnoreCase("terminators")) {
			MacKeyboard.Terminators terminators = new MacKeyboard.Terminators();
			for (Node child : getChildren(node)) {
				terminators.add(parseWhen(child));
			}
			return terminators;
		} else {
			throw new IOException("Unknown element: " + type);
		}
	}
	
	private static int parseInt(NamedNodeMap attr, String key, int def) {
		if (attr == null) return def;
		Node node = attr.getNamedItem(key);
		if (node == null) return def;
		String text = node.getTextContent();
		if (text == null) return def;
		try { return Integer.parseInt(text.trim()); }
		catch (NumberFormatException nfe) { return def; }
	}
	
	private static String parseString(NamedNodeMap attr, String key, boolean trim) {
		if (attr == null) return null;
		Node node = attr.getNamedItem(key);
		if (node == null) return null;
		String text = node.getTextContent();
		if (text == null) return null;
		text = unfixString(text);
		return trim ? text.trim() : text;
	}
	
	private static String unfixString(String s) {
		boolean unfixed = false;
		char[] ch = s.toCharArray();
		for (int i = 0; i < ch.length; i++) {
			if (ch[i] >= 0xFDD0 && ch[i] <= 0xFDEF) {
				ch[i] -= 0xFDD0;
				unfixed = true;
			}
		}
		return unfixed ? String.valueOf(ch) : s;
	}
	
	private static List<Node> getChildren(Node node) {
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
	
	private static final Pattern CHAR_REF_PATTERN = Pattern.compile("&#(x([0-9A-Fa-f]+)|([0-9]+));");
	
	private static String fixString(String s) {
		StringBuffer sb = new StringBuffer();
		Matcher m = CHAR_REF_PATTERN.matcher(s);
		while (m.find()) {
			int v = (
				(m.group(2) != null && m.group(2).length() > 0) ? Integer.parseInt(m.group(2), 16) :
				(m.group(3) != null && m.group(3).length() > 0) ? Integer.parseInt(m.group(3), 10) :
				-1
			);
			if (v >= 0x00 && v < 0x20) {
				m.appendReplacement(sb, ("&#x" + Integer.toHexString(0xFDD0+v) + ";"));
			} else {
				m.appendReplacement(sb, m.group());
			}
		}
		m.appendTail(sb);
		return sb.toString();
	}
	
	private static Reader fixStream(InputStream in) {
		StringBuffer sb = new StringBuffer();
		Scanner scan = new Scanner(in, "UTF-8");
		while (scan.hasNextLine()) {
			sb.append(fixString(scan.nextLine()));
			sb.append("\n");
		}
		scan.close();
		return new StringReader(sb.toString());
	}
	
	private static class KeylayoutEntityResolver implements EntityResolver {
		@Override
		public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
			if (systemId.contains("KeyboardLayout.dtd")) {
				return new InputSource(MacKeyboardParser.class.getResourceAsStream("keylayout.dtd"));
			} else {
				return null;
			}
		}
	}
	
	private static class KeylayoutErrorHandler implements ErrorHandler {
		private final String name;
		public KeylayoutErrorHandler(String name) {
			this.name = name;
		}
		@Override
		public void error(SAXParseException e) throws SAXException {
			//System.err.print("Warning: Failed to parse " + name + ": ");
			//System.err.println("ERROR on "+e.getLineNumber()+":"+e.getColumnNumber()+": "+e.getMessage());
		}
		@Override
		public void fatalError(SAXParseException e) throws SAXException {
			System.err.print("Warning: Failed to parse " + name + ": ");
			System.err.println("FATAL ERROR on "+e.getLineNumber()+":"+e.getColumnNumber()+": "+e.getMessage());
		}
		@Override
		public void warning(SAXParseException e) throws SAXException {
			//System.err.print("Warning: Failed to parse " + name + ": ");
			//System.err.println("WARNING on "+e.getLineNumber()+":"+e.getColumnNumber()+": "+e.getMessage());
		}
	}
}
