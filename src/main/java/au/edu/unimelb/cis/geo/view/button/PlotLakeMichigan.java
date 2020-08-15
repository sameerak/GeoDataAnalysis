package au.edu.unimelb.cis.geo.view.button;

import au.edu.unimelb.cis.geo.controller.*;
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
import static au.edu.unimelb.cis.geo.view.util.UIUtils.*;

public class PlotLakeMichigan extends SafeAction {
    private MapContent map;
    private Layer pointLayer;
    private Layer DelaunayTriangulationLayer;
    private Layer gabrielGraphLayer;
    private Layer steppingStoneGraphLayer;
    private JComboBox CMBgraphList, CMBconfigValuesList;

    public PlotLakeMichigan(MapContent map, JComboBox CMBgraphList, JComboBox CMBconfigValuesList) {
        super("LakeMichigan");
        this.map = map;
        this.CMBgraphList = CMBgraphList;
        this.CMBconfigValuesList = CMBconfigValuesList;
        putValue(Action.SHORT_DESCRIPTION, "Plot boundary points of Michigan Lake");
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
        if (DelaunayTriangulationLayer != null) {
            map.removeLayer(DelaunayTriangulationLayer);
        }
        if (gabrielGraphLayer != null) {
            map.removeLayer(gabrielGraphLayer);
        }
        if (steppingStoneGraphLayer != null) {
            map.removeLayer(steppingStoneGraphLayer);
        }

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

        DelaunayTriangulation delaunayTriangulation = new DelaunayTriangulation(uniqueLocalities);
        ArrayList<Line> DelaunayEdges = delaunayTriangulation.getDelaunayEdges();

        if (CMBgraphList.getSelectedIndex() == 0) {
            DefaultFeatureCollection lineCollection = new DefaultFeatureCollection();
            for (int i = 0; i < DelaunayEdges.size(); i++) {
                lineCollection.add(getLineFeature(DelaunayEdges.get(i).getEndPoints()));
            }

            Style linestyle = SLD.createLineStyle(Color.red, 0.1F);
            DelaunayTriangulationLayer = new FeatureLayer(lineCollection, linestyle);
            map.addLayer(DelaunayTriangulationLayer);
        } else if (CMBgraphList.getSelectedIndex() == 1) {
            GabrielGraph gabrielGraph = new GabrielGraph(delaunayTriangulation);
            ArrayList<Line> gabrielEdges = gabrielGraph.getEdgeList();
            DefaultFeatureCollection gabrielLineCollection = new DefaultFeatureCollection();

            for (int i = 0; i < gabrielEdges.size(); i++) {
                gabrielLineCollection.add(getLineFeature(gabrielEdges.get(i).getEndPoints()));
            }

            Style gabrielLineStyle = SLD.createLineStyle(Color.blue, 0.1F);
            gabrielGraphLayer = new FeatureLayer(gabrielLineCollection, gabrielLineStyle);
            map.addLayer(gabrielGraphLayer);
        } else if (CMBgraphList.getSelectedIndex() == 2) {
            SteppingStoneGraph steppingStoneGraph = new SteppingStoneGraph(delaunayTriangulation);
            ArrayList<Line> steppingStoneGraphEdges =
                    steppingStoneGraph.getSteppingStoneGraphEdges(
                            CONFIGURATION_VALUES[CMBconfigValuesList.getSelectedIndex()]);
            DefaultFeatureCollection SSGLineCollection = new DefaultFeatureCollection();

            for (int i = 0; i < steppingStoneGraphEdges.size(); i++) {
                SSGLineCollection.add(getLineFeature(steppingStoneGraphEdges.get(i).getEndPoints()));
            }

            Style SSGLineStyle = SLD.createLineStyle(Color.green, 0.1F);
            steppingStoneGraphLayer = new FeatureLayer(SSGLineCollection, SSGLineStyle);
            map.addLayer(steppingStoneGraphLayer);
        } else if (CMBgraphList.getSelectedIndex() == 3) {
            DiversionGraph newGraph = new DiversionGraph(delaunayTriangulation);
            ArrayList<Line> newGraphEdges = newGraph.getNewGraph(
                    CONFIGURATION_VALUES[CMBconfigValuesList.getSelectedIndex()]);
            DefaultFeatureCollection NGLineCollection = new DefaultFeatureCollection();

            for (int i = 0; i < newGraphEdges.size(); i++) {
                NGLineCollection.add(getLineFeature(newGraphEdges.get(i).getEndPoints()));
            }

            Style NGLineStyle = SLD.createLineStyle(Color.magenta, 0.1F);
            steppingStoneGraphLayer = new FeatureLayer(NGLineCollection, NGLineStyle);
            map.addLayer(steppingStoneGraphLayer);
        } else if (CMBgraphList.getSelectedIndex() == 4) {
            ShortestPathGraph newGraph = new ShortestPathGraph(delaunayTriangulation);
            ArrayList<Line> newGraphEdges = newGraph.getShortestPathGraphEdges(
                    CONFIGURATION_VALUES[CMBconfigValuesList.getSelectedIndex()]);
            DefaultFeatureCollection NGLineCollection = new DefaultFeatureCollection();

            for (int i = 0; i < newGraphEdges.size(); i++) {
                NGLineCollection.add(getLineFeature(newGraphEdges.get(i).getEndPoints()));
            }

            Style NGLineStyle = SLD.createLineStyle(Color.cyan, 0.1F);
            steppingStoneGraphLayer = new FeatureLayer(NGLineCollection, NGLineStyle);
            map.addLayer(steppingStoneGraphLayer);
        }
    }
}
