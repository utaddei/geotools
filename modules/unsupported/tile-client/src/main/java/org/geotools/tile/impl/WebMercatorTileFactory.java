package org.geotools.tile.impl;

import org.geotools.tile.TileFactory;
import org.geotools.tile.WMTSource;

public abstract class WebMercatorTileFactory extends TileFactory {

    /**
     * {@inheritDoc}
     *
     * @see org.locationtech.udig.catalog.internal.wmt.tile.WMTTile.WMTTileFactory#getZoomLevel(int,
     *      org.locationtech.udig.catalog.internal.wmt.wmtsource.WMTSource)
     */
    @Override
    public ZoomLevel getZoomLevel(int zoomLevel, WMTSource wmtSource) {

        return new WebMercatorZoomLevel(zoomLevel);
    }

    public static final double tile2lon(double x, int z) {
        return (x / Math.pow(2.0, z) * 360.0) - 180;
    }

    public static final double tile2lat(double y, int z) {
        double n = Math.PI - ((2.0 * Math.PI * y) / Math.pow(2.0, z));
        return 180.0 / Math.PI * Math.atan(0.5 * (Math.exp(n) - Math.exp(-n)));
    }

}
