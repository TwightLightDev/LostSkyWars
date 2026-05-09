package org.twightlight.skywars.listeners.server;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.twightlight.skywars.bungee.CoreLobbies;

import java.util.HashMap;
import java.util.Map;

public class PluginMessageListener implements org.bukkit.plugin.messaging.PluginMessageListener {

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] data) {
        if (channel.equals("LostSWAPI")) {
            ByteArrayDataInput in = ByteStreams.newDataInput(data);
            String subChannel = in.readUTF();

            if (subChannel.equals("Count")) {
                String groupId = in.readUTF();
                int count = in.readInt();
                CoreLobbies.setPlayerCount(groupId, count);
            } else if (subChannel.equals("MapSelector")) {
                String groupId = in.readUTF();
                Map<String, Integer> map = new HashMap<>();
                try {
                    while (true) {
                        String entry = in.readUTF();
                        String[] parts = entry.split(" : ");
                        map.put(parts[0], Integer.parseInt(parts[1]));
                    }
                } catch (Exception ex) {
                    // end of stream
                }
                CoreLobbies.setMapSelector(groupId, map);
            }
        }
    }
}
