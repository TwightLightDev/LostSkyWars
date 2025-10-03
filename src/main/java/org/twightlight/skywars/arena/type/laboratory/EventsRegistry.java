package org.twightlight.skywars.arena.type.laboratory;

import org.twightlight.skywars.arena.Arena;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class EventsRegistry {

    private static Map<String, List<Consumer<Arena<?>>>> registry = new HashMap<>();

    public static void register(String submode, List<Consumer<Arena<?>>> consumers) {
        registry.put(submode, consumers);
    }

    public List<Consumer<Arena<?>>> getConsumers(String submode) {
        return registry.getOrDefault(submode, new ArrayList<>());
    }
}
