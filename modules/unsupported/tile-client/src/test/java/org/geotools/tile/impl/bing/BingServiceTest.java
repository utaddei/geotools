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
package org.geotools.tile.impl.bing;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridCoverageFactory;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.MapViewport;
import org.geotools.tile.ServiceTest;
import org.geotools.tile.Tile;
import org.geotools.tile.WMTSource;
import org.geotools.tile.impl.bing.BingSource;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

public class BingServiceTest extends ServiceTest {

    private static Map<String, List<String>> extentNameToUrlList;

    @BeforeClass
    public static void beforeClass() {
        ServiceTest.beforeClass();

        extentNameToUrlList = new HashMap<String, List<String>>();
        List<String> expectedUrls_DE = Arrays.asList(new String[] {
                "http://ak.dynamic.t2.tiles.virtualearth.net/comp/ch/12022",
                "http://ak.dynamic.t2.tiles.virtualearth.net/comp/ch/12021",
                "http://ak.dynamic.t2.tiles.virtualearth.net/comp/ch/12023",
                "http://ak.dynamic.t2.tiles.virtualearth.net/comp/ch/12020" });
        extentNameToUrlList.put(DE_EXTENT_NAME, expectedUrls_DE);

        List<String> expectedUrls_BR = Arrays
                .asList(new String[] {
                        "http://ak.dynamic.t2.tiles.virtualearth.net/comp/ch/2112000123",
                        "http://ak.dynamic.t2.tiles.virtualearth.net/comp/ch/2112000031",
                        "http://ak.dynamic.t2.tiles.virtualearth.net/comp/ch/2112000120",
                        "http://ak.dynamic.t2.tiles.virtualearth.net/comp/ch/2112000121",
                        "http://ak.dynamic.t2.tiles.virtualearth.net/comp/ch/2112000033",
                        "http://ak.dynamic.t2.tiles.virtualearth.net/comp/ch/2112000122" });
        extentNameToUrlList.put(BR_EXTENT_NAME, expectedUrls_BR);

        List<String> expectedUrls_HAWAII = Arrays.asList(new String[] {
                "http://ak.dynamic.t2.tiles.virtualearth.net/comp/ch/022211",
                "http://ak.dynamic.t2.tiles.virtualearth.net/comp/ch/022300",
                "http://ak.dynamic.t2.tiles.virtualearth.net/comp/ch/022033",
                "http://ak.dynamic.t2.tiles.virtualearth.net/comp/ch/022122" });
        extentNameToUrlList.put(HAWAII_EXTENT_NAME, expectedUrls_HAWAII);

    }

    @Test
    public void testGetTilesInExtents() {

        testGetTilesInExtent(DE_EXTENT_NAME, 5957345);
        testGetTilesInExtent(BR_EXTENT_NAME, 500000);
        testGetTilesInExtent(HAWAII_EXTENT_NAME, 5957345);

    }

    @Test
    public void testCoverages() {

        testCoverage(DE_EXTENT_NAME, 20000000);
        testCoverage(BR_EXTENT_NAME, 750000);

    }

    private void testCoverage(String envName, int scale) {

        System.out.println(getExtent(envName).getWidth());

        ReferencedEnvelope env = getExtent(envName);

        String baseURL = "http://ak.dynamic.t2.tiles.virtualearth.net/comp/ch/${code}?mkt=de-de&it=G,VE,BX,L,LA&shading=hill&og=78&n=z";
        WMTSource server = new BingSource("nothing", baseURL);
        Map<String, Tile> tileList = server.cutExtentIntoTiles2(env, scale,
                true, 128);

        Collection<Tile> tiles = tileList.values();

        Dimension size = new Dimension((int) (env.getWidth() * 1000),
                (int) (env.getWidth() * 1000));
        final BufferedImage bufferedImage = new BufferedImage(size.width,
                size.height, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = bufferedImage.createGraphics();
        // g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
        // RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        GridCoverage2D coverage = null;
        double[] points = new double[4];

        try {

            // use instead of viewport??
            // DefaultImageCRS
            // MathTransform mathTransform = CRS.findMathTransform(
            // env.getCoordinateReferenceSystem(), new DefaultImageCRS());

            MapViewport viewport = new MapViewport();
            viewport.setBounds(env);
            viewport.setScreenArea(new Rectangle(size));
            AffineTransform worldToImageTransform = viewport.getWorldToScreen();

            for (Tile tile : tiles) {
                ReferencedEnvelope nativeTileEnvelope = tile.getExtent();

                ReferencedEnvelope tileEnvViewport = nativeTileEnvelope
                        .transform(env.getCoordinateReferenceSystem(), true);

                points[0] = tileEnvViewport.getMinX();
                points[3] = tileEnvViewport.getMinY();
                points[2] = tileEnvViewport.getMaxX();
                points[1] = tileEnvViewport.getMaxY();

                // mathTransform.transform(points, 0, points, 0, 2);
                worldToImageTransform.transform(points, 0, points, 0, 2);

                BufferedImage img = tile.getBufferedImage();

                // try {
                // ImageIO.write(img, "png", new FileOutputStream(
                // "/home/ugo/test_" + tile.getId() + ".png"));
                // } catch (Exception e) {
                // e.printStackTrace();
                // }

                g2d.drawImage(img, (int) points[0], (int) points[1],
                        (int) Math.ceil(points[2] - points[0]),
                        (int) Math.ceil(points[3] - points[1]), null);

            }

            GridCoverageFactory factory = new GridCoverageFactory();
            coverage = factory.create("GridCoverage", bufferedImage, env);

        } catch (TransformException e) {
            // LOGGER.log(Level.FINER, e.getMessage(), e);
            e.printStackTrace();
            return;
        } catch (FactoryException e) {
            // LOGGER.log(Level.FINER, e.getMessage(), e);
            e.printStackTrace();
            return;
        }

        RenderedImage image = coverage.getRenderedImage();

        try {
            ImageIO.write(image, "png", new FileOutputStream(
                    "/home/ugo/temp/test_" + envName + ".png"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void testGetTilesInExtent(final String extentName, int scale) {

        Collection<Tile> tiles = findTilesInExtent(getExtent(extentName), scale);

        List<String> expectedUrls = getUrlList(extentName);
        Assert.assertEquals(expectedUrls.size(), tiles.size());

        for (Tile t : tiles) {
            expectedUrls.contains(t.getUrl().toString());
            System.out.println(t.getUrl().toString());
        }
    }

    private Collection<Tile> findTilesInExtent(ReferencedEnvelope extent,
            int scale) {

        String baseURL = "http://ak.dynamic.t2.tiles.virtualearth.net/comp/ch/${code}?mkt=de-de&it=G,VE,BX,L,LA&shading=hill&og=78&n=z";
        WMTSource server = new BingSource("nothing", baseURL);
        Map<String, Tile> tileList = server.cutExtentIntoTiles2(extent, scale,
                true, 28);

        return tileList.values();
    }

    public List<String> getUrlList(String extentName) {
        return extentNameToUrlList.get(extentName);
    }
}
