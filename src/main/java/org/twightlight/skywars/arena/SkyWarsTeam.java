package org.twightlight.skywars.arena;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.twightlight.skywars.arena.ui.enums.SkyWarsState;
import org.twightlight.skywars.cosmetics.visual.VisualCosmetic;
import org.twightlight.skywars.cosmetics.visual.VisualCosmeticType;
import org.twightlight.skywars.cosmetics.visual.assets.balloons.Balloon;
import org.twightlight.skywars.cosmetics.visual.categories.SkyWarsBalloon;
import org.twightlight.skywars.cosmetics.visual.categories.SkyWarsCage;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.player.Account;
import org.twightlight.skywars.utils.bukkit.BukkitUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class SkyWarsTeam {

    private static ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    private int id;
    private Arena server;
    private String alphabetical;
    private ChatColor color;
    private String location;
    private Balloon balloon;
    private List<UUID> members;
    private UUID cageOwner;

    public SkyWarsTeam(Arena server, int spawns, String serialized) {
        this.id = spawns;
        this.server = server;
        this.location = serialized;
        this.alphabetical = alphabet[spawns];
        if (spawns >= server.getTeamColors().size()) {
            this.color = ChatColor.AQUA;
        } else {
            this.color = server.getTeamColors().get(spawns);
        }
        this.members = new ArrayList<>(server.getGroup().getTeamSize());
    }

    public void destroy() {
        boolean isTeam = server.getGroup().getTeamSize() > 1;
        SkyWarsCage.remove(cageOwner, this.getLocation(), isTeam);
        if (server.getState() == SkyWarsState.INGAME) {
            String ballonSerialized = this.server.getConfig().getBalloon(this.id);

            if (ballonSerialized != null && this.isAlive()) {
                SkyWarsBalloon cosmetic = null;
                Account unique = server.getGroup().isSolo() ? Database.getInstance().getAccount(this.members.get(0)) : null;
                if (unique == null) {
                    List<Account> accounts = new ArrayList<>();
                    for (Player member : this.getMembers()) {
                        Account account = Database.getInstance().getAccount(member.getUniqueId());
                        if (account != null) {
                            int balloonId = account.getSelectedContainer().getGlobalSelection("balloon");
                            if (balloonId > 0) {
                                VisualCosmetic vc = VisualCosmetic.findByTypeAndId(VisualCosmeticType.BALLOON, balloonId);
                                if (vc != null && vc instanceof SkyWarsBalloon && vc.has(account)) {
                                    accounts.add(account);
                                }
                            }
                        }
                    }

                    if (accounts.size() > 0) {
                        unique = accounts.get(ThreadLocalRandom.current().nextInt(accounts.size()));
                    }
                }

                if (unique != null) {
                    int balloonId = unique.getSelectedContainer().getGlobalSelection("balloon");
                    if (balloonId > 0) {
                        VisualCosmetic vc = VisualCosmetic.findByTypeAndId(VisualCosmeticType.BALLOON, balloonId);
                        if (vc != null && vc instanceof SkyWarsBalloon && vc.has(unique)) {
                            cosmetic = (SkyWarsBalloon) vc;
                        }
                    }
                }

                if (cosmetic != null) {
                    this.balloon = new Balloon(BukkitUtils.deserializeLocation(ballonSerialized, server.getConfig().getWorldName()), cosmetic);
                }
            }
        }
    }

    public void reset() {
        this.members.clear();
        if (this.balloon != null) {
            this.balloon.despawn();
            this.balloon = null;
        }
        this.cageOwner = null;
        this.color = null;
    }

    public void addMember(Player player) {
        this.members.add(player.getUniqueId());
    }

    public void removeMember(Player player) {
        boolean isTeam = server.getGroup().getTeamSize() > 1;
        if (cageOwner != null && cageOwner.equals(player.getUniqueId())) {
            SkyWarsCage.remove(cageOwner, this.getLocation(), isTeam);
        }
        this.members.remove(player.getUniqueId());
        if (!members.isEmpty()) {
            cageOwner = members.get(RANDOM.nextInt(members.size()));
            Account account = Database.getInstance().getAccount(cageOwner);
            if (account != null) {
                int cageId = account.getSelectedContainer().getGlobalSelection("cage");
                if (cageId > 0) {
                    VisualCosmetic cosmetic = VisualCosmetic.findByTypeAndId(VisualCosmeticType.CAGE, cageId);
                    if (cosmetic != null && cosmetic instanceof SkyWarsCage && cosmetic.has(account)) {
                        ((SkyWarsCage) cosmetic).apply(account.getPlayer(), getLocation());
                    } else {
                        SkyWarsCage.defaultCage(getLocation(), false);
                    }
                } else {
                    SkyWarsCage.defaultCage(getLocation(), false);
                }
            }
        }
    }

    public boolean isAlive() {
        return !this.members.isEmpty();
    }

    public boolean canJoin() {
        return this.canJoin(1);
    }

    public boolean canJoin(int players) {
        return (this.members.size() + players) <= this.server.getGroup().getTeamSize();
    }

    public boolean hasMember(Player player) {
        return this.members.contains(player.getUniqueId());
    }

    public int getSize() {
        return this.server.getGroup().getTeamSize();
    }

    public String getAlphabetical() {
        return this.alphabetical;
    }

    public String getAlphabeticalTag() {
        return color + "[" + this.alphabetical + "]" + ChatColor.RESET;
    }

    public Location getLocation() {
        return BukkitUtils.deserializeLocation(this.getSerializedLocation(), server.getConfig().getWorldName());
    }

    public String getSerializedLocation() {
        return this.location;
    }

    public List<Player> getMembers() {
        return members.stream().filter(id -> Bukkit.getPlayer(id) != null).map(id -> Bukkit.getPlayer(id)).collect(Collectors.toList());
    }

    public UUID getCageOwner() {
        return cageOwner;
    }

    public void setCageOwner(UUID cageOwner) {
        this.cageOwner = cageOwner;
    }

    private static final String[] alphabet = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "W", "X", "Y", "Z"};
}
