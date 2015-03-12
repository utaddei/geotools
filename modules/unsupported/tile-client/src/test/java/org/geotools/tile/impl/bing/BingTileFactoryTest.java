package org.geotools.tile.impl.bing;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.tile.Tile;
import org.geotools.tile.TileFactory;
import org.geotools.tile.TileFactoryTest;
import org.geotools.tile.WMTSource;
import org.geotools.tile.impl.WebMercatorTileFactory;
import org.geotools.tile.impl.WebMercatorZoomLevel;
import org.junit.Assert;
import org.junit.Test;

public class BingTileFactoryTest extends TileFactoryTest {

    @Test
    public void testGetTileFromCoordinate() {

        Tile tile = factory.getTileFromCoordinate(51, 7,
                new WebMercatorZoomLevel(5), createSource());

        WMTSource service = createSource();
        BingTile expectedTile = new BingTile(20, 15,
                new WebMercatorZoomLevel(5), service);
        Assert.assertEquals(expectedTile, tile);

    }

    @Test
    public void testFindRightNeighbour() {

        WMTSource service = createSource();
        BingTile tile = new BingTile(20, 15, new WebMercatorZoomLevel(5),
                service);

        Tile neighbour = factory.findRightNeighbour(tile, service);

        BingTile expectedNeighbour = new BingTile(21, 15,
                new WebMercatorZoomLevel(5), service);

        Assert.assertEquals(expectedNeighbour, neighbour);

    }

    @Test
    public void testFindLowerNeighbour() {

        WMTSource service = createSource();
        BingTile tile = new BingTile(20, 15, new WebMercatorZoomLevel(5),
                service);

        Tile neighbour = factory.findLowerNeighbour(tile, service);

        BingTile expectedNeighbour = new BingTile(20, 16,
                new WebMercatorZoomLevel(5), service);

        Assert.assertEquals(expectedNeighbour, neighbour);

    }

    @Test
    public void testGetExtentFromTileName() {

        BingTileIdentifier tileId = new BingTileIdentifier(10, 12,
                new WebMercatorZoomLevel(5), "SomeName");
        BingTile tile = new BingTile(tileId, new BingSource("2", "d"));

        ReferencedEnvelope env = WebMercatorTileFactory
                .getExtentFromTileName(tileId);

        Assert.assertEquals(tile.getExtent(), env);
        ReferencedEnvelope envRaw = new ReferencedEnvelope(-67.5, -56.25,
                31.95216223802496, 40.97989806962013,
                DefaultGeographicCRS.WGS84);

        Assert.assertEquals(envRaw, env);

    }

    private WMTSource createSource() {
        String baseURL = "http://ak.dynamic.t2.tiles.virtualearth.net/comp/ch/${code}?mkt=de-de&it=G,VE,BX,L,LA&shading=hill&og=78&n=z";
        return new BingSource("Road", baseURL);

    }

    protected TileFactory createFactory() {
        return new BingTileFactory();
    }
}
