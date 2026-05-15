package org.twightlight.skywars.cosmetics.visual;

import org.twightlight.skywars.Language;

public enum VisualCosmeticType {

    CAGE("cages", "cage", "cages"),
    DEATH_CRY("death_cries", "death_cry", "noneeds"),
    TRAIL("trails", "trail", "trails"),
    BALLOON("balloons", "balloon", "balloons"),
    KILL_MESSAGE("kill_messages", "kill_message", "noneeds"),
    KILL_EFFECT("kill_effects", "kill_effect", "killeffects"),
    SPRAY("sprays", "spray", "sprays"),
    VICTORY_DANCE("victory_dances", "victory_dance", "noneeds"),
    TITLE("titles", "title", "noneeds"),
    SYMBOL("symbols", "symbol", "noneeds");

    private final String ownershipColumn;
    private final String selectionColumn;
    private final String previewId;

    VisualCosmeticType(String ownershipColumn, String selectionColumn, String previewId) {
        this.ownershipColumn = ownershipColumn;
        this.selectionColumn = selectionColumn;
        this.previewId = previewId;
    }

    public String getOwnershipColumn() {
        return ownershipColumn;
    }

    public String getSelectionColumn() {
        return selectionColumn;
    }

    public String getPreviewId() {
        return previewId;
    }

    public String getDisplayName() {
        switch (this) {
            case CAGE: return Language.options$cosmetic$prefix + Language.options$cosmetic$cage;
            case DEATH_CRY: return Language.options$cosmetic$prefix + Language.options$cosmetic$deathcry;
            case TRAIL: return Language.options$cosmetic$prefix + Language.options$cosmetic$trail;
            case BALLOON: return Language.options$cosmetic$prefix + Language.options$cosmetic$ballon;
            case KILL_MESSAGE: return Language.options$cosmetic$prefix + Language.options$cosmetic$killmessage;
            case KILL_EFFECT: return Language.options$cosmetic$prefix + Language.options$cosmetic$killeffect;
            case SPRAY: return Language.options$cosmetic$prefix + Language.options$cosmetic$spray;
            case VICTORY_DANCE: return Language.options$cosmetic$prefix + Language.options$cosmetic$victorydance;
            case TITLE: return Language.options$cosmetic$prefix + Language.options$cosmetic$title;
            case SYMBOL: return Language.options$cosmetic$prefix + "Symbol ";
            default: return Language.options$cosmetic$prefix;
        }
    }

    public static VisualCosmeticType fromOwnershipColumn(String column) {
        for (VisualCosmeticType type : values()) {
            if (type.ownershipColumn.equals(column)) {
                return type;
            }
        }
        return null;
    }

    public static VisualCosmeticType fromSelectionColumn(String column) {
        for (VisualCosmeticType type : values()) {
            if (type.selectionColumn.equals(column)) {
                return type;
            }
        }
        return null;
    }
}
