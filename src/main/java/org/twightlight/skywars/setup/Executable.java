package org.twightlight.skywars.setup;


import org.bukkit.event.Event;

@FunctionalInterface
public interface Executable<T extends Event> {
    void execute(T p);
}
