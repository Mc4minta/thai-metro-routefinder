package com.routefinder.model;

public class Station {
    public String id;
    public String name;
    public String line;

    @Override
    public String toString() {
        return " (" + id + ") " + name;
    }
}
