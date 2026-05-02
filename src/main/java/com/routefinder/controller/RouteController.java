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

        // Group edges for UI display
        List<Object[]> rows = formatForUI(path);

        // Calculate total fare from raw path edges
        double totalFare = 0;
        for (PathEdge edge : path) {
            totalFare += edge.cost;
        }

        if (mainFrame != null) {
            mainFrame.setResult(rows, totalFare);
            mainFrame.showResult();
        }
    }


    private List<Object[]> formatForUI(List<PathEdge> path) {
        List<Object[]> rows = new ArrayList<>();
        if (path.isEmpty())
            return rows;

        PathEdge currentGroupEdge = null;
        String groupStartName = null;
        double groupCost = 0;

        for (PathEdge edge : path) {
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
                        groupStartName,
                        currentGroupEdge.to.name_th,
                        currentGroupEdge.line,
                        String.format("%.2f", groupCost)
                });
                // Start new group
                currentGroupEdge = edge;
                groupStartName = edge.from.name_th;
                groupCost = edge.cost;
            }
        }
        // Flush last group
        if (currentGroupEdge != null) {
            rows.add(new Object[] {
                    groupStartName,
                    currentGroupEdge.to.name_th,
                    currentGroupEdge.line,
                    String.format("%.2f", groupCost)
            });
        }

        return rows;
    }

    public void onBackToInput() {
        if (mainFrame != null) {
            mainFrame.showInput();
        }
    }
}
