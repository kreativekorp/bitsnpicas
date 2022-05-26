package com.kreative.keyedit;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.imageio.ImageIO;

public class ColorReducer {
	public static void main(String[] args) {
		int count = 256;
		for (String arg : args) {
			try {
				count = Integer.parseInt(arg);
			} catch (NumberFormatException nfe) {
				System.out.println(arg);
				try {
					BufferedImage image = ImageIO.read(new File(arg));
					int[] colors = reduce(image, count);
					for (int color : colors) {
						int r = ((color >> 16) & 0xFF);
						int g = ((color >>  8) & 0xFF);
						int b = ((color >>  0) & 0xFF);
						int k = (r*30 + g*59 + b*11);
						System.out.print("\u001B[48;2;" + r + ";" + g + ";" + b + "m");
						System.out.print((k < 12750) ? "\u001B[97m" : "\u001B[30m");
						System.out.print(" " + Integer.toHexString(color).toUpperCase() + " ");
						System.out.print("\u001B[0m");
					}
					System.out.println();
				} catch (IOException e) {
					System.out.println("ERROR: " + e);
				}
			}
		}
	}
	
	public static int[] reduce(BufferedImage image, int count) {
		int w = image.getWidth(), h = image.getHeight();
		int[] rgb = new int[w * h];
		image.getRGB(0, 0, w, h, rgb, 0, w);
		return reduce(rgb, count);
	}
	
	public static int[] reduce(int[] rgb, int count) {
		return reduce(rgb, 0, rgb.length, count);
	}
	
	private static int[] reduce(int[] rgb, int start, int end, int count) {
		if (start >= end || count <= 0) return new int[0];
		List<VBox> vboxes = new ArrayList<VBox>();
		vboxes.add(new VBox(rgb, start, end));
		while (vboxes.size() < count) {
			boolean wv = (vboxes.size() >= count * 0.85f);
			int largestIndex = -1;
			int largestWeight = -1;
			for (int i = 0, n = vboxes.size(); i < n; i++) {
				VBox vbox = vboxes.get(i);
				if (vbox.canSplit()) {
					int weight = vbox.population;
					if (wv) weight *= vbox.volume;
					if (weight > largestWeight) {
						largestIndex = i;
						largestWeight = weight;
					}
				}
			}
			if (largestIndex < 0) break;
			VBox vbox = vboxes.remove(largestIndex);
			vboxes.addAll(largestIndex, Arrays.asList(vbox.split()));
		}
		count = vboxes.size();
		rgb = new int[count];
		for (int i = 0; i < count; i++) {
			rgb[i] = vboxes.get(i).average;
		}
		return rgb;
	}
	
	private static class VBox {
		public final int[] rgb;
		public final int start, end;
		public final int population;
		public final int volume;
		public final int average;
		public final int median;
		public VBox(int[] rgb, int start, int end) {
			this.rgb = rgb;
			this.start = start;
			this.end = end;
			if (start >= end) {
				this.population = 0;
				this.volume = 0;
				this.average = (0xFF << 24);
				this.median = (start + end) / 2;
				return;
			}
			int minR = 255, maxR = 0; long totalR = 0;
			int minG = 255, maxG = 0; long totalG = 0;
			int minB = 255, maxB = 0; long totalB = 0;
			for (int i = start; i < end; i++) {
				int r = (rgb[i] >> 16) & 0xFF;
				if (r < minR) minR = r;
				if (r > maxR) maxR = r;
				totalR += r;
				int g = (rgb[i] >>  8) & 0xFF;
				if (g < minG) minG = g;
				if (g > maxG) maxG = g;
				totalG += g;
				int b = (rgb[i] >>  0) & 0xFF;
				if (b < minB) minB = b;
				if (b > maxB) maxB = b;
				totalB += b;
			}
			int diffR = maxR - minR + 1;
			int diffG = maxG - minG + 1;
			int diffB = maxB - minB + 1;
			int avgR = (int)(totalR / (end - start));
			int avgG = (int)(totalG / (end - start));
			int avgB = (int)(totalB / (end - start));
			this.population = end - start;
			this.volume = diffR * diffG * diffB;
			this.average = (0xFF << 24) | (avgR << 16) | (avgG << 8) | (avgB << 0);
			if (diffG >= diffR && diffG >= diffB) {
				this.median = getVBoxMedian(rgb, start, end,  8, minG, maxG);
			} else if (diffR >= diffB) {
				this.median = getVBoxMedian(rgb, start, end, 16, minR, maxR);
			} else {
				this.median = getVBoxMedian(rgb, start, end,  0, minB, maxB);
			}
		}
		public boolean canSplit() {
			return population > 1 && volume > 1 && start < median && median < end;
		}
		public VBox[] split() {
			boolean leftEmpty = (start >= median);
			boolean rightEmpty = (median >= end);
			if (leftEmpty && rightEmpty) return new VBox[0];
			if (leftEmpty || rightEmpty) return new VBox[]{this};
			VBox left = new VBox(rgb, start, median);
			VBox right = new VBox(rgb, median, end);
			return new VBox[]{left, right};
		}
	}
	
	private static int getVBoxMedian(int[] rgb, int start, int end, int shift, int min, int max) {
		Collections.sort(
			new IntegerListView(rgb).subList(start, end),
			new MaskedComparator(0xFF << shift)
		);
		int mid = (rgb[(start + end) / 2] >> shift) & 0xFF;
		int left = mid - min;
		int right = max - mid;
		mid = (
			(left <= right)
			? Math.min(max - 1, mid + right / 2)
			: Math.max(min, mid - left / 2 - 1)
		);
		for (int i = start; i < end; i++) {
			int c = (rgb[i] >> shift) & 0xFF;
			if (c > mid) return i;
		}
		return end;
	}
	
	private static class MaskedComparator implements Comparator<Integer> {
		private final int mask;
		public MaskedComparator(int mask) {
			this.mask = mask;
		}
		@Override
		public int compare(Integer a, Integer b) {
			return (a & mask) - (b & mask);
		}
	}
	
	private static class IntegerListView extends AbstractList<Integer> {
		private final int[] backingArray;
		public IntegerListView(int[] array) {
			this.backingArray = array;
		}
		@Override
		public Integer get(int index) {
			return backingArray[index];
		}
		@Override
		public boolean isEmpty() {
			return backingArray.length == 0;
		}
		@Override
		public Integer set(int index, Integer value) {
			int oldValue = backingArray[index];
			backingArray[index] = value;
			return oldValue;
		}
		@Override
		public int size() {
			return backingArray.length;
		}
	}
}
