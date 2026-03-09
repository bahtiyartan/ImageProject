package com.ias.image.processing.logic.operations;

import java.io.Serializable;
import java.util.List;

public class ProjectData implements Serializable {

	private List<ImageOperation> operations;
	private byte[] imageData;

	public ProjectData(byte[] imageData, List<ImageOperation> operations) {
		this.imageData = imageData;
		this.operations = operations;
	}

	public byte[] getImageData() {
		return imageData;
	}

	public List<ImageOperation> getOperations() {
		return operations;
	}
}
