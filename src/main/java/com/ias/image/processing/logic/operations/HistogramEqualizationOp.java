package com.ias.image.processing.logic.operations;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.CLAHE;
import org.opencv.imgproc.Imgproc;

@SuppressWarnings("serial")
public class HistogramEqualizationOp implements ImageOperation {

    private String mode; // std and clahe
    private double clipLimit;
    private int tileSize;
    private boolean active = true;

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    public HistogramEqualizationOp() {
        this.mode = "Standard";
        this.clipLimit = 4.0;
        this.tileSize = 8;
    }

    public String getMode() { return mode; }
    public double getClipLimit() { return clipLimit; }
    public int getTileSize() { return tileSize; }

    public void updateOperation(String mode, double clipLimit, int tileSize) {
        this.mode = mode;
        this.clipLimit = clipLimit;
        this.tileSize = (tileSize < 1) ? 1 : tileSize;
    }

    @Override
    public DataType getInputType() { return DataType.IMAGE; }

    @Override
    public DataType getOutputType() { return DataType.IMAGE; }

    @Override
    public OperationResult apply(OperationResult input) {
        if (input == null || !input.hasImage()) {
            throw new IllegalArgumentException("Histogram Equalization requires an IMAGE input");
        }

        BufferedImage img = input.getImageResult();
        Mat src = img2Mat(img);
        Mat ycrcb = new Mat();

        Imgproc.cvtColor(src, ycrcb, Imgproc.COLOR_BGR2YCrCb);
        //(0:Y, 1: Cr, 2: Cb)
        List<Mat> channels = new ArrayList<>();
        Core.split(ycrcb, channels);

        Mat yChannel = channels.get(0);

        if ("Standard".equals(mode)) {
            Imgproc.equalizeHist(yChannel, yChannel);
        } else if ("CLAHE".equals(mode)) {
            CLAHE clahe = Imgproc.createCLAHE(clipLimit, new Size(tileSize, tileSize));
            clahe.apply(yChannel, yChannel);
        }

        channels.set(0, yChannel);
        Core.merge(channels, ycrcb);

        Mat dst = new Mat();
        Imgproc.cvtColor(ycrcb, dst, Imgproc.COLOR_YCrCb2BGR);

        BufferedImage resultImg = mat2Img(dst);
        return new OperationResult(resultImg, "Hist. Equalization (" + mode + ")", null, null);
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
    public String getOperationName() { return "Hist. Eq. (" + mode + ")"; }

    @Override
    public OperationType getOperationType() { return OperationType.HISTOGRAM_EQ; }

    @Override
    public int getOperationId() { return OperationType.HISTOGRAM_EQ.getOperationId(); }

    @Override
    public String toJson() { return "{ \"operationId\": " + getOperationId() + " }"; }

    public static HistogramEqualizationOp fromJson(String json) {
        return new HistogramEqualizationOp();
    }
}