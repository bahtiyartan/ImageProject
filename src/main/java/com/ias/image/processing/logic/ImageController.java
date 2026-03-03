package com.ias.image.processing.logic;

import com.ias.image.processing.logic.operations.ImageOperation;
import java.awt.image.BufferedImage;
import java.util.List;

public class ImageController {
    private ImageModel model;
    private Runnable updateViewCallback;

    public ImageController(ImageModel model) {
        this.model = model;
    }

    public void setUpdateViewCallback(Runnable callback) {
        this.updateViewCallback = callback;
    }

    public void loadImage(BufferedImage img) {
        model.setOriginalImage(img);
        processImage();
    }

    public void addOperation(ImageOperation op) {
        model.addOperation(op);
        processImage();
    }

    private boolean cropModeActive = false;

    public void setCropModeActive(boolean active) {
        this.cropModeActive = active;
        if (updateViewCallback != null) updateViewCallback.run();
    }

    public boolean isCropModeActive() {
        return cropModeActive;
    }
    public void undo() {
        List<ImageOperation> ops = model.getOperations();
        if (!ops.isEmpty()) {
            ops.remove(ops.size() - 1);
            processImage();

        } else {
            System.out.println("There are no more actions to undo.");
        }
    }
    public void updateOperation(int index, ImageOperation newOp) {
        model.getOperations().set(index, newOp);
        processImage();
    }

    public void removeOperation(int index) {
        model.getOperations().remove(index);
        processImage();
    }

    private void processImage() {
        if (model.getOriginalImage() == null) return;

        BufferedImage result = model.getOriginalImage();
        try {
            for (ImageOperation op : model.getOperations()) {
                result = op.apply(result);
            }
        } catch (Exception e) {
            System.err.println("An error occurred while processing the image: " + e.getMessage());
            e.printStackTrace();
        }
        model.setCurrentImage(result);

        if (updateViewCallback != null) updateViewCallback.run();
    }

    public ImageModel getModel() { return model; }
}