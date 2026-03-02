package com.ias.image.processing.logic.operations;

import java.awt.*;
import java.awt.image.BufferedImage;

public class TileOp implements ImageOperation {
    private int count;

    public TileOp(int count) {
        this.count = count;
    }

    @Override
    public BufferedImage apply(BufferedImage img) {
        int w = img.getWidth();
        int h = img.getHeight();
        BufferedImage newImg = new BufferedImage(w * count, h, img.getType());
        Graphics2D g = newImg.createGraphics();

        for (int i = 0; i < count; i++) {
            g.drawImage(img, i * w, 0, null);
        }
        g.dispose();

        return newImg;
    }

    @Override
    public String getOperationName() {
        return "Tile (" + count + "x)";
    }
}