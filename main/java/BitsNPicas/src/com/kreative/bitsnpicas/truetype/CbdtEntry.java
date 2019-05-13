package com.kreative.bitsnpicas.truetype;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

public abstract class CbdtEntry extends EbdtEntry {
	public byte[] imageData;
	
	public BufferedImage getImage() throws IOException {
		ByteArrayInputStream in = new ByteArrayInputStream(imageData);
		BufferedImage image = ImageIO.read(in);
		in.close();
		return image;
	}
	
	public void setImage(RenderedImage image) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageIO.write(image, "png", out);
		out.flush(); out.close();
		imageData = out.toByteArray();
	}
}
