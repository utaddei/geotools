package org.geotools.tile.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.tile.WMTSource;
import org.geotools.util.logging.Logging;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public abstract class WebMercatorTileService extends WMTSource {

    private static final Logger LOGGER = Logging
            .getLogger(WebMercatorTileService.class.getPackage().getName());

    public static final double MIN_LONGITUDE = -180;

    public static final double MIN_LATITUDE = -85.05112878;

    public static final double MAX_LONGITUDE = 180;

    public static final double MAX_LATITUDE = 85.05112878;

    public static final CoordinateReferenceSystem WEB_MERCATOR_CRS;

    static {
        CoordinateReferenceSystem tmpCrs = null;

        try {
            tmpCrs = CRS.decode("EPSG:3857");
        } catch (FactoryException e) {
            LOGGER.log(Level.SEVERE,
                    "Failed to create Web Mercator CRS EPSG:3857", e);
            throw new RuntimeException(e);
        }
        WEB_MERCATOR_CRS = tmpCrs;

    }

    protected WebMercatorTileService(String name, String baseURL) {
        super(name, baseURL);
    }

    public ReferencedEnvelope getBounds() {
        return new ReferencedEnvelope(MIN_LONGITUDE, MAX_LONGITUDE,
                MIN_LATITUDE, MAX_LATITUDE, DefaultGeographicCRS.WGS84);
    }

    public CoordinateReferenceSystem getProjectedTileCrs() {
        return WEB_MERCATOR_CRS;
    }

}