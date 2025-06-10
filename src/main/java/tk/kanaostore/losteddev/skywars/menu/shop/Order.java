package tk.kanaostore.losteddev.skywars.menu.shop;

import tk.kanaostore.losteddev.skywars.cosmetics.Cosmetic;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public enum Order {
    NONE(cosmetics -> {}),
    FROM_A_TO_Z(cosmetics -> cosmetics.sort(Comparator.comparing(Cosmetic::getRawName))),
    FROM_Z_TO_A(cosmetics -> cosmetics.sort(Comparator.comparing(Cosmetic::getRawName).reversed())),
    RARITY(cosmetics -> cosmetics.sort(Comparator.comparing(cos -> cos.getRarity().getWeight()))),
    RARITY_REVERSED(cosmetics -> {
        cosmetics.sort(Comparator.comparing(cos -> ((Cosmetic) cos).getRarity().getWeight()));
        Collections.reverse(cosmetics);
    });


    private final Consumer<List<Cosmetic>> consumer;

    Order(Consumer<List<Cosmetic>> consumer) {
        this.consumer = consumer;
    }

    public void accept(List<Cosmetic> cosmetics) {
        consumer.accept(cosmetics);
    }

    public static Order next(Order current) {
        Order[] values = Order.values();
        int index = (current.ordinal() + 1) % values.length;
        return values[index];
    }
}
