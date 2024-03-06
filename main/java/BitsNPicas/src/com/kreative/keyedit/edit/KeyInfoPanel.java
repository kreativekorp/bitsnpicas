package com.kreative.keyedit.edit;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import com.kreative.keyedit.CapsLockMapping;
import com.kreative.keyedit.Key;
import com.kreative.keyedit.KeyMapping;

public class KeyInfoPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private final Key key;
	private final KeyMapping km;
	private final KeyEditController controller;
	private final OutputPanel uop;
	private final JButton uadd, uedit, udel, ulpo;
	private final OutputPanel sop;
	private final JButton sadd, sedit, sdel, slpo;
	private final JComboBox caps;
	private final OutputPanel auop;
	private final JButton auadd, auedit, audel, aulpo;
	private final OutputPanel asop;
	private final JButton asadd, asedit, asdel, aslpo;
	private final JComboBox acaps;
	private final OutputPanel ctrl;
	private final OutputPanel cmd;
	
	public KeyInfoPanel(Key key, KeyMapping km, KeyEditController controller) {
		this.key = key;
		this.km = km;
		this.controller = controller;
		this.uop = new OutputPanel(km.unshiftedOutput);
		this.uadd = new JButton("Add");
		this.uedit = new JButton("Edit");
		this.udel = new JButton("Delete");
		this.ulpo = new JButton("Edit Long Press Output");
		this.sop = new OutputPanel(km.shiftedOutput);
		this.sadd = new JButton("Add");
		this.sedit = new JButton("Edit");
		this.sdel = new JButton("Delete");
		this.slpo = new JButton("Edit Long Press Output");
		this.caps = new JComboBox(new String[] {"Auto", "Unshifted", "Shifted"});
		this.caps.setSelectedIndex(km.capsLockMapping.ordinal());
		this.auop = new OutputPanel(km.altUnshiftedOutput);
		this.auadd = new JButton("Add");
		this.auedit = new JButton("Edit");
		this.audel = new JButton("Delete");
		this.aulpo = new JButton("Edit Long Press Output");
		this.asop = new OutputPanel(km.altShiftedOutput);
		this.asadd = new JButton("Add");
		this.asedit = new JButton("Edit");
		this.asdel = new JButton("Delete");
		this.aslpo = new JButton("Edit Long Press Output");
		this.acaps = new JComboBox(new String[] {"Auto", "Alt Unshifted", "Alt Shifted"});
		this.acaps.setSelectedIndex(km.altCapsLockMapping.ordinal());
		this.ctrl = new OutputPanel(km.ctrlOutput);
		this.cmd = new OutputPanel(km.commandOutput);
		update();
		
		JPanel labels = verticalStack(
			l("Unshifted:", "Output without any modifier keys pressed."),
			l("Shifted:", "Output with Shift pressed."),
			l("Caps Lock:", "Output with Caps Lock pressed."),
			l("Alt Unshifted:", "AltGr on Windows and Linux. Option on Mac OS X."),
			l("Alt Shifted:", "AltGr + Shift on Windows and Linux. Option + Shift on Mac OS X."),
			l("Alt Caps Lock:", "AltGr + Caps Lock on Windows and Linux. Option + Caps Lock on Mac OS X."),
			l("Control:", "Control on Mac OS X. Should be a corresponding control character. Usually best left alone."),
			l("Command:", "Command on Mac OS X. Should be an unshifted ASCII character. Usually best left alone.")
		);
		JPanel fields = verticalStack(
			uop, sop, leftAlign(caps),
			auop, asop, leftAlign(acaps),
			ctrl, cmd
		);
		JPanel dklbls = verticalStack(
			"Dead Key:", "Dead Key:", "",
			"Dead Key:", "Dead Key:", "",
			"", ""
		);
		JPanel dkbtns = verticalStack(
			horizontalStack(uadd, uedit, udel),
			horizontalStack(sadd, sedit, sdel),
			new JPanel(),
			horizontalStack(auadd, auedit, audel),
			horizontalStack(asadd, asedit, asdel),
			new JPanel(),
			new JPanel(),
			new JPanel()
		);
		JPanel lpobtns = verticalStack(
			ulpo, slpo, new JPanel(),
			aulpo, aslpo, new JPanel(),
			new JPanel(), new JPanel()
		);
		JPanel left = leftSxS(labels, fields, 8);
		JPanel right1 = leftSxS(dklbls, dkbtns, 8);
		JPanel right = rightSxS(right1, lpobtns, 8);
		JPanel panel = topAlign(rightSxS(left, right, 8));
		
		setLayout(new GridLayout(1,1,0,0));
		add(panel);
		
		uadd.addActionListener(new EditDeadKeyActionListener(false, false));
		uedit.addActionListener(new EditDeadKeyActionListener(false, false));
		udel.addActionListener(new DeleteDeadKeyActionListener(false, false));
		ulpo.addActionListener(new EditLongPressActionListener(false, false));
		sadd.addActionListener(new EditDeadKeyActionListener(false, true));
		sedit.addActionListener(new EditDeadKeyActionListener(false, true));
		sdel.addActionListener(new DeleteDeadKeyActionListener(false, true));
		slpo.addActionListener(new EditLongPressActionListener(false, true));
		auadd.addActionListener(new EditDeadKeyActionListener(true, false));
		auedit.addActionListener(new EditDeadKeyActionListener(true, false));
		audel.addActionListener(new DeleteDeadKeyActionListener(true, false));
		aulpo.addActionListener(new EditLongPressActionListener(true, false));
		asadd.addActionListener(new EditDeadKeyActionListener(true, true));
		asedit.addActionListener(new EditDeadKeyActionListener(true, true));
		asdel.addActionListener(new DeleteDeadKeyActionListener(true, true));
		aslpo.addActionListener(new EditLongPressActionListener(true, true));
	}
	
	private static JLabel l(String label, String tooltip) {
		JLabel c = new JLabel(label);
		c.setToolTipText(tooltip);
		return c;
	}
	
	private static JPanel verticalStack(String... labels) {
		JPanel p = new JPanel(new GridLayout(0,1,4,4));
		for (String s : labels) p.add(new JLabel(s));
		return p;
	}
	
	private static JPanel verticalStack(Component... comps) {
		JPanel p = new JPanel(new GridLayout(0,1,4,4));
		for (Component c : comps) p.add(c);
		return p;
	}
	
	private static JPanel horizontalStack(Component... comps) {
		JPanel p = new JPanel(new GridLayout(1,0,4,4));
		for (Component c : comps) p.add(c);
		return p;
	}
	
	private static JPanel leftAlign(Component c) {
		JPanel p = new JPanel(new BorderLayout());
		p.add(c, BorderLayout.LINE_START);
		return p;
	}
	
	private static JPanel leftSxS(Component l, Component c, int gap) {
		JPanel p = new JPanel(new BorderLayout(gap, gap));
		p.add(l, BorderLayout.LINE_START);
		p.add(c, BorderLayout.CENTER);
		return p;
	}
	
	private static JPanel rightSxS(Component c, Component r, int gap) {
		JPanel p = new JPanel(new BorderLayout(gap, gap));
		p.add(c, BorderLayout.CENTER);
		p.add(r, BorderLayout.LINE_END);
		return p;
	}
	
	private static JPanel topAlign(Component c) {
		JPanel p = new JPanel(new BorderLayout());
		p.add(c, BorderLayout.PAGE_START);
		return p;
	}
	
	public void update() {
		uadd.setEnabled(km.unshiftedDeadKey == null);
		uedit.setEnabled(km.unshiftedDeadKey != null);
		udel.setEnabled(km.unshiftedDeadKey != null);
		sadd.setEnabled(km.shiftedDeadKey == null);
		sedit.setEnabled(km.shiftedDeadKey != null);
		sdel.setEnabled(km.shiftedDeadKey != null);
		auadd.setEnabled(km.altUnshiftedDeadKey == null);
		auedit.setEnabled(km.altUnshiftedDeadKey != null);
		audel.setEnabled(km.altUnshiftedDeadKey != null);
		asadd.setEnabled(km.altShiftedDeadKey == null);
		asedit.setEnabled(km.altShiftedDeadKey != null);
		asdel.setEnabled(km.altShiftedDeadKey != null);
	}
	
	public void commit() {
		km.unshiftedOutput = uop.getOutput();
		km.shiftedOutput = sop.getOutput();
		km.capsLockMapping = CapsLockMapping.values()[caps.getSelectedIndex()];
		km.altUnshiftedOutput = auop.getOutput();
		km.altShiftedOutput = asop.getOutput();
		km.altCapsLockMapping = CapsLockMapping.values()[acaps.getSelectedIndex()];
		km.ctrlOutput = ctrl.getOutput();
		km.commandOutput = cmd.getOutput();
	}
	
	private class EditDeadKeyActionListener implements ActionListener {
		private final boolean alt;
		private final boolean shift;
		public EditDeadKeyActionListener(boolean alt, boolean shift) {
			this.alt = alt;
			this.shift = shift;
		}
		public void actionPerformed(ActionEvent e) {
			controller.getDeadKeyTableFrame(key, alt, shift).setVisible(true);
		}
	}
	
	private class DeleteDeadKeyActionListener implements ActionListener {
		private final boolean alt;
		private final boolean shift;
		public DeleteDeadKeyActionListener(boolean alt, boolean shift) {
			this.alt = alt;
			this.shift = shift;
		}
		public void actionPerformed(ActionEvent e) {
			controller.setDeadKey(key, alt, shift, null);
		}
	}
	
	private class EditLongPressActionListener implements ActionListener {
		private final boolean alt;
		private final boolean shift;
		public EditLongPressActionListener(boolean alt, boolean shift) {
			this.alt = alt;
			this.shift = shift;
		}
		public void actionPerformed(ActionEvent e) {
			controller.getLongPressTableFrame(key, alt, shift).setVisible(true);
		}
	}
}
