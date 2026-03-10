package com.ias.image.processing.logic.operations;

public enum OperationType {

	CROP(1), //
	ROTATE(2), //
	TILE(3), //
	GAUSSIANBLUR(4);//

	private  final int OperationId;

	private OperationType(int id) {this.OperationId = id;}

	public int getOperationId() {
		return this.OperationId;
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

