/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2015, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2004-2010, Refractions Research Inc.
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
package org.geotools.tile;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.tile.impl.ScaleZoomLevelMatcher;
import org.geotools.tile.impl.ZoomLevel;
import org.geotools.util.ObjectCache;
import org.geotools.util.ObjectCaches;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Envelope;

/**
 * @author to.srwn
 * @since 1.1.0
 */
public abstract class WMTSource {

    /**
     * This WeakHashMap acts as a memory cache. Because we are using
     * SoftReference, we won't run out of Memory, the GC will free space.
     **/
    private ObjectCache tiles = ObjectCaches.create("soft", 50); //$NON-NLS-1$

    private String baseURL;

    private String name;

    protected WMTSource(String name, String baseURL) {
        setName(name);
        setBaseURL(baseURL);

    }

    private void setBaseURL(String baseURL) {
        if (baseURL == null || baseURL.isEmpty()) {
            throw new IllegalArgumentException("Base URL cannot be null");
        }
        this.baseURL = baseURL;
    }

    private void setName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Deprecated
    public String getId() {
        return getName();
    }

    public int getTileWidth() {
        return 256;
    }

    public int getTileHeight() {
        return 256;
    }

    public String getFileFormat() {
        return "png"; //$NON-NLS-1$
    }

    public abstract ReferencedEnvelope getBounds();

    /**
     * Returns the prefix of an tile-url, e.g.: http://tile.openstreetmap.org/
     *
     * @return
     */
    public String getBaseUrl() {
        return this.baseURL;
    }

    /**
     * The projection the tiles are drawn in.
     *
     * @return
     */
    @Deprecated
    public CoordinateReferenceSystem _getProjectedTileCrs() {
        return null;// WMTSource.CRS_EPSG_900913;
    }

    /**
     * The projection the tiles are drawn in.
     *
     * @return
     */
    public abstract CoordinateReferenceSystem getProjectedTileCrs();

    /**
     * The CRS that is used when the extent is cut in tiles.
     *
     * @return
     */
    public CoordinateReferenceSystem getTileCrs() {
        return DefaultGeographicCRS.WGS84;
    }

    // endregion

    // region Methods to access the tile-list (cache)
    public boolean listContainsTile(String tileId) {
        return !(tiles.peek(tileId) == null || tiles.get(tileId) == null);
    }

    public Tile addTileToList(Tile tile) {
        if (listContainsTile(tile.getId())) {
            System.out
                    .println("[WMTSource.addTileToList] Already in cache: " + tile.getId()); //$NON-NLS-1$
            return getTileFromList(tile.getId());
        } else {
            System.out
                    .println("[WMTSource.addTileToList] Was not in cache: " + tile.getId()); //$NON-NLS-1$
            tiles.put(tile.getId(), tile);
            return tile;
        }
    }

    public Tile getTileFromList(String tileId) {
        return (Tile) tiles.get(tileId);
    }

    // endregion

    // region Zoom-level
    /**
     * Returns a list that represents a mapping between zoom-levels and map
     * scale. Array index: zoom-level Value at index: map scale High zoom-level
     * -> more detailed map Low zoom-level -> less detailed map
     *
     * @return mapping between zoom-levels and map scale
     */
    public abstract double[] getScaleList();

    /**
     * Translates the map scale into a zoom-level for the map services. The
     * scale-factor (0-100) decides whether the tiles will be scaled down (100)
     * or scaled up (0).
     *
     * @param renderJob Contains all the needed information
     * @param scaleFactor Scale-factor (0-100)
     * @return Zoom-level
     */
    public int getZoomLevelFromMapScale(ScaleZoomLevelMatcher zoomLevelMatcher,
            int scaleFactor) {
        // fallback scale-list
        double[] scaleList = getScaleList();
        // during the calculations this list caches already calculated scales
        double[] tempScaleList = new double[scaleList.length];
        Arrays.fill(tempScaleList, Double.NaN);

        assert (scaleList != null && scaleList.length > 0);

        int zoomLevel = zoomLevelMatcher.getZoomLevelFromScale(this,
                tempScaleList);

        System.out.println("zoomLevel0: " + zoomLevel);

        // Now apply the scale-factor
        if (zoomLevel == 0) {
            return zoomLevel;
        } else {
            int upperScaleIndex = zoomLevel - 1;
            int lowerScaleIndex = zoomLevel;

            double deltaScale = tempScaleList[upperScaleIndex]
                    - tempScaleList[lowerScaleIndex];
            double rangeScale = (scaleFactor / 100d) * deltaScale;
            double limitScale = tempScaleList[lowerScaleIndex] + rangeScale;

            if (zoomLevelMatcher.getScale() > limitScale) {
                return upperScaleIndex;
            } else {
                return lowerScaleIndex;
            }
        }
    }

