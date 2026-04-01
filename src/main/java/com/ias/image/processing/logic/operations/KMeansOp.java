package com.ias.image.processing.logic.operations;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.TermCriteria;
import org.opencv.imgproc.Imgproc;

@SuppressWarnings("serial")
public class KMeansOp implements ImageOperation {

    private int kValue;
    private String colorSpace; // BGR or LAB
    private int maxIter;
    private double epsilon;
    private boolean active = true;

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    public KMeansOp() {
        this.kValue = 4;
        this.colorSpace = "LAB";
        this.maxIter = 100;
        this.epsilon = 0.2;
    }

    public int getKValue() { return kValue; }
    public String getColorSpace() { return colorSpace; }
    public int getMaxIter() { return maxIter; }
    public double getEpsilon() { return epsilon; }

    public void updateOperation(int kValue, String colorSpace, int maxIter, double epsilon) {
        this.kValue = Math.max(2, kValue);
        this.colorSpace = colorSpace;
        this.maxIter = Math.max(10, maxIter);
        this.epsilon = Math.max(0.01, epsilon);
    }

    @Override
    public DataType getInputType() { return DataType.IMAGE; }

    @Override
    public DataType getOutputType() { return DataType.IMAGE; }

    @Override
    public OperationResult apply(OperationResult input) {
        if (input == null || !input.hasImage()) {
            throw new IllegalArgumentException("K-Means requires an IMAGE input");
        }

        BufferedImage img = input.getImageResult();
        Mat src = img2Mat(img);
        Mat processingMat = new Mat();

        if ("LAB".equals(colorSpace)) {
            Imgproc.cvtColor(src, processingMat, Imgproc.COLOR_BGR2Lab);
        } else {
            src.copyTo(processingMat);
        }

        Mat reshaped = processingMat.reshape(1, processingMat.rows() * processingMat.cols());
        Mat reshaped32f = new Mat();
        reshaped.convertTo(reshaped32f, CvType.CV_32F);

        Mat labels = new Mat();
        Mat centers = new Mat();
        TermCriteria criteria = new TermCriteria(TermCriteria.EPS + TermCriteria.MAX_ITER, maxIter, epsilon);
        Core.kmeans(reshaped32f, kValue, labels, criteria, 3, Core.KMEANS_PP_CENTERS, centers);

        int totalPixels = (int) labels.total();
        int[] labelArr = new int[totalPixels];
        labels.get(0, 0, labelArr);

        float[] centerArr = new float[(int) centers.total()];
        centers.get(0, 0, centerArr);

        float[] resultArr = new float[totalPixels * 3];

        for (int i = 0; i < totalPixels; i++) {
            int clusterIdx = labelArr[i];
            resultArr[i * 3] = centerArr[clusterIdx * 3];
            resultArr[i * 3 + 1] = centerArr[clusterIdx * 3 + 1];
            resultArr[i * 3 + 2] = centerArr[clusterIdx * 3 + 2];
        }

        Mat result32f = new Mat(reshaped32f.rows(), reshaped32f.cols(), CvType.CV_32F);
        result32f.put(0, 0, resultArr);

        Mat dst = new Mat();
        result32f.convertTo(dst, CvType.CV_8U);
        dst = dst.reshape(3, processingMat.rows());

        if ("LAB".equals(colorSpace)) {
            Imgproc.cvtColor(dst, dst, Imgproc.COLOR_Lab2BGR);
        }

        BufferedImage resultImg = mat2Img(dst);
        return new OperationResult(resultImg, "K-Means (K=" + kValue + ")", null, null);
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
    public String getOperationName() { return "K-Means (K=" + kValue + ")"; }
    @Override
    public OperationType getOperationType() { return OperationType.KMEANS; }
    @Override
    public int getOperationId() { return OperationType.KMEANS.getOperationId(); }
    @Override
    public String toJson() { return "{ \"operationId\": " + getOperationId() + " }"; }

    public static KMeansOp fromJson(String json) { return new KMeansOp(); }
}