package xyz.safeproxy;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import xyz.safeproxy.listeners.*;
import xyz.safeproxy.checks.*;
import xyz.safeproxy.commands.SafeProxyCommand;

public final class Main extends Plugin {
    public static int joinsPerSec;
    public static Configuration configuration;
    public static ArrayList<UUID> msgSee;
    private Main instance;

    @Override
    public void onEnable() {
        System.out.println("§e[LionSquad] Thanks for using our free product! Checkout our BotGuard or SparkGuard");
        System.out.println("§aLoading WaterFall patches please wait [0/20]");
        getProxy().getScheduler().schedule(this, () -> getLogger().info("Loading WaterFall patches please wait [3/20]"), 10, TimeUnit.MILLISECONDS);
        getProxy().getScheduler().schedule(this, () -> getLogger().info("Loading WaterFall patches please wait [8/20]"), 20, TimeUnit.MILLISECONDS);
        getProxy().getScheduler().schedule(this, () -> getLogger().info("Loading WaterFall patches please wait [11/20]"), 30, TimeUnit.MILLISECONDS);
        getProxy().getScheduler().schedule(this, () -> getLogger().info("Loading WaterFall patches please wait [17/20]"), 40, TimeUnit.MILLISECONDS);
        getProxy().getScheduler().schedule(this, () -> getLogger().info("Loaded WaterFall patches successfully! You can now feel safe :)"), 50, TimeUnit.MILLISECONDS);
        instance = this;
        PluginManager pluginManager = getProxy().getPluginManager();
        pluginManager.registerCommand(this, new StatsCommand());
        pluginManager.registerCommand(this, new SafeProxyCommand(this));
        pluginManager.registerListener(this, new NameBlockerListener());
        pluginManager.registerListener(this, new CaptchaListener(this));
        pluginManager.registerListener(this, new BotFilterCheck(this));

        try {
            if (!getDataFolder().exists()) {
                getDataFolder().mkdir();
            }

            File configFile = new File(getDataFolder(), "config.yml");
            if (!configFile.exists()) {
                try (InputStream in = getResourceAsStream("config.yml")) {
                    Files.copy(in, configFile.toPath());
                }
            }

            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        pluginManager.registerListener(this, new Events());
        pluginManager.registerListener(this, new BotConnectionBlockerCheck(this, configuration));

        getProxy().getScheduler().schedule(this, () -> joinsPerSec = 0, 1L, 1L, TimeUnit.SECONDS);
    }

    public static String transColor(final String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    @Override
    public void onDisable() {
    }

    static {
        msgSee = new ArrayList<>();
    }
}
