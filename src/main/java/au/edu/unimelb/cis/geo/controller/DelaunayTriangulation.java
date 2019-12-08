package au.edu.unimelb.cis.geo.controller;


import au.edu.unimelb.cis.geo.model.Line;
import au.edu.unimelb.cis.geo.model.Triangle;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import static au.edu.unimelb.cis.geo.model.util.isPointClockwiseFromLine;

public class DelaunayTriangulation {
    private HashMap<String, Line> edgeSet = new HashMap<String, Line>();
    private HashMap<Integer, Triangle> triangleSet = new HashMap<Integer, Triangle>();

    public ArrayList<Line> createDelaunayTriangulation(ArrayList<Coordinate> pointSet) {

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

        //data structure to hold the edges of the Delaunay triangulation
        ArrayList<Line> DelaunayEdges = new ArrayList<>();

        //S-hull algorithm [http://www.s-hull.org/paper/s_hull.pdf]

        //1. sort point
        Collections.sort(DelaunayPoints);
        //2. select a x_o point randomly from x_i
        Coordinate x_o = DelaunayPoints.get(0);
        //3. find the point x_j closest to x_0
        Coordinate x_j = DelaunayPoints.get(1);
        //4. find the point x_k that creates the smallest circumCircle
        // with x_0 and x_j and record the center of the circum-circle C
        int i_x_k = 0;
        double minCircumRadius = Double.MAX_VALUE;
        Triangle triangle;
        Coordinate[] vertices = new Coordinate[3];
        for (int i = 2; i < DelaunayPoints.size(); i++) {
            vertices[0] = x_o;
            vertices[1] = x_j;
            vertices[2] = DelaunayPoints.get(i);

            triangle = new Triangle(vertices);

            triangle.SetCircumRadius();
            double radius = triangle.getCircumRadius();
            if (radius < minCircumRadius) {
                minCircumRadius = radius;
                i_x_k = i;
            }
        }
        Coordinate x_k = DelaunayPoints.get(i_x_k);
        //5. order point x_0, x_j, x_k to give a right handed (clockwise) system this is the initial x_o convex hull
        //create line in the order x_0, x_j and check x_k is clockwise or not relative to line
        Line line = new Line(x_o, x_j);
        //if x_k is not clockwise swap the locations of x_o and x_j
        if (!isPointClockwiseFromLine(x_k, line)) {
            Coordinate x_temp = x_o;
            x_o = x_j;
            x_j = x_temp;
        }
        //6. after this x_o, x_j and x_k in that order creates a right handed system
        ArrayList<Coordinate> convexHull = new ArrayList<Coordinate>();

        convexHull.add(x_o);
        convexHull.add(x_j);
        convexHull.add(x_k);

        //if input only contain 3 points, return the right handed system
        if (DelaunayPoints.size() == 3) {
            DelaunayEdges.add(new Line(x_o, x_j));
            DelaunayEdges.add(new Line(x_j, x_k));
            DelaunayEdges.add(new Line(x_k, x_o));

            return DelaunayEdges;
        }

        //7. add initial 3 edges to Delaunay Triangulation;
        vertices[0] = x_o;
        vertices[1] = x_j;
        vertices[2] = x_k;
        triangle = new Triangle(vertices);
        processTriangle(triangle);

        //8. re-sort the remaining points with respect to the circumcenter of the first triangle,
        // to give points s_i
        triangle.SetCircumRadius();
        Coordinate c = triangle.getCircumcenter();
        Collections.sort(DelaunayPoints, (Comparator.<Coordinate>
                comparingDouble(point1 -> point1.distance(c))
                .thenComparingDouble(point2 -> point2.distance(c))));

        //9. sequentially add the points s_i to the propagating 2D convex hull
        // that is seeded with the triangle formed from x_0, x_j, x_k
        // as a new point is added the facets of the 2D-hull that are visible to it form new triangles
        int id = 3;
        int resetID = 0; // To store the convex hull position to be replaced
        for (Coordinate point : DelaunayPoints) {
            ArrayList<Integer> postProcessIds = new ArrayList<Integer>();
            for (int i = 0; i < convexHull.size(); i++) {
                int h = (i - 1 < 0) ? convexHull.size() - 1 : i - 1;
                int j = (i + 1 == convexHull.size()) ? 0 : i + 1;

                Coordinate h_point = convexHull.get(h);
                Coordinate i_point = convexHull.get(i);
                Coordinate j_point = convexHull.get(j);

                Line BeforeConvexHullEdge = new Line(h_point, i_point);
                Line afterConvexHullEdge = new Line(i_point, j_point);

                boolean isRotationClockwiseWRTBefore = isPointClockwiseFromLine(point, BeforeConvexHullEdge);
                boolean isRotationClockwiseWRTAfter = isPointClockwiseFromLine(point, afterConvexHullEdge);

                if (!isRotationClockwiseWRTBefore && !isRotationClockwiseWRTAfter) {
                    triangle = new Triangle(new Coordinate[]{point, j_point, i_point});
                    processTriangle(triangle);

                    postProcessIds.add(i);
                } else if (isRotationClockwiseWRTBefore && !isRotationClockwiseWRTAfter) {
                    triangle = new Triangle(new Coordinate[]{point, j_point, i_point});
                    processTriangle(triangle);

                    resetID = j;
                }
            }

            //processing convex hull changes
            //Add new point at the location identified by resetID
            convexHull.add(resetID, point);

            //if there are points to be removed from convex hull
            if (!postProcessIds.isEmpty()) {
                Collections.sort(postProcessIds);
                int key = -1;
                for (int i = postProcessIds.size() -1; i >= 0; i--) {
                    if (postProcessIds.get(i) >= resetID)
                        convexHull.remove(postProcessIds.get(i) + 1);
                    else {
                        //Could not use key extracted from postProcessIds as is
                        //had to assign it to new variable before using
                        //otherwise convex hull point was not removed
                        key = postProcessIds.get(i);
                        convexHull.remove(key);
                    }
                }
            }
        }
        System.out.println("# of triangles = " + triangleSet.size());

        DelaunayEdges.addAll(edgeSet.values());
        return DelaunayEdges; //return resulting triangulation
    }

