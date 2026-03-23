package com.ias.image.processing.logic.operations;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

@SuppressWarnings("serial")
public class ThresholdOp implements ImageOperation {

    private String mode = "Simple";
    private double maxVal = 255.0;
    private int thresholdType = Imgproc.THRESH_BINARY;
    private double thresh = 127.0;
    private int adaptiveMethod = Imgproc.ADAPTIVE_THRESH_MEAN_C;
    private int blockSize = 11;
    private double C = 2.0;

    public ThresholdOp() {
    }

    public String getMode() { return mode; }
    public double getMaxVal() { return maxVal; }
    public int getThresholdType() { return thresholdType; }
    public double getThresh() { return thresh; }
    public int getAdaptiveMethod() { return adaptiveMethod; }
    public int getBlockSize() { return blockSize; }
    public double getC() { return C; }

    public void updateOperation(String mode, double maxVal, int thresholdType, double thresh, int adaptiveMethod, int blockSize, double C) {
        this.mode = mode;
        this.maxVal = maxVal;
        this.thresholdType = thresholdType;
        this.thresh = thresh;
        this.adaptiveMethod = adaptiveMethod;

        if (blockSize < 3) blockSize = 3;
        if (blockSize % 2 == 0) blockSize += 1;
        this.blockSize = blockSize;

        this.C = C;
    }

    @Override
    public DataType getInputType() { return DataType.IMAGE; }

    @Override
    public DataType getOutputType() { return DataType.IMAGE; }

    @Override
    public OperationResult apply(OperationResult input) {
        if (input == null || !input.hasImage()) throw new IllegalArgumentException("Threshold requires an IMAGE input");

        BufferedImage img = input.getImageResult();
        Mat src = img2Mat(img);
        Mat gray = new Mat();
        Mat dst = new Mat();

        Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);

        if ("Simple".equals(mode)) {
            Imgproc.threshold(gray, dst, thresh, maxVal, thresholdType);
        } else if ("Adaptive".equals(mode)) {
            Imgproc.adaptiveThreshold(gray, dst, maxVal, adaptiveMethod, thresholdType, blockSize, C);
        } else if ("Otsu".equals(mode)) {
            Imgproc.threshold(gray, dst, 0, maxVal, thresholdType | Imgproc.THRESH_OTSU);
        }

        BufferedImage resultImg = mat2Img(dst);
        return new OperationResult(resultImg, "Threshold: " + mode, null, null);
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
        Mat bgrMat = new Mat();
        Imgproc.cvtColor(in, bgrMat, Imgproc.COLOR_GRAY2BGR);
        BufferedImage out = new BufferedImage(bgrMat.width(), bgrMat.height(), BufferedImage.TYPE_3BYTE_BGR);
        byte[] data = ((DataBufferByte) out.getRaster().getDataBuffer()).getData();
        bgrMat.get(0, 0, data);
        return out;
    }

    @Override
    public String getOperationName() { return "Threshold (" + mode + ")"; }

    @Override
    public OperationType getOperationType() { return OperationType.THRESHOLD; }

    @Override
    public int getOperationId() { return OperationType.THRESHOLD.getOperationId(); }

    @Override
    public String toJson() { return "{ \"operationId\": " + getOperationId() + " }"; }

    public static ThresholdOp fromJson(String json) { return new ThresholdOp(); }
}