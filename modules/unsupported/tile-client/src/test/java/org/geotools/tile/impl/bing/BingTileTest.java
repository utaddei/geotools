package org.geotools.tile.impl.bing;

import org.geotools.tile.Tile;
import org.geotools.tile.WMTSource;
import org.geotools.tile.impl.WebMercatorZoomLevel;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BingTileTest {

    private Tile tile;

    @Before
    public void beforeTest() {

        String baseURL = "http://ak.dynamic.t2.tiles.virtualearth.net/comp/ch/${code}?mkt=de-de&it=G,VE,BX,L,LA&shading=hill&og=78&n=z";
        WMTSource bingSource = new BingSource("Road", baseURL);
        BingTileIdentifier tileIdentifier = new BingTileIdentifier(10, 12,
                new WebMercatorZoomLevel(5), bingSource.getName());

        this.tile = new BingTile(tileIdentifier, bingSource);

    }

    @After
    public void afterTest() {

        this.tile = null;

    }

    @Test
    public void testConstructor() {

        Assert.assertNotNull(this.tile);

    }

    @Test
    public void testGetURL() {

        Assert.assertEquals(
                "http://ak.dynamic.t2.tiles.virtualearth.net/comp/ch/03210?mkt=de-de&it=G,VE,BX,L,LA&shading=hill&og=78&n=z",
                this.tile.getUrl().toString());

    }

}
