/*
 * Copyright (c) 2020, Sameera Kannangara (dlskannangara@gmail.com) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package au.edu.unimelb.cis.geo;

import au.edu.unimelb.cis.geo.controller.DelaunayTriangulation;
import au.edu.unimelb.cis.geo.controller.GabrielGraph;
import au.edu.unimelb.cis.geo.controller.ShortestPathGraph;
import au.edu.unimelb.cis.geo.controller.SteppingStoneGraph;
import au.edu.unimelb.cis.geo.model.Line;
import org.junit.Ignore;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;

import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ShortestPathGraphTest {
    private DelaunayTriangulation delaunayTriangulation;
    private GabrielGraph gabrielGraph;
    private SteppingStoneGraph steppingStoneGraph;
    private ShortestPathGraph shortestPathGraph;
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

    private void initShortestPathGraph() {
        shortestPathGraph = new ShortestPathGraph(delaunayTriangulation);
    }

    private void clearShortestPathGraph() {
        shortestPathGraph = null;
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
        initGabrielGraph();
        initSteppingStoneGraph();
        initShortestPathGraph();

        ArrayList<Line> shortestPathGraphEdges = shortestPathGraph.getShortestPathGraphEdges(2);

        //Both edgeset.size() == 2
        assertTrue(gabrielGraph.getEdgeList().size() >= shortestPathGraphEdges.size());
//        assertEquals("(2.0, 1.0, NaN) -> (1.0, 1.0, NaN)", steppingStoneGraphEdges.get(0).toString());
//        assertEquals("(1.0, 1.0, NaN) -> (1.0, 2.0, NaN)", steppingStoneGraphEdges.get(1).toString());

        ArrayList<Line> steppingStoneGraphEdges = steppingStoneGraph.getSteppingStoneGraphEdges(3);
        shortestPathGraphEdges = shortestPathGraph.getShortestPathGraphEdges(3);

        assertTrue(steppingStoneGraphEdges.size() >= shortestPathGraphEdges.size());

        clearSimpleTriangle();
        clearGabrielGraph();
        clearSteppingStoneGraph();
        clearShortestPathGraph();
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
        initGabrielGraph();
        initSteppingStoneGraph();
        initShortestPathGraph();

        ArrayList<Line> steppingStoneGraphEdges = steppingStoneGraph.getSteppingStoneGraphEdges(3);
        ArrayList<Line> shortestPathGraphEdges = shortestPathGraph.getShortestPathGraphEdges(3);

        assertTrue(steppingStoneGraphEdges.size() >= shortestPathGraphEdges.size());

        clearSimpleTwoTriangles();
        clearGabrielGraph();
        clearSteppingStoneGraph();
        clearShortestPathGraph();
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
        initGabrielGraph();
        initSteppingStoneGraph();
        initShortestPathGraph();

        ArrayList<Line> steppingStoneGraphEdges = steppingStoneGraph.getSteppingStoneGraphEdges(3);
        ArrayList<Line> shortestPathGraphEdges = shortestPathGraph.getShortestPathGraphEdges(3);

        assertTrue(steppingStoneGraphEdges.size() >= shortestPathGraphEdges.size());

        clearTetrahedron();
        clearGabrielGraph();
        clearSteppingStoneGraph();
        clearShortestPathGraph();
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
        initGabrielGraph();
        initSteppingStoneGraph();
        initShortestPathGraph();

        ArrayList<Line> steppingStoneGraphEdges = steppingStoneGraph.getSteppingStoneGraphEdges(3);
        ArrayList<Line> shortestPathGraphEdges = shortestPathGraph.getShortestPathGraphEdges(3);

        assertTrue(steppingStoneGraphEdges.size() >= shortestPathGraphEdges.size());

        clearIrregularPyramid();
        clearGabrielGraph();
        clearSteppingStoneGraph();
        clearShortestPathGraph();
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
        initGabrielGraph();
        initSteppingStoneGraph();
        initShortestPathGraph();

        ArrayList<Line> steppingStoneGraphEdges = steppingStoneGraph.getSteppingStoneGraphEdges(3);
        ArrayList<Line> shortestPathGraphEdges = shortestPathGraph.getShortestPathGraphEdges(3);

        assertTrue(steppingStoneGraphEdges.size() >= shortestPathGraphEdges.size());

        clearPyramidAndTetrahedron();
        clearGabrielGraph();
        clearSteppingStoneGraph();
        clearShortestPathGraph();
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
        initSteppingStoneGraph();
        initShortestPathGraph();

        ArrayList<Line> steppingStoneGraphEdges = steppingStoneGraph.getSteppingStoneGraphEdges(2);
        ArrayList<Line> shortestPathGraphEdges = shortestPathGraph.getShortestPathGraphEdges(2);

        assertTrue(steppingStoneGraphEdges.size() >= shortestPathGraphEdges.size());

        steppingStoneGraphEdges = steppingStoneGraph.getSteppingStoneGraphEdges(Double.POSITIVE_INFINITY);
        shortestPathGraphEdges = shortestPathGraph.getShortestPathGraphEdges(Double.POSITIVE_INFINITY);

        assertTrue(steppingStoneGraphEdges.size() >= shortestPathGraphEdges.size());

        clearCounterUrquhartGraph();
        clearSteppingStoneGraph();
        clearShortestPathGraph();
        clearDTCreator();
    }
}
