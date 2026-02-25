package com.ias.image.processing.logic;

import com.ias.image.processing.logic.operations.ImageOperation;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class ImageModel {
    private BufferedImage originalImage;
    private BufferedImage currentImage;
    private final List<ImageOperation> operations = new ArrayList<>();

    public void setOriginalImage(BufferedImage img) { this.originalImage = img; }
    public BufferedImage getOriginalImage() { return originalImage; }

    public void setCurrentImage(BufferedImage img) { this.currentImage = img; }
    public BufferedImage getCurrentImage() { return currentImage; }

    public void addOperation(ImageOperation op) { operations.add(op); }
    public List<ImageOperation> getOperations() { return operations; }
}