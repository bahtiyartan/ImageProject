package com.ias.image.processing.logic.operations;

import java.awt.image.BufferedImage;

@SuppressWarnings("serial")
public class ColorHistogramOp implements ImageOperation {

	private int binCount;

	private double domRed = 0.0;
	private double domGreen = 0.0;
	private double domBlue = 0.0;

	public ColorHistogramOp() {
		this.binCount = 2;
	}

	public ColorHistogramOp(int binCount) {
		this.binCount = binCount;
	}

	public int getBinCount() { return binCount; }
	public double getDomRed() { return domRed; }
	public double getDomGreen() { return domGreen; }
	public double getDomBlue() { return domBlue; }

	@Override
	public DataType getInputType() {
		return DataType.IMAGE;
	}

	@Override
	public DataType getOutputType() {
		return DataType.STRING;
	}

	@Override
	public OperationResult apply(OperationResult input) {
		if (input == null || !input.hasImage()) {
			throw new IllegalArgumentException("Histogram operation requires an IMAGE input!");
		}

		BufferedImage img = input.getImageResult();
		int w = img.getWidth();
		int h = img.getHeight();

		int[] rBins = new int[binCount];
		int[] gBins = new int[binCount];
		int[] bBins = new int[binCount];

		double binSize = 256.0 / binCount;

		long sumR = 0, sumG = 0, sumB = 0;

		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				int rgb = img.getRGB(x, y);

				int r = (rgb >> 16) & 0xFF;
				int g = (rgb >> 8) & 0xFF;
				int b = rgb & 0xFF;

				sumR += r;
				sumG += g;
				sumB += b;

				int rIndex = (int) (r / binSize);
				int gIndex = (int) (g / binSize);
				int bIndex = (int) (b / binSize);

				if (rIndex >= binCount) rIndex = binCount - 1;
				if (gIndex >= binCount) gIndex = binCount - 1;
				if (bIndex >= binCount) bIndex = binCount - 1;

				rBins[rIndex]++;
				gBins[gIndex]++;
				bBins[bIndex]++;
			}
		}
		long totalSum = sumR + sumG + sumB;
		if (totalSum > 0) {
			domRed = (sumR * 100.0) / totalSum;
			domGreen = (sumG * 100.0) / totalSum;
			domBlue = (sumB * 100.0) / totalSum;
		}

		int totalPixels = w * h;
		StringBuilder histogramText = new StringBuilder();

		histogramText.append("RED CHANNEL\n");
		appendHistogram(histogramText, rBins, totalPixels);
		histogramText.append("GREEN CHANNEL\n");
		appendHistogram(histogramText, gBins, totalPixels);
		histogramText.append("BLUE CHANNEL\n");
		appendHistogram(histogramText, bBins, totalPixels);

		return new OperationResult(img, histogramText.toString().trim(), null, null);
	}

	private void appendHistogram(StringBuilder sb, int[] bins, int totalPixels) {
		for (int i = 0; i < bins.length; i++) {
			int start = (int)(i * (256.0 / bins.length));
			int end = (int)((i + 1) * (256.0 / bins.length) - 1);
			double percent = (bins[i] * 100.0) / totalPixels;
			sb.append(String.format("  %3d-%3d: %d px (%.1f%%)\n", start, end, bins[i], percent));
		}
	}

	@Override
	public String getOperationName() { return "Color Histogram"; }

	@Override
	public OperationType getOperationType() { return OperationType.COLOR_HISTOGRAM; }

	@Override
	public int getOperationId() { return OperationType.COLOR_HISTOGRAM.getOperationId(); }

	@Override
	public String toJson() {
		StringBuilder json = new StringBuilder();
		json.append("{\n");
		json.append("\"operationId\": ").append(getOperationId()).append(",\n");
		json.append("\"operationName\": \"").append(getOperationName()).append("\",\n");
		json.append("\"params\": {\n");
		json.append("\"binCount\": ").append(binCount).append("\n");
		json.append("}\n");
		json.append("}");
		return json.toString();
	}

	public static ColorHistogramOp fromJson(String json) {
		int binCount = 2;
		try {
			String key = "\"binCount\":";
			int index = json.indexOf(key);
			if (index != -1) {
				int start = index + key.length();
				int end = json.indexOf("\n", start);
				binCount = Integer.parseInt(json.substring(start, end).trim());
			}
		} catch (Exception e) {
			binCount = 2;
		}
		return new ColorHistogramOp(binCount);
	}
}