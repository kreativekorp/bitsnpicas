package com.kreative.bitsnpicas.edit;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Toolkit;

public enum BitmapTool {
	PENCIL("PencilTool.png", SwingUtils.CURSOR_CROSSHAIR),
	ERASER("EraserTool.png", SwingUtils.CURSOR_CROSSHAIR),
	EYEDROPPER("EyedropperTool.png", SwingUtils.CURSOR_CROSSHAIR),
	LINE("LineTool.png", SwingUtils.CURSOR_CROSSHAIR),
	RECTANGLE("RectangleTool.png", SwingUtils.CURSOR_CROSSHAIR),
	FILLED_RECT("FilledRectTool.png", SwingUtils.CURSOR_CROSSHAIR),
	INVERT("InvertTool.png", SwingUtils.CURSOR_CROSSHAIR),
	MOVE("MoveTool.png", SwingUtils.CURSOR_MOVE),
	GRABBER("GrabberTool.png", SwingUtils.CURSOR_HAND_OPEN);
	
	public final Image icon;
	public final Cursor cursor;
	
	private BitmapTool(String iconName, Cursor cursor) {
		Toolkit tk = Toolkit.getDefaultToolkit();
		this.icon = tk.createImage(BitmapTool.class.getResource(iconName));
		this.cursor = cursor;
	}
}
