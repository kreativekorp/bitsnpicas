package com.kreative.unicode.data;

public enum EncodingInclude {
	C0 {
		@Override
		public void includeIn(Encoding e) {
			for (char i = 0x00; i < 0x20; i++) {
				e.setSequence(Character.toString(i), i);
			}
		}
	},
	
	ASCII {
		@Override
		public void includeIn(Encoding e) {
			for (char i = 0x20; i < 0x7F; i++) {
				e.setSequence(Character.toString(i), i);
			}
		}
	},
	
	DEL {
		@Override
		public void includeIn(Encoding e) {
			e.setSequence(Character.toString((char)0x7F), 0x7F);
		}
	},
	
	C1 {
		@Override
		public void includeIn(Encoding e) {
			for (char i = 0x80; i < 0xA0; i++) {
				e.setSequence(Character.toString(i), i);
			}
		}
	},
	
	Latin1 {
		@Override
		public void includeIn(Encoding e) {
			for (char i = 0xA0; i < 0x100; i++) {
				e.setSequence(Character.toString(i), i);
			}
		}
	};
	
	public abstract void includeIn(Encoding e);
}
