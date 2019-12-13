package au.edu.unimelb.cis.geo.view.button;


import au.edu.unimelb.cis.geo.controller.DelaunayTriangulation;
import au.edu.unimelb.cis.geo.model.Line;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.swing.action.SafeAction;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static au.edu.unimelb.cis.geo.view.util.UIUtils.createGeneralPointStyle;
import static au.edu.unimelb.cis.geo.view.util.UIUtils.getLineFeature;

public class PlotExperimentPoints extends SafeAction {
    private MapContent map;
    private Layer pointLayer;
    private Layer DelaunayTriangulation;

    public PlotExperimentPoints(MapContent map) {
        super("ExperimentPoints");
        this.map = map;
        putValue(Action.SHORT_DESCRIPTION, "Plot points necessary for experiments.");
    }

    private void clearLayers() {
        if (pointLayer != null) {
            map.removeLayer(pointLayer);
        }
        if (DelaunayTriangulation != null) {
            map.removeLayer(DelaunayTriangulation);
        }
    }

    private Set<Coordinate> getExperimentPointSet() {
        clearLayers();

        Set<Coordinate> pointSet = new HashSet<Coordinate>();
        //Tetrahedron points
        pointSet.add(new Coordinate(1d, 1.5d));
        pointSet.add(new Coordinate(2d, 4d));
        pointSet.add(new Coordinate(2.5d, 2.5d));
        pointSet.add(new Coordinate(4.5d, 2d));

        //Pyramid points
        pointSet.add(new Coordinate(1d, 0.5d));
        pointSet.add(new Coordinate(1d, 1.5d));
        pointSet.add(new Coordinate(1.8d, 1d));
        pointSet.add(new Coordinate(3d, 0.5d));
        pointSet.add(new Coordinate(4.5d, 2d));

        return pointSet;
    }

    public void action(ActionEvent e) throws Throwable {
        SimpleFeatureTypeBuilder b = new SimpleFeatureTypeBuilder();
        b.setName("PlotUserDataFeatureType");
        b.setCRS(DefaultGeographicCRS.WGS84);
        b.add("location", Point.class);
        b.add("D-Value", Double.class);
        b.add("color", String.class);
        b.add("size", Integer.class);
        // building the type
        final SimpleFeatureType TYPE = b.buildFeatureType();
        SimpleFeatureBuilder featureBuilder;
        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
        DefaultFeatureCollection featureCollection = new DefaultFeatureCollection("internal", TYPE);

        Set<Coordinate> experimentLocalities = getExperimentPointSet();

        for (Coordinate locality : experimentLocalities) {
            Point point = geometryFactory.createPoint(locality);
            featureBuilder = new SimpleFeatureBuilder(TYPE);
            featureBuilder.add(point);
            featureBuilder.add("N/A");
            featureBuilder.add(Color.darkGray);
            featureBuilder.add(5);
            SimpleFeature feature = featureBuilder.buildFeature(locality.toString());
            featureCollection.add(feature);
        }

        Style style = createGeneralPointStyle();
        pointLayer = new FeatureLayer(featureCollection, style);
        map.addLayer(pointLayer);

        DefaultFeatureCollection lineCollection = new DefaultFeatureCollection();
        DelaunayTriangulation DTCreator = new DelaunayTriangulation();
        ArrayList<Line> DelaunayEdges = DTCreator.createDelaunayTriangulation(experimentLocalities);

        for (int i = 0; i < DelaunayEdges.size(); i++) {
            lineCollection.add(getLineFeature(DelaunayEdges.get(i).getEndPoints()));
        }

        Style lineStyle = SLD.createLineStyle(Color.red, 0.1F);
        DelaunayTriangulation = new FeatureLayer(lineCollection, lineStyle);
        map.addLayer(DelaunayTriangulation);
    }
}
