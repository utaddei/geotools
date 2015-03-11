package org.geotools.tile.impl.bing;

import org.geotools.tile.impl.ZoomLevel;

@Deprecated
public class BingZoomLevel extends ZoomLevel {

    public BingZoomLevel(int zoomLevel) {
        super(zoomLevel);
    }

    /**
     * The maximum tile-number: For example at zoom-level 2, the tilenames are
     * in the following range: 2/0/0 - 2/3/3 (zoom-level/x/y):
     * zoom-level/2^(zoom-level)-1/2^(zoom-level)-1)
     */
    public int calculateMaxTilePerColNumber(int zoomLevel) {
        return (1 << zoomLevel); // 2 ^ (zoomLevel)
    }

    public int calculateMaxTilePerRowNumber(int zoomLevel) {
        return calculateMaxTilePerColNumber(zoomLevel);
    }
}