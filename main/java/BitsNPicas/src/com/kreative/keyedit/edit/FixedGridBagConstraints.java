package com.kreative.keyedit.edit;

import java.io.Serializable;

public class FixedGridBagConstraints implements Cloneable, Serializable {
	private static final long serialVersionUID = 1L;
	
	public int gridx = 0;
	public int gridy = 0;
	public int gridwidth = 1;
	public int gridheight = 1;
	
	@Override
	public FixedGridBagConstraints clone() {
		FixedGridBagConstraints c = new FixedGridBagConstraints();
		c.gridx = this.gridx;
		c.gridy = this.gridy;
		c.gridwidth = this.gridwidth;
		c.gridheight = this.gridheight;
		return c;
	}
}
