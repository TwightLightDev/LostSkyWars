package me.leoo.guilds.bukkit.commands;

import me.leoo.guilds.api.enums.PermissionsEnum;
import me.leoo.guilds.api.enums.TagColorEnum;
import me.leoo.guilds.api.events.player.*;
import me.leoo.guilds.api.objects.guild.GuildView;
import me.leoo.guilds.api.objects.player.PlayerView;
import me.leoo.guilds.bukkit.Guilds;
import me.leoo.guilds.bukkit.api.objects.*;
import me.leoo.guilds.bukkit.commands.chat.GuildChatCommand;
import me.leoo.guilds.bukkit.manager.GuildsManager;
import me.leoo.guilds.bukkit.manager.UserManager;
import me.leoo.guilds.bukkit.menu.MainMenu;
import me.leoo.guilds.bukkit.menu.MainNoGuildMenu;
import me.leoo.guilds.bukkit.menu.achievements.AchievementsMenu;
import me.leoo.guilds.bukkit.menu.permissions.PermissionsMenu;
import me.leoo.guilds.bukkit.menu.settings.GuildSettingsMenu;
import me.leoo.guilds.bukkit.utils.GuildUtils;
import me.leoo.guilds.bukkit.utils.TagUtils;
import me.leoo.guilds.libs.utils.bukkit.chat.CC;
import me.leoo.guilds.libs.utils.bukkit.commands.v2.VCommand;
import me.leoo.guilds.libs.utils.bukkit.commands.v2.annotation.Optional;
import me.leoo.guilds.libs.utils.bukkit.commands.v2.annotation.*;
import me.leoo.guilds.libs.utils.bukkit.config.ConfigManager;
import me.leoo.guilds.libs.utils.common.number.NumberUtil;
import me.leoo.guilds.libs.utils.common.string.StringUtil;
import me.leoo.guilds.libs.utils.common.time.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.*;
import java.util.stream.Collectors;

public class GuildCommand extends VCommand {
    private static final ConfigManager LANGUAGE = Guilds.get().getLanguage();

    private static final ConfigManager CONFIG = Guilds.get().getMainConfig();

    @Command(value = {"guild"}, executor = CommandExecutor.ALL)
    public void guilds(@Sender CommandSender paramCommandSender) {
        GuildUtils.sendHelp(paramCommandSender);
    }

    @SubCommand({"settmenu"})
    public void setmenu(@Sender Player paramPlayer) {
        (new GuildSettingsMenu(paramPlayer)).open();
    }

    @SubCommand({"accept"})
    public void accept(@Sender Player paramPlayer) {
        Guild guild1 = GuildsManager.getByPlayer(paramPlayer);
        if (guild1 != null) {
            paramPlayer.sendMessage(LANGUAGE.getString("guilds.message.already-in-guild"));
            return;
        }
        if (!GuildUtils.testConfigPermission((CommandSender)paramPlayer, "join")) {
            paramPlayer.sendMessage(LANGUAGE.getString("guilds.message.no-permissions"));
            return;
        }
        Guild guild2 = GuildsManager.getLastInvite(paramPlayer.getUniqueId());
        if (guild2 == null) {
            Objects.requireNonNull(paramPlayer);
            LANGUAGE.getList("guilds.commands.accept.not-invited").forEach(paramPlayer::sendMessage);
            return;
        }
        if (guild2.isFull()) {
            paramPlayer.sendMessage(LANGUAGE.getString("guilds.message.full-guild"));
            return;
        }
        Objects.requireNonNull(paramPlayer);
        parse(LANGUAGE.getList("guilds.commands.accept.joined-player"), guild2, (GuildRank)null, (OfflinePlayer)null).forEach(paramPlayer::sendMessage);
        List<String> list = LANGUAGE.getList("guilds.commands.accept.joined-broadcast");
        guild2.getMembers().forEach(mem -> {
            if (mem.isOnline()) {
                list.forEach(line -> mem.sendMessage(ChatColor.translateAlternateColorCodes('&', line).replace("{player}", paramPlayer.getDisplayName())) );
            }
        });

        guild2.addMember(paramPlayer.getUniqueId());
    }

    @SubCommand({"achievements"})
    public void achievements(@Sender Player paramPlayer) {
        Guild guild = GuildsManager.getByPlayer(paramPlayer);
        if (guild == null) {
            paramPlayer.sendMessage(LANGUAGE.getString("guilds.message.not-in-guild"));
            return;
        }
        (new AchievementsMenu(guild, paramPlayer)).open();
    }

    @SubCommand({"chat"})
    public void chat(@Sender Player paramPlayer, @Text String paramString) {
        GuildChatCommand.handleChatMessage(paramPlayer, paramString);
    }

    @SubCommand({"create"})
    public void create(@Sender Player paramPlayer, @Text String paramString) {
        if (!GuildUtils.testConfigPermission((CommandSender)paramPlayer, "create")) {
            paramPlayer.sendMessage(LANGUAGE.getString("guilds.message.no-permissions"));
            return;
        }
        if (paramString == null || paramString.isEmpty()) {
            paramPlayer.sendMessage(LANGUAGE.getString("guilds.commands.create.use"));
            return;
        }
        if (GuildsManager.getByPlayer(paramPlayer) != null) {
            paramPlayer.sendMessage(LANGUAGE.getString("guilds.message.already-in-guild"));
            return;
        }
        Guild guild = GuildsManager.getByName(paramString);
        if (guild == null ||
                guild.getMembers().isEmpty()) {
            int i = CONFIG.getInt("guilds.settings.guild-name.max-name-length");
            if (paramString.length() > i) {
                paramPlayer.sendMessage(LANGUAGE.getString("guilds.commands.create.name-length-more-than-limit")
                        .replace("{limit}", String.valueOf(i)));
                return;
            }
            String str = paramString.replace(" ", "");
            if (!str.matches(CONFIG.getString("guilds.settings.guild-name.chars"))) {
                paramPlayer.sendMessage(LANGUAGE.getString("guilds.commands.create.name-with-prohibited-chars"));
                return;
            }
            Guild guild1 = GuildsManager.createGuild(paramString, paramPlayer.getUniqueId());
            Objects.requireNonNull(paramPlayer);
            parse(LANGUAGE.getList("guilds.commands.create.success"), guild1, (GuildRank)null, (OfflinePlayer)null).forEach(paramPlayer::sendMessage);
            return;
        }
        Objects.requireNonNull(paramPlayer);
        parse(LANGUAGE.getList("guilds.commands.create.already-exists"), guild, (GuildRank)null, (OfflinePlayer)null).forEach(paramPlayer::sendMessage);
    }

