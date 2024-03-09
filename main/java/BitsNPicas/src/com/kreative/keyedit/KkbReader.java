package com.kreative.keyedit;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
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

public class KkbReader {
	public static KeyboardMapping read(File file) throws IOException {
		FileInputStream in = new FileInputStream(file);
		KeyboardMapping km = parse(file.getName(), in);
		in.close();
		return km;
	}
	
	public static KeyboardMapping parse(String name, InputStream in) throws IOException {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(true); // make sure the XML is valid
			factory.setExpandEntityReferences(false); // don't allow custom entities
			DocumentBuilder builder = factory.newDocumentBuilder();
			builder.setEntityResolver(new KkbEntityResolver());
			builder.setErrorHandler(new KkbErrorHandler(name));
			Document document = builder.parse(new InputSource(in));
			return parseDocument(document);
		} catch (ParserConfigurationException pce) {
			throw new IOException(pce);
		} catch (SAXException saxe) {
			throw new IOException(saxe);
		}
	}
	
	private static KeyboardMapping parseDocument(Node node) throws IOException {
		String type = node.getNodeName();
		if (type.equalsIgnoreCase("#document")) {
			for (Node child : getChildren(node)) {
				String ctype = child.getNodeName();
				if (ctype.equalsIgnoreCase("keyboardMapping")) {
					if (child.hasAttributes() || child.hasChildNodes()) {
						return parseKeyboardMapping(child);
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
	
	private static KeyboardMapping parseKeyboardMapping(Node node) throws IOException {
		String type = node.getNodeName();
		if (type.equalsIgnoreCase("keyboardMapping")) {
			KeyboardMapping km = new KeyboardMapping();
			for (Node child : getChildren(node)) {
				String ctype = child.getNodeName();
				NamedNodeMap cattr = child.getAttributes();
				if (ctype.equalsIgnoreCase("name")) {
					km.name = textContent(child);
				} else if (ctype.equalsIgnoreCase("winIdentifier")) {
					km.winIdentifier = textContent(child);
				} else if (ctype.equalsIgnoreCase("winCopyright")) {
					km.winCopyright = textContent(child);
				} else if (ctype.equalsIgnoreCase("winCompany")) {
					km.winCompany = textContent(child);
				} else if (ctype.equalsIgnoreCase("winLocale")) {
					km.winLocale = WinLocale.forTag(parseString(cattr, "tag"), km.winLocale);
				} else if (ctype.equalsIgnoreCase("winAltGrEnable")) {
					km.winAltGrEnable = parseBoolean(cattr, "altgr", "true", "false", km.winAltGrEnable);
				} else if (ctype.equalsIgnoreCase("winShiftLock")) {
					km.winShiftLock = parseBoolean(cattr, "shiftlock", "true", "false", km.winShiftLock);
				} else if (ctype.equalsIgnoreCase("winLrmRlm")) {
					km.winLrmRlm = parseBoolean(cattr, "lrmrlm", "true", "false", km.winLrmRlm);
				} else if (ctype.equalsIgnoreCase("macGroupNumber")) {
					km.macGroupNumber = parseInt(cattr, "group", km.macGroupNumber);
				} else if (ctype.equalsIgnoreCase("macIdNumber")) {
					km.macIdNumber = parseInt(cattr, "id", km.macIdNumber);
				} else if (ctype.equalsIgnoreCase("xkbPath")) {
					km.xkbPath = textContent(child);
				} else if (ctype.equalsIgnoreCase("xkbLabel")) {
					km.xkbLabel = textContent(child);
				} else if (ctype.equalsIgnoreCase("xkbComment")) {
					km.xkbComment = stripCommonLeadingWhitespace(textContent(child));
				} else if (ctype.equalsIgnoreCase("xkbUseKeySym")) {
					km.xkbUseKeySym = parseBoolean(cattr, "for", "unicode", "ascii", km.xkbUseKeySym);
				} else if (ctype.equalsIgnoreCase("xkbAltGrKey")) {
					km.xkbAltGrKey = XkbAltGrKey.forInclude(parseString(cattr, "include"));
				} else if (ctype.equalsIgnoreCase("xkbComposeKey")) {
					km.xkbComposeKey = XkbComposeKey.forInclude(parseString(cattr, "include"));
				} else if (ctype.equalsIgnoreCase("keymanIdentifier")) {
					km.keymanIdentifier = textContent(child);
				} else if (ctype.equalsIgnoreCase("keymanName")) {
					km.keymanName = textContent(child);
				} else if (ctype.equalsIgnoreCase("keymanCopyright")) {
					km.keymanCopyright = textContent(child);
				} else if (ctype.equalsIgnoreCase("keymanMessage")) {
					km.keymanMessage = textContent(child);
				} else if (ctype.equalsIgnoreCase("keymanWebHelpText")) {
					km.keymanWebHelpText = textContent(child);
				} else if (ctype.equalsIgnoreCase("keymanVersion")) {
					km.keymanVersion = textContent(child);
				} else if (ctype.equalsIgnoreCase("keymanComments")) {
					km.keymanComments = stripCommonLeadingWhitespace(textContent(child));
				} else if (ctype.equalsIgnoreCase("keymanAuthor")) {
					km.keymanAuthor = textContent(child);
				} else if (ctype.equalsIgnoreCase("keymanEmailAddress")) {
					km.keymanEmailAddress = textContent(child);
				} else if (ctype.equalsIgnoreCase("keymanWebSite")) {
					km.keymanWebSite = textContent(child);
				} else if (ctype.equalsIgnoreCase("keymanRightToLeft")) {
					km.keymanRightToLeft = parseBoolean(cattr, "value", "true", "false", km.keymanRightToLeft);
				} else if (ctype.equalsIgnoreCase("keymanKey102")) {
					km.keymanKey102 = parseBoolean(cattr, "value", "true", "false", km.keymanKey102);
				} else if (ctype.equalsIgnoreCase("keymanDisplayUnderlying")) {
					km.keymanDisplayUnderlying = parseBoolean(cattr, "value", "true", "false", km.keymanDisplayUnderlying);
				} else if (ctype.equalsIgnoreCase("keymanUseAltGr")) {
					km.keymanUseAltGr = parseBoolean(cattr, "value", "true", "false", km.keymanUseAltGr);
				} else if (ctype.equalsIgnoreCase("keymanTargets")) {
					for (KeyManTarget t : KeyManTarget.values()) {
						parseBoolean(cattr, t.toString(), "true", "false", t, km.keymanTargets);
					}
				} else if (ctype.equalsIgnoreCase("keymanPlatforms")) {
					for (KeyManPlatform p : KeyManPlatform.values()) {
						parseBoolean(cattr, p.toString(), "true", "false", p, km.keymanPlatforms);
					}
				} else if (ctype.equalsIgnoreCase("keymanLanguages")) {
					for (Node gchild : getChildren(child)) {
						String gctype = gchild.getNodeName();
						if (gctype.equalsIgnoreCase("keymanLanguage")) {
							NamedNodeMap gcattr = gchild.getAttributes();
							String tag = parseString(gcattr, "tag");
							if (tag != null && tag.length() > 0) {
								String name = parseString(gcattr, "name");
								if (name == null || name.length() == 0) name = tag;
								km.keymanLanguages.put(tag, name);
							}
						} else {
							throw new IOException("Unknown element: " + gctype);
						}
					}
				} else if (ctype.equalsIgnoreCase("keymanAttachments")) {
					for (Node gchild : getChildren(child)) {
						String gctype = gchild.getNodeName();
						if (gctype.equalsIgnoreCase("attachment")) {
							NamedNodeMap gcattr = gchild.getAttributes();
							String name = parseString(gcattr, "name");
							String data = textContent(gchild);
							if (name != null && data != null) {
								try {
									ByteArrayOutputStream ba = new ByteArrayOutputStream();
									Base64InputStream b64 = new Base64InputStream(data);
									byte[] buf = new byte[65536]; int n;
									while ((n = b64.read(buf)) > 0) ba.write(buf, 0, n);
									b64.close();
									ba.close();
									km.keymanAttachments.put(name, ba.toByteArray());
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						} else {
							throw new IOException("Unknown element: " + gctype);
						}
					}
				} else if (ctype.equalsIgnoreCase("keymanCpLabels")) {
					for (Node gchild : getChildren(child)) {
						String gctype = gchild.getNodeName();
						if (gctype.equalsIgnoreCase("cpLabel")) {
							NamedNodeMap gcattr = gchild.getAttributes();
							Integer cp = parseHex(gcattr, "cp", null);
							String label = parseString(gcattr, "label");
							if (cp != null && label != null) {
								km.keymanCpLabels.put(cp, label);
							}
						} else {
							throw new IOException("Unknown element: " + gctype);
						}
					}
				} else if (ctype.equalsIgnoreCase("keymanFont")) {
					km.keymanFontFamily = parseString(cattr, "family");
				} else if (ctype.equalsIgnoreCase("keymanDescription")) {
					km.keymanDescription = stripCommonLeadingWhitespace(textContent(child));
				} else if (ctype.equalsIgnoreCase("keymanLicense")) {
					km.keymanLicenseType = parseString(cattr, "type");
					km.keymanLicenseText = stripCommonLeadingWhitespace(textContent(child));
				} else if (ctype.equalsIgnoreCase("keymanReadme")) {
					km.keymanReadme = stripCommonLeadingWhitespace(textContent(child));
				} else if (ctype.equalsIgnoreCase("keymanHistory")) {
					km.keymanHistory = stripCommonLeadingWhitespace(textContent(child));
				} else if (ctype.equalsIgnoreCase("icon")) {
					String data = textContent(child);
					if (data != null) {
						try {
							Base64InputStream b64 = new Base64InputStream(data);
							km.icon = ImageIO.read(b64);
							b64.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				} else if (ctype.equalsIgnoreCase("macIconVersion")) {
					km.macIconVersion = parseHex(cattr, "version", null);
				} else if (ctype.equalsIgnoreCase("keyMappings")) {
					for (Node gchild : getChildren(child)) {
						parseKeyMapping(gchild, km);
					}
				} else if (ctype.equalsIgnoreCase("macActionIds")) {
					for (Node gchild : getChildren(child)) {
						String gctype = gchild.getNodeName();
						if (gctype.equalsIgnoreCase("macActionId")) {
							NamedNodeMap gcattr = gchild.getAttributes();
							Integer input = parseHex(gcattr, "input", null);
							String action = parseString(gcattr, "action");
							if (input != null && action != null) {
								km.macActionIds.put(input, action);
							}
						} else {
							throw new IOException("Unknown element: " + gctype);
						}
					}
				} else if (ctype.equalsIgnoreCase("html")) {
					parseHTMLConfig(child, km);
				} else {
					throw new IOException("Unknown element: " + ctype);
				}
			}
			return km;
		} else {
			throw new IOException("Unknown element: " + type);
		}
	}
	
	private static void parseKeyMapping(Node node, KeyboardMapping km) throws IOException {
		String type = node.getNodeName();
		if (type.equalsIgnoreCase("keyMapping")) {
			NamedNodeMap attr = node.getAttributes();
			Key key = Key.forName(parseString(attr, "key"));
			KeyMapping map = km.map.get(key);
			if (map == null) return;
			for (Node child : getChildren(node)) {
				String ctype = child.getNodeName();
				NamedNodeMap cattr = child.getAttributes();
				if (ctype.equalsIgnoreCase("unshifted")) {
					map.unshiftedOutput = parseHex(cattr, "output", -1);
					map.unshiftedDeadKey = null;
					map.unshiftedLongPressOutput = null;
					for (Node gchild : getChildren(child)) {
						String gctype = gchild.getNodeName();
						if (gctype.equalsIgnoreCase("deadKey")) {
							map.unshiftedDeadKey = parseDeadKey(gchild);
						} else if (gctype.equalsIgnoreCase("longPressOutput")) {
							map.unshiftedLongPressOutput = parseLongPressOutput(gchild);
						} else {
							throw new IOException("Unknown element: " + gctype);
						}
					}
				} else if (ctype.equalsIgnoreCase("shifted")) {
					map.shiftedOutput = parseHex(cattr, "output", -1);
					map.shiftedDeadKey = null;
					map.shiftedLongPressOutput = null;
					for (Node gchild : getChildren(child)) {
						String gctype = gchild.getNodeName();
						if (gctype.equalsIgnoreCase("deadKey")) {
							map.shiftedDeadKey = parseDeadKey(gchild);
						} else if (gctype.equalsIgnoreCase("longPressOutput")) {
							map.shiftedLongPressOutput = parseLongPressOutput(gchild);
						} else {
							throw new IOException("Unknown element: " + gctype);
						}
					}
				} else if (ctype.equalsIgnoreCase("capsLock")) {
					map.capsLockMapping = parseCapsLock(cattr, "mapsTo", "unshifted", "shifted");
				} else if (ctype.equalsIgnoreCase("altUnshifted")) {
					map.altUnshiftedOutput = parseHex(cattr, "output", -1);
					map.altUnshiftedDeadKey = null;
					map.altUnshiftedLongPressOutput = null;
					for (Node gchild : getChildren(child)) {
						String gctype = gchild.getNodeName();
						if (gctype.equalsIgnoreCase("deadKey")) {
							map.altUnshiftedDeadKey = parseDeadKey(gchild);
						} else if (gctype.equalsIgnoreCase("longPressOutput")) {
							map.altUnshiftedLongPressOutput = parseLongPressOutput(gchild);
						} else {
							throw new IOException("Unknown element: " + gctype);
						}
					}
				} else if (ctype.equalsIgnoreCase("altShifted")) {
					map.altShiftedOutput = parseHex(cattr, "output", -1);
					map.altShiftedDeadKey = null;
					map.altShiftedLongPressOutput = null;
					for (Node gchild : getChildren(child)) {
						String gctype = gchild.getNodeName();
						if (gctype.equalsIgnoreCase("deadKey")) {
							map.altShiftedDeadKey = parseDeadKey(gchild);
						} else if (gctype.equalsIgnoreCase("longPressOutput")) {
							map.altShiftedLongPressOutput = parseLongPressOutput(gchild);
						} else {
							throw new IOException("Unknown element: " + gctype);
						}
					}
				} else if (ctype.equalsIgnoreCase("altCapsLock")) {
					map.altCapsLockMapping = parseCapsLock(cattr, "mapsTo", "altUnshifted", "altShifted");
				} else if (ctype.equalsIgnoreCase("ctrl")) {
					map.ctrlOutput = parseHex(cattr, "output", -1);
					map.ctrlDeadKey = null;
					for (Node gchild : getChildren(child)) {
						String gctype = gchild.getNodeName();
						if (gctype.equalsIgnoreCase("deadKey")) {
							map.ctrlDeadKey = parseDeadKey(gchild);
						} else {
							throw new IOException("Unknown element: " + gctype);
						}
					}
				} else if (ctype.equalsIgnoreCase("command")) {
					map.commandOutput = parseHex(cattr, "output", -1);
					map.commandDeadKey = null;
					for (Node gchild : getChildren(child)) {
						String gctype = gchild.getNodeName();
						if (gctype.equalsIgnoreCase("deadKey")) {
							map.commandDeadKey = parseDeadKey(gchild);
						} else {
							throw new IOException("Unknown element: " + gctype);
						}
					}
				} else {
					throw new IOException("Unknown element: " + ctype);
				}
			}
		} else {
			throw new IOException("Unknown element: " + type);
		}
	}
	
	private static DeadKeyTable parseDeadKey(Node node) throws IOException {
		String type = node.getNodeName();
		if (type.equalsIgnoreCase("deadKey")) {
			DeadKeyTable dkt = new DeadKeyTable();
			for (Node child : getChildren(node)) {
				String ctype = child.getNodeName();
				NamedNodeMap cattr = child.getAttributes();
				if (ctype.equalsIgnoreCase("winTerminator")) {
					dkt.winTerminator = parseHex(cattr, "output", -1);
				} else if (ctype.equalsIgnoreCase("macTerminator")) {
					dkt.macTerminator = parseHex(cattr, "output", -1);
				} else if (ctype.equalsIgnoreCase("macStateId")) {
					dkt.macStateId = parseString(cattr, "state");
				} else if (ctype.equalsIgnoreCase("xkbOutput")) {
					dkt.xkbOutput = parseHex(cattr, "output", -1);
				} else if (ctype.equalsIgnoreCase("xkbDeadKey")) {
					dkt.xkbDeadKey = XkbDeadKey.forKeySym(parseString(cattr, "keysym"));
				} else if (ctype.equalsIgnoreCase("deadKeyMap")) {
					for (Node gchild : getChildren(child)) {
						String gctype = gchild.getNodeName();
						if (gctype.equalsIgnoreCase("deadKeyEntry")) {
							NamedNodeMap gcattr = gchild.getAttributes();
							Integer input = parseHex(gcattr, "input", null);
							Integer output = parseHex(gcattr, "output", null);
							if (input != null && output != null) {
								dkt.keyMap.put(input, output);
							}
						} else {
							throw new IOException("Unknown element: " + gctype);
						}
					}
				} else {
					throw new IOException("Unknown element: " + ctype);
				}
			}
			return dkt;
		} else {
			throw new IOException("Unknown element: " + type);
		}
	}
	
	private static int[] parseLongPressOutput(Node node) throws IOException {
		String type = node.getNodeName();
		if (type.equalsIgnoreCase("longPressOutput")) {
			List<Integer> lpo = new ArrayList<Integer>();
			for (Node child : getChildren(node)) {
				String ctype = child.getNodeName();
				if (ctype.equalsIgnoreCase("longPressEntry")) {
					NamedNodeMap cattr = child.getAttributes();
					Integer output = parseHex(cattr, "output", null);
					if (output != null) lpo.add(output);
				} else {
					throw new IOException("Unknown element: " + ctype);
				}
			}
			int n = lpo.size();
			int[] output = new int[n];
			for (int i = 0; i < n; i++) output[i] = lpo.get(i);
			return output;
		} else {
			throw new IOException("Unknown element: " + type);
		}
	}
	
	private static void parseHTMLConfig(Node node, KeyboardMapping km) throws IOException {
		String type = node.getNodeName();
		if (type.equalsIgnoreCase("html")) {
			for (Node child : getChildren(node)) {
				String ctype = child.getNodeName();
				NamedNodeMap cattr = child.getAttributes();
				if (ctype.equalsIgnoreCase("title")) {
					km.htmlTitle = textContent(child);
				} else if (ctype.equalsIgnoreCase("style")) {
					km.htmlStyle = stripCommonLeadingWhitespace(textContent(child));
				} else if (ctype.equalsIgnoreCase("h1")) {
					km.htmlH1 = textContent(child);
				} else if (ctype.equalsIgnoreCase("h2")) {
					km.htmlH2 = textContent(child);
				} else if (ctype.equalsIgnoreCase("body1")) {
					km.htmlBody1 = stripCommonLeadingWhitespace(textContent(child));
				} else if (ctype.equalsIgnoreCase("body2")) {
					km.htmlBody2 = stripCommonLeadingWhitespace(textContent(child));
				} else if (ctype.equalsIgnoreCase("body3")) {
					km.htmlBody3 = stripCommonLeadingWhitespace(textContent(child));
				} else if (ctype.equalsIgnoreCase("body4")) {
					km.htmlBody4 = stripCommonLeadingWhitespace(textContent(child));
				} else if (ctype.equalsIgnoreCase("install")) {
					km.htmlInstall = stripCommonLeadingWhitespace(textContent(child));
				} else if (ctype.equalsIgnoreCase("square")) {
					km.htmlSquareChars = parseRanges(parseString(cattr, "chars"));
				} else if (ctype.equalsIgnoreCase("outline")) {
					km.htmlOutlineChars = parseRanges(parseString(cattr, "chars"));
				} else if (ctype.equalsIgnoreCase("cpClasses")) {
					for (Node gchild : getChildren(child)) {
						String gctype = gchild.getNodeName();
						Map<String,BitSet> classes;
						if (gctype.equalsIgnoreCase("td")) {
							classes = km.htmlTdClasses;
						} else if (gctype.equalsIgnoreCase("span")) {
							classes = km.htmlSpanClasses;
						} else {
							throw new IOException("Unknown element: " + gctype);
						}
						NamedNodeMap gcattr = gchild.getAttributes();
						String className = parseString(gcattr, "class");
						BitSet chars = parseRanges(parseString(gcattr, "chars"));
						if (className != null && chars != null) {
							BitSet bs = classes.get(className);
							if (bs != null) bs.or(chars);
							else classes.put(className, chars);
						}
					}
				} else if (ctype.equalsIgnoreCase("cpLabels")) {
					for (Node gchild : getChildren(child)) {
						String gctype = gchild.getNodeName();
						if (gctype.equalsIgnoreCase("cpLabel")) {
							NamedNodeMap gcattr = gchild.getAttributes();
							Integer cp = parseHex(gcattr, "cp", null);
							String label = parseString(gcattr, "label");
							if (cp != null && label != null) {
								km.htmlCpLabels.put(cp, label);
							}
						} else {
							throw new IOException("Unknown element: " + gctype);
						}
					}
				} else {
					throw new IOException("Unknown element: " + ctype);
				}
			}
		} else {
			throw new IOException("Unknown element: " + type);
		}
	}
	
	public static BitSet parseRanges(String s) {
		if (s == null || (s = s.trim()).length() == 0) return null;
		BitSet bs = new BitSet();
		for (String r : s.split(",")) {
			try {
				if (r.contains("-")) {
					String[] p = r.split("-", 2);
					int i = Integer.parseInt(p[0].trim(), 16);
					int j = Integer.parseInt(p[1].trim(), 16);
					bs.set(i, j + 1);
				} else {
					int i = Integer.parseInt(r.trim(), 16);
					bs.set(i);
				}
			} catch (NumberFormatException nfe) {
				// ignored
			}
		}
		return bs;
	}
	
	private static String stripCommonLeadingWhitespace(String s) {
		if (s == null) return null;
		String[] lines = s.split("\r\n|\r|\n");
		if (lines.length < 2) return s;
		String prefix = leadingWhitespace(lines[1]);
		for (int i = 2; i < lines.length; i++) {
			String newPrefix = leadingWhitespace(lines[i]);
			prefix = commonPrefix(prefix, newPrefix);
		}
		StringBuffer sb = new StringBuffer(lines[0]);
		for (int i = 1; i < lines.length; i++) {
			sb.append("\n");
			sb.append(lines[i].substring(prefix.length()));
		}
		return sb.toString();
	}
	
	private static final Pattern LEADING_WHITESPACE = Pattern.compile("^\\s+");
	private static String leadingWhitespace(String s) {
		Matcher m = LEADING_WHITESPACE.matcher(s);
		if (m.find()) return m.group();
		return "";
	}
	
	private static String commonPrefix(String a, String b) {
		if (a.startsWith(b)) return b;
		if (b.startsWith(a)) return a;
		return "";
	}
	
	private static String textContent(Node node) {
		if (node == null) return null;
		String text = node.getTextContent();
		if (text == null) return null;
		return text.trim();
	}
	
	private static CapsLockMapping parseCapsLock(NamedNodeMap attr, String key, String unshifted, String shifted) {
		if (attr == null) return CapsLockMapping.AUTO;
		Node node = attr.getNamedItem(key);
		if (node == null) return CapsLockMapping.AUTO;
		String text = node.getTextContent();
		if (text == null) return CapsLockMapping.AUTO;
		text = text.trim();
		if (text.equalsIgnoreCase(unshifted)) return CapsLockMapping.UNSHIFTED;
		if (text.equalsIgnoreCase(shifted)) return CapsLockMapping.SHIFTED;
		return CapsLockMapping.AUTO;
	}
	
	private static <E extends Enum<E>> void parseBoolean(NamedNodeMap attr, String key, String trueValue, String falseValue, E value, EnumSet<E> set) {
		if (attr == null) return;
		Node node = attr.getNamedItem(key);
		if (node == null) return;
		String text = node.getTextContent();
		if (text == null) return;
		text = text.trim();
		if (text.equalsIgnoreCase(trueValue)) set.add(value);
		if (text.equalsIgnoreCase(falseValue)) set.remove(value);
	}
	
	private static boolean parseBoolean(NamedNodeMap attr, String key, String trueValue, String falseValue, boolean def) {
		if (attr == null) return def;
		Node node = attr.getNamedItem(key);
		if (node == null) return def;
		String text = node.getTextContent();
		if (text == null) return def;
		text = text.trim();
		if (text.equalsIgnoreCase(trueValue)) return true;
		if (text.equalsIgnoreCase(falseValue)) return false;
		return def;
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
	
	private static Integer parseHex(NamedNodeMap attr, String key, Integer def) {
		if (attr == null) return def;
		Node node = attr.getNamedItem(key);
		if (node == null) return def;
		String text = node.getTextContent();
		if (text == null) return def;
		try { return Integer.parseInt(text.trim(), 16); }
		catch (NumberFormatException nfe) { return def; }
	}
	
	private static String parseString(NamedNodeMap attr, String key) {
		if (attr == null) return null;
		Node node = attr.getNamedItem(key);
		if (node == null) return null;
		String text = node.getTextContent();
		if (text == null) return null;
		return text.trim();
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
	
	private static class KkbEntityResolver implements EntityResolver {
		@Override
		public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
			if (publicId.contains("KreativeKeyboard") || systemId.contains("kkbx.dtd")) {
				return new InputSource(KkbReader.class.getResourceAsStream("kkbx.dtd"));
			} else {
				return null;
			}
		}
	}
	
	private static class KkbErrorHandler implements ErrorHandler {
		private final String name;
		public KkbErrorHandler(String name) {
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
