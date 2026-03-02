package com.ias.image.processing.logic.operations;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.CvType;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class GaussianBlurOp implements ImageOperation {
    private final int kernelSize;

    public GaussianBlurOp(int kernelSize) {
        this.kernelSize = (kernelSize % 2 == 0) ? kernelSize + 1 : kernelSize;
    }

    @Override
    public BufferedImage apply(BufferedImage img) {
        Mat mat = bufferedImageToMat(img);
        Mat blurredMat = new Mat();

        Imgproc.GaussianBlur(mat, blurredMat, new Size(kernelSize, kernelSize), 0);

        return matToBufferedImage(blurredMat);
    }

    private Mat bufferedImageToMat(BufferedImage bi) {
        Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
        byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
        mat.put(0, 0, data);
        return mat;
    }

    private BufferedImage matToBufferedImage(Mat mat) {
        int bufferSize = mat.channels() * mat.cols() * mat.rows();
        byte[] b = new byte[bufferSize];
        mat.get(0, 0, b);
        BufferedImage image = new BufferedImage(mat.cols(), mat.rows(), BufferedImage.TYPE_3BYTE_BGR);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(b, 0, targetPixels, 0, b.length);
        return image;
    }

    @Override
    public String getOperationName() {
        return "Gaussian Blur (" + kernelSize + "x" + kernelSize + ")";
    }
}