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

package au.edu.unimelb.cis.geo.view.util;

import org.geotools.data.DataUtilities;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.styling.*;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.FilterFactory2;

import java.awt.*;

public class UIUtils {
    public static double[] CONFIGURATION_VALUES = { 2, 3, 4, 8, 16, Double.POSITIVE_INFINITY};

    /**
     * Create a Style to draw point features
     */
    public static Style createGeneralPointStyle() {
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

    /**
     * Get a line for a given set of coordinates
     */
    public static SimpleFeature getLineFeature(Coordinate[] coords) throws SchemaException {
        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
        LineString line = geometryFactory.createLineString(coords);
        SimpleFeatureType LINETYPE = DataUtilities.createType("test", "line", "the_geom:LineString");
        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder((SimpleFeatureType) LINETYPE);
        featureBuilder.add(line);
        SimpleFeature feature = featureBuilder.buildFeature(null);

        return feature;
    }
}
