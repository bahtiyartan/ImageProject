package com.ias.image.processing.logic.operations;

public enum OperationType {

	CROP(1, "Crop"),
	ROTATE(2, "Rotate"),
	TILE(3, "Tile"),
	GAUSSIANBLUR(4, "Gaussian Blur"),
	COLOR_HISTOGRAM(5, "Color Histogram"),
	GRAYSCALE(6,"GrayScale"),
    THRESHOLD(7, "Threshold"),
    HISTOGRAM_EQ(8, "Histogram Equalization"),
    CONTRAST_STRETCH(9, "Contrast Stretching");


	private final int OperationId;
	private final String Description;

	private OperationType(int id, String description) {
		this.OperationId = id;
		this.Description = description;
	}

	public int getOperationId() {
		return this.OperationId;
	}

	public String getDescription() {
		return this.Description;
	}

	public static OperationType fromId(int id) {
		for (OperationType type : values()) {
			if (type.getOperationId() == id) {
				return type;
			}
		}
		return null;
	}
}