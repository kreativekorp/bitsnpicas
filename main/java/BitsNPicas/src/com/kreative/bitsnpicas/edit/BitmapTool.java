package com.kreative.bitsnpicas.edit;

import java.awt.Cursor;
import java.awt.Image;

public enum BitmapTool {
	PENCIL(Resources.PENCIL_TOOL, SwingUtils.CURSOR_CROSSHAIR),
	ERASER(Resources.ERASER_TOOL, SwingUtils.CURSOR_CROSSHAIR),
	EYEDROPPER(Resources.EYEDROPPER_TOOL, SwingUtils.CURSOR_CROSSHAIR),
	LINE(Resources.LINE_TOOL, SwingUtils.CURSOR_CROSSHAIR),
	RECTANGLE(Resources.RECTANGLE_TOOL, SwingUtils.CURSOR_CROSSHAIR),
	FILLED_RECT(Resources.FILLED_RECT_TOOL, SwingUtils.CURSOR_CROSSHAIR),
	INVERT(Resources.INVERT_TOOL, SwingUtils.CURSOR_CROSSHAIR),
	MOVE(Resources.MOVE_TOOL, SwingUtils.CURSOR_MOVE),
	GRABBER(Resources.GRABBER_TOOL, SwingUtils.CURSOR_HAND_OPEN);
	
	public final Image icon;
	public final Cursor cursor;
	
	private BitmapTool(Image icon, Cursor cursor) {
		this.icon = icon;
		this.cursor = cursor;
	}
}
