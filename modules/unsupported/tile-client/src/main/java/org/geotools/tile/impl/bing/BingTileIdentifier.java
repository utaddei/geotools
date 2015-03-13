package org.geotools.tile.impl.bing;

import org.geotools.tile.TileIdentifier;
import org.geotools.tile.impl.ZoomLevel;

public class BingTileIdentifier extends TileIdentifier {

    /**
     * Konstruktor für eine neue BingTileName.
     *
     * @param zoomLevel
     * @param x
     * @param y
     * @param serviceName
     */
    public BingTileIdentifier(int x, int y, ZoomLevel zoomLevel,
            String serviceName) {
        super(x, y, zoomLevel, serviceName);
    }

    public BingTileIdentifier getRightNeighbour() {

        return new BingTileIdentifier(TileIdentifier.arithmeticMod(
                (getX() + 1), getZoomLevel().getMaxTilePerRowNumber()), getY(),
                getZoomLevel(), getServiceName());
    }

    public BingTileIdentifier getLowerNeighbour() {

        return new BingTileIdentifier(getX(), TileIdentifier.arithmeticMod(
                (getY() + 1), getZoomLevel().getMaxTilePerRowNumber()),
                getZoomLevel(), getServiceName());
    }

    public String getId() {
        return getServiceName() + "_" + getCode();
    }

    public String getCode() {
        return BingTileUtil.tileXYToQuadKey(this.getX(), this.getY(),
                this.getZ());
    }
}