package com.ias.image.processing.logic.operations;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

@SuppressWarnings("serial")
public class MorphologyOp implements ImageOperation {

    private boolean active = true;

    private String morphType;
    private String kernelShape;
    private int kernelSize;
    private int iterations;

    public MorphologyOp() {
        this.morphType = "Closing";
        this.kernelShape = "Rect";
        this.kernelSize = 5;
        this.iterations = 1;
    }

    @Override public boolean isActive() { return active; }
    @Override public void setActive(boolean active) { this.active = active; }

    public String getMorphType() { return morphType; }
    public String getKernelShape() { return kernelShape; }
    public int getKernelSize() { return kernelSize; }
    public int getIterations() { return iterations; }

    public void updateOperation(String type, String shape, int size, int iter) {
        this.morphType = type;
        this.kernelShape = shape;
        this.kernelSize = (size % 2 == 0) ? size + 1 : size;
        if (this.kernelSize < 1) this.kernelSize = 1;
        this.iterations = Math.max(1, iter);
    }

    @Override
    public DataType getInputType() { return DataType.IMAGE; }
    @Override
    public DataType getOutputType() { return DataType.IMAGE; }

    @Override
    public OperationResult apply(OperationResult input) {
        if (input == null || !input.hasImage()) {
            throw new IllegalArgumentException("Morphology requires an IMAGE input");
        }

        BufferedImage img = input.getImageResult();
        Mat src = img2Mat(img);
        Mat dst = new Mat();

        int op = Imgproc.MORPH_CLOSE;
        if ("Erosion".equals(morphType)) op = Imgproc.MORPH_ERODE;
        else if ("Dilation".equals(morphType)) op = Imgproc.MORPH_DILATE;
        else if ("Opening".equals(morphType)) op = Imgproc.MORPH_OPEN;

        int shape = Imgproc.MORPH_RECT;
        if ("Cross".equals(kernelShape)) shape = Imgproc.MORPH_CROSS;
        else if ("Ellipse".equals(kernelShape)) shape = Imgproc.MORPH_ELLIPSE;

        Mat element = Imgproc.getStructuringElement(shape, new Size(kernelSize, kernelSize));

        Imgproc.morphologyEx(src, dst, op, element, new Point(-1, -1), iterations);

        BufferedImage resultImg = mat2Img(dst);
        return new OperationResult(resultImg, "Morphology: " + morphType, null, null);
    }

    private Mat img2Mat(BufferedImage in) {
        BufferedImage convertedImg = new BufferedImage(in.getWidth(), in.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        java.awt.Graphics2D g = convertedImg.createGraphics();
        g.drawImage(in, 0, 0, null);
        g.dispose();

        Mat out = new Mat(convertedImg.getHeight(), convertedImg.getWidth(), CvType.CV_8UC3);
        byte[] data = ((DataBufferByte) convertedImg.getRaster().getDataBuffer()).getData();
        out.put(0, 0, data);
        return out;
    }

    private BufferedImage mat2Img(Mat in) {
        BufferedImage out = new BufferedImage(in.width(), in.height(), BufferedImage.TYPE_3BYTE_BGR);
        byte[] data = ((DataBufferByte) out.getRaster().getDataBuffer()).getData();
        in.get(0, 0, data);
        return out;
    }

    @Override public String getOperationName() { return "Morphology (" + morphType + ")"; }
    @Override public OperationType getOperationType() { return OperationType.MORPHOLOGY; }
    @Override public int getOperationId() { return OperationType.MORPHOLOGY.getOperationId(); }

    @Override
    public String toJson() {
        return "{\n" +
                "  \"operationId\": " + getOperationId() + ",\n" +
                "  \"operationName\": \"" + getOperationName() + "\",\n" +
                "  \"params\": {\n" +
                "    \"morphType\": \"" + morphType + "\",\n" +
                "    \"kernelShape\": \"" + kernelShape + "\",\n" +
                "    \"kernelSize\": " + kernelSize + ",\n" +
                "    \"iterations\": " + iterations + "\n" +
                "  }\n" +
                "}";
    }

    public static MorphologyOp fromJson(String json) {
        MorphologyOp op = new MorphologyOp();
        try {
            String type = extractField(json, "morphType");
            String shape = extractField(json, "kernelShape");
            int size = Integer.parseInt(extractField(json, "kernelSize"));
            int iter = Integer.parseInt(extractField(json, "iterations"));

            op.updateOperation(type, shape, size, iter);
        } catch (Exception e) {}
        return op;
    }

    private static String extractField(String json, String field) {
        int idx = json.indexOf("\"" + field + "\"");
        if (idx == -1) return null;
        int colon = json.indexOf(":", idx);
        int comma = json.indexOf(",", colon);
        int endBrace = json.indexOf("}", colon);
        int end = (comma == -1) ? endBrace : Math.min(comma, endBrace);
        String value = json.substring(colon + 1, end).trim();
        return value.replace("\"", "");
    }}