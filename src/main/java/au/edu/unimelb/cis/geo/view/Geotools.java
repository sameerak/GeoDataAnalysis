package au.edu.unimelb.cis.geo.view;

import au.edu.unimelb.cis.geo.view.util.DatePanel;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.styling.*;
import org.geotools.swing.JMapFrame;
import org.geotools.swing.action.SafeAction;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.Point;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.FilterFactory2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static au.edu.unimelb.cis.geo.model.Resources.Lake_Michigan;
import static au.edu.unimelb.cis.geo.model.Resources.WorldMapFile;

/**
 * References
 * [1] https://www.geotools.org/
 * [2] http://docs.geotools.org/latest/userguide/tutorial/quickstart/index.html
 * [3] http://docs.geotools.org/latest/userguide/tutorial/quickstart/maven.html
 */
public class Geotools {

    public static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static DatePanel startDatePanel;
    private static DatePanel endDatePanel;

    private static MapContent map;
    private static Layer pointLayer = null;
    private static Layer trajectoryLayer = null;
    private static ArrayList<Layer> clusterLayers = null;
    private static JMapFrame mapFrame;

    public static void Start() throws ParseException, IOException {
        startDatePanel = new DatePanel(df.parse("2012-03-23 00:00:00"));
        endDatePanel = new DatePanel(df.parse("2012-03-24 00:00:00"));

        File file = new File(WorldMapFile);
        FileDataStore store = FileDataStoreFinder.getDataStore(file);
        SimpleFeatureSource featureSource = store.getFeatureSource();

        // Create a map content and add world map shape file
        map = new MapContent();
        map.setTitle("GPS Data Analysis");

        Style style = SLD.createSimpleStyle(featureSource.getSchema());
        Layer layer = new FeatureLayer(featureSource, style);
        map.addLayer(layer);

        mapFrame = new JMapFrame(map);
        mapFrame.enableToolBar(true);
        mapFrame.enableStatusBar(true);

        JToolBar toolBar = mapFrame.getToolBar();
        toolBar.addSeparator();

        //Start date and End date for GPS trajectory data processing
//        JPanel datePanel = new JPanel();
//        datePanel.add(startDatePanel);
//        datePanel.add(endDatePanel);
//        toolBar.add(datePanel);

        JPanel customPanel = new JPanel();
        customPanel.add(new JButton(new PlotLakeMichigan()));
        toolBar.add(customPanel);

        mapFrame.setSize(900, 600);
        mapFrame.setVisible(true);
    }

    public static void clearLayers() {
        if (pointLayer != null) {
            map.removeLayer(pointLayer);
            pointLayer = null;
        }
        if (trajectoryLayer != null) {
            map.removeLayer(trajectoryLayer);
            trajectoryLayer = null;
        }
    }

    static class PlotLakeMichigan extends SafeAction {

        PlotLakeMichigan() {
            super("LakeMichigan");
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
        }
    }

    /**
     * Create a Style to draw point features
     */
    private static Style createGeneralPointStyle() {
        StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory();
        FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory();
        Graphic gr = styleFactory.createDefaultGraphic();

        Mark mark = styleFactory.getCircleMark();

        mark.setStroke(styleFactory.createStroke(
                filterFactory.literal(Color.BLACK), filterFactory.literal(1)));

//            mark.setFill(styleFactory.createFill(filterFactory.literal(Color.MAGENTA)));
        StyleBuilder sb = new StyleBuilder();
        FilterFactory2 ff = sb.getFilterFactory();
        mark.setFill(styleFactory.createFill(/*filterFactory.literal(Color.CYAN)*/
                sb.attributeExpression("color")));

        gr.graphicalSymbols().clear();
        gr.graphicalSymbols().add(mark);
        gr.setSize(ff.property("size"));

        /*
         * Setting the geometryPropertyName arg to null signals that we want to
         * draw the default geomettry of features
         */
        PointSymbolizer sym = styleFactory.createPointSymbolizer(gr, null);

        Rule rule = styleFactory.createRule();
        rule.symbolizers().add(sym);
        FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(new Rule[]{rule});
        Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(fts);

        return style;
    }
}
