package org.geotools.tile;

import org.geotools.tile.impl.WebMercatorZoomLevel;
import org.geotools.tile.impl.ZoomLevel;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TileIdentifierTest {

    private TileIdentifier tileId;

    @Before
    public void beforeTest() {

        this.tileId = createTestTileIdentifier(5, 10, 12, "SomeService");

    }

    @After
    public void afterTest() {

        this.tileId = null;

    }

    @Test
    public void testConstructor() {
        Assert.assertNotNull(this.tileId);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalZ() {
        createTestTileIdentifier(-1, 10, 12, "SomeService");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullZ() {
        createTestTileIdentifier(null, 10, 12, "SomeService");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalX() {
        createTestTileIdentifier(1, -10, 12, "SomeService");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalY() {
        createTestTileIdentifier(1, 10, -12, "SomeService");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalServiceName() {
        createTestTileIdentifier(1, 10, 12, null);
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
        Assert.assertEquals("SomeService_0123", this.tileId.getId());
    }

    @Test
    public void testGetCode() {
        Assert.assertEquals("0123", this.tileId.getCode());
    }

    @Test
    public void testGetServiceName() {
        Assert.assertEquals("SomeService", this.tileId.getServiceName());
    }

    private TileIdentifier createTestTileIdentifier(int z, int x, int y,
            String name) {
        return createTestTileIdentifier(new WebMercatorZoomLevel(z), x, y, name);
    };

    private TileIdentifier createTestTileIdentifier(ZoomLevel zoomLevel, int x,
            int y, String name) {

        return new TileIdentifier(x, y, zoomLevel, name) {

            @Override
            public String getId() {
                return getServiceName() + "_" + getCode();
            }

            @Override
            public String getCode() {
                return "0123";
            }

            @Override
            public TileIdentifier getRightNeighbour() {
                return null;
            }

            @Override
            public TileIdentifier getLowerNeighbour() {
                return null;
            }

        };
    }

}
