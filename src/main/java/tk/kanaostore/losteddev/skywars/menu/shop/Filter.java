package tk.kanaostore.losteddev.skywars.menu.shop;

import org.bukkit.entity.Player;
import tk.kanaostore.losteddev.skywars.cosmetics.Cosmetic;
import tk.kanaostore.losteddev.skywars.database.Database;

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


    private final BiFunction<Player, List<Cosmetic>, List<Cosmetic>> function;

    Filter(BiFunction<Player, List<Cosmetic>, List<Cosmetic>> function) {
        this.function = function;
    }

    public List<Cosmetic> accept(List<Cosmetic> cosmetics, Player player) {
        return function.apply(player, cosmetics);
    }

    public static Filter next(Filter current) {
        Filter[] values = Filter.values();
        int index = (current.ordinal() + 1) % values.length;
        return values[index];
    }
}
