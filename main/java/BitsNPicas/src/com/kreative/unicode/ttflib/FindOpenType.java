package com.kreative.unicode.ttflib;

import java.awt.Font;
import java.awt.font.OpenType;
import java.lang.reflect.Method;

public class FindOpenType {
	static {
		try {
			Method getModule = Class.class.getMethod("getModule");
			Object javaDesktop = getModule.invoke(Font.class);
			Object allUnnamed = getModule.invoke(FindOpenType.class);
			Class<?> module = Class.forName("java.lang.Module");
			Method addOpens = module.getMethod("addOpens", String.class, module);
			addOpens.invoke(javaDesktop, "java.awt", allUnnamed);
			addOpens.invoke(javaDesktop, "sun.font", allUnnamed);
		} catch (Exception e) {
			// Be gay. Do crimes.
		}
	}
	
	public static OpenType forTtf(TtfBase ttf) {
		if (ttf == null) return null;
		return new TtfOpenType(ttf);
	}
	
	public static OpenType forFont(Font font) {
		if (font == null) return null;
		if (font instanceof OpenType) return (OpenType)font;
		OpenType ot;
		if ((ot = forFont2D(Font_getFont2D(font))) != null) return ot;
		if ((ot = forFont2D(FontUtilities_getFont2D(font))) != null) return ot;
		if ((ot = forFont2D(FontAccess_getFont2D(font))) != null) return ot;
		if ((ot = forFont2D(FontManager_findFont2D(font))) != null) return ot;
		return null;
	}
	
	private static Object Font_getFont2D(final Font font) {
		try {
			Method m = Font.class.getDeclaredMethod("getFont2D");
			if (!m.isAccessible()) m.setAccessible(true);
			return m.invoke(font);
		} catch (Exception e) {
			return null;
		}
	}
	
	private static Object FontUtilities_getFont2D(Font font) {
		try {
			Class<?> cls = Class.forName("sun.font.FontUtilities");
			Method m = cls.getDeclaredMethod("getFont2D", Font.class);
			if (!m.isAccessible()) m.setAccessible(true);
			return m.invoke(null, font);
		} catch (Exception e) {
			return null;
		}
	}
	
	private static Object FontAccess_getFont2D(Font font) {
		try {
			Class<?> cls = Class.forName("sun.font.FontAccess");
			Method m = cls.getDeclaredMethod("getFont2D", Font.class);
			if (!m.isAccessible()) m.setAccessible(true);
			return m.invoke(FontAccess_getFontAccess(), font);
		} catch (Exception e) {
			return null;
		}
	}
	
	private static Object FontAccess_getFontAccess() {
		try {
			Class<?> cls = Class.forName("sun.font.FontAccess");
			Method m = cls.getDeclaredMethod("getFontAccess");
			if (!m.isAccessible()) m.setAccessible(true);
			return m.invoke(null);
		} catch (Exception e) {
			return null;
		}
	}
	
	private static Object FontManager_findFont2D(Font font) {
		try {
			Class<?> cls = Class.forName("sun.font.FontManager");
			Method m = cls.getDeclaredMethod("findFont2D", String.class, int.class, int.class);
			if (!m.isAccessible()) m.setAccessible(true);
			return m.invoke(FontManagerFactory_getInstance(), font.getName(), font.getStyle(), 0);
		} catch (Exception e) {
			return null;
		}
	}
	
	private static Object FontManagerFactory_getInstance() {
		try {
			Class<?> cls = Class.forName("sun.font.FontManagerFactory");
			Method m = cls.getDeclaredMethod("getInstance");
			if (!m.isAccessible()) m.setAccessible(true);
			return m.invoke(null);
		} catch (Exception e) {
			return null;
		}
	}
	
	private static OpenType forFont2D(Object obj) {
		if (obj == null) return null;
		if (obj instanceof OpenType) return (OpenType)obj;
		OpenType ot;
		if ((ot = forRef("sun.font.Font2D", "getTableBytes", obj)) != null) return ot;
		if ((ot = forRef("sun.font.TrueTypeFont", "getTableBytes", obj)) != null) return ot;
		return null;
	}
	
	private static OpenType forRef(String className, String methodName, Object obj) {
		try {
			Class<?> cls = Class.forName(className);
			Method m = cls.getDeclaredMethod(methodName, int.class);
			if (!m.isAccessible()) m.setAccessible(true);
			return new RefOpenType(m, cls.cast(obj));
		} catch (Exception e) {
			return null;
		}
	}
}
