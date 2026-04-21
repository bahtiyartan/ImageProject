package com.ias.image.processing.logic.operations;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

@SuppressWarnings("serial")
public class BlobCounterOp implements ImageOperation {

    private boolean active = true;

    private double minArea = 50.0;
    private double maxArea = 50000.0;

    private boolean drawBox = true;
    private boolean drawCentroid = true;
    private boolean drawAreaText = false;

    private int roiX = 0, roiY = 0, roiW = 0, roiH = 0;

    public BlobCounterOp() {}

    @Override
    public boolean isActive() { return active; }
    @Override
    public void setActive(boolean active) { this.active = active; }

    public double getMinArea() { return minArea; }
    public double getMaxArea() { return maxArea; }
    public boolean isDrawBox() { return drawBox; }
    public boolean isDrawCentroid() { return drawCentroid; }
    public boolean isDrawAreaText() { return drawAreaText; }
    public int getRoiX() { return roiX; }
    public int getRoiY() { return roiY; }
    public int getRoiW() { return roiW; }
    public int getRoiH() { return roiH; }

    public void updateOperation(double minA, double maxA, boolean dBox, boolean dCentroid, boolean dText, int rx, int ry, int rw, int rh) {
        this.minArea = minA;
        this.maxArea = maxA;
        this.drawBox = dBox;
        this.drawCentroid = dCentroid;
        this.drawAreaText = dText;
        this.roiX = rx;
        this.roiY = ry;
        this.roiW = rw;
        this.roiH = rh;
    }

    @Override
    public DataType getInputType() { return DataType.IMAGE; }
    @Override
    public DataType getOutputType() { return DataType.IMAGE; }

    @Override
    public OperationResult apply(OperationResult input) {
        if (input == null || !input.hasImage()) throw new IllegalArgumentException("Blob Counter requires IMAGE input");

        BufferedImage img = input.getImageResult();
        Mat src = img2Mat(img);
        Mat gray = new Mat();

        if (src.channels() == 3) {
            Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);
        } else {
            src.copyTo(gray);
        }
        Mat binary = new Mat();
        Imgproc.threshold(gray, binary, 1, 255, Imgproc.THRESH_BINARY);

        Mat drawTarget = new Mat();
        if (src.channels() == 1) {
            Imgproc.cvtColor(src, drawTarget, Imgproc.COLOR_GRAY2BGR);
        } else {
            src.copyTo(drawTarget);
        }

        Mat procMat = binary;
        Mat targetMat = drawTarget;

        if (roiW > 0 && roiH > 0) {
            int safeX = Math.max(0, roiX);
            int safeY = Math.max(0, roiY);
            int safeW = Math.min(roiW, binary.cols() - safeX);
            int safeH = Math.min(roiH, binary.rows() - safeY);

            Rect roi = new Rect(safeX, safeY, safeW, safeH);

            procMat = new Mat(binary, roi);
            targetMat = new Mat(drawTarget, roi);

            Imgproc.rectangle(drawTarget, roi.tl(), roi.br(), new Scalar(255, 0, 0), 1);
        }

        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(procMat, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        int totalRegions = contours.size();
        int validRegions = 0;

        for (MatOfPoint contour : contours) {
            double area = Imgproc.contourArea(contour);

            if (area >= minArea && area <= maxArea) {
                validRegions++;

                Rect rect = Imgproc.boundingRect(contour);

                if (drawBox) {
                    Imgproc.rectangle(targetMat, rect.tl(), rect.br(), new Scalar(0, 255, 0), 2);
                }

                if (drawCentroid) {
                    Moments moments = Imgproc.moments(contour);
                    if (moments.get_m00() != 0) {
                        int cx = (int) (moments.get_m10() / moments.get_m00());
                        int cy = (int) (moments.get_m01() / moments.get_m00());
                        Imgproc.circle(targetMat, new Point(cx, cy), 4, new Scalar(0, 0, 255), -1);
                    }
                }

                if (drawAreaText) {
                    String text = String.format("%.0f", area);
                    Imgproc.putText(targetMat, text, new Point(rect.x, rect.y - 5),
                            Imgproc.FONT_HERSHEY_SIMPLEX, 0.4, new Scalar(0, 255, 255), 1);
                }
            }
        }

        String resultString = "Total Detected Blobs: " + totalRegions + "\nValid (Filtered) Blobs: " + validRegions;

        BufferedImage resultImg = mat2Img(drawTarget);

        return new OperationResult(resultImg, resultString, null, null);    }

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

    @Override public String getOperationName() { return "Blob Counter"; }
    @Override public OperationType getOperationType() { return OperationType.BLOB_COUNTER; }
    @Override public int getOperationId() { return OperationType.BLOB_COUNTER.getOperationId(); }
    @Override public String toJson() { return "{ \"operationId\": " + getOperationId() + " }"; }
    public static BlobCounterOp fromJson(String json) { return new BlobCounterOp(); }
}