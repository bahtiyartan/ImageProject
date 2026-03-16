package com.ias.image.processing.logic.operations;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class ProjectData implements Serializable {

	private List<ImageOperation> operations;

	public ProjectData(byte[] imageData, List<ImageOperation> operations) {
		this.operations = operations;
	}

	public List<ImageOperation> getOperations() {
		return operations;
	}
}