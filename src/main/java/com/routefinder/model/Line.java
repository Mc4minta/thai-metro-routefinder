package com.routefinder.model;

public class Line {
    public String code;
    public String name_th;
    public String name_en;

    @Override
    public String toString() {
        return name_th + " | " + name_en;
    }
}
