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
import org.geotools.tile.WMTSource;

/**
 * TODO Klassenbeschreibung für 'BingSource'
 *
 * @author Ugo Taddei
 * @version $Revision: $
 */
public class BingSource extends WMTSource {

    private static final TileFactory tileFactory = new BingTileFactory();

    // from https://msdn.microsoft.com/en-us/library/bb259689.aspx
    public static double[] SCALE_LIST = { 295829355.45, 147914677.73,
            73957338.86, 36978669.43, 18489334.72, 9244667.36, 4622333.68,
            2311166.84, 1155583.42, 577791.71, 288895.85, 144447.93, 72223.96,
            36111.98, 18055.99, 9028.0, 4514.0, 2257.0, 1128.50, 564.25,
            282.12, 141.06, 70.53 };

    public BingSource(String name) {
        super(name);

    }

    /**
     * {@inheritDoc}
     *
     * @see org.locationtech.udig.catalog.internal.wmt.wmtsource.WMTSource#getBaseUrl()
     */
    @Override
    public String getBaseUrl() {
        return "http://ak.dynamic.t2.tiles.virtualearth.net/comp/ch/${code}?mkt=de-de&it=G,VE,BX,L,LA&shading=hill&og=78&n=z";
    }

    /**
     * {@inheritDoc}
     *
     * @see org.locationtech.udig.catalog.internal.wmt.wmtsource.WMTSource#getScaleList()
     */
    @Override
    public double[] getScaleList() {
        return SCALE_LIST;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.locationtech.udig.catalog.internal.wmt.wmtsource.WMTSource#getTileFactory()
     */
    public TileFactory getTileFactory() {
        return tileFactory;
    }

    /**
     * Diese Methode wird verwendet um... TODO.
     *
     * @param zoomLevel
     * @param x
     * @param y
     * @return
     */
    public String getTileUrl(int zoomLevel, int x, int y) {

        StringBuffer url = new StringBuffer(getBaseUrl());

        url.append(BingTileUtil.tileXYToQuadKey(x, y, zoomLevel)); //$NON-NLS-1$
        url.append("?mkt=de-de&it=G,VE,BX,L,LA&shading=hill&og=69&n=z"); //$NON-NLS-1$

        return url.toString();
    }

}
