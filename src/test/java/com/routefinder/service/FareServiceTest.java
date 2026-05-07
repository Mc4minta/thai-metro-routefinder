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
        assertEquals(1, route.size(), "Should return the entry/exit fare self-edge");
        assertEquals(17.0, route.get(0).cost, 0.01, "Entry/exit fare for Siam should apply");
    }

    @Test
    public void testSingleLineFare() {
        // PP01 -> PP16 = 42.0
        Station start = new Station();
        start.id = "PP01";
        start.line = "PP";

        Station end = new Station();
        end.id = "PP16";
        end.line = "PP";

        List<PathEdge> route = fareService.calculateRoute(start, end);

        assertNotNull(route);
        assertFalse(route.isEmpty());
        double totalCost = route.stream().mapToDouble(e -> e.cost).sum();
        assertEquals(42.0, totalCost, 0.01);
    }

    @Test
    public void testDiscountAfterInterchange() {
        // PP16(PP) -> BL11(BL)
        Station start = new Station();
        start.id = "PP16";
        start.line = "PP";

        Station end = new Station();
        end.id = "BL11";
        end.line = "BL";

        List<PathEdge> route = fareService.calculateRoute(start, end);

        assertNotNull(route);
        assertTrue(route.size() >= 2);
        
        boolean hasInterchange = route.stream().anyMatch(e -> e.cost == 0.0);
        assertTrue(hasInterchange, "Should contain an interchange edge");

        // The exact fare structure might depend on the JSON, but we verify it's a valid route and discounted.
        double totalCost = route.stream().mapToDouble(e -> e.cost).sum();
        assertTrue(totalCost < (14.0 + 17.0)); // Should be strictly less than full fares combined
    }

    @Test
    public void testLongSingleLineBlue() {
        // BL01 -> BL38
        Station start = new Station();
        start.id = "BL01";
        start.line = "BL";

        Station end = new Station();
        end.id = "BL38";
        end.line = "BL";

        List<PathEdge> route = fareService.calculateRoute(start, end);

        assertNotNull(route);
        assertFalse(route.isEmpty());
        double totalCost = route.stream().mapToDouble(e -> e.cost).sum();
        assertEquals(30.0, totalCost, 0.01); // 30.0 is the BL max in data
    }

    @Test
    public void testShortSingleLineBlueDoesNotUseInterchangeDiscount() {
        Station start = new Station();
        start.id = "BL01";
        start.line = "BL";

        Station end = new Station();
        end.id = "BL03";
        end.line = "BL";

        List<PathEdge> route = fareService.calculateRoute(start, end);

        assertNotNull(route);
        assertFalse(route.isEmpty());
        double totalCost = route.stream().mapToDouble(e -> e.cost).sum();
        assertEquals(20.0, totalCost, 0.01);
    }

    @Test
    public void testTaoPoonBlueToKamphaengPhetStaysOnBlueLine() {
        Station start = new Station();
        start.id = "BL10";
        start.line = "BL";

        Station end = new Station();
        end.id = "BL12";
        end.line = "BL";

        List<PathEdge> route = fareService.calculateRoute(start, end);

        assertNotNull(route);
        assertEquals(1, route.size(), "Route should stay on the Blue Line without a transfer loop");
        assertFalse(route.get(0).isInterchange, "Direct Blue Line route should not include an interchange");
        assertEquals(20.0, route.get(0).cost, 0.01, "Tao Poon to Kamphaeng Phet should cost 20 baht");
    }

    @Test
    public void testSameStationDifferentLineLatPhrao() {
        // BL15 -> YL01 (Lat Phrao)
        Station start = new Station();
        start.id = "BL15";
        start.line = "BL";

        Station end = new Station();
        end.id = "YL01";
        end.line = "YL";

        List<PathEdge> route = fareService.calculateRoute(start, end);

        assertNotNull(route);
        assertEquals(1, route.size());
        assertEquals(17.0, route.get(0).cost, 0.01, "Should apply same-station entry/exit fare");
    }

    @Test
    public void testMoChitToYellowLineIncludesInterchangeEdges() {
        Station start = new Station();
        start.id = "N8";
        start.line = "LG";

        Station end = new Station();
        end.id = "YL01";
        end.line = "YL";

        List<PathEdge> route = fareService.calculateRoute(start, end);

        assertNotNull(route);
        assertFalse(route.isEmpty());
        long interchangeCount = route.stream().filter(e -> e.isInterchange).count();
        assertEquals(2, interchangeCount);
    }

    @Test
    public void testSurasakToMoChitUsesBtsDirectFare() {
        Station start = new Station();
        start.id = "S5";
        start.line = "DG";

        Station end = new Station();
        end.id = "N8";
        end.line = "LG";

        List<PathEdge> route = fareService.calculateRoute(start, end);

        assertNotNull(route);
        assertFalse(route.isEmpty());

        double totalCost = route.stream().mapToDouble(e -> e.cost).sum();
        assertEquals(47.0, totalCost, 0.01, "BTS direct fare should be 47 baht");

        boolean hasBlueLine = route.stream().anyMatch(e -> "BL".equals(e.line));
        assertFalse(hasBlueLine, "Route should not detour through the Blue Line");
    }

    @Test
    public void testSameStationSameLine() {
        // PP16 -> PP16
        Station start = new Station();
        start.id = "PP16";
        start.line = "PP";

        Station end = new Station();
        end.id = "PP16";
        end.line = "PP";

        List<PathEdge> route = fareService.calculateRoute(start, end);

        assertNotNull(route);
        assertEquals(1, route.size());
        assertEquals(16.0, route.get(0).cost, 0.01); // 16.0 based on PP CSV self edge
    }



    @Test
    public void testFixedPinkLineData() {
        // PK19 -> PK20 (Should work now since we patched the data)
        Station start = new Station();
        start.id = "PK19";
        start.line = "PK";

        Station end = new Station();
        end.id = "PK20";
        end.line = "PK";

        List<PathEdge> route = fareService.calculateRoute(start, end);

        assertNotNull(route);
        assertFalse(route.isEmpty());
        double totalCost = route.stream().mapToDouble(e -> e.cost).sum();
        assertEquals(17.0, totalCost, 0.01);
    }
}
