package au.edu.unimelb.cis.geo.model;


import org.locationtech.jts.geom.Coordinate;

public class util {
    public static long miliSeconds_perDay = 24 * 60 * 60 * 1000;

    //line direction is 0 -> 1
    public static boolean isPointClockwiseFromLine(Coordinate point, Line line) {
        double crossProduct = (((point.getX() - line.getEndPoints()[0].getX())
                * (line.getEndPoints()[1].getY() - line.getEndPoints()[0].getY()))
                - (line.getEndPoints()[1].getX() - line.getEndPoints()[0].getX())
                * (point.getY() - line.getEndPoints()[0].getY()));

        return (crossProduct >= 0);
    }
}
