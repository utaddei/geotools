package org.geotools.tile.impl;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.tile.Tile;
import org.geotools.tile.TileIdentifier;

public abstract class WebMercatorTile extends Tile {

    public WebMercatorTile(ReferencedEnvelope env, int tileSize,
            TileIdentifier tileId) {
        super(env, tileSize, tileId);
    }

    public static ReferencedEnvelope getExtentFromTileName(
            TileIdentifier tileName) {

        final int z = tileName.getZ();

        ReferencedEnvelope extent = new ReferencedEnvelope(
                WebMercatorTileFactory.tile2lon(tileName.getX(), z),
                WebMercatorTileFactory.tile2lon(tileName.getX() + 1, z),
                WebMercatorTileFactory.tile2lat(tileName.getY(), z),
                WebMercatorTileFactory.tile2lat(tileName.getY() + 1, z),
                DefaultGeographicCRS.WGS84);

        return extent;
    }
}
