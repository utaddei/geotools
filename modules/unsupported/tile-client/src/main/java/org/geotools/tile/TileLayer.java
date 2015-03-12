package org.geotools.tile;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridCoverageFactory;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.DirectLayer;
import org.geotools.map.MapContent;
import org.geotools.map.MapViewport;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.renderer.lite.RendererUtilities;
import org.geotools.util.logging.Logging;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

public class TileLayer extends DirectLayer {

    private static final Logger LOGGER = Logging.getLogger(DirectLayer.class
            .getPackage().getName());

    private static final GridCoverageFactory gridFactory = new GridCoverageFactory();

    private WMTSource service;

    /** Resolution in DPI */
    private double resolution = 90;

    private GridCoverage2D coverage;

    public TileLayer(WMTSource service) {
        super();
        this.service = service;
    }

    public GridCoverage2D getCoverage() {
        return this.coverage;
    }

    public ReferencedEnvelope getBounds() {
        return new ReferencedEnvelope(-180, 180, -85, 85,
                DefaultGeographicCRS.WGS84);
    }

    @Override
    public void draw(Graphics2D graphics, MapContent map,
            MapViewport theViewport) {

        final MapViewport viewport = new MapViewport(theViewport);

        final ReferencedEnvelope viewportExtent = viewport.getBounds();
        int scale = calculateScale(viewportExtent, viewport.getScreenArea());

        Map<String, Tile> tileList = service.cutExtentIntoTiles2(
                viewportExtent, scale, false, 128);

        Collection<Tile> tiles = tileList.values();

        BufferedImage mosaickedImage = createImage(viewport.getScreenArea());
        Graphics2D g2d = mosaickedImage.createGraphics();

        renderTiles(tiles, g2d, viewportExtent, viewport.getWorldToScreen());

        this.coverage = gridFactory.create("GridCoverage", mosaickedImage,
                viewportExtent);

        graphics.drawImage(mosaickedImage, 0, 0, null);

    }

    protected void renderTiles(Collection<Tile> tiles, Graphics2D g2d,
            ReferencedEnvelope viewportExtent,
            AffineTransform worldToImageTransform) {

        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        double[] points = new double[4];

        for (Tile tile : tiles) {
            ReferencedEnvelope nativeTileEnvelope = tile.getExtent();

            ReferencedEnvelope tileEnvViewport;
            try {
                tileEnvViewport = nativeTileEnvelope.transform(
                        viewportExtent.getCoordinateReferenceSystem(), true);
            } catch (TransformException | FactoryException e) {
                throw new RuntimeException(e);
            }

            points[0] = tileEnvViewport.getMinX();
            points[3] = tileEnvViewport.getMinY();
            points[2] = tileEnvViewport.getMaxX();
            points[1] = tileEnvViewport.getMaxY();

            worldToImageTransform.transform(points, 0, points, 0, 2);

            renderTile(tile, g2d, points);

            // BufferedImage img = getTileImage(tile);
            //
            // g2d.drawImage(img, (int) points[0], (int) points[1],
            // (int) Math.ceil(points[2] - points[0]),
            // (int) Math.ceil(points[3] - points[1]), null);

        }

    }

    private void renderTile(Tile tile, Graphics2D g2d, double[] points) {
        BufferedImage img = getTileImage(tile);

        g2d.drawImage(img, (int) points[0], (int) points[1],
                (int) Math.ceil(points[2] - points[0]),
                (int) Math.ceil(points[3] - points[1]), null);
    }

    protected BufferedImage getTileImage(Tile tile) {

        System.out.println("HARDCODED DIR!!!!");

        BufferedImage img = null;

        File dir = new File("/home/ugo/temp");
        File imgFile = new File(dir, tile.getId() + ".png");
        try {
            if (imgFile.exists()) {
                img = ImageIO.read(imgFile);

            } else {
                img = tile.getBufferedImage();
                ImageIO.write(img, "png", imgFile);
            }
        } catch (IOException e) {
            LOGGER.log(Level.FINER, e.getMessage(), e);
        }
        return img;
    }

    private int calculateScale(ReferencedEnvelope extent, Rectangle screenArea) {

        int scale = 0;

        try {
            scale = (int) Math.round(RendererUtilities.calculateScale(extent,
                    screenArea.width, screenArea.height, this.resolution));
        } catch (FactoryException | TransformException ex) {
            throw new RuntimeException("Failed to calculate scale", ex);
        }
        return scale;
    }

    private BufferedImage createImage(Rectangle rectangle) {

        return new BufferedImage(rectangle.width, rectangle.height,
                BufferedImage.TYPE_INT_RGB);

    }
}
