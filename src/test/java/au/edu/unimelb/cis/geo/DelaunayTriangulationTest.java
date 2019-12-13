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

    private void initDTCreator() {
        DTCreator = new DelaunayTriangulation();
    }

    private void clearDTCreator() {
        DTCreator = null;
    }

    private void initSimpleTriangle() {
        simpleTriangle = new HashSet<Coordinate>(3);
        simpleTriangle.add(new Coordinate(1, 1));
        simpleTriangle.add(new Coordinate(1, 2));
        simpleTriangle.add(new Coordinate(2, 1.5));
    }

    private void clearSimpleTriangle() {
        simpleTriangle = null;
    }

    @Test
    public void TestSimpleTriangle() {
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

    @Test
    public void TestSimpleTwoTriangles() {
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

    private void initCounterUrquhartGraph() {
        pointSet = new HashSet<Coordinate>();
        pointSet.add(new Coordinate(0d, 0d));
        pointSet.add(new Coordinate(0d, 1.5d));
        pointSet.add(new Coordinate(2d, 3.4d));
        pointSet.add(new Coordinate(4d, 1d));
        pointSet.add(new Coordinate(4d, 0d));
    }

    private void clearCounterUrquhartGraph() {
        pointSet = null;
    }

    @Test
    public void TestCounterUrquhartGraph() {
        initCounterUrquhartGraph();
        initDTCreator();

        ArrayList<Line> DelaunayEdges = DTCreator.createDelaunayTriangulation(pointSet);
//        for (Line edge : DelaunayEdges) {
//            System.out.println(edge);
//        }
        assertEquals(7, DelaunayEdges.size());

        clearCounterUrquhartGraph();
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
        for (Line edge : DelaunayEdges) {
            System.out.println(edge);
        }
        assertEquals(6, DelaunayEdges.size());

        clearTetrahedron();
        clearDTCreator();
    }

    private void initIrregularPyramid() {
        pointSet = new HashSet<Coordinate>();
        pointSet.add(new Coordinate(1d, 0.5d));
        pointSet.add(new Coordinate(1d, 1.5d));
        pointSet.add(new Coordinate(1.8d, 1d));
        pointSet.add(new Coordinate(3d, 0.5d));
        pointSet.add(new Coordinate(4.5d, 2d));
    }

    private void clearIrregularPyramid() {
        pointSet = null;
    }

    @Test
    @Ignore //This point set is included in PyramidAndTetrahedron
    public void TestIrregularPyramid() {
        initIrregularPyramid();
        initDTCreator();

        ArrayList<Line> DelaunayEdges = DTCreator.createDelaunayTriangulation(pointSet);
        for (Line edge : DelaunayEdges) {
            System.out.println(edge);
        }
        assertEquals(8, DelaunayEdges.size());

        clearIrregularPyramid();
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
        for (Line edge : DelaunayEdges) {
            System.out.println(edge);
        }
        assertEquals(13, DelaunayEdges.size());

        clearPyramidAndTetrahedron();
        clearDTCreator();
    }


}
