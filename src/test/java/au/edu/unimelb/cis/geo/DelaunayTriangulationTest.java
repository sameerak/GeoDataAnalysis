package au.edu.unimelb.cis.geo;

import au.edu.unimelb.cis.geo.controller.DelaunayTriangulation;
import au.edu.unimelb.cis.geo.model.Line;
import org.junit.Ignore;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;

import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

public class DelaunayTriangulationTest {
    private DelaunayTriangulation DTCreator;
    private ArrayList<Coordinate> simpleTriangle;
    private ArrayList<Coordinate> simpleTwoTriangles;
    private ArrayList<Coordinate> pointSet;

    private void initSimpleTriangle() {
        simpleTriangle = new ArrayList<>(3);
        simpleTriangle.add(new Coordinate(1, 1));
        simpleTriangle.add(new Coordinate(1, 2));
        simpleTriangle.add(new Coordinate(2, 1.5));
    }

    private void clearSimpleTriangle() {
        simpleTriangle = null;
    }

    private void initSimpleTwoTriangles() {
        simpleTwoTriangles = new ArrayList<>(4);
        simpleTwoTriangles.add(new Coordinate(1, 1));
        simpleTwoTriangles.add(new Coordinate(1, 2));
        simpleTwoTriangles.add(new Coordinate(2, 1.5));
        simpleTwoTriangles.add(new Coordinate(2, 2.5));
    }

    private void clearSimpleTwoTriangles() {
        simpleTwoTriangles = null;
    }

    private void initCounterUrquhartGraph() {
        pointSet = new ArrayList<>();
        pointSet.add(new Coordinate(0.0, 1));
        pointSet.add(new Coordinate(0.0, 2));
        pointSet.add(new Coordinate(4.0, 3));
        pointSet.add(new Coordinate(4.01, 4));
        pointSet.add(new Coordinate(6.5, 5));
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

    @Test
    public void BuildSimpleTriangle() {
        initSimpleTriangle();
        initDTCreator();
        ArrayList<Line> DelaunayEdges = DTCreator.createDelaunayTriangulation(simpleTriangle);
        assertTrue( DelaunayEdges.get(0).toString().equals("(1.0, 1.0, NaN) -> (1.0, 2.0, NaN)"));
        assertTrue( DelaunayEdges.get(1).toString().equals("(1.0, 2.0, NaN) -> (2.0, 1.5, NaN)"));
        assertTrue( DelaunayEdges.get(2).toString().equals("(2.0, 1.5, NaN) -> (1.0, 1.0, NaN)"));
        clearSimpleTriangle();
        clearDTCreator();
    }

    @Test
    public void BuildSimpleTwoTriangles() {
        initSimpleTwoTriangles();
        initDTCreator();
        ArrayList<Line> DelaunayEdges = DTCreator.createDelaunayTriangulation(simpleTwoTriangles);
        assertTrue( DelaunayEdges.get(0).toString().equals("(1.0, 2.0, NaN) -> (2.0, 2.5, NaN)"));
        assertTrue( DelaunayEdges.get(1).toString().equals("(2.0, 1.5, NaN) -> (1.0, 1.0, NaN)"));
        assertTrue( DelaunayEdges.get(2).toString().equals("(1.0, 1.0, NaN) -> (1.0, 2.0, NaN)"));
        assertTrue( DelaunayEdges.get(3).toString().equals("(1.0, 2.0, NaN) -> (2.0, 1.5, NaN)"));
        assertTrue( DelaunayEdges.get(4).toString().equals("(2.0, 2.5, NaN) -> (2.0, 1.5, NaN)"));
        clearSimpleTwoTriangles();
        clearDTCreator();
    }

    @Test@Ignore
    public void BuildCounterUrquhartGraph() {
        initCounterUrquhartGraph();
        initDTCreator();
        ArrayList<Line> DelaunayEdges = DTCreator.createDelaunayTriangulation(simpleTwoTriangles);
        for (Line edge : DelaunayEdges) {
            System.out.println(edge);
        }
        assertTrue(true);
        clearCounterUrquhartGraph();
        clearDTCreator();
    }
}
