package au.edu.unimelb.cis.geo.controller;


import au.edu.unimelb.cis.geo.model.Line;
import au.edu.unimelb.cis.geo.model.Triangle;
import org.locationtech.jts.geom.Coordinate;

import java.util.*;

import static au.edu.unimelb.cis.geo.model.util.isPointClockwiseFromLine;

public class DelaunayTriangulation {
    private HashMap<String, Line> edgeSet = new HashMap<String, Line>();
    private HashMap<Integer, Triangle> triangleSet = new HashMap<Integer, Triangle>();
    private boolean debug = false;

    public ArrayList<Line> createDelaunayTriangulation(Set<Coordinate> pointSet) {

        System.out.println("# pointSet size= " + pointSet.size());
        //validate the point set

        //remove overlapping points
        HashMap locationsMap = new HashMap<Coordinate, Integer>();
        ArrayList<Coordinate> DelaunayPoints = new ArrayList<Coordinate>();

        for (Coordinate point : pointSet) {
            if (!locationsMap.containsKey(point)) {
                locationsMap.put(point, 1);
            } else {
                int count = (int) locationsMap.get(point);
                locationsMap.put(point, count++);
            }
        }

        DelaunayPoints.addAll(locationsMap.keySet());
        locationsMap = null; //saving space

        //log the number of points to create Delaunay triangulation on
        System.out.println("# of points for Delaunay graph = " + DelaunayPoints.size());

        //validate number of points
        if (DelaunayPoints.size() < 3) {
            return null; //TODO throw exception: not enough points
        }

        //data structure to hold the edges of the Delaunay triangulation
        ArrayList<Line> DelaunayEdges = new ArrayList<>();

        //S-hull algorithm [http://www.s-hull.org/paper/s_hull.pdf]

        //1. sort points
        Collections.sort(DelaunayPoints);
        //2. select a x_o point randomly from x_i
        Coordinate x_o = DelaunayPoints.get(0);
        //3. find the point x_j closest to x_0
        Coordinate x_j = DelaunayPoints.get(1);

        //remove x_o and x_j from further processing
        DelaunayPoints.remove(0);
        DelaunayPoints.remove(0);

        //debug code segment start
        if (debug == true) {
            Line edgeA = getFromLineSet(x_o, x_j);
            Line edgeB = getFromLineSet(x_j, DelaunayPoints.get(0));
            Line edgeC = getFromLineSet(DelaunayPoints.get(0), x_o);

            System.out.println("# of points for Delaunay graph after removing x_o and x_j = " + DelaunayPoints.size());
            DelaunayEdges.addAll(edgeSet.values());
            return DelaunayEdges;
        }
        //debug code segment end

        //4. find the point x_k that creates the smallest circumCircle
        // with x_0 and x_j and record the center of the circum-circle C
        int i_x_k = 0;
        double minCircumRadius = Double.MAX_VALUE;
        Triangle triangle;
        Coordinate[] vertices = new Coordinate[3];
        for (int i = 0; i < DelaunayPoints.size(); i++) {
            vertices[0] = x_o;
            vertices[1] = x_j;
            vertices[2] = DelaunayPoints.get(i);

            triangle = new Triangle(vertices);

            triangle.SetCircumRadius();
            double radius = triangle.getCircumRadius();
            System.out.println("INFO: Point" + i + "^th " + DelaunayPoints.get(i) + " radius = " + radius);
            if (radius < minCircumRadius) {
                System.out.println("INFO: MIN found at " + i + "^th radius = " + radius);
                minCircumRadius = radius;
                i_x_k = i;
            }
        }
        Coordinate x_k = DelaunayPoints.get(i_x_k);

        //remove x_k from further processing
        DelaunayPoints.remove(i_x_k);

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

        //7. add initial 3 edges to Delaunay Triangulation;
         vertices[0] = x_o;
        vertices[1] = x_j;
        vertices[2] = x_k;
        triangle = new Triangle(vertices);
        //Add the first triangle
        Line edge1 = getFromLineSet(x_o, x_j);
        Line edge2 = getFromLineSet(x_j, x_k);
        Line edge3 = getFromLineSet(x_k, x_o);

        triangle.setIndex(triangleSet.size());
        edge1.addNeighbour(triangle.getIndex());
        edge2.addNeighbour(triangle.getIndex());
        edge3.addNeighbour(triangle.getIndex());

        triangle.setEdges(new Line[]{edge1, edge2, edge3});
        triangleSet.put(triangleSet.size(), triangle);
        //First triangle addition finished

        //debug code segment start
        if (debug == true) {
            DelaunayEdges.addAll(edgeSet.values());
            return DelaunayEdges;
        }
        //debug code segment end

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
                    addTriangle(triangle);

                    postProcessIds.add(i);
                } else if (isRotationClockwiseWRTBefore && !isRotationClockwiseWRTAfter) {
                    triangle = new Triangle(new Coordinate[]{point, j_point, i_point});
                    addTriangle(triangle);

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
                    if (postProcessIds.get(i) >= resetID) {
                        convexHull.remove(postProcessIds.get(i) + 1);
                    }
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
        //INFO: By this point a non-overlapping(planar) triangulation of the set of points is created
        System.out.println("# of triangles = " + triangleSet.size());

        //debug code segment start
        if (debug == true) {
            DelaunayEdges.addAll(edgeSet.values());
            return DelaunayEdges;
        }
        //debug code segment end

        if (triangleSet.size() == 1) { //If there is only one triangle
            DelaunayEdges.addAll(edgeSet.values());
            return DelaunayEdges;
        }

        //adjacent pairs of triangles of this triangulation must be 'flipped'
        // in order to create a Delaunay triangulation from the initial non-overlapping triangulation
        boolean inner_flipped = false, outer_flipped = true;
        int iteration = 0;
        while (outer_flipped) {
            outer_flipped = false;
            for (int i = 0; i < triangleSet.size(); i++) {
                triangle = triangleSet.get(i);
                for (int j = 0; j < 3; j++) {
                    int k = (j == 2) ? 0 : j + 1;
                    line = getFromLineSet(triangle.getVertices()[j], triangle.getVertices()[k]);
                    if (line.getNumOfNeighbours() > 1) {
                        int otherNeighbor = getOtherNeighbour(line.getAdjacentNeighbours(), i);
                        if (otherNeighbor != -1) {
                            inner_flipped = checkAndFlip(i, otherNeighbor);
                        }
                    }

                    if (inner_flipped) {
                        outer_flipped = true;
                        inner_flipped = false;
                        --i;
                        break;
                    }
                }
            }
            ++iteration;
            System.out.println("iteration no = " + iteration);
        }

        //INFO: Delaunay triangulation is created by this point.
        System.out.println("# of edges for Delaunay triangulation = " + edgeSet.size());

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
     * Removes the line between given points
     *
     * @param point1
     * @param point2
     * @return true if line exist and removed, false if line does not exist
     */
    private boolean removeFromLineSet(Coordinate point1, Coordinate point2) {
        if (edgeSet.containsKey(point1.toString() + "," + point2.toString())) {
            Line line = edgeSet.get(point1.toString() + "," + point2.toString());
            edgeSet.remove(point1.toString() + "," + point2.toString());
            return true;
        } else if (edgeSet.containsKey(point2.toString() + "," + point1.toString())) {
            Line line = edgeSet.get(point2.toString() + "," + point1.toString());
            edgeSet.remove(point2.toString() + "," + point1.toString());
            return true;
        } else {
            return false;
        }
    }

    /**
     * Processes a triangle
     * 1. Create lines to make the triangle
     * 1.1 add neighbouring triangles to those lines
     * 2. Add provided triangle to triangle set
     * @param triangle
     */
    private void addTriangle(Triangle triangle) {
        triangle.setIndex(triangleSet.size());
        Line[] edges = new Line[3];
        for (int i = 0; i < 3; i++) {
            int j = (i == 2) ? 0 : i + 1;
            Line line = getFromLineSet(triangle.getVertices()[i], triangle.getVertices()[j]);
            if (line.getNumOfNeighbours() == 1) {
                triangle.addNeighbour(line.getAdjacentNeighbours()[0]);
            } else {
                continue;
            }
            line.addNeighbour(triangle.getIndex());
            edges[i] = line;
        }
        triangle.setEdges(edges);
        triangleSet.put(triangleSet.size(), triangle);
    }

    /**
     * From the array adjacentNeighbours select the neighbour other than the one provided at pos
     *
     * @param adjacentNeighbours
     * @param pos
     * @return
     */
    private int getOtherNeighbour(int[] adjacentNeighbours, int pos) {
        if (adjacentNeighbours[0] == pos) {
            return adjacentNeighbours[1];
        }
        return adjacentNeighbours[0];
    }

    /**
     * Checks and flips given two triangles A and B if they violate Delaunay condition
     *
     a ------- b,2
     |  A   /  |
     |    /    |
     |  /   B  |
     c,3------ d,1
     * @param triangleAIndex
     * @param triangleBIndex
     * @return
     */
    private boolean checkAndFlip(int triangleAIndex, int triangleBIndex) {
        if (triangleAIndex == triangleBIndex) { //same triangle cannot be compared
            return false;
        }

        Triangle triangleA = triangleSet.get(triangleAIndex);
        Triangle triangleB = triangleSet.get(triangleBIndex);
        Coordinate[] triangle1 = triangleA.getVertices();
        Coordinate[] triangle2 = triangleB.getVertices();

//        System.out.println("Beginning = " +triangleAIndex + "," + triangleBIndex);
//        System.out.println("triangleA = " + triangleA);
//        System.out.println("triangleB = " + triangleB);

        int D_index = -1,
            A_index = -1,
            B_index = -1,
            C_index = -1;
        //Following loop locates point D and sets up points for A,B,C from given triangles
        for (int i = 0; i < 3; i++) { //this traverses triangleA clockwise
            int j = (i == 2) ? 0 : i + 1;
            Coordinate endpointB = triangle1[i];
            Coordinate endpointC = triangle1[j];

            for (int k = 2; k >= 0; k--) { //this traverses triangleB counter clockwise
                int l = (k == 0) ? 2 : k - 1;
                Coordinate endpoint1 = triangle2[k];
                Coordinate endpoint2 = triangle2[l];

                if (endpointB.equals(endpoint1) &&
                        endpointC.equals(endpoint2)) {
                    D_index = (l == 0) ? 2 : l - 1;
                    A_index = (j == 2) ? 0 : j + 1;
                    B_index = k;
                    C_index = j;
                    break;
                }
            }

            if (D_index > -1) {
                break;
            }
        }

        if (D_index == -1) {
            System.out.println(triangleAIndex + "," + triangleBIndex + " SKIPPING FLIP!!!! " +
                    "Cannot locate D");
            return false;
        }

        Line BC = getFromLineSet(triangle2[B_index], triangle1[C_index]);
        //Check if BC is previously flipped
        if (BC.getFlipCount() >= 16) {
            System.out.println(triangleAIndex + "," + triangleBIndex + " SKIPPING FLIP!!!! " +
                    "As these triangles are already flipped before");
            return false;
        }

        //If there is a line connecting points A and D there is no point performing determinant test
        //as those D is not gonna be inside circum circle of ABC
        if (edgeSet.containsKey(triangle2[D_index].toString() + "," + triangle1[A_index].toString()) ||
                edgeSet.containsKey(triangle1[A_index].toString()+ "," + triangle2[D_index].toString())) {
//            System.out.println(triangleA.getPos() + "," + triangleB.getPos() + "SKIPPING FLIP!!!! before determinant test");
            return false;
        }

        //if the given triangles fail determinant test
        if (isDInsideABC(triangleA, triangle2[D_index])) {
            //flip given two triangles
            //ABC and BDC -> ABD and ADC
            //edge changes
            //AB ^ BC ^ CA and BD ^ DC ^ CB -> AB ^ BD ^ DA and AD ^ DC ^ CA
            //note that from triangle A and B, edges AB and DC respectively does not change
            //we use this as reference point to decide which edges to replace
            //edge CA from triangle A moves to triangle B after edge DC and
            //edge BD from triangle B moves to triangle A after edge AB
            //positions held by edges CA and BD in triangles A and B respectively are replaced by new edge AD
            /*
            a ------- b,2
            |  \      |
            | A  \  B |
            |      \  |
            c,3------ d,1
             */
            //adding new line
            Line newLine = getFromLineSet(triangle2[D_index], triangle1[A_index]);

            if (newLine.getNumOfNeighbours() > 0) { //if new flipping edge has at least one neighbour
                System.out.println(triangleAIndex + "," + triangleBIndex + "SKIPPING FLIP!!!!");
                return false; // These 2 triangles should not be flipped
            }

            Line[] Triangle1Edges = triangleA.getEdges(),
                    Triangle2Edges = triangleB.getEdges();

//            System.out.println("removing BC line = " + triangle1[C_index].getTweetID() +
//                    "," + triangle2[B_index].getTweetID());
//            System.out.println("Adding AD line = " + triangle2[D_index].getTweetID() +
//                    "," + triangle1[A_index].getTweetID());

            //remove BC line as it is going to be replaced by AD line
            removeFromLineSet(triangle1[C_index], triangle2[B_index]);

            //Add neighbours to new AD line
            newLine.addNeighbour(triangleAIndex);
            newLine.addNeighbour(triangleBIndex);
            //setting AD is a flipped line so that next time it won't be flipped again
            newLine.setFlipCount(BC);
            int flipCount = newLine.getFlipCount();

            Line tempLine;
            //updating existing BD and AC lines as their neighbours are changing
            tempLine = getFromLineSet(triangle2[B_index], triangle2[D_index]);
            tempLine.replaceAdjacentNeighbour(triangleBIndex, triangleAIndex);

            tempLine = getFromLineSet(triangle1[A_index], triangle1[C_index]);
            tempLine.replaceAdjacentNeighbour(triangleAIndex, triangleBIndex);

            //set C <- D and B <- A
            triangle1[C_index] = triangle2[D_index];
            Triangle1Edges[(A_index == 2) ? 0 : A_index + 1] = Triangle2Edges[B_index];
            triangle2[B_index] = triangle1[A_index];
            Triangle2Edges[(D_index == 2) ? 0 : D_index + 1] = Triangle1Edges[C_index];

            Triangle2Edges[B_index] = newLine;
            Triangle1Edges[C_index] = newLine;

            triangleA.SetCircumRadius();
            triangleB.SetCircumRadius();

//            System.out.println(triangleA.getPos() + "," + triangleB.getPos() + " FLIPPING count = " + flipCount);
//            System.out.println("triangleA = " + triangleA);
//            System.out.println("triangleB = " + triangleB);
            return true;
        }

        return false;
    }

    /**
     * Checks if point D is residing inside circum circle of the triangle ABC
     * Note this ABC are traversed counter clockwise direction
     * More info - https://en.wikipedia.org/wiki/Delaunay_triangulation#Algorithms
     A ------- C
     |      /  |
     |    /    |
     |  /      |
     B ------- D
     * @param abc
     * @param d
     * @return True if and only if D lies inside the circumCircle ABC
     */
    public static boolean isDInsideABC(Triangle abc, Coordinate d) {
        abc.SetCircumRadius();

        double D_length = d.distance(abc.getCircumcenter());

        if (D_length < abc.getCircumRadius()) {
            return true;
        }

        return false;
    }
}
