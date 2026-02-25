package com.ias.image.processing.logic.operations;

import java.awt.image.BufferedImage;

public class CropOp implements ImageOperation {
    private int x, y, width, height;

    public CropOp(int x, int y, int width, int height) {
        this.x = x; this.y = y;
        this.width = width; this.height = height;
    }

    @Override
    public BufferedImage apply(BufferedImage img) {
        int actualX = Math.max(0, Math.min(x, img.getWidth() - 1));
        int actualY = Math.max(0, Math.min(y, img.getHeight() - 1));
        int w = Math.max(1, Math.min(width, img.getWidth() - actualX));
        int h = Math.max(1, Math.min(height, img.getHeight() - actualY));

        return img.getSubimage(actualX, actualY, w, h);
    }

    @Override
    public String getOperationName() {
        return "Crop [" + x + "," + y + " " + width + "x" + height + "]";
    }
}