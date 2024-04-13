package xyz.safeproxy.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.Listener;

public class SafeProxyCommand extends Command implements Listener {
    private Plugin plugin;

    public SafeProxyCommand(Plugin plugin) {
        super("safeproxy", "bungeecord.bungee", "proxy", "bungee", "ab", "antibot", "nantibot", "uab", "waterfallproxy", "botguardnajlepszy", "craftproxy", "aegis", "flamecord", "xcord", "nullcordx", "eyfencord");
        this.plugin = plugin;
        plugin.getProxy().getPluginManager().registerListener(plugin, this);
        setPermissionMessage("§8» §7This server is running §b§lSafeProxy §8(v1.1.0-FREE§8) §7Best free AntiBot. Checkout our other and better products on discord§8: §3https://discord.gg/Pw6JSqwRGT");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(new TextComponent("§8» §7This server is running §b§lSafeProxy §8(§fv1.1.0-FREE§8) §7Best free AntiBot. Checkout our other and better products on discord§8: §3https://discord.gg/Pw6JSqwRGT"));
    }
}
