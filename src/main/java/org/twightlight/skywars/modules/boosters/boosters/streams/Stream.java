package org.twightlight.skywars.modules.boosters.boosters.streams;

import org.twightlight.skywars.modules.boosters.boosters.Booster;
import org.twightlight.skywars.modules.boosters.boosters.BoosterData;
import org.twightlight.skywars.modules.boosters.users.User;

import java.util.List;
import java.util.UUID;

public interface Stream {
    boolean add(UUID uuid, String boosterid);
    void setCap(int cap);
    boolean remove(int pos);
    Booster.BoosterType getType();
    boolean isEmpty();
    User getUser();
    List<BoosterData> getAsList();
    UUID getOwner(int pos);
}
