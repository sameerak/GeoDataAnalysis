package au.edu.unimelb.cis.geo.view.util;

import org.geotools.swing.action.SafeAction;
import org.locationtech.jts.geom.Coordinate;

import javax.swing.*;
import java.util.ArrayList;

import static au.edu.unimelb.cis.geo.model.Resources.Lake_Michigan;

public class PlotLakeMichigan {

    PlotLakeMichigan() {
    }

    private ArrayList<Coordinate> getLakeMichiganPointSet() {
        ArrayList<Coordinate> pointSet = new ArrayList<Coordinate>();
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
}