    @SubCommand({"demote"})
    public void demote(@Sender Player paramPlayer, String paramString) {
        Guild guild = GuildsManager.getByPlayer(paramPlayer);
        GuildPlayer guildPlayer1 = UserManager.get(paramPlayer);
        if (guild == null) {
            paramPlayer.sendMessage(LANGUAGE.getString("guilds.message.not-in-guild"));
            return;
        }
        if (guildPlayer1 == null)
            return;
        if (!checkPermission(guildPlayer1, PermissionsEnum.RANKS))
            return;
        GuildPlayer guildPlayer2 = guild.getMember(paramString);
        if (guildPlayer2 == null) {
            paramPlayer.sendMessage(LANGUAGE.getString("guilds.message.player-not-found"));
            return;
        }
        if (paramPlayer == guildPlayer2)
            return;
        if (guildPlayer2.getGuildRank().isMasterRank()) {
            paramPlayer.sendMessage(LANGUAGE.getString("guilds.commands.demote.master-rank"));
            return;
        }
        if (guildPlayer2.getGuildRank().isDefaultRank()) {
            paramPlayer.sendMessage(LANGUAGE.getString("guilds.commands.demote.default-rank"));
            return;
        }
        guild.demote(paramPlayer, guildPlayer2);
    }

    @SubCommand({"disband", "delete"})
    public void delete(@Sender Player paramPlayer) {
        Guild guild = GuildsManager.getByPlayer(paramPlayer);
        GuildPlayer guildPlayer = UserManager.get(paramPlayer);
        if (guild == null) {
            paramPlayer.sendMessage(LANGUAGE.getString("guilds.message.not-in-guild"));
            return;
        }
        if (guildPlayer == null)
            return;
        if (!guildPlayer.getGuildRank().isMasterRank()) {
            paramPlayer.sendMessage(LANGUAGE.getString("guilds.message.no-rank"));
            return;
        }
        requestConfirmation(paramPlayer, LANGUAGE.getList("guilds.commands.disband.confirm-request"), CONFIG
                .getString("guilds.commands.guild.disband.confirm"), () -> {
            Objects.requireNonNull(paramPlayer);
            parse(LANGUAGE.getList("guilds.commands.disband.success"), guild, (GuildRank)null, (OfflinePlayer)null).forEach(paramPlayer::sendMessage);
            GuildsManager.deleteGuild(guild);
        });
    }

    @SubCommand({"discord"})
    public void discord(@Sender Player paramPlayer, @Optional String paramString) {
        Guild guild = GuildsManager.getByPlayer(paramPlayer);
        GuildPlayer guildPlayer = UserManager.get(paramPlayer);
        if (guild == null) {
            paramPlayer.sendMessage(LANGUAGE.getString("guilds.message.not-in-guild"));
            return;
        }
        if (guildPlayer == null)
            return;
        if (paramString == null || paramString.isEmpty()) {
            for (String str : parse(LANGUAGE.getList("guilds.commands.discord.message"), guild, (GuildRank)null, (OfflinePlayer)null)) {
                str = str.replace("{editmsg-or-null}", guildPlayer.hasPermission(PermissionsEnum.DISCORD) ? LANGUAGE.getString("guilds.commands.discord.change-link-help") : "").replace("{link}", guild.getSettings().getEffectiveDiscordLink());
                paramPlayer.sendMessage(StringUtil.getCenteredMessage(str));
            }
            return;
        }
        if (!guildPlayer.hasPermission(PermissionsEnum.DISCORD)) {
            paramPlayer.sendMessage(LANGUAGE.getString("guilds.message.no-rank"));
            return;
        }
        if (!GuildUtils.testConfigPermission((CommandSender)paramPlayer, "discord")) {
            paramPlayer.sendMessage(LANGUAGE.getString("guilds.message.no-permissions"));
            return;
        }
        guild.updateDiscord(paramString);
        Objects.requireNonNull(paramPlayer);
        parse(LANGUAGE.getList("guilds.commands.discord.success"), guild, (GuildRank)null, (OfflinePlayer)paramPlayer).forEach(paramPlayer::sendMessage);
    }

    @SubCommand({"help"})
    public void help(@Sender CommandSender paramCommandSender) {
        GuildUtils.sendHelp(paramCommandSender);
    }

    @SubCommand({"history", "log", "member", "mypermission", "setrank", "top"})
    public void notImplemented(@Sender Player paramPlayer) {
        paramPlayer.sendMessage(LANGUAGE.getString("guilds.message.command-not-available"));
    }

    @SubCommand({"info"})
    public void info(@Sender Player paramPlayer) {
        Guild guild = GuildsManager.getByPlayer(paramPlayer);
        if (guild == null) {
            paramPlayer.sendMessage(LANGUAGE.getString("guilds.message.not-in-guild"));
            return;
        }
        Objects.requireNonNull(paramPlayer);
        parse(LANGUAGE.getList("guilds.commands.info.message"), guild, (GuildRank)null, (OfflinePlayer)null).forEach(paramPlayer::sendMessage);
    }

