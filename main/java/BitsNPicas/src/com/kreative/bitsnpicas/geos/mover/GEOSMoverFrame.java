package com.kreative.bitsnpicas.geos.mover;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.swing.JFrame;
import com.kreative.bitsnpicas.geos.GEOSFontFile;

public class GEOSMoverFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private final SaveManager sm;
	private final GEOSMoverPanel panel;
	private final GEOSMoverMenuBar mb;
	
	public GEOSMoverFrame(File file, GEOSFontFile gff) {
		super(gff.getFontName());
		this.sm = new SaveManager(this, file, gff);
		this.panel = new GEOSMoverPanel(this, gff, sm);
		this.mb = new GEOSMoverMenuBar(this, sm, panel.getTable());
		
		setJMenuBar(mb);
		setContentPane(panel);
		setSize(600, 400);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(sm);
	}
	
	public static GEOSMoverFrame forNewFile() {
		return new GEOSMoverFrame(null, new GEOSFontFile());
	}
	
	public static GEOSMoverFrame forFile(File file) throws IOException {
		DataInputStream in =
			new DataInputStream(
				new FileInputStream(file));
		GEOSFontFile gff = new GEOSFontFile();
		gff.read(in);
		in.close();
		if (gff.isValid()) {
			return new GEOSMoverFrame(file, gff);
		} else {
			return null;
		}
	}
}
