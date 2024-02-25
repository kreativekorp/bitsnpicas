package com.kreative.bitsnpicas.mover;

import java.io.File;
import java.io.IOException;
import javax.swing.JFrame;
import com.kreative.bitsnpicas.MacUtility;
import com.kreative.unicode.ttflib.DfontFile;

public class MoverFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private final SaveManager sm;
	private final MoverPanel panel;
	private final MoverMenuBar mb;
	
	public MoverFrame(File fork, DfontFile rsrc, MoverFile mf) {
		File file = (fork == null) ? null : MacUtility.getDataFork(fork);
		this.sm = new SaveManager(this, file, fork, rsrc);
		this.panel = new MoverPanel(this, file, mf, sm);
		this.mb = new MoverMenuBar(this, sm, panel.getTable());
		
		setTitle((file == null) ? "Untitled Suitcase" : file.getName());
		setJMenuBar(mb);
		setContentPane(panel);
		setSize(600, 400);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(sm);
	}
	
	public static MoverFrame forNewFile() throws IOException {
		DfontFile rsrc = new DfontFile();
		return new MoverFrame(null, rsrc, new MoverFile(rsrc));
	}
	
	public static MoverFrame forFile(File file) throws IOException {
		DfontFile rsrc = new DfontFile(file);
		return new MoverFrame(file, rsrc, new MoverFile(rsrc));
	}
}
