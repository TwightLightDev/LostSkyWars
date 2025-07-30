// Decompiled with: CFR 0.152
// Class Version: 8
package me.leoo.guilds.bukkit.backend.database.sql;

import com.google.gson.Gson;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import me.leoo.guilds.api.enums.PermissionsEnum;
import me.leoo.guilds.api.enums.TagColorEnum;
import me.leoo.guilds.bukkit.Guilds;
import me.leoo.guilds.bukkit.api.objects.Guild;
import me.leoo.guilds.bukkit.api.objects.GuildFinder;
import me.leoo.guilds.bukkit.api.objects.GuildPlayer;
import me.leoo.guilds.bukkit.api.objects.GuildQuest;
import me.leoo.guilds.bukkit.api.objects.GuildRank;
import me.leoo.guilds.bukkit.api.objects.GuildSettings;
import me.leoo.guilds.bukkit.api.objects.achievement.GuildAchievement;
import me.leoo.guilds.bukkit.backend.database.Database;
import me.leoo.guilds.bukkit.backend.database.sql.MigrationManager;
import me.leoo.guilds.bukkit.backend.database.sql.QueryRunner;
import me.leoo.guilds.bukkit.backend.database.sql.SQLUtils;
import me.leoo.guilds.bukkit.backend.database.sql.TableCreator;
import me.leoo.guilds.bukkit.manager.GuildsManager;
import me.leoo.guilds.bukkit.manager.UserManager;
import me.leoo.guilds.libs.utils.bukkit.config.ConfigManager;
import me.leoo.guilds.libs.utils.common.file.FileUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

