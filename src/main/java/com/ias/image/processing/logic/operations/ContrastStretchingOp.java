package com.ias.image.processing.logic.operations;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

@SuppressWarnings("serial")
public class ContrastStretchingOp implements ImageOperation {

    private String mode;
    private double targetMin; // a
    private double targetMax; // b
    private double sourceMin; // c
    private double sourceMax; // d

    public ContrastStretchingOp() {
        this.mode = "Auto (Min-Max)";
        this.targetMin = 0.0;
        this.targetMax = 255.0;
        this.sourceMin = 50.0;
        this.sourceMax = 200.0;
    }

    public String getMode() { return mode; }
    public double getTargetMin() { return targetMin; }
    public double getTargetMax() { return targetMax; }
    public double getSourceMin() { return sourceMin; }
    public double getSourceMax() { return sourceMax; }

    public void updateOperation(String mode, double tMin, double tMax, double sMin, double sMax) {
        this.mode = mode;
        this.targetMin = tMin;
        this.targetMax = tMax;
        this.sourceMin = sMin;
        this.sourceMax = sMax;
    }

    @Override
    public DataType getInputType() { return DataType.IMAGE; }

    @Override
    public DataType getOutputType() { return DataType.IMAGE; }

    @Override
    public OperationResult apply(OperationResult input) {
        if (input == null || !input.hasImage()) {
            throw new IllegalArgumentException("Contrast Stretching requires an IMAGE input");
        }

        BufferedImage img = input.getImageResult();
        Mat src = img2Mat(img);
        Mat ycrcb = new Mat();

        Imgproc.cvtColor(src, ycrcb, Imgproc.COLOR_BGR2YCrCb);
        List<Mat> channels = new ArrayList<>();
        Core.split(ycrcb, channels);
        Mat yChannel = channels.get(0);

        if ("Auto (Min-Max)".equals(mode)) {
            Core.normalize(yChannel, yChannel, targetMin, targetMax, Core.NORM_MINMAX);
        }
        else if ("Custom (Robust)".equals(mode)) {
            double diff = sourceMax - sourceMin;
            if (diff == 0) diff = 1.0;

            // Linear transformation mathematics(y = alpha * x + beta)
            double alpha = (targetMax - targetMin) / diff;
            double beta = targetMin - (sourceMin * alpha);

            yChannel.convertTo(yChannel, -1, alpha, beta);
        }

        channels.set(0, yChannel);
        Core.merge(channels, ycrcb);
        Mat dst = new Mat();
        Imgproc.cvtColor(ycrcb, dst, Imgproc.COLOR_YCrCb2BGR);

        BufferedImage resultImg = mat2Img(dst);
        return new OperationResult(resultImg, "Contrast Stretch (" + mode + ")", null, null);
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

    @Override
    public String getOperationName() { return "Contrast Stretch (" + mode + ")"; }
    @Override
    public OperationType getOperationType() { return OperationType.CONTRAST_STRETCH; }
    @Override
    public int getOperationId() { return OperationType.CONTRAST_STRETCH.getOperationId(); }
    @Override
    public String toJson() { return "{ \"operationId\": " + getOperationId() + " }"; }

    public static ContrastStretchingOp fromJson(String json) { return new ContrastStretchingOp(); }
}