package com.routefinder.model;

/**
 * Represents an edge in the calculated path.
 * Following the architectural logic suggested in logic-new.md
 */
public class PathEdge {
    public Station from;
    public Station to;
    public String line;
    public double cost;
    public boolean isInterchange;

    public PathEdge(Station from, Station to, String line, double cost) {
        this(from, to, line, cost, false);
    }

    public PathEdge(Station from, Station to, String line, double cost, boolean isInterchange) {
        this.from = from;
        this.to = to;
        this.line = line;
        this.cost = cost;
        this.isInterchange = isInterchange;
    }
}
