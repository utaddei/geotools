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

import org.geotools.tile.Tile;
import org.geotools.tile.TileService;
import org.geotools.tile.impl.WebMercatorTileFactory;
import org.geotools.tile.impl.ZoomLevel;

/**
 * TODO Klassenbeschreibung für 'BingTileFactory'
 *
 * @author Ugo Taddei
 * @version $Revision: $
 */
class BingTileFactory extends WebMercatorTileFactory {

    /**
     * {@inheritDoc}
     *
     * @see org.locationtech.udig.catalog.internal.wmt.tile.WMTTile.WMTTileFactory#findTileAtCoordinate(double,
     *      double,
     *      org.locationtech.udig.catalog.internal.wmt.tile.WMTTile.WMTZoomLevel,
     *      org.TileService.udig.catalog.internal.wmt.wmtsource.WMTSource)
     */
    public Tile findTileAtCoordinate(double lon, double lat,
            ZoomLevel zoomLevel, TileService service) {

        int[] tileXY = BingTileUtil.lonLatToPixelXY(lon, lat,
                zoomLevel.getZoomLevel());

        int colX = (int) Math.floor(tileXY[0] / BingTile.DEFAULT_TILE_SIZE);
        int rowY = (int) Math.floor(tileXY[1] / BingTile.DEFAULT_TILE_SIZE);

        return new BingTile(colX, rowY, zoomLevel, service);
    }

    @Override
    public Tile findRightNeighbour(Tile tile, TileService service) {
        return new BingTile(tile.getTileIdentifier().getRightNeighbour(),
                service);
    }

    @Override
    public Tile findLowerNeighbour(Tile tile, TileService service) {
        return new BingTile(tile.getTileIdentifier().getLowerNeighbour(),
                service);
    }

}