    @SubCommand({"invite"})
    public void invite(@Sender Player paramPlayer, OfflinePlayer paramOfflinePlayer) {
        Guild guild = GuildsManager.getByPlayer(paramPlayer);
        GuildPlayer guildPlayer = UserManager.get(paramPlayer);
        if (guild == null) {
            paramPlayer.sendMessage(LANGUAGE.getString("guilds.message.not-in-guild"));
            return;
        }
        if (guildPlayer == null)
            return;
        if (!checkPermission(guildPlayer, PermissionsEnum.INVITE)) {
            paramPlayer.sendMessage(LANGUAGE.getString("guilds.message.no-rank"));
            return;
        }
        if (guild.isFull()) {
            paramPlayer.sendMessage(LANGUAGE.getString("guilds.message.full-guild"));
            return;
        }
        if ((Guilds.get().isProxy() && !GuildsManager.getOnlinePlayers().contains(paramOfflinePlayer.getName())) || (
                !Guilds.get().isProxy() && Bukkit.getPlayer(paramOfflinePlayer.getName()) == null)) {
            paramPlayer.sendMessage(LANGUAGE.getString("guilds.message.player-not-found"));
            return;
        }
        if (paramOfflinePlayer == paramPlayer)
            return;
        if (GuildsManager.hasGuild(paramOfflinePlayer)) {
            paramPlayer.sendMessage(parse(LANGUAGE.getString("guilds.commands.invite.player-already-in-guild"), guild, (GuildRank)null, paramOfflinePlayer));
            return;
        }
        guild.inviteMember(paramPlayer, paramOfflinePlayer);
    }

    @SubCommand({"join"})
    public void join(@Sender Player paramPlayer, Guild paramGuild) {
        Guild guild = GuildsManager.getByPlayer(paramPlayer);
        GuildPlayer guildPlayer = UserManager.get(paramPlayer);
        if (guild != null) {
            paramPlayer.sendMessage(LANGUAGE.getString("guilds.message.already-in-guild"));
            return;
        }
        if (paramGuild == null) {
            paramPlayer.sendMessage(LANGUAGE.getString("guilds.message.join.guild-not-exist"));
            return;
        }
        if (!GuildUtils.testConfigPermission((CommandSender)paramPlayer, "join")) {
            paramPlayer.sendMessage(LANGUAGE.getString("guilds.message.no-permissions"));
            return;
        }
        if (paramGuild.getSettings().isShown()) {
            if (paramGuild.isFull()) {
                paramPlayer.sendMessage(LANGUAGE.getString("guilds.message.full-guild"));
                return;
            }
            GuildJoinEvent guildJoinEvent = new GuildJoinEvent((GuildView)paramGuild, paramPlayer.getUniqueId());
            if (!checkEvent((Event)guildJoinEvent, paramPlayer))
                return;
            Objects.requireNonNull(paramPlayer);
            parse(LANGUAGE.getList("guilds.commands.join.joined-player"), paramGuild, (GuildRank)null, (OfflinePlayer)null).forEach(paramPlayer::sendMessage);
            List<String> joinMsgs = parse(LANGUAGE.getList("guilds.commands.join.joined-broadcast"), paramGuild, (GuildRank)null, (OfflinePlayer)paramPlayer);
            paramGuild.getMembers().forEach(mem -> {
                if (mem.isOnline()) {
                    joinMsgs.forEach(line -> mem.sendMessage(ChatColor.translateAlternateColorCodes('&', line).replace("{player}", paramPlayer.getDisplayName())) );
                }
            });

            paramGuild.addMember(paramPlayer.getUniqueId());
        } else {
            paramPlayer.sendMessage(LANGUAGE.getString("guilds.commands.join.cant-join"));
        }
    }

    @SubCommand({"kick"})
    public void kick(@Sender Player paramPlayer, String paramString1, @Text String paramString2) {
        Guild guild = GuildsManager.getByPlayer(paramPlayer);
        GuildPlayer guildPlayer1 = UserManager.get(paramPlayer);
        if (guild == null) {
            paramPlayer.sendMessage(LANGUAGE.getString("guilds.message.not-in-guild"));
            return;
        }
        if (guildPlayer1 == null)
            return;
        if (!checkPermission(guildPlayer1, PermissionsEnum.KICK))
            return;
        GuildPlayer guildPlayer2 = guild.getMember(paramString1);
        if (guildPlayer2 == null) {
            paramPlayer.sendMessage(LANGUAGE.getString("guilds.message.player-not-found"));
            return;
        }
        if (guildPlayer2 == guildPlayer1 || guild.getLeader().getUuid().equals(guildPlayer2.getUuid()))
            return;
        if (GuildSettings.KICK_REASON && (paramString2 == null || paramString2.isEmpty())) {
            paramPlayer.sendMessage(LANGUAGE.getString("guilds.commands.kick.reason-not-entered"));
            return;
        }
        GuildKickEvent guildKickEvent = new GuildKickEvent((GuildView)guild, (PlayerView)guildPlayer1, (PlayerView)guildPlayer2, paramString2);
        if (!checkEvent((Event)guildKickEvent, paramPlayer))
            return;
        if (guildKickEvent.getReason() == null || guildKickEvent.getReason().isEmpty())
            guildKickEvent.setReason(LANGUAGE.getString("guilds.commands.kick.default-reason"));
        List<String> list = parse(LANGUAGE.getList("guilds.commands.kick.success"), guild, (GuildRank)null, (OfflinePlayer)paramPlayer);
        list.replaceAll(paramString -> paramString.replace("{playerTarget}", guildPlayer2.getFormattedName()).replace("{message}", guildKickEvent.getReason()));
        guild.getMembers().forEach(mem -> {
            if (mem.isOnline()) {
                list.forEach(line -> mem.sendMessage(ChatColor.translateAlternateColorCodes('&', line).replace("{player}", paramPlayer.getDisplayName())) );
            }
        });
        guild.removeMember(guildPlayer2.getUuid());
    }

