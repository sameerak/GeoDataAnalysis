package au.edu.unimelb.cis.geo.view.button;

import au.edu.unimelb.cis.geo.controller.DelaunayTriangulation;
import au.edu.unimelb.cis.geo.controller.GabrielGraph;
import au.edu.unimelb.cis.geo.controller.SteppingStoneGraph;
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

import static au.edu.unimelb.cis.geo.model.Resources.Lake_Michigan;
import static au.edu.unimelb.cis.geo.view.util.UIUtils.createGeneralPointStyle;
import static au.edu.unimelb.cis.geo.view.util.UIUtils.getLineFeature;

public class PlotLakeMichigan extends SafeAction {
    private MapContent map;
    private Layer pointLayer;
    private Layer DelaunayTriangulation;
    private Layer gabrielGraphLayer;
    private Layer steppingStoneGraphLayer;

    public PlotLakeMichigan(MapContent map) {
        super("LakeMichigan");
        this.map = map;
        putValue(Action.SHORT_DESCRIPTION, "Plot boundary points of Michigan Lake");
    }

    private void clearLayers() {
        if (pointLayer != null) {
            map.removeLayer(pointLayer);
        }
        if (DelaunayTriangulation != null) {
            map.removeLayer(DelaunayTriangulation);
        }
        if (gabrielGraphLayer != null) {
            map.removeLayer(gabrielGraphLayer);
        }
    }

    private Set<Coordinate> getLakeMichiganPointSet() {
        Set<Coordinate> pointSet = new HashSet<Coordinate>();
        int i = 0, j = 0;

        String[] lat_long_pairs = Lake_Michigan.split(",");

        for (String coordinate : lat_long_pairs) {
            String[] lat_long_pair = coordinate.split(" ");
            pointSet.add(new Coordinate(Double.parseDouble(lat_long_pair[0]), Double.parseDouble(lat_long_pair[1]),
                    0));
            ++i;
        }
        return pointSet;
    }

    public void action(ActionEvent e) throws Throwable {
        clearLayers();

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

        Set<Coordinate> uniqueLocalities = getLakeMichiganPointSet();

        for (Coordinate locality : uniqueLocalities) {
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
        ArrayList<Line> DelaunayEdges = DTCreator.createDelaunayTriangulation(uniqueLocalities);

        for (int i = 0; i < DelaunayEdges.size(); i++) {
            lineCollection.add(getLineFeature(DelaunayEdges.get(i).getEndPoints()));
        }

        Style linestyle = SLD.createLineStyle(Color.red, 0.1F);
        DelaunayTriangulation = new FeatureLayer(lineCollection, linestyle);
        map.addLayer(DelaunayTriangulation);

        DefaultFeatureCollection gabrielLineCollection = new DefaultFeatureCollection();
        GabrielGraph gabrielGraph = new GabrielGraph(DTCreator);
        ArrayList<Line> gabrielEdges = gabrielGraph.getEdgeList();

        for (int i = 0; i < gabrielEdges.size(); i++) {
            gabrielLineCollection.add(getLineFeature(gabrielEdges.get(i).getEndPoints()));
        }

        Style gabrielLineStyle = SLD.createLineStyle(Color.blue, 0.1F);
        gabrielGraphLayer = new FeatureLayer(gabrielLineCollection, gabrielLineStyle);
        map.addLayer(gabrielGraphLayer);

        DefaultFeatureCollection SSGLineCollection = new DefaultFeatureCollection();
        SteppingStoneGraph steppingStoneGraph = new SteppingStoneGraph(DTCreator);
        ArrayList<Line> steppingStoneGraphEdges = steppingStoneGraph.getSteppingStoneGraphEdges(4);

        for (int i = 0; i < steppingStoneGraphEdges.size(); i++) {
            SSGLineCollection.add(getLineFeature(steppingStoneGraphEdges.get(i).getEndPoints()));
        }

        Style SSGLineStyle = SLD.createLineStyle(Color.green, 0.1F);
        steppingStoneGraphLayer = new FeatureLayer(SSGLineCollection, SSGLineStyle);
        map.addLayer(steppingStoneGraphLayer);
    }
}
