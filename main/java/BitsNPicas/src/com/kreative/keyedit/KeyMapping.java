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
	
	public KeyMapping(Key key) {
		this.commandOutput = key.defaultUnshifted;
		this.unshiftedOutput = key.defaultUnshifted;
		this.shiftedOutput = key.defaultShifted;
		this.ctrlOutput = key.defaultCtrl;
		this.altUnshiftedOutput = -1;
		this.altShiftedOutput = -1;
		this.capsLockMapping = CapsLockMapping.AUTO;
		this.altCapsLockMapping = CapsLockMapping.AUTO;
		this.commandDeadKey = null;
		this.unshiftedDeadKey = null;
		this.shiftedDeadKey = null;
		this.ctrlDeadKey = null;
		this.altUnshiftedDeadKey = null;
		this.altShiftedDeadKey = null;
	}
}