    @SubCommand({"leave"})
    public void leave(@Sender Player paramPlayer) {
        Guild guild = GuildsManager.getByPlayer(paramPlayer);
        GuildPlayer guildPlayer = UserManager.get(paramPlayer);
        if (guild == null) {
            paramPlayer.sendMessage(LANGUAGE.getString("guilds.message.not-in-guild"));
            return;
        }
        if (guildPlayer == null)
            return;
        if (guildPlayer.getGuildRank().isMasterRank()) {
            paramPlayer.sendMessage(LANGUAGE.getString("guilds.commands.leave.owner"));
            return;
        }
        GuildLeaveEvent guildLeaveEvent = new GuildLeaveEvent((GuildView)guild, (PlayerView)guildPlayer);
        if (!checkEvent((Event)guildLeaveEvent, paramPlayer))
            return;
        guild.removeMember(paramPlayer.getUniqueId());
        Objects.requireNonNull(paramPlayer);
        parse(LANGUAGE.getList("guilds.commands.leave.success-player"), guild, (GuildRank)null, (OfflinePlayer)null).forEach(paramPlayer::sendMessage);
        List<String> msg = parse(LANGUAGE.getList("guilds.commands.leave.success-broadcast"), guild, (GuildRank)null, (OfflinePlayer)paramPlayer);
        guild.getMembers().forEach(mem -> {
            if (mem.isOnline()) {
                msg.forEach(line -> mem.sendMessage(ChatColor.translateAlternateColorCodes('&', line).replace("{player}", paramPlayer.getDisplayName())) );
            }
        });
    }

    @SubCommand({"members", "list"})
    public void members(@Sender Player paramPlayer) {
        Guild guild = GuildsManager.getByPlayer(paramPlayer);
        GuildPlayer guildPlayer = UserManager.get(paramPlayer);
        if (guild == null) {
            paramPlayer.sendMessage(LANGUAGE.getString("guilds.message.not-in-guild"));
            return;
        }
        if (guild.getSettings().isOnlineMode()) {
            paramPlayer.performCommand("guild online");
            return;
        }
        guild.sendMembersListFormatted((CommandSender)paramPlayer, false);
    }

    @SubCommand({"menu"})
    public void menu(@Sender Player paramPlayer) {
        Guild guild = GuildsManager.getByPlayer(paramPlayer);
        if (guild == null) {
            (new MainNoGuildMenu(paramPlayer)).open();
        } else {
            (new MainMenu(paramPlayer)).open();
        }
    }

    @SubCommand({"motd"})
    public void motd(@Sender Player paramPlayer, @Optional String paramString1, @Optional @Text String paramString2) {
        Guild guild = GuildsManager.getByPlayer(paramPlayer);
        GuildPlayer guildPlayer = UserManager.get(paramPlayer);
        if (guild == null) {
            paramPlayer.sendMessage(LANGUAGE.getString("guilds.message.not-in-guild"));
            return;
        }
        if (guildPlayer == null)
            return;
        if (!checkPermission(guildPlayer, PermissionsEnum.MOTD))
            return;
        if (paramString1 == null)
            paramString1 = "";
        if (paramString2 == null || paramString1.isEmpty()) {
            switch (paramString1) {
                case "clear":
                    guild.updateMotd(null, paramPlayer.getUniqueId());
                    Objects.requireNonNull(paramPlayer);
                    LANGUAGE.getList("guilds.commands.motd.cleared").forEach(paramPlayer::sendMessage);
                    return;
                case "list":
                    guild.sendMotd((CommandSender)paramPlayer, true);
                    return;
                case "preview":
                    guild.sendMotd((CommandSender)paramPlayer, false);
                    return;
            }
            Objects.requireNonNull(paramPlayer);
            LANGUAGE.getList("guilds.commands.motd.sub-cmds").forEach(paramPlayer::sendMessage);
        } else {
            String str1;
            int i;
            String str2;
            switch (paramString1) {
                case "add":
                    guild.addMotdLine(paramString2, paramPlayer.getUniqueId());
                    for (String str : LANGUAGE.getList("guilds.commands.motd.set-line"))
                        paramPlayer.sendMessage(str
                                .replace("{number}", String.valueOf(guild.getSettings().getMotd().size()))
                                .replace("{text}", CC.color(paramString2)));
                    return;
                case "set":
                    str1 = paramString2.split(" ")[0];
                    if (str1 == null || str1.isEmpty() || paramString2.length() < 3 || !NumberUtil.isInt(str1)) {
                        Objects.requireNonNull(paramPlayer);
                        LANGUAGE.getList("guilds.commands.motd.sub-cmds").forEach(paramPlayer::sendMessage);
                        return;
                    }
                    if (!NumberUtil.isInt(str1)) {
                        paramPlayer.sendMessage(LANGUAGE.getString("guilds.commands.motd.line-must-be-number"));
                        return;
                    }
                    i = NumberUtil.toInt(str1);
                    str2 = paramString2.substring(2);
                    if (!guild.setMotdLine(paramPlayer, i - 1, str2, paramPlayer.getUniqueId()))
                        return;
                    for (String str : LANGUAGE.getList("guilds.commands.motd.set-line"))
                        paramPlayer.sendMessage(str
                                .replace("{number}", String.valueOf(i))
                                .replace("{text}", CC.color(str2)));
                    return;
            }
            Objects.requireNonNull(paramPlayer);
            LANGUAGE.getList("guilds.commands.motd.sub-cmds").forEach(paramPlayer::sendMessage);
        }
    }

