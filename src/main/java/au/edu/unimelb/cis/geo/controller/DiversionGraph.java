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
            //if one of edge lengths < 1, then find minimum length edge
            //and divide edge lengths with minLength
            if (AB < 1 || AC < 1 || BC < 1) {
                minLength = Math.min(AB, Math.min(AC, BC));

                AB = AB / minLength;
                AC = AC / minLength;
                BC = BC / minLength;
            }
            return (Math.pow(AB, d) >= (Math.pow(AC, d) + Math.pow(BC, d)));
        } else {
            return (AB >= AC && AB >= BC);
        }
    }
}
