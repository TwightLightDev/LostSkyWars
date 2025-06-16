package org.twightlight.skywars.privategames.settings;

public enum SettingTypes {
    GAME_SPEED("gamespeed", "INTEGER", "1");

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
