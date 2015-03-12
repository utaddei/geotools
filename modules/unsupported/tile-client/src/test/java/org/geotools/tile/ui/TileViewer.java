package org.geotools.tile.ui;

import java.awt.Color;
import java.io.File;
import java.net.URL;

import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.map.MapContent;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.swing.JMapFrame;
import org.geotools.tile.ServiceTest;
import org.geotools.tile.TileLayer;
import org.geotools.tile.impl.bing.BingSource;
import org.geotools.tile.impl.osm.OSMService;
import org.geotools.tile.map.AsyncCachedTileLayer;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

public class TileViewer {

    private JMapFrame frame;

    public TileViewer() {

        MapContent map = createMap();

        frame = new JMapFrame(map);
        frame.setSize(800, 600);
        frame.enableStatusBar(true);
        // frame.enableTool(JMapFrame.Tool.ZOOM, JMapFrame.Tool.PAN,
        // JMapFrame.Tool.RESET);
        frame.enableToolBar(true);

        frame.setVisible(true);
    }

    private MapContent createMap() {

        final MapContent map = new MapContent();
        map.setTitle("TileLab");
        ReferencedEnvelope env = new ReferencedEnvelope(-180, 180, -90, 90,
                DefaultGeographicCRS.WGS84);

        env = new ReferencedEnvelope(5, 15, 45, 55, DefaultGeographicCRS.WGS84);

        try {
            ServiceTest.beforeClass();
            env = env.transform(ServiceTest.MERCATOR_CRS, true);
        } catch (TransformException | FactoryException e1) {
            e1.printStackTrace();
        }

        map.getViewport().setBounds(env);

        URL url;
        try {
            url = new URL(
                    "http://demo.boundlessgeo.com/geoserver/wms?VERSION=1.1.0&REQUEST=GetCapabilities");
            // WebMapServer wms = new WebMapServer(url);
            // WMSCapabilities capabilities = wms.getCapabilities();
            //

            // List<Layer> layers = capabilities.getLayerList();
            // org.geotools.data.ows.Layer layer = new
            // org.geotools.data.ows.Layer(
            // "osm:osm");
            // WMSLayer wmsLayer = new WMSLayer(wms, layer);
            // WMSMapLayer displayLayer = new WMSMapLayer(wms, layers.get(0));
            // map.addLayer(wmsLayer);

            File shpFile = new File(
                    "/opt/gis/geoserver/geoserver-2.6.0/data_dir/data/shapefiles/states.shp");
            FileDataStore dataStore = FileDataStoreFinder.getDataStore(shpFile);
            SimpleFeatureSource shapefileSource = dataStore.getFeatureSource();

            Style shpStyle = SLD.createPolygonStyle(Color.BLUE, null, 0.50f);
            String baseURL = "http://ak.dynamic.t2.tiles.virtualearth.net/comp/ch/${code}?mkt=de-de&it=G,VE,BX,L,LA&shading=hill&og=78&n=z";
            map.addLayer(new TileLayer(new BingSource("Road", baseURL)));
            map.addLayer(new AsyncCachedTileLayer(new OSMService("Mapnik",
                    "http://tile.openstreetmap.org/")));

            map.addLayer(new FeatureLayer(shapefileSource, shpStyle));

        } catch (Exception e) {
            // LOGGER.log(Level.FINER, e.getMessage(), e);
            e.printStackTrace();
        }

        return map;
    }

    public static void main(String[] args) {

        new TileViewer();
    }
}