    /**
     * Returns the zoom-level that should be used to fetch the tiles.
     *
     * @param scale
     * @param scaleFactor
     * @param useRecommended always use the calculated zoom-level, do not use
     *        the one the user selected
     * @return
     */
    public int getZoomLevelToUse(ScaleZoomLevelMatcher zoomLevelMatcher,
            int scaleFactor, boolean useRecommended) {
        if (useRecommended) {
            return getZoomLevelFromMapScale(zoomLevelMatcher, scaleFactor);
        }

        // try to load the property values
        boolean selectionAutomatic = true;
        int zoomLevel = -1;

        // if (layerProperties.load()) {
        // selectionAutomatic = layerProperties.getSelectionAutomatic();
        // zoomLevel = layerProperties.getZoomLevel();
        // } else {
        // selectionAutomatic = true;
        // }

        // check if the zoom-level is valid
        if (!selectionAutomatic
                && ((zoomLevel >= getMinZoomLevel()) && (zoomLevel <= getMaxZoomLevel()))) {
            // the zoom-level from the properties is valid, so let's take it

            System.out.println("zoomLevel1: " + zoomLevel);

            return zoomLevel;
        } else {

            // No valid property values or automatic selection of the zoom-level
            return getZoomLevelFromMapScale(zoomLevelMatcher, scaleFactor);
        }
    }

    /**
     * Returns the lowest zoom-level number from the scaleList.
     *
     * @param scaleList
     * @return
     */
    public int getMinZoomLevel() {
        double[] scaleList = getScaleList();
        int minZoomLevel = 0;

        while (Double.isNaN(scaleList[minZoomLevel])
                && (minZoomLevel < scaleList.length)) {
            minZoomLevel++;
        }

        return minZoomLevel;
    }

    /**
     * Returns the highest zoom-level number from the scaleList.
     *
     * @param scaleList
     * @return
     */
    public int getMaxZoomLevel() {
        double[] scaleList = getScaleList();
        int maxZoomLevel = scaleList.length - 1;

        while (Double.isNaN(scaleList[maxZoomLevel]) && (maxZoomLevel >= 0)) {
            maxZoomLevel--;
        }

        return maxZoomLevel;
    }

    // endregion

    // region Tiles-Cutting
    /**
     * Returns the TileFactory which is used to call the method
     * getTileFromCoordinate().
     */
    public abstract TileFactory getTileFactory();

