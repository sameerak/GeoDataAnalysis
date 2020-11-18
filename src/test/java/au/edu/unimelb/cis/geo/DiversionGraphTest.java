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
import au.edu.unimelb.cis.geo.controller.DiversionGraph;
import au.edu.unimelb.cis.geo.controller.GabrielGraph;
import au.edu.unimelb.cis.geo.controller.SteppingStoneGraph;
import au.edu.unimelb.cis.geo.model.Line;
import org.junit.Ignore;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;

import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;

public class DiversionGraphTest {
    private DelaunayTriangulation delaunayTriangulation;
    private GabrielGraph gabrielGraph;
    private SteppingStoneGraph steppingStoneGraph;
    private DiversionGraph diversionGraph;
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

    private void initDiversionGraph() {
        diversionGraph = new DiversionGraph(delaunayTriangulation);
    }

    private void clearDiversionGraph() {
        diversionGraph = null;
    }

    private void initSimpleTriangle() {
        pointSet = new HashSet<>(3);
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
        initDiversionGraph();

        ArrayList<Line> diversionGraphEdges = diversionGraph.getDiversionGraph(2);

        //Both edgeSet.size() == 2
        assertEquals(gabrielGraph.getEdgeList().size(), diversionGraphEdges.size());
//        assertEquals("(2.0, 1.0, NaN) -> (1.0, 1.0, NaN)", steppingStoneGraphEdges.get(0).toString());
//        assertEquals("(1.0, 1.0, NaN) -> (1.0, 2.0, NaN)", steppingStoneGraphEdges.get(1).toString());

        clearSimpleTriangle();
        clearGabrielGraph();
        clearDiversionGraph();
        clearDTCreator();
    }

    private void initSimpleTwoTriangles() {
        pointSet = new HashSet<>(4);
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
        initDiversionGraph();

        ArrayList<Line> diversionGraphEdges = diversionGraph.getDiversionGraph(2);

        //Both edgeSet.size() == 5
        assertEquals(gabrielGraph.getEdgeList().size(), diversionGraphEdges.size());

        clearSimpleTwoTriangles();
        clearGabrielGraph();
        clearDiversionGraph();
        clearDTCreator();
    }

    private void initTetrahedron() {
        pointSet = new HashSet<>();
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
        initDiversionGraph();

        ArrayList<Line> diversionGraphEdges = diversionGraph.getDiversionGraph(2);
        //Both == 3
        assertEquals(gabrielGraph.getEdgeList().size(), diversionGraphEdges.size());

        clearTetrahedron();
        clearGabrielGraph();
        clearDiversionGraph();
        clearDTCreator();
    }

    private void initIrregularPyramid() {
        pointSet = new HashSet<>();
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
        initDiversionGraph();

        ArrayList<Line> diversionGraphEdges = diversionGraph.getDiversionGraph(2);
        //Both == 5
        assertEquals(gabrielGraph.getEdgeList().size(), diversionGraphEdges.size());

        clearIrregularPyramid();
        clearGabrielGraph();
        clearDiversionGraph();
        clearDTCreator();
    }

    private void initPyramidAndTetrahedron() {
        pointSet = new HashSet<>();

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
        initDiversionGraph();

        ArrayList<Line> diversionGraphEdges = diversionGraph.getDiversionGraph(2);
        assertEquals(gabrielGraph.getEdgeList().size(), diversionGraphEdges.size());

        clearPyramidAndTetrahedron();
        clearGabrielGraph();
        clearDiversionGraph();
        clearDTCreator();
    }

    private void initCounterUrquhartGraph() {
        pointSet = new HashSet<>();
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
        initDiversionGraph();

        ArrayList<Line> steppingStoneGraphEdges = steppingStoneGraph.getSteppingStoneGraphEdges(2);
        ArrayList<Line> diversionGraphEdges = diversionGraph.getDiversionGraph(2);
        assertEquals(diversionGraphEdges.size(), steppingStoneGraphEdges.size() );

        steppingStoneGraphEdges = steppingStoneGraph.getSteppingStoneGraphEdges(Double.POSITIVE_INFINITY);
        diversionGraphEdges = diversionGraph.getDiversionGraph(Double.POSITIVE_INFINITY);
        assertEquals(diversionGraphEdges.size() , steppingStoneGraphEdges.size() + 1);

        clearCounterUrquhartGraph();
        clearDiversionGraph();
        clearSteppingStoneGraph();
        clearDTCreator();
    }
}
