package au.edu.unimelb.cis.geo.controller;


import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;

import java.util.ArrayList;
import java.util.HashMap;

public class DelaunayTriangulation {

    public ArrayList<LineString> createDelaunayTriangulation(ArrayList<Coordinate> pointSet) {

        //validate the point set

        //remove overlapping points
        HashMap locationsMap = new HashMap<Coordinate, Integer>();
        ArrayList<Coordinate> DelaunayPoints = new ArrayList<Coordinate>();

        for (Coordinate point : pointSet) {
            if (!locationsMap.containsKey(point)) {
                DelaunayPoints.add(point);
                locationsMap.put(point, 0);
            } else {
                int count = (int) locationsMap.get(point);
                locationsMap.put(point, count++);
            }
        }

        //log the number of points to create Delaunay triangulation on
        System.out.println("# of points for Delaunay graph = " + DelaunayPoints.size());

        //validate number of points
        if (DelaunayPoints.size() < 3) {
            return null; //TODO throw exception: not enough points
        }

        //TODO run sweephull algorithm

        //data structure to hold the edges of the Delaunay triangulation
        ArrayList<LineString> DelaunayEdges = new ArrayList<>();

        //TODO compose the result

        return DelaunayEdges; //return resulting triangulation
    }
}
