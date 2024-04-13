package xyz.safeproxy;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Command;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StatsCommand extends Command {

    public StatsCommand() {
        super("stats");
    }

    public void execute(final CommandSender sender, final String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(new ComponentBuilder("You cannot do this from console!").create());
            return;
        }
        final ProxiedPlayer p = (ProxiedPlayer)sender;
        if (Main.msgSee.contains(p.getUniqueId())) {
            Main.msgSee.remove(p.getUniqueId());
            p.sendMessage(new ComponentBuilder(Main.transColor("&cDisabled the Actionbar.")).create());
        }
        else {
            Main.msgSee.add(p.getUniqueId());
            p.sendMessage(new ComponentBuilder(Main.transColor("&aEnabled the Actionbar.")).create());
        }
    }
}
