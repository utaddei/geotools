package org.geotools.tile.impl.bing;

import org.geotools.tile.TileIdentifier;
import org.geotools.tile.impl.ZoomLevel;

public class BingTileIdentifier extends TileIdentifier {

    private ZoomLevel zoomLevel;

    /**
     * Konstruktor für eine neue BingTileName.
     *
     * @param zoomLevel
     * @param x
     * @param y
     * @param source
     */
    public BingTileIdentifier(int x, int y, ZoomLevel zoomLevel,
            String sourceName) {
        super(x, y, zoomLevel, sourceName);
        this.zoomLevel = zoomLevel;
    }

    public BingTileIdentifier getRightNeighbour() {

        return new BingTileIdentifier(TileIdentifier.arithmeticMod(
                (getX() + 1), zoomLevel.getMaxTilePerRowNumber()), getY(),
                zoomLevel, getServiceName());
    }

    public BingTileIdentifier getLowerNeighbour() {

        return new BingTileIdentifier(getX(), TileIdentifier.arithmeticMod(
                (getY() + 1), zoomLevel.getMaxTilePerRowNumber()), zoomLevel,
                getServiceName());
    }

    public String getId() {
        return getServiceName() + "_" + getCode();
    }

    public String getCode() {
        return BingTileUtil.tileXYToQuadKey(this.getX(), this.getY(),
                this.getZoomLevel());
    }
}