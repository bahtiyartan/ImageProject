package com.ias.image.processing.logic.operations;

public class OperationFactory {

	public static ImageOperation createOperation(OperationType type) {

		switch (type) {
		case CROP:
			return new CropOp();

		case ROTATE:
			return new RotateOp();

		case TILE:
			return new TileOp();

		case GAUSSIANBLUR:
			return new GaussianBlurOp();

		case COLOR_HISTOGRAM:
			return new ColorHistogramOp();

		case GRAYSCALE:
			return new GrayscaleOp();

		}
		return new RotateOp();
	}
}
