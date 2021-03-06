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
import au.edu.unimelb.cis.geo.model.Line;
import au.edu.unimelb.cis.geo.model.Triangle;
import org.junit.Ignore;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;

import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.Assert.*;

public class DelaunayTriangulationTest {
    private DelaunayTriangulation delaunayTriangulation;
    private HashSet<Coordinate> simpleTriangle;
    private HashSet<Coordinate> simpleTwoTriangles;
    private HashSet<Coordinate> pointSet;

    private void initDelaunayTriangulation(HashSet<Coordinate> pointSet) {
        delaunayTriangulation = new DelaunayTriangulation(pointSet);
    }

    private void clearDTCreator() {
        delaunayTriangulation = null;
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
        initDelaunayTriangulation(simpleTriangle);
        ArrayList<Line> DelaunayEdges = delaunayTriangulation.getDelaunayEdges();

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
        initDelaunayTriangulation(simpleTwoTriangles);
        ArrayList<Line> DelaunayEdges = delaunayTriangulation.getDelaunayEdges();

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
        initDelaunayTriangulation(pointSet);

        ArrayList<Line> DelaunayEdges = delaunayTriangulation.getDelaunayEdges();
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
        initDelaunayTriangulation(pointSet);

        ArrayList<Line> DelaunayEdges = delaunayTriangulation.getDelaunayEdges();
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
        initDelaunayTriangulation(pointSet);

        ArrayList<Line> DelaunayEdges = delaunayTriangulation.getDelaunayEdges();
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
        initDelaunayTriangulation(pointSet);

        ArrayList<Line> DelaunayEdges = delaunayTriangulation.getDelaunayEdges();
//        for (Line edge : DelaunayEdges) {
//            System.out.println(edge);
//        }
        assertEquals(13, DelaunayEdges.size());

        for (Triangle triangle : delaunayTriangulation.getTriangleSet().values()) {
            Line[] edges = triangle.getEdges();
            for (Line edge : edges) {
                assertNotNull(edge);
            }
        }

        clearPyramidAndTetrahedron();
        clearDTCreator();
    }


}
