package com.routefinder.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.routefinder.model.Line;
import com.routefinder.model.Station;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class StationDataService {

    private List<Line> lines = new ArrayList<>();
    private List<Station> stations = new ArrayList<>();

    public StationDataService() {
        loadData();
    }

    private void loadData() {
        try (Reader reader = new InputStreamReader(getClass().getResourceAsStream("/data.json"))) {
            Gson gson = new Gson();
            JsonObject data = gson.fromJson(reader, JsonObject.class);
            
            JsonArray linesArray = data.getAsJsonArray("lines");
            for (JsonElement element : linesArray) {
                lines.add(gson.fromJson(element, Line.class));
            }
            
            JsonArray stationsArray = data.getAsJsonArray("stations");
            for (JsonElement element : stationsArray) {
                stations.add(gson.fromJson(element, Station.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Line> getLines() {
        return lines;
    }

    public List<Station> getStationsByLine(String lineCode) {
        List<Station> filtered = new ArrayList<>();
        for (Station s : stations) {
            if (s.line.equals(lineCode)) {
                filtered.add(s);
            }
        }
        return filtered;
    }
}
