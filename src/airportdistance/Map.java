/*
 * Copyright (C) 2018 castellir
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package airportdistance;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.Dimension;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import java.io.IOException;
import java.io.File;

import javax.swing.JFrame;

import java.util.Arrays;

/**
 * US Airports from CSV.
 *
 * @author NTropy
 * @version 9/3/2018
 */
final class Map extends JFrame implements ActionListener {

    /**
     * Array of airports that can be selected.
     */
    private final Airport[] airports;

    /**
     * Background image of frame.
     */
    private final BackGroundPanel BACKGROUND_PANEL;

    /**
     * Map image.
     */
    private final BufferedImage BACKGROUND;

    /**
     * Button to send input to GUI.
     */
    private Button distance;

    /**
     * Object that contains coordinate methods.
     */
    private final Coords coord;

    /**
     * Radius of earth, used for haversine.
     */
    private static final double EARTH_RAD = 6378.1;

    /**
     * Conversion factor for kilometers to miles.
     */
    private static final double KM_TO_MI = 0.621371192;

    /**
     * Horizontal and vertical positions of airports selected.
     */
    private int x1, x2, y1, y2;

    /**
     * Number of airports in CSV.
     */
    private static final int AIRPORT_TOTAL = 956;

    /**
     * Input character length.
     */
    private static final int INPUT_LENGTH = 15;

    /**
     * Output character length.
     */
    private static final int OUTPUT_LENGTH = 10;

    /**
     * Window width. Matches image.
     */
    private static final int WINDOW_WIDTH = 492;

    /**
     * Window height. Matches image.
     */
    private static final int WINDOW_HEIGHT = 333;

    /**
     * Map container.
     */
    private static JFrame mainFrame;

    /**
     * Panel for input/output.
     */
    private Panel calc;

    /**
     * First IATA code.
     */
    private TextField input1;
    /**
     * Second IATA code.
     */
    private TextField input2;
    /**
     * Distance calculated.
     */
    private TextField output;

