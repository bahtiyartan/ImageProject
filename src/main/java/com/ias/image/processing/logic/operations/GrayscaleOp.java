package com.ias.image.processing.logic.operations;

import java.awt.image.BufferedImage;

@SuppressWarnings("serial")
public class GrayscaleOp implements ImageOperation {

    private String method;

    public GrayscaleOp() {
        this.method = "Luminance";
    }

    public GrayscaleOp(String method) {
        this.method = method;
    }

    public String getMethod() {
        return method;
    }

    public void updateOperation(String newMethod) {
        this.method = newMethod;
    }

    @Override
    public DataType getInputType() {
        return DataType.IMAGE;
    }

    @Override
    public DataType getOutputType() {
        return DataType.IMAGE;
    }

    @Override
    public OperationResult apply(OperationResult input) {
        if (input == null || !input.hasImage()) {
            throw new IllegalArgumentException("Grayscale operation requires an IMAGE input!");
        }

        BufferedImage img = input.getImageResult();
        int w = img.getWidth();
        int h = img.getHeight();

        BufferedImage result = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int p = img.getRGB(x, y);

                int a = (p >> 24) & 0xff;
                int r = (p >> 16) & 0xff;
                int g = (p >> 8) & 0xff;
                int b = p & 0xff;

                int gray = 0;

                switch (method) {
                    case "Average":
                        gray = (r + g + b) / 3;
                        break;

                    case "Simple":
                        int max = Math.max(r, Math.max(g, b));
                        int min = Math.min(r, Math.min(g, b));
                        gray = (max + min) / 2;
                        break;

                    case "Luminance":
                    default:
                        gray = (int) (0.299 * r + 0.587 * g + 0.114 * b);
                        break;
                }

                p = (a << 24) | (gray << 16) | (gray << 8) | gray;
                result.setRGB(x, y, p);
            }
        }

        String info = "Grayscale applied (Method: " + method + ")";
        return new OperationResult(result, info, null, null);
    }

    @Override
    public String getOperationName() {
        return "Grayscale (" + method + ")";
    }

    @Override
    public OperationType getOperationType() {
        return OperationType.GRAYSCALE;
    }

    @Override
    public int getOperationId() {
        return OperationType.GRAYSCALE.getOperationId();
    }

    @Override
    public String toJson() {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("\"operationId\": ").append(getOperationId()).append(",\n");
        json.append("\"operationName\": \"").append(getOperationName()).append("\",\n");
        json.append("\"params\": {\n");
        json.append("\"method\": \"").append(method).append("\"\n");
        json.append("}\n");
        json.append("}");
        return json.toString();
    }

    public static GrayscaleOp fromJson(String json) {
        String method = "Luminance";
        try {
            int idx = json.indexOf("\"method\"");
            if (idx != -1) {
                int colon = json.indexOf(":", idx);
                int comma = json.indexOf(",", colon);
                int endBrace = json.indexOf("}", colon);
                int end = (comma == -1) ? endBrace : Math.min(comma, endBrace);
                method = json.substring(colon + 1, end).replace("\"", "").trim();
            }
        } catch (Exception e) {
            method = "Luminance";
        }
        return new GrayscaleOp(method);
    }
}