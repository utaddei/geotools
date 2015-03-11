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

import org.geotools.tile.impl.ZoomLevel;

/**
 * A TileIdentifier locates a tile in the grid space of a given tile server by
 * giving its column, row and zoom level. The main resposibility of a
 * TileIdentifier is to translate the grid values (zoom, x, y) into a "code"
 * using an algorithm which denotes the tile in a given server implementation. <br/>
 * For example, OpenStreetMap identifies the tile by z/x/y.png; Bing Maps uses a
 * quad key representation. <br/>
 * This class formerly known as "WMTTileName".
 *
 * @author Tobias Sauerwein
 * @author Ugo Taddei
 * @since 12
 * @source $URL:
 *         http://svn.osgeo.org/geotools/trunk/modules/unsupported/tile-client
 *         /src/main/java/org/geotools/tile/impl/bing/BingTileIdentifier.java $
 */
public abstract class TileIdentifier {

    @Deprecated
    private static final String ID_DIVIDER = "_"; //$NON-NLS-1$

    private int x;

    private int y;

    private ZoomLevel zoomLevel;

    private String sourceName;

    @Deprecated
    private WMTSource source;

    public TileIdentifier(int x, int y, ZoomLevel zoomLevel, String sourceName) {

        this.x = x;
        this.y = y;
        this.zoomLevel = zoomLevel;
        this.sourceName = sourceName;
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

    @Deprecated
    public WMTSource getSource() {
        return source;
    }

    public String getSourceName() {
        return this.sourceName;
    }

    public abstract String getId();

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