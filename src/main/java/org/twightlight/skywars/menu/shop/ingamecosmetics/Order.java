package org.twightlight.skywars.menu.shop.ingamecosmetics;

import org.twightlight.skywars.cosmetics.VisualCosmetic;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public enum Order {
    NONE(cosmetics -> {}),
    FROM_A_TO_Z(cosmetics -> cosmetics.sort(Comparator.comparing(VisualCosmetic::getRawName))),
    FROM_Z_TO_A(cosmetics -> cosmetics.sort(Comparator.comparing(VisualCosmetic::getRawName).reversed())),
    RARITY(cosmetics -> cosmetics.sort(Comparator.comparing(cos -> cos.getRarity().getWeight()))),
    RARITY_REVERSED(cosmetics -> {
        cosmetics.sort(Comparator.comparing(cos -> ((VisualCosmetic) cos).getRarity().getWeight()));
        Collections.reverse(cosmetics);
    });


    private final Consumer<List<VisualCosmetic>> consumer;

    Order(Consumer<List<VisualCosmetic>> consumer) {
        this.consumer = consumer;
    }

    public void accept(List<VisualCosmetic> cosmetics) {
        consumer.accept(cosmetics);
    }

    public static Order next(Order current) {
        Order[] values = Order.values();
        int index = (current.ordinal() + 1) % values.length;
        return values[index];
    }
}
