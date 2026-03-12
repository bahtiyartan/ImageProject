package com.ias.image.processing.logic.operations;

import java.awt.image.BufferedImage;

@SuppressWarnings("serial")
public class ColorHistogramOp implements ImageOperation {

    public ColorHistogramOp() { }

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
            throw new IllegalArgumentException("Histogram operation requires an IMAGE input");
        }

        BufferedImage img = input.getImageResult();
        int w = img.getWidth();
        int h = img.getHeight();

        int[] bins = new int[5];

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;

                double luminance = 0.299 * r + 0.587 * g + 0.114 * b;

                int binIndex = (int) (luminance / 52);
                if (binIndex > 4) binIndex = 4;
                bins[binIndex]++;
            }
        }
        int totalPixels = w * h;

        StringBuilder histogramText = new StringBuilder();
        histogramText.append("Brightness Histogram Analysis:\n\n");
        histogramText.append("Very Dark (0-51)    : %").append(calculatePercentage(bins[0], totalPixels)).append("\n");
        histogramText.append("Dark (52-102)       : %").append(calculatePercentage(bins[1], totalPixels)).append("\n");
        histogramText.append("Mid Tones (103-153) : %").append(calculatePercentage(bins[2], totalPixels)).append("\n");
        histogramText.append("Light (154-204)     : %").append(calculatePercentage(bins[3], totalPixels)).append("\n");
        histogramText.append("Very Light (205-255): %").append(calculatePercentage(bins[4], totalPixels)).append("\n");

        return new OperationResult(img, histogramText.toString(), null, null);
    }

    private String calculatePercentage(int count, int total) {
        double percent = (count * 100.0) / total;
        return String.format("%.2f", percent);
    }

    @Override
    public String getOperationName() {
        return "Generate Color Histogram";
    }

    @Override
    public OperationType getOperationType() {
        return OperationType.COLOR_HISTOGRAM;
    }

    @Override
    public int getOperationId() {
        return OperationType.COLOR_HISTOGRAM.getOperationId();
    }

    @Override
    public String toJson() {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("\"operationId\": ").append(getOperationType().getOperationId()).append(",\n");
        json.append("\"operationName\": \"").append(getOperationName()).append("\",\n");
        json.append("\"params\": {}\n");
        json.append("}");
        return json.toString();
    }

    public static ColorHistogramOp fromJson(String json) {
        return new ColorHistogramOp();
    }
}