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

package au.edu.unimelb.cis.geo.view;

import au.edu.unimelb.cis.geo.controller.DelaunayTriangulation;
import au.edu.unimelb.cis.geo.model.Line;
import au.edu.unimelb.cis.geo.view.button.ExportSVG;
import au.edu.unimelb.cis.geo.view.button.PlotExperimentPoints;
import au.edu.unimelb.cis.geo.view.button.PlotLakeMichigan;
import au.edu.unimelb.cis.geo.view.util.DatePanel;
import org.geotools.data.DataUtilities;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.SchemaException;
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
import static au.edu.unimelb.cis.geo.view.util.UIUtils.createGeneralPointStyle;
import static au.edu.unimelb.cis.geo.view.util.UIUtils.getLineFeature;

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
    private static JMapFrame mapFrame;

    private static String[] graphNameStrings = { "Delaunay Triangulation", "Gabriel Graph",
            "Stepping Stone Graph", "Diversion Graph", "Shortest Path Graph"};
    private static String[] configurationValues = { "2", "3", "4", "8", "16", "INFINITY"};

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

        JComboBox CMBgraphList = new JComboBox(graphNameStrings);
        CMBgraphList.setSelectedIndex(0);

        JComboBox CMBconfigValuesList = new JComboBox(configurationValues);
        CMBconfigValuesList.setSelectedIndex(0);

        JPanel customPanel = new JPanel();
        customPanel.add(CMBgraphList);
        customPanel.add(CMBconfigValuesList);
        customPanel.add(new JButton(new PlotLakeMichigan(map, CMBgraphList, CMBconfigValuesList)));
        customPanel.add(new JButton(new PlotExperimentPoints(map, CMBgraphList, CMBconfigValuesList)));
        toolBar.add(customPanel);

        JPanel savePanel = new JPanel();
        savePanel.add(new JButton(new ExportSVG(mapFrame, map)));
        toolBar.add(savePanel);

        mapFrame.setSize(900, 600);
        mapFrame.setVisible(true);
    }
}
