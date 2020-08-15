package au.edu.unimelb.cis.geo.controller;

import au.edu.unimelb.cis.geo.model.Line;
import org.locationtech.jts.geom.Coordinate;

import java.util.*;

public class ShortestPathGraph {
    private ArrayList<Coordinate> uniqueCoordinates;
    private HashMap<Coordinate, ArrayList<Line>> connections;
    private HashMap<String, Line> edgeSet;
    private DelaunayTriangulation delaunayTriangulation;
    private ArrayList<Line> sortedDelaunayEdges;
    private Line minLengthLine;

    public ShortestPathGraph(DelaunayTriangulation delaunayTriangulation) {
        this.delaunayTriangulation = delaunayTriangulation;
        edgeSet = delaunayTriangulation.getEdgeSet();
        uniqueCoordinates = delaunayTriangulation.getUniqueCoordinates();
        sortedDelaunayEdges = new ArrayList<>(edgeSet.size());
        sortedDelaunayEdges.addAll(edgeSet.values());
        Collections.sort(sortedDelaunayEdges, (Comparator.<Line>
                comparingDouble(edge1 -> edge1.getLength())
                .thenComparingDouble(edge2 -> edge2.getLength())));
        minLengthLine = sortedDelaunayEdges.get(0);
    }

    public ArrayList<Line> getShortestPathGraphEdges(double t) {
        boolean isInfinity = false;
        isInfinity = t == Double.POSITIVE_INFINITY ? true : false;
        t = isInfinity ? 1 : t;

        connections = new HashMap<>(uniqueCoordinates.size());
        ArrayList<Line> shortestPathGraphEdges = new ArrayList<>(edgeSet.size());
        for (Line DTEdge :sortedDelaunayEdges) {
            //Get shortest path from current SPG(t)
            ArrayList<Line> shortestPathFromSPG = getShortestPath(DTEdge, t);
            double shortestPathWeight = getWeight(shortestPathFromSPG, t);
            double edgeWeight = Math.pow(DTEdge.getLength()/minLengthLine.getLength(), t);
            int skipped = 0, added = 0;
            if ((!isInfinity && shortestPathWeight <= edgeWeight) || (isInfinity && shortestPathFromSPG != null)) {
                skipped++;
            } else {
                shortestPathGraphEdges.add(DTEdge);
                //add connection to endpoints
                Coordinate[] endPoints = DTEdge.getEndPoints();
                for (Coordinate endPoint : endPoints) {
                    if (connections.containsKey(endPoint)) {
                        ArrayList<Line> adjLines = connections.get(endPoint);
                        adjLines.add(DTEdge);
                    } else {
                        ArrayList<Line> adjLines = new ArrayList<>();
                        adjLines.add(DTEdge);
                        connections.put(endPoint, adjLines);
                    }
                }
                added++;
            }
        }
        return shortestPathGraphEdges;
    }

    private ArrayList<Line> getShortestPath(Line dtEdge, double t) {
        Coordinate[] endPoints = dtEdge.getEndPoints();

        //if one of end points does not have connections, there cannot be a path
        if (!connections.containsKey(endPoints[0]) || !connections.containsKey(endPoints[1])) {
            return null;
        }

        HashMap<Coordinate, ArrayList<Line>> shortestPaths = new HashMap<>();
        HashMap<Coordinate, Double> shortestPathsDistances = new HashMap<>();
        HashSet<Coordinate> completed = new HashSet<>();

        //find shortest path from endPoints[0] to endPoints[1]
        PriorityQueue<Coordinate> priorityQueue = new PriorityQueue<>((Comparator.<Coordinate>
                comparingDouble(point1 -> shortestPathsDistances.get(point1))
                .thenComparingDouble(point2 -> shortestPathsDistances.get(point2))));

        shortestPathsDistances.put(endPoints[0], 0.0);
        shortestPaths.put(endPoints[0], new ArrayList<>());
        priorityQueue.add(endPoints[0]);

        while(!priorityQueue.isEmpty()) {
            Coordinate tempPoint = priorityQueue.remove();

            //if point is already processed skip further processing
            if (completed.contains(tempPoint)) {
                continue;
            }

            completed.add(tempPoint);

            if (tempPoint == endPoints[1]) {
                return shortestPaths.get(tempPoint);
            }

            ArrayList<Line> adjLines = connections.get(tempPoint);

            for (Line adjLine: adjLines) {
                //find other coordinate
                Coordinate otherPoint = adjLine.getEndPoints()[0] == tempPoint ?
                        adjLine.getEndPoints()[1] : adjLine.getEndPoints()[0];

                ArrayList<Line> tempPath = new ArrayList<>(shortestPaths.get(tempPoint).size()+1);
                tempPath.addAll(shortestPaths.get(tempPoint));
                tempPath.add(adjLine);
                double tempPathWeight = getWeight(tempPath, t);
                //if tempPath is shorter than otherPoints current path, set tempPath as other points shortest path
                double currentPathWeight = shortestPathsDistances.containsKey(otherPoint) ?
                        shortestPathsDistances.get(otherPoint) : Double.POSITIVE_INFINITY;
                if (tempPathWeight < currentPathWeight) {
                    shortestPathsDistances.put(otherPoint, tempPathWeight);
                    shortestPaths.put(otherPoint, tempPath);
                    priorityQueue.add(otherPoint);
                }
            }
        }

        return shortestPaths.containsKey(endPoints[1]) ? shortestPaths.get(endPoints[1]) : null;
    }

    private double getWeight(ArrayList<Line> tempPath, double t) {
        if (tempPath == null) {
            return Double.POSITIVE_INFINITY;
        }

        double pathWeight = 0;

        for (Line edge : tempPath) {
            pathWeight += Math.pow(edge.getLength()/minLengthLine.getLength(), t);
        }

        return pathWeight;
    }
}
