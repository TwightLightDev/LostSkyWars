package org.twightlight.skywars.cosmetics.visual.assets.sprays;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class MapRender {

    public static BufferedImage loadImage(String urlStr) throws IOException {
        BufferedImage image;
        image = ImageIO.read(new URL(urlStr));
        image = MapPalette.resizeImage(image);
        return image;
    }

    public static BufferedImage loadImage(File file) throws IOException {
        BufferedImage image;
        image = ImageIO.read(file);
        image = MapPalette.resizeImage(image);
        return image;
    }

    public static BufferedImage stringToBufferedImage(Font font, String s, int degreesRotation) {
        BufferedImage img = new BufferedImage(1, 1, 6);
        Graphics g = img.getGraphics();
        g.setFont(font);
        FontRenderContext frc = g.getFontMetrics().getFontRenderContext();
        Rectangle2D rect = font.getStringBounds(s, frc);
        g.dispose();
        img = new BufferedImage((int)Math.ceil(rect.getWidth()), (int)Math.ceil(rect.getHeight()), 6);
        g = img.getGraphics();
        g.setColor(Color.black);
        AffineTransform affineTransform = new AffineTransform();
        affineTransform.rotate(Math.toRadians(degreesRotation), 0.0D, 0.0D);
        Font rotatedFont = font.deriveFont(affineTransform);
        g.setFont(rotatedFont);
        FontMetrics fm = g.getFontMetrics();
        int x = 0;
        int y = fm.getAscent();
        g.drawString(s, x, y);
        g.dispose();
        return img;
    }

    public static class ImageMapRenderer extends MapRenderer {
        private final BufferedImage image;
        private boolean rendered = false;

        public ImageMapRenderer(BufferedImage image) {
            super(false);
            this.image = image;
        }

        @Override
        public void render(MapView view, MapCanvas canvas, Player player) {
            if (rendered) return;
            canvas.drawImage(0, 0, image);
            rendered = true;
        }
    }
}
