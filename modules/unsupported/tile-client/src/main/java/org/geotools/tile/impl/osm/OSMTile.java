package org.geotools.tile.impl.osm;

import java.net.URL;

import org.geotools.tile.Tile;
import org.geotools.tile.TileIdentifier;
import org.geotools.tile.WMTSource;
import org.geotools.tile.impl.WebMercatorTileFactory;
import org.geotools.tile.impl.ZoomLevel;

public class OSMTile extends Tile {

    public static final int DEFAULT_TILE_SIZE = 256;

    private WMTSource source;

    public OSMTile(int x, int y, ZoomLevel zoomLevel, WMTSource osmSource) {
        this(new OSMTileIdentifier(x, y, zoomLevel, osmSource.getName()),
                osmSource);
    }

    public OSMTile(TileIdentifier tileName, WMTSource osmSource) {
        super(WebMercatorTileFactory.getExtentFromTileName(tileName),
                DEFAULT_TILE_SIZE, tileName);

        this.source = osmSource;
    }

    @Override
    public URL getUrl() {
        String url = this.source.getBaseUrl().replace("${code}",
                getTileIdentifier().getCode());
        System.out.println(getTileIdentifier().getCode() + " > " + url);
        try {
            return new URL(url);
        } catch (Exception e) {
            final String mesg = "Cannot create URL from " + url;
            throw new RuntimeException(mesg, e);
        }
    }
}
