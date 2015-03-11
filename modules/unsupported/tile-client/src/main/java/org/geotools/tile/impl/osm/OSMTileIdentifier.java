package org.geotools.tile.impl.osm;

import org.geotools.tile.TileIdentifier;
import org.geotools.tile.impl.ZoomLevel;

public class OSMTileIdentifier extends TileIdentifier {

    public OSMTileIdentifier(int x, int y, ZoomLevel zoomLevel,
            String sourceName) {
        super(x, y, zoomLevel, sourceName);
    }

    @Override
    public String getId() {
        final String separator = "_";
        return getZ() + separator + getX() + separator + getY();
    }

    @Override
    public String getCode() {
        final String separator = "/";
        return getZ() + separator + getX() + separator + getY() + ".png";
    }

    @Override
    public TileIdentifier getRightNeighbour() {
        return new OSMTileIdentifier(TileIdentifier.arithmeticMod((getX() + 1),
                getZoomLevel().getMaxTilePerRowNumber()), getY(),
                getZoomLevel(), getServiceName());
    }

    @Override
    public TileIdentifier getLowerNeighbour() {
        return new OSMTileIdentifier(getX(), TileIdentifier.arithmeticMod(
                (getY() + 1), getZoomLevel().getMaxTilePerRowNumber()),
                getZoomLevel(), getServiceName());
    }

}
