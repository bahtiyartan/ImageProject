package com.ias.image.processing.logic.operations;

import java.awt.*;
import java.awt.image.BufferedImage;

@SuppressWarnings("serial")
public class RotateOp implements ImageOperation {

	private  double angle;
	private  String hintName;
	private Object interpolationHint;
	
	
	public RotateOp() {
		this(1.0, java.awt.RenderingHints.VALUE_INTERPOLATION_BICUBIC, "");
	}

	public RotateOp(double angle, Object interpolationHint, String hintName) {
		this.angle = angle;
		this.interpolationHint = interpolationHint;
		this.hintName = hintName;
	}

	public double getAngle() {
		return angle;
	}

	public Object getInterpolationHint() {
		return interpolationHint;
	}

	public String getHintName() {
		return hintName;
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
			throw new IllegalArgumentException("Rotate operation requires an IMAGE input");
		}

		BufferedImage img = input.getImageResult();

		double radians = Math.toRadians(angle);
		double sin = Math.abs(Math.sin(radians));
		double cos = Math.abs(Math.cos(radians));
		int w = img.getWidth();
		int h = img.getHeight();

		int newWidth = (int) Math.floor(w * cos + h * sin);
		int newHeight = (int) Math.floor(h * cos + w * sin);

		BufferedImage rotated = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = rotated.createGraphics();

		Object finalHint = (interpolationHint != null) ? interpolationHint : RenderingHints.VALUE_INTERPOLATION_BICUBIC;

		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, finalHint);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g2d.translate((newWidth - w) / 2, (newHeight - h) / 2);
		g2d.rotate(radians, w / 2.0, h / 2.0);
		g2d.drawImage(img, 0, 0, null);

		g2d.dispose();

		String info = "Rotated " + angle + " degrees, Quality: " + hintName;
		Double angleValue = angle;

		return new OperationResult(rotated, info, angleValue, null);
	}

	@Override
	public String getOperationName() {
		return "Rotate (" + angle + "°, Quality: " + hintName + ")";
	}

	@Override
	public OperationType getOperationType() {
		return OperationType.ROTATE;
	}

	@Override
	public int getOperationId() {
		return OperationType.ROTATE.getOperationId();
	}

	@Override
	public String toJson() {
		StringBuilder json = new StringBuilder();
		json.append("{\n");
		json.append("\"operationId\": ").append(getOperationType().getOperationId()).append(",\n");
		json.append("\"operationName\": \"").append(getOperationName()).append("\",\n");
		json.append("\"params\": {\n");
		json.append("\"angle\": ").append(angle).append(",\n");
		json.append("\"hintName\": \"").append(hintName).append("\"\n");
		json.append("}\n");
		json.append("}");
		return json.toString();
	}

	public static RotateOp fromJson(String json) {
		double angle = Double.parseDouble(extractField(json, "angle"));
		String hintName = extractField(json, "hintName");
		return new RotateOp(angle, null, hintName);
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
	}

	public void updateOperation(double newAngle,Object newHintObject, String newHintName){
		this.angle= newAngle;
		this.interpolationHint = newHintObject;
		this.hintName = newHintName;
	}
}