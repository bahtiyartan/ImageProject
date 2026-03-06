package com.ias.image.processing.logic.operations;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

@SuppressWarnings("serial")
public class GaussianBlurOp implements ImageOperation {

	private final int kernelSize;
	private final double sigmaX;
	private final int borderType;

	public GaussianBlurOp(int kernelSize, double sigmaX, int borderType) {
		this.kernelSize = (kernelSize % 2 == 0) ? kernelSize + 1 : Math.max(1, kernelSize);
		this.sigmaX = sigmaX;
		this.borderType = borderType;
	}

	public int getKernelSize() {
		return kernelSize;
	}

	public double getSigmaX() {
		return sigmaX;
	}

	public int getBorderType() {
		return borderType;
	}

	@Override
	public BufferedImage apply(BufferedImage img) {
		BufferedImage bgrImage = convertTo3ByteBGR(img);
		Mat mat = bufferedImageToMat(bgrImage);
		Mat blurredMat = new Mat();

		Imgproc.GaussianBlur(mat, blurredMat, new Size(kernelSize, kernelSize), sigmaX, 0, borderType);

		return matToBufferedImage(blurredMat);
	}

	private BufferedImage convertTo3ByteBGR(BufferedImage img) {
		if (img.getType() == BufferedImage.TYPE_3BYTE_BGR) {
			return img;
		}
		BufferedImage convertedImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D g2d = convertedImg.createGraphics();
		g2d.drawImage(img, 0, 0, null);
		g2d.dispose();
		return convertedImg;
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
		return "Gaussian Blur (K:" + kernelSize + ", sX:" + sigmaX + ", B:" + getBorderName(borderType) + ")";
	}

	private String getBorderName(int type) {
		switch (type) {
		case org.opencv.core.Core.BORDER_CONSTANT:
			return "CONSTANT";
		case org.opencv.core.Core.BORDER_REPLICATE:
			return "REPLICATE";
		case org.opencv.core.Core.BORDER_REFLECT:
			return "REFLECT";
		default:
			return "DEFAULT";
		}
	}

	@Override
	public OperationType getOperationType() {
		return OperationType.GAUSSIANBLUR;
	}
}