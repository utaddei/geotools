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

import org.geotools.tile.TileFactory;
import org.geotools.tile.impl.WebMercatorTileService;

/**
 * TODO Klassenbeschreibung für 'BingService'
 *
 * @author Ugo Taddei
 * @version $Revision: $
 */
public class BingService extends WebMercatorTileService {

    private static final TileFactory tileFactory = new BingTileFactory();

    // from https://msdn.microsoft.com/en-us/library/bb259689.aspx
    private static double[] SCALE_LIST = { 295829355.45, 147914677.73,
            73957338.86, 36978669.43, 18489334.72, 9244667.36, 4622333.68,
            2311166.84, 1155583.42, 577791.71, 288895.85, 144447.93, 72223.96,
            36111.98, 18055.99, 9028.0, 4514.0, 2257.0, 1128.50, 564.25,
            282.12, 141.06, 70.53 };

    public BingService(String name, String baseUrl) {
        super(name, baseUrl);

    }

    /**
     * {@inheritDoc}
     *
     * @see org.geotools.tile.TileService
     */
    @Override
    public double[] getScaleList() {
        return SCALE_LIST;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.TileService.udig.catalog.internal.wmt.wmtsource.WMTSource#getTileFactory()
     */
    public TileFactory getTileFactory() {
        return tileFactory;
    }

}
