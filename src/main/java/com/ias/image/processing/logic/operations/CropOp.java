package com.ias.image.processing.logic.operations;

import java.awt.image.BufferedImage;

@SuppressWarnings("serial")
public class CropOp implements ImageOperation {

	private final int x, y, width, height;

	public CropOp(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	public int getX() { return x; }
	public int getY() { return y; }
	public int getWidth() { return width; }
	public int getHeight() { return height; }

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
			throw new IllegalArgumentException("Crop operation requires an IMAGE input");
		}
		BufferedImage img = input.getImageResult();

		int actualX = Math.max(0, Math.min(x, img.getWidth() - 1));
		int actualY = Math.max(0, Math.min(y, img.getHeight() - 1));
		int w = Math.max(1, Math.min(width, img.getWidth() - actualX));
		int h = Math.max(1, Math.min(height, img.getHeight() - actualY));

		BufferedImage cropped = img.getSubimage(actualX, actualY, w, h);

		String info = "Cropped area: " + width + "x" + height;
		Double area = (double) width * height;

		return new OperationResult(cropped, info, area, null);
	}

	@Override
	public int getOperationId() {
		return OperationType.CROP.getOperationId();
	}

	@Override
	public String getOperationName() {
		return "Crop [" + x + "," + y + " " + width + "x" + height + "]";
	}

	@Override
	public OperationType getOperationType() {
		return OperationType.CROP;
	}
	@Override
	public String toJson() {
		StringBuilder json = new StringBuilder();
		json.append("{\n");
		json.append("\"operationId\": ").append(getOperationType().getOperationId()).append(",\n");
		json.append("\"operationName\": \"").append(getOperationName()).append("\",\n");
		json.append("\"params\": {\n");
		json.append("\"x\": ").append(x).append(",\n");
		json.append("\"y\": ").append(y).append(",\n");
		json.append("\"width\": ").append(width).append(",\n");
		json.append("\"height\": ").append(height).append("\n");
		json.append("}\n");
		json.append("}");
		return json.toString();
	}
	public static CropOp fromJson(String json) {
		int x = Integer.parseInt(extractField(json, "x"));
		int y = Integer.parseInt(extractField(json, "y"));
		int width = Integer.parseInt(extractField(json, "width"));
		int height = Integer.parseInt(extractField(json, "height"));
		return new CropOp(x, y, width, height);
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
}