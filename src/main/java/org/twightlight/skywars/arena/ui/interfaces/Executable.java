package org.twightlight.skywars.arena.ui.interfaces;


import org.bukkit.event.Event;

@FunctionalInterface
public interface Executable<T extends Event> {
    void execute(T p);
}
