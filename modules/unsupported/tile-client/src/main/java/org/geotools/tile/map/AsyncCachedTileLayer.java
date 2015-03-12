package org.geotools.tile.map;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.concurrent.CountDownLatch;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.tile.Tile;
import org.geotools.tile.TileLayer;
import org.geotools.tile.WMTSource;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

public class AsyncCachedTileLayer extends TileLayer {

    private CountDownLatch countDownLatch;

    public AsyncCachedTileLayer(WMTSource service) {
        super(service);
    }

    @Override
    protected void renderTiles(Collection<Tile> tiles, Graphics2D g2d,
            ReferencedEnvelope viewportExtent,
            AffineTransform worldToImageTransform) {

        long t = System.currentTimeMillis();

        this.countDownLatch = new CountDownLatch(tiles.size());

        // g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
        // (float) 0.5));

        localRenderTiles(tiles, g2d, viewportExtent, worldToImageTransform);
        try {
            this.countDownLatch.await();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
        this.countDownLatch = null;

        System.out.println("Async: " + (System.currentTimeMillis() - t));
    }

    protected void renderTile(final Tile tile, final Graphics2D g2d,
            final double[] points) {

        Runnable r = new Runnable() {

            @Override
            public void run() {
                BufferedImage img = getTileImage(tile);

                g2d.drawImage(img, (int) points[0], (int) points[1],
                        (int) Math.ceil(points[2] - points[0]),
                        (int) Math.ceil(points[3] - points[1]), null);

                AsyncCachedTileLayer.this.countDownLatch.countDown();
            }
        };
        new Thread(r).start();

    }

    protected void localRenderTiles(Collection<Tile> tiles, Graphics2D g2d,
            ReferencedEnvelope viewportExtent,
            AffineTransform worldToImageTransform) {

        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        // double[] points = new double[4];

        for (Tile tile : tiles) {
            ReferencedEnvelope nativeTileEnvelope = tile.getExtent();

            ReferencedEnvelope tileEnvViewport;
            try {
                tileEnvViewport = nativeTileEnvelope.transform(
                        viewportExtent.getCoordinateReferenceSystem(), true);
            } catch (TransformException | FactoryException e) {
                throw new RuntimeException(e);
            }
            double[] points = new double[4];
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

}
