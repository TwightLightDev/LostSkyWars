package org.twightlight.skywars.modules.boosters.menus.submenus;

import org.twightlight.libs.xseries.XMaterial;
import org.twightlight.skywars.modules.boosters.Boosters;
import org.twightlight.skywars.modules.boosters.boosters.Booster;
import org.twightlight.skywars.modules.boosters.boosters.BoosterManager;
import org.twightlight.skywars.modules.boosters.users.PlayerUser;
import org.twightlight.skywars.modules.api.menus.Item;
import org.twightlight.skywars.modules.api.menus.PaginatedMenu;
import org.twightlight.skywars.utils.ItemBuilder;

import java.util.List;

public class Storage extends PaginatedMenu {

    List<String> boosters;

    private Storage(PlayerUser p, Booster.BoosterType type) {
        super(Boosters.getBoostersConfig().getInt("menus.size.storage"));
        if (type == Booster.BoosterType.PERSONAL) {
            boosters = p.getPersonalStorage();
        } else {
            boosters = p.getNetworkStorage();
        }
        int i = 0;

        for (String booster : boosters) {
            Booster booster1 = BoosterManager.getBoosters().get(booster);
            if (booster1 == null) {
                continue;
            }
            setItem(i, new Item((e) -> {
                if (p.addToQueue(p.getUUID(), booster)) {
                    p.removeBooster(booster);
                }
            }, (player) -> new ItemBuilder(XMaterial.GLASS_BOTTLE).
                    setName(Boosters.getLanguage().getString("boosters.display.item-name").replace("{time}", BoosterManager.getAmplifierString(booster1)).replace("{color}", BoosterManager.getColor(booster1)).replace("{amplifier}", BoosterManager.getAmplifierString(booster1)).replace("{currency}", BoosterManager.getCurrencyString(booster1))).
                    setLore(BoosterManager.replaceLore(Boosters.getLanguage().getYml().getStringList("boosters.display.item-lore"), Boosters.getLanguage().getYml().getStringList("boosters.status.in-storage"))).
                    toItemStack()));

            i ++;
        }

        Item close = new Item((e) -> {
            e.getWhoClicked().closeInventory();
        }, (player) -> new ItemBuilder(XMaterial.BARRIER).setName("&cClose").toItemStack());


        open(p.getPlayer());
    }
    @Override
    public void setContents(int page) {
        super.setContents(page);
    }
}
