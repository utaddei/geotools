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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.tile.impl.ScaleZoomLevelMatcher;
import org.geotools.tile.impl.ZoomLevel;
import org.geotools.util.ObjectCache;
import org.geotools.util.ObjectCaches;
import org.geotools.util.logging.Logging;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Envelope;

/**
 * A TileService represent the class of objects that serve map tiles.
 * TileServices must at least have a name and a base URL.
 * 
 * @author to.srwn
 * @author Ugo Taddei
 * @since 1.1.0
 */
public abstract class TileService {

    private static final Logger LOGGER = Logging.getLogger(TileService.class
            .getPackage().getName());

    /**
     * This WeakHashMap acts as a memory cache. Because we are using
     * SoftReference, we won't run out of Memory, the GC will free space.
     **/
    private ObjectCache tiles = ObjectCaches.create("soft", 50); //$NON-NLS-1$

    private String baseURL;

    private String name;

    /**
     * Create a new TileService with a name and a base URL
     * 
     * @param name the name. Cannot be null.
     * @param baseURL the base URL. This is a string representing the common
     *        part of the URL for all this service's tiles. Cannot be null. Note
     *        that this constructor doesn't ensure that the URL is well-formed.
     */
    protected TileService(String name, String baseURL) {
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

    public int getTileWidth() {
        return 256;
    }

    public int getTileHeight() {
        return 256;
    }

    /**
     * Returns the prefix of an tile-url, e.g.: http://tile.openstreetmap.org/
     *
     * @return
     */
    public String getBaseUrl() {
        return this.baseURL;
    }

    /**
     * The CRS that is used when the extent is cut in tiles.
     *
     * @return
     */
    public CoordinateReferenceSystem getTileCrs() {
        return DefaultGeographicCRS.WGS84;
    }

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

        boolean selectionAutomatic = true;
        int zoomLevel = -1;

        // check if the zoom-level is valid
        if (!selectionAutomatic
                && ((zoomLevel >= getMinZoomLevel()) && (zoomLevel <= getMaxZoomLevel()))) {
            // the zoom-level from the properties is valid, so let's take it

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

    public Map<String, Tile> cutExtentIntoTiles2(ReferencedEnvelope _mapExtent,
            int scaleFactor, boolean recommendedZoomLevel, int tileLimitWarning) {

        ReferencedEnvelope mapExtent = createSafeEnvelopeInWGS84(_mapExtent);
        ReferencedEnvelope extent = normalizeExtent(mapExtent);

        // only continue, if we have tiles that cover the requested extent
        if (!extent.intersects((Envelope) getBounds())) {
            return Collections.emptyMap();
        }

        TileFactory tileFactory = getTileFactory();

        // TODO CRS
        ScaleZoomLevelMatcher zoomLevelMatcher = null;
        try {

            zoomLevelMatcher = new ScaleZoomLevelMatcher(getTileCrs(),
                    getProjectedTileCrs(), CRS.findMathTransform(getTileCrs(),
                            getProjectedTileCrs()), CRS.findMathTransform(
                            getProjectedTileCrs(), getTileCrs()), mapExtent,
                    mapExtent, scaleFactor);

        } catch (FactoryException e) {
            throw new RuntimeException(e);
        }

        // TODO understand the minus 1 below
        int zoomLevelA = getZoomLevelToUse(zoomLevelMatcher, scaleFactor,
                recommendedZoomLevel) - 1;

        ZoomLevel zoomLevel = tileFactory.getZoomLevel(zoomLevelA, this);

        long maxNumberOfTiles = zoomLevel.getMaxTileNumber();

        Map<String, Tile> tileList = new HashMap<String, Tile>();

        // Let's get the first tile which covers the upper-left corner
        Tile firstTile = tileFactory.findTileAtCoordinate(extent.getMinX(),
                extent.getMaxY(), zoomLevel, this);

        tileList.put(firstTile.getId(), addTileToList(firstTile));

        Tile firstTileOfRow = null;
        Tile movingTile = firstTileOfRow = firstTile;

        // Loop column
        do {
            // Loop row
            do {

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
                    LOGGER.warning("Reached tile limit of " + tileLimitWarning
                            + ". Returning an empty collection.");
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

    private boolean listContainsTile(String tileId) {
        return !(tiles.peek(tileId) == null || tiles.get(tileId) == null);
    }

    private Tile addTileToList(Tile tile) {
        if (listContainsTile(tile.getId())) {
            if (LOGGER.isLoggable(Level.FINER)) {
                LOGGER.fine("Tile already in cache: " + tile.getId());
            }
            return (Tile) tiles.get(tile.getId());
        } else {
            if (LOGGER.isLoggable(Level.FINER)) {
                LOGGER.fine("Tile added to cache: " + tile.getId());
            }
            tiles.put(tile.getId(), tile);
            return tile;
        }
    }

    /**
     * Returns a list that represents a mapping between zoom-levels and map
     * scale. Array index: zoom-level Value at index: map scale High zoom-level
     * -> more detailed map Low zoom-level -> less detailed map
     *
     * @return mapping between zoom-levels and map scale
     */
    public abstract double[] getScaleList();

    public abstract ReferencedEnvelope getBounds();

    /**
     * The projection the tiles are drawn in.
     *
     * @return
     */
    public abstract CoordinateReferenceSystem getProjectedTileCrs();

    /**
     * Returns the TileFactory which is used to call the method
     * getTileFromCoordinate().
     */
    public abstract TileFactory getTileFactory();

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

    public String toString() {
        return getName();
    }
}
