package com.ias.image.processing.logic.operations;

import java.awt.*;
import java.awt.image.BufferedImage;

public class RotateOp implements ImageOperation {
    private final double angle;

    public RotateOp(double angle) {
        this.angle = angle;
    }

    @Override
    public BufferedImage apply(BufferedImage img) {
        double radians = Math.toRadians(angle);
        double sin = Math.abs(Math.sin(radians));
        double cos = Math.abs(Math.cos(radians));
        int w = img.getWidth();
        int h = img.getHeight();

        int newWidth = (int) Math.floor(w * cos + h * sin);
        int newHeight = (int) Math.floor(h * cos + w * sin);

        BufferedImage rotated = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = rotated.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.translate((newWidth - w) / 2, (newHeight - h) / 2);

        g2d.rotate(radians, w / 2.0, h / 2.0);

        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();

        return rotated;
    }
    @Override
    public String getOperationName() {
        return "Rotate (" + angle + "°)";
    }
}