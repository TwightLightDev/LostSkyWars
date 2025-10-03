package org.twightlight.skywars.cosmetics;

import org.twightlight.skywars.Language;

public enum CosmeticType {

    SKYWARS_KIT(1, "kits", 0, 4),
    SKYWARS_PERK(2, "perks", 1, 3),
    SKYWARS_CAGE(3, "cages", "cages", 2),
    SKYWARS_DEATHCRY(4, "deathcry", 3),
    SKYWARS_BALLOON(5, "ballons", "balloons", 4),
    SKYWARS_SYMBOL(6, "noneeds", 5),
    SKYWARS_TRAIL(7, "trail", "trails", 6),
    SKYWARS_KILLMESSAGE(8, "killmessage", 7),
    SKYWARS_SPRAY(9, "spray", "sprays", 8),
    SKYWARS_KILLEFFECT(10, "killeffect", "killeffects", 9),
    SKYWARS_VICTORYDANCE(11, "victorydance", 10),
    SKYWARS_TITLE(12, "title", 11);


    private int uniqueId;
    private String stats, previewID;
    private int index, size;

    CosmeticType(int uniqueId, String stats, int index) {
        this(uniqueId, stats, "noneeds", index, 1);
    }

    CosmeticType(int uniqueId, String stats, String previewId, int index) {
        this(uniqueId, stats, previewId, index, 1);
    }

    CosmeticType(int uniqueId, String stats, int index, int size) {
        this(uniqueId, stats, "noneeds", index, size);
    }

    CosmeticType(int uniqueId, String stats, String previewId, int index, int size) {
        this.uniqueId = uniqueId;
        this.previewID = previewId;
        this.stats = stats;
        this.index = index;
        this.size = size;
    }

    public String getName() {
        return this == SKYWARS_KIT ? Language.options$cosmetic$prefix + Language.options$cosmetic$kit
                : this == SKYWARS_PERK ? Language.options$cosmetic$prefix + Language.options$cosmetic$perk
                : this == SKYWARS_CAGE ? Language.options$cosmetic$prefix + Language.options$cosmetic$cage
                : this == SKYWARS_DEATHCRY ? Language.options$cosmetic$prefix + Language.options$cosmetic$deathcry
                : this == SKYWARS_TRAIL ? Language.options$cosmetic$prefix + Language.options$cosmetic$trail
                : this == SKYWARS_KILLMESSAGE ? Language.options$cosmetic$prefix + Language.options$cosmetic$killmessage
                : this == SKYWARS_SPRAY ? Language.options$cosmetic$prefix + Language.options$cosmetic$spray
                : this == SKYWARS_KILLEFFECT ? Language.options$cosmetic$prefix + Language.options$cosmetic$killeffect
                : this == SKYWARS_VICTORYDANCE ? Language.options$cosmetic$prefix + Language.options$cosmetic$victorydance
                : this == SKYWARS_TITLE ? Language.options$cosmetic$prefix + Language.options$cosmetic$title

                : Language.options$cosmetic$prefix + Language.options$cosmetic$ballon;
    }

    public int getUniqueId() {
        return uniqueId;
    }

    public int getIndex() {
        return index;
    }

    public int getSize() {
        return size;
    }

    public String getStats() {
        return stats;
    }

    public static CosmeticType getByUniqueId(int uniqueId) {
        for (CosmeticType type : values()) {
            if (type.uniqueId == uniqueId) {
                return type;
            }
        }

        return null;
    }

    public String getPreviewID() {
        return previewID;
    }
}