    /**
     * Initializes JFrame.
     *
     * @param args
     *              command-line arguments
     */
    public static void main(final String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                mainFrame = new Map();
            } catch (IOException ie) {
                System.err.print(ie);
            }
        });
    }

    /**
     * Constructor for Map frame.
     *
     * @throws IOException
     *                      file not found
     */
    private Map() throws IOException {
        this.airports = new Airport[AIRPORT_TOTAL];
        this.BACKGROUND_PANEL = new BackGroundPanel();
        //background image
        this.BACKGROUND = ImageIO.read(new File("./resources/Us2.png"));
        //creates coord obj, class that handles indiv. airport objects
        this.coord = new Coords();

        try {
            //scans through array of airports
            for (int j = 0; j < airports.length; j++) {
                //creates each airport using coord obj
                airports[j] = coord.createAirports(j);
            }
        } catch (IOException e) {
            System.err.println(e);
        }
        Arrays.sort(airports, Airport.airportNameComparator);
        init();
    }

    /**
     * Sets settings for JFrame.
     */
    private void init() {
        requestFocus(); //sets focus to frame

        //sets size of frame to map size
        setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));

        //grid based on pre-determined page locations
        setLayout(new BorderLayout());

        add(BACKGROUND_PANEL, BorderLayout.CENTER); //graphics panel

        calc = new Panel();

        input1 = new TextField(INPUT_LENGTH); //field for first airport

        calc.add(input1);

        input1.setText("Airport 1");
        input1.addFocusListener(new FocusListenerImpl(true));

        input2 = new TextField(INPUT_LENGTH); //field for second airport

        calc.add(input2);

        input2.setText("Airport 2");
        input2.addFocusListener(new FocusListenerImpl(false));

        //creates button to calc distance
        distance = new Button("Calc Distance");
        calc.add(distance);

        output = new TextField(OUTPUT_LENGTH); //field for distance output
        output.setEditable(false);
        calc.add(output);

        output.setText("0");

        //tells the button to perform the action detailed within "this" class
        distance.addActionListener(this);

        add(calc, BorderLayout.PAGE_END); //adds calc panel to bottom of frame

        pack(); //maintains size of graphics objects

        setTitle("Airport Distance Calculator");

        distance.requestFocusInWindow(); //sets default focus to button

        setLocationRelativeTo(null); //centers on screen

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setVisible(true);

        setResizable(false);
    }

    /**
     * Calculates distance between airports using haversine function.
     *
     * @param airport1
     *                  first entered airport
     * @param airport2
     *                  second entered airport
     */
    private void distance(final String airport1, final String airport2) {
        //variables for distance
        double firstLat;
        double secondLat;
        double firstLong;
        double secondLong;

        //resets coord values for drawing in points on map
        x1 = 0;
        x2 = 0;
        y1 = 0;
        y2 = 0;

        firstLat = 0;
        secondLat = 0;
        firstLong = 0;
        secondLong = 0;

        for (Airport airport : airports) { //scans through array of airports
            //checks if current airport's IATA code is the same as
            //the desired IATA code
            if (airport.getName().equals(airport1)) {
                //sets lat and long, then uses coord obj to get x/y
                //coords for point on map
                firstLat = airport.getLat();
                firstLong = airport.getLong();
                x1 = (int) coord.correctX(airport);
                y1 = (int) coord.correctY(airport);
            }
        }

        for (Airport airport : airports) { //scans through array of airports
            //checks if current airport's IATA code is the same as
            //the desired IATA code
            if (airport.getName().equals(airport2)) {
                //sets lat and long, then uses coord obj to get x/y
                //coords for point on map
                secondLat = airport.getLat();
                secondLong = airport.getLong();
                x2 = (int) coord.correctX(airport);
                y2 = (int) coord.correctY(airport);
            }
        }
        if (x1 == 0 || y1 == 0) {
            output.setText("?Airport1"); //errors if airport1 has a coord of 0
        } else if (x2 == 0 || y2 == 0) {
            output.setText("?Airport2"); //errors if airport2 has a coord of 0
        } else {
            output.setText(String.format("%.2f", (haversine(firstLat,
                    firstLong, secondLat, secondLong) * KM_TO_MI))
                    + " miles"); //sets output text to distance in miles
        }
    }

    /**
     * Handles haversine calculations.
     *
     * @param lat1
     *              latitude of first airport
     * @param lon1
     *              longitude of first airport
     * @param lat2
     *              latitude of second airport
     * @param lon2
     *              longitude of second airport
     * @return distance between airports
     */
    private double haversine(final double lat1, final double lon1,
            final double lat2, final double lon2) {
        double dLat = Math.toRadians(lat2 - lat1) / 2;
        double dLon = Math.toRadians(lon2 - lon1) / 2;

        double radLat1 = Math.toRadians(lat1);
        double radLat2 = Math.toRadians(lat2);

        return (EARTH_RAD * 2) * Math.asin(Math.sqrt(Math.pow(Math.sin(dLat), 2)
                + Math.pow(Math.sin(dLon), 2)
                        * Math.cos(radLat1) * Math.cos(radLat2)));
    }

    /**
     * On button press, actionPerformed is invoked and receives input then
     * refreshes frame.
     *
     * @param ae
     *          ActionEvent sent by button press
     */
    @Override
    public void actionPerformed(final ActionEvent ae) {
        distance(input1.getText(), input2.getText());
        BACKGROUND_PANEL.repaint();
    }

    /**
     * Class for background of frame.
     */
    private final class BackGroundPanel extends Panel {

        /**
         * Radius of single marker on map.
         */
        private static final int POINT_RAD = 2;

        /**
         * Width of lines drawn between markers.
         */
        private static final int STROKE_WIDTH = 3;
        /**
         * Constructor for background object.
         */
        private BackGroundPanel() {
            super();
        }

        /**
         * Draws components onto frame.
         *
         * @param g
         *          Graphics object for frame
         */
        @Override
        public void paint(final Graphics g) {
            g.drawImage(BACKGROUND, 0, 0, null);
            g.setColor(Color.red);
            g.fillOval(x1, y1, POINT_RAD, POINT_RAD);
            g.fillOval(x2, y2, POINT_RAD, POINT_RAD);
            super.paint(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(STROKE_WIDTH));
            g2.drawLine(x1, y1, x2, y2);
        }

    }

    /**
     * Checks for focus on entry field.
     */
    private final class FocusListenerImpl implements FocusListener {

        /**
         * Tracks is field is empty when focus released.
         */
        private final boolean FIELD;

        /**
         * Tracks if focus is active on field.
         * @param f
         *          whether or not field is active.
         */
        private FocusListenerImpl(final boolean f) {
            this.FIELD = f;
        }

        /**
         * On click, clears entry box.
         *
         * @param e
         *          FocusEvent sent on focus gained
         */
        @Override
        public void focusGained(final FocusEvent e) {
            if (FIELD) {
                input1.setText("");
            } else {
                input2.setText("");
            }
        }

        /**
         * Does nothing, necessary override.
         *
         * @param e
         *          FocusEvent sent on focus lost
         */
        @Override
        public void focusLost(final FocusEvent e) {
            if (FIELD) {
                if (input1.getText().isEmpty()) {
                    input1.setText("Airport 1");
                }
            } else {
                if (input2.getText().isEmpty()) {
                    input2.setText("Airport 2");
                }
            }
        }
    }
}