package com.kreative.bitsnpicas;

public abstract class IDGenerator {
	public abstract void setRange(int min, int max);
	public abstract int generateID(Font<?> font);
	
	public static class Sequential extends IDGenerator {
		private int id, min, max;
		public Sequential(int id, int min, int max) {
			this.id = id;
			this.min = min;
			this.max = max;
		}
		@Override
		public void setRange(int min, int max) {
			this.min = min;
			this.max = max;
		}
		@Override
		public int generateID(Font<?> font) {
			int newId = id++;
			if (id >= max) id = min;
			return newId;
		}
	}
	
	public static class Random extends IDGenerator {
		private java.util.Random random;
		private int min, max;
		public Random(int min, int max) {
			this.random = new java.util.Random();
			this.min = min;
			this.max = max;
		}
		@Override
		public void setRange(int min, int max) {
			this.min = min;
			this.max = max;
		}
		@Override
		public int generateID(Font<?> font) {
			return random.nextInt(max - min) + min;
		}
	}
	
	public static class HashCode extends IDGenerator {
		private int min, max;
		public HashCode(int min, int max) {
			this.min = min;
			this.max = max;
		}
		@Override
		public void setRange(int min, int max) {
			this.min = min;
			this.max = max;
		}
		@Override
		public int generateID(Font<?> font) {
			int id = font.getName(Font.NAME_FAMILY).hashCode() >>> 1;
			return (id % (max - min)) + min;
		}
	}
}
