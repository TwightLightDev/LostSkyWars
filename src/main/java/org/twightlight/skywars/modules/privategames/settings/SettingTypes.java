package org.twightlight.skywars.modules.privategames.settings;

public enum SettingTypes {
    GAME_SPEED("gamespeed", "REAL", "1.0"),
    GAME_TIME("gametime", "TEXT", "DAY"),
    INSTANT_KILL("instantkill", "INTEGER", "0"),
    HEALTH_MULTIPLY("healthmultiply", "REAL", "1.0"),
    ;

    private String column;
    private String SQLType;
    private String defaultValue;

    SettingTypes(String column, String SQLType, String defaultValue) {
        this.column = column;
        this.SQLType = SQLType;
        this.defaultValue = defaultValue;
    }

    public String getColumn() {
        return column;
    }

    public String getSQLType() {
        return SQLType;
    }

    public String getDefaultValue() {
        return defaultValue;
    }
}
