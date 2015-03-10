package org.geotools.impl.bing;

import org.geotools.tile.Tile;
import org.geotools.tile.WMTSource;
import org.geotools.tile.impl.bing.BingSource;
import org.geotools.tile.impl.bing.BingTile;
import org.geotools.tile.impl.bing.BingTileIdentifier;
import org.geotools.tile.impl.bing.BingZoomLevel;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BingTileTest {

    private Tile tile;

    @Before
    public void beforeTest() {

        WMTSource bingSource = new BingSource("Road");
        BingTileIdentifier tileIdentifier = new BingTileIdentifier(10, 12,
                new BingZoomLevel(5), bingSource);

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

        System.out.println(this.tile.getUrl());
        System.out.println(">" + this.tile.getClass().getPackage().getName()
                + "<");
        System.out.println(">" + this.tile.getExtent());

        Assert.assertEquals(
                "http://ak.dynamic.t2.tiles.virtualearth.net/comp/ch/03210?mkt=de-de&it=G,VE,BX,L,LA&shading=hill&og=78&n=z",
                this.tile.getUrl().toString());

    }

}
