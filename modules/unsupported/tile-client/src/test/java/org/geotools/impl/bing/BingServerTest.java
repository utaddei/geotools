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

import java.util.Map;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.tile.Tile;
import org.geotools.tile.WMTSource;
import org.geotools.tile.impl.bing.BingSource;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Envelope;

public class BingServerTest {

    private static CoordinateReferenceSystem MERCATOR_CRS;

    private static ReferencedEnvelope DE_EXTENT;

    private static ReferencedEnvelope BR_EXTENT;

    private static ReferencedEnvelope HAWAII_EXTENT;

    private static ReferencedEnvelope NZ_EXTENT;

    private static ReferencedEnvelope TZ_EXTENT;

    @BeforeClass
    public static void beforeClass() {
        try {
            MERCATOR_CRS = CRS.decode("EPSG:3857");

        } catch (FactoryException e) {
            e.printStackTrace();
            Assert.fail(e.getLocalizedMessage());
        }

        DE_EXTENT = new ReferencedEnvelope(new Envelope(6, 15, 47, 55),
                DefaultGeographicCRS.WGS84);

        BR_EXTENT = new ReferencedEnvelope(new Envelope(-75.118389, -33.458236,
                -32.745828, 5.380718), DefaultGeographicCRS.WGS84);

        HAWAII_EXTENT = new ReferencedEnvelope(new Envelope(-160.635967,
                -154.483623, 18.651309, 22.598660), DefaultGeographicCRS.WGS84);

        // hmmm failing near data line
        NZ_EXTENT = new ReferencedEnvelope(new Envelope(164.798799, 179.029327,
                -47.732492, -33.697613), DefaultGeographicCRS.WGS84);

        TZ_EXTENT = new ReferencedEnvelope(new Envelope(143.880831, 149.505830,
                -43.700251, -40.338803), DefaultGeographicCRS.WGS84);

    }

    @Test
    public void testGetTilesInExtent() {

        WMTSource server = new BingSource();

        // DE_EXTENT = new ReferencedEnvelope(new Envelope(6, 15, 47, 55),
        // DefaultGeographicCRS.WGS84);

        Map<String, Tile> tileList = server.cutExtentIntoTiles2(TZ_EXTENT,
                5957345, true, 15);
        System.out.println("size: " + tileList.size());

        Assert.assertEquals(4, tileList.size());

        for (Tile t : tileList.values()) {
            System.out.println(t.getId());
        }

    }
}
