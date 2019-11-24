package au.edu.unimelb.cis.geo.model;

import org.locationtech.jts.geom.Coordinate;

public class Line {

    private Coordinate[] endPoints = new Coordinate[2];
    private double length;
    private double a, b, c, c1;
    private Coordinate centerPoint;
    private Coordinate euclideanCenterPoint;
    private String[] neighbouringTriangleIDs = {"-1", "-1"};
    private int numOfNeighbouringTriangles = 0;

    public Line(Coordinate point1, Coordinate point2){
        endPoints[0] = point1;
        endPoints[1] = point2;

        Init(point1, point2);
    }

    public double getLength() {
        return length;
    }

    public double getA() {
        return a;
    }

    public double getB() {
        return b;
    }

    public double getC() {
        return c;
    }

    public double getCPerpendicular() {
        return c1;
    }

    private void Init(Coordinate point1, Coordinate point2) {
        setCircularMidPoint();
        //calculate length
        double dx = point1.getX() - point2.getX();
        double dy = point1.getY() - point2.getY();
        length = point1.distance(point2);
//                Math.sqrt(dx * dx + dy * dy);

        double theta = Math.atan2(dx, dy);
        double directionalTheta = theta;

        if (theta < 0) {
            theta = -1 * theta;
            theta = Math.PI - theta;
        }

        //line equation
        //ax + by + c = 0
        a = point1.getY() - point2.getY();
        b = point2.getX() - point1.getX();
        c = -b * point1.getY() + -a * point1.getX();

        double x = (endPoints[0].getX() + endPoints[1].getX()) / 2,
                y = (endPoints[0].getY() + endPoints[1].getY()) / 2;
        c1 = a * y - b * x;
        //perpendicular equation
        //bx - ay + c1 = 0

        euclideanCenterPoint = new Coordinate(x, y);
    }

    private void setCircularMidPoint(){

        double dLongitude = Math.toRadians(endPoints[1].getX() - endPoints[0].getX());

        //convert to radians
        double latitude1 = Math.toRadians(endPoints[0].getY());
        double latitude2 = Math.toRadians(endPoints[1].getY());
        double longitude1 = Math.toRadians(endPoints[0].getX());

        double Bx = Math.cos(latitude2) * Math.cos(dLongitude);
        double By = Math.cos(latitude2) * Math.sin(dLongitude);
        double latitude3 = Math.atan2(Math.sin(latitude1) + Math.sin(latitude2),
                Math.sqrt((Math.cos(latitude1) + Bx) * (Math.cos(latitude1) + Bx) + By * By));
        double longitude3 = longitude1 + Math.atan2(By, Math.cos(latitude1) + Bx);

        centerPoint = new Coordinate(Math.toDegrees(longitude3), Math.toDegrees(latitude3));
    }

    public Coordinate[] getEndPoints() {
        return endPoints;
    }

    public void addNeighbour(String neighbourID) {
        neighbouringTriangleIDs[numOfNeighbouringTriangles] = neighbourID;
        ++numOfNeighbouringTriangles;
    }

    public String[] getAdjacentNeighbours() {
        return neighbouringTriangleIDs;
    }

    public int getNumOfNeighbours() {
        return numOfNeighbouringTriangles;
    }

    public boolean replaceAdjacentNeighbour(String oldVal, String newVal) {
        if (neighbouringTriangleIDs[0].equals(oldVal)) {
            neighbouringTriangleIDs[0] = newVal;
            return true;
        } else if (neighbouringTriangleIDs[1].equals(oldVal)) {
            neighbouringTriangleIDs[1] = newVal;
            return true;
        }
        return false;
    }
}