    @SubCommand({"mute"})
    public void mute(@Sender Player paramPlayer, String paramString1, String paramString2) {
        Guild guild = GuildsManager.getByPlayer(paramPlayer);
        GuildPlayer guildPlayer = UserManager.get(paramPlayer);
        if (guild == null) {
            paramPlayer.sendMessage(LANGUAGE.getString("guilds.message.not-in-guild"));
            return;
        }
        if (guildPlayer == null)
            return;
        if (!checkPermission(guildPlayer, PermissionsEnum.MUTE))
            return;
        long l = TimeUtil.millisFromTimeString(paramString2);
        if (l <= 0L) {
            paramPlayer.sendMessage(LANGUAGE.getString("guilds.commands.mute.invalid-time"));
            return;
        }
        if (paramString1.equals("everyone")) {
            guild.muteAll(l);
            List<String> list = parse(LANGUAGE.getList("guilds.commands.mute.success-everyone"), (Guild)null, (GuildRank)null, (OfflinePlayer)paramPlayer);
            list.replaceAll(paramString3 -> paramString3.replace("{time}", paramString2));
            guild.getMembers().forEach(mem -> {
                if (mem.isOnline()) {
                    list.forEach(line -> mem.sendMessage(ChatColor.translateAlternateColorCodes('&', line).replace("{player}", paramPlayer.getDisplayName())) );
                }
            });
        } else {
            OfflinePlayer offlinePlayer = GuildUtils.getOfflinePlayerByName(paramString1);
            if (paramPlayer == offlinePlayer)
                return;
            if (!guild.isMember(offlinePlayer.getUniqueId())) {
                paramPlayer.sendMessage(LANGUAGE.getString("guilds.message.player-not-found"));
                return;
            }
            GuildPlayer guildPlayer1 = UserManager.get(offlinePlayer.getUniqueId());
            GuildMuteEvent guildMuteEvent = new GuildMuteEvent((GuildView)guild, (PlayerView)guildPlayer1, l);
            if (!checkEvent((Event)guildMuteEvent, paramPlayer))
                return;
            guildPlayer1.mute(l, true);
            List<String> list = parse(LANGUAGE.getList("guilds.commands.mute.success-player"), (Guild)null, (GuildRank)null, (OfflinePlayer)paramPlayer);
            list.replaceAll(paramString3 -> paramString2.replace("{playerTarget}", guildPlayer1.getFormattedName()).replace("{time}", paramString2));
            guild.getMembers().forEach(mem -> {
                if (mem.isOnline()) {
                    list.forEach(line -> mem.sendMessage(ChatColor.translateAlternateColorCodes('&', line).replace("{player}", paramPlayer.getDisplayName())) );
                }
            });
        }
    }

    @SubCommand({"notifications"})
    public void notifications(@Sender Player paramPlayer) {
        Guild guild = GuildsManager.getByPlayer(paramPlayer);
        GuildPlayer guildPlayer = UserManager.get(paramPlayer);
        if (guild == null) {
            paramPlayer.sendMessage(LANGUAGE.getString("guilds.message.not-in-guild"));
            return;
        }
        if (guildPlayer == null)
            return;
        if (guildPlayer.isNotifications()) {
            guildPlayer.setNotifications(false);
            Objects.requireNonNull(paramPlayer);
            LANGUAGE.getList("guilds.commands.notifications.enabled").forEach(paramPlayer::sendMessage);
        } else {
            guildPlayer.setNotifications(true);
            Objects.requireNonNull(paramPlayer);
            LANGUAGE.getList("guilds.commands.notifications.disabled").forEach(paramPlayer::sendMessage);
        }
        guild.savePlayer(guildPlayer);
        guild.sync();
    }

    @SubCommand({"online"})
    public void online(@Sender Player paramPlayer) {
        Guild guild = GuildsManager.getByPlayer(paramPlayer);
        if (guild == null) {
            paramPlayer.sendMessage(LANGUAGE.getString("guilds.message.not-in-guild"));
            return;
        }
        guild.sendMembersListFormatted((CommandSender)paramPlayer, true);
    }

    @SubCommand({"onlinemode"})
    public void onlineMode(@Sender Player paramPlayer) {
        Guild guild = GuildsManager.getByPlayer(paramPlayer);
        GuildPlayer guildPlayer = UserManager.get(paramPlayer);
        if (guild == null) {
            paramPlayer.sendMessage(LANGUAGE.getString("guilds.message.not-in-guild"));
            return;
        }
        if (guildPlayer == null)
            return;
        if (!checkPermission(guildPlayer, PermissionsEnum.SETTINGS))
            return;
        boolean bool = guild.getSettings().isOnlineMode();
        guild.getSettings().setOnlineMode(!bool);
        guild.saveGuild();
        guild.sync();
        Objects.requireNonNull(paramPlayer);
        LANGUAGE.getList("guilds.commands.onlinemode." + (bool ? "disabled" : "enabled")).forEach(paramPlayer::sendMessage);
    }

    @SubCommand({"party"})
    public void party(@Sender Player paramPlayer) {
        Guild guild = GuildsManager.getByPlayer(paramPlayer);
        GuildPlayer guildPlayer = UserManager.get(paramPlayer);
        if (guild == null) {
            paramPlayer.sendMessage(LANGUAGE.getString("guilds.message.not-in-guild"));
            return;
        }
        if (guildPlayer == null)
            return;
        if (!checkPermission(guildPlayer, PermissionsEnum.PARTY))
            return;
        if (!Guilds.get().isProxy())
            for (GuildPlayer guildPlayer1 : guild.getMembers()) {
                if (guildPlayer == guildPlayer1)
                    continue;
                paramPlayer.performCommand(CONFIG.getString("guilds.settings.guild-party.party-command")
                        .replace("{player}", Bukkit.getOfflinePlayer(guildPlayer1.getUuid()).getName()));
            }
    }

    @SubCommand({"permissions"})
    public void permissions(@Sender Player paramPlayer) {
        Guild guild = GuildsManager.getByPlayer(paramPlayer);
        GuildPlayer guildPlayer = UserManager.get(paramPlayer);
        if (guild == null) {
            paramPlayer.sendMessage(LANGUAGE.getString("guilds.message.not-in-guild"));
            return;
        }
        if (guildPlayer == null)
            return;
        if (!checkPermission(guildPlayer, PermissionsEnum.RANKS))
            return;
        (new PermissionsMenu(paramPlayer)).open();
    }

