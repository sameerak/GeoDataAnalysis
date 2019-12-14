package au.edu.unimelb.cis.geo.controller;

import au.edu.unimelb.cis.geo.model.Line;
import au.edu.unimelb.cis.geo.model.Triangle;
import org.locationtech.jts.geom.Coordinate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

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
                minLength = Double.MAX_VALUE;

        //if one of edge lengths < 1, then find minimum length edge
        //and divide edge lengths with minLength
        if (AB < 1 || AC < 1 || BC < 1) {
            if (AC < BC) {
                minLength = AC;
            } else {
                minLength = BC;
            }
            if (AB < minLength) {
                minLength = AB;
            }

            AB = AB / minLength;
            AC = AC / minLength;
            BC = BC / minLength;
        }

        return (Math.pow(AB, 2) < (Math.pow(AC, 2) + Math.pow(BC, 2)));
    }

    private Coordinate getPointNotOnEdge(Line edge, Triangle delaunayTriangle) {
        Coordinate[] vertices = delaunayTriangle.getVertices();
        Coordinate[] endpoints = edge.getEndPoints();
        for (Coordinate vertex :vertices) {
            if (vertex != endpoints[0] && vertex != endpoints[1]) {
                return vertex;
            }
        }
        return null;
    }

    public ArrayList<Line> getEdgeList() {
        ArrayList<Line> gabrielEdges = new ArrayList<>(edgeSet.size());
        gabrielEdges.addAll(edgeSet.values());
        return gabrielEdges;
    }
}
