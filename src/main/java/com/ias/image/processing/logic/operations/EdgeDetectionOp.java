package com.ias.image.processing.logic.operations;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

@SuppressWarnings("serial")
public class EdgeDetectionOp implements ImageOperation {

    private boolean active = true;

    private String algorithm; // "Canny", "Sobel", "Laplacian"
    private double cannyThresh1;
    private double cannyThresh2;
    private int sobelDx;
    private int sobelDy;
    private int kernelSize;

    public EdgeDetectionOp() {
        this.algorithm = "Canny";
        this.cannyThresh1 = 100.0;
        this.cannyThresh2 = 200.0;
        this.sobelDx = 1;
        this.sobelDy = 1;
        this.kernelSize = 3;
    }

    @Override public boolean isActive() { return active; }
    @Override public void setActive(boolean active) { this.active = active; }

    public String getAlgorithm() { return algorithm; }
    public double getCannyThresh1() { return cannyThresh1; }
    public double getCannyThresh2() { return cannyThresh2; }
    public int getSobelDx() { return sobelDx; }
    public int getSobelDy() { return sobelDy; }
    public int getKernelSize() { return kernelSize; }

    public void updateOperation(String algo, double cT1, double cT2, int dx, int dy, int kSize) {
        this.algorithm = algo;
        this.cannyThresh1 = cT1;
        this.cannyThresh2 = cT2;
        this.sobelDx = dx;
        this.sobelDy = dy;

        this.kernelSize = (kSize % 2 == 0) ? kSize + 1 : kSize;
        if (this.kernelSize < 1) this.kernelSize = 1;
        if (this.kernelSize > 7) this.kernelSize = 7; // In edge detection, a kernel with a value greater than 7 can destabilize the system.
    }

    @Override public DataType getInputType() { return DataType.IMAGE; }
    @Override public DataType getOutputType() { return DataType.IMAGE; }

    @Override
    public OperationResult apply(OperationResult input) {
        if (input == null || !input.hasImage()) {
            throw new IllegalArgumentException("Edge Detection requires an IMAGE input");
        }

        BufferedImage img = input.getImageResult();
        Mat src = img2Mat(img);
        Mat gray = new Mat();

        if (src.channels() == 3) {
            Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);
        } else {
            src.copyTo(gray);
        }

        Mat edges = new Mat();

        if ("Canny".equals(algorithm)) {
            Imgproc.Canny(gray, edges, cannyThresh1, cannyThresh2);

        } else if ("Sobel".equals(algorithm)) {
            Mat sobelTemp = new Mat();
            Imgproc.Sobel(gray, sobelTemp, CvType.CV_16S, sobelDx, sobelDy, kernelSize, 1, 0, Core.BORDER_DEFAULT);
            Core.convertScaleAbs(sobelTemp, edges);

        } else if ("Laplacian".equals(algorithm)) {
            Mat lapTemp = new Mat();
            Imgproc.Laplacian(gray, lapTemp, CvType.CV_16S, kernelSize, 1, 0, Core.BORDER_DEFAULT);
            Core.convertScaleAbs(lapTemp, edges);
        }

        Mat dst = new Mat();
        Imgproc.cvtColor(edges, dst, Imgproc.COLOR_GRAY2BGR);

        BufferedImage resultImg = mat2Img(dst);
        return new OperationResult(resultImg, "Edge Detection (" + algorithm + ")", null, null);
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

    @Override public String getOperationName() { return "Edge Detection (" + algorithm + ")"; }
    @Override public OperationType getOperationType() { return OperationType.EDGE_DETECTION; }
    @Override public int getOperationId() { return OperationType.EDGE_DETECTION.getOperationId(); }

    @Override
    public String toJson() {
        return "{\n" +
                "  \"operationId\": " + getOperationId() + ",\n" +
                "  \"operationName\": \"" + getOperationName() + "\",\n" +
                "  \"params\": {\n" +
                "    \"algorithm\": \"" + algorithm + "\",\n" +
                "    \"cannyThresh1\": " + cannyThresh1 + ",\n" +
                "    \"cannyThresh2\": " + cannyThresh2 + ",\n" +
                "    \"sobelDx\": " + sobelDx + ",\n" +
                "    \"sobelDy\": " + sobelDy + ",\n" +
                "    \"kernelSize\": " + kernelSize + "\n" +
                "  }\n" +
                "}";
    }

    public static EdgeDetectionOp fromJson(String json) {
        EdgeDetectionOp op = new EdgeDetectionOp();
        try {
            String algo = extractField(json, "algorithm");
            double cT1 = Double.parseDouble(extractField(json, "cannyThresh1"));
            double cT2 = Double.parseDouble(extractField(json, "cannyThresh2"));
            int dx = Integer.parseInt(extractField(json, "sobelDx"));
            int dy = Integer.parseInt(extractField(json, "sobelDy"));
            int kSize = Integer.parseInt(extractField(json, "kernelSize"));

            op.updateOperation(algo, cT1, cT2, dx, dy, kSize);
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