package com.routefinder.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.routefinder.model.PathEdge;
import com.routefinder.model.Station;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;
import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;

public class FareService {

    private final Map<String, Station> stationMap = new HashMap<>();
    private final Map<String, List<Station>> stationVariantsById = new HashMap<>();
    private final Map<Node, List<Edge>> adj = new HashMap<>();
    private final Map<String, Map<String, Double>> discounts = new HashMap<>();

    public FareService() {
        loadData();
    }

    private void addDiscount(String fromLine, String toLine, double amount) {
        discounts.computeIfAbsent(fromLine, k -> new HashMap<>()).put(toLine, amount);
    }

    private void loadData() {
        try (Reader reader = new InputStreamReader(getClass().getResourceAsStream("/data.json"))) {
            Gson gson = new Gson();
            JsonObject data = gson.fromJson(reader, JsonObject.class);

            // Load stations to map IDs to objects
            JsonArray stationsArray = data.getAsJsonArray("stations");
            for (JsonElement element : stationsArray) {
                Station s = gson.fromJson(element, Station.class);
                stationMap.put(s.id + "_" + s.line, s);
                stationVariantsById.computeIfAbsent(normalizeStationId(s.id), k -> new ArrayList<>()).add(s);
            }

            // Load edges
            JsonArray edgesArray = data.getAsJsonArray("edges");
            for (JsonElement element : edgesArray) {
                JsonObject obj = element.getAsJsonObject();
                String type = obj.get("type").getAsString();
                double price = obj.get("price").getAsDouble();

                if (type.equals("fare")) {
                    String fromId = obj.get("from").getAsString();
                    String toId = obj.get("to").getAsString();
                    String lineCode = obj.get("line").getAsString();

                    Node u = new Node(fromId, lineCode);
                    Node v = new Node(toId, lineCode);
                    adj.computeIfAbsent(u, k -> new ArrayList<>()).add(new Edge(v, price, false));
                } else if (type.equals("interchange")) {
                    String fromId = obj.get("from").getAsString();
                    String fromLine = obj.get("from_line").getAsString();
                    String toId = obj.get("to").getAsString();
                    String toLine = obj.get("to_line").getAsString();

                    Node u = new Node(fromId, fromLine);
                    Node v = new Node(toId, toLine);
                    adj.computeIfAbsent(u, k -> new ArrayList<>()).add(new Edge(v, 0.0, true));
                }
            }

            // Add direct BTS fares from the combined Light Green + Dark Green matrix.
            // This matrix covers the official BTS through-fare between stations on the
            // Sukhumvit and Silom lines, so we load it as extra direct edges to avoid
            // forcing the search through an unrelated Blue Line detour.
            loadDirectFareMatrix("/fare/price_bts_lightgreen_plus_darkgreen.csv");

            // Load discounts
            if (data.has("discounts")) {
                JsonObject discObj = data.getAsJsonObject("discounts");
                for (String fromLine : discObj.keySet()) {
                    JsonObject toLines = discObj.getAsJsonObject(fromLine);
                    for (String toLine : toLines.keySet()) {
                        addDiscount(fromLine, toLine, toLines.get(toLine).getAsDouble());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadDirectFareMatrix(String resourcePath) {
        try (Reader reader = new InputStreamReader(Objects.requireNonNull(
                getClass().getResourceAsStream(resourcePath)), StandardCharsets.UTF_8);
             BufferedReader bufferedReader = new BufferedReader(reader)) {

            String headerLine = bufferedReader.readLine();
            if (headerLine == null || headerLine.isBlank()) {
                return;
            }

            String[] headers = headerLine.split(",");
            List<String> targetIds = new ArrayList<>();
            for (int i = 1; i < headers.length; i++) {
                targetIds.add(headers[i].trim());
            }

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length < 2) {
                    continue;
                }

                String fromId = parts[0].trim();
                List<Station> fromStations = stationVariantsById.get(normalizeStationId(fromId));
                if (fromStations == null || fromStations.isEmpty()) {
                    continue;
                }

                for (int i = 1; i < parts.length && i <= targetIds.size(); i++) {
                    String priceText = parts[i].trim();
                    if (priceText.isEmpty()) {
                        continue;
                    }

                    double price;
                    try {
                        price = Double.parseDouble(priceText);
                    } catch (NumberFormatException ex) {
                        continue;
                    }

                    String toId = targetIds.get(i - 1);
                    List<Station> toStations = stationVariantsById.get(normalizeStationId(toId));
                    if (toStations == null || toStations.isEmpty()) {
                        continue;
                    }

                    for (Station fromStation : fromStations) {
                        for (Station toStation : toStations) {
                            if (!isBtsLine(fromStation.line) || !isBtsLine(toStation.line)) {
                                continue;
                            }

                            Node u = new Node(fromStation.id, fromStation.line);
                            Node v = new Node(toStation.id, toStation.line);
                            adj.computeIfAbsent(u, k -> new ArrayList<>()).add(new Edge(v, price, false));
                        }
                    }
                }
            }
        } catch (Exception e) {
            // If the matrix is unavailable, keep the app functional with the base graph.
            e.printStackTrace();
        }
    }

    private String normalizeStationId(String stationId) {
        if (stationId == null) {
            return "";
        }
        if (stationId.startsWith("CEN_")) {
            return "CEN";
        }
        return stationId;
    }

    private boolean isBtsLine(String lineCode) {
        return "DG".equals(lineCode) || "LG".equals(lineCode);
    }

    public List<PathEdge> calculateRoute(Station start, Station end) {
        Node startNode = new Node(start.id, start.line);
        Node endId = new Node(end.id, end.line); // Target can be any node with this station ID

        PriorityQueue<State> pq = new PriorityQueue<>(Comparator.comparingDouble(s -> s.cost));
        Map<Node, Map<String, Double>> dist = new HashMap<>();

        Station fullStart = getStation(startNode);
        Station fullEnd = getStation(endId);

        // 1. Same-station logic: if start and end are exactly the same physical station (same name)
        // Check for self-edge (e.g. PP01 -> PP01) to return the minimum entry/exit fare.
        if (fullStart != null && fullEnd != null && fullStart.name_en.equals(fullEnd.name_en)) {
            List<Edge> neighbors = adj.getOrDefault(startNode, Collections.emptyList());
            for (Edge e : neighbors) {
                if (e.to.equals(startNode) && !e.isInterchange) {
                    PathEdge selfEdge = new PathEdge(
                            fullStart,
                            fullEnd,
                            fullStart.line,
                            e.weight);
                    return Collections.singletonList(selfEdge);
                }
            }
        }

        pq.add(new State(startNode, 0.0, null, null, null));

        State bestGoal = null;

        while (!pq.isEmpty()) {
            State curr = pq.poll();

            if (curr.cost > dist.getOrDefault(curr.node, Collections.emptyMap()).getOrDefault(curr.discountFromLineKey(),
                    Double.MAX_VALUE)) {
                continue;
            }

            if (curr.node.equals(endId)) {
                if (bestGoal == null || curr.cost < bestGoal.cost) {
                    bestGoal = curr;
                }
                // Continue to find potentially better paths if needed, but Dijkstra usually
                // finds min cost first
            }

            List<Edge> neighbors = adj.getOrDefault(curr.node, Collections.emptyList());
            for (Edge e : neighbors) {
                // Avoid immediately undoing an interchange. Without this guard, the graph
                // can form zero-cost loops such as BL10 -> PP16 -> BL10, which then allows
                // the search to apply a transfer discount on a detour that should not exist.
                if (e.isInterchange && curr.parentState != null && e.to.equals(curr.parentState.node)) {
                    continue;
                }

                // Self-fare edges are only meant for the special same-station case handled
                // above. During normal routing they create artificial detours that can pick
                // up a transfer discount and undercut the real fare.
                if (!e.isInterchange && e.to.equals(curr.node)) {
                    continue;
                }

                double travelCost = e.weight;
                String nextDiscountFromLine = e.isInterchange ? curr.node.lineCode : null;

                if (!e.isInterchange && curr.pendingDiscountFromLine != null) {
                    double discount = 0;
                    if (discounts.containsKey(curr.pendingDiscountFromLine)) {
                        discount = discounts.get(curr.pendingDiscountFromLine).getOrDefault(e.to.lineCode, 0.0);
                    }
                    travelCost = Math.max(0, travelCost - discount);
                }

                double newCost = curr.cost + travelCost;

                Map<String, Double> nodeDists = dist.computeIfAbsent(e.to, k -> new HashMap<>());
                String nextDiscountKey = State.discountFromLineKey(nextDiscountFromLine);
                if (newCost < nodeDists.getOrDefault(nextDiscountKey, Double.MAX_VALUE)) {
                    nodeDists.put(nextDiscountKey, newCost);
                    pq.add(new State(e.to, newCost, nextDiscountFromLine, curr, new PathEdge(
                            getStation(curr.node),
                            getStation(e.to),
                            e.to.lineCode,
                            travelCost,
                            e.isInterchange)));
                }
            }
        }

        return reconstructPath(bestGoal);
    }

    private List<PathEdge> reconstructPath(State goal) {
        List<PathEdge> path = new ArrayList<>();
        State curr = goal;
        while (curr != null && curr.edgeUsed != null) {
            path.add(0, curr.edgeUsed);
            curr = curr.parentState;
        }
        return path;
    }

    private Station getStation(Node node) {
        return stationMap.get(node.stationId + "_" + node.lineCode);
    }

    // Helper classes
    private static class Node {
        String stationId;
        String lineCode;

        Node(String stationId, String lineCode) {
            this.stationId = stationId;
            this.lineCode = lineCode;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (!(o instanceof Node))
                return false;
            Node node = (Node) o;
            return Objects.equals(stationId, node.stationId) && Objects.equals(lineCode, node.lineCode);
        }

        @Override
        public int hashCode() {
            return Objects.hash(stationId, lineCode);
        }
    }

    private static class Edge {
        Node to;
        double weight;
        boolean isInterchange;

        Edge(Node to, double weight, boolean isInterchange) {
            this.to = to;
            this.weight = weight;
            this.isInterchange = isInterchange;
        }
    }

    private static class State {
        Node node;
        double cost;
        String pendingDiscountFromLine;
        State parentState;
        PathEdge edgeUsed;

        State(Node node, double cost, String pendingDiscountFromLine, State parentState, PathEdge edgeUsed) {
            this.node = node;
            this.cost = cost;
            this.pendingDiscountFromLine = pendingDiscountFromLine;
            this.parentState = parentState;
            this.edgeUsed = edgeUsed;
        }

        String discountFromLineKey() {
            return discountFromLineKey(pendingDiscountFromLine);
        }

        static String discountFromLineKey(String lineCode) {
            return lineCode == null ? "" : lineCode;
        }
    }
}
