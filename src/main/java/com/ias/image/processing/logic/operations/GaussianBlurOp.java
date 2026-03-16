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

	public GaussianBlurOp() {
		this(9,1,org.opencv.core.Core.BORDER_CONSTANT);
	}

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
	public DataType getInputType() {
		return DataType.IMAGE;
	}

	@Override
	public DataType getOutputType() {
		return DataType.IMAGE;
	}

	@Override
	public OperationResult apply(OperationResult input) {
		if (input == null || !input.hasImage()) {
			throw new IllegalArgumentException("Gaussian Blur operation requires an IMAGE input");
		}

		BufferedImage img = input.getImageResult();

		BufferedImage bgrImage = convertTo3ByteBGR(img);
		Mat mat = bufferedImageToMat(bgrImage);
		Mat blurredMat = new Mat();

		Imgproc.GaussianBlur(mat, blurredMat, new Size(kernelSize, kernelSize), sigmaX, 0, borderType);

		BufferedImage blurred = matToBufferedImage(blurredMat);

		String info = "GaussianBlur applied with kernel=" + kernelSize + ", sigmaX=" + sigmaX;
		Double sigmaValue = sigmaX;

		return new OperationResult(blurred, info, sigmaValue, null);
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

	@Override
	public int getOperationId() {
		return OperationType.GAUSSIANBLUR.getOperationId();
	}

	@Override
	public String toJson() {
		StringBuilder json = new StringBuilder();
		json.append("{\n");
		json.append("\"operationId\": ").append(getOperationType().getOperationId()).append(",\n");
		json.append("\"operationName\": \"").append(getOperationName()).append("\",\n");
		json.append("\"params\": {\n");
		json.append("\"kernelSize\": ").append(kernelSize).append(",\n");
		json.append("\"sigmaX\": ").append(sigmaX).append(",\n");
		json.append("\"borderType\": ").append(borderType).append("\n");
		json.append("}\n");
		json.append("}");
		return json.toString();
	}

	public static GaussianBlurOp fromJson(String json) {
		int kernelSize = Integer.parseInt(extractField(json, "kernelSize"));
		double sigmaX = Double.parseDouble(extractField(json, "sigmaX"));
		int borderType = Integer.parseInt(extractField(json, "borderType"));
		return new GaussianBlurOp(kernelSize, sigmaX, borderType);
	}

	private static String extractField(String json, String field) {
		int idx = json.indexOf("\"" + field + "\"");
		if (idx == -1)
			return null;
		int colon = json.indexOf(":", idx);
		int comma = json.indexOf(",", colon);
		int endBrace = json.indexOf("}", colon);
		int end = (comma == -1) ? endBrace : Math.min(comma, endBrace);
		String value = json.substring(colon + 1, end).trim();
		return value.replace("\"", "");
	}
}