package com.routefinder.model;

import java.awt.Color;

public class Line {
    public String code;
    public String name_th;
    public String name_en;
    public transient Color color;

    @Override
    public String toString() {
        return name_th + " | " + name_en;
    }
}
