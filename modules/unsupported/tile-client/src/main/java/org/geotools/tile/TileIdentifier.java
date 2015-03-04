/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2015, Open Source Geospatial Foundation (OSGeo)
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

import java.net.URL;

import org.geotools.tile.impl.ZoomLevel;

/**
 * A TileIdentifier locates a tile in the grid space of a given tile server by
 * giving its column, row and zoom level. (Class formerly know as
 * "WMTTileName".)
 *
 * @author Tobias Sauerwein
 * @author Ugo Taddei
 */
public abstract class TileIdentifier {

    public static final String ID_DIVIDER = "_"; //$NON-NLS-1$

    private int x;

    private int y;

    private ZoomLevel zoomLevel;

    private WMTSource source;

    public TileIdentifier(int x, int y, ZoomLevel zoomLevel, WMTSource source) {

        this.x = x;
        this.y = y;
        this.zoomLevel = zoomLevel;
        this.source = source;
    }

    public int getZoomLevel() {
        return this.zoomLevel.getZoomLevel();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public WMTSource getSource() {
        return source;
    }

    public abstract String getId();

    @Deprecated
    public String _getId() {
        return source.getId() + ID_DIVIDER + getZoomLevel() + ID_DIVIDER
                + getX() + ID_DIVIDER + getY();
    }

    @Deprecated
    public abstract URL getTileUrl();

    public abstract String getCode();

    /**
     * Arithmetic implementation of modulo, as the Java implementation of modulo
     * can return negative values.
     *
     * <pre>
     * arithmeticMod(-1, 8) = 7
     * </pre>
     *
     * @param a
     * @param b
     * @return the positive remainder
     */
    public static int arithmeticMod(int a, int b) {
        return (a >= 0) ? a % b : a % b + b;
    }

}
