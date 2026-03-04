package com.ias.image.processing.logic.operations;

import java.awt.image.BufferedImage;
import java.io.Serializable;

public interface ImageOperation extends java.io.Serializable {
        BufferedImage apply(BufferedImage input);
        String getOperationName();
}
