package com.ias.image.processing.logic.operations;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.CvType;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class GrayscaleOp implements ImageOperation {

    @Override
    public BufferedImage apply(BufferedImage img) {
        // 1. BufferedImage'ı Mat'a çevir
        Mat mat = bufferedImageToMat(img);

        // 2. OpenCV Gri Tonlama Filtresi
        Mat grayMat = new Mat();
        Imgproc.cvtColor(mat, grayMat, Imgproc.COLOR_BGR2GRAY);

        // 3. Tekrar BufferedImage'a çevir
        return matToBufferedImage(grayMat);
    }

    private Mat bufferedImageToMat(BufferedImage bi) {
        Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
        byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
        mat.put(0, 0, data);
        return mat;
    }

    private BufferedImage matToBufferedImage(Mat mat) {
        int type = (mat.channels() > 1) ? BufferedImage.TYPE_3BYTE_BGR : BufferedImage.TYPE_BYTE_GRAY;
        int bufferSize = mat.channels() * mat.cols() * mat.rows();
        byte[] b = new byte[bufferSize];
        mat.get(0, 0, b);
        BufferedImage image = new BufferedImage(mat.cols(), mat.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(b, 0, targetPixels, 0, b.length);
        return image;
    }

    @Override
    public String getOperationName() {
        return "OpenCV: Grayscale";
    }
}