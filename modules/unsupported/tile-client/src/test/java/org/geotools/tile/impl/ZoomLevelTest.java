package org.geotools.tile.impl;

import org.junit.Assert;
import org.junit.Test;

public class ZoomLevelTest {

    @Test
    public void testConstructor() {
        Assert.assertNotNull(createZoomLevel(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalConstructor() {
        createZoomLevel(-10);
    }

    private ZoomLevel createZoomLevel(int z) {
        return new ZoomLevel(z) {

            public int calculateMaxTilePerRowNumber(int zoomLevel) {
                return 12;
            }

            public int calculateMaxTilePerColNumber(int zoomLevel) {
                return 14;
            }
        };
    }

}
