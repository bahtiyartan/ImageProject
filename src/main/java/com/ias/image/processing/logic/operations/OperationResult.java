package com.ias.image.processing.logic.operations;
import java.awt.image.BufferedImage;

public class OperationResult {
    private BufferedImage imageResult;
    private String stringResult;
    private Double doubleResult;
    private Integer intResult;

    public OperationResult(BufferedImage image, String str, Double dbl, Integer i) {
        this.imageResult = image;
        this.stringResult = str;
        this.doubleResult = dbl;
        this.intResult = i;
    }
    public BufferedImage getImageResult() { return imageResult; }
    public String getStringResult() { return stringResult; }
    public Double getDoubleResult() { return doubleResult; }
    public Integer getIntResult() { return intResult; }

    public boolean hasImage() { return imageResult != null; }
    public boolean hasString() { return stringResult != null; }
    public boolean hasDouble() { return doubleResult != null; }
    public boolean hasInteger() { return intResult != null; }

}