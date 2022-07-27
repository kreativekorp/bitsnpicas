package com.kreative.bitsnpicas.edit;

import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;

public class Resources {
	public static final Image ERASER_TOOL = getImage("EraserTool.png");
	public static final Image EYEDROPPER_TOOL = getImage("EyedropperTool.png");
	public static final Image FILLED_RECT_TOOL = getImage("FilledRectTool.png");
	public static final Image GRABBER_TOOL = getImage("GrabberTool.png");
	public static final Image INVERT_TOOL = getImage("InvertTool.png");
	public static final Image LINE_TOOL = getImage("LineTool.png");
	public static final Image MOVE_TOOL = getImage("MoveTool.png");
	public static final Image PENCIL_TOOL = getImage("PencilTool.png");
	public static final Image RECTANGLE_TOOL = getImage("RectangleTool.png");
	
	public static final Font HEX_FONT = getFont("Hex.ttf", 10f);
	public static final Font PSNAME_FONT = getFont("PsName.ttf", 10f);
	
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
