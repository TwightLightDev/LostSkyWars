package org.twightlight.skywars.modules.recentgames;

import org.bukkit.entity.Player;
import org.twightlight.skywars.modules.recentgames.hook.ReplayData;
import org.twightlight.skywars.world.WorldServer;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class GameData {
    private GameResult result;
    private long duration;
    private int kills;
    private String name;
    private int maxPlayer;
    private ReplayData replayData;
    private long startTime;

    private GameData(WorldServer<?> server) {

    }

    public static GameData createGameData(Player p, WorldServer<?> server, List<Player> winners, ReplayData replay) {
        GameData data = new GameData(server);
        if (winners == null || winners.isEmpty()) {
            data.result = GameResult.NO_RESULT;
        } else if (winners.contains(p)) {
            data.result = GameResult.WIN;
        } else {
            data.result = GameResult.LOSE;
        }
        data.duration = System.nanoTime() - server.getStartTime();
        data.startTime = server.getStartTimeMillis();
        data.kills = server.getKills(p);
        data.name = server.getName();
        data.maxPlayer = server.getMaxPlayers();
        data.replayData = replay;
        return data;
    }

    public GameResult getResult() {
        return result;
    }

    public long getDuration() {
        return duration;
    }

    public String getFormattedDuration() {
        long totalSeconds = duration / 1_000_000_000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        StringBuilder sb = new StringBuilder();
        if (hours > 0) sb.append(hours).append("h ");
        if (minutes > 0 || hours > 0) sb.append(minutes).append("m ");
        sb.append(seconds).append("s");

        return sb.toString().trim();
    }

    public String getFormattedStartTime() {
        if (startTime == 0) {
            return "Undefined";
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startTime);

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");

        String datePart = dateFormat.format(new Date(startTime));

        if (hour != 0 || minute != 0) {
            String timePart = String.format("%02d:%02d", hour, minute);
            return datePart + " at " + timePart;
        } else {
            return datePart;
        }
    }

    public int getKills() {
        return kills;
    }

    public String getName() {
        return name;
    }

    public int getMaxPlayer() {
        return maxPlayer;
    }

    public ReplayData getReplay() {
        return replayData;
    }
}
