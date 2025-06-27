package org.twightlight.skywars.leaderboards.objects;

import org.bukkit.Location;
import org.twightlight.skywars.Language;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.holograms.Hologram;
import org.twightlight.skywars.holograms.Holograms;
import org.twightlight.skywars.leaderboards.LeaderBoard;
import org.twightlight.skywars.leaderboards.LeaderBoardStats;
import org.twightlight.skywars.leaderboards.LeaderBoardType;
import org.twightlight.skywars.utils.Logger.Level;
import org.twightlight.skywars.utils.StringUtils;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HologramLB extends LeaderBoard {

    private Hologram hologram;

    public HologramLB(String id, Location location, LeaderBoardStats stats) {
        super(id, location, stats);
        if (!location.getChunk().isLoaded()) {
            location.getChunk().load(true);
        }
    }

    @Override
    public void update() {
        try {
            CachedRowSet rs = Database.getInstance().query(stats.getSQL());
            Map<Integer, String> map = new HashMap<>();
            for (int i = 0; i < Language.options$leaderboard$hologram$lines.size(); i++) {
                map.put(i + 1, Language.options$leaderboard$hologram$lines.get(i).replace("{mode}", stats.getModeHolograms()).replace("{stats}", stats.getStatsHolograms()));
            }

            int slot = 1;
            if (rs != null) {
                rs.beforeFirst();
                while (rs.next()) {
                    int count = 0;
                    for (String row : stats.getStatsRows()) {
                        count += rs.getInt(row);
                    }

                    String lastRank = Database.getInstance().query("SELECT `lastRank` FROM `premium_lostedaccount` WHERE `name` = ?", rs.getString("name")).getString("lastRank");
                    for (Integer i : map.keySet()) {
                        map.put(i, map.get(i).replace("{" + slot + "_name}", lastRank + rs.getString("name")).replace("{" + slot + "_playerstats}", StringUtils.formatNumber(count)));
                    }
                    slot++;
                }
            }

            while (slot < 100) {
                for (Integer i : map.keySet()) {
                    map.put(i, map.get(i).replace("{" + slot + "_name}", Language.options$leaderboard$empty).replace("{" + slot + "_playerstats}", "0"));
                }
                slot++;
            }

            List<String> lines = new ArrayList<>();
            for (int i = 0; i < Language.options$leaderboard$hologram$lines.size(); i++) {
                lines.add(map.get(i + 1));
            }
            if (hologram == null) {
                hologram = Holograms.createHologram(location, lines);
            } else {
                for (int i = 0; i < lines.size(); i++) {
                    hologram.updateLine(i + 1, lines.get(i));
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, "update(): ", ex);
        }
    }

    @Override
    public void destroy() {
        Holograms.removeHologram(hologram);
        this.hologram = null;
    }

    @Override
    public LeaderBoardType getType() {
        return LeaderBoardType.HOLOGRAM;
    }

    @Override
    public int getRanking() {
        return 0;
    }
}
