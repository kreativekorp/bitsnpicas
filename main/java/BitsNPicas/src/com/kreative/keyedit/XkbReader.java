package com.kreative.keyedit;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XkbReader {
	public static KeyboardMapping read(File file) throws IOException {
		KeyboardMapping km = new KeyboardMapping();
		readEvdev(file, km);
		readSymbols(file, km);
		File icon = new File(file.getParentFile(), file.getName() + ".png");
		if (icon.isFile()) km.icon = ImageIO.read(icon);
		return km;
	}
	
	public static void readEvdev(File file, KeyboardMapping km) throws IOException {
		StringBuffer sb = new StringBuffer(file.getName());
		file = file.getParentFile();
		while (file != null) {
			if (file.getName().equals("symbols")) {
				File rules = new File(file.getParentFile(), "rules");
				if (rules.isDirectory()) {
					boolean found = false;
					for (File f : rules.listFiles()) {
						if (f.getName().toLowerCase().endsWith(".xml")) {
							readEvdev(sb.toString(), f, km);
							found = true;
						}
					}
					if (found) return;
				}
			}
			File f = new File(file, "evdev.xml");
			if (f.isFile()) {
				readEvdev(sb.toString(), f, km);
				return;
			}
			sb.insert(0, "/");
			sb.insert(0, file.getName());
			file = file.getParentFile();
		}
	}
	
	public static void readEvdev(String matchingPath, File file, KeyboardMapping km) throws IOException {
		FileInputStream in = new FileInputStream(file);
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(true); // make sure the XML is valid
			factory.setExpandEntityReferences(false); // don't allow custom entities
			DocumentBuilder builder = factory.newDocumentBuilder();
			builder.setEntityResolver(new EvdevEntityResolver());
			builder.setErrorHandler(new EvdevErrorHandler(file.getName()));
			Document document = builder.parse(new InputSource(in));
			parseDocument(matchingPath, document, km);
		} catch (ParserConfigurationException pce) {
			throw new IOException(pce);
		} catch (SAXException saxe) {
			throw new IOException(saxe);
		} finally {
			in.close();
		}
	}
	
	private static void parseDocument(String matchingPath, Node node, KeyboardMapping km) throws IOException {
		String type = node.getNodeName();
		if (type.equalsIgnoreCase("#document")) {
			for (Node child : getChildren(node)) {
				String ctype = child.getNodeName();
				if (ctype.equalsIgnoreCase("xkbConfigRegistry")) {
					if (child.hasAttributes() || child.hasChildNodes()) {
						parseXkbConfigRegistry(matchingPath, child, km);
						return;
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
	
	private static void parseXkbConfigRegistry(String matchingPath, Node node, KeyboardMapping km) throws IOException {
		String type = node.getNodeName();
		if (type.equalsIgnoreCase("xkbConfigRegistry")) {
			for (Node child : getChildren(node)) {
				String ctype = child.getNodeName();
				if (ctype.equalsIgnoreCase("layoutList")) {
					parseLayoutList(matchingPath, child, km);
				}
			}
		} else {
			throw new IOException("Unknown element: " + type);
		}
	}
	
	private static void parseLayoutList(String matchingPath, Node node, KeyboardMapping km) throws IOException {
		String type = node.getNodeName();
		if (type.equalsIgnoreCase("layoutList")) {
			for (Node child : getChildren(node)) {
				String ctype = child.getNodeName();
				if (ctype.equalsIgnoreCase("layout")) {
					parseLayout(matchingPath, child, km);
				}
			}
		} else {
			throw new IOException("Unknown element: " + type);
		}
	}
	
	private static void parseLayout(String matchingPath, Node node, KeyboardMapping km) throws IOException {
		String type = node.getNodeName();
		if (type.equalsIgnoreCase("layout")) {
			for (Node child : getChildren(node)) {
				String ctype = child.getNodeName();
				if (ctype.equalsIgnoreCase("configItem")) {
					parseConfigItem(matchingPath, child, km);
				}
			}
		} else {
			throw new IOException("Unknown element: " + type);
		}
	}
	
	private static void parseConfigItem(String matchingPath, Node node, KeyboardMapping km) throws IOException {
		String type = node.getNodeName();
		if (type.equalsIgnoreCase("configItem")) {
			String path = null;
			String label = null;
			String name = null;
			for (Node child : getChildren(node)) {
				String ctype = child.getNodeName();
				if (ctype.equalsIgnoreCase("name")) {
					path = child.getTextContent();
				} else if (ctype.equalsIgnoreCase("shortDescription")) {
					label = child.getTextContent();
				} else if (ctype.equalsIgnoreCase("description")) {
					name = child.getTextContent();
				}
			}
			if (matchingPath == null || matchingPath.equals(path)) {
				km.xkbPath = path;
				km.xkbLabel = label;
				km.name = name;
			}
		} else {
			throw new IOException("Unknown element: " + type);
		}
	}
	
	public static void readSymbols(File in, KeyboardMapping km) throws IOException {
		Scanner scan = new Scanner(new FileInputStream(in), "UTF-8");
		readSymbols(scan, km);
		scan.close();
	}
	
	public static void readSymbols(Scanner in, KeyboardMapping km) {
		km.xkbComment = null;
		km.xkbAltGrKey = XkbAltGrKey.none;
		km.xkbComposeKey = XkbComposeKey.none;
		while (in.hasNextLine()) {
			String line = in.nextLine().trim();
			if (line.startsWith("//")) {
				line = line.substring(2).trim();
				if (km.xkbComment == null) {
					km.xkbComment = line;
				} else {
					km.xkbComment += "\n";
					km.xkbComment += line;
				}
				continue;
			}
			Matcher nameMatch = NAME_PATTERN.matcher(line);
			if (nameMatch.find()) {
				km.name = nameMatch.group(1);
				continue;
			}
			Matcher keyMatch = KEY_PATTERN.matcher(line);
			if (keyMatch.find()) {
				XkbKey key = XkbKey.forId(keyMatch.group(1));
				if (key != null) {
					String[] fields = keyMatch.group(2).trim().split(",");
					String u = (fields.length > 0) ? fields[0].trim() : null;
					String s = (fields.length > 1) ? fields[1].trim() : null;
					String au = (fields.length > 2) ? fields[2].trim() : null;
					String as = (fields.length > 3) ? fields[3].trim() : null;
					KeyMapping k = km.map.get(key.key);
					k.unshiftedOutput = keysymOutput(u);
					k.shiftedOutput = keysymOutput(s);
					k.altUnshiftedOutput = keysymOutput(au);
					k.altShiftedOutput = keysymOutput(as);
					k.capsLockMapping = CapsLockMapping.AUTO;
					k.altCapsLockMapping = CapsLockMapping.AUTO;
					k.unshiftedDeadKey = keysymDeadKey(u);
					k.shiftedDeadKey = keysymDeadKey(s);
					k.altUnshiftedDeadKey = keysymDeadKey(au);
					k.altShiftedDeadKey = keysymDeadKey(as);
				}
				continue;
			}
			Matcher includeMatch = INCLUDE_PATTERN.matcher(line);
			if (includeMatch.find()) {
				String what = includeMatch.group(1);
				if (what.startsWith("level3")) km.xkbAltGrKey = XkbAltGrKey.forInclude(what);
				if (what.startsWith("compose")) km.xkbComposeKey = XkbComposeKey.forInclude(what);
				continue;
			}
		}
	}
	
	private static final Pattern NAME_PATTERN = Pattern.compile("^name[^=]*=[^\"]*\"([^\"]*)\"[^;]*;");
	private static final Pattern KEY_PATTERN = Pattern.compile("^key[^<]*<([^>]*)>[^{]*\\{[^\\[]*\\[([^\\]]*)\\][^}]*\\}[^;]*;");
	private static final Pattern INCLUDE_PATTERN = Pattern.compile("^include[^\"]*\"([^\"]*)\"");
	
	private static int keysymOutput(String s) {
		if (s == null || s.startsWith("dead_")) return -1;
		return XkbKeySym.MAP.resolveKeySym(s);
	}
	
	private static DeadKeyTable keysymDeadKey(String s) {
		if (s == null || !s.startsWith("dead_")) return null;
		XkbDeadKey dead = XkbDeadKey.forKeySym(s);
		if (dead == null || dead == XkbDeadKey.none) return null;
		DeadKeyTable dkt = new DeadKeyTable();
		dkt.xkbDeadKey = dead;
		return dkt;
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
	
	private static class EvdevEntityResolver implements EntityResolver {
		@Override
		public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
			if (systemId.contains("xkb.dtd")) {
				return new InputSource(XkbReader.class.getResourceAsStream("xkb.dtd"));
			} else {
				return null;
			}
		}
	}
	
	private static class EvdevErrorHandler implements ErrorHandler {
		private final String name;
		public EvdevErrorHandler(String name) {
			this.name = name;
		}
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
	}
}
