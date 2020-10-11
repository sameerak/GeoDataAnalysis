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
import org.locationtech.jts.geom.Coordinate;

import java.util.ArrayList;

import static au.edu.unimelb.cis.geo.controller.utils.util.getPointNotOnEdge;

public class DiversionGraph {
    private DelaunayTriangulation delaunayTriangulation;

    public DiversionGraph(DelaunayTriangulation delaunayTriangulation) {
        this.delaunayTriangulation = delaunayTriangulation;

    }

    public ArrayList<Line> getNewGraph(double d) {
        ArrayList<Line> newGraphAtD = new ArrayList<>();

        for (Line DTEdge: delaunayTriangulation.getDelaunayEdges()) {
            if (isToSkipFromNewGraph(DTEdge, d)) {
                continue; //skip this edge
            } else {
                newGraphAtD.add(DTEdge);
            }
        }
        return newGraphAtD;
    }

    private boolean isToSkipFromNewGraph(Line DTEdge, double d) {
        int[] adjacentNeighbours = DTEdge.getAdjacentNeighbours();

        for (int triangleID : adjacentNeighbours) {
            if (triangleID != -1) {
                //Vertex C of triangle, which is not on evaluating edge
                Coordinate c = getPointNotOnEdge(DTEdge, delaunayTriangulation.getTriangleSet().get(triangleID));
                if (isCInsideDiversionNeighbourhood(DTEdge, c, d)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isCInsideDiversionNeighbourhood(Line dtEdge, Coordinate c, double d) {
        Coordinate[] endpoints = dtEdge.getEndPoints();
        double AB = endpoints[0].distance(endpoints[1]),
                AC = endpoints[0].distance(c),
                BC = endpoints[1].distance(c),
                minLength;

        if (d < Double.POSITIVE_INFINITY) {
            //Standardising the triangle by dividing edge lengths with minLength
            minLength = Math.min(AB, Math.min(AC, BC));

            AB = AB / minLength;
            AC = AC / minLength;
            BC = BC / minLength;

            return (Math.pow(AB, d) >= (Math.pow(AC, d) + Math.pow(BC, d)));
        } else {
            return (AB >= AC && AB >= BC);
        }
    }
}
