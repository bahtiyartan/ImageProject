package com.ias.image.processing.logic;

import com.ias.image.processing.logic.operations.ImageOperation;
import com.ias.image.processing.logic.operations.OperationResult;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class ImageModel {

	private BufferedImage originalImage;
	private BufferedImage currentImage;
	private OperationResult currentResult;

	private final List<ImageOperation> operations = new ArrayList<>();

	public void setOriginalImage(BufferedImage img) {
		this.originalImage = img;
	}

	public BufferedImage getOriginalImage() {
		return originalImage;
	}

	public void setCurrentResult(OperationResult result) {
		this.currentResult = result;
	}

	public OperationResult getCurrentResult() {
		return currentResult;
	}

	public void setCurrentImage(BufferedImage img) {
		this.currentImage = img;
	}

	public BufferedImage getCurrentImage() {
		return currentImage;
	}

	public void addOperation(ImageOperation op) {
		operations.add(op);
	}

	public List<ImageOperation> getOperations() {
		return operations;
	}
}