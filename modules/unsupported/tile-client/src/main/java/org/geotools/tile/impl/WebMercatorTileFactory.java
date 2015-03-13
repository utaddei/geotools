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
package org.geotools.tile.impl;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.tile.TileFactory;
import org.geotools.tile.TileIdentifier;
import org.geotools.tile.TileService;

public abstract class WebMercatorTileFactory extends TileFactory {

    @Override
    public ZoomLevel getZoomLevel(int zoomLevel, TileService wmtSource) {

        return new WebMercatorZoomLevel(zoomLevel);
    }

    public static ReferencedEnvelope getExtentFromTileName(
            TileIdentifier tileName) {

        final int z = tileName.getZ();

        ReferencedEnvelope extent = new ReferencedEnvelope(tile2lon(
                tileName.getX(), z), tile2lon(tileName.getX() + 1, z),
                tile2lat(tileName.getY(), z), tile2lat(tileName.getY() + 1, z),
                DefaultGeographicCRS.WGS84);

        return extent;
    }

    public static final double tile2lon(double x, int z) {
        return (x / Math.pow(2.0, z) * 360.0) - 180;
    }

    public static final double tile2lat(double y, int z) {
        double n = Math.PI - ((2.0 * Math.PI * y) / Math.pow(2.0, z));
        return 180.0 / Math.PI * Math.atan(0.5 * (Math.exp(n) - Math.exp(-n)));
    }

}
