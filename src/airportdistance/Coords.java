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

import java.util.List;
import java.io.IOException;
import java.io.FileReader;
import openCSV.CSVReader;

/**
 * Coordinates of airports corrected for display on-screen
 * using GPS coordinates.
 *
 * @author NTropy
 * @version 9/3/2018
 */
public class Coords {

    /**
     * Temporary container for sorting airports.
     */
    private Airport airportTemp;

    /**
     * Vertical position of marker on map.
     */
    private double y;
    /**
     * Horizontal position of marker on map.
     */
    private double x;

    /**
     * List of airport names.
     */
    private List<String[]> list;

    /**
     * File name of CSV.
     */
    private final String importName;

    /**
     * Array of CSV data.
     */
    private String[][] dataArr;

    /**
     * Constructor for Coords handler.
     */
    public Coords() {
        importName = "./resources/airports.csv";
    }

    /**
     * Creates airport from CSV with GPS coordinates and airport name.
     *
     * @param j
     *          Placement of data in CSV column.
     * @return airport object
     * @throws IOException
     */
    Airport createAirports(int j) throws IOException {

        CSVReader reader = new CSVReader(new FileReader(importName));

        list = reader.readAll(); //gets list of values
        dataArr = new String[list.size()][]; //makes string list
        dataArr = list.toArray(dataArr);  //makes array

        //grabs data from indiv "cells" in csv
        airportTemp = new Airport(Double.parseDouble(dataArr[j + 1][0]),
                Double.parseDouble(dataArr[j + 1][1]), dataArr[j + 1][2]);

        return airportTemp; //passes airport object back
    }

    /**
     * Finds "x" position on image from latitude.
     *
     * @param airport
     *              airport passed to find horizontal position on map
     * @return x position
     */
    double correctX(Airport airport) {
        x = ((120.0 - airport.getLat())) * (387.0 / 50.0) + 64.0;
        return x;
    }

    /**
     * Finds "y" position on image from longitude.
     *
     * @param airport
     *              airport passed to find horizontal position on map
     * @return y position
     */
    double correctY(Airport airport) {
        y = (50.5 - airport.getLong()) * (244.0 / 25.0) + 44.0;
        return y;
    }
}