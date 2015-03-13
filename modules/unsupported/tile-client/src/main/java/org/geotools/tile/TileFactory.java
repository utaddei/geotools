/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2015, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2004-2010, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotools.tile;

import org.geotools.tile.impl.ZoomLevel;

public abstract class TileFactory {

    public abstract Tile getTileFromCoordinate(double lon, double lat,
            ZoomLevel zoomLevel, TileService wmtSource);

    public abstract ZoomLevel getZoomLevel(int zoomLevel, TileService wmtSource);

    /**
     * uDig may produce numbers like -210° for the longitude, but we need a
     * number in the range -180 to 180, so instead of -210 we want 150.
     *
     * @param value the number to normalize (e.g. -210)
     * @param maxValue the maximum value (e.g. 180 -> the range is: -180..180)
     * @return a number between (-maxvalue) and maxvalue
     */
    public static double normalizeDegreeValue(double value, int maxValue) {
        int range = 2 * maxValue;

        if (value > 0) {

            value = (value + maxValue - 1) % range;

            if (value < 0) {
                value += range;
            }

            return (value - maxValue + 1);
        } else {
            value = (value + maxValue) % range;

            if (value < 0) {
                value += range;
            }

            return (value - maxValue);
        }
    }

    public abstract Tile findRightNeighbour(Tile tile, TileService service);

    public abstract Tile findLowerNeighbour(Tile tile, TileService service);

}
