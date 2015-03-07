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

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultGeographicCRS;

import com.vividsolutions.jts.geom.Envelope;

/**
 * TODO Klassenbeschreibung für 'BingTileHelper' Code ported from
 * https://msdn.microsoft.com/en-us/library/bb259689.aspx
 *
 * @author Ugo Taddei
 * @version $Revision: $
 */
public final class BingTileUtil {

    private static final double MIN_LONGITUDE = -180;

    private static final double MIN_LATITUDE = -85.05112878;

    private static final double MAX_LONGITUDE = 180;

    private static final double MAX_LATITUDE = 85.05112878;

    private BingTileUtil() {
        // utility class
    }

    /**
     * /// <summary> /// Converts a point from latitude/longitude WGS-84
     * coordinates (in degrees) /// into pixel XY coordinates at a specified
     * level of detail. /// </summary> /// <param name="latitude">Latitude of
     * the point, in degrees.</param> /// <param name="longitude">Longitude of
     * the point, in degrees.</param> /// <param name="levelOfDetail">Level of
     * detail, from 1 (lowest detail) /// to 23 (highest detail).</param> ///
     * <param name="pixelX">Output parameter receiving the X coordinate in
     * pixels.</param> /// <param name="pixelY">Output parameter receiving the Y
     * coordinate in pixels.</param> Diese Methode wird verwendet um... TODO.
     *
     * @param longitude
     * @param latitude
     * @param levelOfDetail
     * @return
     */
    public static int[] lonLatToPixelXY(double longitude, double latitude,
            int levelOfDetail) {
        double _latitude = clip(latitude, MIN_LATITUDE, MAX_LATITUDE);
        double _longitude = clip(longitude, MIN_LONGITUDE, MAX_LONGITUDE);

        double x = (_longitude + 180) / 360;
        double sinLatitude = Math.sin(_latitude * Math.PI / 180);
        double y = 0.5 - Math.log((1 + sinLatitude) / (1 - sinLatitude))
                / (4 * Math.PI);

        int mapSize = mapSize(levelOfDetail);
        int pixelX = (int) clip(x * mapSize + 0.5, 0, mapSize - 1);
        int pixelY = (int) clip(y * mapSize + 0.5, 0, mapSize - 1);

        return new int[] { pixelX, pixelY };
    }

    // / <summary>
    // / Converts a pixel from pixel XY coordinates at a specified level of
    // detail
    // / into latitude/longitude WGS-84 coordinates (in degrees).
    // / </summary>
    // / <param name="pixelX">X coordinate of the point, in pixels.</param>
    // / <param name="pixelY">Y coordinates of the point, in pixels.</param>
    // / <param name="levelOfDetail">Level of detail, from 1 (lowest detail)
    // / to 23 (highest detail).</param>
    // / <param name="latitude">Output parameter receiving the latitude in
    // degrees.</param>
    // / <param name="longitude">Output parameter receiving the longitude in
    // degrees.</param>
    public static double[] pixelXYToLonLat(int pixelX, int pixelY,
            int levelOfDetail) {
        double mapSize = mapSize(levelOfDetail);
        double x = (clip(pixelX, 0, mapSize - 1) / mapSize) - 0.5;
        double y = 0.5 - (clip(pixelY, 0, mapSize - 1) / mapSize);

        double latitude = 90 - 360 * Math.atan(Math.exp(-y * 2 * Math.PI))
                / Math.PI;
        double longitude = 360 * x;

        return new double[] { longitude, latitude };
    }

    // / <summary>
    // / Determines the map width and height (in pixels) at a specified level
    // / of detail.
    // / </summary>
    // / <param name="levelOfDetail">Level of detail, from 1 (lowest detail)
    // / to 23 (highest detail).</param>
    // / <returns>The map width and height in pixels.</returns>
    public static int mapSize(int levelOfDetail) {
        return BingTile.DEFAULT_TILE_SIZE << levelOfDetail;
    }

