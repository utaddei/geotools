/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2015, Open Source Geospatial Foundation (OSGeo)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotools.impl.bing;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geotools.ServerTest;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.tile.Tile;
import org.geotools.tile.WMTSource;
import org.geotools.tile.impl.bing.BingSource;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class BingServerTest extends ServerTest {

    private static Map<String, List<String>> extentNameToUrlList;

    @BeforeClass
    public static void beforeClass() {
        ServerTest.beforeClass();

        extentNameToUrlList = new HashMap<String, List<String>>();
        List<String> expectedUrls_DE = Arrays.asList(new String[] {
                "http://ak.dynamic.t2.tiles.virtualearth.net/comp/ch/12022",
                "http://ak.dynamic.t2.tiles.virtualearth.net/comp/ch/12021",
                "http://ak.dynamic.t2.tiles.virtualearth.net/comp/ch/12023",
                "http://ak.dynamic.t2.tiles.virtualearth.net/comp/ch/12020" });
        extentNameToUrlList.put(ServerTest.DE_EXTENT_NAME, expectedUrls_DE);

    }

    @Test
    public void testGetTilesInExtent_DE() {

        Collection<Tile> tiles = testGetTilesInExtent(getExtent(ServerTest.DE_EXTENT_NAME));

        Assert.assertEquals(4, tiles.size());

        List<String> expectedUrls = Arrays.asList(new String[] {
                "http://ak.dynamic.t2.tiles.virtualearth.net/comp/ch/12022",
                "http://ak.dynamic.t2.tiles.virtualearth.net/comp/ch/12021",
                "http://ak.dynamic.t2.tiles.virtualearth.net/comp/ch/12023",
                "http://ak.dynamic.t2.tiles.virtualearth.net/comp/ch/12020" });

        Assert.assertEquals(expectedUrls.size(), tiles.size());

        for (Tile t : tiles) {
            expectedUrls.contains(t.getUrl().toString());
        }
    }

    private Collection<Tile> testGetTilesInExtent(ReferencedEnvelope extent) {

        WMTSource server = new BingSource("nothing");

        Map<String, Tile> tileList = server.cutExtentIntoTiles2(extent,
                5957345, true, 15);

        return tileList.values();
    }

    public List<String> getUrlList(String extentName) {
        return extentNameToUrlList.get(extentName);
    }
}
