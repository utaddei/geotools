package org.geotools.tile.impl.bing;

import org.geotools.tile.TileIdentifier;
import org.geotools.tile.TileIdentifierTest;
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

    protected TileIdentifier createTestTileIdentifier(ZoomLevel zoomLevel,
            int x, int y, String name) {
        return new BingTileIdentifier(x, y, zoomLevel, name);

    }
}
