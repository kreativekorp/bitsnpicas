package com.kreative.keyedit;

public class KeyMapping {
	public int commandOutput;
	public int unshiftedOutput;
	public int shiftedOutput;
	public int ctrlOutput;
	public int altUnshiftedOutput;
	public int altShiftedOutput;
	public CapsLockMapping capsLockMapping;
	public CapsLockMapping altCapsLockMapping;
	public DeadKeyTable commandDeadKey;
	public DeadKeyTable unshiftedDeadKey;
	public DeadKeyTable shiftedDeadKey;
	public DeadKeyTable ctrlDeadKey;
	public DeadKeyTable altUnshiftedDeadKey;
	public DeadKeyTable altShiftedDeadKey;
	public int[] unshiftedLongPressOutput;
	public int[] shiftedLongPressOutput;
	public int[] altUnshiftedLongPressOutput;
	public int[] altShiftedLongPressOutput;
	
	public KeyMapping(Key key) {
		this.commandOutput = key.defaultUnshifted;
		this.unshiftedOutput = key.defaultUnshifted;
		this.shiftedOutput = key.defaultShifted;
		this.ctrlOutput = key.defaultCtrl;
		this.altUnshiftedOutput = key.defaultAltUnshifted;
		this.altShiftedOutput = key.defaultAltShifted;
		this.capsLockMapping = CapsLockMapping.AUTO;
		this.altCapsLockMapping = CapsLockMapping.AUTO;
		this.commandDeadKey = null;
		this.unshiftedDeadKey = null;
		this.shiftedDeadKey = null;
		this.ctrlDeadKey = null;
		this.altUnshiftedDeadKey = null;
		this.altShiftedDeadKey = null;
		this.unshiftedLongPressOutput = null;
		this.shiftedLongPressOutput = null;
		this.altUnshiftedLongPressOutput = null;
		this.altShiftedLongPressOutput = null;
	}
	
	public void swapAlt() {
		int o; CapsLockMapping m; DeadKeyTable d;
		o = unshiftedOutput  ; unshiftedOutput  = altUnshiftedOutput  ; altUnshiftedOutput  = o;
		o = shiftedOutput    ; shiftedOutput    = altShiftedOutput    ; altShiftedOutput    = o;
		m = capsLockMapping  ; capsLockMapping  = altCapsLockMapping  ; altCapsLockMapping  = m;
		d = unshiftedDeadKey ; unshiftedDeadKey = altUnshiftedDeadKey ; altUnshiftedDeadKey = d;
		d = shiftedDeadKey   ; shiftedDeadKey   = altShiftedDeadKey   ; altShiftedDeadKey   = d;
	}
}
