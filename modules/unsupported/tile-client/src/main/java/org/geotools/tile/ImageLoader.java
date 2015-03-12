package org.geotools.tile;

import java.awt.image.BufferedImage;
import java.io.IOException;

public interface ImageLoader {

    BufferedImage loadImageTileImage(Tile tile) throws IOException;

}
