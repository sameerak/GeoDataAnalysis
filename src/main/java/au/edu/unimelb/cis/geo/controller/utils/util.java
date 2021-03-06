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

package au.edu.unimelb.cis.geo.controller.utils;

import au.edu.unimelb.cis.geo.model.Line;
import au.edu.unimelb.cis.geo.model.Triangle;
import org.locationtech.jts.geom.Coordinate;

public class util {

    //line direction is 0 -> 1
    public static boolean isPointClockwiseFromLine(Coordinate point, Line line) {
        double crossProduct = (((point.getX() - line.getEndPoints()[0].getX())
                * (line.getEndPoints()[1].getY() - line.getEndPoints()[0].getY()))
                - (line.getEndPoints()[1].getX() - line.getEndPoints()[0].getX())
                * (point.getY() - line.getEndPoints()[0].getY()));

        return (crossProduct >= 0);
    }

    public static boolean DoesSegmentsIntersect(Coordinate p1, Coordinate p2,
                                                Coordinate p3, Coordinate p4) {
        double d1 = direction(p3, p4, p1),
                d2 = direction(p3, p4, p2),
                d3 = direction(p1, p2, p3),
                d4 = direction(p1, p2, p4);

        if (((d1>0 && d2<0) || (d1<0 && d2>0)) && ((d3>0 && d4<0) || (d3<0 && d4>0)))
            return true;
        else if (d1==0 && onSegment(p3, p4, p1))
            return true;
        else if (d2==0 && onSegment(p3, p4, p2))
            return true;
        else if (d3==0 && onSegment(p1, p2, p3))
            return true;
        else if (d4==0 && onSegment(p1, p2, p4))
            return true;

        return false;
    }

    private static double direction(Coordinate line0, Coordinate line1, Coordinate k) {
        return (((k.getX() - line0.getX())
                * (line1.getY() - line0.getY()))
                - (line1.getX() - line0.getX())
                * (k.getY() - line0.getY()));
    }

    private static boolean onSegment(Coordinate line0, Coordinate line1, Coordinate k) {
        double minX = line0.getX() < line1.getX() ? line0.getX() : line1.getX(),
                maxX = line0.getX() > line1.getX() ? line0.getX() : line1.getX(),
                minY = line0.getY() < line1.getY() ? line0.getY() : line1.getY(),
                maxY = line0.getY() > line1.getY() ? line0.getY() : line1.getY();

        if (minX<=k.getX() && k.getX()<=maxX && minY<=k.getY() && k.getY()<=maxY)
            return true;

        return false;
    }

    public static double solveForDSecant(double c, double a, double b) {
        //throw exception if triangle inequality is not satisfied
//        System.out.println(c + ", " + a + ", " + b);

        double D = 2, tempD = 1, previousHighD = 10;
        double fx = Math.pow(c, D) - Math.pow(a, D) - Math.pow(b, D), oldfx;

        if (fx == 0) {
            return D;
        }
        //try finding upper bound for D-value
        while (fx < 0) {
            D *= 2;
            if (Math.pow(c, D) == Double.POSITIVE_INFINITY)
                return D;
            fx = Math.pow(c, D) - Math.pow(a, D) - Math.pow(b, D);
        }

        previousHighD = D + 1;
        oldfx = Math.pow(c, previousHighD) - Math.pow(a, previousHighD) - Math.pow(b, previousHighD);

        int iterations = 0;
        while (Math.abs(previousHighD - D) > 0.00001) { //setting sensitivity
            fx = Math.pow(c, D) - Math.pow(a, D) - Math.pow(b, D);
//            System.out.println("fx = " + fx);

            tempD = (D*oldfx - previousHighD*fx) / (oldfx - fx);
            previousHighD = D;
            D = tempD;
            oldfx = fx;
            ++iterations;
        }
//        System.out.println("# of Secant iterations = " + iterations);
//        fx = Math.pow(c, D) - Math.pow(a, D) - Math.pow(b, D);
//        System.out.println("fx = " + fx);
        return D;
    }

    public static Coordinate getPointNotOnEdge(Line edge, Triangle triangle) {
        Coordinate[] vertices = triangle.getVertices();
        Coordinate[] endpoints = edge.getEndPoints();
        for (Coordinate vertex :vertices) {
            if (vertex != endpoints[0] && vertex != endpoints[1]) {
                return vertex;
            }
        }
        return null;
    }
}
