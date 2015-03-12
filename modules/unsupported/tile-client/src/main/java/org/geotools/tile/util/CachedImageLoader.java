package org.geotools.tile.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.geotools.tile.ImageLoader;
import org.geotools.tile.Tile;
import org.geotools.util.logging.Logging;

public class CachedImageLoader implements ImageLoader {

    private static final Logger LOGGER = Logging
            .getLogger(CachedImageLoader.class.getPackage().getName());

    private final File cacheDirectory;

    public CachedImageLoader(File cacheDirectory) {
        this.cacheDirectory = cacheDirectory;
    }

    @Override
    public BufferedImage loadImageTileImage(Tile tile) throws IOException {

        BufferedImage img = null;

        File imgFile = new File(this.cacheDirectory, tile.getId() + ".png");
        if (imgFile.exists()) {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.fine("Found image in cache for '" + tile.getId()
                        + "' at " + imgFile.getAbsolutePath());
            }
            img = ImageIO.read(imgFile);

        } else {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.fine("Not found in cache '" + tile.getId()
                        + "'. Loading from " + tile.getUrl());
            }
            img = ImageIO.read(tile.getUrl());
            ImageIO.write(img, "png", imgFile);
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.fine("Wrote to cache " + imgFile.getAbsolutePath());
            }
        }
        return img;
    }

}
