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
}
