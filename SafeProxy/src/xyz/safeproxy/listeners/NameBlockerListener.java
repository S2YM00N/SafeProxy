package xyz.safeproxy.listeners;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import xyz.safeproxy.Main;

import java.util.regex.Pattern;

public class NameBlockerListener implements Listener {

    private final Pattern[] blockedPatterns = {
            Pattern.compile("^(?i)MCSTORM_.*$"),
            Pattern.compile("^(?i)mcstorm_.*$"),
            Pattern.compile("^(?i)mrstorm_.*$"),
            Pattern.compile("^(?i)ayakashi_.*$"),
            Pattern.compile("^(?i)NIGGER_.*$"),
            Pattern.compile("^(?i)nigger_.*$"),
            Pattern.compile("^(?i)NIGGER.*$"),
            Pattern.compile("^(?i)MCSTORM.*$"),
            Pattern.compile("^(?i)MCST0RM.*$"),
            Pattern.compile("^(?i)mcstorm.*$"),
            Pattern.compile("^(?i)mcst0rm.*$"),
            Pattern.compile("^(?i)MCSTORM_NET_.*$")
            
    };

	@EventHandler
    public void onPlayerLogin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
        String playerName = player.getName();
        for (Pattern pattern : blockedPatterns) {
            if (pattern.matcher(playerName).matches()) {
                player.disconnect("Your username is not allowed on this server.");
                return;
            }
        }
        String nickName = player.getDisplayName();
        for (Pattern pattern : blockedPatterns) {
            if (pattern.matcher(nickName).matches()) {
                player.disconnect("Your nickname is not allowed on this server.");
                return;
            }
        }
    }
}
