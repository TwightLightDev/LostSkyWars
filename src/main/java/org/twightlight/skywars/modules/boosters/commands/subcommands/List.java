package org.twightlight.skywars.modules.boosters.commands.subcommands;

import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.twightlight.skywars.modules.api.ModulesUser;
import org.twightlight.skywars.modules.api.cmds.SubCommand;
import org.twightlight.skywars.modules.boosters.Boosters;
import org.twightlight.skywars.modules.boosters.boosters.Booster;
import org.twightlight.skywars.modules.boosters.boosters.BoosterManager;
import org.twightlight.skywars.modules.boosters.users.PlayerUser;

import java.util.stream.Collectors;

public class List extends SubCommand {
    public List(Permission permission) {
        super(permission);
    }

    @Override
    public String getSubCommand() {
        return "list";
    }

    @Override
    public void sendUsage(ModulesUser user) {

    }
    @Override
    public boolean execute(Player user, String[] args) {
        java.util.List<String> list = BoosterManager.getBoosters().values().stream().map(Booster::getId).collect(Collectors.toList());
        PlayerUser.getFromUUID(user.getUniqueId()).sendMessage(list);
        return true;
    }
}
