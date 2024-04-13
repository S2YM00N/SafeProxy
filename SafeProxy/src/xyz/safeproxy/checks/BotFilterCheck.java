package xyz.safeproxy.checks;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import xyz.safeproxy.Main;

import java.util.*;

public class BotFilterCheck implements Listener {
    private final Main plugin;

    private final Set<String> allowedCountries = new HashSet<>(Arrays.asList("Poland", "Germany"));
    private final Set<String> suspiciousNames = new HashSet<>(Arrays.asList("bot", "hack", "cheat", "admin"));
    private final int maxPingThreshold = 500;

    public BotFilterCheck(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();

        if (!isAllowedCountry(player)) {
            player.disconnect("You are connecting from a restricted country and are not allowed to join this server.");
            return;
        }

        if (isBot(player)) {
            player.disconnect("You have been identified as a bot and are not allowed to join this server.");
        }
    }

    private boolean isAllowedCountry(ProxiedPlayer player) {
        String playerCountry = getPlayerCountry(player);
        return allowedCountries.contains(playerCountry);
    }

    private boolean isBot(ProxiedPlayer player) {
        return isSuspiciousName(player.getName()) || isSuspiciousPing(player.getPing());
    }

    private String getPlayerCountry(ProxiedPlayer player) {
        return "Poland";
    }

    private boolean isSuspiciousName(String name) {
        for (String suspiciousName : suspiciousNames) {
            if (name.toLowerCase().contains(suspiciousName)) {
                return true;
            }
        }
        return false;
    }

    private boolean isSuspiciousPing(int ping) {
        return ping > maxPingThreshold;
    }
}
