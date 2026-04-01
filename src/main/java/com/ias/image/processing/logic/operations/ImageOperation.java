package com.ias.image.processing.logic.operations;

public interface ImageOperation extends java.io.Serializable {

	OperationResult apply(OperationResult input);

	DataType getInputType();
	DataType getOutputType();
	String getOperationName();
	OperationType getOperationType();
	int getOperationId();
	String toJson();

    boolean isActive();
    void setActive(boolean active);
}


