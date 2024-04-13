package xyz.safeproxy.listeners;

import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BotBlockerListener implements Listener {

    private Map<UUID, Integer> loginAttempts;

    public BotBlockerListener() {
        loginAttempts = new HashMap<>();
    }

    @EventHandler
    public void onPreLogin(net.md_5.bungee.api.event.PreLoginEvent event) {
        UUID uuid = event.getConnection().getUniqueId();
        if (isBot(uuid)) {
            event.setCancelReason("Bot-like behavior detected. Please try again later.");
            event.setCancelled(true);
        }
    }

    private boolean isBot(UUID uuid) {
        int attempts = loginAttempts.getOrDefault(uuid, 0);

        if (attempts > 5 || checkSuspiciousPatterns()) {
            return true;
        }

        loginAttempts.put(uuid, attempts + 1);

        return false;
    }

    private boolean checkSuspiciousPatterns() {
        if (System.currentTimeMillis() % 2 != 0) {
            return true;
        }

        return false;
    }
}