    /**
     * Select a line from lineSet to represent a line between given to points
     * Creates a new line between provided points if that line does not exist
     * @param point1
     * @param point2
     * @return
     */
    private Line getFromLineSet(Coordinate point1, Coordinate point2) {

        /* Each line's ID is a combination of tweet ids from points
         * as the combination can depend on ordering of 2 ids this method checks both combinations */
        if (edgeSet.containsKey(point1.toString() + "," + point2.toString())) {
            return edgeSet.get(point1.toString() + "," + point2.toString());
        } else if (edgeSet.containsKey(point2.toString() + "," + point1.toString())) {
            return edgeSet.get(point2.toString() + "," + point1.toString());
        } else { //if line does not exist for both ID combinations
            //creates a new line and return
            Line line = new Line(point1, point2);
            edgeSet.put(point1.toString() + "," + point2.toString(), line);
            return edgeSet.get(point1.toString() + "," + point2.toString());
        }
    }

    /**
     * Processes a triangle
     * 1. Create lines to make the triangle
     * 1.1 add neighbouring triangles to those lines
     * 2. Add provided triangle to triangle set
     * @param triangle
     */
    private void processTriangle(Triangle triangle) {
        Line[] edges = new Line[3];
        for (int i = 0; i < 3; i++) {
            int j = (i == 2) ? 0 : i + 1;
            Line line = getFromLineSet(triangle.getVertices()[i], triangle.getVertices()[j]);
            if (line.getNumOfNeighbours() == 2) {
                continue;
            }
            if (line.getNumOfNeighbours() > 0) {
                triangle.addNeighbour(line.getAdjacentNeighbours()[0]);
            }
            if (line.getNumOfNeighbours() < 2) {
                line.addNeighbour(triangle.getID());
            }
            edges[i] = line;
        }
        triangle.setEdges(edges);
        triangleSet.put(triangleSet.size(), triangle);
    }
}
