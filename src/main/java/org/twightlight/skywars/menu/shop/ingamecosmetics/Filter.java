package org.twightlight.skywars.menu.shop.ingamecosmetics;

import org.bukkit.entity.Player;
import org.twightlight.skywars.cosmetics.VisualCosmetic;
import org.twightlight.skywars.database.Database;

import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public enum Filter {
    ALL((p, cosmetics) -> cosmetics),
    OWNED((p, cosmetics) -> cosmetics.stream().
            filter(cosmetic -> {
                return Database.getInstance().getAccount(p.getUniqueId()) != null && cosmetic.has(Database.getInstance().getAccount(p.getUniqueId()));
            }).collect(Collectors.toList())),
    NOT_OWNED((p, cosmetics) -> cosmetics.stream().
            filter(cosmetic -> {
                return Database.getInstance().getAccount(p.getUniqueId()) != null && !cosmetic.has(Database.getInstance().getAccount(p.getUniqueId()));
            }).collect(Collectors.toList()));


    private final BiFunction<Player, List<VisualCosmetic>, List<VisualCosmetic>> function;

    Filter(BiFunction<Player, List<VisualCosmetic>, List<VisualCosmetic>> function) {
        this.function = function;
    }

    public List<VisualCosmetic> accept(List<VisualCosmetic> cosmetics, Player player) {
        return function.apply(player, cosmetics);
    }

    public static Filter next(Filter current) {
        Filter[] values = Filter.values();
        int index = (current.ordinal() + 1) % values.length;
        return values[index];
    }
}
