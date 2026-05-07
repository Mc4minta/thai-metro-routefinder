package com.routefinder.controller;

import com.routefinder.model.Line;
import com.routefinder.model.PathEdge;
import com.routefinder.model.Station;
import com.routefinder.service.FareService;
import com.routefinder.service.StationDataService;
import com.routefinder.view.MainFrame;

import java.util.ArrayList;
import java.util.List;

public class RouteController {

    private final StationDataService dataService;
    private final FareService fareService;
    private MainFrame mainFrame;

    public RouteController(StationDataService dataService, FareService fareService) {
        this.dataService = dataService;
        this.fareService = fareService;
    }

    public void setMainFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    public List<Line> getLines() {
        return dataService.getLines();
    }

    public List<Station> getStationsByLine(String lineCode) {
        return dataService.getStationsByLine(lineCode);
    }

    public void onCalculateRoute(Station start, Station end) {
        if (start == null || end == null)
            return;

        // Use real Dijkstra result from FareService
        List<PathEdge> path = fareService.calculateRoute(start, end);

        if (mainFrame != null) {
            if (path == null || path.isEmpty()) {
                mainFrame.showNoRoute();
                return;
            }

            // Group edges for UI display
            List<Object[]> rows = formatForUI(path);

            // Calculate total fare from raw path edges
            double totalFare = 0;
            for (PathEdge edge : path) {
                totalFare += edge.cost;
            }

            mainFrame.setResult(rows, totalFare, start.name_th, end.name_th);
            mainFrame.showResult();
        }
    }


    private List<Object[]> formatForUI(List<PathEdge> path) {
        List<Object[]> rows = new ArrayList<>();
        if (path.isEmpty())
            return rows;

        PathEdge currentGroupEdge = null;
        String groupStartName = null;
        String groupEndName = null;
        double groupCost = 0;

        for (PathEdge edge : path) {
            if (edge.isInterchange) {
                if (currentGroupEdge != null) {
                    groupEndName = currentGroupEdge.to.name_th;
                    rows.add(new Object[] {
                            "STEP",
                            groupStartName,
                            groupEndName,
                            currentGroupEdge.line,
                            String.format("%.2f", groupCost)
                    });
                }
                rows.add(new Object[] {
                        "TRANSFER",
                        edge.from.line,
                        edge.to.line,
                        groupEndName != null ? groupEndName : edge.from.name_th,
                        edge.to.name_th
                });
                currentGroupEdge = null;
                groupStartName = null;
                groupEndName = null;
                groupCost = 0;
                continue;
            }

            if (isCrossBtsEdge(edge)) {
                if (currentGroupEdge != null) {
                    rows.add(new Object[] {
                            "STEP",
                            groupStartName,
                            currentGroupEdge.to.name_th,
                            currentGroupEdge.line,
                            String.format("%.2f", groupCost)
                    });
                    currentGroupEdge = null;
                    groupStartName = null;
                    groupEndName = null;
                    groupCost = 0;
                }

                rows.add(new Object[] {
                        "STEP",
                        edge.from.name_th,
                        centralStationId(edge.from.line),
                        edge.from.line,
                        String.format("%.2f", edge.cost)
                });
                rows.add(new Object[] {
                        "STEP",
                        centralStationId(edge.to.line),
                        edge.to.name_th,
                        edge.to.line,
                        "0.00"
                });
                continue;
            }

            if (currentGroupEdge == null) {
                currentGroupEdge = edge;
                groupStartName = edge.from.name_th;
                groupCost = edge.cost;
            } else if (edge.line.equals(currentGroupEdge.line)) {
                // Same line, continue grouping
                currentGroupEdge = edge; // update the "to" station of the group
                groupCost += edge.cost;
            } else {
                // Line changed, flush previous group
                rows.add(new Object[] {
                        "STEP",
                        groupStartName,
                        currentGroupEdge.to.name_th,
                        currentGroupEdge.line,
                        String.format("%.2f", groupCost)
                });
                // Start new group
                currentGroupEdge = edge;
                groupStartName = edge.from.name_th;
                groupEndName = null;
                groupCost = edge.cost;
            }
        }
        // Flush last group
        if (currentGroupEdge != null) {
            groupEndName = currentGroupEdge.to.name_th;
            rows.add(new Object[] {
                    "STEP",
                    groupStartName,
                    groupEndName,
                    currentGroupEdge.line,
                    String.format("%.2f", groupCost)
            });
        }

        return rows;
    }

    private boolean isCrossBtsEdge(PathEdge edge) {
        if (edge == null || edge.isInterchange) {
            return false;
        }
        boolean fromBts = "DG".equals(edge.from.line) || "LG".equals(edge.from.line);
        boolean toBts = "DG".equals(edge.to.line) || "LG".equals(edge.to.line);
        return fromBts && toBts && !edge.from.line.equals(edge.to.line);
    }

    private String centralStationId(String lineCode) {
        if ("DG".equals(lineCode)) {
            return "CEN_DG";
        }
        if ("LG".equals(lineCode)) {
            return "CEN_LG";
        }
        return "";
    }

    public java.awt.Color getLineColor(String lineCode) {
        for (Line line : dataService.getLines()) {
            if (line.code.equals(lineCode)) {
                return line.color;
            }
        }
        return java.awt.Color.GRAY;
    }


    public void onBackToInput() {
        if (mainFrame != null) {
            mainFrame.showInput();
        }
    }
}
