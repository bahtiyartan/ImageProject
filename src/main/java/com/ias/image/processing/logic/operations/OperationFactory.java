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

        case THRESHOLD:
            return new ThresholdOp();

        case HISTOGRAM_EQ:
            return new HistogramEqualizationOp();

        case CONTRAST_STRETCH:
            return new ContrastStretchingOp();

        case KMEANS:
                return new KMeansOp();
		}
		return new RotateOp();
	}
}
