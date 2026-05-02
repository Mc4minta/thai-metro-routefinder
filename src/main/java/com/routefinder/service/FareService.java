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

public class FareService {

    private final Map<String, Station> stationMap = new HashMap<>();
    private final Map<Node, List<Edge>> adj = new HashMap<>();
    private final Map<String, Map<String, Double>> discounts = new HashMap<>();

    public FareService() {
        loadData();
    }

    private void addDiscount(String fromLine, String toLine, double amount) {
        discounts.computeIfAbsent(fromLine, k -> new HashMap<>()).put(toLine, amount);
    }

    private void loadData() {
        try (Reader reader = new InputStreamReader(getClass().getResourceAsStream("/data-fare-discount.json"))) {
            Gson gson = new Gson();
            JsonObject data = gson.fromJson(reader, JsonObject.class);

            // Load stations to map IDs to objects
            JsonArray stationsArray = data.getAsJsonArray("stations");
            for (JsonElement element : stationsArray) {
                Station s = gson.fromJson(element, Station.class);
                stationMap.put(s.id + "_" + s.line, s);
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

    public List<PathEdge> calculateRoute(Station start, Station end) {
        Node startNode = new Node(start.id, start.line);
        Node endId = new Node(end.id, end.line); // Target can be any node with this station ID

        PriorityQueue<State> pq = new PriorityQueue<>(Comparator.comparingDouble(s -> s.cost));
        Map<Node, Map<Boolean, Double>> dist = new HashMap<>();

        pq.add(new State(startNode, 0.0, false, null, null));
        
        State bestGoal = null;

        while (!pq.isEmpty()) {
            State curr = pq.poll();

            if (curr.cost > dist.getOrDefault(curr.node, Collections.emptyMap()).getOrDefault(curr.afterInterchange, Double.MAX_VALUE)) {
                continue;
            }

            if (curr.node.stationId.equals(end.id)) {
                if (bestGoal == null || curr.cost < bestGoal.cost) {
                    bestGoal = curr;
                }
                // Continue to find potentially better paths if needed, but Dijkstra usually finds min cost first
            }

            List<Edge> neighbors = adj.getOrDefault(curr.node, Collections.emptyList());
            for (Edge e : neighbors) {
                double travelCost = e.weight;
                boolean nextAfterInterchange = e.isInterchange;

                if (!e.isInterchange && curr.afterInterchange) {
                    // Apply discount
                    double discount = 0;
                    if (discounts.containsKey(curr.parentState.node.lineCode)) {
                        discount = discounts.get(curr.parentState.node.lineCode).getOrDefault(e.to.lineCode, 0.0);
                    }
                    travelCost = Math.max(0, travelCost - discount);
                }

                double newCost = curr.cost + travelCost;

                Map<Boolean, Double> nodeDists = dist.computeIfAbsent(e.to, k -> new HashMap<>());
                if (newCost < nodeDists.getOrDefault(nextAfterInterchange, Double.MAX_VALUE)) {
                    nodeDists.put(nextAfterInterchange, newCost);
                    pq.add(new State(e.to, newCost, nextAfterInterchange, curr, new PathEdge(
                            getStation(curr.node), 
                            getStation(e.to), 
                            e.to.lineCode, 
                            travelCost
                    )));
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
            if (this == o) return true;
            if (!(o instanceof Node)) return false;
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
        boolean afterInterchange;
        State parentState;
        PathEdge edgeUsed;

        State(Node node, double cost, boolean afterInterchange, State parentState, PathEdge edgeUsed) {
            this.node = node;
            this.cost = cost;
            this.afterInterchange = afterInterchange;
            this.parentState = parentState;
            this.edgeUsed = edgeUsed;
        }
    }
}
