package au.edu.unimelb.cis.geo;

import au.edu.unimelb.cis.geo.controller.DelaunayTriangulation;
import au.edu.unimelb.cis.geo.controller.GabrielGraph;
import au.edu.unimelb.cis.geo.model.Line;
import org.junit.Ignore;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;

import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;

public class GabrielGraphTest {
    private DelaunayTriangulation DTCreator;
    private GabrielGraph gabrielGraph;
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

    private void initSimpleTriangle() {
        pointSet = new HashSet<Coordinate>(3);
        pointSet.add(new Coordinate(1, 1));
        pointSet.add(new Coordinate(1, 2));
        pointSet.add(new Coordinate(2, 1.5));
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

        assertEquals(3, gabrielGraph.getEdgeList().size());
//        assertEquals("(1.0, 1.0, NaN) -> (1.0, 2.0, NaN)", DelaunayEdges.get(0).toString());
//        assertEquals("(1.0, 2.0, NaN) -> (2.0, 1.5, NaN)", DelaunayEdges.get(1).toString());
//        assertEquals("(2.0, 1.5, NaN) -> (1.0, 1.0, NaN)", DelaunayEdges.get(2).toString());

        clearSimpleTriangle();
        clearGabrielGraph();
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

        assertEquals(5, gabrielGraph.getEdgeList().size());
        assertEquals("(1.0, 2.0, NaN) -> (2.0, 2.5, NaN)", DelaunayEdges.get(0).toString());
        assertEquals("(2.0, 1.5, NaN) -> (1.0, 1.0, NaN)", DelaunayEdges.get(1).toString());
        assertEquals("(1.0, 1.0, NaN) -> (1.0, 2.0, NaN)", DelaunayEdges.get(2).toString());
        assertEquals("(1.0, 2.0, NaN) -> (2.0, 1.5, NaN)", DelaunayEdges.get(3).toString());
        assertEquals("(2.0, 2.5, NaN) -> (2.0, 1.5, NaN)", DelaunayEdges.get(4).toString());

        clearSimpleTwoTriangles();
        clearGabrielGraph();
        clearDTCreator();
    }

    private void initTetrahedron() {
        pointSet = new HashSet<Coordinate>();
        pointSet.add(new Coordinate(1d, 1.5d));
        pointSet.add(new Coordinate(2d, 4d));
        pointSet.add(new Coordinate(2.5d, 2.5d));
        pointSet.add(new Coordinate(4.5d, 2d));
    }

    private void clearTetrahedron() {
        pointSet = null;
    }

    @Test
    @Ignore //This point set is included in PyramidAndTetrahedron
    public void TestTetrahedron() {
        initTetrahedron();
        initDTCreator();

        ArrayList<Line> DelaunayEdges = DTCreator.createDelaunayTriangulation(pointSet);
        initGabrielGraph();
//        for (Line edge : gabrielGraph.getEdgeList()) {
//            System.out.println(edge);
//        }
        assertEquals(3, gabrielGraph.getEdgeList().size());

        clearTetrahedron();
        clearGabrielGraph();
        clearDTCreator();
    }

    private void initPyramidAndTetrahedron() {
        pointSet = new HashSet<Coordinate>();

        //Tetrahedron points
        pointSet.add(new Coordinate(1d, 1.5d));
        pointSet.add(new Coordinate(2d, 4d));
        pointSet.add(new Coordinate(2.5d, 2.5d));
        pointSet.add(new Coordinate(4.5d, 2d));

        //Pyramid points
        pointSet.add(new Coordinate(1d, 0.5d));
        pointSet.add(new Coordinate(1d, 1.5d));
        pointSet.add(new Coordinate(1.8d, 1d));
        pointSet.add(new Coordinate(3d, 0.5d));
        pointSet.add(new Coordinate(4.5d, 2d));
    }

    private void clearPyramidAndTetrahedron() {
        pointSet = null;
    }

    @Test
    public void TestPyramidAndTetrahedron() {
        initPyramidAndTetrahedron();
        initDTCreator();

        ArrayList<Line> DelaunayEdges = DTCreator.createDelaunayTriangulation(pointSet);
        initGabrielGraph();
//        for (Line edge : gabrielGraph.getEdgeList()) {
//            System.out.println(edge);
//        }
        assertEquals(10, gabrielGraph.getEdgeList().size());

        clearPyramidAndTetrahedron();
        clearGabrielGraph();
        clearDTCreator();
    }
}