    @SubCommand({"promote"})
    public void promote(@Sender Player paramPlayer, String paramString) {
        Guild guild = GuildsManager.getByPlayer(paramPlayer);
        GuildPlayer guildPlayer1 = UserManager.get(paramPlayer);
        if (guild == null) {
            paramPlayer.sendMessage(LANGUAGE.getString("guilds.message.not-in-guild"));
            return;
        }
        if (guildPlayer1 == null)
            return;
        if (!checkPermission(guildPlayer1, PermissionsEnum.RANKS))
            return;
        GuildPlayer guildPlayer2 = guild.getMember(paramString);
        if (guildPlayer2 == null) {
            paramPlayer.sendMessage(LANGUAGE.getString("guilds.message.player-not-found"));
            return;
        }
        if (paramPlayer == guildPlayer2)
            return;
        if (guildPlayer2.getGuildRank().isMasterRank()) {
            paramPlayer.sendMessage(LANGUAGE.getString("guilds.commands.promote.already-master-rank"));
            return;
        }
        if (guild.getNextRank(guildPlayer2.getGuildRank()).equals(guild.getMasterRank())) {
            paramPlayer.sendMessage(LANGUAGE.getString("guilds.commands.promote.next-master-rank"));
            return;
        }
        guild.promote(paramPlayer, guildPlayer2);
    }

    @SubCommand({"quest"})
    public void quest(@Sender Player paramPlayer) {
        Guild guild = GuildsManager.getByPlayer(paramPlayer);
        if (guild == null) {
            paramPlayer.sendMessage(LANGUAGE.getString("guilds.message.not-in-guild"));
            return;
        }
        Objects.requireNonNull(paramPlayer);
        parse(LANGUAGE.getList("guilds.commands.quest.message"), guild, (GuildRank)null, (OfflinePlayer)null).forEach(paramPlayer::sendMessage);
    }

    @SubCommand({"rename"})
    public void rename(@Sender Player paramPlayer, String paramString) {
        Guild guild = GuildsManager.getByPlayer(paramPlayer);
        GuildPlayer guildPlayer = UserManager.get(paramPlayer);
        if (guild == null) {
            paramPlayer.sendMessage(LANGUAGE.getString("guilds.message.not-in-guild"));
            return;
        }
        if (guildPlayer == null)
            return;
        if (!checkPermission(guildPlayer, PermissionsEnum.NAME))
            return;
        guild.rename(paramString, paramPlayer);
    }

    @SubCommand({"settings"})
    public void settings(@Sender Player paramPlayer, String paramString1, @Optional @Text String paramString2) {
        Guild guild = GuildsManager.getByPlayer(paramPlayer);
        GuildPlayer guildPlayer = UserManager.get(paramPlayer);
        if (guild == null) {
            paramPlayer.sendMessage(LANGUAGE.getString("guilds.message.not-in-guild"));
            return;
        }
        if (guildPlayer == null)
            return;
        if (!checkPermission(guildPlayer, PermissionsEnum.SETTINGS))
            return;
        if (paramString1.equalsIgnoreCase("description")) {
            guild.getSettings().setDescription(paramString2);
            guild.saveGuild();
            guild.sync();
            guild.saveAll();
            parse(LANGUAGE.getList("guilds.commands.settings.description.success").stream().map(line -> line.replace("{message}", paramString2)).collect(Collectors.toList()), guild, (GuildRank)null, (OfflinePlayer)paramPlayer).forEach(paramPlayer::sendMessage);
            return;
        }
        if (paramString1.equalsIgnoreCase("shown"))
            if (paramString2 != null) {
                if (paramString2.equalsIgnoreCase("true") || paramString2.equalsIgnoreCase("t")) {
                    guild.getSettings().updateShown(true, guild);
                    Objects.requireNonNull(paramPlayer);
                    parse(LANGUAGE.getList("guilds.commands.settings.shown.true"), guild, (GuildRank)null, (OfflinePlayer)paramPlayer).forEach(paramPlayer::sendMessage);
                } else if (paramString2.equalsIgnoreCase("false") || paramString2.equalsIgnoreCase("f")) {
                    guild.getSettings().updateShown(false, guild);
                    Objects.requireNonNull(paramPlayer);
                    parse(LANGUAGE.getList("guilds.commands.settings.shown.false"), guild, (GuildRank)null, (OfflinePlayer)paramPlayer).forEach(paramPlayer::sendMessage);
                } else {
                    for (String str : LANGUAGE.getList("guilds.commands.settings.shown.error"))
                        paramPlayer.sendMessage(str.replace("{value}", paramString2));
                }
            } else {
                for (String str : LANGUAGE.getList("guilds.commands.settings.use"))
                    paramPlayer.sendMessage(str);
            }
    }

    @SubCommand({"slow"})
    public void slow(@Sender Player paramPlayer) {
        Guild guild = GuildsManager.getByPlayer(paramPlayer);
        GuildPlayer guildPlayer = UserManager.get(paramPlayer);
        if (guild == null) {
            paramPlayer.sendMessage(LANGUAGE.getString("guilds.message.not-in-guild"));
            return;
        }
        if (guildPlayer == null)
            return;
        if (!checkPermission(guildPlayer, PermissionsEnum.SETTINGS))
            return;
        if (guild.getSettings().isSlowMode()) {
            guild.getSettings().updateSlowMode(false, guild);
            paramPlayer.sendMessage(parse(LANGUAGE.getString("guilds.commands.slow.disabled"), guild, (GuildRank)null, (OfflinePlayer)paramPlayer));
        } else {
            guild.getSettings().updateSlowMode(true, guild);
            paramPlayer.sendMessage(parse(LANGUAGE.getString("guilds.commands.slow.enabled")
                    .replace("{chat-cooldown}", String.valueOf(CONFIG.getInt("guilds.settings.guild-chat.mute-time"))), guild, (GuildRank)null, (OfflinePlayer)paramPlayer));
        }
    }

