package com.ias.image.processing.logic;

import com.ias.image.processing.logic.operations.ImageOperation;
import java.io.Serializable;
import java.util.List;

public class ProjectData implements Serializable {
    private byte[] imageData;
    private List<ImageOperation> operations;

    public ProjectData(byte[] imageData, List<ImageOperation> operations){
        this.imageData=imageData;
        this.operations=operations;
    }

    public byte[] getImageData() { return imageData;}
    public List<ImageOperation> getOperations(){ return operations;}
}
