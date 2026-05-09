package org.twightlight.skywars.bungee.server.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.twightlight.skywars.bungee.server.ServerManager;
import org.twightlight.skywars.bungee.server.ServerType;
import org.twightlight.skywars.bungee.server.balancer.server.ArenaServer;
import org.twightlight.skywars.bungee.server.balancer.server.BungeeServer;
import org.twightlight.skywars.bungee.server.balancer.type.MostConnection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ServerListener implements Listener {

    private ServerManager manager;

    public ServerListener() {
        this.manager = ServerManager.getManager();
    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent evt) {
        if (evt.getTag().equals("LostSWAPI") && evt.getSender() instanceof Server
                && evt.getReceiver() instanceof ProxiedPlayer) {
            ByteArrayDataInput in = ByteStreams.newDataInput(evt.getData());

            String subChannel = in.readUTF();
            if (subChannel.startsWith("Lobby")) {
                ProxiedPlayer player = (ProxiedPlayer) evt.getReceiver();

                BungeeServer server = this.manager.getBalancer(ServerType.LOBBY).next();
                if (server != null) {
                    player.connect(server.getServerInfo());
                } else {
                    player.sendMessage(TextComponent.fromLegacyText("\u00a7cCould not find a available lobby!"));
                }
            } else if (subChannel.startsWith("Count")) {
                String groupId = in.readUTF();

                int total = 0;
                for (BungeeServer server : manager.getBalancer(ServerType.ARENA).getList()) {
                    if (server instanceof ArenaServer) {
                        ArenaServer arenaServer = (ArenaServer) server;
                        if (groupId.equals(arenaServer.getGroupId())) {
                            total += arenaServer.getOnlinePlayers();
                        }
                    }
                }

                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("Count");
                out.writeUTF(groupId);
                out.writeInt(total);
                ((Server) evt.getSender()).sendData("LostSWAPI", out.toByteArray());
            } else if (subChannel.startsWith("Play")) {
                ProxiedPlayer player = (ProxiedPlayer) evt.getReceiver();
                String groupId = in.readUTF();
                String mapFilter = in.readUTF();

                Map<String, BungeeServer> candidates = new HashMap<>();
                for (BungeeServer server : manager.getBalancer(ServerType.ARENA).getList()) {
                    if (server instanceof ArenaServer) {
                        ArenaServer arenaServer = (ArenaServer) server;
                        if (groupId.equals(arenaServer.getGroupId()) && arenaServer.canBeSelected()) {
                            if (mapFilter.equals("all") || (arenaServer.getMap() != null && arenaServer.getMap().equalsIgnoreCase(mapFilter))) {
                                candidates.put(arenaServer.getServerInfo().getName(), arenaServer);
                            }
                        }
                    }
                }

                if (!candidates.isEmpty()) {
                    MostConnection<BungeeServer> most = new MostConnection<>();
                    most.addAll(candidates);
                    BungeeServer picked = most.next();
                    most.destroy();
                    if (picked != null) {
                        player.connect(picked.getServerInfo());
                    }
                }
            } else if (subChannel.startsWith("MapSelector")) {
                String groupId = in.readUTF();

                Map<String, Integer> set = new HashMap<>();
                List<BungeeServer> servers = manager.getBalancer(ServerType.ARENA).getList();
                for (BungeeServer server : servers) {
                    if (server instanceof ArenaServer) {
                        ArenaServer arenaServer = (ArenaServer) server;
                        if (groupId.equals(arenaServer.getGroupId())) {
                            String map = arenaServer.getMap();
                            if (map != null) {
                                int current = set.getOrDefault(map, 0);
                                set.put(map, current + (server.canBeSelected() ? 1 : 0));
                            }
                        }
                    }
                }

                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("MapSelector");
                out.writeUTF(groupId);
                for (Entry<String, Integer> entry : set.entrySet()) {
                    out.writeUTF(entry.getKey() + " : " + entry.getValue());
                }
                set.clear();
                ((Server) evt.getSender()).sendData("LostSWAPI", out.toByteArray());
            }
        }
    }
}