    @SubCommand({"tag"})
    public void tag(@Sender Player paramPlayer, @Text String paramString) {
        Guild guild = GuildsManager.getByPlayer(paramPlayer);
        GuildPlayer guildPlayer = UserManager.get(paramPlayer);
        if (guild == null) {
            paramPlayer.sendMessage(LANGUAGE.getString("guilds.message.not-in-guild"));
            return;
        }
        if (guildPlayer == null)
            return;
        if (!checkPermission(guildPlayer, PermissionsEnum.TAG))
            return;
        if (!GuildUtils.testConfigPermission((CommandSender)paramPlayer, "tag")) {
            paramPlayer.sendMessage(LANGUAGE.getString("guilds.message.no-permissions"));
            return;
        }
        if (paramString == null || paramString.isEmpty()) {
            Objects.requireNonNull(paramPlayer);
            LANGUAGE.getList("guilds.commands.tag.use").forEach(paramPlayer::sendMessage);
            return;
        }
        TagUtils.updateTag(guild, paramString, paramPlayer);
    }

    @SubCommand({"tagcolor"})
    public void tagColor(@Sender Player paramPlayer, @Optional String paramString) {
        if (CONFIG.getBoolean("guilds.settings.guild-tag.disable-color-system"))
            return;
        Guild guild = GuildsManager.getByPlayer(paramPlayer);
        GuildPlayer guildPlayer = UserManager.get(paramPlayer);
        if (guild == null) {
            paramPlayer.sendMessage(LANGUAGE.getString("guilds.message.not-in-guild"));
            return;
        }
        if (guildPlayer == null)
            return;
        if (!checkPermission(guildPlayer, PermissionsEnum.TAG))
            return;
        if (guild.getTag() == null) {
            paramPlayer.sendMessage(LANGUAGE.getString("guilds.commands.tagcolor.no-tag"));
            return;
        }
        TagUtils.updateColor(guild, TagColorEnum.getByName(paramString), paramPlayer);
    }

    @SubCommand({"toggle"})
    public void toggle(@Sender Player paramPlayer) {
        Guild guild = GuildsManager.getByPlayer(paramPlayer);
        GuildPlayer guildPlayer = UserManager.get(paramPlayer);
        if (guild == null) {
            paramPlayer.sendMessage(LANGUAGE.getString("guilds.message.not-in-guild"));
            return;
        }
        if (guildPlayer == null)
            return;
        if (guildPlayer.isToggle()) {
            guildPlayer.setToggle(false);
            Objects.requireNonNull(paramPlayer);
            LANGUAGE.getList("guilds.commands.toggle.enabled").forEach(paramPlayer::sendMessage);
        } else {
            guildPlayer.setToggle(true);
            Objects.requireNonNull(paramPlayer);
            LANGUAGE.getList("guilds.commands.toggle.disabled").forEach(paramPlayer::sendMessage);
        }
        guild.sync();
    }

    @SubCommand(value = {"reload"}, executor = CommandExecutor.ALL)
    public void reload(@Sender CommandSender paramCommandSender) {
        if (paramCommandSender.hasPermission(CONFIG.getString("guilds.permissions.admin"))) {
            ConfigManager.reloadAll();
            Guilds.get().getRewardManager().reload();
            paramCommandSender.sendMessage("reloaded successfully!");
        }
    }

    @SubCommand({"transfer"})
    public void transfer(@Sender Player paramPlayer, String paramString) {
        Guild guild = GuildsManager.getByPlayer(paramPlayer);
        GuildPlayer guildPlayer1 = UserManager.get(paramPlayer);
        if (guild == null) {
            paramPlayer.sendMessage(LANGUAGE.getString("guilds.message.not-in-guild"));
            return;
        }
        if (guildPlayer1 == null)
            return;
        if (!guildPlayer1.getGuildRank().isMasterRank()) {
            paramPlayer.sendMessage(LANGUAGE.getString("guilds.message.no-rank"));
            return;
        }
        GuildPlayer guildPlayer2 = guild.getMember(paramString);
        if (guildPlayer2 == null) {
            paramPlayer.sendMessage(LANGUAGE.getString("guilds.message.player-not-found"));
            return;
        }
        if (paramPlayer == guildPlayer2)
            return;
        guild.transfer(paramPlayer, guildPlayer2);
    }

    @SubCommand({"unmute"})
    public void unmute(@Sender Player paramPlayer, String paramString) {
        Guild guild = GuildsManager.getByPlayer(paramPlayer);
        GuildPlayer guildPlayer = UserManager.get(paramPlayer);
        if (guild == null) {
            paramPlayer.sendMessage(LANGUAGE.getString("guilds.message.not-in-guild"));
            return;
        }
        if (guildPlayer == null)
            return;
        if (!checkPermission(guildPlayer, PermissionsEnum.MUTE))
            return;
        if (paramString.equals("everyone")) {
            guild.unmuteAll();
            List<String> list = LANGUAGE.getList("guilds.commands.unmute.success-everyone");
            guild.getMembers().forEach(mem -> {
                if (mem.isOnline()) {
                    list.forEach(line -> mem.sendMessage(ChatColor.translateAlternateColorCodes('&', line).replace("{player}", paramPlayer.getDisplayName())) );
                }
            });
        } else {
            OfflinePlayer offlinePlayer = GuildUtils.getOfflinePlayerByName(paramString);
            if (paramPlayer == offlinePlayer)
                return;
            if (!guild.isMember(offlinePlayer.getUniqueId())) {
                paramPlayer.sendMessage(LANGUAGE.getString("guilds.message.player-not-found"));
                return;
            }
            GuildPlayer guildPlayer1 = UserManager.get(offlinePlayer.getUniqueId());
            if (!guildPlayer1.isMuted()) {
                Objects.requireNonNull(paramPlayer);
                parse(LANGUAGE.getList("guilds.commands.unmute.not-muted"), guild, (GuildRank)null, (OfflinePlayer)paramPlayer).forEach(paramPlayer::sendMessage);
                return;
            }
            GuildUnmuteEvent guildUnmuteEvent = new GuildUnmuteEvent((GuildView)guild, (PlayerView)guildPlayer1);
            if (!checkEvent((Event)guildUnmuteEvent, paramPlayer))
                return;
            guildPlayer1.unmute(true);
            List<String> list = parse(LANGUAGE.getList("guilds.commands.unmute.success-player"), (Guild)null, (GuildRank)null, (OfflinePlayer)paramPlayer);
            list.replaceAll(paramString1 -> paramString1.replace("{playerTarget}", guildPlayer1.getFormattedName()));
            guild.broadcast(list);
        }
    }

