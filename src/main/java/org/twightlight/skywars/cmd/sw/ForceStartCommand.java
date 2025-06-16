package org.twightlight.skywars.cmd.sw;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.twightlight.skywars.api.server.SkyWarsState;
import org.twightlight.skywars.cmd.SubCommand;
import org.twightlight.skywars.database.Database;
import org.twightlight.skywars.player.Account;
import org.twightlight.skywars.world.WorldServer;

public class ForceStartCommand extends SubCommand {

    public ForceStartCommand() {
        super("fstart");
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
    }

    @Override
    public void perform(Player player, String[] args) {
        Account account = Database.getInstance().getAccount(player.getUniqueId());
        if (account != null) {
            WorldServer<?> server = (WorldServer<?>) account.getServer();
            if (server != null) {
                if (server.getState() != SkyWarsState.WAITING && server.getState() != SkyWarsState.STARTING) {
                    return;
                }

                server.start();
            }
        }
    }

    @Override
    public String getUsage() {
        return "fstart";
    }

    @Override
    public String getDescription() {
        return "Forcestart your current server.";
    }

    @Override
    public boolean onlyForPlayer() {
        return true;
    }
}
