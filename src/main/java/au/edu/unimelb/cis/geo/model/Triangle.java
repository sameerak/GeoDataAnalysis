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

package au.edu.unimelb.cis.geo.model;

import org.locationtech.jts.geom.Coordinate;

public class Triangle {
    private int index;
    private Coordinate[] vertices;//these should form a clockwise rotation
    private Line[] edges; //if edges exist, contains 3 edges in order 0-1,1-2,2-0
    private Coordinate circumCenter;
    private double circumRadius = Double.MAX_VALUE;

    private int numOfAdjacentTriangles = 0;
    private int[] adjacentTriangleIndexes = new int[3];

    public Triangle(Coordinate[] vertices) {
        this.vertices = vertices;
    }

    public Coordinate[] getVertices() {
        return vertices;
    }

    public void addNeighbour(int neighbourID) {
        adjacentTriangleIndexes[numOfAdjacentTriangles] = neighbourID;
        ++numOfAdjacentTriangles;
    }

    public Coordinate getCircumcenter() {
        return circumCenter;
    }

    public double getCircumRadius() {
        return circumRadius;
    }

    public Line[] getEdges() {
        return edges;
    }

    public void setEdges(Line[] edges) {
        this.edges = edges;
    }


    public void SetCircumRadius() {
        Line line1, line2;
        line1 = new Line(vertices[0], vertices[1]);
        line2 = new Line(vertices[1], vertices[2]);

        double a1, b1, c1, a2, b2, c2;

        a1 = line1.getB();
        b1 = line1.getA() * -1;
        c1 = line1.getCPerpendicular();
        a2 = line2.getB();
        b2 = line2.getA() * -1;
        c2 = line2.getCPerpendicular();

        double a2b1MINa1b2 = a2 * b1 - a1 * b2;

        double sx, sy;

        sx = (b2 * c1 - b1 * c2) / a2b1MINa1b2;
        sy = (a1 * c2 - a2 * c1) / a2b1MINa1b2;

        circumCenter = new Coordinate(sx, sy);
        double dist0 = circumCenter.distance(vertices[0]),
                dist1 = circumCenter.distance(vertices[1]),
                dist2 = circumCenter.distance(vertices[2]);

        //Assign the distance to furthest vertex as radius
        circumRadius = Math.max(dist2, Math.max(dist0, dist1));
    }

    @Override
    public String toString() {
        String out = "Vertices = " + vertices[0] + " -> " +
                vertices[1] + " -> " + vertices[2];
        if (edges != null) {
            out += "\n Edges = " + edges[0] + " ^ " + edges[1] +
                    " ^ " + edges[2];
        }
        return out;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
