package org.twightlight.skywars.systems.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import org.twightlight.skywars.utils.string.StringUtils;

@SuppressWarnings("deprecation")
public abstract class LostScoreboard {

    private Player player;
    private Objective objective;
    private Scoreboard scoreboard;
    private ScoreboardScroller scroller;

    private String display;

    private VirtualTeam[] teams = new VirtualTeam[15];

    public LostScoreboard() {
    }

    public void scroll() {
        if (scroller != null) {
            display(scroller.next());
        }
    }

    public void update() {
    }


    public LostScoreboard add(int line) {
        return add(line, "");
    }

    public LostScoreboard add(int line, String name) {
        if (line > 15 || line < 1) {
            return this;
        }

        VirtualTeam team = getOrCreate(line);
        team.setValue(name);
        if (scoreboard != null) {
            team.update();
        }
        return this;
    }

    public LostScoreboard remove(int line) {
        if (line > 15 || line < 1) {
            return this;
        }

        VirtualTeam team = teams[line - 1];
        if (team != null) {
            team.destroy();
            teams[line - 1] = null;
        }

        return this;
    }

    public LostScoreboard to(Player player) {
        Player lastPlayer = this.player;
        this.player = player;
        if (scoreboard != null) {
            if (lastPlayer != null) {
                lastPlayer.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            }

            player.setScoreboard(scoreboard);
        }

        lastPlayer = null;
        return this;
    }

    public void hide() {
        if (player != null) {
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        }
    }

    public LostScoreboard display(String display) {
        this.display = StringUtils.translateAlternateColorCodes('&', display);
        if (objective != null) {
            objective.setDisplayName(this.display.substring(0, Math.min(this.display.length(), 32)));
        }

        return this;
    }

    public LostScoreboard scroller(ScoreboardScroller ss) {
        this.scroller = ss;
        return this;
    }


    public LostScoreboard build() {
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.objective = scoreboard.registerNewObjective(getObjectiveName(), "dummy");
        this.objective.setDisplayName(
                this.display == null ? "" : this.display.substring(0, Math.min(this.display.length(), 32)));
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        Team t = scoreboard.registerNewTeam("npcs");
        t.setNameTagVisibility(NameTagVisibility.NEVER);

        t.addEntry("§8[NPC] ");

        if (player != null) {
            player.setScoreboard(scoreboard);
        }



        for (VirtualTeam team : teams) {
            if (team != null) {
                team.update();
            }
        }

        return this;
    }

    public void destroy() {
        this.objective.unregister();
        this.objective = null;
        this.scoreboard = null;
        this.teams = null;
        this.player = null;
        this.display = null;
    }

    public VirtualTeam getTeam(int line) {
        if (line > 15 || line < 1) {
            return null;
        }

        return teams[line - 1];
    }

    public VirtualTeam getOrCreate(int line) {
        if (line > 15 || line < 1) {
            return null;
        }

        if (teams[line - 1] == null) {
            teams[line - 1] = new VirtualTeam(this, "score[" + line + "]", line);
        }

        return teams[line - 1];
    }

    public String getObjectiveName() {
        return "LostScoreboard";
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public Objective getObjective() {
        return objective;
    }
}
