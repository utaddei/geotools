package org.geotools.tile.impl.bing;

import org.geotools.tile.TileIdentifier;
import org.geotools.tile.TileIdentifierTest;
import org.geotools.tile.impl.WebMercatorZoomLevel;
import org.geotools.tile.impl.ZoomLevel;
import org.junit.Assert;
import org.junit.Test;

public class BingTileIdentifierTest extends TileIdentifierTest {

    @Test
    public void testGetId() {
        Assert.assertEquals("SomeService_03210", this.tileId.getId());
    }

    @Test
    public void testGetCode() {
        Assert.assertEquals("03210", this.tileId.getCode());
    }

    @Test
    public void testGetRightNeighbour() {
        BingTileIdentifier neighbour = new BingTileIdentifier(11, 12,
                new WebMercatorZoomLevel(5), "SomeService");

        Assert.assertEquals(neighbour, this.tileId.getRightNeighbour());
    }

    @Test
    public void testGetLowertNeighbour() {
        BingTileIdentifier neighbour = new BingTileIdentifier(10, 13,
                new WebMercatorZoomLevel(5), "SomeService");

        Assert.assertEquals(neighbour, this.tileId.getLowerNeighbour());
    }

    protected TileIdentifier createTestTileIdentifier(ZoomLevel zoomLevel,
            int x, int y, String name) {
        return new BingTileIdentifier(x, y, zoomLevel, name);

    }
}
