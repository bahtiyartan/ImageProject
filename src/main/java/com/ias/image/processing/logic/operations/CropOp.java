package com.ias.image.processing.logic.operations;

import java.awt.image.BufferedImage;

@SuppressWarnings("serial")
public class CropOp implements ImageOperation {

	private int x, y, width, height;

	public CropOp(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	@Override
	public BufferedImage apply(BufferedImage img) {
		int actualX = Math.max(0, Math.min(x, img.getWidth() - 1));
		int actualY = Math.max(0, Math.min(y, img.getHeight() - 1));
		int w = Math.max(1, Math.min(width, img.getWidth() - actualX));
		int h = Math.max(1, Math.min(height, img.getHeight() - actualY));

		return img.getSubimage(actualX, actualY, w, h);
	}

	@Override
	public String getOperationName() {
		return "Crop [" + x + "," + y + " " + width + "x" + height + "]";
	}
	
	@Override
	public OperationType getOperationType() {
		return OperationType.CROP;
	}
	//Writing to JSON
	@Override
	public String toJson() {
		StringBuilder json = new StringBuilder();
		json.append("{\n");
		json.append("\"operationType\": \"").append(getOperationType().name()).append("\",\n");
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
	// Creating objects from JSON
	public static CropOp fromJson(String json) {
		int x = Integer.parseInt(extractField(json, "x"));
		int y = Integer.parseInt(extractField(json, "y"));
		int width = Integer.parseInt(extractField(json, "width"));
		int height = Integer.parseInt(extractField(json, "height"));
		return new CropOp(x, y, width, height);
	}

	// Extracting data from JSON.
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
