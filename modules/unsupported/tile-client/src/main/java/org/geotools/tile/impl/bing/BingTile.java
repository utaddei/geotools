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
package org.geotools.tile.impl.bing;

import java.net.MalformedURLException;
import java.net.URL;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.tile.Tile;
import org.geotools.tile.TileIdentifier;
import org.geotools.tile.WMTSource;
import org.geotools.tile.impl.ZoomLevel;

/**
 * TODO Klassenbeschreibung für 'BingTile'
 *
 * @author Ugo Taddei
 * @version $Revision: $
 */
public class BingTile extends Tile {

    public static final int DEFAULT_TILE_SIZE = 256;

    private BingTileName tileName;

    private WMTSource source;

    public static class BingTileName extends TileIdentifier {

        private ZoomLevel zoomLevel;

        private WMTSource source;

        /**
         * Konstruktor für eine neue BingTileName.
         *
         * @param zoomLevel
         * @param x
         * @param y
         * @param source
         */
        public BingTileName(int x, int y, ZoomLevel zoomLevel, WMTSource source) {
            super(zoomLevel, x, y, source);
            this.zoomLevel = zoomLevel;
            this.source = source;
        }

        /**
         * {@inheritDoc}
         *
         * @see org.locationtech.udig.catalog.internal.wmt.tile.TileIdentifier#getTileUrl()
         */
        @Override
        public URL getTileUrl() {
            try {
                return new URL(null, ((BingSource) source).getTileUrl(zoomLevel.getZoomLevel(),
                        getX(), getY()));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            return null;
        }

        public BingTileName getRightNeighbour() {

            return new BingTileName(TileIdentifier.arithmeticMod((getX() + 1),
                    zoomLevel.getMaxTilePerRowNumber()), getY(), zoomLevel, source);
        }

        public BingTileName getLowerNeighbour() {

            return new BingTileName(getX(), TileIdentifier.arithmeticMod((getY() + 1),
                    zoomLevel.getMaxTilePerRowNumber()), zoomLevel, source);
        }
    }

    /**
     * Konstruktor für eine neue BingTile.
     *
     * @param extent
     * @param tileName
     */
    public BingTile(int x, int y, ZoomLevel zoomLevel, WMTSource bingSource) {
        this(new BingTileName(x, y, zoomLevel, bingSource), bingSource);
    }

    public BingTile(BingTileName tileName, WMTSource bingSource) {

        super(BingTile.getExtentFromTileName(tileName), DEFAULT_TILE_SIZE, tileName);

        this.tileName = tileName;
        this.source = bingSource;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.locationtech.udig.catalog.internal.wmt.tile.WMTTile#getRightNeighbour()
     */
    @Override
    public Tile getRightNeighbour() {

        return new BingTile(tileName.getRightNeighbour(), source);

    }

    public static ReferencedEnvelope getExtentFromTileName(BingTileName tileName) {

        final int z = tileName.getZoomLevel();

        ReferencedEnvelope extent = new ReferencedEnvelope(tile2lon(tileName.getX(), z), tile2lon(
                tileName.getX() + 1, z), tile2lat(tileName.getY(), z), tile2lat(
                        tileName.getY() + 1, z), DefaultGeographicCRS.WGS84);

        return extent;
    }

    private static double tile2lon(double x, int z) {
        return (x / Math.pow(2.0, z) * 360.0) - 180;
    }

    private static double tile2lat(double y, int z) {
        double n = Math.PI - ((2.0 * Math.PI * y) / Math.pow(2.0, z));
        return 180.0 / Math.PI * Math.atan(0.5 * (Math.exp(n) - Math.exp(-n)));
    }

    /**
     * {@inheritDoc}
     *
     * @see org.locationtech.udig.catalog.internal.wmt.tile.WMTTile#getLowerNeighbour()
     */
    public Tile getLowerNeighbour() {

        return new BingTile(tileName.getLowerNeighbour(), source);
    }

}
