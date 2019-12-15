package au.edu.unimelb.cis.geo;

import au.edu.unimelb.cis.geo.controller.DelaunayTriangulation;
import au.edu.unimelb.cis.geo.controller.GabrielGraph;
import au.edu.unimelb.cis.geo.controller.SteppingStoneGraph;
import au.edu.unimelb.cis.geo.model.Line;
import org.junit.Ignore;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;

import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;

public class SteppingStoneGraphTest {
    private DelaunayTriangulation DTCreator;
    private GabrielGraph gabrielGraph;
    private SteppingStoneGraph steppingStoneGraph;
    private HashSet<Coordinate> pointSet;

    private void initDTCreator() {
        DTCreator = new DelaunayTriangulation();
    }

    private void clearDTCreator() {
        DTCreator = null;
    }

    private void initGabrielGraph() {
        gabrielGraph = new GabrielGraph(DTCreator);
    }

    private void clearGabrielGraph() {
        gabrielGraph = null;
    }

    private void initSteppingStoneGraph() {
        steppingStoneGraph = new SteppingStoneGraph(DTCreator);
    }

    private void clearSteppingStoneGraph() {
        steppingStoneGraph = null;
    }

    private void initSimpleTriangle() {
        pointSet = new HashSet<Coordinate>(3);
        pointSet.add(new Coordinate(1, 1));
        pointSet.add(new Coordinate(1, 2));
        pointSet.add(new Coordinate(2, 1));
    }

    private void clearSimpleTriangle() {
        pointSet = null;
    }

    @Test
    public void TestSimpleTriangle() {
        initSimpleTriangle();
        initDTCreator();
        ArrayList<Line> DelaunayEdges = DTCreator.createDelaunayTriangulation(pointSet);
        initGabrielGraph();
        initSteppingStoneGraph();

        ArrayList<Line> steppingStoneGraphEdges = steppingStoneGraph.getSteppingStoneGraphEdges(2);

        //Both edgeset.size() == 2
        assertEquals(gabrielGraph.getEdgeList().size(), steppingStoneGraphEdges.size());
//        assertEquals("(2.0, 1.0, NaN) -> (1.0, 1.0, NaN)", steppingStoneGraphEdges.get(0).toString());
//        assertEquals("(1.0, 1.0, NaN) -> (1.0, 2.0, NaN)", steppingStoneGraphEdges.get(1).toString());

        clearSimpleTriangle();
        clearGabrielGraph();
        clearSteppingStoneGraph();
        clearDTCreator();
    }

    private void initSimpleTwoTriangles() {
        pointSet = new HashSet<Coordinate>(4);
        pointSet.add(new Coordinate(1, 1));
        pointSet.add(new Coordinate(1, 2));
        pointSet.add(new Coordinate(2, 1.5));
        pointSet.add(new Coordinate(2, 2.5));
    }

    private void clearSimpleTwoTriangles() {
        pointSet = null;
    }

    @Test
    public void TestSimpleTwoTriangles() {
        initSimpleTwoTriangles();
        initDTCreator();
        ArrayList<Line> DelaunayEdges = DTCreator.createDelaunayTriangulation(pointSet);
        initGabrielGraph();
        initSteppingStoneGraph();

        ArrayList<Line> steppingStoneGraphEdges = steppingStoneGraph.getSteppingStoneGraphEdges(2);

        //Both edgeset.size() == 5
        assertEquals(gabrielGraph.getEdgeList().size(), steppingStoneGraphEdges.size());

        clearSimpleTwoTriangles();
        clearGabrielGraph();
        clearSteppingStoneGraph();
        clearDTCreator();
    }

    private void initCounterUrquhartGraph() {
        pointSet = new HashSet<Coordinate>();
        pointSet.add(new Coordinate(0d, 0d));
        pointSet.add(new Coordinate(0d, 1.5d));
        pointSet.add(new Coordinate(2d, 3.0d));
        pointSet.add(new Coordinate(4d, 1d));
        pointSet.add(new Coordinate(4d, 0d));
    }

    private void clearCounterUrquhartGraph() {
        pointSet = null;
    }

    @Test
    @Ignore
    public void TestCounterUrquhartGraph() {
        initCounterUrquhartGraph();
        initDTCreator();
        ArrayList<Line> DelaunayEdges = DTCreator.createDelaunayTriangulation(pointSet);
        initSteppingStoneGraph();
        ArrayList<Line> steppingStoneGraphEdges = steppingStoneGraph.getSteppingStoneGraphEdges(2);

//        for (Line edge : DelaunayEdges) {
//            System.out.println(edge);
//        }
        assertEquals(7, steppingStoneGraphEdges.size());

        steppingStoneGraphEdges = steppingStoneGraph.getSteppingStoneGraphEdges(Double.POSITIVE_INFINITY);

        assertEquals(4, steppingStoneGraphEdges.size());

        clearCounterUrquhartGraph();
        clearSteppingStoneGraph();
        clearDTCreator();
    }
}
