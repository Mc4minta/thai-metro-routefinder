package com.routefinder.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.routefinder.model.Line;
import com.routefinder.model.Station;

import java.awt.Color;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class StationDataService {

    private List<Line> lines = new ArrayList<>();
    private List<Station> stations = new ArrayList<>();
    private final String dataSource = "/data.json";

    public StationDataService() {
        loadData();
        loadColors();
    }

    private void loadColors() {
        try (Reader reader = new InputStreamReader(getClass().getResourceAsStream("/color-code.json"))) {
            Gson gson = new Gson();
            JsonObject colorMap = gson.fromJson(reader, JsonObject.class);

            for (Line line : lines) {
                if (colorMap.has(line.code)) {
                    JsonArray rgb = colorMap.getAsJsonArray(line.code);
                    line.color = new Color(
                        rgb.get(0).getAsInt(),
                        rgb.get(1).getAsInt(),
                        rgb.get(2).getAsInt()
                    );
                } else {
                    line.color = Color.WHITE;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Default to white if loading fails
            for (Line line : lines) {
                if (line.color == null) line.color = Color.WHITE;
            }
        }
    }

    private void loadData() {
        try (Reader reader = new InputStreamReader(getClass().getResourceAsStream(dataSource))) {
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