public class SQLStorage
        extends Database {
    private HikariDataSource dataSource;
    private static final Gson GSON = new Gson();
    private static final ConfigManager CONFIG = Guilds.get().getMainConfig();
    public static final String GUILDS = SQLUtils.validateTableName(CONFIG.getString("database.tables.guilds"));
    public static final String PLAYERS = SQLUtils.validateTableName(CONFIG.getString("database.tables.players"));
    public static final String RANKS = SQLUtils.validateTableName(CONFIG.getString("database.tables.ranks"));

    public SQLStorage(String string, ConfigManager configManager) {
        super(string.equalsIgnoreCase("mysql") ? Database.Type.MYSQL : Database.Type.SQLITE, configManager);
    }

    @Override
    public void connect(String string, int n, String string2, String string3, String string4, boolean bl, String string5, String string6) {
        HikariConfig hikariConfig = new HikariConfig();
        switch (this.getType()) {
            case SQLITE: {
                File file = FileUtil.generateFile(Guilds.get().getDataFolder().getPath(), "database.db");
                hikariConfig.setJdbcUrl("jdbc:sqlite:" + file);
                hikariConfig.setDriverClassName("org.sqlite.JDBC");
                hikariConfig.addDataSourceProperty("busy_timeout", (Object)"1000");
                break;
            }
            case MYSQL: {
                hikariConfig.setJdbcUrl(String.format("jdbc:mysql://%s:%s/%s", string, n, string2));
                hikariConfig.setUsername(string3);
                hikariConfig.setPassword(string4);
                if (string5.isEmpty()) break;
                hikariConfig.setDriverClassName(string5);
            }
        }
        hikariConfig.setMaximumPoolSize(10);
        hikariConfig.setConnectionTestQuery("SELECT 1");
        hikariConfig.setPoolName("Guilds-Pool");
        hikariConfig.addDataSourceProperty("useSSL", (Object)bl);
        hikariConfig.addDataSourceProperty("characterEncoding", (Object)"utf8");
        hikariConfig.addDataSourceProperty("useUnicode", (Object)true);
        hikariConfig.addDataSourceProperty("cachePrepStmts", (Object)true);
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", (Object)250);
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", (Object)2048);
        hikariConfig.addDataSourceProperty("useServerPrepStmts", (Object)true);
        this.dataSource = new HikariDataSource(hikariConfig);
        Guilds.get().getLogger().info("Connected to the database");
        if (!this.createTables()) {
            Bukkit.getLogger().severe("Failed to create the database tables.");
            Bukkit.getPluginManager().disablePlugin((Plugin)Guilds.get());
            return;
        }
        if (!this.migrateTables()) {
            return;
        }
        this.loadGuilds();
        Guilds.get().getLogger().info("Loaded " + GuildsManager.getGuilds().size() + " Guilds from database");
    }

    @Override
    public void shutdown() {
        for (Guild guild : new ArrayList<Guild>(GuildsManager.getGuilds())) {
            guild.saveAll();
            guild.destroy();
        }
        this.dataSource.close();
        Guilds.get().getLogger().info("Closed database connection");
    }

    @Override
    public void loadGuilds() {
        this.execute("SELECT uuid FROM " + GUILDS, (ResultSet resultSet) -> {
            while (resultSet.next()) {
                UUID uUID = UUID.fromString(resultSet.getString("uuid"));
                Guild guild = this.loadGuild(uUID, true);
                if (guild == null) {
                    Bukkit.getLogger().warning("Failed to load guild with uuid " + uUID);
                    continue;
                }
                String string = guild.getLoadValidation();
                if (string != null) {
                    Bukkit.getLogger().warning("Failed to load guild with uuid " + uUID + ": " + string);
                    continue;
                }
                GuildsManager.getGuilds().add(guild);
            }
        }, new Object[0]);
    }

    @Override
    public Guild loadGuild(UUID uUID, boolean bl) {
        if (GuildsManager.getByUuid(uUID) != null) {
            Guilds.debug("Guild with uuid " + uUID + " already loaded");
            return null;
        }
        ArrayList<GuildPlayer> arrayList = new ArrayList<GuildPlayer>();
        ArrayList<GuildRank> arrayList2 = new ArrayList();
        this.execute("SELECT * FROM " + RANKS + " WHERE guild = ?", (ResultSet resultSet) -> {
            while (resultSet.next()) {
                arrayList2.add(this.loadRank(uUID, UUID.fromString(resultSet.getString("uuid"))));
            }
        }, uUID.toString());
        if (arrayList2.isEmpty()) {
            Guilds.debug("No ranks found in guild with uuid " + uUID);
            this.registerRanks(uUID);
            Guilds.debug("Created default ranks for guild with uuid " + uUID);
            Guilds.debug("Loading guild with uuid " + uUID + " again");
            this.loadGuild(uUID, bl);
            return null;
        }
        GuildRank guildRank = arrayList2.stream().filter(GuildRank::isMasterRank).findFirst().orElse(null);
        if (bl && guildRank == null) {
            Bukkit.getLogger().warning("Guild master rank not found in guild with uuid " + uUID);
            return null;
        }
        GuildRank guildRank2 = arrayList2.stream().filter(GuildRank::isDefaultRank).findFirst().orElse(null);
        if (bl && guildRank2 == null) {
            Bukkit.getLogger().warning("Guild default rank not found in guild with uuid " + uUID);
            return null;
        }
        this.execute("SELECT * FROM " + PLAYERS + " WHERE guild = ?", (ResultSet resultSet) -> {
            while (resultSet.next()) {
                UUID uUID1 = UUID.fromString(resultSet.getString("uuid"));
                GuildPlayer guildPlayer = this.loadPlayer(uUID1);
                if (guildPlayer.getRankUuid() == null || arrayList2.stream().noneMatch(guildRank1 -> guildRank1.getUuid().equals(guildPlayer.getRankUuid()))) {
                    if (guildRank2 == null) {
                        Bukkit.getLogger().warning("User " + guildPlayer.getName() + " has invalid rank and no default rank to assign. Skipping user.");
                        return;
                    }
                    guildPlayer.setRankUuid(guildRank2.getUuid());
                }
                UserManager.put(uUID1, guildPlayer);
                arrayList.add(guildPlayer);
            }
        }, uUID.toString());
        arrayList.removeIf(Objects::isNull);
        if (bl && arrayList.isEmpty()) {
            Bukkit.getLogger().warning("Cannot load guild with uuid " + uUID + " because it has no players");
            return null;
        }
        Guilds.debug("All is good, loading guild with uuid " + uUID);
        return this.execute("SELECT * FROM " + GUILDS + " WHERE uuid = ?", (ResultSet resultSet) -> {
            if (!resultSet.next()) {
                return null;
            }
            GuildSettings guildSettings = new GuildSettings(resultSet.getString("description"), resultSet.getString("motd") == null ? null : new ArrayList<String>(Arrays.asList(resultSet.getString("motd").split("-n-"))), resultSet.getString("motdLastEditor") == null ? null : UUID.fromString(resultSet.getString("motdLastEditor")), SQLUtils.parseTimestamp(resultSet.getTimestamp("motdLastEdit")), resultSet.getBoolean("onlineMode"), resultSet.getBoolean("slowMode"), resultSet.getBoolean("shown"), resultSet.getString("games") == null ? new HashSet<String>() : new HashSet<String>(Arrays.asList(resultSet.getString("games").split("-n-"))), resultSet.getString("discordLink"), SQLUtils.parseTimestamp(resultSet.getTimestamp("creationTime")));
            GuildQuest guildQuest = new GuildQuest(UUID.fromString(resultSet.getString("uuid")), resultSet.getInt("questProgress"), SQLUtils.parseTimestamp(resultSet.getTimestamp("questReset")));
            GuildAchievement guildAchievement = new GuildAchievement();
            String string = resultSet.getString("achievements");
            Map map = (Map)GSON.fromJson(string, GuildAchievement.ACHIEVEMENT_TYPE);
            if (map != null) {
                guildAchievement.getProgress().putAll(map);
            }
            Guilds.debug("Loaded guild with uuid " + uUID + " with " + arrayList.size() + " players and " + arrayList2.size() + " ranks");
            return new Guild(UUID.fromString(resultSet.getString("uuid")), resultSet.getString("name"), resultSet.getString("tag"), TagColorEnum.valueOf(resultSet.getString("tagColor")), resultSet.getInt("level"), resultSet.getInt("exp"), guildAchievement, guildSettings, guildQuest, arrayList.stream().filter(guildPlayer -> guildRank != null && guildPlayer.getRankUuid().equals(guildRank.getUuid())).findFirst().orElse(null), arrayList, arrayList2);
        }, uUID.toString());
    }

    @Override
    public Guild registerGuild(String string) {
        if (GuildsManager.getByName(string) == null) {
            UUID uUID = UUID.randomUUID();
            while (GuildsManager.getByUuid(uUID) != null) {
                uUID = UUID.randomUUID();
            }
            Guilds.debug("Registering guild with uuid " + uUID);
            this.executeUpdate("INSERT INTO " + GUILDS + " (uuid, `name`, `tag`, tagColor, `description`, motd, motdLastEditor, creationTime, games, discordLink, achievements) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", uUID.toString(), string, null, TagColorEnum.GRAY.name(), null, null, null, new Timestamp(System.currentTimeMillis()), null, "", null);
            this.registerRanks(uUID);
            Guild guild = this.loadGuild(uUID, false);
            guild.saveGuild();
            return guild;
        }
        return null;
    }

    @Override
    public void saveGuild(Guild guild) {
        GuildAchievement guildAchievement = guild.getAchievement();
        String string = GSON.toJson(guildAchievement.getProgress());
        GuildSettings guildSettings = guild.getSettings();
        this.executeUpdate("UPDATE " + GUILDS + " SET `name` = ?, `tag` = ?, tagColor = ?, level = ?, exp = ?, `description` = ?, motd = ?, motdLastEditor = ?, motdLastEdit = ?,  games = ?, onlineMode = ?, slowMode = ?, shown = ?, discordLink = ?, questProgress = ?, questReset = ?, achievements = ? WHERE uuid = ?", guild.getName(), guild.getTag(), guild.getTagColor().name(), guild.getLevel().getLevel(), guild.getLevel().getXp(), guildSettings.getDescription() == null ? null : String.join((CharSequence)"-n-", guildSettings.getDescription()), guildSettings.getMotd() == null ? null : String.join((CharSequence)"-n-", guildSettings.getMotd()), guildSettings.getMotdLastEditorUuid() == null ? null : guildSettings.getMotdLastEditorUuid().toString(), SQLUtils.getTimestamp(guildSettings.getMotdLastEdit()), guildSettings.getGames().isEmpty() ? null : String.join((CharSequence)"-n-", guildSettings.getGames()), guildSettings.isOnlineMode(), guildSettings.isSlowMode(), guildSettings.isShown(), guildSettings.getDiscordLink(), guild.getQuest().getProgress(), SQLUtils.getTimestamp(guild.getQuest().getNextReset()), string, guild.getUuid().toString());
    }

    @Override
    public void deleteGuild(UUID uUID) {
        this.executeUpdate("DELETE FROM " + GUILDS + " WHERE uuid = ?", uUID.toString());
    }

    @Override
    public void saveGuildLevel(Guild guild) {
        this.executeUpdate("UPDATE " + GUILDS + " SET level = ?, exp = ? WHERE uuid = ?", guild.getLevel().getLevel(), guild.getLevel().getXp(), guild.getUuid().toString());
    }

    @Override
    public void registerPlayer(UUID uUID) {
        if (this.loadPlayer(uUID) == null) {
            Guilds.debug("Registering player with uuid " + uUID);
            this.executeUpdate("INSERT INTO " + PLAYERS + " (uuid, `name`, displayName, guild, `rank`, notifications, toggle, muteTime, joinTime, lastOnline) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", uUID.toString(), Bukkit.getPlayer(uUID).getName(), Bukkit.getPlayer(uUID).getDisplayName(), null, null, true, false, null, null, null);

        }
    }

    @Override
    public GuildPlayer loadPlayer(UUID uUID) {
        Guilds.debug("Loading player with uuid " + uUID);
        return this.execute("SELECT * FROM " + PLAYERS + " WHERE uuid = ?", (ResultSet resultSet) -> {
            if (!resultSet.next()) {
                return null;
            }
            String string = resultSet.getString("rank");
            return new GuildPlayer(uUID, resultSet.getString("name"), resultSet.getString("displayName"), string == null ? null : UUID.fromString(string), resultSet.getBoolean("notifications"), resultSet.getBoolean("toggle"), SQLUtils.parseTimestamp(resultSet.getTimestamp("muteTime")), SQLUtils.parseTimestamp(resultSet.getTimestamp("joinTime")), SQLUtils.parseTimestamp(resultSet.getTimestamp("lastOnline")), new GuildFinder());
        }, uUID.toString());
    }

    @Override
    public void savePlayer(Guild guild, GuildPlayer guildPlayer) {
        Guilds.debug("Saving player " + guildPlayer.getName() + " with uuid " + guildPlayer.getUuid());
        this.executeUpdate("UPDATE " + PLAYERS + " SET `name` = ?, displayName = ?, guild = ?, `rank` = ?, muteTime = ?, joinTime = ?, lastOnline = ? WHERE uuid = ?", guildPlayer.getName(), guildPlayer.getDisplayName(), guild == null ? null : guild.getUuid().toString(), guildPlayer.getGuildRank() == null ? null : guildPlayer.getGuildRank().getUuid().toString(), SQLUtils.getTimestamp(guildPlayer.getMuteTime()), SQLUtils.getTimestamp(guildPlayer.getJoin()), SQLUtils.getTimestamp(guildPlayer.getLastOnline()), guildPlayer.getUuid().toString());
    }

    @Override
    public void updatePlayer(GuildPlayer guildPlayer) {
        Guilds.debug("Updating player " + guildPlayer.getName() + " with uuid " + guildPlayer.getUuid());
        this.executeUpdate("UPDATE " + PLAYERS + " SET `name` = ?, displayName = ?, `rank` = ?, muteTime = ?, joinTime = ?, lastOnline = ? WHERE uuid = ?", guildPlayer.getName(), guildPlayer.getDisplayName(), guildPlayer.getGuildRank() == null ? null : guildPlayer.getGuildRank().getUuid().toString(), SQLUtils.getTimestamp(guildPlayer.getMuteTime()), SQLUtils.getTimestamp(guildPlayer.getJoin()), SQLUtils.getTimestamp(guildPlayer.getLastOnline()), guildPlayer.getUuid().toString());
    }

    @Override
    public void clearOldPlayers(Guild guild) {
        this.execute("SELECT * FROM " + PLAYERS + " WHERE guild = ?", (ResultSet resultSet) -> {
            while (resultSet.next()) {
                UUID uUID = UUID.fromString(resultSet.getString("uuid"));
                String string = resultSet.getString("name");
                if (guild.isMember(uUID)) continue;
                this.clearPlayer(uUID, string);
            }
        }, guild.getUuid().toString());
    }

    @Override
    public UUID registerRank(UUID uUID, String string, String string2, boolean bl) {
        if (this.execute("SELECT * FROM " + RANKS + " WHERE guild = ? AND `name` = ?", ResultSet::next, uUID.toString(), string).booleanValue()) {
            Guilds.debug("Rank " + string + " already exists in guild with uuid " + uUID);
            return null;
        }
        ConfigurationSection configurationSection = Guilds.get().getRanksConfig().getSection("guilds.ranks." + string);
        UUID uUID2 = UUID.randomUUID();
        String string3 = bl ? configurationSection.getString("displayName") : string2;
        String string4 = String.join((CharSequence)"-n-", bl ? configurationSection.getStringList("permissions") : new ArrayList());
        int n = bl ? configurationSection.getInt("priority") : 0;
        boolean bl2 = bl && configurationSection.getBoolean("master");
        boolean bl3 = bl && configurationSection.getBoolean("default");
        this.executeUpdate("INSERT INTO " + RANKS + " (uuid, guild, `name`, displayName, `tag`, permissions, priority, isDefault, isMaster) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)", uUID2.toString(), uUID.toString(), string, string3, string3, string4, n, bl3, bl2);
        Guilds.debug("Registered rank " + string + " with uuid " + uUID2 + " in guild with uuid " + uUID);
        return uUID2;
    }

    @Override
    public GuildRank loadRank(UUID uUID, UUID uUID2) {
        return this.execute("SELECT * FROM " + RANKS + " WHERE guild = ? AND uuid = ?", (ResultSet resultSet) -> {
            if (!resultSet.next()) {
                return null;
            }
            return new GuildRank(uUID2, resultSet.getString("name"), resultSet.getString("displayName"), resultSet.getString("tag"), this.parsePermissions(resultSet.getString("permissions")), resultSet.getInt("priority"), resultSet.getBoolean("isDefault"), resultSet.getBoolean("isMaster"));
        }, uUID.toString(), uUID2.toString());
    }

    @Override
    public void saveRank(UUID uUID, GuildRank guildRank) {
        this.executeUpdate("UPDATE " + RANKS + " SET guild = ?, `name` = ?, displayName = ?, `tag` = ?, permissions = ?, priority = ?, isDefault = ?, isMaster = ? WHERE guild = ? AND uuid = ?", uUID.toString(), guildRank.getName(), guildRank.getDisplayName(), guildRank.getTag(), guildRank.getPermissionsString(), guildRank.getPriority(), guildRank.isDefaultRank(), guildRank.isMasterRank(), uUID.toString(), guildRank.getUuid().toString());
    }

    @Override
    public void deleteRank(UUID uUID, UUID uUID2) {
        this.executeUpdate("DELETE FROM " + RANKS + " WHERE guild = ? AND uuid = ?", uUID.toString(), uUID2.toString());
    }

    @Override
    public void deleteOldRanks(Guild guild) {
    }

    public PreparedStatement prepareStatement(String string, Object ... objectArray) {
        return QueryRunner.prepareStatement((DataSource)this.dataSource, string, objectArray);
    }

    public <T> T execute(String string, Database.ThrowingFunction<ResultSet, T> throwingFunction, Object ... objectArray) {
        try {
            return QueryRunner.execute((DataSource)this.dataSource, string, throwingFunction, objectArray);
        }
        catch (Exception sQLException) {
            Bukkit.getLogger().warning("Error executing SQL: " + sQLException.getMessage());
            return null;
        }
    }

    public void execute(String string, Database.ThrowingConsumer<ResultSet> throwingConsumer, Object ... objectArray) {
        try {
            QueryRunner.execute((DataSource)this.dataSource, string, throwingConsumer, objectArray);
        }
        catch (Exception sQLException) {
            Bukkit.getLogger().warning("Error executing SQL: " + sQLException.getMessage());
        }
    }

    public boolean executeUpdate(String string, Object ... objectArray) {
        try {
            QueryRunner.executeUpdate((DataSource)this.dataSource, string, objectArray);
            return true;
        }
        catch (Exception sQLException) {
            Bukkit.getLogger().warning("Error executing SQL update: " + sQLException.getMessage());
            return false;
        }
    }

    protected void executeAsync(String string, Object ... objectArray) {
        QueryRunner.executeAsync((DataSource)this.dataSource, string, objectArray);
    }

    private void applyFields(PreparedStatement preparedStatement, Object ... objectArray) {
        QueryRunner.applyFields(preparedStatement, objectArray);
    }

    private boolean createTables() {
        Guilds.get().getLogger().info("Creating database tables...");
        boolean bl = this.getType() == Database.Type.MYSQL;
        String string = bl ? "id INT AUTO_INCREMENT PRIMARY KEY" : "id INTEGER PRIMARY KEY AUTOINCREMENT";
        String string2 = bl ? "UNIQUE KEY" : "UNIQUE";
        TableCreator tableCreator = new TableCreator(this.dataSource, string, string2);
        if (!tableCreator.isSuccess()) {
            return false;
        }
        Guilds.get().getLogger().info("Database tables created successfully.");
        if (!this.createOrReplaceIndex("idx_players_guild", PLAYERS, "guild")) {
            return false;
        }
        if (!this.createOrReplaceIndex("idx_players_rank", PLAYERS, "rank")) {
            return false;
        }
        if (!this.createOrReplaceIndex("idx_ranks_guild", RANKS, "guild")) {
            return false;
        }
        Guilds.get().getLogger().info("Database indexes created successfully.");
        return true;
    }

    private boolean migrateTables() {
        boolean bl = new MigrationManager((DataSource)this.dataSource, this.getType()).migrate(this.getType());
        if (!bl) {
            Bukkit.getPluginManager().disablePlugin((Plugin)Guilds.get());
        }
        return bl;
    }

    private boolean existColumn(String string, String string2) {
        return SQLUtils.existColumn(string, string2, (DataSource)this.dataSource);
    }

    private boolean createOrReplaceIndex(String string, String string2, String string3) {
        if (this.getType() == Database.Type.SQLITE) {
            return this.executeUpdate("CREATE INDEX IF NOT EXISTS " + string + " ON " + string2 + "(" + string3 + ")", new Object[0]);
        }
        Integer n = this.execute("SELECT COUNT(1) as cnt FROM INFORMATION_SCHEMA.STATISTICS WHERE table_schema=DATABASE() AND table_name=? AND index_name=?", (ResultSet resultSet) -> {
            if (resultSet.next()) {
                return resultSet.getInt("cnt");
            }
            return 0;
        }, string2, string);
        if (n != null && n > 0) {
            return true;
        }
        return this.executeUpdate("CREATE INDEX " + string + " ON " + string2 + "(" + string3 + ")", new Object[0]);
    }

    private List<PermissionsEnum> parsePermissions(String string) {
        if (string == null || string.isEmpty()) {
            return new ArrayList<PermissionsEnum>();
        }
        return Arrays.stream(string.split("-n-")).map(PermissionsEnum::getByName).filter(Objects::nonNull).collect(Collectors.toList());
    }
}
