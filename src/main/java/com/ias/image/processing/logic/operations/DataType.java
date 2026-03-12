package com.ias.image.processing.logic.operations;

public enum DataType {
    IMAGE(1),
    INTEGER(2),
    STRING(3),
    DOUBLE(4),
    NONE(0);

    private final int id;

    DataType(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }
}