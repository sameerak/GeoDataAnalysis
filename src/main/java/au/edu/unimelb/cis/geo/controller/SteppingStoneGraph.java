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

package au.edu.unimelb.cis.geo.controller;

import au.edu.unimelb.cis.geo.model.Line;
import au.edu.unimelb.cis.geo.model.Triangle;
import org.locationtech.jts.geom.Coordinate;

import java.util.ArrayList;
import java.util.HashMap;

import static au.edu.unimelb.cis.geo.controller.utils.util.*;

public class SteppingStoneGraph {
    private HashMap<String, Line> edgeSet;
    private DelaunayTriangulation delaunayTriangulation;
    private Coordinate Zmax, MaskingVertex;
    private Line MaskingEdge, nextMaskingEdge;
    private Triangle MaskingTriangle;

    public SteppingStoneGraph(DelaunayTriangulation delaunayTriangulation) {
        this.delaunayTriangulation = delaunayTriangulation;
        edgeSet = delaunayTriangulation.getEdgeSet();
        CreateD_Spectrum();
    }

    private void CreateD_Spectrum() {
        double[] minDs;
        for (String delaunayEdgeKey :edgeSet.keySet()) {
            Line DTEdge = edgeSet.get(delaunayEdgeKey);
            minDs = new double[]{Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY};

            for (int i = 0; i < 2; i++) {
                    walkRelativeNeighborhood(DTEdge, i, minDs);
            }
//            System.out.println("INFO: Edge" + DTEdge + "minDs = [" + minDs[0] + " ," + minDs[1] + "]");

            DTEdge.setD_value(Math.min(minDs[0], minDs[1]));
        }
    }

    private void walkRelativeNeighborhood(Line DE, int i, double[] minDs) {
        MaskingEdge = DE;
        int MaskingTriangleID = DE.getAdjacentNeighbours()[i];
        HashMap<Integer, Triangle> delaunayTriangleSet = delaunayTriangulation.getTriangleSet();

        if (MaskingTriangleID != -1) {
            MaskingTriangle = delaunayTriangleSet.get(MaskingTriangleID);
            Zmax = null;
            MaskingVertex = getMaskingVertex(DE, i, minDs);
            Zmax = getZmax(DE, i, minDs); //set edge of relative neighborhood on masking vertex side
        } else {
            return;
        }

//        if (MaskingVertex.distance(DE.getCenterPoint()) > Zmax.distance(DE.getCenterPoint())){
//            return;
//        }

        int totTrianglesChecked = 0;
        while (MaskingTriangleID != -1) {
            ++totTrianglesChecked;
            MaskingTriangle = delaunayTriangleSet.get(MaskingTriangleID);
            MaskingVertex = getMaskingVertex(DE, i, minDs);

            MaskingTriangle.SetCircumRadius();
            if (isZmaxWithinCircumcircleOfMaskingTriangle()) {
                break;
            }
            //else run rest of the code

            double length0 = MaskingVertex.distance(DE.getEndPoints()[0]);
            double length1 = MaskingVertex.distance(DE.getEndPoints()[1]);
            double DELength = DE.getLength();
            double minLength;

            //Standardising the triangle by dividing edge lengths with minLength
            minLength = Math.min(DELength, Math.min(length0, length1));

            DELength = DELength / minLength;
            length0 = length0 / minLength;
            length1 = length1 / minLength;

            if((minDs[i] == Double.POSITIVE_INFINITY && length0 < DELength && length1 < DELength) ||
                    (minDs[i] != Double.POSITIVE_INFINITY &&
                            Math.pow(length0, minDs[i]) + Math.pow(length1, minDs[i])
                                    <= Math.pow(DELength, minDs[i]))) {
                double newD = solveForDSecant(DELength, length0, length1);
                if (newD < minDs[i]) {
                    minDs[i] = newD;
                    Zmax = getZmax(DE, i, minDs);
                }
            }

            if (nextMaskingEdge == null) {
                break;
            }
            //get new MaskingEdge of the MaskingTriangle
            MaskingEdge = nextMaskingEdge;

            //get new MaskingTriangleID from new MaskingEdge
            if (MaskingEdge.getAdjacentNeighbours()[1] == MaskingTriangleID) {
                MaskingTriangleID = MaskingEdge.getAdjacentNeighbours()[0];
            } else {
                MaskingTriangleID = MaskingEdge.getAdjacentNeighbours()[1];
            }
        }
    }


