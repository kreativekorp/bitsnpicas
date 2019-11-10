package com.kreative.bitsnpicas;

public abstract class PointSizeGenerator {
	public abstract void setRange(int min, int max);
	public abstract void setPointSizes(int ...pointSizes);
	public abstract int generatePointSize(Font<?> font);
	
	public static class Fixed extends PointSizeGenerator {
		private int pointSize;
		public Fixed(int pointSize) {
			this.pointSize = pointSize;
		}
		@Override
		public void setRange(int min, int max) {
			// Nothing.
		}
		@Override
		public void setPointSizes(int ...pointSizes) {
			// Nothing.
		}
		@Override
		public int generatePointSize(Font<?> font) {
			return pointSize;
		}
	}
	
	public static class Automatic extends PointSizeGenerator {
		private int min, max;
		public Automatic(int min, int max) {
			this.min = min;
			this.max = max;
		}
		@Override
		public void setRange(int min, int max) {
			this.min = min;
			this.max = max;
		}
		@Override
		public void setPointSizes(int ...pointSizes) {
			// Nothing.
		}
		@Override
		public int generatePointSize(Font<?> font) {
			int size = font.getEmAscent() + font.getEmDescent();
			return (size < min) ? min : (size > max) ? max : size;
		}
	}
	
	public static class Standard extends PointSizeGenerator {
		private int[] pointSizes;
		public Standard(int ...pointSizes) {
			this.pointSizes = pointSizes;
		}
		@Override
		public void setRange(int min, int max) {
			// Nothing.
		}
		@Override
		public void setPointSizes(int ...pointSizes) {
			this.pointSizes = pointSizes;
		}
		@Override
		public int generatePointSize(Font<?> font) {
			int size = font.getEmAscent() + font.getEmDescent();
			if (size <= pointSizes[0]) return pointSizes[0];
			int n = pointSizes.length - 1;
			for (int i = 0; i < n; i++) {
				if (size <= ((pointSizes[i] + pointSizes[i+1]) / 2)) {
					return pointSizes[i];
				}
			}
			return pointSizes[n];
		}
	}
}
