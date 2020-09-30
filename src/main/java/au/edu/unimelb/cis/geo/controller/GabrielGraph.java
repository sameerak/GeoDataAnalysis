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

import static au.edu.unimelb.cis.geo.controller.utils.util.getPointNotOnEdge;

public class GabrielGraph {
    private HashMap<String, Line> edgeSet = new HashMap<String, Line>();
    private DelaunayTriangulation delaunayTriangulation;

    public GabrielGraph(DelaunayTriangulation delaunayTriangulation) {
        this.delaunayTriangulation = delaunayTriangulation;
        HashMap<String, Line> delaunayEdgeSet = delaunayTriangulation.getEdgeSet();
        HashMap<Integer, Triangle> delaunayTriangleSet = delaunayTriangulation.getTriangleSet();
        //For each Delaunay edge
        for (String delaunayEdgeKey :delaunayEdgeSet.keySet()) {
            Line edge = delaunayEdgeSet.get(delaunayEdgeKey);
            //Check for each neighbouring triangle
            int[] neighbouringTriangleIndexes = edge.getAdjacentNeighbours();
            boolean[] cOutsideDiametericCircle = {true, true};
            int i = 0;
            for (int index :neighbouringTriangleIndexes) {
                if (index == -1) {
                    continue;
                }

                Triangle delaunayTriangle = delaunayTriangleSet.get(index);
                //Whether the vertex of triangle, not on the edge
                Coordinate c = getPointNotOnEdge(edge, delaunayTriangle);
                //is outside the circle of which, edge is the diameter
                cOutsideDiametericCircle[i] = isPointOutsideDiametericCircle(c, edge);
                i++;
            }
            //if both vertices of the neighbouring triangles are outside
            //the diametric circle of the edge, then add it to Gabriel edge set
            if (cOutsideDiametericCircle[0] && cOutsideDiametericCircle[1]) {
//            if ((neighbouringTriangleIndexes[0] != -1 && cOutsideDiametericCircle[0]) &&
//                    (neighbouringTriangleIndexes[1] != -1 && cOutsideDiametericCircle[1])) {
                edgeSet.put(delaunayEdgeKey, edge);
            }
        }
    }

    private boolean isPointOutsideDiametericCircle(Coordinate c, Line edge) {
        Coordinate[] endpoints = edge.getEndPoints();
        double AB = endpoints[0].distance(endpoints[1]),
                AC = endpoints[0].distance(c),
                BC = endpoints[1].distance(c),
                minLength;

        //if one of edge lengths < 1, then find minimum length edge
        //and divide edge lengths with minLength
        if (AB < 1 || AC < 1 || BC < 1) {
            minLength = Math.min(AB, Math.min(AC, BC));

            AB = AB / minLength;
            AC = AC / minLength;
            BC = BC / minLength;
        }

        return (Math.pow(AB, 2) < (Math.pow(AC, 2) + Math.pow(BC, 2)));
    }

    public ArrayList<Line> getEdgeList() {
        ArrayList<Line> gabrielEdges = new ArrayList<>(edgeSet.size());
        gabrielEdges.addAll(edgeSet.values());
        return gabrielEdges;
    }
}
