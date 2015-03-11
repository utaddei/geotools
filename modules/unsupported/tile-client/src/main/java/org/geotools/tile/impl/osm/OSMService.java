package org.geotools.tile.impl.osm;

import org.geotools.tile.TileFactory;
import org.geotools.tile.impl.WebMercatorTileService;

public class OSMService extends WebMercatorTileService {

    private static final TileFactory tileFactory = new OSMTileFactory();

    private static double[] scaleList = { Double.NaN, Double.NaN, 147914381,
            73957190, 36978595, 18489297, 9244648, 4622324, 2311162, 1155581,
            577790, 288895, 144447, 72223, 36111, 18055, 9027, 4513, 2256 };

    public OSMService(String name, String baseUrl) {
        super(name, baseUrl);

    }

    @Override
    public double[] getScaleList() {
        return scaleList;
    }

    @Override
    public TileFactory getTileFactory() {
        return tileFactory;
    }

}
