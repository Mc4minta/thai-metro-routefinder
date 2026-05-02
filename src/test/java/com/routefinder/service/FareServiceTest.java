package com.routefinder.service;

import com.routefinder.model.PathEdge;
import com.routefinder.model.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FareServiceTest {

    private FareService fareService;

    @BeforeEach
    public void setUp() {
        // Initialize the service which loads data from data.json
        fareService = new FareService();
    }

    @Test
    public void testDirectRouteNoInterchange() {
        // Example: National Stadium (W1, DG) to Siam (CEN_DG, DG)
        Station start = new Station();
        start.id = "W1";
        start.line = "DG";

        Station end = new Station();
        end.id = "CEN_DG";
        end.line = "DG";

        List<PathEdge> route = fareService.calculateRoute(start, end);

        assertNotNull(route, "Route should not be null");
        assertFalse(route.isEmpty(), "Route should not be empty");
        assertEquals(1, route.size(), "Route should have 1 edge");
        assertEquals(17.0, route.get(0).cost, 0.01, "Cost should match fare matrix");
    }

    @Test
    public void testRouteWithInterchange() {
        // Example: Siam (CEN_DG, DG) to Samyan (BL27, BL)
        // Siam (DG) -> Sala Daeng (S2, DG) -> interchange -> Si Lom (BL26, BL) -> Sam Yan (BL27, BL)
        Station start = new Station();
        start.id = "CEN_DG";
        start.line = "DG";

        Station end = new Station();
        end.id = "BL27";
        end.line = "BL";

        List<PathEdge> route = fareService.calculateRoute(start, end);

        assertNotNull(route, "Route should not be null");
        assertFalse(route.isEmpty(), "Route should not be empty");

        double totalCost = route.stream().mapToDouble(e -> e.cost).sum();
        assertTrue(totalCost > 0, "Total cost should be greater than 0");

        // Should include an interchange edge (cost 0)
        boolean hasInterchange = route.stream().anyMatch(e -> e.cost == 0.0);
        assertTrue(hasInterchange, "Route should include an interchange edge");
    }

    @Test
    public void testSameStationDifferentLines() {
        // Example: Siam (CEN_DG, DG) to Siam (CEN_LG, LG)
        Station start = new Station();
        start.id = "CEN_DG";
        start.line = "DG";

        Station end = new Station();
        end.id = "CEN_LG";
        end.line = "LG";

        List<PathEdge> route = fareService.calculateRoute(start, end);

        assertNotNull(route);
        assertEquals(1, route.size(), "Should just be the interchange edge");
        assertEquals(0.0, route.get(0).cost, 0.01, "Interchange within same station should be free");
    }
}
