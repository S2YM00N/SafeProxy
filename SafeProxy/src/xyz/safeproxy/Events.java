package xyz.safeproxy;

import net.md_5.bungee.api.plugin.*;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.connection.*;
import net.md_5.bungee.api.*;
import net.md_5.bungee.api.chat.*;
import java.util.*;
import net.md_5.bungee.event.*;

public class Events implements Listener
{
    @EventHandler
    public void onJoin(final PreLoginEvent e) {
        ++Main.joinsPerSec;
        for (final ProxiedPlayer proxyPlayer : ProxyServer.getInstance().getPlayers()) {
            if (proxyPlayer.hasPermission("safeproxy.stats") && Main.msgSee.contains(proxyPlayer.getUniqueId())) {
                proxyPlayer.sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder(Main.transColor(Main.configuration.getString("actionbarmsg")).replace("{prefix}", Main.transColor(Main.configuration.getString("prefix"))).replace("{amount}", String.valueOf(Main.joinsPerSec))).create());
            }
        }
    }
}
