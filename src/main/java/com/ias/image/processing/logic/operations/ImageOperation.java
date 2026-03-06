package com.ias.image.processing.logic.operations;

import java.awt.image.BufferedImage;

public interface ImageOperation extends java.io.Serializable {

	public BufferedImage apply(BufferedImage input);

	public String getOperationName();
	
	public OperationType getOperationType();
}
