package org.twightlight.skywars.nms;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.awt.image.BufferedImage;
import java.util.List;

public abstract class MapHelper {

    public abstract void createMap(int frameId, List<Player> players, Location location, BlockFace direction, BufferedImage image);

    public abstract void destroyMap(List<Player> players, int[] frameIds);

    public abstract byte[] createPixels(BufferedImage image);
}
