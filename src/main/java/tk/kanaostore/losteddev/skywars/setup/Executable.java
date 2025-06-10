package tk.kanaostore.losteddev.skywars.setup;


import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;

@FunctionalInterface
public interface Executable<T extends Event> {
    void execute(T p);
}
