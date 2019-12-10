package au.edu.unimelb.cis.geo;

import au.edu.unimelb.cis.geo.controller.DelaunayTriangulation;
import au.edu.unimelb.cis.geo.model.Line;
import org.junit.Ignore;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;

import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DelaunayTriangulationTest {
    private DelaunayTriangulation DTCreator;
    private HashSet<Coordinate> simpleTriangle;
    private HashSet<Coordinate> simpleTwoTriangles;
    private HashSet<Coordinate> pointSet;

    private void initSimpleTriangle() {
        simpleTriangle = new HashSet<Coordinate>(3);
        simpleTriangle.add(new Coordinate(1, 1));
        simpleTriangle.add(new Coordinate(1, 2));
        simpleTriangle.add(new Coordinate(2, 1.5));
    }

    private void clearSimpleTriangle() {
        simpleTriangle = null;
    }

    private void initSimpleTwoTriangles() {
        simpleTwoTriangles = new HashSet<Coordinate>(4);
        simpleTwoTriangles.add(new Coordinate(1, 1));
        simpleTwoTriangles.add(new Coordinate(1, 2));
        simpleTwoTriangles.add(new Coordinate(2, 1.5));
        simpleTwoTriangles.add(new Coordinate(2, 2.5));
    }

    private void clearSimpleTwoTriangles() {
        simpleTwoTriangles = null;
    }

    private void initCounterUrquhartGraph() {
        pointSet = new HashSet<Coordinate>();
        pointSet.add(new Coordinate(0d, 1d));
        pointSet.add(new Coordinate(0d, 2d));
        pointSet.add(new Coordinate(4d, 3d));
        pointSet.add(new Coordinate(4d, 4.01d));
        pointSet.add(new Coordinate(2d, 3d));
    }

    private void clearCounterUrquhartGraph() {
        pointSet = null;
    }

    private void initDTCreator() {
        DTCreator = new DelaunayTriangulation();
    }

    private void clearDTCreator() {
        DTCreator = null;
    }

    @Test@Ignore
    public void BuildSimpleTriangle() {
        initSimpleTriangle();
        initDTCreator();
        ArrayList<Line> DelaunayEdges = DTCreator.createDelaunayTriangulation(simpleTriangle);

        assertEquals(3, DelaunayEdges.size());
//        assertEquals("(1.0, 1.0, NaN) -> (1.0, 2.0, NaN)", DelaunayEdges.get(0).toString());
//        assertEquals("(1.0, 2.0, NaN) -> (2.0, 1.5, NaN)", DelaunayEdges.get(1).toString());
//        assertEquals("(2.0, 1.5, NaN) -> (1.0, 1.0, NaN)", DelaunayEdges.get(2).toString());

        clearSimpleTriangle();
        clearDTCreator();
    }

    @Test@Ignore
    public void BuildSimpleTwoTriangles() {
        initSimpleTwoTriangles();
        initDTCreator();
        ArrayList<Line> DelaunayEdges = DTCreator.createDelaunayTriangulation(simpleTwoTriangles);

        assertEquals(5, DelaunayEdges.size());
        assertEquals("(1.0, 2.0, NaN) -> (2.0, 2.5, NaN)", DelaunayEdges.get(0).toString());
        assertEquals("(2.0, 1.5, NaN) -> (1.0, 1.0, NaN)", DelaunayEdges.get(1).toString());
        assertEquals("(1.0, 1.0, NaN) -> (1.0, 2.0, NaN)", DelaunayEdges.get(2).toString());
        assertEquals("(1.0, 2.0, NaN) -> (2.0, 1.5, NaN)", DelaunayEdges.get(3).toString());
        assertEquals("(2.0, 2.5, NaN) -> (2.0, 1.5, NaN)", DelaunayEdges.get(4).toString());

        clearSimpleTwoTriangles();
        clearDTCreator();
    }

    @Test@Ignore
    public void BuildCounterUrquhartGraph() {
        initCounterUrquhartGraph();
        initDTCreator();

        ArrayList<Line> DelaunayEdges = DTCreator.createDelaunayTriangulation(pointSet);
        for (Line edge : DelaunayEdges) {
            System.out.println(edge);
        }
        assertTrue(true);

        clearCounterUrquhartGraph();
        clearDTCreator();
    }
}
