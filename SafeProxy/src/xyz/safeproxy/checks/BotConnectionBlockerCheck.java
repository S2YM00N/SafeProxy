package xyz.safeproxy.checks;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class BotConnectionBlockerCheck implements Listener {
    private final Plugin plugin;
    private final Map<String, Long> connectionAttempts = new ConcurrentHashMap<>();
    private final Map<ProxiedPlayer, Integer> kicksPerSecondBlocked = new ConcurrentHashMap<>();
    private final Map<ProxiedPlayer, Integer> connectionsPerSecondBlocked = new ConcurrentHashMap<>();
    private int maxAttempts;
    private long checkInterval;
    private String kickMessage;
    private final Set<String> suspiciousIPs;
    private boolean blockVPN;
    private Set<String> allowedCountries;

    public BotConnectionBlockerCheck(Plugin plugin, Configuration config) {
        this.plugin = plugin;
        maxAttempts = config.getInt("bot-connection-blocker.max-attempts");
        checkInterval = config.getLong("bot-connection-blocker.check-interval");
        kickMessage = ChatColor.translateAlternateColorCodes('&', config.getString("bot-connection-blocker.kick-message", "&cBot behavior detected."));
        suspiciousIPs = new HashSet<>(config.getStringList("bot-connection-blocker.suspicious-ips"));
        blockVPN = config.getBoolean("bot-connection-blocker.block-vpn", true);
        allowedCountries = new HashSet<>(config.getStringList("bot-connection-blocker.allowed-countries"));

        ProxyServer.getInstance().getScheduler().schedule(plugin, this::clearConnectionAttempts, 1, checkInterval, TimeUnit.SECONDS);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPreLogin(PreLoginEvent event) {
        PendingConnection pendingConnection = event.getConnection();
        String ipAddress = pendingConnection.getAddress().getHostString();

        if (isBot(ipAddress)) {
            event.setCancelReason(kickMessage);
            event.setCancelled(true);
        } else {
            trackConnectionAttempt(ipAddress);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onKick(ServerKickEvent event) {
        ProxiedPlayer player = event.getPlayer();
        if (player != null) {
            incrementKicksPerSecondBlocked(player);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerConnect(ServerConnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        incrementConnectionsPerSecondBlocked(player);
    }

    private boolean isBot(String ipAddress) {
        long attempts = connectionAttempts.getOrDefault(ipAddress, 0L);
        return attempts > maxAttempts || (blockVPN && isVPN(ipAddress)) || suspiciousIPs.contains(ipAddress);
    }

    private boolean isVPN(String ipAddress) {
        // Implement VPN detection logic here
        return false;
    }

    private void trackConnectionAttempt(String ipAddress) {
        connectionAttempts.merge(ipAddress, 1L, Long::sum);
    }

    private void clearConnectionAttempts() {
        connectionAttempts.clear();
    }

    public void incrementKicksPerSecondBlocked(ProxiedPlayer player) {
        kicksPerSecondBlocked.merge(player, 1, Integer::sum);
    }

    public void incrementConnectionsPerSecondBlocked(ProxiedPlayer player) {
        connectionsPerSecondBlocked.merge(player, 1, Integer::sum);
    }

    public int getKicksPerSecondBlocked(ProxiedPlayer player) {
        return kicksPerSecondBlocked.getOrDefault(player, 0);
    }

    public int getConnectionsPerSecondBlocked(ProxiedPlayer player) {
        return connectionsPerSecondBlocked.getOrDefault(player, 0);
    }
}