    private boolean isZmaxWithinCircumcircleOfMaskingTriangle() {
        return MaskingTriangle.getCircumcenter().distance(Zmax) <= MaskingTriangle.getCircumRadius();
    }

    private Coordinate getZmax(Line DE, int i, double[] minDs) {
        double Ymax = (DE.getLength()/2)*(Math.sqrt(3));
        if (Ymax < Double.POSITIVE_INFINITY) {
            Ymax = (DE.getLength()/2)*(Math.sqrt(Math.pow(4, 1-1/minDs[i])-1));
        }

        double m = DE.getA() != 0 ? DE.getB()/DE.getA() : DE.getB();
        double c = DE.getA() != 0 ? DE.getCPerpendicular()/DE.getA() : DE.getCPerpendicular();

        double diff = Ymax/Math.sqrt(1 + Math.pow(m, 2));
        Coordinate center = DE.getCenterPoint();
        double x = DE.getA() != 0 ? center.getX() + diff : center.getX();
        double y = DE.getA() != 0 ? m*x + c : center.getY() + Ymax;

        Coordinate z = new Coordinate(x, y);

        if (isPointClockwiseFromLine(z, DE) != isPointClockwiseFromLine(MaskingVertex, DE)) {
            x = DE.getA() != 0 ? center.getX() - diff : center.getX();
            y = DE.getA() != 0 ? m*x + c : center.getY() - Ymax;

            z = new Coordinate(x, y);
        }
        return z;
    }

    private Coordinate getMaskingVertex(Line DE, int i, double[] minDs) {
        Coordinate vertex = null,
                e0 = MaskingEdge.getEndPoints()[0],
                e1 = MaskingEdge.getEndPoints()[1];

        for (Coordinate vertex1 : MaskingTriangle.getVertices()) {
            if (vertex1 != e0 &&
                    vertex1 != e1)
                vertex = vertex1;
        }

        if (Zmax == null) {
            MaskingVertex = vertex;
            Zmax = getZmax(DE, i, minDs);
        }

        boolean e0Intersect = DoesSegmentsIntersect(vertex, e0, DE.getCenterPoint(), Zmax),
                e1Intersect = DoesSegmentsIntersect(vertex, e1, DE.getCenterPoint(), Zmax);

//        if (e0Intersect && e1Intersect) {
//            System.out.println("INFO : Both edges of masking triangle intersect with Z max");
//        }

        if (e0Intersect)
            nextMaskingEdge = delaunayTriangulation.getFromLineSet(vertex, e0);
        if (e1Intersect)
            nextMaskingEdge = delaunayTriangulation.getFromLineSet(vertex, e1);
        if (!e0Intersect && !e1Intersect)
            nextMaskingEdge = null;

        if (MaskingEdge == nextMaskingEdge) {
            System.out.println("ERROR : next masking edge set incorrectly!!!!!!!!!!!!!!!");
        }

        return vertex;
    }

    public ArrayList<Line> getSteppingStoneGraphEdges(double d) {
        ArrayList<Line> steppingStoneGraphEdges = new ArrayList<>(edgeSet.size());
        for (String delaunayEdgeKey :edgeSet.keySet()) {
            Line DTEdge = edgeSet.get(delaunayEdgeKey);
            int skipped = 0, added = 0;
            if ((d < Double.POSITIVE_INFINITY && DTEdge.getD_value() <= d)
                    || (d == Double.POSITIVE_INFINITY && DTEdge.getD_value() < d)) {
                skipped++;
            } else {
                steppingStoneGraphEdges.add(DTEdge);
                added++;
            }
        }
        return steppingStoneGraphEdges;
    }
}
