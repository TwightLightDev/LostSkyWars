package org.twightlight.skywars.hook.guilds.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.twightlight.skywars.SkyWars;
import org.twightlight.skywars.hook.guilds.donation.Donator;

public class GuildsDonationExpansion extends PlaceholderExpansion {

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getAuthor() {
        return "TwightLight";
    }

    @Override
    public String getIdentifier() {
        return "guildsdonation";
    }

    @Override
    public String getVersion() {
        return SkyWars.getInstance().getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        Donator donator = Donator.getFromUUID(player.getUniqueId());
        if (donator == null) {
            return "";
        }
        if (params.equals("next_refresh")) {
            return String.valueOf(donator.getNextRefresh());
        } else if (params.equals("today_donation")) {
            return String.valueOf(donator.getDonationToday());
        } else if (params.equals("limit_donation")) {
            return String.valueOf(donator.getDonationLimit());
        } else if (params.equals("coins")) {
            return String.valueOf(donator.getGuildCoins());
        } else if (params.equals("exchange_ratio")) {
            return String.valueOf(donator.getRatio());
        } else if (params.equals("current_level")) {
            return String.valueOf(donator.getLevel().getLevel());
        } else if (params.equals("next_level")) {
            return String.valueOf(donator.getLevel().getLevel() + 1);
        }  else if (params.equals("current_xp")) {
            return String.valueOf(donator.getLevel().getCurrentXP());
        } else if (params.equals("required_xp")) {
            return String.valueOf(donator.getLevel().getRequiredXP());
        } else if (params.equals("progress_percentage")) {
            return String.valueOf((donator.getLevel().getCurrentXP() / donator.getLevel().getRequiredXP()) * 100);
        } else if (params.equals("progress_bar")) {
            double current = donator.getLevel().getCurrentXP();
            double required = donator.getLevel().getRequiredXP();

            int barLength = 20;
            double ratio = required == 0 ? 0 : current / required;
            int filledBars = (int) Math.round(ratio * barLength);

            StringBuilder bar = new StringBuilder("&b");
            for (int i = 0; i < barLength; i++) {
                if (i < filledBars) {
                    bar.append("■");
                } else {
                    bar.append("&8■");
                }
            }
            return bar.toString();
        }

        return null;
    }
}
