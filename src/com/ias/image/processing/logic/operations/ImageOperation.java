package com.ias.image.processing.logic.operations;

import java.awt.image.BufferedImage;

public interface ImageOperation {
        BufferedImage apply(BufferedImage input);
        String getOperationName();
}
