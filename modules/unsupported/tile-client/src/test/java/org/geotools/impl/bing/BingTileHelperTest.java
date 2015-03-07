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

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.tile.impl.bing.BingTileUtil;
import org.junit.Assert;
import org.junit.Test;

public class BingTileHelperTest {

    @Test
    public void testLonLatToPixelXY() {

        int[] pixelXY = BingTileUtil.lonLatToPixelXY(7, 51, 5);
        System.out.println(Arrays.toString(pixelXY));
    }

    @Test
    public void tesLonLatgToPixelXYAndBack() {

        double[] coords = { 7, 51 };

        int levelOfDetail = 5;

        int[] pixelXY = BingTileUtil.lonLatToPixelXY(coords[0], coords[1],
                levelOfDetail);
        System.out.println(Arrays.toString(pixelXY));

        double[] calculatedCoords = BingTileUtil.pixelXYToLonLat(pixelXY[0],
                pixelXY[1], levelOfDetail);

        System.out.println(Arrays.toString(calculatedCoords));
    }

    @Test
    public void testLonLatToTileQuadRaw() {

        double[] coords = { 7, 51 };

        int levelOfDetail = 5;

        int[] pixelXY = BingTileUtil.lonLatToPixelXY(coords[0], coords[1],
                levelOfDetail);
        int[] tileXY = BingTileUtil.pixelXYToTileXY(pixelXY[0], pixelXY[1]);

        String quadKey = BingTileUtil.tileXYToQuadKey(tileXY[0], tileXY[1],
                levelOfDetail);

        System.out.println(quadKey);

    }

    @Test
    public void testLonLatZoomToTileQuad() {

        double[] coords = { 7, 51 };
        int levelOfDetail = 5;

        String quadKey = BingTileUtil.lonLatToQuadKey(coords[0], coords[1],
                levelOfDetail);

        Assert.assertEquals("12020", quadKey);

    }

    @Test
    public void testGetTileBoundingBox() {

        double[] coords = { 7, 51 };
        int levelOfDetail = 5;

        ReferencedEnvelope env = BingTileUtil.getTileBoundingBox(coords[0],
                coords[1], levelOfDetail);

        System.out.println(env);

        // Assert.assertEquals("12020", quadKey);

    }

}