    @TabComplete(value = "demote", aliases = {"kick", "mute", "promote", "transfer", "unmute"})
    public List<String> guildPlayerTabComplete(CommandSender paramCommandSender, String paramString, String[] paramArrayOfString) {
        if (!(paramCommandSender instanceof Player))
            return Collections.emptyList();
        Player player = (Player)paramCommandSender;
        Guild guild = GuildsManager.getByPlayer(player);
        if (paramArrayOfString.length == 2 &&
                guild != null) {
            List<String> list = (List)guild.getMembers().stream().map(paramGuildPlayer -> paramGuildPlayer.getOfflinePlayer().getName()).collect(Collectors.toList());
            if (paramArrayOfString[0].contains("mute"))
                list.add("everyone");
            return list;
        }
        return Collections.emptyList();
    }

    @TabComplete("join")
    public List<String> guildJoinTabComplete(CommandSender paramCommandSender, String paramString, String[] paramArrayOfString) {
        if (!(paramCommandSender instanceof Player))
            return Collections.emptyList();
        Player player = (Player)paramCommandSender;
        Guild guild = GuildsManager.getByPlayer(player);
        if (paramArrayOfString.length == 2)
            return (List<String>)GuildsManager.getShownGuilds().stream().map(Guild::getName).collect(Collectors.toList());
        return Collections.emptyList();
    }

    @TabComplete("discord")
    public List<String> discordTabComplete(CommandSender paramCommandSender, String paramString, String[] paramArrayOfString) {
        if (!(paramCommandSender instanceof Player))
            return Collections.emptyList();
        if (paramArrayOfString.length == 2)
            return Collections.singletonList("reset");
        return Collections.emptyList();
    }

    @TabComplete("invite")
    public List<String> guildInviteTabComplete(CommandSender paramCommandSender, String paramString, String[] paramArrayOfString) {
        if (!(paramCommandSender instanceof Player))
            return Collections.emptyList();
        if (paramArrayOfString.length == 2) {
            if (Guilds.get().isProxy())
                return new ArrayList<>(GuildsManager.getOnlinePlayers());
            return (List<String>)Bukkit.getOnlinePlayers().stream().map(OfflinePlayer::getName).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @TabComplete("settings")
    public List<String> guildSettingsTabComplete(CommandSender paramCommandSender, String paramString, String[] paramArrayOfString) {
        if (!(paramCommandSender instanceof Player))
            return Collections.emptyList();
        if (paramArrayOfString.length == 2)
            return Arrays.asList(new String[] { "SHOWN", "DESCRIPTION", "GAMES" });
        return Collections.emptyList();
    }

    @TabComplete("motd")
    public List<String> guildMotdTabComplete(CommandSender paramCommandSender, String paramString, String[] paramArrayOfString) {
        if (!(paramCommandSender instanceof Player))
            return Collections.emptyList();
        Player player = (Player)paramCommandSender;
        Guild guild = GuildsManager.getByPlayer(player);
        GuildPlayer guildPlayer = UserManager.get(player);
        if (paramArrayOfString.length == 2 &&
                guildPlayer != null && guildPlayer.hasPermission(PermissionsEnum.MOTD))
            return Arrays.asList(new String[] { "add", "clear", "list", "preview", "set" });
        return Collections.emptyList();
    }

    @TabComplete("tagcolor")
    public List<String> guildTagColorTabComplete(CommandSender paramCommandSender, String paramString, String[] paramArrayOfString) {
        if (!(paramCommandSender instanceof Player))
            return Collections.emptyList();
        Player player = (Player)paramCommandSender;
        Guild guild = GuildsManager.getByPlayer(player);
        if (guild != null)
            return (List<String>)guild.getTagList(player).stream().map(GuildTag::getName).collect(Collectors.toList());
        return Collections.emptyList();
    }

    private boolean checkPermission(GuildPlayer paramGuildPlayer, PermissionsEnum paramPermissionsEnum) {
        if (!paramGuildPlayer.hasPermission(paramPermissionsEnum)) {
            paramGuildPlayer.sendMessage(LANGUAGE.getString("guilds.message.no-rank"));
            return false;
        }
        return true;
    }

    private boolean checkEvent(Event paramEvent, Player paramPlayer) {
        return GuildUtils.checkEvent(paramEvent, paramPlayer);
    }

    public String parse(String paramString, Guild paramGuild, GuildRank paramGuildRank, OfflinePlayer paramOfflinePlayer) {
        return GuildUtils.parse(paramString, paramGuild, paramGuildRank, paramOfflinePlayer);
    }

    public List<String> parse(List<String> paramList, Guild paramGuild, GuildRank paramGuildRank, OfflinePlayer paramOfflinePlayer) {
        return GuildUtils.parse(paramList, paramGuild, paramGuildRank, paramOfflinePlayer);
    }
}

