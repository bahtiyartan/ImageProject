package com.ias.image.processing.logic.operations;
import java.io.Serializable;
import java.awt.*;
import java.awt.image.BufferedImage;

public class TileOp implements ImageOperation, Serializable {
    private final int countX;
    private final int countY;
    private final int spacingX;
    private final int spacingY;

    public TileOp(int countX, int countY, int spacingX, int spacingY) {
        this.countX = Math.max(1, countX);
        this.countY = Math.max(1, countY);
        this.spacingX = Math.max(0, spacingX);
        this.spacingY = Math.max(0, spacingY);
    }

    public int getCountX() { return countX;}
    public int getCountY() {return countY;}
    public int getSpacingX() {return spacingX;}
    public int getSpacingY() {return spacingY;}

    @Override
    public BufferedImage apply(BufferedImage img) {
        int w = img.getWidth();
        int h = img.getHeight();

        int finalWidth = (w * countX) + (spacingX * (countX - 1));
        int finalHeight = (h * countY) + (spacingY * (countY - 1));

        BufferedImage newImg = new BufferedImage(finalWidth, finalHeight, img.getType());
        Graphics2D g = newImg.createGraphics();

        for (int i = 0; i < countX; i++) {
            for (int j = 0; j < countY; j++) {
                int posX = i * (w + spacingX);
                int posY = j * (h + spacingY);
                g.drawImage(img, posX, posY, null);
            }
        }
        g.dispose();

        return newImg;
    }

    @Override
    public String getOperationName() {
        return "Tile (Grid: " + countX + "x" + countY + ", Spacing: " + spacingX + "," + spacingY + ")";
    }
}