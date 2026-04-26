package com.routefinder.controller;

import com.routefinder.model.Line;
import com.routefinder.model.Station;
import com.routefinder.service.StationDataService;
import com.routefinder.view.MainFrame;

import java.util.List;

public class RouteController {
    
    private final StationDataService dataService;
    private MainFrame mainFrame;

    public RouteController(StationDataService dataService) {
        this.dataService = dataService;
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

    public void onCalculateRoute() {
        // Here we would implement the route finding logic, 
        // but for now, maintaining the existing functionality:
        if (mainFrame != null) {
            mainFrame.showResult();
        }
    }

    public void onBackToInput() {
        if (mainFrame != null) {
            mainFrame.showInput();
        }
    }
}
