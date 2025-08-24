package org.twightlight.skywars.modules.friends.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.twightlight.skywars.modules.libs.yaml.YamlWrapper;


public class MainConfig extends YamlWrapper {
    public MainConfig(Plugin pl, String name, String dir) {
        super(pl, name, dir);
        YamlConfiguration yml = getYml();
        yml.addDefault("options.friend-add-timeout", 5);
        yml.addDefault("options.max-friends.default", 25);
        yml.addDefault("options.max-friends.vip", 50);
        yml.addDefault("options.max-friends.vip+", 100);
        yml.addDefault("options.max-friends.mvp", 200);
        yml.addDefault("options.max-friends.mvp+", 500);
        yml.addDefault("options.max-friends.mvp++", 800);

        // Messages
        yml.addDefault("messages.invalid-arguments", "&cInvalid usage! Valid usage: '/%0%'");
        yml.addDefault("messages.player-not-found", "&cCan't find a player by the name of '%0%'");
        yml.addDefault("messages.no-permission", "&cYou do not have permission to execute this command!");
        yml.addDefault("messages.invalid-input", "&cInvalid input '%0%'");
        yml.addDefault("messages.invalid-page-number", list(
                "&9&l-------------------------------------------",
                "&cInvalid page number!",
                "&9&l-------------------------------------------"
        ));
        yml.addDefault("messages.help", list(
                "&9&l-------------------------------------------",
                "&aFriend commands:",
                "&e/friend help &7- &bPrints this help message",
                "&e/friend add &7- &bAdds a player as a friend",
                "&e/friend accept &7- &bAccept a friend request",
                "&e/friend deny &7- &bDecline a friend request",
                "&e/friend list &7- &bList your friends",
                "&e/friend remove <player> &7- &bRemove a player from your friends",
                "&e/friend requests &7- &bView friend requests",
                "&e/friend toggle &7- &bToggle friend requests",
                "&e/friend removeall &7- &bRemove all your friends",
                "&9&l-------------------------------------------"
        ));
        yml.addDefault("messages.max-friends", list(
                "&9&l-------------------------------------------",
                "&cYou have reached the maximum of %0% friends. To add more friends please remove some. If you wish to clear your friends list, please contact support.",
                "&9&l-------------------------------------------"
        ));
        yml.addDefault("messages.request-sent", list(
                "&9&l-------------------------------------------",
                "&eYou sent a friend request to %0%&e! They have %1% minutes to accept it!",
                "&9&l-------------------------------------------"
        ));
        yml.addDefault("messages.friend-request-header", list(
                "&9&l-------------------------------------------",
                "&eFriend request from %0%"
        ));
        yml.addDefault("messages.friend-request-options", "&eClick one: ");
        yml.addDefault("messages.request-option-accept", "&a&l[ACCEPT]");
        yml.addDefault("messages.spacer", " &8- ");
        yml.addDefault("messages.request-option-deny", "&c&l[DENY]");
        yml.addDefault("messages.friend-request-footer", list(
                "&9&l-------------------------------------------"
        ));
        yml.addDefault("messages.request-accepted", list(
                "&9&l-------------------------------------------",
                "&aYou are now friends with %0%",
                "&9&l-------------------------------------------"
        ));
        yml.addDefault("messages.request-denied", list(
                "&9&l-------------------------------------------",
                "&eDeclined %0%&e's friend request!",
                "&9&l-------------------------------------------"
        ));
        yml.addDefault("messages.request-expired-sender", list(
                "&9&l-------------------------------------------",
                "&eYour friend request to %0% has expired.",
                "&9&l-------------------------------------------"
        ));
        yml.addDefault("messages.pending-requests", list(
                "&9&l-------------------------------------------",
                "&aYou have %0% pending friend requests.",
                "&eUse &b/f requests &eto see them!",
                "&9&l-------------------------------------------"
        ));
        yml.addDefault("messages.request-expired-receiver", list(
                "&9&l-------------------------------------------",
                "&eYour friend request from %0% has expired.",
                "&9&l-------------------------------------------"
        ));
        yml.addDefault("messages.list-header", list(
                "&9&l-------------------------------------------"
        ));
        yml.addDefault("messages.list-page", " &6Friends (Page %1% of %2%): ");
        yml.addDefault("messages.list-status.online", "&ais online (%0%).");
        yml.addDefault("messages.list-status.offline", "&cis offline");
        yml.addDefault("messages.list-format", "%0% %1%");
        yml.addDefault("messages.list-footer", list(
                "&9&l-------------------------------------------"
        ));
        yml.addDefault("messages.no-friends", "&eYou do not have any friends!");
        yml.addDefault("messages.requests", list("&e--- Friend requests (Page %0% of %1%) ---"));
        yml.addDefault("messages.no-request", list(
                "&9&l-------------------------------------------",
                "&cThat person hasn't invited you to be friends! Try &e/friend %0%",
                "&9&l-------------------------------------------"
        ));
        yml.addDefault("messages.toggle", list(
                "&9&l-------------------------------------------",
                "&eFriend requests %0%!",
                "&9&l-------------------------------------------"
        ));
        yml.addDefault("messages.request-not-allowed", list(
                "&9&l-------------------------------------------",
                "&cYou aren't allowed to send friend requests to this person!",
                "&9&l-------------------------------------------"
        ));
        yml.addDefault("messages.removeall", list(
                "&9&l-------------------------------------------",
                "&eYou have removed all of your friends!",
                "&9&l-------------------------------------------"
        ));
        yml.addDefault("messages.unfriended", list(
                "&9&l-------------------------------------------",
                "%0% &eremoved you from their friends list!",
                "&9&l-------------------------------------------"
        ));
        yml.addDefault("messages.friends-list-cleared", list(
                "&9&l-------------------------------------------",
                "&eYou have removed everyone from your friends list!",
                "&9&l-------------------------------------------"
        ));
        yml.addDefault("messages.friends-list-already-empty", list(
                "&9&l-------------------------------------------",
                "&cYour friends list is already empty!",
                "&9&l-------------------------------------------"
        ));
        yml.addDefault("messages.not-friends", list(
                "&9&l-------------------------------------------",
                "&c%0% &eisn't in your friends list!",
                "&9&l-------------------------------------------"
        ));
        yml.addDefault("messages.unfriend-sender", list(
                "&9&l-------------------------------------------",
                "&eYou removed %0% &efrom your friends list!",
                "&9&l-------------------------------------------"
        ));
        yml.addDefault("messages.unfriend-receiver", list(
                "&9&l-------------------------------------------",
                "&e%0% &eremoved you from their friends list!",
                "&9&l-------------------------------------------"
        ));
        yml.addDefault("messages.cannot-friend-yourself", list(
                "&9&l-------------------------------------------",
                "&eYou can't add yourself as a friend!",
                "&9&l-------------------------------------------"
        ));
        yml.addDefault("messages.already-friends", list(
                "&9&l-------------------------------------------",
                "&cYou're already friends with this person!",
                "&9&l-------------------------------------------"
        ));
        yml.addDefault("messages.join", "&e%0% joined.");
        yml.addDefault("messages.quit", "&e%0% left.");

        yml.addDefault("messages.online-status.idling", "&aIdling");
        yml.addDefault("messages.online-status.playing", "&ePlaying on %0%");

        yml.options().copyDefaults(true);
        save();
    }

    private java.util.List<String> list(String... lines) {
        return java.util.Arrays.asList(lines);
    }
}
