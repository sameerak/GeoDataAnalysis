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
    private DelaunayTriangulation delaunayTriangulation;
    private GabrielGraph gabrielGraph;
    private SteppingStoneGraph steppingStoneGraph;
    private HashSet<Coordinate> pointSet;

    private void initDTCreator(HashSet<Coordinate> pointSet) {
        delaunayTriangulation = new DelaunayTriangulation(pointSet);
    }

    private void clearDTCreator() {
        delaunayTriangulation = null;
    }

    private void initGabrielGraph() {
        gabrielGraph = new GabrielGraph(delaunayTriangulation);
    }

    private void clearGabrielGraph() {
        gabrielGraph = null;
    }

    private void initSteppingStoneGraph() {
        steppingStoneGraph = new SteppingStoneGraph(delaunayTriangulation);
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
        initDTCreator(pointSet);
        ArrayList<Line> DelaunayEdges = delaunayTriangulation.getDelaunayEdges();
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
        initDTCreator(pointSet);
        ArrayList<Line> DelaunayEdges = delaunayTriangulation.getDelaunayEdges();
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
        initDTCreator(pointSet);
        ArrayList<Line> DelaunayEdges = delaunayTriangulation.getDelaunayEdges();
        initGabrielGraph();
        initSteppingStoneGraph();

        ArrayList<Line> steppingStoneGraphEdges = steppingStoneGraph.getSteppingStoneGraphEdges(2);
        //Both == 3
        assertEquals(gabrielGraph.getEdgeList().size(), steppingStoneGraphEdges.size());

        clearTetrahedron();
        clearGabrielGraph();
        clearSteppingStoneGraph();
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
        initDTCreator(pointSet);
        ArrayList<Line> DelaunayEdges = delaunayTriangulation.getDelaunayEdges();
        initGabrielGraph();
        initSteppingStoneGraph();

        ArrayList<Line> steppingStoneGraphEdges = steppingStoneGraph.getSteppingStoneGraphEdges(2);
        //Both == 5
        assertEquals(gabrielGraph.getEdgeList().size(), steppingStoneGraphEdges.size());

        clearIrregularPyramid();
        clearGabrielGraph();
        clearSteppingStoneGraph();
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
        initDTCreator(pointSet);
        ArrayList<Line> DelaunayEdges = delaunayTriangulation.getDelaunayEdges();
//        for (Line edge : DelaunayEdges) {
//            System.out.println(edge);
//        }
        initGabrielGraph();
        initSteppingStoneGraph();

        ArrayList<Line> steppingStoneGraphEdges = steppingStoneGraph.getSteppingStoneGraphEdges(2);
        assertEquals(gabrielGraph.getEdgeList().size(), steppingStoneGraphEdges.size());

        clearPyramidAndTetrahedron();
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
    public void TestCounterUrquhartGraph() {
        initCounterUrquhartGraph();
        initDTCreator(pointSet);
        ArrayList<Line> DelaunayEdges = delaunayTriangulation.getDelaunayEdges();
        initSteppingStoneGraph();

        ArrayList<Line> steppingStoneGraphEdges = steppingStoneGraph.getSteppingStoneGraphEdges(2);
        assertEquals(5, steppingStoneGraphEdges.size());

        steppingStoneGraphEdges = steppingStoneGraph.getSteppingStoneGraphEdges(Double.POSITIVE_INFINITY);
        assertEquals(4, steppingStoneGraphEdges.size());

        clearCounterUrquhartGraph();
        clearSteppingStoneGraph();
        clearDTCreator();
    }
}
