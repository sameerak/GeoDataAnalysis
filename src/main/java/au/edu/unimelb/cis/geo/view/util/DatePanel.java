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

import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.awt.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DatePanel extends JPanel {
    private JDateChooser dateChooser;
    private JSpinner timeSpinner;

    public DatePanel (Date value) {
        super();
        this.dateChooser = new JDateChooser();
        if (value != null) {
            dateChooser.setDate(value);
        }
        for (Component comp : dateChooser.getComponents()) {
            if (comp instanceof JTextField) {
                ((JTextField) comp).setColumns(50);
                ((JTextField) comp).setEditable(false);
            }
        }

        this.add(dateChooser);

        SpinnerModel model = new SpinnerDateModel();
        this.timeSpinner = new JSpinner(model);
        JComponent editor = new JSpinner.DateEditor(timeSpinner, "HH:mm:ss");
        timeSpinner.setEditor(editor);
        if(value != null) {
            timeSpinner.setValue(value);
        }

        this.add(timeSpinner);
        this.setLayout(new WrapLayout());
    }

    public Date GetDate() {
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat dftime = new SimpleDateFormat("HH:mm:ss");
        DateFormat dfreal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date date = dateChooser.getDate();
        Date time = (Date) timeSpinner.getValue();

        String val = dfdate.format(date) + " " + dftime.format(time);
        dfreal.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date ret = new Date();
        try {
            ret = dfreal.parse(val);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return ret;
    }
}
