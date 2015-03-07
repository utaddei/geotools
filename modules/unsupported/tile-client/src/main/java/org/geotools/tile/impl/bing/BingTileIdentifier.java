package org.geotools.tile.impl.bing;

import java.net.MalformedURLException;
import java.net.URL;

import org.geotools.tile.TileIdentifier;
import org.geotools.tile.WMTSource;
import org.geotools.tile.impl.ZoomLevel;

public class BingTileIdentifier extends TileIdentifier {

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
    public BingTileIdentifier(int x, int y, ZoomLevel zoomLevel, WMTSource source) {
        super(x, y, zoomLevel, source);
        this.zoomLevel = zoomLevel;
        this.source = source;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.locationtech.udig.catalog.internal.wmt.tile.TileIdentifier#getTileUrl()
     */
    @Override
    @Deprecated
    public URL getTileUrl() {
        try {
            return new URL(null, ((BingSource) source).getTileUrl(
                    zoomLevel.getZoomLevel(), getX(), getY()));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public BingTileIdentifier getRightNeighbour() {

        return new BingTileIdentifier(TileIdentifier.arithmeticMod((getX() + 1),
                zoomLevel.getMaxTilePerRowNumber()), getY(), zoomLevel, source);
    }

    public BingTileIdentifier getLowerNeighbour() {

        return new BingTileIdentifier(getX(), TileIdentifier.arithmeticMod(
                (getY() + 1), zoomLevel.getMaxTilePerRowNumber()), zoomLevel,
                source);
    }

    public String getId() {
        return this.source.getName()
                + "_"
                + BingTileUtil.tileXYToQuadKey(this.getX(), this.getY(),
                        this.getZoomLevel());
    }

    public String getCode() {
        return BingTileUtil.tileXYToQuadKey(this.getX(), this.getY(),
                this.getZoomLevel());
    }
}