    // / <summary>
    // / Converts pixel XY coordinates into tile XY coordinates of the tile
    // containing
    // / the specified pixel.
    // / </summary>
    // / <param name="pixelX">Pixel X coordinate.</param>
    // / <param name="pixelY">Pixel Y coordinate.</param>
    // / <param name="tileX">Output parameter receiving the tile X
    // coordinate.</param>
    // / <param name="tileY">Output parameter receiving the tile Y
    // coordinate.</param>
    public static int[] pixelXYToTileXY(int pixelX, int pixelY) {
        int tileX = pixelX / BingTile.DEFAULT_TILE_SIZE;
        int tileY = pixelY / BingTile.DEFAULT_TILE_SIZE;

        return new int[] { tileX, tileY };
    }

    // / <summary>
    // / Converts tile XY coordinates into a QuadKey at a specified level of
    // detail.
    // / </summary>
    // / <param name="tileX">Tile X coordinate.</param>
    // / <param name="tileY">Tile Y coordinate.</param>
    // / <param name="levelOfDetail">Level of detail, from 1 (lowest detail)
    // / to 23 (highest detail).</param>
    // / <returns>A string containing the QuadKey.</returns>
    public static String tileXYToQuadKey(int tileX, int tileY, int levelOfDetail) {
        StringBuilder quadKey = new StringBuilder();
        for (int i = levelOfDetail; i > 0; i--) {
            char digit = '0';
            int mask = 1 << (i - 1);
            if ((tileX & mask) != 0) {
                digit++;
            }
            if ((tileY & mask) != 0) {
                digit++;
                digit++;
            }
            quadKey.append(digit);
        }

        return quadKey.toString();
    }

    // / <summary>
    // / Clips a number to the specified minimum and maximum values.
    // / </summary>
    // / <param name="n">The number to clip.</param>
    // / <param name="minValue">Minimum allowable value.</param>
    // / <param name="maxValue">Maximum allowable value.</param>
    // / <returns>The clipped value.</returns>
    private static double clip(double n, double minValue, double maxValue) {
        return Math.min(Math.max(n, minValue), maxValue);
    }

    /**
     * Diese Methode wird verwendet um... TODO.
     *
     * @param d
     * @param e
     * @param levelOfDetail
     * @return
     */
    public static String lonLatToQuadKey(double lon, double lat,
            int levelOfDetail) {

        int[] pixelXY = BingTileUtil.lonLatToPixelXY(lon, lat, levelOfDetail);
        int[] tileXY = BingTileUtil.pixelXYToTileXY(pixelXY[0], pixelXY[1]);

        return BingTileUtil
                .tileXYToQuadKey(tileXY[0], tileXY[1], levelOfDetail);
    }

    /**
     * Diese Methode wird verwendet um... TODO.
     *
     * @param d
     * @param e
     * @param levelOfDetail
     * @return
     */
    public static ReferencedEnvelope getTileBoundingBox(double lon, double lat,
            int levelOfDetail) {

        int[] imageXY = lonLatToPixelXY(lon, lat, levelOfDetail);

        int numberOfTilesX = imageXY[0] / BingTile.DEFAULT_TILE_SIZE;
        int numberOfTilesY = imageXY[1] / BingTile.DEFAULT_TILE_SIZE;

        int tileTopLeftPixelX = numberOfTilesX * BingTile.DEFAULT_TILE_SIZE;
        int tileTopLeftPixelY = numberOfTilesY * BingTile.DEFAULT_TILE_SIZE;

        double[] topLeftCoords = pixelXYToLonLat(tileTopLeftPixelX,
                tileTopLeftPixelY, levelOfDetail);
        double[] bottomRightCoords = pixelXYToLonLat(tileTopLeftPixelX
                + BingTile.DEFAULT_TILE_SIZE, tileTopLeftPixelY
                + BingTile.DEFAULT_TILE_SIZE, levelOfDetail);

        Envelope envelope = new Envelope(topLeftCoords[0],
                bottomRightCoords[0], topLeftCoords[1], bottomRightCoords[1]);

        return new ReferencedEnvelope(envelope, DefaultGeographicCRS.WGS84);
    }

}
