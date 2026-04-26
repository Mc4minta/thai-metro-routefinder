package com.routefinder.model;

public class Station {
    public String id;
    public String name_th;
    public String name_en;
    public String line;

    @Override
    public String toString() {
        return "(" + id + ") " + name_th + " | " + name_en;
    }
}