    public Map<String, Tile> cutExtentIntoTiles2(ReferencedEnvelope _mapExtent,
            int scaleFactor, boolean recommendedZoomLevel, int tileLimitWarning) {

        if (DefaultGeographicCRS.WGS84.equals(_mapExtent
                .getCoordinateReferenceSystem())) {
            System.out.println("Should be: WGS 84");
        } else {
            System.out
                    .println("Should be: WGS 84 _______ but ain't!!!!!!!!!!!");

        }
        ReferencedEnvelope mapExtent = createSafeEnvelopeInWGS84(_mapExtent);
        ReferencedEnvelope extent = normalizeExtent(mapExtent);

        System.out.println(extent);
        System.out.println(getBounds());

        // only continue, if we have tiles that cover the requested extent
        if (!extent.intersects((Envelope) getBounds())) {
            System.out.println("out");
            return Collections.emptyMap();
        }

        TileFactory tileFactory = getTileFactory();

        // TODO CRS
        ScaleZoomLevelMatcher zoomLevelMatcher = null;
        try {

            System.out.println("Check the _mapExtents here____");

            zoomLevelMatcher = new ScaleZoomLevelMatcher(getTileCrs(),
                    getProjectedTileCrs(), CRS.findMathTransform(getTileCrs(),
                            getProjectedTileCrs()), CRS.findMathTransform(
                            getProjectedTileCrs(), getTileCrs()), mapExtent,
                    mapExtent, scaleFactor);

        } catch (FactoryException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("FISHY minus 1 below!!!");
        int zoomLevelA = getZoomLevelToUse(zoomLevelMatcher, scaleFactor,
                recommendedZoomLevel) - 1;

        ZoomLevel zoomLevel = tileFactory.getZoomLevel(zoomLevelA, this);

        System.out.println("zoom level?? " + zoomLevelA + " x "
                + zoomLevel.getZoomLevel());

        long maxNumberOfTiles = zoomLevel.getMaxTileNumber();

        Map<String, Tile> tileList = new HashMap<String, Tile>();

        System.out.println(" Zoom-Level: " + zoomLevel.getZoomLevel()
                + " Extent: " + extent + " ");

        // Let's get the first tile which covers the upper-left corner
        Tile firstTile = tileFactory.getTileFromCoordinate(extent.getMinX(),
                extent.getMaxY(), zoomLevel, this);

        tileList.put(firstTile.getId(), addTileToList(firstTile));

        Tile firstTileOfRow = null;
        Tile movingTile = firstTileOfRow = firstTile;

        System.out.printf("TS: %s %s ", tileList.size(), maxNumberOfTiles);

        // Loop column
        do {
            // Loop row
            do {

                // System.out.println("row");

                // get the next tile right of this one
                // Tile rightNeighbour = movingTile.getRightNeighbour();
                Tile rightNeighbour = tileFactory.findRightNeighbour(
                        movingTile, this);// movingTile.getRightNeighbour();

                // Check if the new tile is still part of the extent and
                // that we don't have the first tile again
                if (extent.intersects((Envelope) rightNeighbour.getExtent())
                        && !firstTileOfRow.equals(rightNeighbour)) {

                    // System.out.printf("N: %s %s", rightNeighbour.getId(),
                    // addTileToList(rightNeighbour));

                    tileList.put(rightNeighbour.getId(),
                            addTileToList(rightNeighbour));

                    movingTile = rightNeighbour;
                } else {
                    break;
                }
                if (tileList.size() > tileLimitWarning) {
                    System.out.println("TOO MANY TILES " + tileList.size()
                            + " > " + tileLimitWarning);
                    return Collections.emptyMap();
                }
            } while (tileList.size() < maxNumberOfTiles);

            // get the next tile under the first one of the row
            // Tile lowerNeighbour = firstTileOfRow.getLowerNeighbour();
            Tile lowerNeighbour = tileFactory.findLowerNeighbour(
                    firstTileOfRow, this);

            // Check if the new tile is still part of the extent
            if (extent.intersects((Envelope) lowerNeighbour.getExtent())
                    && !firstTile.equals(lowerNeighbour)) {

                // System.out.printf("N: %s %s", lowerNeighbour.getId(),
                // addTileToList(lowerNeighbour));

                tileList.put(lowerNeighbour.getId(),
                        addTileToList(lowerNeighbour));

                firstTileOfRow = movingTile = lowerNeighbour;
            } else {
                break;
            }
        } while (tileList.size() < maxNumberOfTiles);

        return tileList;
    }

    public static final ReferencedEnvelope createSafeEnvelopeInWGS84(
            ReferencedEnvelope _mapExtent) {

        try {

            return _mapExtent.transform(DefaultGeographicCRS.WGS84, true);

        } catch (TransformException e) {
            throw new RuntimeException(e);
        } catch (FactoryException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * The extent from the viewport may look like this: MaxY: 110° (=-70°) MinY:
     * -110° MaxX: 180° MinX: -180° But cutExtentIntoTiles(..) requires an
     * extent that looks like this: MaxY: 85° (or 90°) MinY: -85° (or -90°)
     * MaxX: 180° MinX: -180°
     *
     * @param envelope
     * @return
     */
    private ReferencedEnvelope normalizeExtent(ReferencedEnvelope envelope) {
        ReferencedEnvelope bounds = getBounds();

        if (envelope.getMaxY() > bounds.getMaxY()
                || envelope.getMinY() < bounds.getMinY()
                || envelope.getMaxX() > bounds.getMaxX()
                || envelope.getMinX() < bounds.getMinX()) {

            double maxY = (envelope.getMaxY() > bounds.getMaxY()) ? bounds
                    .getMaxY() : envelope.getMaxY();
            double minY = (envelope.getMinY() < bounds.getMinY()) ? bounds
                    .getMinY() : envelope.getMinY();
            double maxX = (envelope.getMaxX() > bounds.getMaxX()) ? bounds
                    .getMaxX() : envelope.getMaxX();
            double minX = (envelope.getMinX() < bounds.getMinX()) ? bounds
                    .getMinX() : envelope.getMinX();

            ReferencedEnvelope newEnvelope = new ReferencedEnvelope(minX, maxX,
                    minY, maxY, envelope.getCoordinateReferenceSystem());

            return newEnvelope;
        }

        return envelope;
    }

    // endregion

    @Override
    public String toString() {
        return getName();
    }
}
