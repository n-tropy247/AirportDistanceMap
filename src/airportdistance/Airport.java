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

import java.util.Comparator;

/**
 * Handles individual objects from CSV lines.
 *
 * @author Ntropy
 * @version 9/3/2018
 */
public class Airport {

    /**
     * Latitude of airport.
     */
    private final double LATITUDE;
    
    /**
     * Longitude of airport.
     */
    private final double LONGITUDE;
    
    /**
     * IATA code.
     */
    private final String AIRPORT_NAME;

    /**
     * Assigns necessary tags to data from CSV.
     *
     * @param latitude
     *                  latitude of airport
     * @param longitude
     *                  longitude of airport
     * @param name
     *                  name of airport
     */
    public Airport(final double latitude, final double longitude,
            final String name) {
        this.LATITUDE = latitude;
        this.LONGITUDE = longitude;
        this.AIRPORT_NAME = name;
    }

    /**
     * Pass latitude to frame.
     * @return latitude of airport
     */
    double getLat() {
        return LATITUDE;
    }

    /**
     * Pass longitude to frame.
     * @return airport longitude
     */
    double getLong() {
        return LONGITUDE;
    }

    /**
     * Pass name to frame.
     * @return name of airport
     */
    String getName() {
        return AIRPORT_NAME;
    }

    /**
     * Comparator for airport names to sort by name.
     */
    public static Comparator<Airport> airportNameComparator = (Airport airport1,
            Airport airport2) -> {

        String name1 = airport1.getName().toUpperCase();
        String name2 = airport2.getName().toUpperCase();

        //ascending order
        return name1.compareTo(name2);
    };
}