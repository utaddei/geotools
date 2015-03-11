package org.geotools.tile.impl.bing;

import org.geotools.tile.TileIdentifier;
import org.geotools.tile.impl.WebMercatorZoomLevel;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BingTileIdentifierTest {

    private TileIdentifier tileId;

    @Before
    public void beforeTest() {

        this.tileId = new BingTileIdentifier(10, 12,
                new WebMercatorZoomLevel(5), "SomeName");

    }

    @After
    public void afterTest() {

        this.tileId = null;

    }

    @Test
    public void testConstructor() {
        Assert.assertNotNull(this.tileId);

    }

    @Test
    public void testX() {
        Assert.assertEquals(10, this.tileId.getX());
    }

    @Test
    public void testY() {
        Assert.assertEquals(12, this.tileId.getY());
    }

    @Test
    public void testZ() {
        Assert.assertEquals(5, this.tileId.getZ());
    }

    @Test
    public void testGetId() {
        Assert.assertEquals("SomeName_03210", this.tileId.getId());
    }

    @Test
    public void testGetCode() {
        Assert.assertEquals("03210", this.tileId.getCode());
    }

    @Test
    public void testGetServiceName() {
        Assert.assertEquals("SomeName", this.tileId.getServiceName());
    }

}
