package com.kreative.bitsnpicas.edit.glmlicon;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import javax.imageio.ImageIO;

public class GLMLResources {
	public static Image getImage(String group, String name) {
		if (group != null && name != null) {
			if (group.equals("subtable")) {
				try {
					int v = Integer.parseInt(name.substring(name.length() - 2), 16);
					if (subtableImages[v] != null) return subtableImages[v];
				} catch (NumberFormatException nfe) {}
			}
			Map<String,Image> submap = mappedImages.get(group);
			if (submap != null) {
				Image image = submap.get(name);
				if (image != null) return image;
			}
		}
		return getImage("unknown.png");
	}
	
	private static final Map<String,Image> imageResources = new HashMap<String,Image>();
	private static Image getImage(String name) {
		Image image = imageResources.get(name);
		if (image != null) return image;
		URL res = GLMLResources.class.getResource(name);
		if (res == null) return null;
		image = Toolkit.getDefaultToolkit().createImage(res);
		if (image == null) return null;
		imageResources.put(name, image);
		return image;
	}
	
	private static final Map<String,Map<String,Image>> mappedImages = new HashMap<String,Map<String,Image>>();
	static {
		Scanner index = new Scanner(GLMLResources.class.getResourceAsStream("index.txt"));
		while (index.hasNextLine()) {
			String[] line = index.nextLine().trim().split("\\s+", 3);
			if (line.length != 3) continue;
			Image image = getImage(line[0]);
			if (image == null) continue;
			Map<String,Image> submap = mappedImages.get(line[1]);
			if (submap == null) mappedImages.put(line[1], (submap = new HashMap<String,Image>()));
			submap.put(line[2], image);
		}
		index.close();
	}
	
	private static final BufferedImage[] subtableImages = new BufferedImage[256];
	static {
		try {
			BufferedImage ss = ImageIO.read(GLMLResources.class.getResource("subtable.png"));
			int cw = ss.getWidth() / 32;
			int ch = ss.getHeight();
			int[] rgb = new int[cw * ch];
			for (int v0 = 0; v0 < 16; v0++) {
				for (int v1 = 0; v1 < 16; v1++) {
					BufferedImage ci = new BufferedImage(cw*2, ch, BufferedImage.TYPE_INT_ARGB);
					ss.getRGB((v0*2) * cw, 0, cw, ch, rgb, 0, cw);
					ci.setRGB(0, 0, cw, ch, rgb, 0, cw);
					ss.getRGB((v1*2+1) * cw, 0, cw, ch, rgb, 0, cw);
					ci.setRGB(cw, 0, cw, ch, rgb, 0, cw);
					subtableImages[(v0 << 4) | v1] = ci;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
