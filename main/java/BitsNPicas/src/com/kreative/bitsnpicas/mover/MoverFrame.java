package com.kreative.bitsnpicas.mover;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.swing.JFrame;
import com.kreative.bitsnpicas.MacUtility;
import com.kreative.rsrc.MacResourceArray;

public class MoverFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private final SaveManager sm;
	private final MoverPanel panel;
	private final MoverMenuBar mb;
	
	public MoverFrame(File fork, MacResourceArray rp, MoverFile mf) {
		File file = (fork == null) ? null : MacUtility.getDataFork(fork);
		this.sm = new SaveManager(this, file, fork, rp);
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
		MacResourceArray rp = new MacResourceArray();
		return new MoverFrame(null, rp, new MoverFile(rp));
	}
	
	public static MoverFrame forFile(File file) throws IOException {
		FileInputStream in = new FileInputStream(file);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int read; byte[] buf = new byte[65536];
		while ((read = in.read(buf)) > 0) out.write(buf, 0, read);
		out.flush(); out.close(); in.close();
		MacResourceArray rp = new MacResourceArray(out.toByteArray());
		return new MoverFrame(file, rp, new MoverFile(rp));
	}
}
