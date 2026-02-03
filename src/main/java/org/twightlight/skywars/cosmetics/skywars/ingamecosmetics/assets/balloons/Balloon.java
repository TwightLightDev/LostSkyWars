package org.twightlight.skywars.cosmetics.skywars.ingamecosmetics.assets.balloons;

import org.bukkit.Location;
import org.twightlight.skywars.cosmetics.skywars.ingamecosmetics.categories.SkyWarsBalloon;
import org.twightlight.skywars.nms.BalloonEntity;
import org.twightlight.skywars.nms.NMS;
import org.twightlight.skywars.utils.MinecraftVersion;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Balloon {

    private BalloonEntity leash;
    private BalloonEntity bat;
    private BalloonEntity giant;
    public Balloon(Location location, SkyWarsBalloon balloon) {
        this(location, balloon, Collections.emptyList(), false);
    }

    public Balloon(Location location, SkyWarsBalloon balloon, List<UUID> uuids, boolean mini) {

        this.leash = NMS.createBalloonLeash(location, uuids);

        boolean higherThan183 = MinecraftVersion.getCurrentVersion().getCompareId() > 183;
        Location batLocation = location.clone();
        batLocation.setX(batLocation.getX() - ((higherThan183 ? 4.5 : 4.0)) + (mini ? 1.0 : 0.0));
        batLocation.setY(batLocation.getY() + ((higherThan183 ? 16.0 : 18.0)) - (mini ? 9.5 : 0.0));
        batLocation.setZ(batLocation.getZ() + ((higherThan183 ? 7.5 : 5.5)) - (mini ? 1.5 : 0.0));
        this.bat = NMS.createBalloonBat(batLocation, this.leash, uuids);

        Location giantLocation = location.clone();
        giantLocation.setX(giantLocation.getX() - 2.0 + (mini ? 1.0 : 0.0));
        giantLocation.setY(giantLocation.getY() + (mini ? 0 : 9.0));
        giantLocation.setZ(giantLocation.getZ() + 3.0 - (mini ? 1.5 : 0.0));

        this.giant = NMS.createBalloonGiant(giantLocation, balloon.getTextures(), uuids);
    }

    public void despawn() {

        if (this.leash != null) {
            this.leash.kill();
            this.leash = null;
        }
        if (this.bat != null) {
            this.bat.kill();
            this.bat = null;
        }
        if (this.giant != null) {
            this.giant.kill();
            this.giant = null;
        }
    }
}
