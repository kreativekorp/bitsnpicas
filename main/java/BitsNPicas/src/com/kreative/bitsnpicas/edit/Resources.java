package com.kreative.bitsnpicas.edit;

import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;

public class Resources {
	public static final Image ERASER_TOOL = getImage("EraserTool.png");
	public static final Image EYEDROPPER_TOOL = getImage("EyedropperTool.png");
	public static final Image FILLED_RECT_TOOL = getImage("FilledRectTool.png");
	public static final Image GRABBER_TOOL = getImage("GrabberTool.png");
	public static final Image INVERT_TOOL = getImage("InvertTool.png");
	public static final Image LINE_TOOL = getImage("LineTool.png");
	public static final Image MOVE_TOOL = getImage("MoveTool.png");
	public static final Image PENCIL_TOOL = getImage("PencilTool.png");
	public static final Image BRUSH_TOOL = getImage("BrushTool.png");
	public static final Image RECTANGLE_TOOL = getImage("RectangleTool.png");
	
	public static Font HEX_FONT = getFont("Hex.ttf", 10f);
	public static Font PSNAME_FONT = getFont("PsName.ttf", 10f);
	
	// GNU Unifont (https://unifoundry.com/unifont/) under SIL Open Font License 1.1 (OFL-1.1.txt)
	public static Font GNU_UNIFONT = getFont("GNU_Unifont.otf", 16f);
	
	public static ArrayList<Font> UNICODE_FONTS = new ArrayList<Font>() {{
		add(GNU_UNIFONT);
		// Fairfax HD (https://www.kreativekorp.com/software/fonts/fairfaxhd/) under SIL Open Font License 1.1 (OFL-1.1.txt)
		add(getFont("FairfaxHD.ttf", 16f));
	}};
	
	
	public static void reloadFonts(float size) {
		HEX_FONT = getFont("Hex.ttf", size);
		PSNAME_FONT = getFont("PsName.ttf", size);
		GNU_UNIFONT = getFont("GNU_Unifont.otf", size * 1.6f);
		UNICODE_FONTS.set(0, GNU_UNIFONT);
	}
	
	private static int[] codepointCache = new int[0x10FFFF + 1];
	static {
		Arrays.fill(codepointCache, -1);
	}

	public static Font getFontForCodepoint(Font fallback, int codepoint) {
		int cached = codepointCache[codepoint];
		if (cached != -1) {
			return UNICODE_FONTS.get(cached);
		}
		for(int i = 0; i < UNICODE_FONTS.size(); i ++) {
			Font f = UNICODE_FONTS.get(i);
			if (fontHasGlyph(f, codepoint)) {
				codepointCache[codepoint] = i;
				return f;
			}
		}
		return fallback;
	}
	
	private static final FontRenderContext FRC = new FontRenderContext(null, false, false);
	private static boolean fontHasGlyph(Font font, int codepoint) {
        try {
            String str = new String(Character.toChars(codepoint));
            GlyphVector gv = font.createGlyphVector(FRC, str);
            int glyphCode = gv.getGlyphCode(0);
            return glyphCode != 0 && glyphCode != -1;
        } catch (Exception e) {
            return false;
        }
	}
	
	private static Image getImage(String name) {
		return Toolkit.getDefaultToolkit().createImage(Resources.class.getResource(name));
	}
	 
	private static Font getFont(String name, float size) {
		try {
			return Font.createFont(
				Font.TRUETYPE_FONT,
				Resources.class.getResourceAsStream(name)
			).deriveFont(size);
		} catch (Exception e) {
			return null;
		}
	}
}
